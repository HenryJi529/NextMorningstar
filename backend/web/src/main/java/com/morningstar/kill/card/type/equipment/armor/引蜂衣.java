package com.morningstar.kill.card.type.equipment.armor;

import com.morningstar.kill.card.type.equipment.ArmorType;

/**
 * ①锁定技，当你受到渠道为锦囊牌的伤害时，你令伤害值+1。
 * ②锁定技，当你因执行因【毒】而添加的规则的效果而失去体力时，你令失去的体力值+1。
 */
public class 引蜂衣 extends ArmorType {
    public 引蜂衣() {
        super(
                """
                        锁定技，你受到锦囊牌的伤害+1；当你因【毒】失去体力时，失去的体力值+1。
                        """
        );
    }
}
