<script lang="ts" setup>

import {useHead} from "@vueuse/head";
import {onMounted, ref} from "vue";
import {VuePrintNext} from 'vue-print-next';
import {useFavicon} from '@vueuse/core'
import MorningstarFooter from "@/components/Footer.vue";
import {isDesktop} from "@/utils/handleClient";
import {hasAnyPermission} from "@/utils/handlePermission";
import {ROLE_SUPER_ADMIN} from "@/constants/RoleConstant";

const isAdmin = hasAnyPermission([ROLE_SUPER_ADMIN]);

const page = ref();
useFavicon().value = '/resume.ico'

useHead({
    title: 'Resume - 在线简历',
    meta: [
        {
            name: 'description',
            content: 'Web开发小学生的在线简历'
        }
    ]
});

const handlePrint = () => {
    new VuePrintNext({el: '#page'});
}

const setPageA4 = () => {
    // 使得高和宽满足一张A4纸比例
    const height = page.value.offsetHeight;
    const width = height * 0.707; // 依据box-border，此时的width就是box模型的总宽度
    page.value.style.width = `${width}px`;
    // console.log(page.value.offsetHeight, page.value.offsetHeight);
}

onMounted(() => {
    setPageA4();
    // 解决未知原因引发的比例错误
    setInterval(() => {
        setPageA4()
    }, 200)
})

</script>

