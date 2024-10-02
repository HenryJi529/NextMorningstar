package com.morningstar.kill.domain.skill.skill;

import com.morningstar.kill.domain.skill.Skill;

public class 轻眉 extends Skill {
    public 轻眉(){
        super(
                """
                        若你的装备区里没有防具牌，你视为装备着【仁王盾】；
                        若你的装备区里没有坐骑牌，你视为装备着【进攻马】；
                        若你的装备区里没有武器牌，你视为装备着【雌雄双股剑】。
                        """
        );
    }
}
