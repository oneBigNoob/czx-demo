package cc.caozx.ware.service.impl.base;

import cc.caozx.ware.entity.StockCommon;
import cc.caozx.ware.entity.StockDetail;
import cc.caozx.ware.entity.StockRecord;
import cc.caozx.ware.entity.dto.LockStockDTO;
import cc.caozx.ware.enums.OperationType;
import cc.caozx.ware.mapper.StockCommonMapper;
import cc.caozx.ware.mapper.StockDetailMapper;
import cc.caozx.ware.mapper.StockRecordMapper;
import cc.caozx.ware.service.StockCommonService;
import cc.caozx.ware.service.StockDetailService;
import cc.caozx.ware.service.StockRecordService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockBaseService {

    @Autowired
    private ThreadPoolExecutor executorService;

    @Resource
    private StockCommonService stockCommonService;
    @Resource
    private StockDetailService stockDetailService;
    @Resource
    private StockRecordService stockRecordService;

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
        stockDetailService.saveBatch(stockDetails);

        List<StockRecord> stockRecords = stockDetails.stream().map(stockDetail ->
                StockRecord.builder()
                        .batchNo(stockDetail.getBatchNo())
                        .stockDetailId(stockDetail.getId())
                        .skuNo(stockDetail.getSkuNo())
                        .warehouseId(stockDetail.getWarehouseId())
                        .optQty(stockDetail.getQty())
                        .optType(OperationType.ADD)
                        .lockOrderSn(OperationType.ADD.getDescription())
                        .build()).collect(Collectors.toList());
        // 插入库存操作记录
        stockRecordService.saveBatch(stockRecords);

        List<StockCommon> updateDataList = new ArrayList<>();
        List<StockCommon> insertDataList = new ArrayList<>();
        stockCommonMap.keySet().forEach(key -> {
            StockCommon stockCommon = stockCommonMap.get(key);
            StockCommon existStockCommon = stockCommonService.getOne(Wrappers.lambdaQuery(StockCommon.class)
                    .eq(StockCommon::getWarehouseId, stockCommon.getWarehouseId())
                    .eq(StockCommon::getSkuNo, stockCommon.getSkuNo()));
            if (existStockCommon != null) {
                existStockCommon.setQty(existStockCommon.getQty() + stockCommon.getQty());
                updateDataList.add(existStockCommon);
            } else {
                insertDataList.add(stockCommon);
            }
        });
        // 插入总库存记录
        stockCommonService.updateBatchById(updateDataList);
        stockCommonService.saveBatch(insertDataList);
    }

    public void lockStock(List<LockStockDTO> lockStoreDTOList) {
        List<StockDetail> updateStockDetailList = new ArrayList<>();
        List<StockRecord> insertStockRecordList = new ArrayList<>();

        for (LockStockDTO lockStockDTO : lockStoreDTOList) {
            String orderSn = lockStockDTO.getOrderSn();
            Long warehouseId = lockStockDTO.getWarehouseId();
            List<StockDetail> stockDetails = lockStockDTO.getStockDetails();
            log.info("锁库仓库：{}，锁库订单号：{}，锁库sku信息：{}", warehouseId, orderSn, stockDetails);
            for (StockDetail item : lockStockDTO.getStockDetails()) {
                List<StockDetail> existStockDetails = stockDetailService
                        .findBySkuNoAndWarehouseId(item.getSkuNo(), warehouseId);
                // 理论上不存在这个情况
                if (CollectionUtils.isEmpty(existStockDetails)) {
                    throw new RuntimeException("库存不够扣啊");
                }
                Integer existStockQty = existStockDetails.stream()
                        .map(stockDetail -> stockDetail.getQty() - stockDetail.getLockedQty())
                        .reduce(0, Integer::sum);
                if (item.getLockedQty() > existStockQty) {
                    throw new RuntimeException("库存不够扣啊");
                }
                AtomicReference<Integer> orderLockedQty = new AtomicReference<>(item.getLockedQty());
                for (StockDetail stockDetail : existStockDetails) {
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
                }
            }
            List<List<StockDetail>> batchUpdateStockDetailList = new ArrayList<>();
            int step = 1000;
            for (int i = 0; i < updateStockDetailList.size(); i += step) {
                List<StockDetail> subList = updateStockDetailList.subList(i, Math.min(i + step, updateStockDetailList.size()));
                batchUpdateStockDetailList.add(subList);
            }

//            for (List<StockDetail> detailList : batchUpdateStockDetailList) {
//                CompletableFuture.runAsync(() -> {
//                    stockDetailService.updateBatch(detailList);
//                }, executorService);
//            }

            stockDetailService.updateBatch(updateStockDetailList);

            CompletableFuture.runAsync(() -> {
                stockRecordService.saveBatch(insertStockRecordList);
            }, executorService);
        }
    }
}
