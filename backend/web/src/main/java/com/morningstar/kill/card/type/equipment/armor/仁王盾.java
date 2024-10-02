package com.morningstar.kill.card.type.equipment.armor;

import com.morningstar.kill.card.type.equipment.ArmorType;


/**
 * 锁定技，当黑色【杀】对目标的使用结算开始时，若此目标对应的角色为你，你令此【杀】对此目标无效。
 */
public class 仁王盾 extends ArmorType {
    public 仁王盾() {
        super(
                """
                        锁定技，黑色的【杀】对你无效。
                        """
        );
    }
}
