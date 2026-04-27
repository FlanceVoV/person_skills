package com.flance.framework.modules.ai.langchain4j;

import com.flance.framework.modules.ai.langchain4j.handler.DocumentHandler;
import dev.langchain4j.data.document.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DocumentHandlerFactory {

    private final Map<String, DocumentHandler> handlerMap = new ConcurrentHashMap<>();

    /**
     * 自动注入所有处理器
     */
    public DocumentHandlerFactory(Map<String, DocumentHandler> handlers) {
        for (DocumentHandler handler : handlers.values()) {
            handlerMap.put(handler.getFileSuffix().toLowerCase(), handler);
        }
    }

    public DocumentHandler getHandler(String suffix) {
        DocumentHandler handler = handlerMap.get(suffix.toLowerCase());
        if (handler == null) {
            throw new IllegalArgumentException("不支持的文件类型：" + suffix);
        }
        return handler;
    }

    public Document parse(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String suffix = getSuffix(fileName);
        return getHandler(suffix).parse(file);
    }

    private String getSuffix(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("文件格式错误");
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

}
