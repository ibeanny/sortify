package com.ibeanny.aisorter.model;

import java.util.List;

public class ProcessedFileResult {
    private String fileName;
    private List<String> discoveredCategories;
    private List<ClassifiedLine> items;

    public ProcessedFileResult() {
    }

    public ProcessedFileResult(String fileName, List<String> discoveredCategories, List<ClassifiedLine> items) {
        this.fileName = fileName;
        this.discoveredCategories = discoveredCategories;
        this.items = items;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getDiscoveredCategories() {
        return discoveredCategories;
    }

    public void setDiscoveredCategories(List<String> discoveredCategories) {
        this.discoveredCategories = discoveredCategories;
    }

    public List<ClassifiedLine> getItems() {
        return items;
    }

    public void setItems(List<ClassifiedLine> items) {
        this.items = items;
    }
}