package cc.caozx.ware.service.impl;

import cc.caozx.ware.entity.StockCommon;
import cc.caozx.ware.mapper.StockCommonMapper;
import cc.caozx.ware.service.StockCommonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StockCommonServiceImpl extends ServiceImpl<StockCommonMapper, StockCommon> implements StockCommonService {

    @Resource
    private StockCommonMapper stockCommonMapper;


    @Override
    public int lockCommonStock(Long warehouseId, String skuNo, Integer lockedQty) {
        return stockCommonMapper.lockCommonStock(warehouseId, skuNo, lockedQty);
    }
}
