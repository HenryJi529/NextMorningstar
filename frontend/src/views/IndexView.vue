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
                <router-link :to="'/image'">开放图床(Image)</router-link>
            </div>
            <div class="bg-primary">
                技术博客(Blog)
            </div>
            <div class="bg-primary">
                爱人相册(lover)
            </div>
            <div class="bg-primary">
                神兔笑话(Joke)
            </div>
            <div class="bg-primary">
                代理互助(Proxy)
            </div>
            <div class="bg-primary">
                聚合阅读(Feed)
            </div>
            <div class="bg-primary">
                链接分享(Share)
            </div>
            <div class="bg-primary">
                <router-link :to="'/resume'">个人简历(Resume)</router-link>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>