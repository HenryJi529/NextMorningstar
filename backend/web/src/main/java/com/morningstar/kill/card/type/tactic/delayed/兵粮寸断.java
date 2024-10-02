package com.morningstar.kill.card.type.tactic.delayed;

import com.morningstar.kill.card.type.tactic.DelayedTacticType;

public class 兵粮寸断 extends DelayedTacticType {
    public 兵粮寸断() {
        super(
                """
                        出牌阶段，对距离为1的一名其他角色使用。将【兵粮寸断】置入该角色的判定区，若判定结果不为梅花，其跳过本回合的摸牌阶段。
                        """
        );
    }
}
