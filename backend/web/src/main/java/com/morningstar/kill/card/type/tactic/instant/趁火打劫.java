package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

/**
 * 使用时机：出牌阶段。
 * 使用目标：一名有手牌的其他角色。
 * 作用效果：你展示目标对应的角色一张手牌->目标对应的角色选择：1.将此牌交给你；2.受到你造成的1点普通伤害。
 */
public class 趁火打劫 extends InstantTacticType {
    public 趁火打劫() {
        super(
                """
                        出牌阶段，对一名其他角色使用。你展示其一张手牌并令其选择一项：1.将此牌交给你；2.受到你造成的1点伤害。
                        """
        );
    }
}
