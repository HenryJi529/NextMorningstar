<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import SockJS from 'sockjs-client/dist/sockjs.min.js'
import {CompatClient, Stomp} from '@stomp/stompjs';
import {useUserStore} from "@/stores/users";

const userStore = useUserStore();

let stompClient: CompatClient;
const messages = ref<Object[]>([]);

const connect = () => {
    stompClient = Stomp.over(new SockJS('/ws'));
    stompClient.connect(
        {
            'Authorization': userStore.token,
        },
        () => {
            console.log('连接成功');
            // 订阅主题
            stompClient.subscribe("/topic/notice", (res: any) => {
                const receivedMessage = JSON.parse(res.body);
                messages.value.push(receivedMessage);
            });

            stompClient.subscribe("/user/queue/greeting", (res) => {
                console.log("订阅点对点成功：" + res.body);
            });
        },
        error => {
            console.error('连接失败:', error);
        }
    );
}

const disconnect = () => {
    if (stompClient && stompClient.connected) {
        stompClient.disconnect(() => {
            console.log("断开连接");
        });
    }
}


const sendToAll = () => {
    stompClient.send("/app/sendToAll", {},  "亲爱的大冤种们，由于一只史诗级的BUG，系统版本已经被迫回退到了0.0.1。");
    console.log("广播消息发送成功");
}

const sendToOne = (username: string) => {
    stompClient.send(`/app/sendToOne/${username}`, {}, `嗨! ${username} 我是 admin ,想和您交个朋友`);
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
    <div>
        <template v-for="message in messages">
            {{message}}
        </template>
    </div>
</template>

<style scoped lang="scss">

</style>