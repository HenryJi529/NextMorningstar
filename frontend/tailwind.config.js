/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{vue,js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {},
    },
    plugins: [
        require("daisyui"),
        require('@tailwindcss/aspect-ratio'),
        require('@tailwindcss/container-queries'),
    ],
    daisyui: {
        prefix: "",
        darkTheme: "dark",
        logs: true,
        themes: ["light", "dark"],
    },
    // darkMode: 'class'
    darkMode: ['selector', '[data-theme="dark"]']
}

