package com.morningstar.kill.lobby;

import com.morningstar.kill.KillUser;

/**
 * 牌桌
 */
public abstract class Table {
    public abstract KillUser[] getMembers();

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
