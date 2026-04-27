package com.flance.framework.modules.ai.langchain4j.handler;

import dev.langchain4j.data.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentHandler {

    /**
     * 支持的文件后缀，如 pdf、docx、xls、txt
     */
    String getFileSuffix();

    /**
     * 解析文件
     */
    Document parse(MultipartFile file) throws IOException;

}
