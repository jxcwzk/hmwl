package com.hmwl.controller;

import com.hmwl.entity.GoodsImage;
import com.hmwl.service.GoodsImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/order/goods-image")
public class GoodsImageController {

    @Autowired
    private GoodsImageService goodsImageService;

    @PostMapping(value = "/upload/cos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadToCos(@RequestParam("orderId") Long orderId,
                             @RequestParam("orderNo") String orderNo,
                             @RequestParam("file") MultipartFile file) {
        try {
            GoodsImage goodsImage = goodsImageService.uploadToCos(orderId, orderNo, file);
            return Result.success(goodsImage);
        } catch (Exception e) {
            System.out.println("上传货物图片到COS失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("上传货物图片到COS失败: " + e.getMessage());
        }
    }

    @GetMapping(value = "/list")
    public Result list(@RequestParam("orderId") Long orderId) {
        try {
            List<GoodsImage> list = goodsImageService.listByOrderId(orderId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable Long id) {
        try {
            boolean success = goodsImageService.deleteImage(id);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping(value = "/{id}")
    public Result getById(@PathVariable Long id) {
        try {
            GoodsImage goodsImage = goodsImageService.getById(id);
            return Result.success(goodsImage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
