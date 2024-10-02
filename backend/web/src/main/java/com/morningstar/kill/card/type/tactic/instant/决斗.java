package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

/**
 * 使用时机：出牌阶段。
 * 使用目标：一名其他角色。
 * 作用效果：由目标对应的角色开始，其与你轮流打出一张【杀】，直到你与其中的一名角色未打出【杀】。未打出【杀】的角色受到其与你中的另一名角色造成的1点普通伤害。
 */
public class 决斗 extends InstantTacticType {
    public 决斗() {
        super(
                """
                        出牌阶段，对一名其他角色使用。由该角色开始，其与你轮流打出一张【杀】，然后首先未打出【杀】的角色受到另一名角色造成的1点伤害。
                        """
        );
    }
}
