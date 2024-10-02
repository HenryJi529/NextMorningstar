package com.morningstar.kill.pojo.po;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Deque;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.morningstar.kill.domain.exception.UserAlreadyInRoomException;
import com.morningstar.kill.domain.exception.UserNotInRoomException;
import com.morningstar.kill.domain.lobby.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TableName: user
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("user")
@Schema(description = "用户")
public class User implements Serializable {
    /**
     * 主键
     */
    @Schema(description = "用户id")
    @TableId("id")
    private UUID id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;

    /**
     * 是否锁定
     */
    @Schema(description = "是否锁定")
    private Boolean isLocked;

    /**
     * 是否为管理员
     */
    @Schema(description = "是否为管理员")
    private Boolean isAdmin;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    @TableLogic
    private Boolean isDeleted;

    /**
     * 是否在线
     */
    @Schema(description = "是否在线")
    private Boolean isOnline;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private String sex;

    /**
     * 头像
     */
    @Schema(description = "头像")
    private String avatar;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Timestamp createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    // NOTE: 不修改直接通过自身修改room属性，避免逻辑混乱
    @TableField(exist = false)
    private Room room;


    public void createRoom(){
        if(room != null){
            throw new UserAlreadyInRoomException();
        }
        new Room(this);
    }

    public void createRoomWithOthers(Deque<User> users){
        if(room != null){
            throw new UserAlreadyInRoomException();
        }
        new Room(this, users);
    }

    public void exitRoom(){
        if(this.room == null){
            throw new UserNotInRoomException();
        }
        this.room.removeMember(this);
    }

    public void inviteUser(User user){
        if(this.room == null){
            throw new UserNotInRoomException();
        }
        this.room.addMember(user);
    }

    public void closeRoom(){
        if(this.room == null){
            throw new UserNotInRoomException();
        }
        if(this.room.getOwner().equals(this)){
            this.room.close();
        }else{
            throw new RuntimeException("没有权限关闭房间");
        }
    }
}