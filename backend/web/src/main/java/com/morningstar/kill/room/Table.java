package com.morningstar.kill.room;

import lombok.Data;

import java.util.Random;

/**
 * 牌桌
 * - 座位顺序与成员进入房间顺序不一致
 */
@Data
public class Table {
    private Room room;
    private Killer[] members;

    public Table(Room room){
        this.room = room;
        // 拉好椅子
        members = new Killer[room.getMemberCount()];
        // 成员上桌
        takeSeat();
    }

    /**
     * 房间中的人依次落座
     */
    private void takeSeat() {
        int i = 0;
        for (Killer member : room.getMembers()) {
            members[i++] = member;
        }
    }

    /**
     * 打乱座位次序
     */
    public void shuffleSeat() {
        Random random = new Random();

        for (int i = members.length - 1; i > 0; i--) {
            int randomIndex = random.nextInt(i + 1);
            swapSeat(i, randomIndex);
        }
    }

    private void swapSeat(int i, int j) {
        Killer temp = members[i];
        members[i] = members[j];
        members[j] = temp;
    }

    public int getMemberCount() {
        return getMembers().length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s(", this.getClass().getSimpleName()));
        for (int i = 0; i < getMembers().length; i++) {
            sb.append(String.format("%d: %s; ", i, getMembers()[i].getUser().getUsername()));
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(")");
        return sb.toString();
    }
}
