package com.morningstar.kill.card.type.equipment.armor;

import com.morningstar.kill.card.type.equipment.ArmorType;

/**
 * ①锁定技，当【南蛮入侵】、【万箭齐发】或普【杀】[1]对目标的使用结算开始时，若此目标对应的角色为你，你令此牌对此目标无效。
 * ②锁定技，当你受到火焰伤害时，你令伤害值+1。
 */
public class 藤甲 extends ArmorType {
    public 藤甲() {
        super(
                """
                        锁定技，【南蛮入侵】、【万箭齐发】和普【杀】对你无效；当你受到火焰伤害时，你令伤害值+1。
                        """
        );
    }
}
