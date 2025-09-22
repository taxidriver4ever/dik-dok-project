package org.example.tiktokproject.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("direct.SendPhoto.exchange", true, false);
    }

    @Bean
    public Queue queue() {
        return new Queue("SendPhoto.direct.queue", true, false, false);
    }

    @Bean
    public Binding binding(DirectExchange directExchange, Queue queue) {
        return BindingBuilder.bind(queue).to(directExchange).with("SendPhoto");
    }

    @Bean
    public DirectExchange directOTPExchange() {
        return new DirectExchange("direct.SendOTP.exchange", true, false);
    }

    @Bean
    public Queue OTPQueue() {
        return new Queue("SendOTP.direct.queue", true, false, false);
    }

    @Bean
    public Binding bindingOTP(DirectExchange directOTPExchange, Queue OTPQueue) {
        // 修正：使用 directOTPExchange 而不是 directExchange
        return BindingBuilder.bind(OTPQueue).to(directOTPExchange).with("SendOTP");
    }

    @Bean
    public DirectExchange directIncrLikeExchange() {return new DirectExchange("direct.incrLike.exchange", true, false);}

    @Bean
    public Queue incrLikeQueue() {return new Queue("direct.incrLike.queue", true, false, false);}

    @Bean
    public Binding bindingIncrLike(DirectExchange directIncrLikeExchange, Queue incrLikeQueue) {
        return BindingBuilder.bind(incrLikeQueue).to(directIncrLikeExchange).with("IncrLike");
    }

    @Bean
    public DirectExchange directDecrLikeExchange() {return new DirectExchange("direct.decrLike.exchange", true, false);}

    @Bean
    public Queue decrLikeQueue() {return new Queue("direct.decrLike.queue", true, false, false);}

    @Bean
    public Binding bindingDecrLike(DirectExchange directDecrLikeExchange, Queue decrLikeQueue) {
        return BindingBuilder.bind(decrLikeQueue).to(directDecrLikeExchange).with("DecrLike");
    }

    @Bean
    public DirectExchange directExchangeLive() {return new DirectExchange("direct.exchange.live", true, false);}

    @Bean
    public Queue liveQueue() {return new Queue("direct.live.queue", true, false, false);}

    @Bean
    public Binding bindingLive(DirectExchange directExchangeLive, Queue liveQueue) {
        return BindingBuilder.bind(liveQueue).to(directExchangeLive).with("store_live_data");
    }

    @Bean
    public DirectExchange directExchangeDeleteLive() {return new DirectExchange("direct.exchange.liveDelete", true, false);}
    @Bean
    public Queue liveDeleteQueue() {return new Queue("direct.liveDelete.queue", true, false, false);}

    @Bean
    public Binding bindingLiveDelete(DirectExchange directExchangeDeleteLive, Queue liveDeleteQueue) {
        return BindingBuilder.bind(liveDeleteQueue).to(directExchangeDeleteLive).with("delete_live_data");
    }

    @Bean
    public MessageConverter messageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        // 设置允许的类模式
        List<String> list = new ArrayList<>();
        list.add("org.example.tiktokproject.pojo.*");
        list.add("java.util.*");
        list.add("java.lang.*");
        converter.setAllowedListPatterns(list);
        return converter;
    }
}