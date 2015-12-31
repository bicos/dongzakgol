package com.pockru.dongzakgol.module.tumblr.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.pockru.dongzakgol.R;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;

import java.io.File;
import java.io.IOException;

/**
 * Created by rhpark on 16. 1. 1..
 */
public class TumblrUploadImg extends AsyncTask<String, Void, PhotoPost> {

    Context mContext;
    TumblrUploadImg mImgUpload;
    ProgressDialog mProgress;
    TumblrUploadListener mListener;

    public boolean startImgUpload;

    public TumblrUploadImg(Context context, TumblrUploadListener listener) {
        super();
        mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startImgUpload = true;
        if (mProgress == null) {
            mProgress = new ProgressDialog(mContext);
            mProgress.setMessage("이미지 업로딩중입니다...");
        }
        mProgress.show();

        if (mImgUpload != null) {
            boolean canCancel = mImgUpload.cancel(false);
            if (!canCancel) {
                this.cancel(true);
            }
        }
        mImgUpload = this;
    }

    @Override
    protected PhotoPost doInBackground(String... params) {
        JumblrClient client = new JumblrClient(TumblrOAuthActivity.CONSUMER_ID, TumblrOAuthActivity.CONSUMER_SECRET, params[0], params[1]);
        if (client.user().getBlogs() != null && client.user().getBlogs().size() > 0) {
            try {
                PhotoPost post = client.newPost(client.user().getBlogs().get(0).getName(), PhotoPost.class);
                post.setPhoto(new Photo(new File(params[2])));
                post.save();
                return (PhotoPost) client.blogPost(post.getBlogName(), post.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(PhotoPost result) {
        super.onPostExecute(result);
        startImgUpload = false;

        if (mProgress != null) {
            mProgress.dismiss();
        }

        if (mListener != null) {
            mListener.getResponse(result);
        }
    }

    public interface TumblrUploadListener {
        public void getResponse(PhotoPost result);
    }
}
