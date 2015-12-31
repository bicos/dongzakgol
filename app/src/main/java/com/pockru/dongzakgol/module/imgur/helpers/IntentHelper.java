package com.pockru.dongzakgol.module.imgur.helpers;

import android.app.Activity;
import android.content.Intent;

import com.pockru.dongzakgol.BaseActivity;

/**
 * Created by AKiniyalocts on 2/23/15.
 *
 */
public class IntentHelper {

  public static void chooseFileIntent(Activity activity){
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("image/*");
    activity.startActivityForResult(intent, BaseActivity.REQ_FILECHOOSER_FOR_TUMBLR);
  }
}
