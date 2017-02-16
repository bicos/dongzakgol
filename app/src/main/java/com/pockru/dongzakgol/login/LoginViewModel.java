package com.pockru.dongzakgol.login;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.pockru.dongzakgol.R;

/**
 * Created by raehyeong.park on 2017. 2. 14..
 */

public class LoginViewModel extends BaseObservable {

    private String email;

    private String password;

    private Activity mActivity;

    private FirebaseAuth mAuth;

    public LoginViewModel(Activity activity) {
        mActivity = activity;
        mAuth = FirebaseAuth.getInstance();
    }

    public void changeEmail(CharSequence email) {
        this.email = email.toString();
    }

    public void changePassword(CharSequence password) {
        this.password = password.toString();
    }

    public void clickSignUp() {
        if (!validateEmail()) {
            Toast.makeText(mActivity, R.string.toast_msg_vaildate_email_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validatePassword()) {
            Toast.makeText(mActivity, R.string.toast_msg_vaildate_pwd_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(mActivity, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(mActivity, R.string.toast_msg_success_sign_up, Toast.LENGTH_SHORT).show();
                        mActivity.setResult(Activity.RESULT_OK);
                        mActivity.finish();
                    }
                })
                .addOnFailureListener(mActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void clickSignIn() {
        if (!validateEmail()) {
            Toast.makeText(mActivity, R.string.toast_msg_vaildate_email_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validatePassword()) {
            Toast.makeText(mActivity, R.string.toast_msg_vaildate_pwd_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(mActivity, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(mActivity, R.string.toast_msg_success_sign_in, Toast.LENGTH_SHORT).show();
                        mActivity.setResult(Activity.RESULT_OK);
                        mActivity.finish();
                    }
                })
                .addOnFailureListener(mActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private boolean validateEmail() {
        return !TextUtils.isEmpty(email) && email.contains("@");
    }

    private boolean validatePassword() {
        return !TextUtils.isEmpty(password);
    }
}
