package com.morningstar.kill.skill.skill;

import com.morningstar.kill.skill.Skill;
import com.morningstar.kill.skill.tag.ConversionSkill;


public class 豹变 extends Skill implements ConversionSkill {
    public 豹变() {
        super(
                """
                        **锁定技**，若你的体力值：不大于3，你拥有技能“**挑衅**”；不大于2，你拥有技能“**咆哮**”；为1，你拥有技能“**神速**”。
                        """
        );
    }
}
