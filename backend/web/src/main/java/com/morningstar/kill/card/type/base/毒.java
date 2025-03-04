package com.morningstar.kill.card.type.base;

import com.morningstar.kill.card.type.BaseType;

/**
 * ◆当牌正面朝上从一名角色的手牌区移至目标区域后❷，若这些牌中有在其手牌区里为【毒】的牌且这些牌不是因其执行【刮骨疗毒】的效果而被弃置而移动，其失去X点体力（X为这些牌中在其手牌区里为【毒】的牌数）。
 * ◆当一名角色的拼点的牌亮出后，若牌名为【毒】且此牌因其此次选择拼点的牌而离开其手牌区，其失去1点体力。
 * ◆当一名角色因摸牌或获得起始手牌而得到【毒】后❷，其可{展示此牌，将此牌交给其他角色}。
 */
public class 毒 extends BaseType {
    public 毒(){
        super(
                """
                        当此牌正面向上离开你的手牌区时，你失去1点体力；当你因摸牌或分发起始手牌而获得【毒】后，你可将其分配给其他角色（正面朝上移动且不触发前一个效果）。
                        """
        );
    }
}
