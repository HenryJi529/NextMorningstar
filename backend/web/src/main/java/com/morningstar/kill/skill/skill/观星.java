package com.morningstar.kill.skill.skill;

import com.morningstar.kill.skill.Skill;
import com.morningstar.kill.skill.tag.PositiveSkill;

public class 观星 extends Skill implements PositiveSkill {
    public 观星() {
        super(
                """
                        **准备阶段**，你可以观看牌堆顶的五张牌（若存活人数小于4则改为三张），然后将其中任意张牌置于牌堆顶，其余置于牌堆底。若你将这些牌均放至牌堆底，则结束阶段你可以再进行一次“**观星**”。
                        """
        );
    }
}
