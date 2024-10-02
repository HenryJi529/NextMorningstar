<script setup lang="ts">

import {RouterLink} from "vue-router";


</script>

<template>
    <div class="h-screen flex items-center justify-center">
        <div class="container grid grid-flow-row grid-cols-2 lg:grid-cols-3 gap-4 text-center">
            <div class="bg-primary">
                <router-link :to="'/kill'">三国杀(Kill)</router-link>
            </div>
            <div class="bg-primary">
                <router-link :to="'/nav'">晨星导航(Nav)</router-link>
            </div>
            <div class="bg-primary">
                <router-link :to="'/blog'">技术博客(Blog)</router-link>
            </div>
            <div class="bg-primary">
                <router-link :to="'/love'">爱の相册(Love)</router-link>
            </div>
            <div class="bg-primary">
                <router-link to="/joke">神兔笑话(Joke)</router-link>
            </div>
            <div class="bg-primary">
                <router-link to="/proxy">代理互助(Proxy)</router-link>
            </div>
            <div class="bg-primary">
                <router-link to="/rss">聚合阅读(Feed)</router-link>
            </div>
            <div class="bg-primary">
                <router-link to="/share">链接分享(Share)</router-link>
            </div>
            <div class="bg-primary">
                <router-link :to="'/resume'">个人简历(Resume)</router-link>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>