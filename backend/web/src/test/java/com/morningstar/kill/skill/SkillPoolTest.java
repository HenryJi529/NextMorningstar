package com.morningstar.kill.skill;

import com.morningstar.kill.skill.skill.观星;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class SkillPoolTest {
    @Test
    public void test() {
        log.info(SkillPool.showSkills());
        log.info(SkillPool.getSkillNames().toString());
        Skill skill1 = SkillPool.getSkillByClass(观星.class);
        Skill skill2 = SkillPool.getSkillByName("观星");
        assert skill1 == skill2;
    }
}