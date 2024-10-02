package com.morningstar.kill.domain.player;

import com.morningstar.kill.domain.hero.Hero;
import com.morningstar.kill.pojo.po.User;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Player {
    private User user;
    private Hero hero;
    private Set<Status> statuses;
    private Identity identity;
    private Health health;
    private JudgmentArea judgmentArea;
    private HandArea handArea;
    private EquipmentArea equipmentArea;

    public Player(User user, Hero hero){
        this.user = user;
        this.hero = hero;
        this.statuses = new HashSet<>();
        this.health = new Health(hero.getBlood());
        this.judgmentArea = new JudgmentArea();
        this.handArea = new HandArea();
        this.equipmentArea = new EquipmentArea();
    }

    public Player(User user, Hero hero, Identity identity){
        this(user, hero);
        this.identity = identity;
    }
}