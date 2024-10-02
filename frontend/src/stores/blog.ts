import {defineStore} from "pinia";

export const useBlogStore = defineStore('blog', {
    state: () => {
        return {
            isMobileMenuOpen: false,
        }
    }
})