<script lang="ts" setup>
import {useUserStore} from "@/stores/users";
import {onMounted, reactive, ref} from "vue";
import axios from '@/axios/index';
import {upload as qiniuUpload} from "qiniu-js";

import {
    API_COMMON_QINIU_UPLOAD,
    API_KILL_STATS_DAILY,
    API_USER_INFO_AVATAR,
    API_USER_INFO_EMAIL,
    API_USER_INFO_NICKNAME,
    API_USER_INFO_PASSWORD,
    API_USER_RANDOM_NICKNAME
} from "@/constants/ApiConstant";
import {USER_AVATAR_PREFIX} from "@/constants/MediaConstant";
import {getFileExtension} from "@/utils/handleFile";
import type {R} from "@/types/common";
import SeasonStatsRadar from "@/components/kill/SeasonStatsRadar.vue";
import SeasonRankGrid from "@/components/kill/SeasonRankGrid.vue";
import AlertSuccess from "@/components/common/AlertSuccess.vue";
import AlertError from "@/components/common/AlertError.vue";
import type {UpdateEmailRequestVo, UpdatePasswordRequestVo, UserEditableInfo} from "@/types/auth";
import EmailCaptchaField from "@/components/common/EmailCaptchaField.vue";

const userStore = useUserStore();

const selectedAvatarFile = ref<File>(); // 用户上传的头像图片
const selectedAvatar = ref<string>(""); // 根据上传的头像图片获取的base64url
const nickname = ref<string>("");
const dailyStatsDayNum = ref<number>(10);
const updatePasswordRequestVo = reactive<UpdatePasswordRequestVo>({
    newPassword: "",
    confirmPassword: ""
})
const updateEmailRequestVo = reactive<UpdateEmailRequestVo>({
    email: userStore.email,
    code: ""
})

const successMessage = ref<string>("");
const errorMessage = ref<string>("");
const isProcessing = ref<boolean>(false);
const hiddenNewPassword = ref(true);
const hiddenConfirmPassword = ref(true);

const setRandomNickname = async () => {
    nickname.value = (await axios.get(API_USER_RANDOM_NICKNAME)).data.data;
}

const readAvatar = (event: Event) => {
    const target = event.target as HTMLInputElement;
    if (!(target.type === 'file' && target.files && target.files.length > 0)) {
        return;
    }
    selectedAvatarFile.value = target.files[0];
    let reader = new FileReader();
    reader.readAsDataURL(selectedAvatarFile.value);
    reader.onload = (ev) => {
        if (ev.target && ev.target.result && typeof ev.target.result === 'string') {
            selectedAvatar.value = ev.target.result;
        }
    }
}

const exportDailyStats = () => {
    axios.get(`${API_KILL_STATS_DAILY}?dayNum=${dailyStatsDayNum.value}`, {
        responseType: 'blob'
    }).then(res => {
        let data = res.data;
        if (!data) {
            return
        }
        let url = URL.createObjectURL(data)
        let a = document.createElement('a')
        a.style.display = 'none'
        a.href = url
        a.download = `${userStore.nickname}的战绩信息(近${dailyStatsDayNum.value}日).xlsx`; // a.setAttribute('download',`${userStore.nickname}的战绩信息(近${dailyStatsDayNum.value}日).xlsx`)
        document.body.appendChild(a)
        a.click() //执行下载
        setTimeout(() => {
            URL.revokeObjectURL(a.href); // 释放url
        }, 100)
        document.body.removeChild(a) //释放标签
    });
}

const updateAvatar = async () => {
    if (selectedAvatarFile.value === undefined) {
        return;
    }
    // 获取后端提供的七牛云上传token
    const token: string = (
        await axios.get(
            API_COMMON_QINIU_UPLOAD
        )
    ).data.data;
    // 设置上传任务
    const observable = qiniuUpload(
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
            const response: R<UserEditableInfo> = (
                await axios.patch(
                    API_USER_INFO_AVATAR,
                    {"avatar": filename}
                )
            ).data;
            if (response.code <= 0) {
                errorMessage.value += response.msg;
            } else {
                await userStore.loadUser();
                selectedAvatar.value = "";
            }
        },
    });
}

