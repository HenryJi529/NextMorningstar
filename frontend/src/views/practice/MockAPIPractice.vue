<script lang="ts" setup>
import {onMounted} from "vue";
import {fakerEN, fakerZH_CN as fakerCN} from '@faker-js/faker';


const mockData = () => {
    const delay = Math.round(Math.random() * 2000 + 1000);
    const shouldFail = fakerEN.datatype.boolean(0.2);
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            if (shouldFail) {
                reject(new Error("模拟网络错误"))
            }
            const data = {
                username: fakerEN.person.fullName(),
                nickname: fakerCN.person.fullName(),
                color: fakerCN.color.rgb(),
                email: fakerEN.internet.email(),
                text: fakerCN.lorem.paragraph(2)
            };
            resolve(data);
        }, delay);
    })

}

onMounted(() => {
    for (let i = 0; i < 10; i++) {
        mockData().then(res => {
            console.log(`接口模拟${i}: ` + JSON.stringify(res))
        })
    }
})

</script>

<template>
    <div class="text-2xl">
        看控制台
    </div>
</template>

<style lang="scss" scoped>

</style>