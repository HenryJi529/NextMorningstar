package com.morningstar.kill.card.type.base;


/**
 * 使用时机：出牌阶段限一次。
 * 使用目标：你攻击范围内的一名其他角色
 * 作用效果：你对目标对应的角色造成1点普通伤害。
 */
public class 普杀 extends 杀 {
    public 普杀() {
        super(
                """
                        出牌阶段限一次，对你攻击范围内的一名其他角色使用，你对该角色造成1点伤害。
                        """
        );
    }
}
