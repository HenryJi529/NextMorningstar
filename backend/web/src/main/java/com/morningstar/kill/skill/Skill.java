package com.morningstar.kill.skill;

import lombok.Data;

/**
 * 技能
 * - 不考虑同名，但内容不同的情况
 */
@Data
public abstract class Skill {
    private String description;

    public Skill(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
