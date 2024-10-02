package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

/**
 * 使用时机：出牌阶段。
 * 使用目标：所有角色。
 * 作用效果：目标对应的角色回复1点体力。
 * ◆【桃园结义】对对应的角色未受伤的目标无效。
 */
public class 桃园结义 extends InstantTacticType {
    public 桃园结义() {
        super(
                """
                        出牌阶段，对所有角色使用。每名目标角色回复1点体力。
                        """
        );
    }
}
