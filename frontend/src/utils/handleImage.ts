export const setMaxWidthToNaturalWidth = () => {
    const images = document.querySelectorAll<HTMLImageElement>('img');
    images.forEach(image => {
        if(image.complete){
            image.style.maxWidth = `${image.naturalWidth}px`;
        }else{
            image.addEventListener('load', () => {
                image.style.maxWidth = `${image.naturalWidth}px`;
            })
        }
    })
}