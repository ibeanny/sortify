package com.ibeanny.aisorter.model;

import java.util.List;

public class ProcessResponse {
    private List<ProcessedFileResult> files;
    private List<CombinedCategoryGroup> combinedCategories;
    private int totalFiles;
    private int totalLines;

    public ProcessResponse() {
    }

    public ProcessResponse(List<ProcessedFileResult> files,
                           List<CombinedCategoryGroup> combinedCategories,
                           int totalFiles,
                           int totalLines) {
        this.files = files;
        this.combinedCategories = combinedCategories;
        this.totalFiles = totalFiles;
        this.totalLines = totalLines;
    }

    public List<ProcessedFileResult> getFiles() {
        return files;
    }

    public void setFiles(List<ProcessedFileResult> files) {
        this.files = files;
    }

    public List<CombinedCategoryGroup> getCombinedCategories() {
        return combinedCategories;
    }

    public void setCombinedCategories(List<CombinedCategoryGroup> combinedCategories) {
        this.combinedCategories = combinedCategories;
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