package cc.caozx.ware.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SyncStockDTO implements Serializable {
    private String skuNo;
    private Long warehouseId;
    private Integer qty;
    private BigDecimal costPrice;
    private String batchNo;
}
