<script lang="ts" setup>
import { useFavicon } from '@vueuse/core';
import { useHead } from '@vueuse/head';
import { computed, onMounted, ref, watch } from 'vue';
import { type AxiosProgressEvent } from 'axios';
import dayjs from 'dayjs';
import {
    BestImageLinkGenerator,
    GithubImageLinkGenerator,
    JsDelivrImageLinkGenerator,
    JSDMirrorImageLinkGenerator,
    RelayImageLinkGenerator,
} from '@/libs/pic';
import { useUserStore } from '@/stores/users';
import useClipboard from 'vue-clipboard3';
import {
    Button as AButton,
    message,
    Modal as AModal,
    Slider as ASlider,
    Switch as ASwitch,
} from 'ant-design-vue';
import {
    convertBlobToBase64,
    convertBlobToUrl,
    convertImgToBase64,
    createImageFromFile,
    isSupportWebp,
} from '@/utils/image';
import type { Image, Usage } from '@/types/pic';
import MorningstarFooter from '@/components/Footer.vue';
import mWebLogoImage from '@/assets/img/pic/mweb-logo.png';
import mWebConfigDetailImage from '@/assets/img/pic/mweb-config-detail.png';
import mWebConfigResultImage from '@/assets/img/pic/mweb-config-result.png';
import { roundToDecimal } from '@/utils/math';
import { useRouter } from 'vue-router';
import type { PageResult, R } from '@/types/common';
import { isDateStrValid } from '@/utils/date';
import * as api from '@/axios/pic';

useFavicon().value = '/pic.ico';
useHead({
    title: 'Pic - 开放图床',
    meta: [
        {
            name: 'description',
            content: '基于Github API开发的图床工具',
        },
    ],
});

const router = useRouter();
const userStore = useUserStore();
const { toClipboard } = useClipboard();
const pat = ref<string>();
const hasValidPat = ref(false);
const configModalOpen = ref(true);
const noticeModalOpen = ref(false);
const uploadModalOpen = ref(false);
const mWebModalOpen = ref(false);
const ownerName = ref<string>();
const secretKey = ref<string>();
const images = ref<Image[]>();
const usage = ref<Usage>();
const isPatSetting = ref(false);
const isPatClearing = ref(false);
const isCompressionQualitySetting = ref(false);
const isImagesFetching = ref(false);
const isImageUploading = ref(false);
const isFirstLoading = ref(false);
const deletingImages = ref<string[]>([]);
const compressionQuality = ref<number>();
const useCompress = ref<boolean>();
const pageNum = ref<number>(1);
const imageRangeCount = ref<number>();
const pageSize = ref<number>(10);
const totalPageCount = computed(() =>
    imageRangeCount.value ? Math.ceil(imageRangeCount.value / pageSize.value) : undefined
);
const dateFormat = 'YYYY-MM-DD';
const startDate = ref('2025-01-01');
const endDate = ref(dayjs().format(dateFormat));
const rawInputFile = ref<File>();
const rawInputImage = ref<HTMLImageElement>();
const compressedInputImageBase64 = ref<string>();
const compressedInputImageBlob = ref<Blob>();

const usageWarningThreshold: Usage = {
    usedStorage: 1024 * 1024 * 1024,
    imageCount: 10000,
    dayCount: 900,
};

const showConfigModal = () => {
    configModalOpen.value = true;
};

const hideConfigModal = () => {
    if (hasValidPat.value) {
        configModalOpen.value = false;
    } else {
        message.error({
            content: '你还没配置好Github PAT哦~',
            class: 'ant-message-notice-custom',
            duration: 2,
        });
    }
};

const showNoticeModal = () => {
    noticeModalOpen.value = true;
};

const hideNoticeModal = () => {
    noticeModalOpen.value = false;
};

const showMWebModal = () => {
    mWebModalOpen.value = true;
};

const hideMWebModal = () => {
    mWebModalOpen.value = false;
};

const showUploadModal = () => {
    uploadModalOpen.value = true;
};

const hideUploadModal = () => {
    uploadModalOpen.value = false;
};

const deleteImage = async (path: string) => {
    if (images.value === undefined) {
        return;
    }
    deletingImages.value.push(path);
    const response = (await api.deleteImage(path)).data;
    if (response.code <= 0) {
        message.error({ content: response.msg, class: 'ant-message-notice-custom', duration: 2 });
    } else {
        message.success({
            content: `${path}删除成功`,
            class: 'ant-message-notice-custom',
            duration: 2,
        });
        images.value = images.value.filter(image => image.path !== path);
        fetchImages(true).then(_ => {});
        getUsage().then(_ => {});
    }
    deletingImages.value = deletingImages.value.filter(item => item !== path);
};

