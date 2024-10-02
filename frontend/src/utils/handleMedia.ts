import {CDN_DOMAIN, USER_AVATAR_PREFIX} from "@/constants/MediaConstant";

export const getAvatarLink = (avatar: string) =>{
    return `https://${CDN_DOMAIN}/${USER_AVATAR_PREFIX}/${avatar}`
}