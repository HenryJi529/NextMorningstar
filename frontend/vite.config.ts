import { fileURLToPath, URL } from 'node:url';

import { defineConfig, loadEnv } from 'vite';
import { configDefaults } from 'vitest/config';
import vue from '@vitejs/plugin-vue';
import viteCompression from 'vite-plugin-compression';
import { visualizer } from 'rollup-plugin-visualizer';
import removeConsole from 'vite-plugin-remove-console';
import vueDevTools from 'vite-plugin-vue-devtools';
import path from 'path';

// https://vitejs.dev/config/
export default defineConfig(({ command, mode }) => {
    console.info('command:', command);
    console.info('mode:', mode);

    const { VITE_API_TARGET, VITE_API_BASE_PATH } = loadEnv(mode, process.cwd());

    return {
        base: '/',
        server: {
            port: 8090,
            open: true,
            proxy: {
                [VITE_API_BASE_PATH]: {
                    ws: true,
                    secure: false,
                    target: VITE_API_TARGET,
                    changeOrigin: true,
                    rewrite: path => path.replace(new RegExp(`^${VITE_API_BASE_PATH}`), ''),
                    maxBodySize: '140MB',
                },
            },
        },
        plugins: [
            vue(),
            vueDevTools(),
            removeConsole({
                includes: ['log', 'info'],
            }),
            visualizer({
                filename: new URL(`report/build/${mode}.html`, import.meta.url).pathname,
                // filename: path.resolve(__dirname, `build-report/${mode}.html`),
                gzipSize: true,
            }),
            viteCompression({
                algorithm: 'gzip', // 压缩算法，可选 'gzip' 或 'brotli'
                ext: '.gz', // 生成的压缩后缀
                threshold: 1024, // 仅压缩大于 1KB 的文件
                deleteOriginFile: false, // 是否删除原始文件（建议 false）
            }),
        ],
        resolve: {
            alias: {
                '@': fileURLToPath(new URL('./src', import.meta.url)),
                html2canvas: 'html2canvas-pro',
            },
        },
        css: {
            preprocessorOptions: {
                scss: {
                    api: 'modern-compiler', // or 'modern' 解决Legacy JS API is deprecated
                },
            },
        },
        build: {
            commonjsOptions: {
                transformMixedEsModules: true,
            },
        },
        test: {
            coverage: {
                enabled: true,
                all: false, // 默认仅包含被测试的文件，而不会统计项目中完全未被测试的文件
                reporter: ['html', 'text'],
                reportsDirectory: path.resolve(__dirname, 'report/coverage/'),
            },
            environment: 'jsdom',
            exclude: [...configDefaults.exclude, 'e2e/**'],
            reporters: ['verbose', 'html'], // 'default', 'verbose', 'dot',
            outputFile: {
                html: new URL('report/test/index.html', import.meta.url).pathname,
            },
            root: fileURLToPath(new URL('./', import.meta.url)),
        },
    };
});
