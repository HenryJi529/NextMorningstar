package com.morningstar.kill.hero;

import com.morningstar.util.ClassUtil;
import com.morningstar.util.RandomUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 英雄池
 * - 设定不分游戏模式，只有一个英雄池
 */
public class HeroPool {
    private static final String packageName = HeroPool.class.getPackageName() + ".hero";
    private static final Map<Class<? extends Hero>, Hero> heroes = new HashMap<>();
    private static final List<String> _heroNames;
    private static final List<String> _lordNames = new ArrayList<>();

    static {
        Map<Class<?>, Object> instances = ClassUtil.loadInstancesByPackageName(Hero.class, packageName);
        for (Map.Entry<Class<?>, Object> entry : instances.entrySet()) {
            @SuppressWarnings("unchecked")
            Class<? extends Hero> heroClass = (Class<? extends Hero>) entry.getKey();
            Hero heroInstance = (Hero) entry.getValue();
            heroes.put(heroClass, heroInstance);
            if (heroInstance.isLord()) {
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
            heroClass = (Class<? extends Hero>) Class.forName(packageName + "." + heroName);
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
        for (int i = 0; i < N; i++) {
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
