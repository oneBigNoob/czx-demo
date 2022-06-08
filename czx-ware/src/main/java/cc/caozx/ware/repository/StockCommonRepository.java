package cc.caozx.ware.repository;

import cc.caozx.ware.entity.StockCommon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockCommonRepository extends JpaRepository<StockCommon, Long> {

    StockCommon findByWarehouseIdAndSkuNo(Long warehouseId, String skuNo);

    @Modifying
    @Query("update StockCommon sc set sc.lockedQty = sc.lockedQty + :lockedQty " +
            "where sc.warehouseId = :warehouseId and sc.skuNo = :skuNo and sc.qty - sc.lockedQty > :lockedQty")
    int lockCommonStock(@Param("warehouseId") Long warehouseId,
                        @Param("skuNo") String skuNo,
                        @Param("lockedQty") Integer lockedQty);
}
