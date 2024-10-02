<script lang="ts" setup>
import type {TocTreeNode} from "@/utils/handleMarked";
import {useBlogStore} from "@/stores/blog";

defineProps<{
    tocTree: TocTreeNode[];
}>()
const blogStore = useBlogStore();

const jumpToAnchor = (anchor: string) => {
    blogStore.isMobileMenuOpen = false;
    const targetElement = document.getElementById(anchor);
    setTimeout(() => {
        if (targetElement) {
            targetElement.scrollIntoView({behavior: 'auto'});
        }
    }, 100);
}

</script>

<template>
    <div class="pl-4">
        <template v-for="tocNode in tocTree" :key="tocNode.anchor">
            <details v-if="tocNode.children.length">
                <summary>
                    <span class="hover:text-orange-500 cursor-pointer" @click="jumpToAnchor(tocNode.anchor)"
                          v-html="tocNode.text"></span>
                </summary>
                <toc-section :toc-tree="tocNode.children"/>
            </details>
            <div v-else class="pl-4">
                <div class="hover:text-orange-500 cursor-pointer" @click="jumpToAnchor(tocNode.anchor)"
                     v-html="tocNode.text"></div>
            </div>
        </template>
    </div>
</template>

<style lang="scss" scoped>
summary::marker {
    @apply cursor-pointer;
}
</style>