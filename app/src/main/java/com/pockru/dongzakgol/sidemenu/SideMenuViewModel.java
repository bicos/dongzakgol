package com.pockru.dongzakgol.sidemenu;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.pockru.dongzakgol.BR;
import com.pockru.dongzakgol.R;

/**
 * Created by raehyeong.park on 2017. 2. 15..
 */

public class SideMenuViewModel extends BaseObservable{

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            notifyPropertyChanged(BR.isLogin);
            notifyPropertyChanged(BR.sideMenuMessage);
        }
    };

    private SideMenuContract.View mView;

    private Context mContext;

    public SideMenuViewModel(Context context, SideMenuContract.View view) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mView = view;
    }

    public void addAuthStateListener(){
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    public void removeStateListener(){
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Bindable
    public boolean getIsLogin(){
        return mAuth.getCurrentUser() != null;
    }

    @Bindable
    public String getSideMenuMessage(){
        if (mAuth.getCurrentUser() == null) {
            return mContext.getString(R.string.inform_msg_login);
        } else {
            return mContext.getString(R.string.inform_msg_logout);
        }
    }

    public void clickLogout(){
        mAuth.signOut();
        mView.closeDrawer();
    }

    public void clickLogin(){
        mView.requestLogin();
        mView.closeDrawer();
    }
}
