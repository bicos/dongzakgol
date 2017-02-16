package com.pockru.dongzakgol.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.pockru.dongzakgol.MainActivity;
import com.pockru.dongzakgol.R;
import com.pockru.dongzakgol.login.LoginActivity;

/**
 * Created by raehyeong.park on 2017. 2. 15..
 */

public class SplashActivity extends AppCompatActivity {

    private static final int RC_LOGIN = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivityForResult(new Intent(this, LoginActivity.class), RC_LOGIN);
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_LOGIN) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, MainActivity.class));
            }
        }

        finish();
    }
}
