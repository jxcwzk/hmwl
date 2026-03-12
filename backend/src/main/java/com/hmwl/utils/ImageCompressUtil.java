package com.hmwl.utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ImageCompressUtil {

    private static final long MAX_SIZE_BYTES = 2 * 1024 * 1024L;

    public static boolean needsCompression(MultipartFile file) {
        return file.getSize() > MAX_SIZE_BYTES;
    }

    public static InputStream compressImageIfNeeded(MultipartFile file) throws IOException {
        if (!needsCompression(file)) {
            return file.getInputStream();
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = getImageFormat(originalFilename);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
                .scale(1.0)
                .outputFormat(suffix)
                .outputQuality(0.7)
                .toOutputStream(outputStream);

        byte[] compressedBytes = outputStream.toByteArray();

        if (compressedBytes.length > MAX_SIZE_BYTES) {
            double quality = 0.7;
            while (compressedBytes.length > MAX_SIZE_BYTES && quality > 0.1) {
                quality -= 0.1;
                outputStream = new ByteArrayOutputStream();
                Thumbnails.of(new ByteArrayInputStream(compressedBytes))
                        .scale(1.0)
                        .outputFormat(suffix)
                        .outputQuality(quality)
                        .toOutputStream(outputStream);
                compressedBytes = outputStream.toByteArray();
            }
        }

        System.out.println("图片压缩完成，原始大小: " + file.getSize() + " bytes, 压缩后: " + compressedBytes.length + " bytes");
        return new ByteArrayInputStream(compressedBytes);
    }

    private static String getImageFormat(String filename) {
        if (filename == null) {
            return "jpg";
        }
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) {
            return "png";
        } else if (lower.endsWith(".gif")) {
            return "gif";
        } else if (lower.endsWith(".bmp")) {
            return "bmp";
        } else if (lower.endsWith(".webp")) {
            return "webp";
        }
        return "jpg";
    }
}
