import daisyui from 'daisyui';
import aspectRatio from '@tailwindcss/aspect-ratio';
import containerQueries from '@tailwindcss/container-queries';

/** @type {import('tailwindcss').Config} */
export default {
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
