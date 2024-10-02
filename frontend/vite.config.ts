import { fileURLToPath, URL } from 'node:url'

import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'


// https://vitejs.dev/config/
export default defineConfig(({ command, mode}) => {

  const {VITE_BACKEND_ADDRESS} = loadEnv(mode, process.cwd());

  return {
    base: "/",
    server: {
      port: 8090,
      open: true,
      proxy: {
        "/api": {
          secure: false,
          target: VITE_BACKEND_ADDRESS,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ""),
        },
      },
    },
    plugins: [
        vue(),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          api: "modern-compiler" // or 'modern' 解决Legacy JS API is deprecated
        }
      }
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