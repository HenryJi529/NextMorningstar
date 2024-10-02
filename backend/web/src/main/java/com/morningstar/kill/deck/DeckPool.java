package com.morningstar.kill.deck;

import com.morningstar.util.ClassUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeckPool {
    private static final String packageName = DeckPool.class.getPackageName() + ".deck";
    private static final Map<Class<? extends Deck>, Deck> decks = new HashMap<>();

    static {
        Map<Class<?>, Object> instances = ClassUtil.loadInstancesByPackageName(Deck.class, packageName);
        for (Map.Entry<Class<?>, Object> entry : instances.entrySet()) {
            @SuppressWarnings("unchecked")
            Class<? extends Deck> deckClass = (Class<? extends Deck>) entry.getKey();
            Deck deckInstance = (Deck) entry.getValue();
            decks.put(deckClass, deckInstance);
        }
    }

    public static Deck getDeckByClass(Class<? extends Deck> deckClass) {
        return decks.get(deckClass);
    }

    @SuppressWarnings("unchecked")
    public static Deck getDeckByName(String deckName) {
        Class<? extends Deck> deckClass;
        try {
            deckClass = (Class<? extends Deck>) Class.forName(packageName + "." + deckName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return getDeckByClass(deckClass);
    }

    public static List<String> getDeckNames() {
        return decks.keySet().stream().map(Class::getSimpleName).collect(Collectors.toList());
    }

    public static String showDecks() {
        return decks.toString();
    }
}
