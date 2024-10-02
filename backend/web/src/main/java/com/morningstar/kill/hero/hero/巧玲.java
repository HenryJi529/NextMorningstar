package com.morningstar.kill.hero.hero;

import com.morningstar.kill.hero.Faction;
import com.morningstar.kill.hero.Hero;
import com.morningstar.kill.hero.Sex;
import com.morningstar.kill.skill.SkillPool;
import com.morningstar.kill.skill.skill.*;


public class 巧玲 extends Hero {
    public 巧玲(){
        super(
                3,
                Sex.FEMALE,
                Faction.QUN,
                "穿越到汉末的神秘女子",
                SkillPool.getSkillSetByClasses(吗哪.class, 轻眉.class, 涅槃.class, 归心.class),
                false
        );
    }
}
