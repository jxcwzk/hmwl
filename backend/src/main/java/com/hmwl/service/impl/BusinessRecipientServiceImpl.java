package com.hmwl.service.impl;

import com.hmwl.entity.BusinessRecipient;
import com.hmwl.mapper.BusinessRecipientMapper;
import com.hmwl.service.BusinessRecipientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.io.UnsupportedEncodingException;
import java.io.Serializable;

@Service
public class BusinessRecipientServiceImpl extends ServiceImpl<BusinessRecipientMapper, BusinessRecipient> implements BusinessRecipientService {
    @Override
    public List<BusinessRecipient> listByBusinessUserId(Long businessUserId) {
        List<BusinessRecipient> recipients = baseMapper.selectByBusinessUserId(businessUserId);
        return convertEncoding(recipients);
    }
    
    @Override
    public List<BusinessRecipient> list() {
        List<BusinessRecipient> recipients = super.list();
        return convertEncoding(recipients);
    }
    
    @Override
    public BusinessRecipient getById(Serializable id) {
        BusinessRecipient recipient = super.getById(id);
        if (recipient != null) {
            try {
                if (recipient.getName() != null) {
                    // 尝试直接返回，看看是否是编码转换的问题
                    System.out.println("Direct name: " + recipient.getName());
                }
                if (recipient.getAddress() != null) {
                    System.out.println("Direct address: " + recipient.getAddress());
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return recipient;
    }
    
    private List<BusinessRecipient> convertEncoding(List<BusinessRecipient> recipients) {
        for (BusinessRecipient recipient : recipients) {
            try {
                if (recipient.getName() != null) {
                    System.out.println("Direct name: " + recipient.getName());
                }
                if (recipient.getAddress() != null) {
                    System.out.println("Direct address: " + recipient.getAddress());
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return recipients;
    }
}