package com.morningstar.kill.hero.hero;

import com.morningstar.kill.hero.Faction;
import com.morningstar.kill.hero.Hero;
import com.morningstar.kill.hero.Sex;
import com.morningstar.kill.skill.SkillPool;
import com.morningstar.kill.skill.skill.豹变;


public class 夏侯霸 extends Hero {
    public 夏侯霸() {
        super(
                4,
                Sex.MALE,
                Faction.SHU,
                "夏侯渊降蜀的儿子",
                SkillPool.getSkillSetByClasses(豹变.class),
                false
        );
    }
}
