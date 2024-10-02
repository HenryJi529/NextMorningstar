package com.morningstar.kill.card.type.equipment.mounts;

import com.morningstar.kill.card.type.equipment.OffensiveMountsType;

public class 驽马 extends OffensiveMountsType {
    public 驽马() {
        super(
                """
                        锁定技，你计算与其他角色的距离-1；其他角色计算与你的距离视为1。
                        """
        );
    }
}
