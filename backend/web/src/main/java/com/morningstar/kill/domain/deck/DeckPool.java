package com.morningstar.kill.domain.deck;

import com.morningstar.kill.util.ClassScanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeckPool {
    private static final String packagePath = "com.morningstar.kill.domain.deck.deck";
    private static final Map<Class<? extends Deck>, Deck> decks = new HashMap<>();
    static {
        Map<Class<?>, Object> instances = ClassScanner.loadChildrenByPackagePath(Deck.class, packagePath);
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
            deckClass = (Class<? extends Deck>) Class.forName(packagePath + "." + deckName);
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
