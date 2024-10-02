package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

/**
 * 使用时机：出牌阶段。
 * 使用目标：你。
 * 作用效果：目标对应的角色摸两张牌。
 */
public class 无中生有 extends InstantTacticType {
    public 无中生有() {
        super(
                """
                        出牌阶段，对你使用。你摸两张牌。
                        """
        );
    }
}
