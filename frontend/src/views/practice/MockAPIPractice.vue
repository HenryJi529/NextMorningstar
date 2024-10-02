<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { fakerEN, fakerZH_CN as fakerCN } from '@faker-js/faker';
import BaseLayout from '@/views/practice/BaseLayout.vue';

const messages = ref<string[]>([]);

const mockData = () => {
    const delay = Math.round(Math.random() * 2000 + 1000);
    const shouldFail = fakerEN.datatype.boolean(0.2);
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            if (shouldFail) {
                reject(new Error('模拟网络错误'));
            }
            const data = {
                nickname: fakerCN.person.fullName(),
                color: fakerCN.color.rgb(),
                email: fakerEN.internet.email(),
                text: fakerCN.lorem.paragraph(2),
            };
            resolve(data);
        }, delay);
    });
};

onMounted(() => {
    for (let i = 0; i < 10; i++) {
        mockData()
            .then(res => {
                messages.value.push(`接口模拟${i}成功: ${JSON.stringify(res)}`);
            })
            .catch(err => {
                messages.value.push(`接口模拟${i}失败: ${err.message}`);
            });
    }
});
</script>

<template>
    <base-layout>
        <div>
            <div v-for="(message, ind) in messages" :key="ind" class="w-full text-start">
                {{ message }}
            </div>
        </div>
    </base-layout>
</template>

<style lang="scss" scoped></style>
