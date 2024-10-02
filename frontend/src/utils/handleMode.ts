export const isDevMode = () => {
    return import.meta.env.DEV;
}

export const isProdMode = () => {
    return import.meta.env.PROD;
}