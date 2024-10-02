package com.morningstar.kill.card;

import lombok.Getter;

@Getter
public enum Color {
    RED("红"),
    BLACK("黑");

    public final String value;

    Color(String value) {
        this.value = value;
    }
}
