package com.pockru.dongzakgol.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by raehyeong.park on 2017. 2. 20..
 */

public class RequestProfileImpl implements ProfileContract.Request {

    public static int RC_GALLERY = 1000;

    private Activity mActivity;

    private FirebaseAuth mAuth;

    public RequestProfileImpl(Activity activity) {
        mActivity = activity;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getProfileUri() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        mActivity.startActivityForResult(Intent.createChooser(intent, "이미지 선택"), RC_GALLERY);
    }

    @Override
    public void updateProfile(Uri uri, String updateUserName, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        FirebaseUser user = mAuth.getCurrentUser();

        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();

        if (uri != null) {
            builder.setPhotoUri(uri);
        }

        if (updateUserName != null) {
            builder.setDisplayName(updateUserName);
        }

        if (user != null) {
            user.updateProfile(builder.build())
                    .addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i("test", "task.isSuccessful() : " + task.isSuccessful());

                        }
                    })
                    .addOnSuccessListener(mActivity, successListener)
                    .addOnFailureListener(mActivity, failureListener);
        }
    }

    @Override
    public FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }
}
