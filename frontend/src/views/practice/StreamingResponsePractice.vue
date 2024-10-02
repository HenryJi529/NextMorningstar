<script setup lang="ts">
import {ref} from "vue";
import {RadioGroup as ARadioGroup, RadioButton as ARadioButton} from 'ant-design-vue'


const data = ref<string[]>([]);
const endpoint = ref<string>('h3');
const method = ref<string>('Fetch');


const clear = () => {
    data.value = [];
}

const requestByEventSource = (endpoint: string) => {
    const eventSource = new EventSource(endpoint);

    eventSource.onmessage = function(event) {
        data.value.push(event.data);
        console.log('收到消息:', data);
    };

    eventSource.onopen = function(event) {
        console.log('EventSource 已打开:', event);
    };

    eventSource.onerror = function(event) {
        console.error('EventSource 错误:', event);
    };
}

const requestByFetch = (endpoint: string) => {
    fetch(endpoint)
        .then(response => {
            const reader = (response.body as ReadableStream<Uint8Array>).getReader();
            const decoder = new TextDecoder();

            function read() {
                reader.read().then(({ done, value }) => {
                    if (done) return;
                    data.value.push(decoder.decode(value));
                    read();
                });
            }
            read();
        });
}

const send = () => {
    if (method.value === 'Fetch') {
        requestByFetch("/api/practice/" + endpoint.value);
    } else {
        requestByEventSource("/api/practice/" + endpoint.value);
    }
}

</script>

<template>
    <div class="flex flex-col items-center">
        <div class="mb-4">
            <div class="mb-4">
                <a-radio-group v-model:value="endpoint" button-style="solid">
                    <a-radio-button value="h1" :disabled="method === 'EventSource'">StreamingResponseBody</a-radio-button>
                    <a-radio-button value="h2" :disabled="method === 'EventSource'">ResponseBodyEmitter</a-radio-button>
                    <a-radio-button value="h3">SseEmitter</a-radio-button>
                </a-radio-group>
            </div>
            <div class="flex items-center justify-center">
                <a-radio-group v-model:value="method" button-style="solid">
                    <a-radio-button value="Fetch">Fetch</a-radio-button>
                    <a-radio-button value="EventSource">EventSource</a-radio-button>
                </a-radio-group>
            </div>
        </div>
        <div class="flex items-center space-x-4">
            <button class="btn" @click="send">发送</button>
            <button class="btn" @click="clear">清空</button>
        </div>
        <div>
            <template v-for="item in data" :key="item">
                <div>{{item}}</div>
            </template>
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>