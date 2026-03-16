package com.hmwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmwl.entity.NetworkQuote;
import com.hmwl.mapper.NetworkQuoteMapper;
import com.hmwl.service.NetworkQuoteService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NetworkQuoteServiceImpl extends ServiceImpl<NetworkQuoteMapper, NetworkQuote> implements NetworkQuoteService {
    
    @Override
    public void saveQuotes(Long orderId, List<NetworkQuote> quotes) {
        for (NetworkQuote quote : quotes) {
            quote.setOrderId(orderId);
            this.save(quote);
        }
    }
    
    @Override
    public List<NetworkQuote> getQuotesByOrderId(Long orderId) {
        LambdaQueryWrapper<NetworkQuote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NetworkQuote::getOrderId, orderId);
        return this.list(wrapper);
    }
    
    @Override
    public NetworkQuote getBestQuote(Long orderId) {
        LambdaQueryWrapper<NetworkQuote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NetworkQuote::getOrderId, orderId);
        wrapper.eq(NetworkQuote::getStatus, 1);
        wrapper.orderByAsc(NetworkQuote::getFinalPrice);
        wrapper.last("LIMIT 1");
        return this.getOne(wrapper);
    }
    
    @Override
    public void selectBestQuote(Long orderId, Long quoteId) {
        LambdaQueryWrapper<NetworkQuote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NetworkQuote::getOrderId, orderId);
        
        List<NetworkQuote> quotes = this.list(wrapper);
        for (NetworkQuote quote : quotes) {
            if (quote.getId().equals(quoteId)) {
                quote.setStatus(2);
            } else {
                quote.setStatus(0);
            }
            this.updateById(quote);
        }
    }
}
