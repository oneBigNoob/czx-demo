package cc.caozx.ware.entity;

import cc.caozx.ware.converter.OperationTypeConverter;
import cc.caozx.ware.enums.OperationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wms_stock_record")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class StockRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_no", length = 64, nullable = false)
    private String batchNo;

    @Column(name = "stock_detail_id", nullable = false)
    private Long stockDetailId;

    @Column(name = "sku_no", length = 64, nullable = false)
    private String skuNo;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "opt_type", nullable = false)
    @Convert(converter = OperationTypeConverter.class)
    private OperationType optType;

    @Column(name = "opt_qty", nullable = false)
    private Integer optQty;

    @Column(name = "lock_order_sn", length = 64, nullable = false)
    private String lockOrderSn;

    @Column(name = "create_time", nullable = false)
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}


