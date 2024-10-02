package com.morningstar.kill.card.type.equipment.weapon;

import com.morningstar.kill.card.type.equipment.WeaponType;

/**
 * 当你使用的【杀】被其中一个目标对应的角色使用的【闪】抵消后，若其存活，你可弃置两张牌不会因此【闪】的抵消而生成“此【杀】对此目标生效时”和“此【杀】对此目标生效后”两个时机。
 */
public class 贯石斧 extends WeaponType {
    public 贯石斧() {
        super(
                """
                        当你使用的【杀】被抵消时，你可以弃置两张牌，令此【杀】依然造成伤害。
                        """
        );
    }
}
