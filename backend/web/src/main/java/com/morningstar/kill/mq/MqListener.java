package com.morningstar.kill.mq;

import com.morningstar.kill.constant.MqConstant;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MqListener {
    @RabbitListener(queues = MqConstant.topSeasonRankQueueName)
    public void updateTopPlayerRanking(String message) {
        System.out.println(message);
    }
}
