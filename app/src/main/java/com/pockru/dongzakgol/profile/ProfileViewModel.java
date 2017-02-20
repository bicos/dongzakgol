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
import com.pockru.dongzakgol.BR;

/**
 * Created by raehyeong.park on 2017. 2. 16..
 */

public class ProfileViewModel extends BaseObservable {

    private String mUpdateUserName;

    private ProfileContract.Request mRequest;

    private ProfileContract.View mView;

    public ProfileViewModel(ProfileContract.Request request, ProfileContract.View view) {
        mRequest = request;
        mView = view;
    }

    @Bindable
    public String getUserName(){
        if (mRequest.getCurrentUser() != null) {
            return mRequest.getCurrentUser().getDisplayName();
        }

        return null;
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
        if (mRequest.getCurrentUser() != null) {
            return mRequest.getCurrentUser().getPhotoUrl();
        }

        return null;
    }

    public void clickProfileImage(){
        mRequest.getProfileUri();
    }

    public void receiveProfileImageUri(Uri uri){
        mRequest.updateProfileImage(uri, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void object) {
                notifyPropertyChanged(BR.profileImageUrl);
                mView.showUiSuccessUpdateProfileImage();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                notifyPropertyChanged(BR.profileImageUrl);
                mView.showUiFailedUpdateProfileImage();
            }
        });
    }

    public void changeName(CharSequence userName) {
        if (userName == null) {
            mUpdateUserName = "";
            return;
        }
        mUpdateUserName = userName.toString();
    }

    public void clickUpdateUserProfile(){
        if (TextUtils.isEmpty(mUpdateUserName)) {
            mView.showUiFailedUpdateUserName();
            return;
        }

        if (mUpdateUserName.equals(getUserName())) {
            return;
        }

        mRequest.updateUserName(mUpdateUserName, new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void object) {
                notifyPropertyChanged(BR.userName);
                mView.showUiSuccessUpdateUserName();
            }
        }, new OnFailureListener(){

            @Override
            public void onFailure(@NonNull Exception e) {
                notifyPropertyChanged(BR.userName);
                mView.showUiFailedUpdateUserName();
            }
        });
    }
}
