package com.pockru.dongzakgol.profile;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.pockru.dongzakgol.BR;

/**
 * Created by raehyeong.park on 2017. 2. 16..
 */

public class ProfileViewModel extends BaseObservable {

    private String mUpdateUserName;

    private Uri mPhotoUri;

    private ProfileContract.Request mRequest;

    private ProfileContract.View mView;

    public ProfileViewModel(ProfileContract.Request request, ProfileContract.View view) {
        mRequest = request;
        mView = view;

        initUi();
    }

    private void initUi() {
        FirebaseUser user = mRequest.getCurrentUser();

        if (user != null) {
            mPhotoUri = mRequest.getCurrentUser().getPhotoUrl();
        }
        notifyPropertyChanged(BR.profileImageUrl);

        if (user != null) {
            mUpdateUserName = mRequest.getCurrentUser().getDisplayName();
        }
        notifyPropertyChanged(BR.userName);
    }

    @Bindable
    public String getUserName(){
        return mUpdateUserName;
    }

    public void changeName(CharSequence userName) {
        if (userName == null) {
            mUpdateUserName = "";
            return;
        }
        mUpdateUserName = userName.toString();
    }

    @Bindable
    public String getUserEmail(){
        if (mRequest.getCurrentUser() != null) {
            return mRequest.getCurrentUser().getEmail();
        }

        return null;
    }

    @Bindable
    public String getUserProvider(){
        if (mRequest.getCurrentUser() != null) {
            return mRequest.getCurrentUser().getProviderId() + "(으)로 로그인";
        }

        return null;
    }

    @BindingAdapter("url")
    public static void setUrl(ImageView imageView, Uri uri) {
        Glide.with(imageView.getContext()).load(uri).into(imageView);
    }

    @Bindable
    public Uri getProfileImageUrl(){
        return mPhotoUri;
    }

    public void setProfileImageUri(Uri uri){
        mPhotoUri = uri;
        notifyPropertyChanged(BR.profileImageUrl);
    }

    public void clickProfileImage(){
        mRequest.getProfileUri();
    }

    public void clickUpdateUserProfile(){
        if (TextUtils.isEmpty(mUpdateUserName)) {
            mView.showUiFailedUpdateProfile();
            return;
        }

        mRequest.updateProfile(mPhotoUri, mUpdateUserName, new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void object) {
                notifyPropertyChanged(BR.userName);
                mView.showUiSuccessUpdateProfile();
            }
        }, new OnFailureListener(){

            @Override
            public void onFailure(@NonNull Exception e) {
                notifyPropertyChanged(BR.userName);
                mView.showUiFailedUpdateProfile();
            }
        });
    }
}
