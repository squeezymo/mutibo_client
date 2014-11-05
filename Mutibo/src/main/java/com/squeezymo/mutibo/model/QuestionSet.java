package com.squeezymo.mutibo.model;

import java.util.*;

public class QuestionSet {

    private long id;
    private Domain domain;
    private Question question;
    private Set<Answer> correctAnswers;
    private Set<Answer> incorrectAnswers;
    private String explanation;

    /* Client meta */
    public enum State {UNCONFIRMED, CONFIRMED};
    private State state;
    /* ----------- */

    public QuestionSet(Domain domain, Question question, String explanation, Set<Answer> correctAnswers, Set<Answer> incorrectAnswers) {
        this.domain = domain;
        this.question = question;
        this.explanation = explanation;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
    }

    public long getId() { return id; }
    public Domain getDomain() { return domain; }
    public Question getQuestion() { return question; }
    public String getExplanation() { return explanation; }
    public Set<Answer> getCorrectAnswers() { return correctAnswers; }
    public Set<Answer> getIncorrectAnswers() { return incorrectAnswers; }
    public State getState() { return state; }

    public void setDomain(Domain domain) { this.domain = domain; }
    public void setQuestion(Question question) {this.question = question;}
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public void setCorrectAnswers(Set<Answer> correctAnswers) { this.correctAnswers = correctAnswers; }
    public void setIncorrectAnswers(Set<Answer> incorrectAnswers) { this.incorrectAnswers = incorrectAnswers; }
    public void setState(State state) { this.state = state; }

}
