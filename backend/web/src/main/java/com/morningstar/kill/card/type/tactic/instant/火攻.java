package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

/**
 * 使用时机：出牌阶段。
 * 使用目标：一名有手牌的角色。
 * 作用效果：目标对应的角色展示一张手牌，你可弃置与此牌花色相同的一张手牌->其受到你对其造成的1点火焰伤害。
 */
public class 火攻 extends InstantTacticType {
    public 火攻() {
        super(
                """
                        出牌阶段，对一名有手牌的角色使用。目标对应的角色展示一张手牌，然后你可以弃置一张与所展示牌相同花色的手牌，则对其造成1点火焰伤害。
                        """
        );
    }
}
