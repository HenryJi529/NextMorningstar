export const wait4Element = (elementId: string) => {
    return new Promise(resolve => {
        const element = document.getElementById(elementId);
        if (element) {
            return resolve(element);
        }
        const observer = new MutationObserver(() => {
            const element = document.getElementById(elementId);
            if (element) {
                observer.disconnect();
                resolve(element);
            }
        });
        observer.observe(document.body, {
            childList: true,
            subtree: true
        });
    });
};