package com.morningstar.kill.player;

import com.morningstar.kill.card.Card;
import lombok.Data;

import java.util.Deque;

/**
 * 手牌区
 */
@Data
public class HandArea {
    private Deque<Card> cards;
}
