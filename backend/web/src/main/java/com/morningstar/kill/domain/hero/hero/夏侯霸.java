package com.morningstar.kill.domain.hero.hero;

import com.morningstar.kill.domain.hero.Faction;
import com.morningstar.kill.domain.hero.Hero;
import com.morningstar.kill.domain.hero.Sex;
import com.morningstar.kill.domain.skill.SkillPool;
import com.morningstar.kill.domain.skill.skill.豹变;

import java.util.HashSet;
import java.util.List;

public class 夏侯霸 extends Hero {
    public 夏侯霸(){
        super(
                4,
                Sex.MALE,
                Faction.SHU,
                "夏侯渊降蜀的儿子",
                new HashSet<>(List.of(SkillPool.getSkillByClass(豹变.class))),
                false
        );
    }
}
