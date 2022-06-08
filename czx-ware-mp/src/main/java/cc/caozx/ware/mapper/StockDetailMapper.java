package cc.caozx.ware.mapper;

import cc.caozx.ware.entity.StockDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StockDetailMapper extends BaseMapper<StockDetail> {

    /**
     * 通过商品编码以及仓库号查询可被锁库的库存明细
     *
     * @param skuNo       商品编码
     * @param warehouseId 仓库号
     * @return 可被锁库的库存明细
     */
    List<StockDetail> findBySkuNoAndWarehouseId(
            @Param("skuNo") String skuNo,
            @Param("warehouseId") Long warehouseId);

    /**
     * 通过商品编码以及仓库号批量锁定可被锁库的库存明细
     * @param detailList 库存明细
     */
    void updateBatch(@Param("detailList") List<StockDetail> detailList);
}
