package com.morningstar.kill.skill.skill;

import com.morningstar.kill.skill.Skill;
import com.morningstar.kill.skill.tag.PositiveSkill;

public class 挑衅 extends Skill implements PositiveSkill {
    public 挑衅() {
        super(
                """
                        **出牌阶段**限一次，你可以选择一名其他角色，然后除非该角色对你使用一张【杀】，否则你弃置其一张牌。
                        """
        );
    }
}
