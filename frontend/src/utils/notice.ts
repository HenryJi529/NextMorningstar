import { notification } from 'ant-design-vue';
import { fetchSysParam } from '@/axios/system';
import type { SysParam } from '@/types/system';
import type { R } from '@/types/common';
import { ResponseCode } from '@/constants/response';

interface Notice {
    type: 'success' | 'info' | 'warning' | 'error';
    message: string;
    description: string;
    enable: boolean;
}

const isNotice = (data: unknown): data is Notice => {
    const validTypes = ['success', 'info', 'warning', 'error'];
    return (
        // NOTE: null的类型是object。。。
        data !== null &&
        typeof data === 'object' &&
        'type' in data &&
        typeof data.type === 'string' &&
        validTypes.includes(data.type) &&
        'message' in data &&
        typeof data.message === 'string' &&
        'description' in data &&
        typeof data.description === 'string' &&
        'enable' in data &&
        typeof data.enable === 'boolean'
    );
};

export const popupNotice = async () => {
    const response: R<SysParam> = (await fetchSysParam('notice')).data;
    if (response.code !== ResponseCode.SUCCESS) {
        return;
    }

    try {
        const notice = JSON.parse(response.data.value);

        if (!isNotice(notice)) {
            console.error('公告格式错误');
            return;
        }

        if (notice.enable) {
            notification.open({
                message: notice.message,
                description: notice.description,
                duration: 0,
                type: notice.type,
            });
        }
    } catch {
        console.error('公告解析失败');
    }
};
