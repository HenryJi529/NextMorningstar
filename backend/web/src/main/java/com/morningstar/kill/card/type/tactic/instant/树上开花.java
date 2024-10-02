package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

/**
 * 使用时机：出牌阶段。
 * 使用目标：你。
 * 作用效果：目标对应的角色弃置至多两张牌->若其以此法弃置的牌中：包括装备牌，其摸X+1张牌；不包括装备牌，其摸X张牌。（X为目标对应的角色以此法弃置的牌数）
 */
public class 树上开花 extends InstantTacticType {
    public 树上开花() {
        super(
                """
                        出牌阶段，对你使用。你弃置至多两张牌，然后摸等量的牌。若你以此法弃置了装备牌，则多摸一张牌。
                        """
        );
    }
}
