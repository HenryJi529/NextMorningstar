import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import OnlyNameField from './OnlyNameField.vue';

describe('NameInput 组件', () => {
    it('初始渲染 props.name，未触发 update-name 事件', () => {
        const wrapper = mount(OnlyNameField, {
            props: {
                name: '张三',
            },
        });

        // 输入框初始值正确
        expect(wrapper.find('input').element.value).toBe('张三');

        // 没有事件触发
        expect(wrapper.emitted('update-name')).toBeUndefined();
    });

    it('输入框修改值，触发 update-name 事件并传递新值', async () => {
        const wrapper = mount(OnlyNameField, {
            props: { name: '李四' },
        });

        const input = wrapper.find('input');

        // 模拟输入新值（直接赋值 + 单次 input 事件）
        await input.setValue('王五');

        // 断言事件传递的参数（数组的每一项是单次触发的参数数组）
        const updateNameEvents = wrapper.emitted('update-name');

        // 理应触发了一次 update-name 事件
        expect(updateNameEvents).toBeDefined();
        expect(updateNameEvents?.length).toBe(1);
        expect(updateNameEvents?.[0]).toEqual(['王五']);
    });
});
