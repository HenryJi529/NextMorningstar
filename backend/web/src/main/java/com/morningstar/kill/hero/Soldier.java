package com.morningstar.kill.hero;

import java.util.HashSet;


public class Soldier extends Hero {
    public Soldier(int blood, Sex sex, Faction faction) {
        super(
                blood, sex, faction, "谁说士兵就不是英雄", new HashSet<>(), false
        );
    }
}
