package com.morningstar.kill.domain.skill.skill;

import com.morningstar.kill.domain.skill.tag.ConversionSkill;
import com.morningstar.kill.domain.skill.Skill;


public class 豹变 extends Skill implements ConversionSkill {
    public 豹变() {
        super(
        """
                **锁定技**，若你的体力值：不大于3，你拥有技能“**挑衅**”；不大于2，你拥有技能“**咆哮**”；为1，你拥有技能“**神速**”。
                """
        );
    }
}
