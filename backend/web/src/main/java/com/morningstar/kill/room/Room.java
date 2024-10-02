package com.morningstar.kill.room;

import com.morningstar.kill.exception.UserAlreadyInRoomException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

/**
 * 房间
 * - 由用户手动建立
 * - 可以由房主控制直接开启游戏
 * - 也可以拼桌由系统开启游戏
 */
@Data
public class Room {
    @Schema(description = "房间id")
    private UUID id;

    @Schema(description = "成员队列")
    private Deque<Killer> members; // 双向队列方便选房主

    @Schema(description = "是否锁定")
    private boolean isLocked;

    public Room(Killer owner) {
        this.id = UUID.randomUUID();
        owner.setRoom(this);
        this.members = new ArrayDeque<>();
        this.members.offerFirst(owner);
        Lobby.registerRoom(this);
    }

    public Room(Killer owner, Deque<Killer> members) {
        this(owner);
        this.members.addAll(members);
        for (Killer member : members) {
            member.setRoom(this);
        }
    }

    public Killer getOwner() {
        return members.peekFirst();
    }

    public int getMemberCount() {
        return members.size();
    }

    public void addMember(Killer member) {
        if (member.getRoom() != null) {
            throw new UserAlreadyInRoomException();
        }
        this.members.offer(member);
        member.setRoom(this);
    }

    public void removeMember(Killer member) {
        if (member.equals(getOwner())) {
            members.pollFirst();
            if (this.members.isEmpty()) {
                Lobby.deleteRoom(this.id);
            }
        } else {
            this.members.remove(member);
        }
        member.setRoom(null);
    }

    public void close() {
        for (Killer member : members) {
            member.setRoom(null);
        }
        Lobby.deleteRoom(this.id);
    }

    @Override
    public String toString() {
        return String.format("Room(%s, %d)", id, members.size());
    }
}
