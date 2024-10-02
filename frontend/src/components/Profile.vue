<script setup lang="ts">
import {useUserStore} from "@/stores/users";
import {onMounted, ref} from "vue";
import axios from "axios";
import * as qiniu from "qiniu-js";
import {storeToRefs} from "pinia";

import {
    API_COMMON_QINIU_UPLOAD,
    API_USER_RANDOM_NICKNAME, API_USER_GAME_STATS_DAILY,
    API_USER_UPDATE_AVATAR,
    API_USER_UPDATE_NICKNAME
} from "@/constants/ApiConstant";
import {TOKEN} from "@/constants/LocalStorageConstant";
import {USER_AVATAR_PREFIX} from "@/constants/MediaConstant";
import {getFileExtension} from "@/utils/handleFile";
import type {Response} from "@/types/common";
import SeasonStatsRadar from "@/components/kill/SeasonStatsRadar.vue";
import SeasonRankGrid from "@/components/kill/SeasonRankGrid.vue";
import AlertSuccess from "@/components/common/AlertSuccess.vue";
import AlertError from "@/components/common/AlertError.vue";

const userStore = useUserStore();
const {username} = storeToRefs(userStore);

const selectedAvatarFile = ref<File>(); // 用户上传的头像图片
const selectedAvatar = ref<string>(""); // 根据上传的头像图片获取的base64url
const nickname = ref<string>("");
const dailyStatsDayNum = ref<number>(10);
const successMessage = ref<string>("");
const errorMessage = ref<string>("");
const isProcessing = ref<boolean>(false);

const setRandomNickname = async () => {
    nickname.value = (await axios.get(API_USER_RANDOM_NICKNAME)).data.data;
}

const handleAvatar = (event: Event) => {
    const target = event.target as HTMLInputElement;
    if (!(target.type === 'file' && target.files && target.files.length > 0)) {
        return;
    }
    selectedAvatarFile.value = target.files[0];
    let reader = new FileReader();
    reader.readAsDataURL(selectedAvatarFile.value);
    reader.onload = (ev) => {
        if(ev.target && ev.target.result && typeof ev.target.result === 'string'){
            selectedAvatar.value = ev.target.result;
        }
    }
}

const updateAvatar = async () => {
    if(selectedAvatarFile.value === undefined){
        return;
    }
    isProcessing.value = true;
    // 获取后端提供的七牛云上传token
    const token : string = (
        await axios.get(
            API_COMMON_QINIU_UPLOAD,
            {
                headers: {
                    "authorization": localStorage.getItem(TOKEN),
                }
            }
        )
    ).data.data;
    // 设置上传任务
    const observable = qiniu.upload(
        selectedAvatarFile.value,
        `${USER_AVATAR_PREFIX}/${new Date().getTime()}.${getFileExtension(selectedAvatarFile.value)}`,
        token,
        {},
        {useCdnDomain: true}
    )
    // 开始上传
    observable.subscribe({
        error: () => {
            alert('上传图片失败');
        },
        complete: async (res) => {
            // 更新后端存储的头像文件名
            let filename = res.key.substring(USER_AVATAR_PREFIX.length + 1);
            const response: Response = (
                await axios.patch(
                    API_USER_UPDATE_AVATAR,
                    {"avatar": filename},
                    {
                        headers: {
                            "authorization": localStorage.getItem(TOKEN),
                        }
                    }
                )
            ).data;
            if(response.code != 1){
                errorMessage.value += response.msg;
            }else{
                await userStore.loadUser();
                selectedAvatar.value = "";
            }
            isProcessing.value = false;
        },
    });
}

const updateNickname = async () => {
    isProcessing.value = true;
    const response: Response = (
        await axios.patch(
            API_USER_UPDATE_NICKNAME,
            {"nickname": nickname.value},
            {
                headers: {
                    "authorization": localStorage.getItem(TOKEN),
                }
            }
        )
    ).data;
    await userStore.loadUser();
    if(response.code != 1){
        errorMessage.value += response.msg;
    }else{
    }
    isProcessing.value = false;
}

const updateSocialInfo = async () => {
    errorMessage.value = "";
    successMessage.value = "";
    if(selectedAvatar.value != ""){
        await updateAvatar();
    }
    if(nickname.value != userStore.nickname){
        await updateNickname();
    }
    if(errorMessage.value == ""){
        successMessage.value = "更新成功...";
    }
    setTimeout(()=>{
        errorMessage.value = "";
        successMessage.value = "";
    }, 1000);
}

