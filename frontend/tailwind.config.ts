import type { Config } from 'tailwindcss';
import daisyui from 'daisyui';
import aspectRatio from '@tailwindcss/aspect-ratio';
import containerQueries from '@tailwindcss/container-queries';

const config: Config = {
    content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
    theme: {
        extend: {},
    },
    plugins: [daisyui, aspectRatio, containerQueries],
    daisyui: {
        prefix: '',
        darkTheme: 'dark',
        logs: true,
        themes: ['light', 'dark'],
    },
    // darkMode: 'class'
    darkMode: ['selector', '[data-theme="dark"]'],
};

export default config;
