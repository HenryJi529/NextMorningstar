package com.morningstar.kill.card.type.tactic.delayed;

import com.morningstar.kill.card.type.tactic.DelayedTacticType;

/**
 * 使用时机：出牌阶段。
 * 使用目标：一名其他角色。
 * 作用效果：目标对应的角色判定，若结果不为红桃，其跳过出牌阶段。
 */
public class 乐不思蜀 extends DelayedTacticType {
    public 乐不思蜀() {
        super(
                """
                        出牌阶段，对一名其他角色使用。将【乐不思蜀】置入该角色的判定区，若判定结果不为红桃，其跳过本回合的出牌阶段。
                        """
        );
    }
}
