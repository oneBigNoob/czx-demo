package cc.caozx.ware.config;

import cc.caozx.ware.enums.RabbitOptionalValue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue wareLockStockQueue() {
        return new Queue(
                RabbitOptionalValue.LOCK_STOCK.getQueueName(),
                true, false, false);
    }

    @Bean
    public Exchange wareLockStockExchange() {
        return new TopicExchange(
                RabbitOptionalValue.LOCK_STOCK.getExchangeName(),
                true, false, null);
    }

    @Bean
    public Binding wareLockStockUpBinding() {
        return new Binding(RabbitOptionalValue.LOCK_STOCK.getQueueName(),
                Binding.DestinationType.QUEUE,
                RabbitOptionalValue.LOCK_STOCK.getExchangeName(),
                RabbitOptionalValue.LOCK_STOCK.getBindingKey(),
                null);
    }

}
