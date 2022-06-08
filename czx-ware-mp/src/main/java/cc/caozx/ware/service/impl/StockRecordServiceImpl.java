package cc.caozx.ware.service.impl;

import cc.caozx.ware.entity.StockRecord;
import cc.caozx.ware.mapper.StockRecordMapper;
import cc.caozx.ware.service.StockRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class StockRecordServiceImpl extends ServiceImpl<StockRecordMapper, StockRecord> implements StockRecordService {
}
