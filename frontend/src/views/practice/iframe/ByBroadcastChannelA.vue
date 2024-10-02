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
    <button class="btn" @click="send">发送消息给父页面</button>
</template>

<style scoped lang="scss">

</style>