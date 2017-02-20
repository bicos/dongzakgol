package com.pockru.dongzakgol.profile;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by raehyeong.park on 2017. 2. 20..
 */

public class ProfileContract {

    public interface View {

        void showUiSuccessUpdateProfileImage();

        void showUiFailedUpdateProfileImage();

        void showUiFailedUpdateUserName();

        void showUiSuccessUpdateUserName();
    }

    public interface Request {

        FirebaseUser getCurrentUser();

        void getProfileUri();

        void updateProfileImage(Uri uri, OnSuccessListener<Void> successListener, OnFailureListener failureListener);

        void updateUserName(String mUpdateUserName, OnSuccessListener<Void> successListener, OnFailureListener failureListener);
    }
}
