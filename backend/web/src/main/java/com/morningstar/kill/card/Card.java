package com.morningstar.kill.card;

import lombok.Data;

/**
 * 游戏牌分为基本牌、锦囊牌、装备牌三种类型；
 * 锦囊牌分为延时类锦囊牌和及时类锦囊牌，
 * 装备牌分为武器牌、防具牌、坐骑牌、宝物牌。
 * =====================================
 * 赠予: 出牌阶段，你可以将带“赠”标记的手牌以正面向上的方式交给其他角色。若为装备牌，则进入该角色装备区且替换已有装备。
 */
@Data
public class Card {
    private Rank rank;
    private Suit suit;
    private Type type;

    public Card(Rank rank, Suit suit, Type type) {
        this.rank = rank;
        this.suit = suit;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%s(%s%s)", type, suit, rank);
    }

    public Color getColor() {
        if(suit.equals(Suit.CLUBS) || suit.equals(Suit.SPADES)){
            return Color.BLACK;
        }else{
            return Color.RED;
        }
    }
}

