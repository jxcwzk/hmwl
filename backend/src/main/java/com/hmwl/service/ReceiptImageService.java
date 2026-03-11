package com.hmwl.service;

import com.hmwl.entity.ReceiptImage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReceiptImageService extends IService<ReceiptImage> {
    /**
     * 上传回单图片到COS
     */
    ReceiptImage uploadToCos(Long orderId, String orderNo, MultipartFile file) throws Exception;

    /**
     * 根据订单ID获取回单图片列表
     */
    List<ReceiptImage> listByOrderId(Long orderId);

    /**
     * 删除回单图片（包括COS文件）
     */
    boolean deleteImage(Long id) throws Exception;
}
