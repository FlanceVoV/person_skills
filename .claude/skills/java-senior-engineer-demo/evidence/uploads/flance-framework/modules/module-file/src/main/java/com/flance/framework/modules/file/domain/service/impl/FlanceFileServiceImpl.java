package com.flance.framework.modules.file.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flance.framework.common.core.exception.WebException;
import com.flance.framework.modules.file.config.FlanceFileConfig;
import com.flance.framework.modules.file.domain.entity.FlanceFile;
import com.flance.framework.modules.file.domain.mapper.FlanceFileMapper;
import com.flance.framework.modules.file.domain.service.FlanceFileService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FlanceFileServiceImpl extends ServiceImpl<FlanceFileMapper, FlanceFile> implements FlanceFileService {

    @Resource
    FlanceFileConfig flanceFileConfig;

    @Override
    public String upload(MultipartFile file) {
        try {

        } catch (Exception e) {
            throw WebException.getNormal(500, "文件解析失败");
        }
        return "";
    }

}
