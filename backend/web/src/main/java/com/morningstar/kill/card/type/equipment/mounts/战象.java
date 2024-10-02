package com.morningstar.kill.card.type.equipment.mounts;

import com.morningstar.kill.card.type.equipment.DefensiveMountsType;

/**
 * ①锁定技，其他角色至你的距离+1。
 * ②锁定技，当牌因赠予而移至你的装备区或手牌区前，你将此次移动的目标区域改为弃牌堆。
 * ③锁定技，当有牌因其他角色的赠予而移动前，若其中有你的装备区里的牌，你取消你的装备区里的牌的此次移动。
 */
public class 战象 extends DefensiveMountsType {
    public 战象() {
        super(
                """
                        锁定技，其他角色计算与你的距离+1；其他角色对你赠予的牌视为赠予失败。
                        """
        );
    }
}
