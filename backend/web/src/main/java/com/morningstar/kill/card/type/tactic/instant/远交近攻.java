package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

/**
 * 使用时机：出牌阶段。
 * 使用目标：与你势力不同的一名角色。
 * 作用效果：目标对应的角色摸一张牌。你摸三张牌。
 */
public class 远交近攻 extends InstantTacticType {
    public 远交近攻() {
        super(
                """
                        出牌阶段，对有明置武将牌且与你势力不同的一名角色使用。该角色摸一张牌，然后你摸三张牌。
                        """
        );
    }
}