const fetchImages = async (quiet: boolean = false) => {
    if (!quiet) {
        isImagesFetching.value = true;
    }
    const response: R<PageResult<Image>> = (
        await api.getImageList(pageNum.value, pageSize.value, startDate.value, endDate.value)
    ).data;

    if (response.code <= 0) {
        message.error({
            content: response.msg,
            class: 'ant-message-notice-custom',
            duration: 2,
        });
        return;
    }
    images.value = response.data.records;
    imageRangeCount.value = response.data.totalRecordNum;
    if (!quiet) {
        isImagesFetching.value = false;
    }
};

const getUsage = async () => {
    const isUsage = (data: unknown): data is Usage => {
        return (
            data !== null &&
            typeof data === 'object' &&
            'usedStorage' in data &&
            'imageCount' in data &&
            'dayCount' in data
        );
    };

    const data = (await api.getUsage()).data.data as unknown;
    if (isUsage(data)) {
        usage.value = data;
        if (
            usage.value.usedStorage >= usageWarningThreshold.usedStorage ||
            usage.value.imageCount >= usageWarningThreshold.imageCount ||
            usage.value.dayCount >= usageWarningThreshold.dayCount
        ) {
            message.warning({
                content: '你薅羊毛薅的有点多了呀~',
                class: 'ant-message-notice-custom',
                duration: 10,
            });
        }
    } else {
        message.error({
            content: '获取使用情况失败...',
            class: 'ant-message-notice-custom',
            duration: 2,
        });
    }
};

const getOwnerName = async () => {
    ownerName.value = (await api.getGithubAccount()).data.data as string;
};

const getSecretKey = async () => {
    secretKey.value = (await api.getSecretKey()).data.data as string;
};

const getCompressionQuality = async () => {
    const response: R<number> = (await api.getCompressionQuality()).data;
    if (response.data === undefined) {
        useCompress.value = false;
        compressionQuality.value = 0.0;
    } else {
        useCompress.value = true;
        compressionQuality.value = response.data;
    }
};

const showCompressionNotification = () => {
    message.info({
        content: '压缩可以有效缩短图片上传和加载时间，强烈建议开启...',
        class: 'ant-message-notice-custom',
        duration: 2,
    });
};

const setCompressionQuality = async () => {
    if (compressionQuality.value === undefined) {
        return;
    }
    isCompressionQualitySetting.value = true;
    const response = (
        await api.setCompressionQuality(
            useCompress.value ? compressionQuality.value.toString() : '1.0'
        )
    ).data;
    isCompressionQualitySetting.value = false;
    if (response.code <= 0) {
        message.error({ content: response.msg, class: 'ant-message-notice-custom', duration: 2 });
    } else {
        message.success({
            content: '压缩配置更新成功...',
            class: 'ant-message-notice-custom',
            duration: 2,
        });
    }
};

const githubImageLinkGenerator = new GithubImageLinkGenerator();
const jsDelivrImageLinkGenerator = new JsDelivrImageLinkGenerator();
const jSDMirrorImageLinkGenerator = new JSDMirrorImageLinkGenerator();
const relayImageLinkGenerator = new RelayImageLinkGenerator();
const bestImageLinkGenerator = new BestImageLinkGenerator();

const measureResponseTime = async (url: string) => {
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 5000); // 5秒超时

        const startTime = performance.now();
        await fetch(url, {
            method: 'HEAD',
            mode: 'no-cors', // 避免 CORS 问题
            signal: controller.signal,
        });
        const endTime = performance.now();

        clearTimeout(timeoutId);
        return endTime - startTime;
    } catch {
        return Infinity; // 出错时返回无穷大
    }
};

const updateBestImageLinkGenerator = async () => {
    if (!images.value || !(images.value.length > 0) || !ownerName.value) {
        return false;
    }
    let githubBestCount = 0;
    let jsDelivrBestCount = 0;
    let jSDMirrorBestCount = 0;
    for (const image of images.value) {
        const githubImageUrl = githubImageLinkGenerator.generate(ownerName.value, image.path);
        const jsDelivrImageUrl = jsDelivrImageLinkGenerator.generate(ownerName.value, image.path);
        const jSDMirrorImageUrl = jSDMirrorImageLinkGenerator.generate(ownerName.value, image.path);

        const githubResponseTime = await measureResponseTime(githubImageUrl);
        const jsDelivrResponseTime = await measureResponseTime(jsDelivrImageUrl);
        const jSDMirrorResponseTime = await measureResponseTime(jSDMirrorImageUrl);
        if (
            githubResponseTime < jsDelivrResponseTime &&
            githubResponseTime < jSDMirrorResponseTime
        ) {
            githubBestCount++;
        } else if (
            jsDelivrResponseTime < githubResponseTime &&
            jsDelivrResponseTime < jSDMirrorResponseTime
        ) {
            jsDelivrBestCount++;
        } else if (
            jSDMirrorResponseTime < githubResponseTime &&
            jSDMirrorResponseTime < jsDelivrResponseTime
        ) {
            jSDMirrorBestCount++;
        }
    }
    if (githubBestCount >= jsDelivrBestCount && githubBestCount >= jSDMirrorBestCount) {
        bestImageLinkGenerator.setImageLinkGenerator(githubImageLinkGenerator);
    }
    if (jsDelivrBestCount >= githubBestCount && jsDelivrBestCount >= jSDMirrorBestCount) {
        bestImageLinkGenerator.setImageLinkGenerator(jsDelivrImageLinkGenerator);
    }
    if (jSDMirrorBestCount >= githubBestCount && jSDMirrorBestCount >= jsDelivrBestCount) {
        bestImageLinkGenerator.setImageLinkGenerator(jSDMirrorImageLinkGenerator);
    }
    console.log(
        '当前最佳的ImageLinkGenerator: ' + bestImageLinkGenerator.getImageLinkGenerator().sourceName
    );
    return true;
};

