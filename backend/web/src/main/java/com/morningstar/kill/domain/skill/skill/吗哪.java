package com.morningstar.kill.domain.skill.skill;

import com.morningstar.kill.domain.skill.Skill;

public class 吗哪 extends Skill {
    public 吗哪(){
        super(
                """
                        出牌阶段限一次，你可以选择弃置场上的一张【兵粮寸断】，若如此做，你获得一枚“仁”标记。
                        """
        );
    }
}
