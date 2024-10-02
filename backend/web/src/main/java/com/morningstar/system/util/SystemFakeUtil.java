package com.morningstar.system.util;

import com.github.javafaker.Faker;
import com.morningstar.infra.constant.SimulationConstant;
import com.morningstar.infra.util.TimeUtil;
import com.morningstar.system.pojo.bo.Place;
import com.morningstar.system.pojo.po.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class SystemFakeUtil {
    private static final Random random = new Random();
    private static final Faker enFaker = new Faker(new Locale("en", "US"));
    private static final Faker zhFaker = new Faker(new Locale("zh", "CN"));
    private final PasswordEncoder passwordEncoder;

    public String getRandomNickname() {
        return Place.getRandomPlace().name() + zhFaker.name().fullName();
    }

    public String getRandomNickname(double latitude, double longitude) {
        return Place.getNearestPlace(latitude, longitude).name() + zhFaker.name().fullName();
    }

    public User fakeUser() {
        return fakeUser(SimulationConstant.DAY_AFTER_RELEASE, TimeUnit.DAYS);
    }

    public User fakeUser(int atMost, TimeUnit timeUnit) {
        Date dateCreated = enFaker.date().past(atMost, timeUnit);
        long timeDiff = new Date().getTime() - dateCreated.getTime();
        Date dateUpdated = new Date(dateCreated.getTime() + random.nextLong(timeDiff));

        return User
                .builder()
                .id(UUID.randomUUID())
                .username(enFaker.name().username().replace(".", ""))
                .password(passwordEncoder.encode(enFaker.internet().password()))
                .email(enFaker.internet().emailAddress())
                .nickname(getRandomNickname())
                .avatar(random.nextFloat() > 0.2 ? UUID.randomUUID() + ".jpg" : null)
                .createTime(TimeUtil.convertDateToLocalDateTime(dateCreated))
                .updateTime(TimeUtil.convertDateToLocalDateTime(dateUpdated))
                .build();
    }
}
