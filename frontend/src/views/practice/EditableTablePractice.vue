<script setup lang="ts">

import {onMounted, ref, watch} from 'vue';
import dayjs, { Dayjs } from 'dayjs';
import {Input as AInput, Button as AButton, DatePicker as ADatePicker, TimePicker as ATimePicker} from 'ant-design-vue'

interface Info {
    id: string,
    name: string,
    age: number,
    date: Dayjs,
    time: Dayjs,
}

const isEditing = ref(false);
const info = ref<Info[]>([]);

onMounted(() => {
    setTimeout(() => {
        info.value = [
            {
                id: '1',
                name: '张三',
                age: 18,
                date: dayjs("2024/10/25", "YYYY/MM/DD"),
                time: dayjs("13:12:35", 'HH:mm:ss')
            },
            {
                id: '2',
                name: '李四',
                age: 29,
                date: dayjs("2025/02/05", "YYYY/MM/DD"),
                time: dayjs("16:12:35", 'HH:mm:ss')
            }
        ]
    }, 1000);
})

watch(info, ()=>{
    console.log(info.value.map(item => {
        return {
            id: item.id,
            name: item.name,
            age: item.age,
            date: item.date.format("YYYY/MM/DD"),
            time: item.time.format("hh:mm:ss")
        }
    }));
}, {
    deep: true
})

</script>

<template>
    <div>
        <div class="flex justify-end items-center">
            <a-button @click="isEditing=true">编辑</a-button>
            <a-button @click="isEditing=false">保存</a-button>
        </div>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Age</th>
                    <th>Date</th>
                    <th>Time</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="item in info" :key="item.id">
                    <td class="w-12" >
                        <div v-if="!isEditing" class="text-center" >
                            {{ item.id }}
                        </div>
                        <a-input v-else v-model:value="item.id" />
                    </td>
                    <td class="w-12" >
                        <div v-if="!isEditing" class="text-center">
                            {{ item.name }}
                        </div>
                        <a-input v-else v-model:value="item.name" />
                    </td>
                    <td class="w-24" >
                        <div v-if="!isEditing" class="text-center">
                            {{ item.age }}
                        </div>
                        <a-input v-else v-model:value="item.age" />
                    </td>
                    <td class="w-32" >
                        <div v-if="!isEditing" class="text-center">
                            {{ item.date.format("YYYY/MM/DD") }}
                        </div>
                        <a-date-picker v-else v-model:value="item.date" />
                    </td>
                    <td class="w-32" >
                        <div v-if="!isEditing" class="text-center">
                            {{ item.time.format("hh:mm:ss") }}
                        </div>
                        <a-time-picker v-else v-model:value="item.time" />
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<style scoped lang="scss">

</style>