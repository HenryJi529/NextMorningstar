package com.morningstar.kill.character.characters;

import com.morningstar.kill.character.Faction;
import com.morningstar.kill.character.Character;
import com.morningstar.kill.character.Sex;
import com.morningstar.kill.skill.SkillPool;
import com.morningstar.kill.skill.skill.仁德;
import com.morningstar.kill.skill.skill.激将;


public class 刘备 extends Character {
    public 刘备() {
        super(
                4,
                Sex.MALE,
                Faction.SHU,
                "大汉皇叔，汉昭烈帝",
                SkillPool.getSkillSetByClasses(仁德.class, 激将.class),
                true
        );
    }
}
