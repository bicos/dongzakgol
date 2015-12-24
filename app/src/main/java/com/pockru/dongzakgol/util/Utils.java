package com.pockru.dongzakgol.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by 래형 on 2015-12-24.
 */
public class Utils {

    /**
     * 카메라 또는 갤러리로부터 받은 url정보를 file 정보로 변환한다.
     * @param uri
     * @return
     */
    @TargetApi(19)
    private File uriToFile (Context context, Uri uri ) {

        String filePath = "";

        if ( uri.getPath().contains(":") ) {
            //:이 존재하는 경우

            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Images.Media.DATA };

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{ id }, null);


            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();

        } else {
            //:이 존재하지 않을경우
            String id = uri.getLastPathSegment();
            final String[] imageColumns = {MediaStore.Images.Media.DATA };
            final String imageOrderBy = null;

            String selectedImagePath = "path";
            String scheme = uri.getScheme();
            if ( scheme.equalsIgnoreCase("content") ) {
                Cursor imageCursor = context.getContentResolver().query(uri, imageColumns, null, null, null);

                if (imageCursor.moveToFirst()) {
                    filePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }
            } else {
                filePath = uri.getPath();
            }
        }

        File file = new File( filePath );

        return file;
    }
}
