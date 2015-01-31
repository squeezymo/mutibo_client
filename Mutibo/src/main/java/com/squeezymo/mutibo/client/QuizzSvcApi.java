package com.squeezymo.mutibo.client;

import java.util.*;
import retrofit.http.*;
import com.squeezymo.mutibo.model.*;

interface QuizzSvcApi {

    public static final String QSET_PTH = "/set";
    public static final String QSETS_PTH = "/sets";
    public static final String RATE_PTH = "/rate";
    public static final String TOKEN_PATH = "/oauth/token";

    @POST(QSET_PTH)
    public QuestionSet addQuestionSet(@Body QuestionSet qset);

    @GET(QSETS_PTH)
    public List<Long> getAllQuestionSetIDs();

    @GET(QSET_PTH + "/{id}")
    public QuestionSet getQuestionSetByID(@Path("id") long id);

    @POST(QSET_PTH + RATE_PTH + "/{id}")
    public QuestionSet rateQuestionSet(@Path("id") long id, @Body int rating);

}