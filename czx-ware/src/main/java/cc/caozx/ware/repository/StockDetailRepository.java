package cc.caozx.ware.repository;

import cc.caozx.ware.entity.StockDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockDetailRepository extends JpaRepository<StockDetail, Long> {

    @Query("select sd.id, sd.skuNo, sd.warehouseId, sd.batchNo, sd.qty, sd.lockedQty, sd.price, sd.createTime from StockDetail sd " +
            "where sd.skuNo = :skuNo and sd.warehouseId = :warehouseId order by :sortField asc ")
    List<StockDetail> findBySkuNoAndWarehouseIdOrderByField(
            @Param("skuNo") String skuNo,
            @Param("warehouseId") Long warehouseId,
            @Param("sortField") String sortField);

    List<StockDetail> findBySkuNoAndWarehouseId(String skuNo, Long warehouseId);
}
