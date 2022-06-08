package cc.caozx.ware.service;

import cc.caozx.ware.repository.StockRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StockRecordService {
    @Autowired
    private StockRecordRepository stockRecordRepository;
}
