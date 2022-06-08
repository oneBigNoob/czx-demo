package cc.caozx.ware.repository;

import cc.caozx.ware.entity.StockRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRecordRepository extends JpaRepository<StockRecord, Long> {

}
