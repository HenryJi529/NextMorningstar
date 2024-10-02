package com.morningstar.kill.skill.skill;

import com.morningstar.kill.skill.Skill;
import com.morningstar.kill.skill.tag.AwakeningSkill;

public class 志继 extends Skill implements AwakeningSkill {
    public 志继() {
        super(
                """
                        **觉醒技**，**准备阶段**，若你没有手牌，你回复1点体力或摸两张牌，减1点体力上限，然后获得“**观星**”。
                        """
        );
    }
}
