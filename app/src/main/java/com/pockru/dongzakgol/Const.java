package com.pockru.dongzakgol;

/**
 * Created by 래형 on 2015-12-24.
 */
public class Const {

    public static final String IMG_PREFIX_NAME = "dongzakgol_img";


    public static final String FLAG_CHECK_LOGIN = "10";
    public static final String FLAG_MAIN_LIST = "11";

    // imgur

    /*
      Logging flag
     */
    public static final boolean LOGGING = true;

    /*
      Your imgur client id. You need this to upload to imgur.

      More here: https://api.imgur.com/
     */
    public static final String MY_IMGUR_CLIENT_ID = "68d1fcfc0d247b7";
    public static final String MY_IMGUR_CLIENT_SECRET = "f25abc511d0075905b70c75a88dc49644c4124ca";

    /*
      Redirect URL for android.
     */
    public static final String MY_IMGUR_REDIRECT_URL = "http://android";


    /*
      Client Auth
     */
    public static String getClientAuth() {
        return "Client-ID " + MY_IMGUR_CLIENT_ID;
    }
}
