package com.morningstar.kill.skill.skill;

import com.morningstar.kill.skill.Skill;
import com.morningstar.kill.skill.tag.PositiveSkill;


public class 仁德 extends Skill implements PositiveSkill {
    public 仁德() {
        super(
                """
                        **出牌阶段**每名角色限一次，你可以将任意张手牌交给一名其他角色，每回合你以此法给出第二张牌时，你可以视为使用一张基本牌。
                        """
        );
    }
}
