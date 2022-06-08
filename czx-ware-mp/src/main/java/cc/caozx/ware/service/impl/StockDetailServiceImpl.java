package cc.caozx.ware.service.impl;

import cc.caozx.ware.entity.StockDetail;
import cc.caozx.ware.entity.dto.LockStockDTO;
import cc.caozx.ware.entity.dto.SyncStockDTO;
import cc.caozx.ware.enums.RabbitOptionalValue;
import cc.caozx.ware.mapper.StockDetailMapper;
import cc.caozx.ware.service.StockCommonService;
import cc.caozx.ware.service.StockDetailService;
import cc.caozx.ware.service.impl.base.StockBaseService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockDetailServiceImpl extends ServiceImpl<StockDetailMapper, StockDetail> implements StockDetailService {

    @Autowired
    private StockBaseService stockBaseService;
    @Autowired
    private StockCommonService stockCommonService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Resource
    private StockDetailMapper stockDetailMapper;

    private static final String STOCK_LOCK = "stock:lock:";

    @Override
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

    @Override
    @Transactional(rollbackFor = Exception.class)
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
                RLock lock = redissonClient.getLock(STOCK_LOCK + stockDetail.getSkuNo());
                lock.lock(1, TimeUnit.SECONDS);
                int rows = 0;
                try {
                    rows = stockCommonService
                            .lockCommonStock(warehouseId, stockDetail.getSkuNo(), stockDetail.getLockedQty());
                } finally {
                    lock.unlock();
                }
                if (rows == 0) {
                    throw new RuntimeException("库存数量不足，库存锁定失败");
                }
            });
            lockStockDTO.setStockDetails(stockDetails);
        });
        log.info("发送到队列中的锁库明细：{}", lockStoreDTOList);
        rabbitTemplate.convertAndSend(RabbitOptionalValue.LOCK_STOCK.getExchangeName(),
                RabbitOptionalValue.LOCK_STOCK.getBindingKey(), JSON.toJSONString(lockStoreDTOList));
    }

    @Override
    public List<StockDetail> findBySkuNoAndWarehouseId(String skuNo, Long warehouseId) {
        return stockDetailMapper.findBySkuNoAndWarehouseId(skuNo, warehouseId);
    }

    @Override
    public void updateBatch(List<StockDetail> detailList) {
        stockDetailMapper.updateBatch(detailList);
    }
}
