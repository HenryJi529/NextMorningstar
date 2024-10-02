<script lang="ts" setup>
import {DownOutlined} from "@ant-design/icons-vue"
import {ref} from "vue";

interface Item {
    id: number;
    name: string;
}

const isDropdownVisible = ref(false);
const items: Item[] = [
    {
        id: 1,
        name: "item1"
    },
    {
        id: 2,
        name: "item2"
    },
    {
        id: 3,
        name: "item3"
    }
]
const toggleDropdown = () => {
    isDropdownVisible.value = !isDropdownVisible.value;
};

</script>

<template>
    <div class="flex justify-between items-center w-full">
        <div>前一个区域</div>
        <div class="relative">
            <div class="flex justify-center items-center space-x-1 cursor-pointer" @click="toggleDropdown">
                <span>点击下拉</span>
                <down-outlined class="text-xs"/>
            </div>
            <transition name="slide">
                <div v-if="isDropdownVisible" class="absolute w-full">
                    <template v-for="item in items" :key="item.id">
                        <div class="text-center">{{ item.name }}</div>
                    </template>
                </div>
            </transition>
        </div>
        <div>后一个区域</div>
    </div>
</template>

<style lang="scss" scoped>
.slide-enter-active, .slide-leave-active {
    transition: opacity 1s ease;
}

.slide-enter-from, .slide-leave-to {
    opacity: 0; /* 初始透明度为0 */
}

.slide-enter-to, .slide-leave-from {
    opacity: 1; /* 结束时完全可见 */
}
</style>