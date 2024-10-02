package com.morningstar.kill.domain.card;

import com.morningstar.kill.domain.card.type.equipment.weapon.诸葛连弩;
import com.morningstar.kill.domain.card.type.tactic.instant.国无懈可击;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TestCard {
    @Test
    public void test() {
        Card card1 = new Card(Rank.EIGHT, Suit.SPADES, new 国无懈可击());
        log.info(card1.toString());
        Card card2 = new Card(Rank.JACK, Suit.CLUBS, new 诸葛连弩(), true);
        log.info(card2.toString());
    }
}
