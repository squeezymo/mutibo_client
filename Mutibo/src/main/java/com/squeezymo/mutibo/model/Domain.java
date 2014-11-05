package com.squeezymo.mutibo.model;

public class Domain {

    private long id;
    private String textContent;

    public Domain(String textContent) {
        this.textContent = textContent;
    }

    public long getId() { return id; }
    public String getTextContent() { return textContent; }

    public void setTextContent(String textContent) { this.textContent = textContent; }

}
