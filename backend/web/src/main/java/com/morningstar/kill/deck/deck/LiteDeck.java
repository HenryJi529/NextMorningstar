package com.morningstar.kill.deck.deck;

import com.morningstar.kill.card.Card;
import com.morningstar.kill.card.Rank;
import com.morningstar.kill.card.Suit;
import com.morningstar.kill.card.type.base.普杀;
import com.morningstar.kill.card.type.base.桃;
import com.morningstar.kill.card.type.base.闪;
import com.morningstar.kill.card.type.equipment.armor.八卦阵;
import com.morningstar.kill.card.type.equipment.mounts.大宛;
import com.morningstar.kill.card.type.equipment.mounts.爪黄飞电;
import com.morningstar.kill.card.type.equipment.mounts.的卢;
import com.morningstar.kill.card.type.equipment.mounts.赤兔;
import com.morningstar.kill.card.type.equipment.weapon.*;
import com.morningstar.kill.card.type.tactic.delayed.乐不思蜀;
import com.morningstar.kill.card.type.tactic.delayed.闪电;
import com.morningstar.kill.card.type.tactic.instant.*;
import com.morningstar.kill.deck.Deck;

import java.util.List;

public class LiteDeck extends Deck {
    public LiteDeck() {
        super("精简版牌堆");
    }

    @Override
    protected List<Card> load() {
        return List.of(
                new Card(Rank.ACE, Suit.HEARTS, new 万箭齐发()), new Card(Rank.ACE, Suit.HEARTS, new 桃园结义()),
                new Card(Rank.ACE, Suit.SPADES, new 决斗()), new Card(Rank.ACE, Suit.SPADES, new 闪电()),
                new Card(Rank.ACE, Suit.DIAMONDS, new 决斗()),
                new Card(Rank.ACE, Suit.CLUBS, new 诸葛连弩()),

                new Card(Rank.TWO, Suit.HEARTS, new 闪()),
                new Card(Rank.TWO, Suit.SPADES, new 雌雄双股剑()),
                new Card(Rank.TWO, Suit.DIAMONDS, new 闪()),
                new Card(Rank.TWO, Suit.CLUBS, new 八卦阵()),

                new Card(Rank.THREE, Suit.HEARTS, new 五谷丰登()),
                new Card(Rank.THREE, Suit.SPADES, new 过河拆桥()),
                new Card(Rank.THREE, Suit.DIAMONDS, new 闪()),
                new Card(Rank.THREE, Suit.CLUBS, new 普杀()),

                new Card(Rank.FOUR, Suit.HEARTS, new 桃()),
                new Card(Rank.FOUR, Suit.SPADES, new 过河拆桥()),
                new Card(Rank.FOUR, Suit.DIAMONDS, new 顺手牵羊()),
                new Card(Rank.FOUR, Suit.CLUBS, new 普杀()),

                new Card(Rank.FIVE, Suit.HEARTS, new 赤兔()),
                new Card(Rank.FIVE, Suit.SPADES, new 青龙偃月刀()),
                new Card(Rank.FIVE, Suit.DIAMONDS, new 贯石斧()),
                new Card(Rank.FIVE, Suit.CLUBS, new 的卢()),

                new Card(Rank.SIX, Suit.HEARTS, new 乐不思蜀()),
                new Card(Rank.SIX, Suit.SPADES, new 乐不思蜀()),
                new Card(Rank.SIX, Suit.DIAMONDS, new 普杀()),
                new Card(Rank.SIX, Suit.CLUBS, new 普杀()),

                new Card(Rank.SEVEN, Suit.HEARTS, new 桃()),
                new Card(Rank.SEVEN, Suit.SPADES, new 南蛮入侵()),
                new Card(Rank.SEVEN, Suit.DIAMONDS, new 闪()),
                new Card(Rank.SEVEN, Suit.CLUBS, new 南蛮入侵()),

                new Card(Rank.EIGHT, Suit.HEARTS, new 无中生有()),
                new Card(Rank.EIGHT, Suit.SPADES, new 普杀()),
                new Card(Rank.EIGHT, Suit.DIAMONDS, new 闪()),
                new Card(Rank.EIGHT, Suit.CLUBS, new 普杀()),

                new Card(Rank.NINE, Suit.HEARTS, new 桃()),
                new Card(Rank.NINE, Suit.SPADES, new 普杀()),
                new Card(Rank.NINE, Suit.DIAMONDS, new 闪()),
                new Card(Rank.NINE, Suit.CLUBS, new 普杀()),

                new Card(Rank.TEN, Suit.HEARTS, new 普杀()),
                new Card(Rank.TEN, Suit.SPADES, new 普杀()),
                new Card(Rank.TEN, Suit.DIAMONDS, new 普杀()),
                new Card(Rank.TEN, Suit.CLUBS, new 普杀()),

                new Card(Rank.JACK, Suit.HEARTS, new 无中生有()),
                new Card(Rank.JACK, Suit.SPADES, new 顺手牵羊()),
                new Card(Rank.JACK, Suit.DIAMONDS, new 闪()),
                new Card(Rank.JACK, Suit.CLUBS, new 普杀()),

                new Card(Rank.QUEEN, Suit.HEARTS, new 过河拆桥()),
                new Card(Rank.QUEEN, Suit.SPADES, new 丈八蛇矛()),
                new Card(Rank.QUEEN, Suit.DIAMONDS, new 桃()), new Card(Rank.QUEEN, Suit.DIAMONDS, new 无懈可击()),
                new Card(Rank.QUEEN, Suit.CLUBS, new 借刀杀人()), new Card(Rank.QUEEN, Suit.CLUBS, new 无懈可击()),

                new Card(Rank.KING, Suit.HEARTS, new 爪黄飞电()),
                new Card(Rank.KING, Suit.SPADES, new 大宛()),
                new Card(Rank.KING, Suit.DIAMONDS, new 普杀()),
                new Card(Rank.KING, Suit.CLUBS, new 普杀())
        );
    }
}
