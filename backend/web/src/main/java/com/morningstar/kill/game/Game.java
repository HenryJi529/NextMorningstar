package com.morningstar.kill.game;

import com.morningstar.kill.deck.Deck;
import com.morningstar.kill.deck.DeckPool;
import com.morningstar.kill.exception.*;
import com.morningstar.kill.hero.Hero;
import com.morningstar.kill.hero.HeroPool;
import com.morningstar.kill.room.Table;
import com.morningstar.kill.player.Player;
import lombok.Data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;
import java.util.UUID;


/**
 * 游戏抽象类
 * - 可以根据table和mode创建具体的游戏类
 * - SharedTable只能玩2V2和3V3
 * - 除了国战，每种游戏模式都可以分配角色
 * - 国战按照国家划分队伍，如果某个势力明示的人数已经超过总人数的一半，该势力之后明示的玩家就会变为野心家
 */
@Data
public abstract class Game {
    private final UUID id;
    private final Table table;
    private final Mode mode;
    private Deck deck;
    private DrawPile drawPile;
    private DiscardPile discardPile;
    private Player[] players;

    protected Game(Table table, Mode mode) {
        this.id = UUID.randomUUID();
        this.table = table;
        this.mode = mode;
    }

    public static Game createGame(Table table, Mode mode) {
        Game game;
        // 判断牌桌人数是否与游戏模式冲突
        if (table.getMemberCount() < mode.getMaxHeadNum() || table.getMemberCount() > mode.getMaxHeadNum()) {
            throw new GameModeNotSatisfiedByTableMemberCountException();
        }
        // 创建对应的游戏
        try {
            Constructor<? extends Game> constructor = mode.getGameClass().getDeclaredConstructor(Table.class, Mode.class);
            constructor.setAccessible(true);
            game = constructor.newInstance(table, mode);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new GameCreatedFailedException(e.getMessage());
        }
        // 将对应的游戏放入游戏池
        GamePool.put(game);
        return game;
    }

    public void init() {

    }

    public void start() {
        this.drawPile = new DrawPile(deck);
        this.discardPile = new DiscardPile();
        if (players == null) {
            throw new GamePlayersNotInitializedException();
        }
    }

    public void initializePlayers() {
        this.players = new Player[table.getMemberCount()];
        // TODO: 先分配身份，后选英雄【国战是选英雄的时候分配身份】
        Hero[] heroes = selectHeroes();
        for (int i = 0; i < table.getMemberCount(); i++) {
            this.players[i] = new Player(table.getMembers()[i], heroes[i]);
        }
    }

    public void initializePile() {
        if (deck == null) {
            throw new GameDeckNotSelectedException();
        }
    }

    public Deck selectDeck() {
        // TODO: 这里需要实现让用户投票决定怎样拼接牌堆
        // 目前先实现从可用的Deck[]中随机选择一个
        List<String> possibleDeckNames = getPossibleDeckNames();
        return DeckPool.getDeckByName(possibleDeckNames.get(new Random().nextInt(possibleDeckNames.size())));
    }

    public List<String> getPossibleDeckNames() {
        // TODO: 需要解决某些卡组只能用于国战的问题
        return DeckPool.getDeckNames();
    }

    public Hero[] selectHeroes() {
        // TODO: 这里需要实现从英雄池获取固定个不同的英雄给每个玩家挑选，挑选完成后才能进入下一步
        // 注意身份场需要主公先挑（包含主公英雄）
        // 国战需要注意可选项的势力问题

        // 目前先实现随机分配不同的英雄
        return HeroPool.getRandomNHeroes(table.getMemberCount());
    }

    @Override
    public String toString() {
        return String.format("Game(%s, %d)", id, table.getRoom().getMemberCount());
    }
}
