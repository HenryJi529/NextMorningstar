package com.morningstar.kill.domain.hero;

import com.morningstar.kill.util.ClassScanner;
import com.morningstar.kill.util.RandomUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 英雄池
 * - 设定不分游戏模式，只有一个英雄池
 */
public class HeroPool {
    private static final String packagePath = "com.morningstar.kill.domain.hero.hero";
    private static final Map<Class<? extends Hero>, Hero> heroes = new HashMap<>();
    private static final List<String> _heroNames;
    private static final List<String> _lordNames = new ArrayList<>();

    static {
        Map<Class<?>, Object> instances = ClassScanner.loadChildrenByPackagePath(Hero.class, packagePath);
        for (Map.Entry<Class<?>, Object> entry : instances.entrySet()) {
            @SuppressWarnings("unchecked")
            Class<? extends Hero> heroClass = (Class<? extends Hero>) entry.getKey();
            Hero heroInstance = (Hero) entry.getValue();
            heroes.put(heroClass, heroInstance);
            if(heroInstance.isLord()) {
                _lordNames.add(heroClass.getSimpleName());
            }
        }
        _heroNames = heroes.keySet().stream().map(Class::getSimpleName).collect(Collectors.toList());
    }

    public static Hero getHeroByClass(Class<? extends Hero> heroClass) {
        return heroes.get(heroClass);
    }

    @SuppressWarnings("unchecked")
    public static Hero getHeroByName(String heroName) {
        Class<? extends Hero> heroClass;
        try {
            heroClass = (Class<? extends Hero>) Class.forName(packagePath + "." + heroName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return getHeroByClass(heroClass);
    }

    public static List<String> getHeroNames() {
        return _heroNames;
    }

    public static List<String> getLordNames() {
        return _lordNames;
    }

    public static String showHeroes() {
        return heroes.toString();
    }

    private static Hero[] getHeroesByNameList(int N, List<String> names) {
        Hero[] selected = new Hero[N];
        Integer[] indexes = RandomUtil.getSampleIndexes(N, names.size());
        for(int i = 0; i < N; i++) {
            selected[i] = getHeroByName(names.get(indexes[i]));
        }
        return selected;
    }

    public static Hero[] getRandomNHeroes(int N) {
        return getHeroesByNameList(N, _heroNames);
    }

    public static Hero[] getRandomNLords(int N) {
        return getHeroesByNameList(N, _lordNames);
    }
}
