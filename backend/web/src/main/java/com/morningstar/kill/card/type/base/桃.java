package com.morningstar.kill.card.type.base;

import com.morningstar.kill.card.type.BaseType;

/**
 * 使用时机：出牌阶段。
 * 使用目标：已受伤的你。
 * 作用效果：目标对应的角色回复1点体力。
 * ================================
 * 使用时机：当一名角色处于濒死状态时。
 * 使用目标：该角色。
 * 作用效果：目标对应的角色回复1点体力。
 */
public class 桃 extends BaseType {
    public 桃() {
        super(
                """
                        出牌阶段回复已受伤的你1点体力或令一名处于濒死状态的角色回复1点体力。
                        """
        );
    }
}
