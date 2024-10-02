package com.morningstar.kill.hero.hero;

import com.morningstar.kill.hero.Faction;
import com.morningstar.kill.hero.Hero;
import com.morningstar.kill.hero.Sex;
import com.morningstar.kill.skill.SkillPool;
import com.morningstar.kill.skill.skill.空城;
import com.morningstar.kill.skill.skill.观星;

public class 诸葛亮 extends Hero {
    public 诸葛亮() {
        super(
                3,
                Sex.MALE,
                Faction.SHU,
                "丞相",
                SkillPool.getSkillSetByClasses(观星.class, 空城.class),
                false
        );
    }
}
