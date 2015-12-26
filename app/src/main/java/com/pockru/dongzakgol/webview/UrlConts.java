package com.pockru.dongzakgol.webview;

import android.net.Uri;
import android.util.Log;

/**
 * Created by 래형 on 2015-12-24.
 */
public class UrlConts {

    public static final String MAIN_URL = "http://dzgol.net";
    public static final String MAIN_PATH = "index.php";

    public static final String LOGIN_URL = MAIN_URL + "/index.php?mid=%s&act=dispMemberLoginForm";
    public static final String LOGOUT_URL = MAIN_URL + "/index.php?mid=%s&act=dispMemberLogout";

    // http://sungyun4463.cafe24.com/index.php?act=dispMemberInfo&mid=board_kKDY32
    public static final String MY_INFO_URL = MAIN_URL + "/index.php?act=dispMemberInfo";

    public static final String MY_SCRAP_URL = MAIN_URL + "/index.php?act=dispMemberScrappedDocument";

    // http://sungyun4463.cafe24.com/index.php?act=dispMemberSavedDocument&mid=board_kKDY32
    public static final String MY_SAVE_URL = MAIN_URL + "/index.php?act=dispMemberSavedDocument";

    // http://sungyun4463.cafe24.com/index.php?act=dispMemberOwnDocument&mid=board_kKDY32
    public static final String MY_DOC_URL = MAIN_URL + "/index.php?act=dispMemberOwnDocument";

    // http://sungyun4463.cafe24.com/index.php?act=dispMemberOwnDocument&mid=board_kKDY32
    public static final String MY_PRIEND_URL = MAIN_URL + "/index.php?act=dispMemberOwnDocument";

    // http://sungyun4463.cafe24.com/index.php?act=dispCommunicationMessages&mid=board_kKDY32
    public static final String MY_MSG_URL = MAIN_URL +"/index.php?act=dispCommunicationMessages";

    public static final String ACT_WRITE = "dispBoardWrite";
    public static final String ACT_LOGIN = "dispMemberLoginForm";

    public static final String PARAM_COMPONENT = "component";
    public static final String COMPONENT_IMG_LINK = "image_link";

    public static final String PARAM_MID = "mid";
    public static final String PARAM_ACT = "act";

    public static final String MAIN_MID = "page_dhbZ41";

    //document.querySelector("")
//    public static final java.lang.String CHECK_LOGIN_JS = "javascript:window.JSBridge.print(document.querySelector(\"#top_mn\"));";

    public static final String getMainUrl() {
        Uri uri = Uri.parse(MAIN_URL);
        uri = uri.buildUpon()
                .appendPath(MAIN_PATH)
                .appendQueryParameter(PARAM_MID, MAIN_MID).build();
        return uri.toString();
    }

    public static final String getWriteUrl(String mid) {
        Uri uri = Uri.parse(MAIN_URL);
        uri = uri.buildUpon()
                .appendPath(MAIN_PATH)
                .appendQueryParameter(PARAM_MID, mid)
                .appendQueryParameter(PARAM_ACT, ACT_WRITE).build();
        return uri.toString();
    }

    public static String getLoginUrl(String url) {
        Uri uri = Uri.parse(url);
        uri = uri.buildUpon()
                .appendQueryParameter(PARAM_ACT, ACT_LOGIN).build();
        return uri.toString();
    }

    public static String getMyInfoUrlUrl(String mid) {
        Uri uri = Uri.parse(MY_INFO_URL);
        uri = uri.buildUpon()
                .appendQueryParameter(PARAM_MID, mid).build();
        return uri.toString();
    }

    public static final String INSERT_INTO_JS = "javascript:(function(){document.getElementById(\"editor\").innerHTML += '<br/><img src= \"%s\"><br/>'})()";

    public static String insertImageJS(String imgLink) {
        return String.format(INSERT_INTO_JS, imgLink);
    }

    public static final String GET_HTML_JS = "javascript:window.JSBridge.print('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>','%s');";

    public static String getHtml(String tag){
        String js = String.format(GET_HTML_JS, tag);
        Log.i("test", js);
        return js;
    }

}
