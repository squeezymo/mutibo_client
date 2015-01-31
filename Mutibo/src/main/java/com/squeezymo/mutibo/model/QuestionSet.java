package com.squeezymo.mutibo.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.*;

public class QuestionSet implements Parcelable {
    /* Client meta */
    transient private boolean answered;

    public void setAnswered(boolean answered) { this.answered = answered; }

    public boolean isAnswered() { return answered; }
    /* ----------- */

    private long id;

    private String question;
    private String explanation;

    private Set<Answer> correctAnswers;
    private Set<Answer> incorrectAnswers;

    private int ratingSum;
    private int ratingNum;

    public QuestionSet() {}

    public QuestionSet(String question, String explanation, Set<Answer> correctAnswers, Set<Answer> incorrectAnswers) {
        this.question = question;
        this.explanation = explanation;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
    }

    private QuestionSet(long id, String question, String explanation, Answer[] correctAnswers, Answer[] incorrectAnswers, int ratingSum, int ratingNum) {
        this.id = id;
        this.question = question;
        this.explanation = explanation;
        this.correctAnswers = new HashSet<Answer>(Arrays.asList(correctAnswers));
        this.incorrectAnswers = new HashSet<Answer>(Arrays.asList(incorrectAnswers));
        this.ratingSum = ratingSum;
        this.ratingNum = ratingNum;
    }

    public long getId() { return id; }
    public String getQuestion() { return question; }
    public String getExplanation() { return explanation; }
    public Set<Answer> getCorrectAnswers() { return correctAnswers; }
    public Set<Answer> getIncorrectAnswers() { return incorrectAnswers; }
    public int getRatingSum() { return ratingSum; }
    public int getRatingNum() { return ratingNum; }

    public void setId(long id) {this.id = id;}
    public void setQuestion(String question) {this.question = question;}
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public void setCorrectAnswers(Set<Answer> correctAnswers) { this.correctAnswers = correctAnswers; }
    public void setIncorrectAnswers(Set<Answer> incorrectAnswers) { this.incorrectAnswers = incorrectAnswers; }
    public void setRatingSum(int ratingSum) { this.ratingSum = ratingSum; }
    public void setRatingNum(int ratingNum) { this.ratingNum = ratingNum; }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof QuestionSet))
            return false;

        return getId() == ((QuestionSet) o).getId();
    }

    @Override
    public int hashCode() {
        return (int) getId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(question);
        dest.writeString(explanation);
        dest.writeTypedArray(correctAnswers.toArray(new Answer[correctAnswers.size()]), flags);
        dest.writeTypedArray(incorrectAnswers.toArray(new Answer[incorrectAnswers.size()]), flags);
        dest.writeInt(ratingNum);
        dest.writeInt(ratingSum);
    }

    public static final Parcelable.Creator<QuestionSet> CREATOR = new Parcelable.Creator<QuestionSet>() {
        public QuestionSet createFromParcel(Parcel in) {
            return new QuestionSet(
                    in.readLong(),
                    in.readString(),
                    in.readString(),
                    (Answer[]) in.readParcelableArray(Answer.class.getClassLoader()),
                    (Answer[]) in.readParcelableArray(Answer.class.getClassLoader()),
                    in.readInt(),
                    in.readInt()
            );
        }

        public QuestionSet[] newArray(int size) {
            return new QuestionSet[size];
        }
    };
}
