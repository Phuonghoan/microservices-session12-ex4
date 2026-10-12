package org.example.productservice.config;

import org.example.productservice.subscriber.PromotionUpdateSubscriber;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class PromotionRedisConfig {

    @Bean
    public RedisMessageListenerContainer promotionListenerContainer(
            RedisConnectionFactory connectionFactory,
            PromotionUpdateSubscriber subscriber
    ) {

        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(
                subscriber,
                new ChannelTopic("promotion-updates")
        );

        return container;
    }
}