const checkForBestImageLinkGeneratorUpdate = async () => {
    const isUpdated = await updateBestImageLinkGenerator();
    if (!isUpdated) {
        setTimeout(checkForBestImageLinkGeneratorUpdate, 3000);
    }
};

const copyImageLink = async (imageLink: string) => {
    await toClipboard(imageLink);
    message.success({
        content: '链接复制成功...',
        class: 'ant-message-notice-custom',
        duration: 2,
    });
};

const copyImageData = async (event: Event) => {
    const buttonElement = event.target as HTMLImageElement;
    let parentElement = buttonElement.parentElement as HTMLElement;
    while (parentElement) {
        if (parentElement.querySelector('img') !== null) {
            break;
        }
        parentElement = parentElement.parentElement as HTMLElement;
    }
    const img = parentElement.querySelector('img') as HTMLImageElement;

    // 检查图片是否已经加载完成
    if (img.complete && img.naturalHeight !== 0) {
        // 图片已加载，转换为base64并复制
        await toClipboard(convertImgToBase64(img));
        message.success({
            content: '数据复制成功...',
            class: 'ant-message-notice-custom',
            duration: 2,
        });
    } else {
        // 图片未加载，添加事件监听器
        img.addEventListener('load', async () => {
            await toClipboard(convertImgToBase64(img));
            message.success({
                content: '数据复制成功...',
                class: 'ant-message-notice-custom',
                duration: 2,
            });
        });
    }
};

const getGithubPat = async () => {
    const response: R<string> = (await api.getGithubPat()).data;
    if (response.code <= 0) {
        message.error({ content: response.msg, class: 'ant-message-notice-custom', duration: 2 });
    } else {
        pat.value = response.data;
    }
};

const setGithubPat = async () => {
    if (isPatSetting.value) {
        return;
    }
    isPatSetting.value = true;
    const response = (await api.setGithubPat(pat.value as string)).data;
    isPatSetting.value = false;
    if (response.code <= 0) {
        message.error({
            content: 'Github PAT配置错误，请重试...',
            class: 'ant-message-notice-custom',
            duration: 2,
        });
    } else {
        hasValidPat.value = true;
        message.success({
            content: 'Github PAT配置成功...',
            class: 'ant-message-notice-custom',
            duration: 2,
        });
        getOwnerName().then(_ => {});
        fetchImages().then(_ => {});
    }
};

const clearGithubPat = async () => {
    if (isPatClearing.value) {
        return;
    }
    isPatClearing.value = true;
    const response = (await api.clearGithubPat()).data;
    if (response.code <= 0) {
        message.error({
            content: 'Github PAT清除失败，请重试...',
            class: 'ant-message-notice-custom',
            duration: 2,
        });
    } else {
        pat.value = '';
        hasValidPat.value = false;
        message.success({
            content: 'Github PAT清除成功...',
            class: 'ant-message-notice-custom',
            duration: 2,
            onClose: () => {
                location.reload();
            },
        });
    }
    isPatClearing.value = false;
};

const jumpToRepo = () => {
    message.success({
        content: '即将跳转到图床仓库...',
        class: 'ant-message-notice-custom',
        duration: 1,
        onClose: () => {
            window.open(`https://github.com/${ownerName.value}/MorningstarPicRepo`, '_blank');
        },
    });
};

const jumpToBlankImageUrl = (url: string) => {
    message.success({
        content: '即将跳转指定图片链接...',
        class: 'ant-message-notice-custom',
        duration: 1,
        onClose: () => {
            window.open(url, '_blank');
        },
    });
};

const jumpToGithubPatDoc = () => {
    message.success({
        content: '即将跳转到GithubPat参考文档...',
        class: 'ant-message-notice-custom',
        duration: 1,
        onClose: () => {
            window.open(
                `https://docs.github.com/zh/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens`,
                '_blank'
            );
        },
    });
};

const logout = async () => {
    await userStore.handleLogout();
    location.reload();
};

