package com.morningstar.kill.character.characters;

import com.morningstar.kill.character.Faction;
import com.morningstar.kill.character.Character;
import com.morningstar.kill.character.Sex;
import com.morningstar.kill.skill.SkillPool;
import com.morningstar.kill.skill.skill.志继;
import com.morningstar.kill.skill.skill.挑衅;


public class 姜维 extends Character {
    public 姜维() {
        super(
                4,
                Sex.MALE,
                Faction.SHU,
                "权力欲重的蜀汉后期顶梁柱",
                SkillPool.getSkillSetByClasses(挑衅.class, 志继.class),
                false
        );
    }
}
