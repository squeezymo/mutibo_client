package com.squeezymo.mutibo.client;

import java.util.*;
import retrofit.http.*;
import com.squeezymo.mutibo.model.*;
//import com.squeezymo.mutibo.repository.*;

public interface QuizzSvcApi {
    public static final String DOMAIN_ID_PARAMETER = "domain_id";

    public static final String ANSWER_PTH = "/answer";
    public static final String QSET_PTH = "/set";
    // retrieve all question sets by particular domain
    public static final String QSET_BY_DOMAIN_ID_ALL_PTH = QSET_PTH + "/search/findByDomainId";

    @POST(QSET_PTH)
    public QuestionSet addQuestionSet(@Body QuestionSet qset);

    @GET(QSET_BY_DOMAIN_ID_ALL_PTH)
    public Collection<QuestionSet> findByDomainID(@Query(DOMAIN_ID_PARAMETER) long domainId);

    @GET(QSET_PTH + "/{id}")
    public QuestionSet getQuestionSetByID(@Path("id") long id);
}