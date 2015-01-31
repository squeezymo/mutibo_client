package com.squeezymo.mutibo.client;

import android.util.Log;

import com.squeezymo.mutibo.model.*;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import org.apache.http.HttpStatus;

import java.util.List;

public class RestfulClient {
    private static final String LOG_TAG = RestfulClient.class.getCanonicalName();
    private static final String LOCAL_HOST = "https://192.168.56.1:8443"; // "http://192.168.56.1:8080";

    public static final String USER_PREFS = "credentials";
    public static final String LOGIN_PREF = "login";
    public static final String PASSWORD_PREF = "password";
    public static final String HIGHSCORE_PREF = "highscore";

    private SecuredRestBuilder mQuizzBuilder;
    private boolean mIsBuilt;
    private User mUser;

    private static RestfulClient sRestfulClient;
    private QuizzSvcApi mQuizzSvc;
    private UserSvcApi mUserSvc;

    public static RestfulClient getInstance() {
        if (sRestfulClient == null)
            sRestfulClient = new RestfulClient();

        return sRestfulClient;
    }

    private RestfulClient() {
        mUserSvc = new RestAdapter.Builder()
                .setEndpoint(LOCAL_HOST)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(new ApacheClient(HttpsClient.getInstance()))
                .build()
                .create(UserSvcApi.class);

        mQuizzBuilder = new SecuredRestBuilder()
                .setLoginEndpoint(LOCAL_HOST + QuizzSvcApi.TOKEN_PATH)
                .setClientId("mobile")
                .setClient(new ApacheClient(HttpsClient.getInstance()))
                .setEndpoint(LOCAL_HOST)
                .setGuestAccess(true)
                .setLogLevel(RestAdapter.LogLevel.FULL);

        mIsBuilt = false;
        mUser = null;
    }

    public boolean signIn(String login, String password) {
        checkConnectivity();

        try {
            User user = mUserSvc.signIn(login, password);
            if (user == null)
                return false;

            mQuizzBuilder
                    .setGuestAccess(false)
                    .setUsername(user.getLogin())
                    .setPassword(user.getPassword());

            mIsBuilt = false;
            mUser = user;
        }
        catch (RetrofitError re) {
            if (re.getResponse() != null && re.getResponse().getStatus() == HttpStatus.SC_NOT_FOUND)
                return false;
            else
                throw re;
        }

        return true;
    }

    public boolean signUp(User userParam) {
        checkConnectivity();

        try {
            User user = mUserSvc.signUp(userParam);
            if (user == null)
                return false;

            mQuizzBuilder
                    .setGuestAccess(false)
                    .setUsername(user.getLogin())
                    .setPassword(user.getPassword());

            mIsBuilt = false;
            mUser = user;
        }
        catch (RetrofitError re) {
            if (re.getResponse() != null && re.getResponse().getStatus() == HttpStatus.SC_BAD_REQUEST)
                return false;
            else
                throw re;
        }
        return true;
    }

    public boolean signOut() {
        mQuizzBuilder
                .setGuestAccess(true)
                .setUsername(null)
                .setPassword(null);

        mIsBuilt = false;
        mUser = null;

        return true;
    }

    public User getUser() { return mUser; }
    public String getUsername() {
        return mUser == null ? null : mUser.getLogin();
    }

    private void build() {
        if (!mIsBuilt || mQuizzSvc == null) {
            mQuizzSvc = mQuizzBuilder
                    .build()
                    .create(QuizzSvcApi.class);

            mIsBuilt = true;
        }
    }

    private void checkConnectivity() {
        // TODO

        if (false) throw new SecuredRestException("No connection", SecuredRestException.NO_CONNECTION);
    }

    public boolean isAuthorized() {
        return !(mUser == null);
    }

    public QuestionSet addQuestionSet(QuestionSet qset) {
        build();
        return mQuizzSvc.addQuestionSet(qset);
    }

    public QuestionSet getQuestionSetById(long id) {
        build();
        return mQuizzSvc.getQuestionSetByID(id);
    }

    public List<Long> getAllQuestionSetIDs() {
        build();
        return mQuizzSvc.getAllQuestionSetIDs();
    }

    public QuestionSet rateQuestionSet(QuestionSet qset, int rating) {
        build();
        return mQuizzSvc.rateQuestionSet(qset.getId(), rating);
    }
}
