<script lang="ts" setup>
import { defineComponent, h, ref } from 'vue';
import BaseLayout from '@/views/practice/BaseLayout.vue';

interface Item {
    name: string;
    url: string;
}

const items: Item[] = [
    {
        name: '应届生求职网',
        url: 'https://www.yingjiesheng.com/',
    },
    {
        name: '牛客网',
        url: 'https://www.nowcoder.com/',
    },
    {
        name: '前程无忧(51Job)',
        url: 'https://www.51job.com/',
    },
    {
        name: '智联招聘',
        url: 'https://xiaoyuan.zhaopin.com/scrd/delivery/record',
    },
    {
        name: 'BOSS直聘',
        url: 'https://www.zhipin.com/web/geek/recommend',
    },
    {
        name: '高校人才网',
        url: 'https://www.gaoxiaojob.com/member/person/resume',
    },
];

const staticElement = h(
    'ul',
    {
        class: 'rocket-list-style text-lg',
    },
    items.map(item =>
        h(
            'li',
            { class: 'py-1' },
            h(
                'a',
                {
                    class: 'pl-2',
                    href: item.url,
                    target: '_blank',
                },
                item.name
            )
        )
    )
);

const dynamicComponent = defineComponent({
    setup() {
        const count = ref(0);

        const increment = () => {
            count.value++;
        };

        return () => {
            // 使用 h 函数创建虚拟 DOM
            return h('div', { class: 'flex items-center justify-between space-x-4' }, [
                h('p', { class: 'w-40' }, `当前Offer数: ${count.value}`),
                h('button', { onClick: increment, class: 'border px-3 py-1 rounded-xl' }, '增加'),
            ]);
        };
    },
});
</script>

<template>
    <base-layout>
        <div>
            <div class="title">静态组件</div>
            <div class="component">
                <component :is="staticElement" />
            </div>
            <div class="title">动态组件</div>
            <div class="component">
                <component :is="dynamicComponent" />
            </div>
        </div>
    </base-layout>
</template>

<style lang="scss" scoped>
.title {
    @apply text-lg text-center mb-3;
}
.component {
    @apply mb-4 border-2 px-10 py-2 rounded-3xl;
}
</style>
