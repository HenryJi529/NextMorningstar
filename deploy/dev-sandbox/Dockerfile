# NOTE: 镜像构建:
# docker build -f deploy/dev-sandbox/Dockerfile -t morningstar-dev-sandbox:latest deploy/
# NOTE: 容器运行:
# docker run -d -e MODEL_API_KEY=... -e SONARQUBE_TOKEN=... --add-host=host.docker.internal:host-gateway morningstar-dev-sandbox:latest

FROM node:20-bullseye AS node-src

FROM maven:3.9-eclipse-temurin-17

WORKDIR /workspace

# ===== 时区 =====
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone
ENV TZ=Asia/Shanghai

# ===== 系统依赖 =====
RUN export DEBIAN_FRONTEND=noninteractive \
    && apt-get update \
    && apt-get install -y --no-install-recommends vim curl ca-certificates unzip python3 python3-pip \
    && rm -rf /var/lib/apt/lists/*

# ===== Node.js =====
COPY --from=node-src /usr/local/bin/node /usr/local/bin/node
COPY --from=node-src /usr/local/lib/node_modules /usr/local/lib/node_modules
RUN ln -s /usr/local/lib/node_modules/npm/bin/npm-cli.js /usr/local/bin/npm \
    && ln -s /usr/local/lib/node_modules/npm/bin/npx-cli.js /usr/local/bin/npx \
    && node -v && npm -v

# ===== Claude Code =====
RUN npm install -g @anthropic-ai/claude-code \
    && (claude --version || true)
# Claude Code CLI 禁止 root 使用 --dangerously-skip-permissions
RUN groupadd -r bot && useradd -r -g bot -m -d /home/bot -s /bin/sh bot \
    && mkdir -p /workspace/repo \
    && chown -R bot:bot /workspace
COPY --chown=bot:bot dev-sandbox/config/claude/settings.json /home/bot/.claude/settings.json
RUN chown -R bot:bot /home/bot/.claude
COPY --chown=bot:bot dev-sandbox/config/claude/mcp.json /workspace/.mcp.json

# ===== Sonar Scanner =====
ARG SONAR_SCANNER_VERSION=8.1.0.6389
ARG TARGETARCH
ENV SONAR_SCANNER_HOME=/opt/sonar-scanner
ENV PATH=${SONAR_SCANNER_HOME}/bin:${PATH}
RUN case "${TARGETARCH}" in \
        amd64) SCANNER_ARCH=x64 ;; \
        arm64) SCANNER_ARCH=aarch64 ;; \
        *) echo "unsupported TARGETARCH: ${TARGETARCH}" >&2; exit 1 ;; \
    esac \
    && curl -fsSL -o /tmp/scanner.zip \
        https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-${SONAR_SCANNER_VERSION}-linux-${SCANNER_ARCH}.zip \
    && unzip -q /tmp/scanner.zip -d /opt \
    && mv /opt/sonar-scanner-* ${SONAR_SCANNER_HOME} \
    && rm /tmp/scanner.zip \
    && sonar-scanner --version

# ===== Maven =====
COPY --chown=bot:bot dev-sandbox/config/maven/settings.xml /workspace/maven-settings.xml

# ===== 启动脚本 =====
USER bot

COPY --chmod=755 dev-sandbox/entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
CMD ["sleep", "infinity"]
