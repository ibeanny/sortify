package com.ibeanny.aisorter.model;

public class ClassifiedLine {
    private String originalText;
    private String category;
    private String value;
    private double confidence;

    public ClassifiedLine() {
    }

    public ClassifiedLine(String originalText, String category, String value, double confidence) {
        this.originalText = originalText;
        this.category = category;
        this.value = value;
        this.confidence = confidence;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}