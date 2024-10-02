package com.morningstar.kill.card.type.equipment.armor;

import com.morningstar.kill.card.type.equipment.ArmorType;

/**
 * 当你需要使用/打出【闪】时，你可判定若结果为红色，你使用/打出无对应的实体牌的【闪】。
 * ◆在一名角色执行〖八卦阵〗的效果而进行的判定流程中，其不能以任何方式操作其装备区里的【八卦阵】，但在此【闪】的使用流程中可操作其装备区里的【八卦阵】。
 */
public class 八卦阵 extends ArmorType {
    public 八卦阵() {
        super(
                """
                        当你需要使用或打出【闪】时，你可以进行判定，若结果为红色，你视为使用或打出一张【闪】。
                        """
        );
    }
}
