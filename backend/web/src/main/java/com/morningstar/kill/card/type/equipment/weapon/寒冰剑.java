package com.morningstar.kill.card.type.equipment.weapon;

import com.morningstar.kill.card.type.equipment.WeaponType;

public class 寒冰剑 extends WeaponType {
    public 寒冰剑() {
        super(
                """
                        当你使用【杀】对目标角色造成伤害时，若该角色有牌，你可以防止此伤害，改为依次弃置其两张牌。
                        """
        );
    }
}
