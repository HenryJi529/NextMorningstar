from timeit import default_timer as timer
import subprocess
import os
from pathlib import Path
from timeit import default_timer as timer
import argparse
import socket

from fabric import Connection
import colorama
import git
from dotenv import load_dotenv


LOCAL_BASE_DIR = Path(__file__).resolve().parent
load_dotenv(dotenv_path=LOCAL_BASE_DIR / ".env", verbose=True)


def colored_print(var, end: str = "\n"):
    formatted_output = (
        colorama.Fore.YELLOW
        + colorama.Style.BRIGHT
        + str(var)
        + colorama.Style.RESET_ALL
    )
    print(formatted_output, end=end)


def time(func):
    def wrapper(*args, **kwargs):
        start_time = timer()
        result = func(*args, **kwargs)
        end_time = timer()
        execution_time = end_time - start_time
        colored_print(f"[INFO] Total running time: {execution_time:.3f} seconds")
        return result

    return wrapper


def runcmd(command, blocking: bool = False):
    if not blocking:
        # 启动子进程并建立管道
        process = subprocess.Popen(
            command,
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            errors="replace",
            encoding="utf-8",
            universal_newlines=True,
        )
        # 逐行读取输出
        for line in process.stdout:
            print(line, end="")
        # 等待子进程结束
        process.wait()
    else:
        ret = subprocess.run(
            command,
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            encoding="utf-8",
            universal_newlines=True,
        )
        if ret.returncode == 0:
            print(ret.stdout)
        else:
            colored_print(ret.stderr)


class Env:
    CLOUD_HOST = os.getenv("CLOUD_HOST")
    CLOUD_USERNAME = os.getenv("CLOUD_USERNAME")
    CLOUD_PASSWORD = os.getenv("CLOUD_PASSWORD")
    LOCAL_USERNAME = os.getenv("LOCAL_USERNAME")
    LOCAL_PASSWORD = os.getenv("LOCAL_PASSWORD")

DEPLOY_BASE_DIR = Path(f"/home/{Env.CLOUD_USERNAME}/deploy")

def get_ip_address(domain):
    try:
        # 获取第一个 IP 地址
        ip = socket.gethostbyname(domain)
        return ip
    except socket.gaierror as e:
        raise Exception(f"无法解析域名: {e}")


ENV_DICT = {
    key: value for key, value in Env.__dict__.items() if not key.startswith("__")
}
ENV_DICT["CLOUD_IP_ADDRESS"] = get_ip_address(Env.CLOUD_HOST)


class MorningstarConnection(Connection):
    def __init__(self):
        super().__init__(
            host=Env.CLOUD_HOST,
            user=Env.CLOUD_USERNAME,
            connect_kwargs={
                "password": Env.CLOUD_PASSWORD,
            },
        )

    def run(self, command: str, **kwargs):
        exportEnv = " && ".join(
            [f"export {key}='{value}'" for key, value in ENV_DICT.items()]
        )
        return super().run(f"{exportEnv} && {command}", **kwargs)


conn = MorningstarConnection()

DOCKER_VOLUME_NAMES: list[str] = conn.run(
    "docker volume ls | grep " + DEPLOY_BASE_DIR.name + " | awk '{print $2}' | tr '\n' ' '", hide=True
).stdout.split(" ")[:-1]

