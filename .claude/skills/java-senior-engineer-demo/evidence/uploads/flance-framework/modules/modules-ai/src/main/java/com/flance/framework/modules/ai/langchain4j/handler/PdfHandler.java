package com.flance.framework.modules.ai.langchain4j.handler;

import dev.langchain4j.data.document.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PdfHandler implements DocumentHandler {

    @Override
    public String getFileSuffix() {
        return "pdf";
    }

    @Override
    public Document parse(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             PDDocument pdDocument = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdDocument);
            return Document.from(text);
        }
    }
}
