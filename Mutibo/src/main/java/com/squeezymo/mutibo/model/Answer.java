package com.squeezymo.mutibo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Set;

public class Answer implements Parcelable {
    /* Client meta */
    transient private boolean correct;
    transient private boolean picked;

    public void setCorrect(boolean correct) { this.correct = correct; }
    public void setPicked(boolean picked) { this.picked = picked; }

    public boolean isCorrect() { return correct; }
    public boolean isPicked() { return picked; }
    /* ----------- */

    private long id;
    private String textContent;

    public Answer() {}

    public Answer(String textContent) {
        this.textContent = textContent;
    }

    private Answer(long id, boolean correct, boolean picked, String textContent) {
        this.id = id;
        this.correct = correct;
        this.picked = picked;
        this.textContent = textContent;
    }

    public long getId() { return id; }
    public String getTextContent() { return textContent; }

    public void setId(long id) {this.id = id;}
    public void setTextContent(String textContent) { this.textContent = textContent; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(correct ? 1 : 0);
        dest.writeInt(picked ? 1 : 0);
        dest.writeString(textContent);
    }

    public static final Parcelable.Creator<Answer> CREATOR = new Parcelable.Creator<Answer>() {
        public Answer createFromParcel(Parcel in) {
            return new Answer(
                    in.readLong(),
                    in.readInt() == 1,
                    in.readInt() == 1,
                    in.readString()
            );
        }

        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };
}
