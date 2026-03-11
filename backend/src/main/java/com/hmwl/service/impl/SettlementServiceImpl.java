package com.hmwl.service.impl;

import com.hmwl.entity.Settlement;
import com.hmwl.mapper.SettlementMapper;
import com.hmwl.service.SettlementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SettlementServiceImpl extends ServiceImpl<SettlementMapper, Settlement> implements SettlementService {
}
