<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { RadioGroup as ARadioGroup, RadioButton as ARadioButton } from 'ant-design-vue';
import BaseLayout from '@/views/practice/BaseLayout.vue';
import { getDefaultTheme, setTheme } from '@/utils/theme';

onMounted(() => {
    setTheme('light');
});

onUnmounted(() => {
    setTheme(getDefaultTheme());
});

const messages = ref<string[]>([]);
const slug = ref<'streaming-response-body' | 'response-body-emitter' | 'sse-emitter'>(
    'sse-emitter'
);
const endpoint = computed(() => import.meta.env.VITE_API_BASE_PATH + '/practice/streaming-response/' + slug.value);
const method = ref<'Fetch' | 'EventSource'>('Fetch');
watch(method, () => {
    if (method.value === 'EventSource') {
        slug.value = 'sse-emitter';
    }
});

const clear = () => {
    messages.value = [];
};

const requestByEventSource = () => {
    const eventSource = new EventSource(endpoint.value);

    eventSource.onmessage = function (event) {
        messages.value.push(event.data);
        console.log('收到消息:', event.data);
    };

    eventSource.onopen = function (event) {
        console.log('EventSource 已打开:', event);
    };

    eventSource.onerror = function (event) {
        console.error('EventSource 错误:', event);
    };
};

const requestByFetch = () => {
    fetch(endpoint.value).then(response => {
        const reader = (response.body as ReadableStream<Uint8Array>).getReader();
        const decoder = new TextDecoder();

        function read() {
            reader.read().then(({ done, value }) => {
                if (done) return;
                messages.value.push(decoder.decode(value));
                read();
            });
        }
        read();
    });
};

const requestUtils = {
    Fetch: requestByFetch,
    EventSource: requestByEventSource,
};

const send = () => {
    clear();
    requestUtils[method.value]();
};
</script>

<template>
    <base-layout>
        <div class="flex flex-col items-center">
            <div class="mb-4">
                <div class="mb-4">
                    <a-radio-group v-model:value="slug" button-style="solid">
                        <a-radio-button
                            value="streaming-response-body"
                            :disabled="method === 'EventSource'"
                            >StreamingResponseBody</a-radio-button
                        >
                        <a-radio-button
                            value="response-body-emitter"
                            :disabled="method === 'EventSource'"
                            >ResponseBodyEmitter</a-radio-button
                        >
                        <a-radio-button value="sse-emitter">SseEmitter</a-radio-button>
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
                <template v-for="message in messages" :key="message">
                    <div>{{ message }}</div>
                </template>
            </div>
        </div>
    </base-layout>
</template>

<style scoped lang="scss"></style>
