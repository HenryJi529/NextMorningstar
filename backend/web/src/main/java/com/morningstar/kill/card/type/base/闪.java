package com.morningstar.kill.card.type.base;

import com.morningstar.kill.card.type.BaseType;

/**
 * 使用时机：当【杀】对对应的角色为你的目标生效前。
 * 使用目标：此【杀】。
 * 作用效果：抵消此【杀】。
 */
public class 闪 extends BaseType {
    public 闪() {
        super(
                """
                        抵消【杀】的效果。
                        """
        );
    }
}
