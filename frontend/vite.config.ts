import {fileURLToPath, URL} from 'node:url'

import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'
import viteCompression from 'vite-plugin-compression';


// https://vitejs.dev/config/
export default defineConfig(({command, mode}) => {

    const {VITE_BACKEND_ADDRESS} = loadEnv(mode, process.cwd());

    return {
        base: "/",
        server: {
            port: 8090,
            open: true,
            proxy: {
                "/api": {
                    ws: true,
                    secure: false,
                    target: VITE_BACKEND_ADDRESS,
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/api/, ""),
                },
            },
        },
        plugins: [
            vue(),
            viteCompression({
                algorithm: 'gzip', // 压缩算法，可选 'gzip' 或 'brotli'
                ext: '.gz',       // 生成的压缩后缀
                threshold: 1024,  // 仅压缩大于 1KB 的文件
                deleteOriginFile: false, // 是否删除原始文件（建议 false）
            }),
        ],
        resolve: {
            alias: {
                '@': fileURLToPath(new URL('./src', import.meta.url)),
                'html2canvas': 'html2canvas-pro',
            }
        },
        css: {
            preprocessorOptions: {
                scss: {
                    api: "modern-compiler" // or 'modern' 解决Legacy JS API is deprecated
                }
            }
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
            },
            environment: 'jsdom',
            reporters: ['default', 'html'],
            outputFile: `${__dirname}/test/index.html`,
            root: fileURLToPath(new URL('./', import.meta.url))
        }
    }
}) as any;