const readImage = async (event: Event) => {
    const target = event.target as HTMLInputElement;
    if (!(target.type === 'file' && target.files && target.files.length > 0)) {
        return;
    }
    const file = target.files[0];
    rawInputFile.value = file;
    try {
        rawInputImage.value = (await createImageFromFile(file)) as HTMLImageElement;
    } catch {
        message.error({
            content: '图片读取失败，请更换图片...',
            class: 'ant-message-notice-custom',
            duration: 2,
        });
    }
    target.value = '';
};

const clearInputImage = () => {
    rawInputFile.value = undefined;
    rawInputImage.value = undefined;
    compressedInputImageBase64.value = undefined;
    compressedInputImageBlob.value = undefined;
};

const onUploadProgress = (progressEvent: AxiosProgressEvent) => {
    // NOTE: 意义不大，与Github通信的时间难以记录
    const percent = Math.round((progressEvent.loaded / (progressEvent.total as number)) * 100);
    console.log(`上传进度: ${percent}%`);
};

const uploadImage = async () => {
    if (rawInputFile.value === undefined || rawInputImage.value === undefined) {
        message.warn({
            content: '你还没选图片呢...',
            class: 'ant-message-notice-custom',
            duration: 2,
        });
        return;
    }
    let filename;
    let response: R<Image>;
    isImageUploading.value = true;
    if (useCompress.value) {
        if (compressedInputImageBlob.value === undefined) {
            return;
        }
        filename = rawInputImage.value.alt.replace(/\.\w+$/, isSupportWebp() ? '.webp' : '.jpg');
        if (compressedInputImageBlob.value.size > 100 * 1024 * 1024) {
            message.error({
                content: '图片过大，请重新选择图片...',
                class: 'ant-message-notice-custom',
                duration: 2,
            });
            return;
        }
        if (compressedInputImageBlob.value.size > 50 * 1024) {
            const file = new File([compressedInputImageBlob.value], filename, {
                type: isSupportWebp() ? 'image/webp' : 'image/jpeg',
            });
            response = (await api.uploadLargeImage(file, onUploadProgress)).data;
        } else {
            compressedInputImageBase64.value = (await convertBlobToBase64(
                compressedInputImageBlob.value
            )) as string;
            response = (await api.uploadSmallImage(filename, compressedInputImageBase64.value))
                .data;
        }
    } else {
        filename = rawInputImage.value.alt;
        if (rawInputFile.value.size > 100 * 1024 * 1024) {
            message.error({
                content: '图片过大，建议压缩...',
                class: 'ant-message-notice-custom',
                duration: 2,
            });
            return;
        }
        if (rawInputFile.value.size > 50 * 1024) {
            response = (await api.uploadLargeImage(rawInputFile.value, onUploadProgress)).data;
        } else {
            response = (
                await api.uploadSmallImage(
                    filename,
                    (await convertBlobToBase64(rawInputFile.value)) as string
                )
            ).data;
        }
    }
    isImageUploading.value = false;
    if (response.code <= 0) {
        message.error({ content: response.msg, class: 'ant-message-notice-custom', duration: 2 });
    } else {
        message.success({
            content: `${filename}上传成功`,
            class: 'ant-message-notice-custom',
            duration: 2,
        });
        getUsage().then(_ => {});
        if (images.value !== undefined && pageNum.value === 1) {
            images.value.unshift(response.data); // OR: images.value = [response.data, ...images.value];
            fetchImages(true).then(_ => {});
        }
    }
};

const jumpToPreviousPage = () => {
    if (pageNum.value > 1) {
        pageNum.value -= 1;
    }
};

const jumpToNextPage = () => {
    if (totalPageCount.value && pageNum.value < totalPageCount.value) {
        pageNum.value += 1;
    }
};

watch([startDate, endDate], () => {
    if (!isDateStrValid(startDate.value) || !isDateStrValid(endDate.value)) {
        return;
    }
    if (startDate.value > endDate.value) {
        message.error({
            content: '结束时间不能早于开始时间',
            class: 'ant-message-notice-custom',
            duration: 2,
        });
    }
    fetchImages().then(_ => {});
});

watch([pageSize, pageNum], () => {
    if (
        typeof (pageNum.value as unknown) === 'string' ||
        !Number.isInteger(pageNum.value) ||
        Number.isNaN(pageNum.value) ||
        pageNum.value < 1
    ) {
        return;
    }
    fetchImages().then(_ => {});
});

watch(rawInputImage, newValue => {
    if (newValue === null || newValue === undefined) {
        return;
    }
    if (useCompress.value === false) {
        return;
    }
    const canvas = document.createElement('canvas');
    canvas.width = newValue.naturalWidth;
    canvas.height = newValue.naturalHeight;
    const ctx = canvas.getContext('2d') as CanvasRenderingContext2D;
    const imageType = isSupportWebp() ? 'image/webp' : 'image/jpeg';
    ctx.drawImage(newValue as CanvasImageSource, 0, 0);
    canvas.toBlob(
        blob => {
            compressedInputImageBlob.value = blob as Blob;
        },
        imageType,
        compressionQuality.value
    );
});

