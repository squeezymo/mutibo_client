package com.squeezymo.mutibo.client;

import com.squeezymo.mutibo.model.User;
import retrofit.http.*;

public interface UserSvcApi {
    public static final String USER_PATH = "/user";
    public static final String LOGIN_PARAM = "login";
    public static final String PASSWORD_PARAM = "password";

    @GET(USER_PATH)
    public User signIn(@Query(LOGIN_PARAM) String login, @Query(PASSWORD_PARAM) String password);
    
    @POST(USER_PATH)
    public User signUp(@Body User user);
}
