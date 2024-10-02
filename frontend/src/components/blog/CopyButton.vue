<script lang="ts" setup>
import useClipboard from "vue-clipboard3";
import {ref} from "vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";

const {toClipboard} = useClipboard();

const props = defineProps<{
    source: string;
}>();

const copied = ref(false);


const copy = async () => {
    try {
        await toClipboard(props.source);
        copied.value = true;
        setTimeout(() => {
            copied.value = false;
        }, 1500);
        console.log('复制到剪贴板')
    } catch (e) {
        console.error(e)
    }
}


</script>

<template>
    <div class="text-xl visible">
        <div v-if="copied" class="dark:text-green-300 text-green-700">
            <font-awesome-icon :icon="['fas', 'clipboard-check']"/>
        </div>
        <div v-else @click="copy">
            <font-awesome-icon :icon="['fas', 'clipboard']"/>
        </div>
    </div>
</template>

<style lang="scss" scoped>

</style>