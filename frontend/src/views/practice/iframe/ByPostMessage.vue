<script setup lang="ts">
import {onMounted} from "vue";
import {message} from "ant-design-vue";

const fullHost = `${window.location.protocol}//${window.location.host}`;

const send = () => {
    window.parent.postMessage('hello from child[post message]', fullHost);
}


onMounted(()=>{
    window.addEventListener('message', (event) => {
        if (event.origin !== fullHost) return;
        if(typeof event.data === 'object') return;
        message.info(event.data)
    });
})
</script>

<template>
    <button class="btn" @click="send">发送消息给父页面</button>
</template>

<style scoped lang="scss">

</style>