package cc.caozx.ware.entity;

import cc.caozx.ware.enums.OperationType;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wms_stock_record")
public class StockRecord {

    @TableField(value = "id")
    @TableId(type = IdType.NONE)
    private Long id;

    @TableField(value = "batch_no")
    private String batchNo;

    @TableField(value = "stock_detail_id")
    private Long stockDetailId;

    @TableField(value = "sku_no")
    private String skuNo;

    @TableField(value = "warehouse_id")
    private Long warehouseId;

    @TableField(value = "opt_type")
    private OperationType optType;

    @TableField(value = "opt_qty")
    private Integer optQty;

    @TableField(value = "lock_order_sn")
    private String lockOrderSn;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
