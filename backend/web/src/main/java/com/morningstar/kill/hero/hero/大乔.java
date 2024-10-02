package com.morningstar.kill.hero.hero;

import com.morningstar.kill.hero.Faction;
import com.morningstar.kill.hero.Hero;
import com.morningstar.kill.hero.Sex;
import com.morningstar.kill.skill.SkillPool;
import com.morningstar.kill.skill.skill.国色;
import com.morningstar.kill.skill.skill.流离;

import java.util.HashSet;
import java.util.List;

public class 大乔 extends Hero {
    public 大乔(){
        super(
                3,
                Sex.FEMALE,
                Faction.WU,
                "江东二乔的老大，孙策老婆",
                new HashSet<>(List.of(SkillPool.getSkillByClass(国色.class), SkillPool.getSkillByClass(流离.class))),
                false
        );
    }
}
