package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

/**
 * 使用时机：出牌阶段。
 * 使用目标：所有其他角色。
 * 作用效果：目标对应的角色需打出【杀】，否则受到你造成的1点普通伤害。
 */
public class 南蛮入侵 extends InstantTacticType {
    public 南蛮入侵() {
        super(
                """
                        出牌阶段，对所有其他角色使用。每名目标角色需打出一张【杀】，否则受到你造成的1点伤害。
                        """
        );
    }
}
