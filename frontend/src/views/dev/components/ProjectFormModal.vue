<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Modal as AModal, message } from 'ant-design-vue';
import { createProject, updateProject } from '@/axios/dev';
import { ResponseCode } from '@/constants/response';
import type { ProjectDetail } from '@/types/dev';

const props = defineProps<{
    open: boolean;
    mode: 'create' | 'edit';
    project?: ProjectDetail;
    username: string;
}>();
const emit = defineEmits<{
    'update:open': [value: boolean];
    submitted: [project: ProjectDetail];
}>();

const form = reactive({
    name: '',
    link: '',
    branchName: 'main',
    description: '',
    maxSonarIssuesPerRun: 10,
    maxAiIssuesPerRun: 5,
});
const submitting = ref(false);

watch(
    () => props.open,
    open => {
        if (!open) {
            return;
        }
        if (props.mode === 'edit' && props.project) {
            form.name = props.project.name;
            form.link = props.project.link;
            form.branchName = props.project.branchName;
            form.description = props.project.description ?? '';
            form.maxSonarIssuesPerRun = props.project.maxSonarIssuesPerRun;
            form.maxAiIssuesPerRun = props.project.maxAiIssuesPerRun;
        } else {
            form.name = '';
            form.link = '';
            form.branchName = 'main';
            form.description = '';
            form.maxSonarIssuesPerRun = 10;
            form.maxAiIssuesPerRun = 5;
        }
    }
);

const close = () => emit('update:open', false);

const submit = async () => {
    if (
        !form.name.trim() ||
        !form.branchName.trim() ||
        (props.mode === 'create' && !form.link.trim())
    ) {
        message.error('项目名称、仓库链接、分支为必填项');
        return;
    }
    if (
        !Number.isInteger(form.maxSonarIssuesPerRun) ||
        form.maxSonarIssuesPerRun < 1 ||
        !Number.isInteger(form.maxAiIssuesPerRun) ||
        form.maxAiIssuesPerRun < 1
    ) {
        message.error('Sonar/AI 单轮处理上限必须为正整数');
        return;
    }
    submitting.value = true;
    try {
        const response =
            props.mode === 'create'
                ? await createProject({
                      name: form.name.trim(),
                      link: form.link.trim(),
                      branchName: form.branchName.trim(),
                      description: form.description.trim() || undefined,
                      maxSonarIssuesPerRun: form.maxSonarIssuesPerRun,
                      maxAiIssuesPerRun: form.maxAiIssuesPerRun,
                  })
                : await updateProject(props.project!.id, {
                      name: form.name.trim(),
                      branchName: form.branchName.trim(),
                      description: form.description.trim() || undefined,
                      maxSonarIssuesPerRun: form.maxSonarIssuesPerRun,
                      maxAiIssuesPerRun: form.maxAiIssuesPerRun,
                  });
        if (response.data.code !== ResponseCode.SUCCESS) {
            message.error(response.data.msg);
            return;
        }
        message.success(props.mode === 'create' ? '项目接入成功' : '配置已保存');
        emit('submitted', response.data.data);
        close();
    } finally {
        submitting.value = false;
    }
};

const inputClass =
    'w-full px-3 py-2 rounded-lg border border-slate-200 text-sm outline-none focus:border-orange-400 focus:ring-2 focus:ring-orange-100 transition';
</script>

<template>
    <a-modal :open="open" :width="480" :footer="null" :closable="false" @cancel="close">
        <div class="flex items-center justify-between pb-4 border-b border-slate-100">
            <h2 class="text-base font-semibold text-slate-800">
                {{ mode === 'create' ? '接入新项目' : '编辑配置' }}
            </h2>
            <button
                class="w-7 h-7 rounded-md text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition"
                @click="close">
                ✕
            </button>
        </div>
        <div class="py-5 space-y-4">
            <div>
                <label class="block text-xs text-slate-500 mb-1.5">
                    项目名称 <span class="text-rose-400">*</span>
                </label>
                <input v-model="form.name" placeholder="如 my-service" :class="inputClass" />
            </div>
            <div>
                <label class="block text-xs text-slate-500 mb-1.5">
                    仓库链接 <span class="text-rose-400">*</span>
                </label>
                <input
                    v-model="form.link"
                    :disabled="mode === 'edit'"
                    placeholder="http://gitea.local/owner/repo"
                    :class="[
                        inputClass,
                        'font-mono',
                        mode === 'edit' ? 'bg-slate-50 text-slate-400' : '',
                    ]" />
                <p v-if="mode === 'create'" class="mt-1 text-[11px] text-slate-400">
                    提交时自动校验仓库存在，并把 bot 加为协作者
                </p>
            </div>
            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label class="block text-xs text-slate-500 mb-1.5">
                        分支 <span class="text-rose-400">*</span>
                    </label>
                    <input
                        v-model="form.branchName"
                        placeholder="main"
                        :class="[inputClass, 'font-mono']" />
                </div>
                <div>
                    <label class="block text-xs text-slate-500 mb-1.5">管理员</label>
                    <input
                        :value="`${username}(当前用户)`"
                        disabled
                        :class="[inputClass, 'bg-slate-50 text-slate-400 border-slate-100']" />
                </div>
            </div>
            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label class="block text-xs text-slate-500 mb-1.5">Sonar 上限/轮</label>
                    <input
                        v-model.number="form.maxSonarIssuesPerRun"
                        type="number"
                        min="1"
                        step="1"
                        :class="[inputClass, 'font-mono']" />
                </div>
                <div>
                    <label class="block text-xs text-slate-500 mb-1.5">AI 上限/轮</label>
                    <input
                        v-model.number="form.maxAiIssuesPerRun"
                        type="number"
                        min="1"
                        step="1"
                        :class="[inputClass, 'font-mono']" />
                </div>
            </div>
            <div>
                <label class="block text-xs text-slate-500 mb-1.5">描述</label>
                <textarea
                    v-model="form.description"
                    rows="2"
                    placeholder="选填"
                    :class="[inputClass, 'resize-none']"></textarea>
            </div>
        </div>
        <div class="pt-4 border-t border-slate-100 flex justify-end gap-2.5">
            <button
                class="px-4 py-2 rounded-lg border border-slate-200 text-sm text-slate-500 hover:border-slate-300 transition"
                @click="close">
                取消
            </button>
            <button
                :disabled="submitting"
                class="px-4 py-2 rounded-lg bg-orange-500 hover:bg-orange-400 disabled:opacity-60 text-white text-sm font-medium transition shadow-sm shadow-orange-200"
                @click="submit">
                {{ mode === 'create' ? '接入' : '保存' }}
            </button>
        </div>
    </a-modal>
</template>
