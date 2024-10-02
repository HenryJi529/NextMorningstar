import { describe, it, expect } from 'vitest';
import { h } from 'vue';
import { mount } from '@vue/test-utils'; // 导入挂载组件的方法

import Alert from './Alert.vue'; // 导入要测试的组件

describe('Alert.vue', () => {
    it('是否正确传递属性和渲染插槽', () => {
        const wrapper = mount(Alert, {
            props: { alertType: 'info' },
            slots: {
                svg: h('div', {}, 'icon'),
                message: h('div', {}, '这是一个消息'),
            },
        });

        const container = wrapper.find('.alert');

        // 检查插槽是否渲染
        expect(container.html()).toContain('icon');
        expect(container.html()).toContain('这是一个消息');

        // 检查属性是否传递
        expect(container.classes()).toContain('alert-info');
    });
});
