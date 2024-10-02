package com.morningstar.kill.character;

import java.util.HashSet;


public class Soldier extends Character {
    public Soldier(int blood, Sex sex, Faction faction) {
        super(
                blood, sex, faction, "谁说士兵就不是英雄", new HashSet<>(), false
        );
    }
}
