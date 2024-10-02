package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;
import com.morningstar.kill.card.type.重铸;

public class 铁索连环 extends InstantTacticType implements 重铸 {
    public 铁索连环() {
        super(
                """
                        - 出牌阶段，对一至两名角色使用。目标角色横置或重置。（被横置的角色处于“连环状态”）
                        - 重铸：出牌阶段，你可以将此牌置入弃牌堆，然后摸一张牌。
                        """
        );
    }
}
