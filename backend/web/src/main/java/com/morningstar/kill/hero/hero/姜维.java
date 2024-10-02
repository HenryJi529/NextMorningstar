package com.morningstar.kill.hero.hero;

import com.morningstar.kill.hero.Faction;
import com.morningstar.kill.hero.Hero;
import com.morningstar.kill.hero.Sex;
import com.morningstar.kill.skill.SkillPool;
import com.morningstar.kill.skill.skill.志继;
import com.morningstar.kill.skill.skill.挑衅;


public class 姜维 extends Hero {
    public 姜维() {
        super(
                4,
                Sex.MALE,
                Faction.SHU,
                "权力欲重的蜀汉后期顶梁柱",
                SkillPool.getSkillSetByClasses(挑衅.class, 志继.class),
                false
        );
    }
}
