package com.hmwl.service;

import com.hmwl.entity.SenderImage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SenderImageService extends IService<SenderImage> {
    /**
     * 上传发货单图片到COS
     */
    SenderImage uploadToCos(Long orderId, String orderNo, MultipartFile file) throws Exception;

    /**
     * 根据订单ID获取发货单图片列表
     */
    List<SenderImage> listByOrderId(Long orderId);

    /**
     * 删除发货单图片（包括COS文件）
     */
    boolean deleteImage(Long id) throws Exception;
}
