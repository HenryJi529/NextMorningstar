package com.morningstar.kill.character;

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
public class CharacterPool {
    private static final String packageName = CharacterPool.class.getPackageName() + ".characters";
    private static final Map<Class<? extends Character>, Character> characters = new HashMap<>();
    private static final List<String> _characterNames;
    private static final List<String> _lordNames = new ArrayList<>();

    static {
        Map<Class<?>, Object> instances = ClassUtil.loadInstancesByPackageName(Character.class, packageName);
        for (Map.Entry<Class<?>, Object> entry : instances.entrySet()) {
            @SuppressWarnings("unchecked")
            Class<? extends Character> characterClass = (Class<? extends Character>) entry.getKey();
            Character characterInstance = (Character) entry.getValue();
            characters.put(characterClass, characterInstance);
            if (characterInstance.isLord()) {
                _lordNames.add(characterClass.getSimpleName());
            }
        }
        _characterNames = characters.keySet().stream().map(Class::getSimpleName).collect(Collectors.toList());
    }

    public static Character getCharacterByClass(Class<? extends Character> characterClass) {
        return characters.get(characterClass);
    }

    @SuppressWarnings("unchecked")
    public static Character getCharacterByName(String characterName) {
        Class<? extends Character> characterClass;
        try {
            characterClass = (Class<? extends Character>) Class.forName(packageName + "." + characterName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return getCharacterByClass(characterClass);
    }

    public static List<String> getCharacterNames() {
        return _characterNames;
    }

    public static List<String> getLordNames() {
        return _lordNames;
    }

    public static String showCharacters() {
        return characters.toString();
    }

    private static Character[] getCharactersByNameList(int N, List<String> names) {
        Character[] selected = new Character[N];
        Integer[] indexes = RandomUtil.getSampleIndexes(N, names.size());
        for (int i = 0; i < N; i++) {
            selected[i] = getCharacterByName(names.get(indexes[i]));
        }
        return selected;
    }

    public static Character[] getRandomNCharacters(int N) {
        return getCharactersByNameList(N, _characterNames);
    }

    public static Character[] getRandomNLords(int N) {
        return getCharactersByNameList(N, _lordNames);
    }
}
