package com.morningstar.kill.domain.skill;

import com.morningstar.kill.util.ClassScanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 技能池
 * - 所有英雄的技能都登记在这里
 * - 不存在重名的英雄技能
 */
public class SkillPool {
    private static final String packagePath = "com.morningstar.kill.domain.skill.skill";
    private static final Map<Class<? extends Skill>, Skill> skills = new HashMap<>();
    static {
        Map<Class<?>, Object> instances = ClassScanner.loadChildrenByPackagePath(Skill.class, packagePath);
        for (Map.Entry<Class<?>, Object> entry : instances.entrySet()) {
            @SuppressWarnings("unchecked")
            Class<? extends Skill> skillClass = (Class<? extends Skill>) entry.getKey();
            Skill skillInstance = (Skill) entry.getValue();
            skills.put(skillClass, skillInstance);
        }
    }

    public static Skill getSkillByClass(Class<? extends Skill> skillClass) {
        return skills.get(skillClass);
    }

    @SuppressWarnings("unchecked")
    public static Skill getSkillByName(String skillName) {
        Class<? extends Skill> skillClass;
        try {
            skillClass = (Class<? extends Skill>) Class.forName(packagePath + "." + skillName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return getSkillByClass(skillClass);
    }

    public static List<String> getSkillNames() {
        return skills.keySet().stream().map(Class::getSimpleName).collect(Collectors.toList());
    }

    public static String showSkills() {
        return skills.toString();
    }
}
