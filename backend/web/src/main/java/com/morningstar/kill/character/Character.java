package com.morningstar.kill.character;

import com.morningstar.kill.skill.Skill;
import lombok.Data;

import java.util.Set;

/**
 * 英雄
 * - 作用: 只用来初始化Player
 * - 每个英雄只设计一个版本
 */
@Data
public class Character {
    private final int blood;
    private final Sex sex;
    private final Faction faction;
    private final String description;
    private final Set<Skill> skills;
    private final boolean isLord;

    public Character(int blood, Sex sex, Faction faction, String description, Set<Skill> skills, boolean isLord) {
        this.blood = blood;
        this.sex = sex;
        this.faction = faction;
        this.description = description;
        this.skills = skills;
        this.isLord = isLord;
    }

    @Override
    public String toString() {
        return String.format("Character(name=%s, blood=%d, sex=%s, faction=%s, skills=%s)", this.getClass().getSimpleName(), blood, sex, faction, skills);
    }
}
