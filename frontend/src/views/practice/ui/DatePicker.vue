<script setup lang="ts">
import {DatePicker as ADatePicker, ConfigProvider as AConfigProvider} from "ant-design-vue";
import {ref, watch} from "vue";
import calendarIcon from '@/assets/img/practice/Calendar.svg';
import zhCN from 'ant-design-vue/es/locale/zh_CN';
import dayjs, {type Dayjs} from 'dayjs';
import 'dayjs/locale/zh-cn';
dayjs.locale('zh-cn');

const date = ref(dayjs());

const disabledDate = (current: Dayjs) => {
    return current && current >= dayjs().startOf('day');
}

watch(date, () => {
    console.log("date:", date.value.format('YYYY年M月D日'));
});

</script>

<template>
    <div class="flex items-center space-x-2 cursor-pointer">
        <img :src="calendarIcon" alt="日历图标" class="h-4" />
        <a-config-provider :locale="zhCN">
            <a-date-picker v-model:value="date" :disabled-date="disabledDate">
                <template #suffixIcon>
                </template>
            </a-date-picker>
        </a-config-provider>
    </div>
</template>

<style scoped lang="scss">
:deep(.ant-picker) {
    border-width: 0;
    background-color: rgba(255,255,255,0.1);
    .ant-picker-clear {
        background-color: transparent;
    }
}
</style>