<template>
    <div v-if="isDesktop()" class="absolute right-0 top-0 p-10 text-6xl" @click="handlePrint">
        <font-awesome-icon :icon="['fas', 'download']" class="cursor-pointer"/>
    </div>
    <div id="page" ref="page" class="box-border px-8 py-12 mx-auto font-family-song" data-theme="light">
        <div class="flex justify-between items-end gap-10">
            <div class="flex-1">
                <div class="flex flex-col">
                    <div class="flex items-center px-8 space-x-32 font-family-kai">
                        <div class="text-5xl">
                            <span>
                                吉普
                            </span>
                        </div>
                        <div class="text-2xl text-gray-700">
                            求职意向: Web全栈开发
                        </div>
                    </div>
                    <div class="pt-4 px-8 grid grid-cols-2 gap-y-2 gap-x-0 text-lg">
                        <div>
                            <span>毕业院校: </span>
                            河海大学
                        </div>
                        <div>
                            <span>最高学历: </span>
                            硕士研究生
                        </div>
                        <div>
                            <span>Github: </span>
                            <a href="https://github.com/HenryJi529">HenryJi529</a>
                        </div>
                        <div>
                            <span>个人网站: </span>
                            <a href="https://morningstar369.com">morningstar369.com</a>
                        </div>
                        <div>
                            <span>联系电话: </span>
                            <span v-if="isAdmin">19850052801</span>
                            <span v-else class="text-gray-600">***********</span>
                        </div>
                        <div>
                            <span>电子邮箱: </span>
                            <a v-if="isAdmin" href="mailto:jeep.jipu@outlook.com">jeep.jipu@outlook.com</a>
                            <span v-else class="text-gray-900">*****@********</span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="w-[8rem]">
                <img alt="证件照" class="w-full" src="../../assets/img/resume/证件照.JPG">
            </div>
        </div>
        <div class="divider"></div>
        <div class="section">
            <div class="text-xl font-bold">
                <font-awesome-icon :icon="['fas', 'graduation-cap']"/>
                教育经历
            </div>
            <div class="items">
                <div class="item">
                    <div class="time">2017.9 - 2021.6</div>
                    <div>
                        <span class="font-bold text-lg">
                            <font-awesome-icon :icon="['fas', 'star']"/>
                            南京工业大学</span>
                        |
                        <span>工学学士</span>
                    </div>
                    <div class="pl-8 flex space-x-6">
                        <span>
                            <span class="font-bold">专业: </span>
                            电子信息工程
                        </span>
                        <span>
                            <span class="font-bold">学业成绩: </span>
                            3.6/4
                        </span>
                        <span>
                            <span class="font-bold">英语水平: </span>
                            四级528 雅思6.0
                        </span>
                    </div>
                </div>
                <div class="item">
                    <div class="time">2022.9 - 2025.6</div>
                    <div>
                        <span class="font-bold text-lg">
                            <font-awesome-icon :icon="['fas', 'star']"/>
                            河海大学
                        </span>
                        |
                        <span>学术型硕士研究生</span>
                    </div>
                    <div class="pl-8 flex space-x-6">
                        <span>
                            <span class="font-bold">专业: </span>
                            信号与信息处理
                        </span>
                        <span>
                            <span class="font-bold">研究方向: </span>
                            复杂网络
                        </span>
                        <span>
                            <span class="font-bold">英语水平: </span>
                            六级481
                        </span>
                    </div>
                </div>
            </div>
        </div>
        <div class="divider"></div>
        <div class="section">
            <div class="text-xl font-bold">
                <font-awesome-icon :icon="['fas', 'briefcase']"/>
                工作经历
            </div>
            <div class="items">
                <div class="item">
                    <div class="time">2022.6 - 2022.8</div>
                    <div>
                        <span class="font-bold text-lg">
                            <font-awesome-icon :icon="['fas', 'star']"/>
                            达发科技（苏州）有限公司
                        </span>
                        |
                        <span>Linux软件开发实习生</span>
                    </div>
                    <div class="ml-16">
                        <ol class="list-decimal">
                            <li>阅读speedtest模块源码, 参与speedtest性能测试</li>
                            <li>部署USE测试环境并记录数据</li>
                            <li>完成mesh与漫游相关测试</li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <div class="divider"></div>
        <div class="section">
            <div class="text-xl font-bold">
                <font-awesome-icon :icon="['fas', 'diagram-project']"/>
                项目经历
            </div>
            <div class="items">
                <div class="item">
                    <div class="time">2022.7 - 2023.12</div>
                    <div>
                    <span class="font-bold text-lg">
                        <font-awesome-icon :icon="['fas', 'star']"/>
                        个人网站
                    </span>
                        |
                        <span>独立负责人</span>
                    </div>
                    <div class="pl-8">
                        <div><span class="font-bold">主要工作：</span>
                            <ol class="ml-8 list-disc">
                                <li>
                                    前端: 采用Vue3与TypeScript构建SPA，并结合TailwindCSS和DaisyUI美化页面样式
                                </li>
                                <li>
                                    后端: 基于Spring构建RESTful API，使用RabbitMQ作为消息队列，使用WebSocket实现部分通信
                                </li>
                                <li>
                                    运维: 使用Docker与Fabric将项目部署于腾讯云，并通过GithubAction实现持续集成
                                </li>
                            </ol>
                        </div>
                        <div>
                            <span class="font-bold">项目成果：</span>开发并部署了一系列Web应用程序，包括技术博客、开发者导航、在线简历、爱の相册等。
                        </div>
                    </div>
                </div>
                <div class="item">
                    <div class="time">2023.6 - 2024.3</div>
                    <div>
                    <span class="font-bold text-lg">
                        <font-awesome-icon :icon="['fas', 'star']"/>
                        基于深度强化学习的复杂网络节点影响力排序算法
                    </span>
                        |
                        <span>独立负责人</span>
                    </div>
                    <div class="pl-8">
                        <div><span class="font-bold">主要工作：</span>
                            <ol class="ml-8 list-disc">
                                <li>
                                    从网络拆解的视角看待节点影响力，将节点影响力的评估问题转化为节点拆除策略的优化问题
                                </li>
                                <li>
                                    利用排序学习进行预训练来增强图神经网络的节点特征提取能力，并将节点特征矩阵应用于节点影响力的价值学习
                                </li>
                                <li>
                                    通过深度强化学习对节点影响力作价值学习，从而优化节点的拆除策略，进而获得节点的影响力排序
                                </li>
                            </ol>
                        </div>
                        <div>
                            <span class="font-bold">项目成果：</span>该算法在保持了接近朴素算法高准确度的同时，还实现了较低的复杂度，且具有良好的泛化能力和实用性。
                        </div>
                    </div>
                </div>
                <div class="item">
                    <div class="time">2024.7 - 2024.9</div>
                    <div>
                    <span class="font-bold text-lg">
                        <font-awesome-icon :icon="['fas', 'star']"/>
                        基于Github的开放图床平台
                    </span>
                        |
                        <span>独立负责人</span>
                    </div>
                    <div class="pl-8">
                        <div><span class="font-bold">主要工作：</span>
                            <ol class="ml-8 list-disc">
                                <li>从用户注册时提供的Personal Access Token获取用户在Github中的相关信息</li>
                                <li>
                                    将图片的上传与更新转换为Git仓库中commit的提交，响应的文件路径通过jsDelivr封装，无缝提供CDN支持
                                </li>
                                <li>实现图片压缩功能，支持自定义压缩质量，默认采用WebP格式，兼容JPEG格式作为备选方案</li>
                                <li>提供Web界面管理图片资源，通过缓存每日图片列表与按响应时间选择CDN等方法提升访问速度
                                </li>
                                <li>开放API为Markdown写作平台(如MWeb)提供图床服务支持</li>
                            </ol>
                        </div>
                        <div>
                            <span class="font-bold">项目成果：</span>实现了图床服务Web端的搭建，并提供了相关的API，可以为用户提供简便、专业且零成本的图床服务。
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="divider"></div>
        <div class="section">
            <div class="text-xl font-bold">
                <font-awesome-icon :icon="['fas', 'trophy']"/>
                获奖经历
            </div>
            <ul class="list-disc ml-12">
                <li>
                    <span class="font-bold">竞赛获奖: </span>
                    蓝桥杯国赛Java研究生组优秀奖 蓝桥杯省赛Java研究生组一等奖 本科生学术科技论坛特等奖 美国大学生数学竞赛S奖
                </li>
                <li>
                    <span class="font-bold">专业证书: </span>
                    思科认证网络工程师 谷歌技术支持认证
                </li>
                <li>
                    <span class="font-bold">校奖学金: </span>
                    校一等奖学金(3次) 校二等奖学金(7次)
                </li>
                <li>
                    <span class="font-bold">志愿活动: </span>
                    2019仁恒凤凰山居环保半程马拉松优秀志愿者
                </li>
            </ul>
        </div>
        <div class="divider"></div>
        <div class="section">
            <div class="text-xl font-bold">
                <font-awesome-icon :icon="['fas', 'screwdriver-wrench']"/>
                个人技能
            </div>
            <ul class="list-disc ml-12">
                <li>
                    <span class="font-bold">Java后端开发: </span>
                    <span class="font-family-code">
                        Java, MySQL, Spring, Spring Boot, MyBatis, Redis, RabbitMQ, WebSocket
                    </span>
                </li>
                <li>
                    <span class="font-bold">Web前端开发: </span>
                    <span class="font-family-code">
                        Sass, TailwindCSS, DaisyUI, TypeScript, Vue3, Pinia
                    </span>
                </li>
                <li>
                    <span class="font-bold">机器学习: </span>
                    <span class="font-family-code">
                        PyTorch, Graph Neural Networks, Vision Transformer, Deep Reinforcement Learning
                    </span>
                </li>
                <li>
                    <span class="font-bold">其他技能: </span>
                    <span class="font-family-code">
                        Django, Nginx, Docker, Git, Markdown, LaTex
                    </span>
                </li>
            </ul>
        </div>
        <div class="divider"></div>
        <div class="section">
            <div class="text-xl font-bold">
                <font-awesome-icon :icon="['fab', 'black-tie']"/>
                自我评价
            </div>
            <div class="indent-8">
                我是一位充满热情的计算机专业学生，对计算机技术及其相关领域保持着浓厚的兴趣，乐于学习新鲜事物，探索自己的非舒适区。凭借扎实的编程基础和出色的问题解决能力，我期待着能有机会迎接新的挑战，和贵公司共创美好的明天。
            </div>
        </div>
    </div>
    <morningstar-footer v-if="isDesktop()" class="pt-2"/>
</template>

<style lang="scss" scoped>
@use "@/assets/css/fonts" as *;

.section {
    .items {
        .item {
            margin-bottom: 0.3rem;
            padding: 0.2rem 1rem;

            .time {
                float: right;
                font-weight: bold;
                color: gray;
                @apply font-family-code;
            }
        }
    }
}
</style>