package com.flance.framework.modules.ai.langchain4j.handler;

import dev.langchain4j.data.document.Document;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class MdHandler implements DocumentHandler {

    @Override
    public String getFileSuffix() { return "md"; }
    @Override
    public Document parse(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return Document.from(IOUtils.toString(is, StandardCharsets.UTF_8));
        }
    }

}
