<script setup lang="ts">
const stages = [
    { label: '启动', desc: '创建项目专属容器，挂载共享代码卷，环境隔离互不干扰' },
    { label: '同步', desc: '由后端命令行 git 拉取最新代码，凭证不进 AI 容器' },
    { label: '扫描', desc: '规则引擎客观扫描 + AI 语义审查，双通道互补发现问题' },
    { label: '修复', desc: '逐 issue 生成修复，每修一个用 SonarQube MCP 即时自查' },
    { label: '验证', desc: '复扫确认问题消除，再由 AI 独立判定修复思路是否正确' },
    { label: '提交', desc: '推送修复分支并创建 PR，附带完整诊断报告' },
    { label: '清理', desc: '回收容器与临时现场，无论成败都不留残留' },
];

const security = [
    {
        title: '凭证隔离',
        icon: '⛨',
        desc: 'Git 凭证只在后端内存，用时注入临时容器、用完即毁；AI 容器内零 git 凭证，prompt injection 偷不到仓库写权限',
    },
    {
        title: '最小权限',
        icon: '◈',
        desc: 'admin token 只在授权协作者的一瞬间使用；bot token 只有单仓库写权限，爆炸半径压缩到单个仓库',
    },
    {
        title: '出站白名单',
        icon: '⊘',
        desc: '容器网络只放行 Gitea / SonarQube / DeepSeek 三个目标，其余全部挡死，凭证被偷也传不出去',
    },
];

const identities = [
    {
        role: '项目管理员',
        who: 'project admin',
        desc: '配置、触发、启停、删除自己的项目；平台自跑项目归专门服务账号 morningstar-nightly',
        style: 'border-orange-200 bg-orange-50/50',
        titleColor: 'text-orange-600',
    },
    {
        role: '提交机器人',
        who: 'bot · HaibaraAi369',
        desc: 'Gitea 侧的机器身份，负责推送修复分支、创建 PR，对平台内部无感知',
        style: 'border-slate-200 bg-slate-50/60',
        titleColor: 'text-slate-600',
    },
    {
        role: '平台管理员',
        who: 'admin · 熔断者',
        desc: '只有熔断权没有所有权：可取消任何 run、停用任何项目；不能恢复、不能改配置',
        style: 'border-amber-200 bg-amber-50/50',
        titleColor: 'text-amber-600',
    },
];

const onboard = [
    {
        title: '填仓库链接和分支',
        desc: '工作台点"接入新项目"，粘贴 Gitea 仓库地址，平台自动校验仓库与分支存在',
    },
    { title: '授权协作自动完成', desc: '平台自动把 bot 加为仓库协作者，无需手动配置任何凭证' },
    {
        title: '明早来看 PR',
        desc: '每晚调度时段自动开跑，清晨在 Gitea review 修复 PR，合并与否你说了算',
    },
];
</script>

