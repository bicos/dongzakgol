package com.pockru.dongzakgol.module.tumblr.model;

import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by rhpark on 16. 1. 1..
 */
public interface TumblrApi {

    String server = "https://www.tumblr.com/";

    @POST("/oauth/access_token")
    void auth(@Query("x_auth_username") String userName,
              @Query("x_auth_password") String password,
              @Query("x_auth_mode") String mode);
}
