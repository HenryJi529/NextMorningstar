package com.morningstar.kill.domain.player;

import com.morningstar.kill.domain.card.Card;
import lombok.Data;

import java.util.Deque;

/**
 * 手牌区
 */
@Data
public class HandArea {
    private Deque<Card> cards;
}
