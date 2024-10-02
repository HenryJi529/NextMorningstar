package com.morningstar.kill.player;

import com.morningstar.kill.character.Character;
import com.morningstar.kill.room.Killer;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Player {
    private Killer user;
    private Character character;
    private Set<Status> statuses;
    private Identity identity;
    private Health health;
    private JudgmentArea judgmentArea;
    private HandArea handArea;
    private EquipmentArea equipmentArea;

    public Player(Killer user, Character character) {
        this.user = user;
        this.character = character;
        this.statuses = new HashSet<>();
        this.health = new Health(character.getBlood());
        this.judgmentArea = new JudgmentArea();
        this.handArea = new HandArea();
        this.equipmentArea = new EquipmentArea();
    }

    public Player(Killer user, Character character, Identity identity) {
        this(user, character);
        this.identity = identity;
    }
}