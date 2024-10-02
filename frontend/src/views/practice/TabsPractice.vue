<script setup lang="ts">

import {defineComponent, shallowRef, h, ref, computed, type Component} from "vue";
import ComponentCContent from './Tabs/ComponentC.vue'


const componentA = defineComponent({
    name: "A",
    setup() {
        return () => {
            return "Content A"
        }
    }
})
const componentB = defineComponent({
    name: "B",
    setup() {
        return () => {
            return "Content B"
        }
    }
})
const componentC = defineComponent({
    name: "C",
    setup() {
        return () => h(ComponentCContent)
    }
})

const currentComponent1 = shallowRef<any>(componentA);
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

const activeTabName2 = ref('A')
const currentComponent2 = computed(()=>{
    return componentMap2[activeTabName2.value]
})


</script>

<template>
    <div>
        <div>
            <div>通过组件的name属性</div>
            <div class="flex justify-center items-center space-x-2">
                <template v-for="component in componentList1">
                    <div class="cursor-pointer p-2" :class="{'text-blue-500': currentComponent1.name === component.name}"
                         @click="currentComponent1 = component">
                        {{component.name}}
                    </div>
                </template>
            </div>
            <div>
                <component :is="currentComponent1" />
            </div>
        </div>

        <hr class="my-4" />

        <div>
            <div>通过activeTabName</div>
            <div class="flex justify-center items-center space-x-2">
                <template v-for="tabName in Object.keys(componentMap2)">
                    <div class="cursor-pointer p-2" :class="{'text-blue-500': activeTabName2 === tabName}"
                         @click="activeTabName2 = tabName">
                        {{tabName}}
                    </div>
                </template>
            </div>
            <div>
                <component :is="currentComponent2" />
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>