package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

/**
 * 使用时机：出牌阶段。
 * 使用目标：距离为1的一名区域里有牌的其他角色。
 * 作用效果：你获得目标对应的角色的区域里的一张牌。
 */
public class 顺手牵羊 extends InstantTacticType {
    public 顺手牵羊() {
        super(
                """
                        出牌阶段，对距离为1的一名区域里有牌的其他角色使用。你获得该角色区域内的一张牌。
                        """
        );
    }
}
