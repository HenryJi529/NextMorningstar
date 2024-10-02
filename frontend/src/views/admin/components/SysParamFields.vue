<script setup lang="ts">
import { Scope } from '@/types/system';
import { Select as ASelect } from 'ant-design-vue';

interface Data {
    name: string;
    value: string;
    scope: string;
    status: boolean;
    remark: string;
}

defineProps<{
    data: Data;
}>();
</script>

<template>
    <div class="flex flex-col space-y-4">
        <div class="flex space-x-16 items-center">
            <label class="font-bold w-32" for="name">Name</label>
            <input id="name" v-model="data.name" class="admin-manage-input" type="text" />
        </div>
        <div class="flex space-x-16 items-center">
            <label class="font-bold w-32" for="value">Value</label>
            <textarea
                id="value"
                v-model="data.value"
                class="admin-manage-input"
                rows="3"></textarea>
        </div>
        <div class="flex space-x-16 items-center">
            <label class="font-bold w-32" for="scope">Scope</label>
            <a-select
                id="scope"
                v-model:value="data.scope"
                class="flex-1"
                :options="
                    Object.entries(Scope).map(([key, value]) => ({
                        label: key,
                        value: value,
                    }))
                "
                @change="selectedValue => (data.scope = selectedValue as string)"></a-select>
        </div>
        <div class="flex space-x-16 items-center">
            <label class="font-bold w-32" for="status">Status</label>
            <div class="flex-1 p-2 flex items-center justify-center space-x-3" id="status">
                <div>关</div>
                <input type="checkbox" class="toggle toggle-lg" v-model="data.status" />
                <div>开</div>
            </div>
        </div>
        <div class="flex space-x-16 items-center">
            <label class="font-bold w-32" for="remark">Remark</label>
            <textarea
                id="remark"
                v-model="data.remark"
                class="admin-manage-input"
                rows="5"></textarea>
        </div>
    </div>
</template>

<style scoped lang="scss"></style>
