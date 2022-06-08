package cc.caozx.ware.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RabbitOptionalValue {

    LOCK_STOCK("ware.lock.stock.binding",
            "ware.lock.stock.queue",
            "ware.lock.stock.exchange");

    private final String bindingKey;
    private final String queueName;
    private final String exchangeName;
}
