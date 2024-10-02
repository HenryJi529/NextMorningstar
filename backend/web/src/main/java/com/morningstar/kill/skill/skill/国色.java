package com.morningstar.kill.skill.skill;

import com.morningstar.kill.skill.Skill;

public class 国色 extends Skill {
    public 国色() {
        super(
                """
                        **出牌阶段**限一次，你可以选择一项：
                        1. 将一张♦牌当【乐不思蜀】使用；
                        2. 弃置一张♦牌并弃置场上的一张【乐不思蜀】。
                        选择完成后，你摸一张牌。
                        """
        );
    }
}
