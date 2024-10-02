package com.morningstar.kill.card;

import com.morningstar.kill.card.type.equipment.weapon.诸葛连弩;
import com.morningstar.kill.card.type.tactic.instant.国无懈可击;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CardTest {
    @Test
    public void test() {
        Card card1 = new Card(Rank.EIGHT, Suit.SPADES, new 国无懈可击());
        log.info(card1.toString());
        log.info("{}({})", card1.getColor().name(), card1.getColor().getValue());

        Card card2 = new Card(Rank.JACK, Suit.DIAMONDS, new 诸葛连弩());
        log.info(card2.toString());
        log.info("{}({})", card2.getColor().name(), card2.getColor().getValue());
    }
}
