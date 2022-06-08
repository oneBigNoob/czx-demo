package cc.caozx.ware.service.base;

import cc.caozx.ware.entity.StockCommon;
import cc.caozx.ware.entity.StockDetail;
import cc.caozx.ware.entity.StockRecord;
import cc.caozx.ware.entity.dto.LockStockDTO;
import cc.caozx.ware.enums.OperationType;
import cc.caozx.ware.repository.StockCommonRepository;
import cc.caozx.ware.repository.StockDetailRepository;
import cc.caozx.ware.repository.StockRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockBaseService {

    @Autowired
    private StockDetailRepository stockDetailRepository;
    @Autowired
    private StockCommonRepository stockCommonRepository;
    @Autowired
    private StockRecordRepository stockRecordRepository;



    @Transactional(rollbackFor = Exception.class)
    public void addStock(List<StockDetail> stockDetails) {
        log.info("开始处理新增的库存明细数据：{}", stockDetails);
        Map<String, StockCommon> stockCommonMap = new HashMap<>();
        stockDetails.forEach(stockDetail -> {
            StockCommon stockCommon = new StockCommon();
            BeanUtils.copyProperties(stockDetail, stockCommon);
            // 针对同一个仓库下的同一个订货号的数量进行汇总
            String key = stockCommon.getWarehouseId() + stockCommon.getSkuNo();
            if (stockCommonMap.containsKey(key)) {
                StockCommon existStockCommon = stockCommonMap.get(key);
                existStockCommon.setQty(existStockCommon.getQty() + stockCommon.getQty());
                stockCommonMap.put(key, existStockCommon);
            } else {
                stockCommonMap.put(key, stockCommon);
            }
        });
        // 插入库存明细
        List<StockDetail> insertStockDetails = stockDetailRepository.saveAll(stockDetails);
        log.info("更新的库存明细数据：{}", insertStockDetails);

        // 插入库存操作记录
        List<StockRecord> stockRecords = insertStockDetails.stream().map(stockDetail ->
                StockRecord.builder()
                        .batchNo(stockDetail.getBatchNo())
                        .stockDetailId(stockDetail.getId())
                        .skuNo(stockDetail.getSkuNo())
                        .warehouseId(stockDetail.getWarehouseId())
                        .optQty(stockDetail.getQty())
                        .optType(OperationType.ADD)
                        .lockOrderSn(OperationType.ADD.getDescription())
                        .build()).collect(Collectors.toList());
        log.info("开始处理新增的库存操作记录数据：{}", stockRecords);
        List<StockRecord> insertStockRecord = stockRecordRepository.saveAll(stockRecords);
        log.info("更新的库存操作记录数据：{}", insertStockRecord);

        // 插入总库存
        log.info("开始处理新增的总库存数据：{}", stockCommonMap);
        List<StockCommon> updateDataList = new ArrayList<>();
        List<StockCommon> insertDataList = new ArrayList<>();
        stockCommonMap.keySet().forEach(key -> {
            StockCommon stockCommon = stockCommonMap.get(key);
            StockCommon existStockCommon = stockCommonRepository
                    .findByWarehouseIdAndSkuNo(stockCommon.getWarehouseId(), stockCommon.getSkuNo());
            if (existStockCommon != null) {
                existStockCommon.setQty(existStockCommon.getQty() + stockCommon.getQty());
                updateDataList.add(existStockCommon);
            } else {
                insertDataList.add(stockCommon);
            }
        });
        List<StockCommon> updateStockCommons = stockCommonRepository.saveAll(updateDataList);
        log.info("更新的总库存数据：{}", updateStockCommons);
        List<StockCommon> insertStockCommons = stockCommonRepository.saveAll(insertDataList);
        log.info("新插入的总库存数据：{}", insertStockCommons);
    }

    @Transactional(rollbackFor = Exception.class)
    public void lockStock(List<LockStockDTO> lockStoreDTOList) {
        for (LockStockDTO lockStockDTO : lockStoreDTOList) {
            String orderSn = lockStockDTO.getOrderSn();
            Long warehouseId = lockStockDTO.getWarehouseId();
            List<StockDetail> stockDetails = lockStockDTO.getStockDetails();
            log.info("锁库仓库：{}，锁库订单号：{}，锁库sku信息：{}", warehouseId, orderSn, stockDetails);
            List<StockDetail> updateStockDetailList = new ArrayList<>();
            List<StockRecord> insertStockRecordList = new ArrayList<>();
            for (StockDetail item : lockStockDTO.getStockDetails()) {
                List<StockDetail> existStockDetails = stockDetailRepository.findBySkuNoAndWarehouseId(item.getSkuNo(), warehouseId);
                AtomicReference<Integer> orderLockedQty = new AtomicReference<>(item.getLockedQty());
                for (int i = 0; i < existStockDetails.size(); i++) {
                    StockDetail stockDetail = existStockDetails.get(i);
                    StockRecord record = new StockRecord();
                    record.setBatchNo(stockDetail.getBatchNo());
                    record.setStockDetailId(stockDetail.getId());
                    record.setSkuNo(stockDetail.getSkuNo());
                    record.setWarehouseId(stockDetail.getWarehouseId());
                    record.setOptType(OperationType.LOCK);
                    record.setLockOrderSn(orderSn);
                    if (orderLockedQty.get() > stockDetail.getQty() - stockDetail.getLockedQty()) {
                        Integer optQty = stockDetail.getQty() - stockDetail.getLockedQty();
                        orderLockedQty.updateAndGet(v -> v - optQty);
                        stockDetail.setLockedQty(stockDetail.getLockedQty() + optQty);
                        // 封装库存操作记录中的操作数量
                        record.setOptQty(optQty);
                    } else {
                        Integer lockedQty = orderLockedQty.getAndSet(0);
                        stockDetail.setLockedQty(stockDetail.getLockedQty() + lockedQty);
                        // 封装库存操作记录中的操作数量
                        record.setOptQty(lockedQty);
                    }
                    insertStockRecordList.add(record);
                    updateStockDetailList.add(stockDetail);
                    if (orderLockedQty.get() <= 0) {
                        break;
                    }
                    // 这种情况理论上不存在 完美中的完美 爆炸中的爆炸
                    if (i == existStockDetails.size() - 1 && orderLockedQty.get() > 0) {
                        throw new RuntimeException("库存没扣够啊");
                    }
                }
            }
            log.info("更新库存明细锁库记录：{}", updateStockDetailList);
            stockDetailRepository.saveAll(updateStockDetailList);
            log.info("插入库存操作记录数据：{}", insertStockRecordList);
            stockRecordRepository.saveAll(insertStockRecordList);
        }
    }
}
