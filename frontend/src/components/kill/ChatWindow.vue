<script lang="ts" setup>
import {onMounted, onUnmounted, ref} from 'vue';
import SockJS from 'sockjs-client/dist/sockjs.min.js'
import {CompatClient, type Frame, Stomp} from '@stomp/stompjs';
import {useUserStore} from "@/stores/users";
import type {ChatMessage} from "@/types/websocket";
import {API_WEBSOCKET_CONNECTION} from "@/constants/ApiConstant";
import {TOKEN} from "@/constants/LocalStorageConstant";

const userStore = useUserStore();
let stompClient: CompatClient;
let wsCreateHandler: number;
const chatMessages = ref<ChatMessage[]>([]);
const receiverId = ref<string>("");
const content = ref<string>("");
const isConnected = ref(false);

const subscribe = () => {
    console.log("订阅...");
    stompClient.subscribe("/topic/kill.chat.all", (res) => {
        const chatMessage: ChatMessage = JSON.parse(res.body);
        chatMessages.value.push(chatMessage);
    });
    stompClient.subscribe(`/queue/kill.chat.${userStore.id}`, (res) => {
        const chatMessage: ChatMessage = JSON.parse(res.body);
        chatMessages.value.push(chatMessage);
    });
}

const connect = () => {
    stompClient = Stomp.over(new SockJS(API_WEBSOCKET_CONNECTION));
    stompClient.debug = (msg) => {
        console.log("debug: " + msg);
    }
    stompClient.heartbeat.outgoing = 10000;
    stompClient.heartbeat.incoming = 10000;
    stompClient.connect(
        {
            Authorization: localStorage.getItem(TOKEN),
        },
        (frame: Frame) => {
            console.log('connected: ' + frame);
            subscribe();
            isConnected.value = true;
        },
        (error: Frame) => {
            console.error('连接失败:', error);
            if(wsCreateHandler){
                clearTimeout(wsCreateHandler);
            }
            wsCreateHandler = window.setTimeout(() => {
                console.log("重连...");
                connect();
            }, 1000);
        }
    );
}

const disconnect = () => {
    if (stompClient && stompClient.connected) {
        stompClient.disconnect(() => {
            console.log("断开连接");
        });
        isConnected.value = false;
    }
}

const sendToAll = () => {
    let chatMessage: ChatMessage = {senderId: userStore.id, receiverId: "", content: content.value};
    stompClient.send("/ws/sendToAll", {}, JSON.stringify(chatMessage));
    console.log("广播消息发送成功");
}

const sendToOne = () => {
    let chatMessage: ChatMessage = {senderId: userStore.id, receiverId: receiverId.value, content: content.value};
    stompClient.send("/ws/sendToOne", {}, JSON.stringify(chatMessage));
    console.log("点对点消息发送成功");
}


onMounted(() => {
    connect();
});

onUnmounted(() => {
    disconnect();
});

</script>

<template>
    <div v-if="isConnected">
        <div>{{ userStore.id }}</div>
        <div>接收者：<input v-model="receiverId" type="text"/></div>
        <div>发送内容：<input v-model="content" type="text"/></div>
        <div>
            <button class="btn" @click="sendToOne">点对点</button>
            <button class="btn" @click="sendToAll">群发</button>
        </div>

        <div>
            {{ chatMessages }}
        </div>
    </div>
    <div v-else>Connecting...</div>
</template>

<style lang="scss" scoped>

</style>