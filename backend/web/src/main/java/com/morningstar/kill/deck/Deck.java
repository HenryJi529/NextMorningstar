package com.morningstar.kill.deck;

import com.morningstar.kill.card.Card;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 牌堆
 * - 参考: <a href="https://zh.moegirl.org.cn/三国杀/牌堆结构">萌娘百科 - 三国杀/牌堆结构</a>
 * - 允许用户组合牌堆，但国战牌堆只能用于国战
 */
@Data
public abstract class Deck {
    private final String name;
    private final List<Card> cards;

    public Deck(String name) {
        this.name = name;
        this.cards = load();
    }

    protected abstract List<Card> load();

    public int size() {
        return cards.size();
    }

    @Override
    public String toString() {
        return String.format("%s(%d张)", name, cards.size());
    }

    public Map<String, Integer>[] count() {
        Map<String, Integer> rankCounts = new HashMap<>();
        Map<String, Integer> suitCounts = new HashMap<>();
        Map<String, Integer> typeCounts = new HashMap<>();

        for (Card card : cards) {
            String rank = card.getRank().toString();
            String suit = card.getSuit().toString();
            String type = card.getType().toString();
            rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
            suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
            typeCounts.put(type, typeCounts.getOrDefault(type, 0) + 1);
        }
        @SuppressWarnings("unchecked")
        Map<String, Integer>[] counts = new Map[3];
        counts[0] = rankCounts;
        counts[1] = suitCounts;
        counts[2] = typeCounts;
        return counts;
    }
}
