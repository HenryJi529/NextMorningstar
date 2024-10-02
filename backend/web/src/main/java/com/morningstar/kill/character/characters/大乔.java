package com.morningstar.kill.character.characters;

import com.morningstar.kill.character.Faction;
import com.morningstar.kill.character.Character;
import com.morningstar.kill.character.Sex;
import com.morningstar.kill.skill.SkillPool;
import com.morningstar.kill.skill.skill.国色;
import com.morningstar.kill.skill.skill.流离;

public class 大乔 extends Character {
    public 大乔() {
        super(
                3,
                Sex.FEMALE,
                Faction.WU,
                "江东二乔的老大，孙策老婆",
                SkillPool.getSkillSetByClasses(国色.class, 流离.class),
                false
        );
    }
}
