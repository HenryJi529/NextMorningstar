package com.morningstar.kill.hero.hero;

import com.morningstar.kill.hero.Faction;
import com.morningstar.kill.hero.Hero;
import com.morningstar.kill.hero.Sex;
import com.morningstar.kill.skill.SkillPool;
import com.morningstar.kill.skill.skill.奸雄;
import com.morningstar.kill.skill.skill.护驾;

import java.util.HashSet;
import java.util.List;

public class 曹操 extends Hero {
    public 曹操(){
        super(
                4,
                Sex.MALE,
                Faction.WEI,
                "治世之能臣，乱世之奸雄",
                new HashSet<>(List.of(SkillPool.getSkillByClass(奸雄.class), SkillPool.getSkillByClass(护驾.class))),
                true
        );
    }
}
