package com.morningstar.kill.card.type.equipment.weapon;

import com.morningstar.kill.card.type.equipment.WeaponType;

public class 雌雄双股剑 extends WeaponType {
    public 雌雄双股剑() {
        super(
                """
                        当你使用【杀】指定一名与你性别不同的角色为目标时，你可以令其选择一项：1.弃置一张手牌；2.令你摸一张牌。
                        """
        );
    }
}
