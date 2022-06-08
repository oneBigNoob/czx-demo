package cc.caozx.ware.listener;

import cc.caozx.ware.entity.dto.LockStockDTO;
import cc.caozx.ware.service.base.StockBaseService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RabbitListener(queues = "ware.lock.stock.queue")
@Slf4j
public class LockStockListener {

    @Autowired
    private StockBaseService stockBaseService;

    @RabbitHandler
    public void listenerLockStock(String jsonString, Channel channel, Message message) {
        List<LockStockDTO> lockStockDTO = JSON.parseObject(jsonString, new TypeReference<List<LockStockDTO>>() {
        });
        log.info("监听到的锁库明细：{}", lockStockDTO);

        try {
            stockBaseService.lockStock(lockStockDTO);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.error("锁定库存明细发生异常：", e);
        }
    }

}
