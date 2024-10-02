import {QINIU_DOMAIN, USER_AVATAR_PREFIX} from "@/constants/MediaConstant";

export const getAvatarLink = (avatar: string | null) =>{
    return avatar === null ? "/media/base/img/avatar.svg": `https://${QINIU_DOMAIN}/${USER_AVATAR_PREFIX}/${avatar}`;
}