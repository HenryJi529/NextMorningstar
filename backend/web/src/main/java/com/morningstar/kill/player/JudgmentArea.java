package com.morningstar.kill.player;

import com.morningstar.kill.card.Card;
import lombok.Data;

/**
 * 判定区
 * - 相同的判定牌只能有一张
 * - 不能使用数组/队列，注意考虑特殊情况【e.g. 大乔的"国色"】
 */
@Data
public class JudgmentArea {
    private Card 闪电位;
    private Card 乐不思蜀位;
    private Card 兵粮寸断位;
}
