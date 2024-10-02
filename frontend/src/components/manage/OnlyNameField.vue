<script lang="ts" setup>
import { ref, watch } from 'vue';

const props = defineProps<{
    name: string;
}>();

const emit = defineEmits<{
    (e: 'update-name', name: string): void;
}>();

const name = ref(props.name);

watch(name, () => {
    emit('update-name', name.value);
});
</script>

<template>
    <div class="flex space-x-16 items-center">
        <span class="font-bold text-center w-32">Name</span>
        <input v-model="name" class="admin-manage-input" type="text" />
    </div>
</template>

<style lang="scss" scoped></style>
