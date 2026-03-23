package com.ibeanny.aisorter.model;

import java.util.List;

public class CombinedCategoryGroup {
    private String category;
    private List<String> values;

    public CombinedCategoryGroup() {
    }

    public CombinedCategoryGroup(String category, List<String> values) {
        this.category = category;
        this.values = values;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}