const updateNickname = async () => {
    const response: R<UserEditableInfo> = (
        await axios.patch(
            API_USER_INFO_NICKNAME,
            {"nickname": nickname.value}
        )
    ).data;
    if (response.code <= 0) {
        errorMessage.value += response.msg;
    } else {
        await userStore.loadUser();
        nickname.value = userStore.nickname;
    }
}

const updatePassword = async () => {
    const response: R<UserEditableInfo> = (
        await axios.patch(
            API_USER_INFO_PASSWORD,
            updatePasswordRequestVo
        )
    ).data;
    if (response.code <= 0) {
        errorMessage.value += response.msg;
    } else {
        updatePasswordRequestVo.newPassword = "";
        updatePasswordRequestVo.confirmPassword = "";
    }
}

const updateEmail = async () => {
    const response: R<UserEditableInfo> = (
        await axios.patch(
            API_USER_INFO_EMAIL,
            updateEmailRequestVo
        )
    ).data;
    if (response.code <= 0) {
        errorMessage.value += response.msg;
    } else {
        await userStore.loadUser();
        updateEmailRequestVo.email = userStore.email;
        updateEmailRequestVo.code = "";
    }
}

const clearMessage = () => {
    errorMessage.value = "";
    successMessage.value = "";
}

const updateSocialInfo = async () => {
    isProcessing.value = true;
    clearMessage();
    if (selectedAvatar.value != "") {
        await updateAvatar();
    }
    if (nickname.value != userStore.nickname) {
        await updateNickname();
    }
    if (errorMessage.value == "") {
        successMessage.value = "更新成功...";
    }
    isProcessing.value = false;
    setTimeout(clearMessage, 3000);
}

const updateSecurityInfo = async () => {
    isProcessing.value = true;
    clearMessage();
    if (updatePasswordRequestVo.newPassword != "" && updatePasswordRequestVo.confirmPassword != "") {
        await updatePassword();
    }
    if (updateEmailRequestVo.email != userStore.email && updateEmailRequestVo.code != "") {
        await updateEmail();
    }
    if (errorMessage.value == "") {
        successMessage.value = "更新成功...";
    }
    isProcessing.value = false;
    setTimeout(clearMessage, 3000);
}

const handleSendEmailError = (message: string) => {
    errorMessage.value = message;
}

onMounted(() => {
    nickname.value = userStore.nickname;
})

</script>

