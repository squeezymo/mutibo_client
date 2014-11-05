package com.squeezymo.mutibo.model;

public class Answer {
    private long id;
    private String textContent;

    /* Client meta */
    private boolean correct;
    private boolean picked;
    /* ----------- */

    public Answer(String textContent) {
        this.textContent = textContent;
    }

    public long getId() { return id; }
    public String getTextContent() { return textContent; }
    public boolean isCorrect() { return correct; }
    public boolean isPicked() { return picked; }

    public void setTextContent(String textContent) { this.textContent = textContent; }
    public void setCorrect(boolean correct) { this.correct = correct; }
    public void setPicked(boolean picked) { this.picked = picked; }

}
