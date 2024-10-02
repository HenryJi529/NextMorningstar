package com.morningstar.kill.card.type.equipment.weapon;

import com.morningstar.kill.card.type.equipment.WeaponType;

public class 古锭刀 extends WeaponType {
    public 古锭刀() {
        super(
                """
                        锁定技，当你使用【杀】对目标角色造成伤害时，若该角色没有手牌，则此伤害+1。
                        """
        );
    }
}
