package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

public class 推心置腹 extends InstantTacticType {
    public 推心置腹() {
        super(
                """
                        出牌阶段，对距离为1的一名其他角色使用。你获得目标角色区域里至多两张牌，然后交给其等量的手牌。
                        """
        );
    }
}
