package com.hmwl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmwl.entity.NetworkQuote;
import java.util.List;

public interface NetworkQuoteService extends IService<NetworkQuote> {
    void saveQuotes(Long orderId, List<NetworkQuote> quotes);
    List<NetworkQuote> getQuotesByOrderId(Long orderId);
    NetworkQuote getBestQuote(Long orderId);
    void selectBestQuote(Long orderId, Long quoteId);
}
