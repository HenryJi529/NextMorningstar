import js from "@eslint/js";
import globals from "globals";
import tseslint from "typescript-eslint";
import pluginVue from "eslint-plugin-vue";
import { defineConfig } from "eslint/config";

export default defineConfig([
  {
      files: ["**/*.{js,mjs,cjs,ts,mts,cts,vue}"],
      plugins: { js },
      extends: ["js/recommended"],
      languageOptions: {
          globals: {
              ...globals.browser,
              ...globals.node,
              "NodeJS": "readonly",
              "CanvasImageSource": "readonly",
              "NodeListOf": "readonly"
          }
      },
      rules: {
      }
  },
    tseslint.configs.recommended,
    pluginVue.configs["flat/essential"],
  {
      files: ["**/*.vue"],
      languageOptions: {
          parserOptions: { parser: tseslint.parser },
      },
      rules: {
          "vue/multi-word-component-names": "off",
          "@typescript-eslint/ban-ts-comment": "off",
          "vue/require-v-for-key": "off",
          "vue/no-mutating-props": "off",
          "@typescript-eslint/no-unused-vars": ["warn", {
              "argsIgnorePattern": "^_$",
              "varsIgnorePattern": "^_$"
          }]
      }
  },
]);
