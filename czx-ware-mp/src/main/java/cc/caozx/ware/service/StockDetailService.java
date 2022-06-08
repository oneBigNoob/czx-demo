package cc.caozx.ware.service;

import cc.caozx.ware.entity.StockDetail;
import cc.caozx.ware.entity.dto.LockStockDTO;
import cc.caozx.ware.entity.dto.SyncStockDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface StockDetailService extends IService<StockDetail> {

    /**
     * 新增库存明细以及总库存数
     *
     * @param syncStockDTOS 同步库存数据
     */
    void saveStockDetailAndStockCommon(List<SyncStockDTO> syncStockDTOS);

    /**
     * 锁定库存明细以及总库存数
     *
     * @param lockStoreDTOList 锁定库存数据
     */
    void lockStockDetailAndStockCommon(List<LockStockDTO> lockStoreDTOList);

    /**
     * 通过商品编码以及仓库号查询可被锁库的库存明细
     *
     * @param skuNo       商品编码
     * @param warehouseId 仓库号
     * @return 可被锁库的库存明细
     */
    List<StockDetail> findBySkuNoAndWarehouseId(String skuNo, Long warehouseId);

    /**
     * 批量锁定库存明细
     * @param detailList 库存明细
     */
    void updateBatch(List<StockDetail> detailList);
}
