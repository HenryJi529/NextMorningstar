import {useUserStore} from "@/stores/users";

export const hasAnyPermission = (permissions: string[])=>{
    const userStore = useUserStore();
    for (const permission of permissions) {
        if(userStore.permissions.includes(permission)){
            return true;
        }
    }
    return false;
}