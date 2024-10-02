package com.morningstar.kill.config;

import com.morningstar.kill.constant.MqConstant;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestMqConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test(){
        rabbitTemplate.convertAndSend(MqConstant.killExchangeName, MqConstant.topSeasonRankQueueRoutingKey,"hello");
    }
}
