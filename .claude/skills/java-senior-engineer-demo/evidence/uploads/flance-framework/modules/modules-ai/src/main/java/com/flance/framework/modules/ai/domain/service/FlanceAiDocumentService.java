package com.flance.framework.modules.ai.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flance.framework.modules.ai.domain.entity.FlanceAiDocument;
import org.springframework.web.multipart.MultipartFile;

public interface FlanceAiDocumentService extends IService<FlanceAiDocument> {

    String upload(MultipartFile file);

}
