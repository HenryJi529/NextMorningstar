<script lang="ts" setup>
import JSZip from "jszip";
import {saveAs} from "file-saver-es";
import {ref} from "vue";
import axios from '@/axios/index';


const isProcessing = ref(false);

const saveZip = () => {
    isProcessing.value = true;
    const zip = new JSZip();
    zip.file("hello.txt", "Hello World\n");
    const folder = zip.folder("images") as JSZip;
    const requests = [];
    for (let i = 0; i < 10; i++) {
        const request = axios.get(`https://picsum.photos/200/300`, {responseType: 'blob'})
        requests.push(request);
    }
    Promise.all(requests)
        .then(responses => {
            for (let i = 0; i < responses.length; i++) {
                folder.file(`${i}.jpg`, responses[i].data, {binary: true});
            }
            zip.generateAsync({type: "blob"})
                .then((content) => {
                    isProcessing.value = false;
                    saveAs(content, "example.zip");
                });
        })
        .catch(error => {
            console.error('某个请求失败了:', error);
        });
}


</script>

<template>
    <div>
        <div v-if="!isProcessing" class="btn" @click="saveZip">
            点击下载
        </div>
        <div v-else>
            <span class="loading loading-spinner w-[10rem]"></span>
        </div>
    </div>
</template>

<style lang="scss" scoped>

</style>