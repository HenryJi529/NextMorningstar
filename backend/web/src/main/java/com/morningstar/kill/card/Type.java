package com.morningstar.kill.card;

import lombok.Data;

@Data
public abstract class Type {
    private String description;

    public Type(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
