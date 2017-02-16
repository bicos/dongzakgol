package com.pockru.dongzakgol.sidemenu;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.pockru.dongzakgol.BR;

/**
 * Created by raehyeong.park on 2017. 2. 15..
 */

public class SideMenuViewModel extends BaseObservable{

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            if (firebaseAuth.getCurrentUser() == null) {
                // logout
            } else {
                // login
            }

            notifyPropertyChanged(BR.isLogin);
        }
    };

    private SideMenuContract.View mView;

    public SideMenuViewModel(SideMenuContract.View view) {
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

    public void clickLogout(){
        mAuth.signOut();
    }

    public void clickLogin(){
        mView.requestLogin();
    }
}
