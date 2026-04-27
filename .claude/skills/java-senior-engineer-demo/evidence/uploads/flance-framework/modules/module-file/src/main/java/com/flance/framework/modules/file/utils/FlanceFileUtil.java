package com.flance.framework.modules.file.utils;

import org.springframework.web.multipart.MultipartFile;

public class FlanceFileUtil {

    public static String getFileSuffix(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String getFileSuffix(MultipartFile file) {
        return getFileSuffix(file.getOriginalFilename());
    }


}
