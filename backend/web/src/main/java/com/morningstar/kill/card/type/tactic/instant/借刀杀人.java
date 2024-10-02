package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

public class 借刀杀人 extends InstantTacticType {
    public 借刀杀人() {
        super(
                """
                        出牌阶段，对一名装备区里有武器牌的其他角色使用。除非该角色对其攻击范围内，由你选择的另一名角色使用一张【杀】，否则其将其装备区里的武器牌交给你。
                        """
        );
    }
}
