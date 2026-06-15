import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import OnlyNameField from './OnlyNameField.vue';

describe('NameInput 组件', () => {
    it('初始渲染name', () => {
        const wrapper = mount(OnlyNameField, {
            props: {
                data: {
                    name: '张三',
                },
            },
        });

        // 输入框初始值正确
        expect(wrapper.find('input').element.value).toBe('张三');

        // 没有事件触发
        expect(wrapper.emitted('update-name')).toBeUndefined();
    });
});
