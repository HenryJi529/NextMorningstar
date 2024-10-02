package com.morningstar.common.converter;

import com.morningstar.common.pojo.bo.UserEditableInfo;
import com.morningstar.common.pojo.bo.UserPrivateInfo;
import com.morningstar.common.pojo.po.User;
import com.morningstar.common.pojo.bo.UserPublicInfo;
import com.morningstar.common.pojo.vo.req.RegisterRequestVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserConverter {
    @Mapping(target = "isSelf", ignore = true)
    @Mapping(target = "isOnline", ignore = true)
    UserPublicInfo userToUserPublicInfo(User user);

    @Mapping(target = "permissions", ignore = true)
    UserPrivateInfo userToUserPrivateInfo(User user);

    UserEditableInfo userToUserEditableInfo(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isLocked", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    User registerRequestVoToUser(RegisterRequestVo registerRequestVo);
}
