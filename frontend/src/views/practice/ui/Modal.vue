<script setup lang="ts">
import {ref} from "vue";

const isModalOpen = ref(false);

const openModal = () => {
    let el = document.getElementById("modal-container") as HTMLDivElement;
    isModalOpen.value = true;
    el.classList.remove('invisible');
}

const closeModal = () => {
    let el = document.getElementById("modal-container") as HTMLDivElement;
    isModalOpen.value = false;
    el.classList.add('invisible');
}
</script>

<template>
    <div>
        <div class="h-[30vh] w-full relative">
            <div class="w-full h-full flex justify-center items-center">
                <button class="btn cursor-pointer" @click="openModal">
                    <span v-if="isModalOpen">模态窗已开</span>
                    <span v-else>打开模态窗</span>
                </button>
            </div>
            <div id="modal-container" class="left-0 top-0 invisible absolute z-100 h-full w-full flex justify-center items-center">
                <div class="relative w-full h-full">
                    <div class="overlay-mask absolute w-full h-full" style="background-color: rgba(0,26,57,0.95);">
                    </div>
                    <div class="w-full h-full flex justify-center items-center">
                        <div class="relative w-[90%] h-[70%]" style="background-color: rgba(0,26,57,0.9);">
                            <div class="w-full h-full relative p-[2.5vw]"
                                 style="clip-path: polygon(1% 0%, 99% 0%, 100% 2%, 100% 98%, 99% 100%, 1% 100%, 0% 98%, 0% 2%);
                                    box-shadow: inset 0 0 1vw #419ce1;
                                    margin: 0 auto;">
                                    <span class="absolute right-0 top-0 font-bold cursor-pointer w-[4vh] h-[4vh] flex items-center justify-center"
                                          style="font-size: 3.5vh;background: radial-gradient(rgba(69, 106, 153, 1), rgba(105, 169, 237, 1));color: rgba(5, 34, 66, 1)"
                                          @click="closeModal">
                                        &times;
                                    </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
.overlay-mask {
    mask-image:
        linear-gradient(black, black 15%, transparent 15%, transparent 85%, black 85%),
        linear-gradient(90deg, black, black 5%, transparent 5%, transparent 95%, black 95%);
}
</style>