package com.morningstar.kill.domain.skill.skill;

import com.morningstar.kill.domain.skill.Skill;

public class 酒池 extends Skill {
    public 酒池(){
        super(
                """
                        你可以将一张♠手牌当【酒】使用；你使用的受【酒】影响的【杀】造成伤害后，本回合“**崩坏**”失效。
                        """
        );
    }
}
