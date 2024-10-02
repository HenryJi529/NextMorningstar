package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;
import com.morningstar.kill.card.type.重铸;

/**
 * 使用时机：出牌阶段。
 * 使用目标：一名有手牌或暗置的武将牌的其他角色。
 * 作用效果：你选择：1.观看目标对应的角色的手牌；2.观看目标对应的角色的一张暗置的武将牌。
 */
public class 知己知彼 extends InstantTacticType implements 重铸 {
    public 知己知彼() {
        super(
                """
                        出牌阶段，对一名其他角色使用，观看其一张暗置的武将牌或其手牌。
                        """
        );
    }
}
