package com.morningstar.kill.hero.hero;

import com.morningstar.kill.hero.Faction;
import com.morningstar.kill.hero.Hero;
import com.morningstar.kill.hero.Sex;
import com.morningstar.kill.skill.SkillPool;
import com.morningstar.kill.skill.skill.制衡;
import com.morningstar.kill.skill.skill.救援;

import java.util.HashSet;
import java.util.List;

public class 孙权 extends Hero {
    public 孙权(){
        super(
                4,
                Sex.MALE,
                Faction.WU,
                "江东吴老二，大魏吴王",
                new HashSet<>(List.of(SkillPool.getSkillByClass(制衡.class), SkillPool.getSkillByClass(救援.class))),
                true
        );
    }
}
