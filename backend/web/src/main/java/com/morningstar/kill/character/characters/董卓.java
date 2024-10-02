package com.morningstar.kill.character.characters;

import com.morningstar.kill.character.Character;
import com.morningstar.kill.character.Faction;
import com.morningstar.kill.character.Sex;
import com.morningstar.kill.skill.SkillPool;
import com.morningstar.kill.skill.skill.崩坏;
import com.morningstar.kill.skill.skill.暴虐;
import com.morningstar.kill.skill.skill.肉林;
import com.morningstar.kill.skill.skill.酒池;


public class 董卓 extends Character {
    public 董卓() {
        super(
                8,
                Sex.MALE,
                Faction.QUN,
                "敢废立皇帝的西凉军头",
                SkillPool.getSkillSetByClasses(酒池.class, 肉林.class, 崩坏.class, 暴虐.class),
                true
        );
    }
}
