package com.morningstar.config;

import com.morningstar.constant.MqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {
    /**
     * 重新定义消息序列化的方式，改为基于json格式序列化和反序列化
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public TopicExchange killExchange() {
        return new TopicExchange(MqConstant.killExchangeName, true, false);
    }

    @Bean
    public TopicExchange proxyExchange() {
        return new TopicExchange(MqConstant.proxyExchangeName, true, false);
    }


    @Bean
    public Queue topSeasonRankQueue() {
        return new Queue(MqConstant.topSeasonRankQueueName, true);
    }

    @Bean
    public Queue subSyncQueue() {
        return new Queue(MqConstant.subSyncQueueName, true);
    }


    @Bean
    public Binding bindingKillExchange() {
        return BindingBuilder.bind(topSeasonRankQueue()).to(killExchange())
                .with(MqConstant.topSeasonRankQueueRoutingKey);
    }

    @Bean
    public Binding bindingProxyExchange() {
        return BindingBuilder.bind(subSyncQueue()).to(proxyExchange())
                .with(MqConstant.subSyncQueueRoutingKey);
    }

}