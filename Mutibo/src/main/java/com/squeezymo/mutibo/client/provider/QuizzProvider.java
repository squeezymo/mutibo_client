package com.squeezymo.mutibo.client.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import com.squeezymo.mutibo.model.Answer;
import com.squeezymo.mutibo.model.QuestionSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuizzProvider extends ContentProvider {
    private static final String LOG_TAG = QuizzProvider.class.getCanonicalName();

    private static final String AUTHORITY = "com.squeezymo.mutibo.QuestionSet";
    private static final String PATH_QSETS = "QuestionSet";
    private static final String PATH_ANSWERS = "Answer";
    public static final Uri URI_QSET_CONTENT = Uri.parse("content://" + AUTHORITY + "/" + PATH_QSETS);
    public static final Uri URI_ANSWER_CONTENT = Uri.parse("content://" + AUTHORITY + "/" + PATH_ANSWERS);

    private static final String DB_NAME = "mutibo_db";
    private static final int DB_VERSION = 2;
    private static final String TABLE_QSETS = "question_sets";
    private static final String TABLE_ANSWERS = "answers";

    public static final String COL_ID = "id";
    public static final String COL_QUESTION = "question";
    public static final String COL_EXPLANATION = "explanation";
    public static final String COL_CORRECT_ANS = "correct_answers";
    public static final String COL_INCORRECT_ANS = "incorrect_answers";
    public static final String COL_RATING_SUM = "rating_sum";
    public static final String COL_RATING_NUM = "rating_num";
    public static final String COL_ANSWERED = "answered";
    public static final String COL_TEXT_CONTENT = "text_content";

    private static final String DB_CREATE_QSETS = "create table " + TABLE_QSETS + "("
            + COL_ID + " integer primary key not null, "
            + COL_QUESTION + " text, "
            + COL_EXPLANATION + " text, "
            + COL_CORRECT_ANS + " text, "
            + COL_INCORRECT_ANS + " text, "
            + COL_RATING_SUM + " integer, "
            + COL_RATING_NUM + " integer, "
            + COL_ANSWERED + " integer"
            + ");";
    private static final String DB_CREATE_ANSWERS = "create table " + TABLE_ANSWERS + "("
            + COL_ID + " integer primary key not null, "
            + COL_TEXT_CONTENT + " text"
            + ");";
    private static final String DB_DROP_QSETS = "drop table if exists " + TABLE_QSETS;
    private static final String DB_DROP_ANSWERS = "drop table if exists " + TABLE_ANSWERS;

    private static final int URI_QSETS = 1;
    private static final int URI_QSETS_ID = 2;
    private static final int URI_ANSWERS = 3;
    private static final int URI_ANSWERS_ID = 4;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PATH_QSETS, URI_QSETS);
        uriMatcher.addURI(AUTHORITY, PATH_QSETS + "/#", URI_QSETS_ID);
        uriMatcher.addURI(AUTHORITY, PATH_ANSWERS, URI_ANSWERS);
        uriMatcher.addURI(AUTHORITY, PATH_ANSWERS + "/#", URI_ANSWERS_ID);
    }

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String tableName = "";
        Uri notifUri = null;

        switch (uriMatcher.match(uri)) {
            case URI_QSETS:
                tableName = TABLE_QSETS;
                notifUri = URI_QSET_CONTENT;

                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = COL_ID + " ASC";
                }
                break;
            case URI_QSETS_ID:
                tableName = TABLE_QSETS;
                notifUri = URI_QSET_CONTENT;

                String qsetId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COL_ID + " = " + qsetId;
                }
                else {
                    selection = selection + " AND " + COL_ID + " = " + qsetId;
                }
                break;
            case URI_ANSWERS:
                tableName = TABLE_ANSWERS;
                notifUri = URI_ANSWER_CONTENT;

                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = COL_ID + " ASC";
                }
                break;
            case URI_ANSWERS_ID:
                tableName = TABLE_ANSWERS;
                notifUri = URI_ANSWER_CONTENT;

                String answerId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COL_ID + " = " + answerId;
                }
                else {
                    selection = selection + " AND " + COL_ID + " = " + answerId;
                }
                break;
            default:
                throw new IllegalArgumentException("Illegal URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), notifUri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableName = "";
        Uri notifUri = null;

        switch (uriMatcher.match(uri)) {
            case URI_QSETS:
                tableName = TABLE_QSETS;
                notifUri = URI_QSET_CONTENT;
                break;
            case URI_ANSWERS:
                tableName = TABLE_ANSWERS;
                notifUri = URI_ANSWER_CONTENT;
                break;
            default:
                throw new IllegalArgumentException("Illegal URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(tableName, null, values);
        Uri resultUri = ContentUris.withAppendedId(notifUri, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);

        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = "";

        switch (uriMatcher.match(uri)) {
            case URI_QSETS:
                tableName = TABLE_QSETS;
                break;
            case URI_QSETS_ID:
                tableName = TABLE_QSETS;

                String qsetId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COL_ID + " = " + qsetId;
                } else {
                    selection = selection + " AND " + COL_ID + " = " + qsetId;
                }
                break;
            case URI_ANSWERS:
                tableName = TABLE_ANSWERS;
                break;
            case URI_ANSWERS_ID:
                tableName = TABLE_ANSWERS;

                String answerId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COL_ID + " = " + answerId;
                } else {
                    selection = selection + " AND " + COL_ID + " = " + answerId;
                }
                break;
            default:
                throw new IllegalArgumentException("Illegal URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(tableName, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName = "";

        switch (uriMatcher.match(uri)) {
            case URI_QSETS:
                tableName = TABLE_QSETS;
                break;
            case URI_QSETS_ID:
                tableName = TABLE_QSETS;

                String qsetId = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = COL_ID + " = " + qsetId;
                } else {
                    selection = selection + " AND " + COL_ID + " = " + qsetId;
                }
                break;
            case URI_ANSWERS:
                tableName = TABLE_ANSWERS;
                break;
            case URI_ANSWERS_ID:
                tableName = TABLE_ANSWERS;

                String answerId = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = COL_ID + " = " + answerId;
                } else {
                    selection = selection + " AND " + COL_ID + " = " + answerId;
                }
                break;
            default:
                throw new IllegalArgumentException("Illegal URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        int cnt = db.update(tableName, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return cnt;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    public static ContentValues questionSetToContentValues(QuestionSet questionSet) {
        ContentValues values = new ContentValues();

        values.put(COL_ID, questionSet.getId());
        values.put(COL_QUESTION, questionSet.getQuestion());
        values.put(COL_EXPLANATION, questionSet.getExplanation());

        List<Long> correctAnswersIds = new ArrayList<Long>();
        for (Answer correctAnswer : questionSet.getCorrectAnswers()) {
            correctAnswersIds.add(correctAnswer.getId());
        }

        List<Long> incorrectAnswersIds = new ArrayList<Long>();
        for (Answer incorrectAnswer : questionSet.getIncorrectAnswers()) {
            incorrectAnswersIds.add(incorrectAnswer.getId());
        }

        values.put(COL_CORRECT_ANS, StringUtils.join(correctAnswersIds, "|"));
        values.put(COL_INCORRECT_ANS, StringUtils.join(incorrectAnswersIds, "|"));
        values.put(COL_RATING_SUM, questionSet.getRatingSum());
        values.put(COL_RATING_NUM, questionSet.getRatingNum());
        values.put(COL_ANSWERED, questionSet.isAnswered() ? 1 : 0);

        return values;
    }

    public static ContentValues answerToContentValues(Answer answer) {
        ContentValues values = new ContentValues();

        values.put(COL_ID, answer.getId());
        values.put(COL_TEXT_CONTENT, answer.getTextContent());

        return values;
    }

    public static void storeQuestionSet(Context context, QuestionSet questionSet) {
        context.getContentResolver().insert(URI_QSET_CONTENT, questionSetToContentValues(questionSet));

        for (Answer correctAnswer : questionSet.getCorrectAnswers()) {
            context.getContentResolver().insert(URI_ANSWER_CONTENT, answerToContentValues(correctAnswer));
        }
        for (Answer incorrectAnswer : questionSet.getIncorrectAnswers()) {
            context.getContentResolver().insert(URI_ANSWER_CONTENT, answerToContentValues(incorrectAnswer));
        }
    }

    public static void updateQuestionSet(Context context, QuestionSet questionSet) {
        context.getContentResolver().update(
                URI_QSET_CONTENT,
                questionSetToContentValues(questionSet),
                COL_ID + "=?",
                new String[] {""+questionSet.getId()}
        );

        for (Answer correctAnswer : questionSet.getCorrectAnswers()) {
            context.getContentResolver().update(
                    URI_ANSWER_CONTENT,
                    answerToContentValues(correctAnswer),
                    COL_ID + "=?",
                    new String[] {""+correctAnswer.getId()}
            );
        }

        for (Answer incorrectAnswer : questionSet.getIncorrectAnswers()) {
            context.getContentResolver().update(
                    URI_ANSWER_CONTENT,
                    answerToContentValues(incorrectAnswer),
                    COL_ID + "=?",
                    new String[] {""+incorrectAnswer.getId()}
            );
        }
    }

    public static Answer retrieveAnswer(Context context, Cursor cursor) {
        long id = cursor.getLong(0);
        String textContent = cursor.getString(1);

        Answer answer = new Answer();
        answer.setId(id);
        answer.setTextContent(textContent);

        return answer;
    }

    public static QuestionSet retrieveQuestionSet(Context context, Cursor cursor) {
        long id = cursor.getLong(0);
        String question = cursor.getString(1);
        String explanation = cursor.getString(2);
        String correctAnswerIds = cursor.getString(3);
        String incorrectAnswerIds = cursor.getString(4);
        int ratingSum = cursor.getInt(5);
        int ratingNum = cursor.getInt(6);
        boolean answered = (cursor.getInt(7) == 0 ? false : true);

        Set<Answer> correctAnswers = new HashSet<Answer>();
        for (String correctAnswerId : StringUtils.split(correctAnswerIds, "|")) {
            Uri uri = Uri.parse("content://" + AUTHORITY + "/" + PATH_ANSWERS + "/" + correctAnswerId);
            Cursor ansCurs = context.getContentResolver().query(uri, null, null, null, null);
            if (ansCurs != null) {
                ansCurs.moveToFirst();
                correctAnswers.add(retrieveAnswer(context, ansCurs));
            }
        }

        Set<Answer> incorrectAnswers = new HashSet<Answer>();
        for (String incorrectAnswerId : StringUtils.split(incorrectAnswerIds, "|")) {
            Uri uri = Uri.parse("content://" + AUTHORITY + "/" + PATH_ANSWERS + "/" + incorrectAnswerId);
            Cursor ansCurs = context.getContentResolver().query(uri, null, null, null, null);
            if (ansCurs != null) {
                ansCurs.moveToFirst();
                incorrectAnswers.add(retrieveAnswer(context, ansCurs));
            }
        }

        QuestionSet questionSet = new QuestionSet();
        questionSet.setId(id);
        questionSet.setQuestion(question);
        questionSet.setExplanation(explanation);
        questionSet.setCorrectAnswers(correctAnswers);
        questionSet.setIncorrectAnswers(incorrectAnswers);
        questionSet.setRatingSum(ratingSum);
        questionSet.setRatingNum(ratingNum);
        questionSet.setAnswered(answered);

        return questionSet;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_QSETS);
            db.execSQL(DB_CREATE_ANSWERS);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DB_DROP_QSETS);
            db.execSQL(DB_DROP_ANSWERS);
            onCreate(db);
        }
    }
}
