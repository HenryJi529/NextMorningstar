package com.morningstar.kill.card.type.equipment.weapon;

import com.morningstar.kill.card.type.equipment.WeaponType;

/**
 * 当你声明使用【杀】后，若此【杀】为普【杀】，你可将此【杀】改为火【杀】。
 */
public class 朱雀羽扇 extends WeaponType {
    public 朱雀羽扇() {
        super(
                """
                        当你使用普通【杀】时，你可以将此【杀】改为火【杀】。
                        """
        );
    }
}
