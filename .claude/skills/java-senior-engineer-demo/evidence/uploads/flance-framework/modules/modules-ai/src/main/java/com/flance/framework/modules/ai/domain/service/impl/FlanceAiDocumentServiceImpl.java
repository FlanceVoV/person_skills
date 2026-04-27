package com.flance.framework.modules.ai.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flance.framework.common.core.exception.WebException;
import com.flance.framework.modules.ai.domain.entity.FlanceAiDocument;
import com.flance.framework.modules.ai.domain.mapper.FlanceAiDocumentMapper;
import com.flance.framework.modules.ai.domain.service.FlanceAiDocumentService;
import com.flance.framework.modules.ai.langchain4j.DocumentHandlerFactory;
import dev.langchain4j.data.document.Document;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FlanceAiDocumentServiceImpl extends ServiceImpl<FlanceAiDocumentMapper, FlanceAiDocument> implements FlanceAiDocumentService {

    @Resource
    private DocumentHandlerFactory factory;

    @Override
    public String upload(MultipartFile file) {
        try {
            Document document = factory.parse(file);

            FlanceAiDocument documentEntity = new FlanceAiDocument();
            documentEntity.preInsert("1", "1");
            documentEntity.setContent(document.text());
        } catch (Exception e) {
            throw WebException.getNormal(500, "文件解析失败");
        }
        return "";
    }
}