<template>
    <section class="space-y-5 max-w-5xl mx-auto px-6">
        <!-- Hero -->
        <div
            class="rounded-2xl border border-orange-100 bg-gradient-to-br from-orange-50 via-white to-amber-50 px-10 py-10 text-center ring-4 ring-orange-50">
            <div class="text-[11px] tracking-[0.3em] text-orange-400 mb-3">
                代码工坊 | 代码质量优化流水线
            </div>
            <h1 class="text-2xl font-bold text-slate-800 leading-relaxed">
                每晚自动修复代码问题，<span class="text-orange-500">清晨给你一个待合并的 PR</span>
            </h1>
            <p class="mt-3 text-sm text-slate-500 max-w-2xl mx-auto leading-relaxed">
                夜间由 AI 无人值守完成 扫描 → 修复 → 验证 → 提交，清晨你只需在 Gitea 上 review 一个
                PR。 AI 拥有完整的执行权，但合并到主干的权力永远在人手里——AI 自主 + 人工门。
            </p>
        </div>

        <!-- 流水线怎么跑 -->
        <div class="rounded-xl border border-slate-200 bg-white shadow-sm p-6">
            <h2 class="text-sm font-semibold text-slate-800 mb-1">流水线怎么跑</h2>
            <p class="text-xs text-slate-400 mb-5">
                一条主链七个阶段，任一阶段失败则整轮回退清理，绝不留半成品现场
            </p>
            <div class="grid grid-cols-2 gap-x-8 gap-y-4">
                <div v-for="(s, i) in stages" :key="s.label" class="flex items-start gap-3">
                    <div
                        class="shrink-0 w-8 h-8 rounded-full flex items-center justify-center text-xs font-mono bg-orange-500 text-white">
                        {{ i + 1 }}
                    </div>
                    <div class="min-w-0">
                        <div class="text-sm font-medium text-slate-700">{{ s.label }}</div>
                        <div class="text-xs text-slate-400 mt-0.5 leading-relaxed">
                            {{ s.desc }}
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 安全吗 -->
        <div class="rounded-xl border border-slate-200 bg-white shadow-sm p-6">
            <h2 class="text-sm font-semibold text-slate-800 mb-1">AI 碰得到我的代码吗</h2>
            <p class="text-xs text-slate-400 mb-5">
                能改仓库的凭证绝不进 AI 容器，三道防线逐层收紧
            </p>
            <div class="grid grid-cols-3 gap-4">
                <div
                    v-for="s in security"
                    :key="s.title"
                    class="rounded-lg border border-slate-200 bg-slate-50/60 p-4">
                    <div
                        class="w-8 h-8 rounded-lg bg-orange-100 text-orange-500 flex items-center justify-center text-sm mb-3"
                        v-html="s.icon"></div>
                    <div class="text-sm font-medium text-slate-700">{{ s.title }}</div>
                    <div class="text-xs text-slate-400 mt-1.5 leading-relaxed">{{ s.desc }}</div>
                </div>
            </div>
        </div>

        <!-- 三层身份 -->
        <div class="rounded-xl border border-slate-200 bg-white shadow-sm p-6">
            <h2 class="text-sm font-semibold text-slate-800 mb-1">谁在操作系统</h2>
            <p class="text-xs text-slate-400 mb-5">
                三层身份各司其职，任一身份被攻破，爆炸半径都限制在自己的职责内
            </p>
            <div class="grid grid-cols-3 gap-4">
                <div
                    v-for="r in identities"
                    :key="r.role"
                    class="rounded-lg border p-4"
                    :class="r.style">
                    <div class="text-sm font-semibold" :class="r.titleColor">{{ r.role }}</div>
                    <div class="text-[11px] font-mono mt-0.5" :class="r.titleColor">
                        {{ r.who }}
                    </div>
                    <div class="text-xs text-slate-500 mt-2 leading-relaxed">{{ r.desc }}</div>
                </div>
            </div>
        </div>

        <!-- 怎么接入 -->
        <div class="rounded-xl border border-slate-200 bg-white shadow-sm p-6">
            <h2 class="text-sm font-semibold text-slate-800 mb-5">三步接入你的仓库</h2>
            <div class="flex items-start">
                <template v-for="(s, i) in onboard" :key="s.title">
                    <div class="flex-1 text-center px-4">
                        <div
                            class="w-10 h-10 mx-auto rounded-full bg-orange-500 text-white flex items-center justify-center font-mono font-semibold">
                            {{ i + 1 }}
                        </div>
                        <div class="text-sm font-medium text-slate-700 mt-3">{{ s.title }}</div>
                        <div class="text-xs text-slate-400 mt-1.5 leading-relaxed">
                            {{ s.desc }}
                        </div>
                    </div>
                    <div v-if="i < 2" class="flex items-center h-10 text-slate-300 text-lg">→</div>
                </template>
            </div>
            <div class="mt-6 text-center">
                <router-link
                    :to="{ name: 'dev-project' }"
                    class="inline-block px-5 py-2.5 rounded-lg bg-orange-500 hover:bg-orange-400 text-white text-sm font-medium transition shadow-sm shadow-orange-200">
                    去接入项目
                </router-link>
            </div>
        </div>
    </section>
</template>

<style scoped lang="scss"></style>