<template>
    <div class="p-4 w-full max-w-[50rem] mx-auto">
        <div class="text-center text-2xl pb-4">
            &gt;<span class="font-bold">{{ userStore.username }}</span>&lt;的档案
        </div>
        <div v-show="errorMessage!='' || successMessage!=''" class="flex justify-center py-4">
            <div class="w-3/5 min-w-[20rem]">
                <alert-success v-if="successMessage">{{ successMessage }}</alert-success>
                <alert-error v-if="errorMessage">{{ errorMessage }}</alert-error>
            </div>
        </div>
        <div>
            <div class="text-xl indent-4">游戏信息</div>
            <div class="overflow-scroll">
                <div class="flex items-center">
                    <label class="field-label">赛季数据</label>
                    <div class="flex-1 flex items-center justify-center space-x-4">
                        <season-stats-radar/>
                        <season-rank-grid/>
                    </div>
                </div>
                <div class="flex items-center">
                    <label class="field-label">每日数据</label>
                    <div class="flex-1 flex justify-center items-center space-x-6">
                        <span>近</span>
                        <input id="tentacles" v-model="dailyStatsDayNum" class="input w-[4rem] rounded-md" max="90"
                               min="1" name="tentacles"
                               type="number"/>
                        <span>日</span>
                        <button class="btn rounded-md" @click="exportDailyStats">
                            <font-awesome-icon :icon="['fas', 'download']"/>
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <div class="divider"></div>
        <div>
            <div class="text-xl indent-4">社交信息</div>
            <div class="overflow-scroll">
                <div class="flex items-center py-2">
                    <label class="field-label" for="nickname">昵称</label>
                    <div class="input input-bordered flex-1 flex items-center space-x-2 mx-4">
                        <font-awesome-icon :icon="['fas', 'mask']"/>
                        <input id="nickname" v-model="nickname" class="w-full" name="nickname" placeholder="请输入昵称"
                               type="text">
                        <font-awesome-icon :icon="['fas', 'dice']" @click="setRandomNickname"/>
                    </div>
                </div>
                <div class="flex items-center py-2">
                    <label class="field-label" for="avatar">头像</label>
                    <div class="flex-1 p-2 flex items-center justify-center space-x-4 mx-4">
                        <div class="avatar">
                            <div class="rounded-xl w-24">
                                <img :src="selectedAvatar == ''? userStore.avatarLink: selectedAvatar" alt="头像"
                                     class="w-full rounded-full"/>
                            </div>
                        </div>
                        <div class="flex-1">
                            <input id="avatar" accept="image/*" class="file-input" name="avatar" type="file"
                                   @change="readAvatar"/>
                        </div>
                    </div>
                </div>
            </div>
            <button :disabled="selectedAvatar == '' && nickname == userStore.nickname" class="btn w-full"
                    @click="updateSocialInfo">
                <span v-if="isProcessing" class="loading loading-dots loading-sm"></span>
                <span v-else>更新社交信息</span>
            </button>
        </div>
        <div class="divider"></div>
        <div>
            <div class="text-xl indent-4">安全信息</div>
            <div class="overflow-scroll">
                <div class="flex items-center py-2">
                    <label class="field-label" for="password">
                        更换密码
                    </label>
                    <div class="flex-1 flex">
                        <div class="w-1/2 px-4">
                            <div class="input input-bordered flex items-center space-x-2">
                                <font-awesome-icon :icon="['fas', 'key']"/>
                                <input id="password" v-model="updatePasswordRequestVo.newPassword"
                                       :type="hiddenNewPassword ?'password': 'text'"
                                       class="flex-1" name="password"
                                       placeholder="请输入密码">
                                <font-awesome-icon :icon="hiddenNewPassword ? ['fas', 'eye-slash']: ['fas', 'eye']"
                                                   class="cursor-pointer"
                                                   @click="hiddenNewPassword = !hiddenNewPassword"/>
                            </div>
                        </div>
                        <div class="w-1/2 px-4">
                            <div class="input input-bordered flex items-center space-x-2">
                                <font-awesome-icon :icon="['fas', 'key']"/>
                                <input v-model="updatePasswordRequestVo.confirmPassword"
                                       :type="hiddenConfirmPassword? 'password': 'text'"
                                       class="flex-1" name="confirmPassword"
                                       placeholder="请确认密码">
                                <font-awesome-icon :icon="hiddenConfirmPassword ? ['fas', 'eye-slash']: ['fas', 'eye']"
                                                   class="cursor-pointer"
                                                   @click="hiddenConfirmPassword = !hiddenConfirmPassword"/>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="flex items-center py-2">
                    <label class="field-label" for="email">
                        换绑邮箱
                    </label>
                    <div class="flex-1 flex">
                        <div class="w-1/2 px-4">
                            <div class="input input-bordered flex items-center space-x-2">
                                <font-awesome-icon :icon="['fas', 'envelope']"/>
                                <input id="email" v-model="updateEmailRequestVo.email" class="w-full" name="email"
                                       placeholder="请输入邮箱" type="text">
                            </div>
                        </div>
                        <div class="w-1/2 px-4">
                            <email-captcha-field :data="updateEmailRequestVo" class="w-full"
                                                 @error="handleSendEmailError"/>
                        </div>
                    </div>
                </div>
            </div>
            <button
                :disabled="(updatePasswordRequestVo.newPassword == '' || updatePasswordRequestVo.confirmPassword == '') && (updateEmailRequestVo.email == userStore.email || updateEmailRequestVo.code == '')"
                class="btn w-full"
                @click="updateSecurityInfo">
                <span v-if="isProcessing" class="loading loading-dots loading-sm"></span>
                <span v-else>更新安全信息</span>
            </button>
        </div>
    </div>
</template>


<style lang="scss" scoped>
.field-label {
    @apply inline-block text-center mx-8 font-bold text-lg whitespace-nowrap;
}
</style>