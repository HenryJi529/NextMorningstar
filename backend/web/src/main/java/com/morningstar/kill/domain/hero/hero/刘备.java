package com.morningstar.kill.domain.hero.hero;

import com.morningstar.kill.domain.hero.*;
import com.morningstar.kill.domain.skill.SkillPool;
import com.morningstar.kill.domain.skill.skill.仁德;
import com.morningstar.kill.domain.skill.skill.激将;

import java.util.HashSet;
import java.util.List;


/**
 * 技能: 仁德、激将
 */
public class 刘备 extends Hero {
    public 刘备() {
        super(
                4,
                Sex.MALE,
                Faction.SHU,
                "大汉皇叔，汉昭烈帝",
                new HashSet<>(List.of(SkillPool.getSkillByClass(仁德.class), SkillPool.getSkillByClass(激将.class))),
                true
        );
    }
}
