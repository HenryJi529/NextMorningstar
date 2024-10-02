<script setup lang="ts">
import {onMounted} from "vue";
import {message} from "ant-design-vue";

const broadcastChannel = new BroadcastChannel('my_channel');

const send = () => {
    broadcastChannel.postMessage('hello from child A[broadcast channel]');
}

onMounted(()=>{
    broadcastChannel.onmessage = (event) => {
        message.info(event.data);
    };
})
</script>

<template>
    <div class="flex flex-col items-center justify-center h-screen">
        <div class="text-lg font-bold">BroadcastChannel A</div>
        <button class="btn" @click="send">发送消息给父页面</button>
    </div>
</template>

<style scoped lang="scss">

</style>