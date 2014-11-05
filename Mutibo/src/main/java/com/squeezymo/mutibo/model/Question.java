package com.squeezymo.mutibo.model;

public class Question {

    private long id;
    private String textContent;

    public Question(String textContent) {
        this.textContent = textContent;
    }

    public long getId() { return id; }
    public String getTextContent() { return textContent; }

    public void setTextContent(String textContent) { this.textContent = textContent; }

}
