package com.morningstar.kill.character.characters;

import com.morningstar.kill.character.Character;
import com.morningstar.kill.character.Faction;
import com.morningstar.kill.character.Sex;
import com.morningstar.kill.skill.SkillPool;
import com.morningstar.kill.skill.skill.奸雄;
import com.morningstar.kill.skill.skill.护驾;


public class 曹操 extends Character {
    public 曹操() {
        super(
                4,
                Sex.MALE,
                Faction.WEI,
                "治世之能臣，乱世之奸雄",
                SkillPool.getSkillSetByClasses(奸雄.class, 护驾.class),
                true
        );
    }
}
