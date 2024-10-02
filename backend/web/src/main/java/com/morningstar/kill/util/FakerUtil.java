package com.morningstar.kill.util;

import com.github.javafaker.Faker;
import com.morningstar.kill.dao.mapper.UserMapper;
import com.morningstar.kill.domain.game.GameLog;
import com.morningstar.kill.domain.game.Mode;
import com.morningstar.kill.pojo.bo.Place;
import com.morningstar.kill.pojo.po.User;
import com.morningstar.kill.pojo.po.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Component
public class FakerUtil {
    private static final Faker zhFaker = new Faker(new Locale("zh", "CN"));
    private static final Faker enFaker = new Faker(new Locale("en", "US"));
    private final Random random = new Random();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    public String getRandomNickname(){
        return Place.getRandomPlace().name() + zhFaker.name().fullName();
    }

    public String getRandomNickname(double latitude, double longitude){
        return Place.getNearestPlace(latitude, longitude).name() + zhFaker.name().fullName();
    }

    public User fakeUser(){
        // NOTE: 默认系统上线100天
        return fakeUser(100, TimeUnit.DAYS);
    }

    public User fakeUser(int createdLeftMost, TimeUnit timeUnit){
        Date dateCreated = zhFaker.date().past(createdLeftMost, timeUnit);
        long timeDiff = new Date().getTime() - dateCreated.getTime();
        Date dateUpdated = new Date(dateCreated.getTime() + random.nextLong(timeDiff));

        return User
                .builder()
                .id(UUID.randomUUID())
                .username(enFaker.name().username().replace(".", ""))
                .password(passwordEncoder.encode(enFaker.internet().password()))
                .email(enFaker.internet().emailAddress())
                .phone(zhFaker.phoneNumber().cellPhone())
                .nickname(getRandomNickname())
                .sex(new Random().nextFloat() > 0.5? "男": "女")
                .avatar(new Random().nextFloat() > 0.2 ? UUID.randomUUID() + ".jpg": null)
                .createTime(new Timestamp(dateCreated.getTime()))
                .updateTime(new Timestamp(dateUpdated.getTime()))
                .build();
    }

    public Record fakeRecord(){
        // 默认最迟创建时间只考虑游戏中玩家的最晚创建时间
        return fakeRecord(0);
    }

    public Record fakeRecord(long leftMostCreated){
        // 确定玩家人数
        Mode mode = Mode.getRandomMode();
        int minHeadNum = mode.getMinHeadNum();
        int maxHeadNum = mode.getMaxHeadNum();
        int headNum = minHeadNum != maxHeadNum ? random.nextInt(maxHeadNum - minHeadNum) + minHeadNum: minHeadNum;
        List<User> users = userMapper.selectRandomN(headNum);
        List<User> winners = users.stream().limit((headNum > 2 ? random.nextInt(headNum-2): 0) + 1).toList(); // NOTE: 赢家数难确定(当前设置为1~N-1)
        GameLog gameLog = new GameLog(mode, users, winners);

        // 游戏开始时间应要迟于最晚创建的用户的创建时间
        Timestamp lastUserCreateTime = users.stream().map(User::getCreateTime).max(Comparator.comparingLong(Timestamp::getTime)).orElse(new Timestamp(new Date().getTime()-1000));
        leftMostCreated = Math.max(lastUserCreateTime.getTime(), leftMostCreated);
        long timeDiff = System.currentTimeMillis() - leftMostCreated;
        Date dateCreated = new Date(random.nextLong(timeDiff) + leftMostCreated);

        // 游戏时长一般不会超过一小时, 且结束时间不会超过当前时间
        Date now = new Date();
        Date dateUpdated = new Date(dateCreated.getTime() + random.nextLong(60 * 60 * 1000));
        dateUpdated = dateUpdated.getTime() > now.getTime() ? now : dateUpdated;

        return Record.builder()
                .id(UUID.randomUUID())
                .content(gameLog)
                .createTime(new Timestamp(dateCreated.getTime()))
                .updateTime(new Timestamp(dateUpdated.getTime()))
                .build();
    }
}
