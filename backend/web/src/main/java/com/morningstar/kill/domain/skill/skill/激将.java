package com.morningstar.kill.domain.skill.skill;

import com.morningstar.kill.domain.skill.tag.LordSkill;
import com.morningstar.kill.domain.skill.tag.PositiveSkill;
import com.morningstar.kill.domain.skill.Skill;

public class 激将 extends Skill implements PositiveSkill, LordSkill {
    public 激将(){
        super(
        """
                **主公技**，你可以令其他蜀势力角色选择是否替你使用或打出【杀】。
                """
        );
    }
}
