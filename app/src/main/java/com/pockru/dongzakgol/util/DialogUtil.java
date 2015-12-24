package com.pockru.dongzakgol.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

/**
 * Created by 래형 on 2015-12-24.
 */
public class DialogUtil {

    public static void showListDialog(Context context, String titleName, String[] array, DialogInterface.OnClickListener itemListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleName);
        builder.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, array), itemListener);
        builder.show();
    }
}
