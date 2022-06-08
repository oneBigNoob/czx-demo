package cc.caozx.ware.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("wms_stock_detail")
public class StockDetail {
    @TableField(value = "id")
    @TableId(type = IdType.NONE)
    private Long id;

    @TableField(value = "sku_no")
    private String skuNo;

    @TableField(value = "warehouse_id")
    private Long warehouseId;

    @TableField(value = "batch_no")
    private String batchNo;

    @TableField(value = "qty")
    private Integer qty;

    @TableField(value = "locked_qty")
    private Integer lockedQty;

    @TableField(value = "price")
    private BigDecimal price;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
