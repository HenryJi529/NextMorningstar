package com.morningstar.kill.card.type.base;

public class 刺杀 extends 杀 {
    public 刺杀() {
        super(
                """
                        目标角色使用【闪】抵消刺【杀】时，若有手牌则需弃置一张手牌，否则此刺【杀】依然造成伤害。
                        """
        );
    }
}
