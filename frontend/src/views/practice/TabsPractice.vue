<script lang="ts" setup>

import {type Component, computed, defineComponent, h, ref, shallowRef} from "vue";
import ComponentCContent from '@/views/practice/tabs/ComponentC.vue'
import {useRoute} from "vue-router";
import {getFirstParam} from "@/utils/handleHttp";

const route = useRoute();

const componentA = defineComponent({
    name: "组件A",
    setup() {
        return () => {
            return "Content A"
        }
    }
})
const componentB = defineComponent({
    name: "组件B",
    setup() {
        return () => {
            return "Content B"
        }
    }
})
const componentC = defineComponent({
    name: "组件C",
    setup() {
        return () => h(ComponentCContent)
    }
})
const currentComponent1 = shallowRef<Component>(componentA);
const componentList1 = [
    componentA,
    componentB,
    componentC
]


const componentMap2: Record<string, Component> = {
    "A": componentA,
    "B": componentB,
    "C": ComponentCContent
}
const selectedTabName2 = getFirstParam(route.query['tab2'] as string[] | string);
const activeTabName2 = ref(Object.keys(componentMap2).includes(selectedTabName2) ? selectedTabName2 : 'A')
const currentComponent2 = computed(() => {
    return componentMap2[activeTabName2.value]
})


</script>

<template>
    <div>
        <div>
            <div>通过组件的name属性</div>
            <div class="flex justify-center items-center space-x-2">
                <template v-for="component in componentList1">
                    <div :class="{'text-blue-500': currentComponent1.name === component.name}"
                         class="cursor-pointer p-2"
                         @click="currentComponent1 = component">
                        {{ component.name }}
                    </div>
                </template>
            </div>
            <div>
                <component :is="currentComponent1"/>
            </div>
        </div>

        <hr class="my-4"/>

        <div>
            <div>通过activeTabName</div>
            <div class="flex justify-center items-center space-x-2">
                <template v-for="tabName in Object.keys(componentMap2)">
                    <div :class="{'text-blue-500': activeTabName2 === tabName}" class="cursor-pointer p-2"
                         @click="activeTabName2 = tabName">
                        {{ tabName }}
                    </div>
                </template>
            </div>
            <div>
                <component :is="currentComponent2"/>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>

</style>