package com.morningstar.kill.domain.hero.hero;

import com.morningstar.kill.domain.hero.Faction;
import com.morningstar.kill.domain.hero.Hero;
import com.morningstar.kill.domain.hero.Sex;
import com.morningstar.kill.domain.skill.SkillPool;
import com.morningstar.kill.domain.skill.skill.奸雄;
import com.morningstar.kill.domain.skill.skill.护驾;

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
