package com.flance.framework.modules.ai.langchain4j.handler;

import dev.langchain4j.data.document.Document;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class DocHandler implements DocumentHandler {

    @Override
    public String getFileSuffix() {
        return "doc";
    }

    @Override
    public Document parse(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             HWPFDocument doc = new HWPFDocument(is);
             WordExtractor extractor = new WordExtractor(doc)) {

            String text = extractor.getText();
            return Document.from(text);
        }
    }

}
