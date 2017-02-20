package com.pockru.dongzakgol.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    public void updateProfileImage(Uri uri, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.updateProfile(new UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri).build())
                    .addOnSuccessListener(mActivity, successListener)
                    .addOnFailureListener(mActivity, failureListener);
        }
    }

    @Override
    public void updateUserName(String updateUserName, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.updateProfile(new UserProfileChangeRequest.Builder()
                    .setDisplayName(updateUserName).build())
                    .addOnSuccessListener(mActivity, successListener)
                    .addOnFailureListener(mActivity, failureListener);
        }
    }

    @Override
    public FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }
}
