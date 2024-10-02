package com.morningstar.kill.deck;

import com.morningstar.kill.deck.deck.ConquestDeck;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@Slf4j
public class DeckPoolTest {
    @Test
    public void test() {
        log.info(DeckPool.showDecks());
        log.info(DeckPool.getDeckNames().toString());
        Deck deck1 = DeckPool.getDeckByClass(ConquestDeck.class);
        Deck deck2 = DeckPool.getDeckByName("ConquestDeck");
        log.info(Arrays.toString(deck1.count()));
        assert deck1 == deck2;
    }
}
