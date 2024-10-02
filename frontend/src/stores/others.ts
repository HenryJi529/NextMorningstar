import {defineStore} from "pinia";

export const useOtherStore = defineStore('others', {
    state: () => {
        return {
            isMobileMenuOpen: false,
        }
    }
})