package com.squeezymo.mutibo.client;

import com.squeezymo.mutibo.model.*;
import retrofit.RestAdapter;
import retrofit.http.*;

public class RestfulClient {

    private QuizzSvcApi service;

    public RestfulClient(String endpoint) {
        service = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .build()
                .create(QuizzSvcApi.class);
    }

    public QuestionSet addQuestionSet(QuestionSet qset) {
        return service.addQuestionSet(qset);
    }

    public QuestionSet getQuestionSetById(long id) {
       return service.getQuestionSetByID(id);
    }

    
}
