package com.morningstar.kill.domain.hero.hero;

import com.morningstar.kill.domain.hero.Faction;
import com.morningstar.kill.domain.hero.Hero;
import com.morningstar.kill.domain.hero.Sex;
import com.morningstar.kill.domain.skill.SkillPool;
import com.morningstar.kill.domain.skill.skill.吗哪;
import com.morningstar.kill.domain.skill.skill.归心;
import com.morningstar.kill.domain.skill.skill.涅槃;
import com.morningstar.kill.domain.skill.skill.轻眉;

import java.util.HashSet;
import java.util.List;

public class 巧玲 extends Hero {
    public 巧玲(){
        super(
                3,
                Sex.FEMALE,
                Faction.QUN,
                "穿越到汉末的神秘女子",
                new HashSet<>(List.of(SkillPool.getSkillByClass(吗哪.class), SkillPool.getSkillByClass(轻眉.class), SkillPool.getSkillByClass(涅槃.class), SkillPool.getSkillByClass(归心.class))),
                false
        );
    }
}
