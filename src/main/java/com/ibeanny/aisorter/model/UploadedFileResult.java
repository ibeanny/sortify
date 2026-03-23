package com.ibeanny.aisorter.model;

import java.util.List;

public class UploadedFileResult {
    private String fileName;
    private List<String> lines;
    private int lineCount;

    public UploadedFileResult() {
    }

    public UploadedFileResult(String fileName, List<String> lines, int lineCount) {
        this.fileName = fileName;
        this.lines = lines;
        this.lineCount = lineCount;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }
}