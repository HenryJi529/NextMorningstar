package com.morningstar.kill.card;

import lombok.Getter;

@Getter
public enum Suit {
    SPADES("黑桃", '♠'), HEARTS("红心", '♥'), CLUBS("梅花", '♣'), DIAMONDS("方块", '♦');

    private final String value;
    private final Character icon;

    Suit(String value, Character icon) {
        this.value = value;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return this.icon.toString();
    }
}
