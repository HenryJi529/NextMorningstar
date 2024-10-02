package com.morningstar.kill.card.type.base;

import com.morningstar.kill.card.type.BaseType;

/**
 * 使用时机：出牌阶段。
 * 使用条件：你于此回合内未使用过【酒】（使用方法①）。[1]
 * 使用目标：你。
 * 作用效果：目标对应的角色于此回合内使用的下一张【杀】的伤害值基数+1。
 * ===============================================
 * 使用时机：当你处于濒死状态时。
 * 使用目标：你。
 * 作用效果：目标对应的角色回复1点体力。
 */
public class 酒 extends BaseType {
    public 酒() {
        super(
                """
                        出牌阶段令你本回合使用的下一张【杀】伤害+1或令处于濒死状态的你回复1点体力。
                        """
        );
    }
}
