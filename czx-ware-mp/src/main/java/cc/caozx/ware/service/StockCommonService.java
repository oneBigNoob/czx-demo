package cc.caozx.ware.service;

import cc.caozx.ware.entity.StockCommon;
import com.baomidou.mybatisplus.extension.service.IService;

public interface StockCommonService extends IService<StockCommon> {

    /**
     * 锁定库存总记录表
     *
     * @param warehouseId 仓库id
     * @param skuNo       商品编码
     * @param lockedQty   锁定数量
     * @return 锁定行数
     */
    int lockCommonStock(Long warehouseId, String skuNo, Integer lockedQty);
}
