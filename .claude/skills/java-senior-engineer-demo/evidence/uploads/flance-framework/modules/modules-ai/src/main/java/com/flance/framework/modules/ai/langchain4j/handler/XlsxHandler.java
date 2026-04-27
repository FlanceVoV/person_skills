package com.flance.framework.modules.ai.langchain4j.handler;

import dev.langchain4j.data.document.Document;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class XlsxHandler implements DocumentHandler {

    @Override
    public String getFileSuffix() {
        return "xlsx";
    }

    @Override
    public Document parse(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            StringBuilder sb = new StringBuilder();
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        String value = getCellValue(cell);
                        if (!value.isBlank()) {
                            sb.append(value).append("\t");
                        }
                    }
                    sb.append("\n");
                }
            }
            return Document.from(sb.toString());
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

}
