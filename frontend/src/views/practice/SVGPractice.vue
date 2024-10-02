<script lang="ts" setup>

import RightArrow from "@/views/practice/svg/RightArrow.vue";
import InvertedEquilateralTriangle from "@/views/practice/svg/InvertedEquilateralTriangle.vue";
</script>

<template>
    <div>
        <div class="flex flex-col space-y-4">
            <div class="flex items-center justify-center space-x-4">
                <div class="w-[6rem] cursor-pointer">
                    <RightArrow :head-length="40" :head-width="40" :shaft-length="100" :shaft-width="20"
                                fill-color="red"/>
                </div>
                <div class="w-[6rem]">
                    <RightArrow :head-length="40" :head-width="60" :shaft-length="100" :shaft-width="8"
                                fill-color="rgb(128, 0, 128)"/>
                </div>
                <div class="w-[6rem]">
                    <RightArrow :head-length="40" :head-width="60" :shaft-length="100" :shaft-width="8"/>
                </div>
            </div>
            <div class="flex items-center justify-center space-x-4">
                <div class="w-[4rem]">
                    <InvertedEquilateralTriangle fill-color="red"/>
                </div>
                <div class="w-[6rem]">
                    <InvertedEquilateralTriangle fill-color="blue"/>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>

</style>