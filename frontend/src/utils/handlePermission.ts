import {useUserStore} from "@/stores/users";
import {useRouter} from "vue-router";

export const hasAnyPermission = (permissions: string[]) => {
    const userStore = useUserStore();
    for (const permission of permissions) {
        if (userStore.permissions.includes(permission)) {
            return true;
        }
    }
    return false;
}

export const getPermissions = (routeName: string) => {
    const router = useRouter();
    return router.getRoutes().filter(route => route.name === routeName)[0].meta.permissions as string[];
}