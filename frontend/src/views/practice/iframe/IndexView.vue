<script lang="ts" setup>

import {useRouter} from "vue-router";
import {onMounted, ref, watch} from "vue";
import {message} from "ant-design-vue";

const router = useRouter();
const iframeByPostMessage = ref<HTMLIFrameElement>();
const iframeByMessageChannel = ref<HTMLIFrameElement>();
const fullHost = `${window.location.protocol}//${window.location.host}`;


const broadcastChannel = new BroadcastChannel('my_channel');
const messageChannel = new MessageChannel();

const sendByBroadcastChannel = () => {
    broadcastChannel.postMessage('hello from parent[broadcast channel]');
}

const sendByPostMessage = () => {
    iframeByPostMessage.value?.contentWindow?.postMessage('hello from parent[post message]', "*");
}

const sendByMessageChannel = () => {
    messageChannel.port1.postMessage('hello from parent[message channel]');
}

const setupMessageChannel = () => {
    setTimeout(()=>{
        iframeByMessageChannel.value?.contentWindow?.postMessage(
            'INIT_CHANNEL',  // 初始化消息
            '*',             // 目标origin
            [messageChannel.port2]  // 传输端口
        );
    }, 1000);
}

watch(() => iframeByMessageChannel.value, () => {
    // Message Channel发送初始化消息给子页面
    if (iframeByMessageChannel.value) {
        const iframe = iframeByMessageChannel.value;
        if (iframe.contentDocument?.readyState === 'complete') {
            setupMessageChannel();
        } else {
            iframe.addEventListener('load', setupMessageChannel, { once: true });
        }
    }
})

onMounted(()=> {
    // Post Message接收子页面消息
    window.addEventListener('message', function(event) {
        if (event.origin !== fullHost) return;
        if(typeof event.data === 'object') return;
        message.info(event.data);
    });

    // Broadcast Channel接收子页面消息
    broadcastChannel.onmessage = (event) => {
        message.info(event.data);
    };

    // Message Channel接收子页面消息
    messageChannel.port1.onmessage = (event) => {
        message.info(event.data);
    };
});


</script>

<template>
    <div class="flex-1 w-full flex flex-col">
        <div class="flex justify-center mb-4 space-x-4 flex-wrap">
            <div class="btn my-2" @click="sendByPostMessage">Post Message</div>
            <div class="btn my-2" @click="sendByBroadcastChannel">Broadcast Channel</div>
            <div class="btn my-2" @click="sendByMessageChannel">Message Channel</div>
        </div>
        <div class="flex-1 grid w-full grid-cols-2 gap-8">
            <iframe ref="iframeByPostMessage" :src="router.resolve({name: 'practice-iframe-communication-by-post-message'}).href" class="w-full h-full"></iframe>
            <iframe ref="iframeByMessageChannel" :src="router.resolve({name: 'practice-iframe-communication-by-message-channel'}).href" class="w-full h-full"></iframe>
            <iframe :src="router.resolve({name: 'practice-iframe-communication-by-broadcast-channel-a'}).href" class="w-full h-full"></iframe>
            <iframe :src="router.resolve({name: 'practice-iframe-communication-by-broadcast-channel-b'}).href" class="w-full h-full"></iframe>
        </div>
    </div>
</template>

<style lang="scss" scoped>

</style>