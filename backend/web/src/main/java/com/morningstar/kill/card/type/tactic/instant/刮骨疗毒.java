package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

/**
 * 使用时机：出牌阶段。
 * 使用目标：一名已受伤的角色。
 * 作用效果：目标对应的角色回复1点体力，可弃置一张【毒】。
 */
public class 刮骨疗毒 extends InstantTacticType {
    public 刮骨疗毒() {
        super(
                """
                        出牌阶段，对一名已受伤的角色使用。该角色回复1点体力，然后其可以弃置一张【毒】（以此法弃置【毒】无须失去体力）。
                        """
        );
    }
}
