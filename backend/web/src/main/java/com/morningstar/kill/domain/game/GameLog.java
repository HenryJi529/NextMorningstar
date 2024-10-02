package com.morningstar.kill.domain.game;

import com.morningstar.kill.pojo.po.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameLog {
    @Schema(description = "游戏模式")
    private Mode mode;

    @Schema(description = "成员列表")
    private List<User> members;

    @Schema(description = "赢家列表")
    private List<User> winners;
}
