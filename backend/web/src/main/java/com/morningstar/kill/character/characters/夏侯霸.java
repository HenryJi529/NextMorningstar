package com.morningstar.kill.character.characters;

import com.morningstar.kill.character.Faction;
import com.morningstar.kill.character.Character;
import com.morningstar.kill.character.Sex;
import com.morningstar.kill.skill.SkillPool;
import com.morningstar.kill.skill.skill.豹变;


public class 夏侯霸 extends Character {
    public 夏侯霸() {
        super(
                4,
                Sex.MALE,
                Faction.SHU,
                "夏侯渊降蜀的儿子",
                SkillPool.getSkillSetByClasses(豹变.class),
                false
        );
    }
}
