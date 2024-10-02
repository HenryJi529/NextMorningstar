package com.morningstar.kill.card.type.equipment.armor;

import com.morningstar.kill.card.type.equipment.ArmorType;

/**
 * 锁定技，当你成为【杀】的目标后，若你为男性，你判定若结果为黑色，你令此【杀】于对此目标进行的使用结算中的伤害值基数+1。
 */
public class 女装 extends ArmorType {
    public 女装() {
        super(
                """
                        锁定技，若你是男性角色，当你成为【杀】的目标后，你进行判定：若结果为黑色，此【杀】伤害+1。
                        """
        );
    }
}