class Commands:
    @staticmethod
    def buildProd():
        colored_print("前端打包...")
        runcmd("cd ../frontend && npm run build_prod")
        colored_print("后端打包...")
        runcmd("cd ../backend && mvn clean package -Pprod -DskipTests")

    @staticmethod
    def deployProd():
        colored_print("同步代码...")
        runcmd(
            f"rsync -avz . {Env.CLOUD_USERNAME}@{Env.CLOUD_HOST}:{DEPLOY_BASE_DIR} --exclude-from='rsync-exclude.txt'"
        )
        colored_print("应用更新...")
        with conn.cd(DEPLOY_BASE_DIR):
            conn.run("docker compose up --build -d")
        colored_print("前端刷新...")
        conn.run("docker restart morningstar_nginx")

    @staticmethod
    def backupProd():
        colored_print("暂停容器...")
        with conn.cd(DEPLOY_BASE_DIR):
            conn.run("docker-compose pause")
        colored_print("数据卷打包...")
        with conn.cd("~/"):
            for dockerVolumeName in DOCKER_VOLUME_NAMES:
                shortName = dockerVolumeName[len(DEPLOY_BASE_DIR.name)+1:]
                print(f"- 备份{shortName}")
                command = f'docker run --rm -v {DEPLOY_BASE_DIR.name}_{shortName}:/volume -v ~/backup:/backup alpine sh -c "tar -C /volume -cvzf /backup/{shortName}.tar.gz ./"'
                conn.run(command, hide=True)
        colored_print("重启容器...")
        with conn.cd(DEPLOY_BASE_DIR):
            conn.run("docker-compose unpause")
        colored_print("数据卷同步到本地...")
        runcmd(
            f"rsync -avz {Env.CLOUD_USERNAME}@{Env.CLOUD_HOST}:~/backup {LOCAL_BASE_DIR}"
        )

    @staticmethod
    def restoreProd():
        colored_print("同步压缩包到云端...")
        # # NOTE: 不支持增量同步，网络差时效果不好
        # conn.run(
        #     "sshpass -p "
        #     + Env.LOCAL_PASSWORD
        #     + f" scp -P 1022 -r {Env.LOCAL_USERNAME}@{Env.CLOUD_HOST}:{LOCAL_BASE_DIR}/backup/  ~/backup/"
        # )
        runcmd(
            f"rsync -avz {LOCAL_BASE_DIR}/backup {Env.CLOUD_USERNAME}@{Env.CLOUD_HOST}:~/"
        )
        colored_print("暂停容器...")
        with conn.cd(DEPLOY_BASE_DIR):
            conn.run("docker-compose pause")
        colored_print("复原压缩包为volume...")
        with conn.cd("~/"):
            for dockerVolumeName in DOCKER_VOLUME_NAMES:
                shortName = dockerVolumeName[len(DEPLOY_BASE_DIR.name)+1:]
                print(f"- 还原{shortName}")
                command = f'docker run --rm -v {DEPLOY_BASE_DIR.name}_{shortName}:/volume -v ~/backup:/backup alpine sh -c "rm -rf /volume/* ; tar -C /volume/ -xzvf /backup/{shortName}.tar.gz"'
                conn.run(command, hide=True)
        colored_print("重启容器...")
        with conn.cd(DEPLOY_BASE_DIR):
            conn.run("docker-compose unpause")

    @staticmethod
    def renewCert():
        # NOTE: 如果certbot与nginx在一台机器上
        # certbot renew \ 
        # --webroot -w /var/www/html \
        # --pre-hook "mkdir -p /var/www/html/.well-known/acme-challenge && chown nginx:nginx /var/www/html/.well-known" \
        # --post-hook "nginx -s reload" \
        # --dry-run --force-renewal

        colored_print("更新证书...")
        conn.run("docker exec morningstar_certbot certbot renew --webroot -w /var/www/html --force-renewal")
        colored_print("重载Nginx...")
        conn.run("docker exec morningstar_nginx nginx -s reload")

    @staticmethod
    def _test():
        runcmd('echo "\033[33mThis is a test...\033[0m"')


if __name__ == "__main__":
    # Commands._test()
    parser = argparse.ArgumentParser(description="Execute Task")
    parser.add_argument(
        "action",
        nargs=1,
        help="操作",
        choices=[
            "buildProd",
            "deployProd",
            "backupProd",
            "restoreProd",
            "renewCert"
        ],
    )
    parser.add_argument(
        "--force", "-f",
        action="store_true",
        help="允许在非main分支上执行操作",
    )
    args = parser.parse_args()

    repo = git.Repo(Path(__file__).parent.parent)
    current_branch = repo.active_branch.name
    if current_branch != "main" and not args.force:
        raise Exception("当前分支不是main, 可使用`--force`强制运行")

    action = args.action[0]
    time(getattr(Commands, action))()
    runcmd("figlet Morningstar")
