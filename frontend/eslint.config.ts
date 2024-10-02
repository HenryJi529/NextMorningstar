import { globalIgnores } from 'eslint/config';
import { defineConfigWithVueTs, vueTsConfigs } from '@vue/eslint-config-typescript';
import pluginVue from 'eslint-plugin-vue';
import pluginVitest from '@vitest/eslint-plugin';
import skipFormatting from 'eslint-config-prettier/flat';

export default defineConfigWithVueTs(
    {
        name: 'app/files-to-lint',
        files: ['**/*.{vue,ts,mts,js,mjs}'],
    },

    globalIgnores(['**/report/**', '**/dist/**', '**/dist-ssr/**']),

    ...pluginVue.configs['flat/essential'],
    vueTsConfigs.recommended,

    {
        files: ['**/*.{vue,ts,mts,js,mjs}'],
        rules: {
            'vue/no-mutating-props': 'off',
            'vue/multi-word-component-names': 'off',
            '@typescript-eslint/no-unused-vars': [
                'warn', // 级别：警告（不会阻断构建，只提示）
                {
                    argsIgnorePattern: '^_$', // 忽略以 "_" 开头的参数
                    varsIgnorePattern: '^_$', // 忽略以 "_" 开头的变量
                },
            ],
        },
    },

    {
        ...pluginVitest.configs.recommended,
        files: ['src/**/__tests__/*', 'src/**/*.{spec,test}.ts'],
    },

    skipFormatting
);
