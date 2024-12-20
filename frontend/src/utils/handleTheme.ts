export const setTheme = (theme: string) => {
    const html = document.querySelector('html') as HTMLHtmlElement;
    html.dataset.theme = theme;
    if(theme === 'dark') {
        html.classList.add('dark');
    }else{
        html.classList.remove('dark');
    }
}

export const getCurrentTheme = (): string => {
    const html = document.querySelector('html') as HTMLHtmlElement;
    return html.dataset.theme as string;
}

export const getDefaultTheme = (): string =>{
    const hour = new Date().getHours();
    if((hour >= 18 || hour < 6) && window.matchMedia('(prefers-color-scheme: dark)').matches){
        return "dark";
    }else {
        return "cupcake";
    }
}

export const getOppositeTheme = (theme: string) : string => {
    return theme === 'dark' ? 'cupcake' : 'dark'
}