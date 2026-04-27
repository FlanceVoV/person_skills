package com.flance.framework.modules.file.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flance.framework.modules.file.domain.entity.FlanceFile;
import org.springframework.web.multipart.MultipartFile;

public interface FlanceFileService extends IService<FlanceFile> {

    String upload(MultipartFile file);

}
