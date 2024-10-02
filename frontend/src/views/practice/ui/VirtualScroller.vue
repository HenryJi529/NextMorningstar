<script setup lang="ts">
// @ts-ignore
import { RecycleScroller } from 'vue-virtual-scroller';
import "vue-virtual-scroller/dist/vue-virtual-scroller.css"

const data = Array.from({ length: 1000 }, (_, i) => ({
    id: i,
    name: `Item ${i + 1}`,
}));
</script>

<template>
    <recycle-scroller
        style="height: 500px"
        :items="data"
        :item-size="100"
        key-field="id"
        :buffer="200"
    >
        <template #default="{ item }">
            <div class="h-[100px] flex items-center justify-center">
                {{ item.name }}
            </div>
        </template>
    </recycle-scroller>
</template>

<style scoped lang="scss">
</style>
