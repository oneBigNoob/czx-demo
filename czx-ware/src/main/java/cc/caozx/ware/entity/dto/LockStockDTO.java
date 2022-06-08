package cc.caozx.ware.entity.dto;

import cc.caozx.ware.entity.StockDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LockStockDTO implements Serializable {
    private String orderSn;
    private Long warehouseId;
    private List<LockStockItemDTO> lockStoreItems;
    private List<StockDetail> stockDetails;
}
