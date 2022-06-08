package cc.caozx.ware.service;

import cc.caozx.ware.entity.StockDetail;
import cc.caozx.ware.entity.dto.LockStockDTO;
import cc.caozx.ware.entity.dto.SyncStockDTO;
import cc.caozx.ware.enums.RabbitOptionalValue;
import cc.caozx.ware.repository.StockCommonRepository;
import cc.caozx.ware.repository.StockDetailRepository;
import cc.caozx.ware.repository.StockRecordRepository;
import cc.caozx.ware.service.base.StockBaseService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockDetailService {
    @Autowired
    private StockDetailRepository stockDetailRepository;
    @Autowired
    private StockCommonRepository stockCommonRepository;
    @Autowired
    private StockRecordRepository stockRecordRepository;
    @Autowired
    private StockBaseService stockBaseService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void saveStockDetailAndStockCommon(List<SyncStockDTO> syncStockDTOS) {
        log.info("导入（同步）库存数据：{}", syncStockDTOS);
        List<StockDetail> stockDetails = syncStockDTOS.stream().map(item -> {
            StockDetail stockDetail = new StockDetail();
            BeanUtils.copyProperties(item, stockDetail);
            stockDetail.setLockedQty(0);
            stockDetail.setPrice(item.getCostPrice());
            return stockDetail;
        }).collect(Collectors.toList());
        // 插入库存
        stockBaseService.addStock(stockDetails);

    }

    @Transactional
    public void lockStockDetailAndStockCommon(List<LockStockDTO> lockStoreDTOList) {
        lockStoreDTOList.forEach(lockStockDTO -> {
            Long warehouseId = lockStockDTO.getWarehouseId();
            String orderSn = lockStockDTO.getOrderSn();
            List<StockDetail> stockDetails = lockStockDTO.getLockStoreItems().stream().map(item ->
                    StockDetail.builder()
                            .skuNo(item.getSkuNo())
                            .lockedQty(item.getQty())
                            .build()).collect(Collectors.toList());

            log.info("锁库仓库：{}，锁库订单号：{}，锁库sku信息：{}", warehouseId, orderSn, stockDetails);
            stockDetails.forEach(stockDetail -> {
                int rows = stockCommonRepository
                        .lockCommonStock(warehouseId, stockDetail.getSkuNo(), stockDetail.getLockedQty());
                if (rows == 0) {
                    throw new RuntimeException("库存锁定失败");
                }
            });
            lockStockDTO.setStockDetails(stockDetails);
        });

        log.info("发送到队列中的锁库明细：{}", lockStoreDTOList);
        rabbitTemplate.convertAndSend(RabbitOptionalValue.LOCK_STOCK.getExchangeName(),
                RabbitOptionalValue.LOCK_STOCK.getBindingKey(), JSON.toJSONString(lockStoreDTOList));

    }
}
