package com.morningstar.kill.domain.deck.deck;

import com.morningstar.kill.domain.card.Card;
import com.morningstar.kill.domain.card.Rank;
import com.morningstar.kill.domain.card.Suit;
import com.morningstar.kill.domain.card.type.base.*;
import com.morningstar.kill.domain.card.type.equipment.armor.*;
import com.morningstar.kill.domain.card.type.equipment.mounts.*;
import com.morningstar.kill.domain.card.type.equipment.treasure.*;
import com.morningstar.kill.domain.card.type.equipment.weapon.*;
import com.morningstar.kill.domain.card.type.tactic.instant.*;
import com.morningstar.kill.domain.deck.Deck;

import java.util.List;

public class SpyDeck extends Deck {
    public SpyDeck() {
        super("用间篇扩展包");
    }

    @Override
    protected List<Card> load() {
        return List.of(
                new Card(Rank.ACE, Suit.HEARTS, new 刮骨疗毒()),
                new Card(Rank.ACE, Suit.SPADES, new 刮骨疗毒()),
                new Card(Rank.ACE, Suit.DIAMONDS, new 决斗(), true),
                new Card(Rank.ACE, Suit.CLUBS, new 折戟(), true),

                new Card(Rank.TWO, Suit.HEARTS, new 闪(), true),
                new Card(Rank.TWO, Suit.SPADES, new 七星宝刀(), true),
                new Card(Rank.TWO, Suit.DIAMONDS, new 闪(), true),
                new Card(Rank.TWO, Suit.CLUBS, new 刺杀()),

                new Card(Rank.THREE, Suit.HEARTS, new 五谷丰登(),true),
                new Card(Rank.THREE, Suit.SPADES, new 顺手牵羊(),true),
                new Card(Rank.THREE, Suit.DIAMONDS, new 树上开花(), true),
                new Card(Rank.THREE, Suit.CLUBS, new 引蜂衣(), true),

                new Card(Rank.FOUR, Suit.HEARTS, new 信鸽(), true),
                new Card(Rank.FOUR, Suit.SPADES, new 毒(), true),
                new Card(Rank.FOUR, Suit.DIAMONDS, new 树上开花(), true),
                new Card(Rank.FOUR, Suit.CLUBS, new 毒()),

                new Card(Rank.FIVE, Suit.HEARTS, new 普杀(), true),
                new Card(Rank.FIVE, Suit.SPADES, new 毒(), true),
                new Card(Rank.FIVE, Suit.DIAMONDS, new 闪()),
                new Card(Rank.FIVE, Suit.CLUBS, new 倚天剑()),

                new Card(Rank.SIX, Suit.HEARTS, new 趁火打劫()),
                new Card(Rank.SIX, Suit.SPADES, new 刺杀()),
                new Card(Rank.SIX, Suit.DIAMONDS, new 闪()),
                new Card(Rank.SIX, Suit.CLUBS, new 刺杀()),

                new Card(Rank.SEVEN, Suit.HEARTS, new 桃()),
                new Card(Rank.SEVEN, Suit.SPADES, new 刺杀()),
                new Card(Rank.SEVEN, Suit.DIAMONDS, new 闪()),
                new Card(Rank.SEVEN, Suit.CLUBS, new 刺杀()),

                new Card(Rank.EIGHT, Suit.HEARTS, new 桃()),
                new Card(Rank.EIGHT, Suit.SPADES, new 刺杀()),
                new Card(Rank.EIGHT, Suit.DIAMONDS, new 闪()),
                new Card(Rank.EIGHT, Suit.CLUBS, new 刺杀()),

                new Card(Rank.NINE, Suit.HEARTS, new 女装(), true),
                new Card(Rank.NINE, Suit.SPADES, new 毒(), true),
                new Card(Rank.NINE, Suit.DIAMONDS, new 推心置腹()),
                new Card(Rank.NINE, Suit.CLUBS, new 刺杀()),

                new Card(Rank.TEN, Suit.HEARTS, new 普杀(), true),
                new Card(Rank.TEN, Suit.SPADES, new 毒(), true),
                new Card(Rank.TEN, Suit.DIAMONDS, new 推心置腹()),
                new Card(Rank.TEN, Suit.CLUBS, new 刺杀()),

                new Card(Rank.JACK, Suit.HEARTS, new 普杀(), true),
                new Card(Rank.JACK, Suit.SPADES, new 无懈可击()),
                new Card(Rank.JACK, Suit.DIAMONDS, new 桃(), true),
                new Card(Rank.JACK, Suit.CLUBS, new 无懈可击()),

                new Card(Rank.QUEEN, Suit.HEARTS, new 普杀(), true),
                new Card(Rank.QUEEN, Suit.SPADES, new 趁火打劫()),
                new Card(Rank.QUEEN, Suit.DIAMONDS, new 闪()),
                new Card(Rank.QUEEN, Suit.CLUBS, new 无懈可击()),

                new Card(Rank.KING, Suit.HEARTS, new 战象(), true),
                new Card(Rank.KING, Suit.SPADES, new 趁火打劫()),
                new Card(Rank.KING, Suit.DIAMONDS, new 刺杀()),
                new Card(Rank.KING, Suit.CLUBS, new 驽马(), true)
        );
    }
}
