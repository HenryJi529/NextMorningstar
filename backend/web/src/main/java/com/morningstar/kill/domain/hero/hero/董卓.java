package com.morningstar.kill.domain.hero.hero;

import com.morningstar.kill.domain.hero.Faction;
import com.morningstar.kill.domain.hero.Hero;
import com.morningstar.kill.domain.hero.Sex;
import com.morningstar.kill.domain.skill.SkillPool;
import com.morningstar.kill.domain.skill.skill.崩坏;
import com.morningstar.kill.domain.skill.skill.暴虐;
import com.morningstar.kill.domain.skill.skill.肉林;
import com.morningstar.kill.domain.skill.skill.酒池;

import java.util.HashSet;
import java.util.List;

public class 董卓 extends Hero {
    public 董卓(){
        super(
                8,
                Sex.MALE,
                Faction.QUN,
                "敢废立皇帝的西凉军头",
                new HashSet<>(List.of(SkillPool.getSkillByClass(酒池.class), SkillPool.getSkillByClass(肉林.class), SkillPool.getSkillByClass(崩坏.class), SkillPool.getSkillByClass(暴虐.class))),
                true
        );
    }
}
