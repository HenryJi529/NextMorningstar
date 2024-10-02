import {QINIU_DOMAIN, USER_AVATAR_PREFIX} from "@/constants/MediaConstant";
import defaultAvatar from "@/assets/img/base/avatar.svg";

export const getAvatarLink = (avatar: string | null) => {
    return avatar === null || avatar === "" ? defaultAvatar : `https://${QINIU_DOMAIN}/${USER_AVATAR_PREFIX}/${avatar}`;
}