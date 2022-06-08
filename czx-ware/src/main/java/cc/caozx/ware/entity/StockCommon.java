package cc.caozx.ware.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wms_stock_common")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class StockCommon {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku_no", length = 64, nullable = false)
    private String skuNo;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "locked_qty", nullable = false)
    private Integer lockedQty;

    @Column(name = "create_time", nullable = false)
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime create_time;

}
