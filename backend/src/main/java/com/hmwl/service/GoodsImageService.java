package com.hmwl.service;

import com.hmwl.entity.GoodsImage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GoodsImageService extends IService<GoodsImage> {
    /**
     * 上传货物图片到COS
     */
    GoodsImage uploadToCos(Long orderId, String orderNo, MultipartFile file) throws Exception;

    /**
     * 根据订单ID获取货物图片列表
     */
    List<GoodsImage> listByOrderId(Long orderId);

    /**
     * 删除货物图片（包括COS文件）
     */
    boolean deleteImage(Long id) throws Exception;
}
