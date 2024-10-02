package com.morningstar.kill.domain.hero.hero;

import com.morningstar.kill.domain.hero.Faction;
import com.morningstar.kill.domain.hero.Hero;
import com.morningstar.kill.domain.hero.Sex;
import com.morningstar.kill.domain.skill.SkillPool;
import com.morningstar.kill.domain.skill.skill.空城;
import com.morningstar.kill.domain.skill.skill.观星;

import java.util.HashSet;
import java.util.List;

public class 诸葛亮 extends Hero {
    public 诸葛亮(){
        super(
                3,
                Sex.MALE,
                Faction.SHU,
                "丞相",
                new HashSet<>(List.of(SkillPool.getSkillByClass(观星.class), SkillPool.getSkillByClass(空城.class))),
                false
        );
    }
}
