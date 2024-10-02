package com.morningstar.blog.pojo.bo;

import com.morningstar.blog.pojo.po.Comment;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDetail extends Comment {
    private String username;
    private String nickname;
    private String avatar;
    private Set<UUID> thumbsUpUserIds;
    private Set<UUID> thumbsDownUserIds;
}
