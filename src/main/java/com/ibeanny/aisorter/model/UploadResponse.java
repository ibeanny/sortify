package com.ibeanny.aisorter.model;

import java.util.List;

public class UploadResponse {
    private List<UploadedFileResult> files;
    private int totalFiles;
    private int totalLines;

    public UploadResponse() {
    }

    public UploadResponse(List<UploadedFileResult> files, int totalFiles, int totalLines) {
        this.files = files;
        this.totalFiles = totalFiles;
        this.totalLines = totalLines;
    }

    public List<UploadedFileResult> getFiles() {
        return files;
    }

    public void setFiles(List<UploadedFileResult> files) {
        this.files = files;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public int getTotalLines() {
        return totalLines;
    }

    public void setTotalLines(int totalLines) {
        this.totalLines = totalLines;
    }
}