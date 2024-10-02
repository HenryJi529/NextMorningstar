package com.morningstar.kill.domain.skill.skill;

import com.morningstar.kill.domain.skill.Skill;

public class 归心 extends Skill {
    public 归心(){
        super(
                """
                        回合开始时，若你的“仁”标记数大于等于手牌数或体力值，你可以选择场上体力最大的男性角色（之一）转变为己方势力【若你为主公，则该男性角色转变为忠臣】，
                        若如此做，清空你的“仁”标记。
                        """
        );
    }
}