onMounted(async () => {
    isFirstLoading.value = true;
    await getGithubPat();
    isFirstLoading.value = false;
    if (pat.value === undefined) {
        configModalOpen.value = true;
    } else {
        hasValidPat.value = true;
        getOwnerName().then(_ => {});
        getSecretKey().then(_ => {});
        getCompressionQuality().then(_ => {});
        getUsage().then(_ => {});
        fetchImages().then(_ => {});
        checkForBestImageLinkGeneratorUpdate().then(_ => {});
    }
});
</script>

<template>
    <div class="container mx-auto flex flex-col min-h-dvh">
        <div class="md:flex items-center mt-4 justify-between mb-4 md:mb-8 px-8">
            <div class="text-2xl md:text-5xl font-bold text-center">开 放 图 床</div>
            <div v-if="usage" class="hidden xl:flex justify-center items-center space-x-8 text-lg">
                <div>
                    已使用天数:
                    <span
                        :class="{
                            'text-red-600': usage.dayCount >= usageWarningThreshold.dayCount,
                            'dark:text-red-400': usage.dayCount >= usageWarningThreshold.dayCount,
                        }">
                        {{ usage.dayCount }} </span
                    >/1000
                </div>
                <div
                    :class="{
                        'text-red-600': usage.usedStorage >= usageWarningThreshold.usedStorage,
                        'dark:text-red-400': usage.usedStorage >= usageWarningThreshold.usedStorage,
                    }">
                    已使用空间:
                    <span>
                        {{ roundToDecimal(usage.usedStorage / 1024 / 1024, 2) }}
                    </span>
                    MB
                </div>
                <div>
                    当前图片数:
                    <span
                        :class="{
                            'text-red-600': usage.imageCount >= usageWarningThreshold.imageCount,
                            'dark:text-red-400':
                                usage.imageCount >= usageWarningThreshold.imageCount,
                        }">
                        {{ usage.imageCount }}
                    </span>
                </div>
            </div>
            <div class="flex justify-center items-center space-x-4">
                <font-awesome-icon
                    v-if="ownerName"
                    :icon="['fas', 'upload']"
                    class="text-2xl cursor-pointer"
                    @click="showUploadModal" />
                <a-modal v-model:open="uploadModalOpen" :closable="false" @ok="hideUploadModal">
                    <template #title>
                        <div class="text-2xl text-center">图片上传</div>
                    </template>
                    <template #footer>
                        <a-button key="submit" type="primary" @click="hideUploadModal"
                            >关闭</a-button
                        >
                    </template>
                    <input
                        id="file-upload"
                        accept="image/*"
                        class="hidden"
                        type="file"
                        @change="readImage" />
                    <div class="mb-2">
                        <div
                            v-if="useCompress ? compressedInputImageBlob : rawInputImage"
                            class="relative">
                            <font-awesome-icon
                                :icon="['fas', 'circle-xmark']"
                                class="absolute top-2 right-2 cursor-pointer text-2xl"
                                @click="clearInputImage" />
                            <img
                                :alt="useCompress ? '压缩预览图' : '原始预览图'"
                                :src="
                                    useCompress
                                        ? compressedInputImageBlob &&
                                          convertBlobToUrl(compressedInputImageBlob)
                                        : rawInputImage && convertImgToBase64(rawInputImage)
                                "
                                class="h-[10rem] rounded-2xl mx-auto" />
                        </div>
                        <label
                            v-if="useCompress ? !compressedInputImageBlob : !rawInputImage"
                            class="cursor-pointer flex justify-center items-center border-dashed border-4 rounded-2xl w-[10rem] h-[10rem] mx-auto"
                            for="file-upload">
                            <div>
                                <font-awesome-icon :icon="['fas', 'plus']" class="text-5xl" />
                                <div class="text-center text-xl mt-2">选择</div>
                            </div>
                        </label>
                    </div>
                    <div class="flex justify-center items-center space-x-8">
                        <button
                            class="px-4 py-1 rounded-lg bg-red-400 dark:bg-red-800"
                            @click="clearInputImage">
                            重置
                        </button>
                        <button
                            class="px-4 py-1 rounded-lg bg-green-400 dark:bg-green-700 flex justify-center items-center"
                            @click="!isImageUploading && uploadImage()">
                            <span v-if="isImageUploading" class="loading loading-spinner"></span>
                            <span v-else>上传</span>
                        </button>
                    </div>
                </a-modal>
                <div
                    v-if="ownerName"
                    class="flex justify-center items-center cursor-pointer hover:animate-pulse"
                    @click="jumpToRepo">
                    <font-awesome-icon :icon="['fab', 'github']" class="text-2xl" />
                </div>
                <img
                    :src="mWebLogoImage"
                    alt="MWeb Logo"
                    class="w-8 cursor-pointer"
                    @click="showMWebModal" />
                <a-modal
                    v-model:open="mWebModalOpen"
                    :centered="true"
                    :closable="false"
                    :width="540"
                    @ok="hideMWebModal">
                    <template #title>
                        <div class="text-2xl text-center">MWeb图床配置</div>
                    </template>
                    <template #footer>
                        <a-button key="submit" type="primary" @click="hideMWebModal">了解</a-button>
                    </template>
                    <p class="indent-8">
                        请先参考官方文档<a
                            class="text-green-600 dark:text-green-400"
                            href="https://zh.mweb.im/how_to_use_custom_image_upload_url"
                            target="_blank"
                            >《iOS版MWeb图床功能中自定义图床的使用指南》</a
                        >， 本服务的具体配置如下：
                    </p>
                    <div class="flex justify-center items-center">
                        <img
                            :src="mWebConfigDetailImage"
                            alt="MWeb图床配置细节"
                            class="w-[26rem] rounded-3xl" />
                    </div>
                    <ul class="list-disc pl-12 mb-2">
                        <li>
                            <span class="font-bold mr-2">API URL:</span
                            >https://morningstar369.com/api/pic/resource/mweb
                        </li>
                        <li><span class="font-bold mr-2">POST File Name:</span>file</li>
                        <li><span class="font-bold mr-2">Response URL Path:</span>data/url</li>
                        <li><span class="font-bold mr-2">secretKey:</span>{{ secretKey }}</li>
                        <li><span class="font-bold mr-2">compressionQuality:</span>0.8【举🌰】</li>
                        <li>
                            <span class="font-bold mr-2">linkType:</span>Github / JSDMirror /
                            JsDelivr(默认) / Relay【区分大小写】
                        </li>
                    </ul>
                    <p class="indent-8">
                        配置完成后，点击"Validate"按钮。验证通过时，会显示类似下图的结果。
                    </p>
                    <div class="flex justify-center items-center">
                        <img
                            :src="mWebConfigResultImage"
                            alt="MWeb图床配置结果"
                            class="w-[10rem]" />
                    </div>
                </a-modal>
                <font-awesome-icon
                    :icon="['fas', 'bell']"
                    class="text-2xl cursor-pointer"
                    @click="showNoticeModal" />
                <a-modal v-model:open="noticeModalOpen" :closable="false" @ok="hideNoticeModal">
                    <template #title>
                        <div class="text-2xl text-center">本站通告</div>
                    </template>
                    <template #footer>
                        <a-button key="submit" type="primary" @click="hideNoticeModal"
                            >了解</a-button
                        >
                    </template>
                    <ul class="list-disc">
                        <li>
                            <span class="font-bold text-red-500 dark:text-red-400">操作警告：</span>
                            <ul class="ml-5 list-decimal">
                                <li>
                                    本系统所有删除操作均为直接执行模式，无二次确认机制，请务必谨慎操作以避免数据丢失。
                                </li>
                                <li>
                                    不建议直接在关联的Git仓库中手动删除由本系统上传的图片，因为这样会干扰缓存机制的正常运作。
                                </li>
                            </ul>
                        </li>
                        <li>
                            <span class="font-bold">性质说明：</span
                            >本服务基于GitHub搭建，旨在为个人开发者、学生等预算有限的用户提供简单的图片托管支持。
                        </li>
                        <li>
                            <span class="font-bold">隐私保障：</span
                            >您的图片数据将直接存储于GitHub，我们仅保存必要的PAT授权信息用于服务对接，不会在服务器留存您的图片。
                        </li>
                        <li>
                            <span class="font-bold">资源配额：</span
                            >为了保证Github资源的合理使用，每个Github账号只享有1000天的新增权限，超过配额后仍可管理历史图片资源。
                        </li>
                        <li>
                            <span class="font-bold">替代方案：</span
                            >如果需要更稳定的图床，可参考以下免费/低成本服务：<a
                                class="text-green-600 dark:text-green-400"
                                href="https://imgur.com/"
                                target="_blank"
                                >Imgur</a
                            >、<a
                                class="text-green-600 dark:text-green-400"
                                href="https://www.qiniu.com/products/kodo"
                                target="_blank"
                                >七牛云</a
                            >。
                        </li>
                    </ul>
                </a-modal>
                <font-awesome-icon
                    :icon="['fas', 'gear']"
                    class="text-2xl cursor-pointer"
                    @click="showConfigModal" />
                <a-modal
                    v-model:open="configModalOpen"
                    :closable="false"
                    :keyboard="false"
                    :maskClosable="false"
                    @ok="hideConfigModal">
                    <template #title>
                        <div class="text-2xl text-center">配置菜单</div>
                    </template>
                    <template #footer>
                        <a-button key="submit" type="primary" @click="hideConfigModal"
                            >关闭</a-button
                        >
                    </template>
                    <div
                        v-if="isFirstLoading"
                        class="absolute top-0 left-0 w-full h-full flex justify-center items-center bg-gray-700/20 dark:bg-gray-100/20">
                        <span class="loading loading-bars w-[3rem]"></span>
                    </div>
                    <div>
                        <div class="text-lg mb-2">
                            Github PAT
                            <font-awesome-icon
                                :icon="['fas', 'circle-question']"
                                class="cursor-pointer"
                                @click="jumpToGithubPatDoc" />
                        </div>
                        <input
                            v-model="pat"
                            class="w-full p-1 rounded-xl pl-4 bg-transparent mb-2"
                            placeholder="请输入您的Github PAT..."
                            type="text" />
                        <div class="flex justify-center items-center space-x-8">
                            <button
                                class="px-4 py-1 rounded-lg bg-green-400 dark:bg-green-700 flex justify-center items-center"
                                @click="!isPatSetting && setGithubPat()">
                                <span v-if="isPatSetting" class="loading loading-spinner"></span>
                                <span v-else>设置</span>
                            </button>
                            <button
                                class="px-4 py-1 rounded-lg bg-red-400 dark:bg-red-800 flex justify-center items-center"
                                @click="!isPatClearing && clearGithubPat()">
                                <span v-if="isPatClearing" class="loading loading-spinner"></span>
                                <span v-else>清除</span>
                            </button>
                        </div>
                    </div>
                    <div class="divider"></div>
                    <div>
                        <div class="text-lg mb-2">
                            压缩配置
                            <font-awesome-icon
                                :icon="['fas', 'circle-question']"
                                class="cursor-pointer"
                                @click="showCompressionNotification" />
                        </div>
                        <div class="flex justify-center items-center space-x-8 px-4 mb-2">
                            <a-switch
                                v-model:checked="useCompress"
                                checked-children="开"
                                un-checked-children="关" />
                            <div class="flex-1 flex justify-center items-center">
                                <span class="mr-4">质量: </span>
                                <a-slider
                                    v-model:value="compressionQuality"
                                    :disabled="!useCompress"
                                    :max="0.9"
                                    :min="0.0"
                                    :step="0.01"
                                    class="flex-1" />
                            </div>
                        </div>
                        <div class="flex justify-center items-center">
                            <button
                                class="px-16 py-1 rounded-lg bg-green-400 dark:bg-green-700 flex justify-center items-center"
                                @click="!isCompressionQualitySetting && setCompressionQuality()">
                                <span
                                    v-if="isCompressionQualitySetting"
                                    class="loading loading-spinner"></span>
                                <span v-else>设置</span>
                            </button>
                        </div>
                    </div>
                </a-modal>
                <div title="用户">
                    <div v-if="userStore.id" class="flex justify-center items-center">
                        <details class="dropdown dropdown-bottom dropdown-end">
                            <summary class="list-none w-10 h-10 cursor-pointer">
                                <img
                                    :src="userStore.avatarLink"
                                    alt="头像"
                                    class="rounded-full w-full h-full" />
                            </summary>
                            <ul
                                class="dropdown-content mt-1 rounded-box shadow flex flex-col text-lg">
                                <li v-if="userStore.isAuthenticated" class="nav-item">
                                    <div
                                        class="cursor-pointer w-full text-center"
                                        @click="router.push({ name: 'auth-profile' })">
                                        档案
                                    </div>
                                </li>
                                <li class="nav-item">
                                    <div class="cursor-pointer w-full text-center" @click="logout">
                                        登出
                                    </div>
                                </li>
                            </ul>
                        </details>
                    </div>
                    <div v-else>
                        <font-awesome-icon
                            :icon="['fas', 'user']"
                            class="cursor-pointer text-2xl" />
                    </div>
                </div>
            </div>
        </div>
        <div class="flex justify-center items-center mb-2 flex-wrap space-x-8 px-2">
            <div class="flex justify-center items-center space-x-4">
                <div class="flex flex-wrap items-center">
                    <span class="mr-2">开始日期: </span>
                    <input
                        v-model="startDate"
                        class="bg-transparent w-[9rem] p-1 border-2 rounded-lg"
                        type="date" />
                </div>
                <div class="flex flex-wrap items-center">
                    <span class="mr-2">结束日期: </span>
                    <input
                        v-model="endDate"
                        class="bg-transparent w-[9rem] p-1 border-2 rounded-lg"
                        type="date" />
                </div>
            </div>
            <div v-if="imageRangeCount" class="flex justify-center items-center space-x-4">
                <div>
                    <font-awesome-icon
                        :class="{ 'text-gray-400': pageNum < 2, 'dark:text-gray-900': pageNum < 2 }"
                        :icon="['fas', 'angle-left']"
                        class="cursor-pointer"
                        @click="jumpToPreviousPage" />
                    <input
                        type="number"
                        min="1"
                        :max="totalPageCount"
                        v-model.lazy.trim.number="pageNum"
                        class="w-[3rem] p-1 my-1 bg-transparent border-2 rounded-lg" />
                    <span class="px-1"> / </span>
                    <span>{{ totalPageCount }}</span>
                    <font-awesome-icon
                        :class="{
                            'text-gray-400': pageNum === totalPageCount,
                            'dark:text-gray-900': pageNum === totalPageCount,
                        }"
                        :icon="['fas', 'angle-right']"
                        class="cursor-pointer"
                        @click="jumpToNextPage" />
                </div>
                <select v-model="pageSize" class="border-2 rounded-lg p-1 my-1 bg-transparent">
                    <option selected value="10">10 / 页</option>
                    <option value="20">20 / 页</option>
                    <option value="30">30 / 页</option>
                </select>
            </div>
        </div>
        <div class="flex-1 border-2 rounded-2xl p-4 mb-4 flex flex-col mx-2">
            <div
                v-if="ownerName && images && !isImagesFetching"
                class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 p-4 gap-y-8">
                <template v-for="image in images" :key="image.path">
                    <div class="flex justify-center items-center">
                        <div class="rounded-2xl item-shadow">
                            <div
                                :data-url="`${bestImageLinkGenerator.generate(ownerName, image.path)}`"
                                class="relative">
                                <div class="absolute top-2 right-2 z-10">
                                    <font-awesome-icon
                                        v-if="!deletingImages.includes(image.path)"
                                        :icon="['fas', 'trash']"
                                        class="cursor-pointer text-xl text-blue-400 hover:text-red-500"
                                        @click="deleteImage(image.path)" />
                                    <span
                                        v-else
                                        class="loading loading-spinner loading-md text-gray-700"></span>
                                </div>
                                <div class="w-[16rem] h-[12rem]">
                                    <img
                                        :alt="image.path.split('/')[1]"
                                        v-lazy="{
                                            src: bestImageLinkGenerator.generate(
                                                ownerName,
                                                image.path
                                            ),
                                            loading:
                                                'https://devtool.tech/api/placeholder/256/192?text=加载中...&color=#555555&bgColor=#EFEFEF',
                                            error: 'https://devtool.tech/api/placeholder/256/192?text=加载失败&color=#555555&bgColor=#EFEFEF',
                                            delay: 50,
                                            lifecycle: {},
                                        }"
                                        class="w-full h-full object-cover mb-1 mx-auto rounded-2xl cursor-pointer"
                                        crossorigin="anonymous"
                                        @click="
                                            jumpToBlankImageUrl(
                                                bestImageLinkGenerator.generate(
                                                    ownerName,
                                                    image.path
                                                )
                                            )
                                        " />
                                </div>
                            </div>
                            <div
                                class="overflow-x-scroll whitespace-nowrap w-[16rem] pb-1 text-center px-2 text-blue-400">
                                {{ image.filename.slice(10) }}
                            </div>
                            <div class="text-center mb-1 px-2">
                                {{ image.updateTime }}
                            </div>
                            <div class="flex justify-center items-center space-x-4 mb-2">
                                <details class="dropdown">
                                    <summary class="btn btn-outline btn-primary btn-sm">
                                        复制链接
                                    </summary>
                                    <ul
                                        class="menu dropdown-content p-0 bg-base-100 rounded-box z-[1] w-[5.2rem] shadow mt-1">
                                        <template
                                            v-for="imageLinkGenerator in [
                                                githubImageLinkGenerator,
                                                jsDelivrImageLinkGenerator,
                                                jSDMirrorImageLinkGenerator,
                                                relayImageLinkGenerator,
                                            ]"
                                            :key="imageLinkGenerator.sourceName">
                                            <li
                                                class="py-1 text-sm text-center hover:bg-gray-300 dark:hover:bg-gray-700 rounded-lg w-full cursor-pointer"
                                                @click="
                                                    copyImageLink(
                                                        imageLinkGenerator.generate(
                                                            ownerName,
                                                            image.path
                                                        )
                                                    )
                                                ">
                                                {{ imageLinkGenerator.sourceName }}
                                            </li>
                                        </template>
                                    </ul>
                                </details>
                                <button
                                    class="btn btn-outline btn-accent btn-sm"
                                    @click="copyImageData">
                                    复制内容
                                </button>
                            </div>
                        </div>
                    </div>
                </template>
            </div>
            <div v-else class="flex justify-center items-center h-full flex-1">
                <span class="loading loading-ring w-24 h-24"></span>
            </div>
        </div>
        <morningstar-footer />
    </div>
</template>

<style lang="scss" scoped>
.item-shadow {
    &:hover {
        box-shadow:
            10px 10px 20px #bebebe,
            -10px -10px 20px #ffffff;
    }
}

[data-theme='dark'] .item-shadow {
    &:hover {
        box-shadow:
            10px 10px 20px #191e24,
            -10px -10px 20px #212830;
    }
}

.nav-item {
    @apply hover:bg-gray-300 dark:hover:bg-gray-700 rounded-sm lg:rounded-xl w-[3.5rem] p-1;
}
</style>
