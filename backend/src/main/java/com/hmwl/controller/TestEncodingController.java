package com.hmwl.controller;

import com.hmwl.entity.BusinessRecipient;
import com.hmwl.mapper.BusinessRecipientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/test-encoding")
public class TestEncodingController {
    
    @Autowired
    private BusinessRecipientMapper businessRecipientMapper;
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public List<BusinessRecipient> test() {
        List<BusinessRecipient> recipients = businessRecipientMapper.selectByBusinessUserId(1L);
        for (BusinessRecipient recipient : recipients) {
            try {
                System.out.println("Original name: " + recipient.getName());
                System.out.println("Original address: " + recipient.getAddress());
                
                // 尝试不同的编码转换
                if (recipient.getName() != null) {
                    // 尝试从 UTF-8 转换为 ISO-8859-1
                    byte[] nameBytes = recipient.getName().getBytes("UTF-8");
                    String nameIso = new String(nameBytes, "ISO-8859-1");
                    System.out.println("UTF-8 to ISO-8859-1 name: " + nameIso);
                    
                    // 尝试从 ISO-8859-1 转换为 UTF-8
                    byte[] nameBytes2 = recipient.getName().getBytes("ISO-8859-1");
                    String nameUtf8 = new String(nameBytes2, "UTF-8");
                    System.out.println("ISO-8859-1 to UTF-8 name: " + nameUtf8);
                    
                    recipient.setName(nameUtf8);
                }
                
                if (recipient.getAddress() != null) {
                    // 尝试从 UTF-8 转换为 ISO-8859-1
                    byte[] addressBytes = recipient.getAddress().getBytes("UTF-8");
                    String addressIso = new String(addressBytes, "ISO-8859-1");
                    System.out.println("UTF-8 to ISO-8859-1 address: " + addressIso);
                    
                    // 尝试从 ISO-8859-1 转换为 UTF-8
                    byte[] addressBytes2 = recipient.getAddress().getBytes("ISO-8859-1");
                    String addressUtf8 = new String(addressBytes2, "UTF-8");
                    System.out.println("ISO-8859-1 to UTF-8 address: " + addressUtf8);
                    
                    recipient.setAddress(addressUtf8);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return recipients;
    }
}
