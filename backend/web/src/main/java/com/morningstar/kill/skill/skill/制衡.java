package com.morningstar.kill.skill.skill;

import com.morningstar.kill.skill.Skill;

public class 制衡 extends Skill {
    public 制衡() {
        super(
                """
                        **出牌阶段**限一次，你可以弃置任意张牌，然后摸等量的牌。若你以此法弃置了所有手牌，则你多摸一张牌。
                        """
        );
    }
}
