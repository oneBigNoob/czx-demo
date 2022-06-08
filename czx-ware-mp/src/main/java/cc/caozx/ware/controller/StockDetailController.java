package cc.caozx.ware.controller;

import cc.caozx.ware.entity.StockDetail;
import cc.caozx.ware.entity.dto.LockStockDTO;
import cc.caozx.ware.entity.dto.SyncStockDTO;
import cc.caozx.ware.service.StockDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/stock-detail")
public class StockDetailController {

    @Autowired
    private StockDetailService stockDetailService;

    /**
     * 批量导入库存（模拟同步库存）
     *
     * @param syncStockDTOS syncStockDTOS
     * @return OK
     */
    @PostMapping("/batchImport")
    public String batchImport(@RequestBody List<SyncStockDTO> syncStockDTOS) {
        stockDetailService.saveStockDetailAndStockCommon(syncStockDTOS);
        return "OK";
    }

    @PostMapping("/lockStock")
    public String lockStock(@RequestBody List<LockStockDTO> lockStoreDTOList) {
        long startTime = System.currentTimeMillis();
        log.info("开始时间:{}", startTime);
        stockDetailService.lockStockDetailAndStockCommon(lockStoreDTOList);
        long endTime = System.currentTimeMillis();
        log.info("结束时间:{}", endTime);
        log.info("总耗时:{}", endTime - startTime);
        return "OK";
    }
}