const exportDailyStats = () => {
    axios.get(`${API_USER_GAME_STATS_DAILY}?dayNum=${dailyStatsDayNum.value}`, {
        responseType: 'blob',
        headers: {
            "authorization": localStorage.getItem(TOKEN),
        }
    }).then(res => {
        let data = res.data;
        if (!data) {
            return
        }
        let url = window.URL.createObjectURL(new Blob([data]))
        let a = document.createElement('a')
        a.style.display = 'none'
        a.href = url
        a.setAttribute('download',`${userStore.nickname}的战绩信息(近${dailyStatsDayNum.value}日).xlsx`)
        document.body.appendChild(a)
        a.click() //执行下载
        window.URL.revokeObjectURL(a.href) //释放url
        document.body.removeChild(a) //释放标签
    });


}

onMounted(()=>{
    nickname.value = userStore.nickname;
})

</script>

<template>
    <div class="p-4">
        <div class="text-center text-2xl pb-4">
            <<span class="font-bold">{{username}}</span>>的档案
        </div>
        <div class="flex justify-center py-4" v-show="errorMessage!='' || successMessage!=''">
            <div class="w-3/5 min-w-[20rem]">
                <alert-success v-if="successMessage">{{successMessage}}</alert-success>
                <alert-error v-if="errorMessage">{{errorMessage}}</alert-error>
            </div>
        </div>
        <div>
            <div class="text-xl indent-4">游戏信息</div>
            <div class="flex items-center">
                <label for="seasonStats" class="inline-block text-center w-16 mx-8 font-bold">赛季数据</label>
                <div class="flex-1 flex items-center justify-center space-x-4">
                    <season-stats-radar />
                    <season-rank-grid />
                </div>
            </div>
            <div class="flex items-center">
                <label for="dailyStats" class="inline-block text-center w-16 mx-8 font-bold">每日数据</label>
                <div class="flex-1 flex justify-center items-center space-x-6">
                    <input type="number" id="tentacles" name="tentacles" min="1" max="90" v-model="dailyStatsDayNum" class="input w-[4rem] rounded-md"/>
                    <button class="btn rounded-md" @click="exportDailyStats">
                        <font-awesome-icon :icon="['fas', 'download']" />
                    </button>
                </div>
            </div>
        </div>
        <div class="divider"></div>
        <div>
            <div class="text-xl indent-4">社交信息</div>
            <div class="flex items-center py-2">
                <label for="nickname" class="inline-block text-center w-12 mx-8 font-bold">昵称</label>
                <div class="input input-bordered flex-1 flex items-center space-x-2 mx-4">
                    <font-awesome-icon :icon="['fas', 'mask']" />
                    <input type="text" name="nickname" placeholder="请输入昵称" v-model="nickname" class="w-full">
                    <font-awesome-icon :icon="['fas', 'dice']" @click="setRandomNickname"/>
                </div>
            </div>
            <div class="flex items-center py-2">
                <label for="avatar" class="inline-block text-center w-12 mx-8 font-bold">头像</label>
                <div class="flex-1 p-2 flex items-center justify-center space-x-4 mx-4">
                    <div class="avatar">
                        <div class="rounded-xl w-24">
                            <img :src="selectedAvatar == ''? userStore.avatarLink: selectedAvatar" alt="头像" class="w-full rounded-full" />
                        </div>
                    </div>
                    <div class="w-full">
                        <input type="file" class="file-input w-full max-w-xs" name="avatar" accept="image/*" @change="handleAvatar" />
                    </div>
                </div>
            </div>
            <button class="btn w-full" @click="updateSocialInfo" :disabled="selectedAvatar == '' && nickname == userStore.nickname">
                <span class="loading loading-dots loading-sm" v-if="isProcessing"></span>
                <span v-else>更新社交信息</span>
            </button>
        </div>
        <div class="divider"></div>
        <div>
            <div class="text-xl indent-4">安全信息</div>
            <div class="indent-16">
                <div>
                    <div>换绑手机</div>
                </div>
                <div>
                    <div>换绑邮箱</div>
                </div>
                <div>
                    <div>更换密码</div>
                </div>
            </div>
        </div>
    </div>
</template>


<style scoped lang="scss">
</style>