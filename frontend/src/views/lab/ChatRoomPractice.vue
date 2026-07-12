<script lang="ts" setup>
import { onMounted, onUnmounted, ref } from 'vue';
import SockJS from 'sockjs-client/dist/sockjs.min.js';
import { CompatClient, type Frame, Stomp } from '@stomp/stompjs';
import { useUserStore } from '@/stores/users';
import type { ChatMessage } from '@/types/websocket';
import { API_WEBSOCKET_CHAT } from '@/constants/api';
import { LocalStorageKey } from '@/constants/storage';
import BaseLayout from '@/views/lab/BaseLayout.vue';

const userStore = useUserStore();
let stompClient: CompatClient;
let wsCreateHandler: number;
const chatMessages = ref<ChatMessage[]>([]);
const receiverId = ref<string>('');
const content = ref<string>('');
const isConnected = ref(false);

const subscribe = () => {
    console.log('订阅...');
    stompClient.subscribe('/topic/lab.chat.all', res => {
        const chatMessage: ChatMessage = JSON.parse(res.body);
        chatMessages.value.push(chatMessage);
    });
    stompClient.subscribe(`/queue/lab.chat.${userStore.id}`, res => {
        const chatMessage: ChatMessage = JSON.parse(res.body);
        chatMessages.value.push(chatMessage);
    });
};

const connect = () => {
    stompClient = Stomp.over(
        () => new SockJS(import.meta.env.VITE_API_BASE_PATH + API_WEBSOCKET_CHAT)
    );
    stompClient.debug = msg => {
        console.log('debug: ' + msg);
    };
    stompClient.heartbeat.outgoing = 10000;
    stompClient.heartbeat.incoming = 10000;
    stompClient.connect(
        {
            Authorization: localStorage.getItem(LocalStorageKey.ACCESS_TOKEN),
        },
        (frame: Frame) => {
            console.log('connected: ' + frame);
            subscribe();
            isConnected.value = true;
        },
        (error: Frame) => {
            console.error('连接失败:', error);
            if (wsCreateHandler) {
                clearTimeout(wsCreateHandler);
            }
            wsCreateHandler = window.setTimeout(() => {
                console.log('重连...');
                connect();
            }, 1000);
        }
    );
};

const disconnect = () => {
    if (stompClient && stompClient.connected) {
        stompClient.disconnect(() => {
            console.log('断开连接');
        });
        isConnected.value = false;
    }
};

const sendToAll = () => {
    const chatMessage: ChatMessage = {
        senderId: userStore.id,
        receiverId: '',
        content: content.value,
    };
    stompClient.send('/chat/sendToAll', {}, JSON.stringify(chatMessage));
    console.log('广播消息发送成功');
};

const sendToOne = () => {
    const chatMessage: ChatMessage = {
        senderId: userStore.id,
        receiverId: receiverId.value,
        content: content.value,
    };
    stompClient.send('/chat/sendToOne', {}, JSON.stringify(chatMessage));
    console.log('点对点消息发送成功');
};

onMounted(() => {
    connect();
});

onUnmounted(() => {
    disconnect();
});
</script>

<template>
    <base-layout>
        <div v-if="isConnected" class="flex flex-col gap-1">
            <div>发送者: {{ userStore.id }}</div>
            <div>接受者: <input v-model="receiverId" type="text" class="bg-transparent" /></div>
            <div>发送内容: <input v-model="content" type="text" class="bg-transparent" /></div>
            <div class="flex justify-start items-center gap-2">
                <button class="btn btn-sm w-[6em]" @click="sendToOne">点对点</button>
                <button class="btn btn-sm w-[6em]" @click="sendToAll">群发</button>
            </div>
            <div
                v-for="chatMessage in chatMessages"
                :key="chatMessage.senderId + chatMessage.receiverId + chatMessage.content">
                {{ chatMessage }}
            </div>
        </div>
        <div v-else>Connecting...</div>
    </base-layout>
</template>

<style lang="scss" scoped></style>
