package com.morningstar.kill.mq;

import com.morningstar.constant.MqConstant;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class KillMqListener {
    @RabbitListener(queues = MqConstant.topSeasonRankQueueName)
    public void updateTopPlayerRanking(String message) {
        System.out.println(message);
    }
}
