package cc.caozx.ware.mapper;

import cc.caozx.ware.entity.StockCommon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface StockCommonMapper extends BaseMapper<StockCommon> {
    int lockCommonStock(
            @Param("warehouseId") Long warehouseId,
            @Param("skuNo") String skuNo,
            @Param("lockedQty") Integer lockedQty);
}
