package com.morningstar.kill.domain.skill.skill;

import com.morningstar.kill.domain.skill.tag.AwakeningSkill;
import com.morningstar.kill.domain.skill.Skill;

public class 志继 extends Skill implements AwakeningSkill {
    public 志继() {
        super(
        """
                **觉醒技**，**准备阶段**，若你没有手牌，你回复1点体力或摸两张牌，减1点体力上限，然后获得“**观星**”。
                """
        );
    }
}
