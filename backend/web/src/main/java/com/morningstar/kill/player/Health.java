package com.morningstar.kill.player;

import lombok.Data;

@Data
public class Health {
    private int currBlood;
    private int maxBlood;

    public Health(int maxBlood) {
        this.maxBlood = maxBlood;
        this.currBlood = maxBlood;
    }
}
