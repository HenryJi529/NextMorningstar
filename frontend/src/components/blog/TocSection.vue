<script setup lang="ts">
import type {TocTreeNode} from "@/utils/handleMarked";
import {useBlogStore} from "@/stores/blog";

defineProps<{
    tocTree: TocTreeNode[];
}>()
const blogStore = useBlogStore();

const jumpToAnchor = (anchor: string ) => {
    blogStore.isMobileMenuOpen = false;
    const targetElement = document.getElementById(anchor);
    setTimeout(()=> {
        if (targetElement) {
            targetElement.scrollIntoView({ behavior: 'auto' });
        }
    }, 100);
}

</script>

<template>
    <div class="pl-4">
        <template v-for="tocNode in tocTree">
            <details v-if="tocNode.children.length">
                <summary>
                    <span @click="jumpToAnchor(tocNode.anchor)" v-html="tocNode.text" class="hover:text-orange-500 cursor-pointer"></span>
                </summary>
                <toc-section :toc-tree="tocNode.children"/>
            </details>
            <div v-else class="pl-4">
                <div @click="jumpToAnchor(tocNode.anchor)" v-html="tocNode.text" class="hover:text-orange-500 cursor-pointer"></div>
            </div>
        </template>
    </div>
</template>

<style scoped lang="scss">
summary::marker{
    @apply cursor-pointer;
}
</style>