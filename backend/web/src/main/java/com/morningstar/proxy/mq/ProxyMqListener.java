package com.morningstar.proxy.mq;

import com.morningstar.constant.MqConstant;
import com.morningstar.proxy.pojo.po.Sub;
import com.morningstar.proxy.service.SubService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProxyMqListener {

    private final SubService subService;

    @RabbitListener(queues = MqConstant.subSyncQueueName)
    public void syncSub(Sub sub) {
        subService.sync(sub);
    }
}
