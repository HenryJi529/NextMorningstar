<script setup lang="ts">
import type {TocTreeNode} from "@/utils/handleMarked";

defineProps<{
    tocTree: TocTreeNode[];
}>()
</script>

<template>
    <div class="pl-4">
        <template v-for="tocNode in tocTree">
            <details v-if="tocNode.children.length">
                <summary><a :href="'#' + tocNode.anchor" v-html="tocNode.text" class="hover:text-orange-500"></a></summary>
                <toc-section :toc-tree="tocNode.children"/>
            </details>
            <div v-else class="pl-4">
                <a :href="'#' + tocNode.anchor" v-html="tocNode.text" class="hover:text-orange-500"></a>
            </div>
        </template>
    </div>
</template>

<style scoped lang="scss">
summary::marker{
    @apply cursor-pointer;
}
</style>