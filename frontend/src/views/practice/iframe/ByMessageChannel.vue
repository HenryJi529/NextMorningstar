<script setup lang="ts">
import {onBeforeMount, ref} from "vue";
import {message} from "ant-design-vue";

const parentPort = ref<MessagePort>();

onBeforeMount(() => {
    window.addEventListener('message', (event) => {
        if(typeof event.data === 'object') return;
        if (event.data === 'INIT_CHANNEL' && event.ports[0]) {
            // ports参数并不一定是port的列表，只是转移的对象的列表
            parentPort.value = event.ports[0];
            message.info('已与父页面建立通信通道...')
            parentPort.value.onmessage = (e) => {
                message.info(e.data)
            };
        }
    });
})

const send = () => {
    parentPort.value?.postMessage('hello from child[message channel]');
}
</script>

<template>
    <div class="flex flex-col items-center justify-center h-screen">
        <div class="text-lg font-bold">MessageChannel</div>
        <button class="btn" @click="send">发送消息给父页面</button>
    </div>
</template>

<style scoped lang="scss">

</style>