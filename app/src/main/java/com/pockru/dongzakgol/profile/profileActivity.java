package com.pockru.dongzakgol.profile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.pockru.dongzakgol.R;
import com.pockru.dongzakgol.databinding.ActivityProfileBinding;

/**
 * Created by raehyeong.park on 2017. 2. 16..
 */

public class ProfileActivity extends AppCompatActivity implements ProfileContract.View {

    private ProfileViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RequestProfileImpl requestProfile = new RequestProfileImpl(this);

        mViewModel = new ProfileViewModel(requestProfile, this);

        ActivityProfileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        binding.setViewModel(mViewModel);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RequestProfileImpl.RC_GALLERY == requestCode) {
            if (resultCode == RESULT_OK) {
                mViewModel.setProfileImageUri(data.getData());
            } else {
                Toast.makeText(getApplicationContext(), "프로필 이미지 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void showUiSuccessUpdateProfile() {
        Toast.makeText(getApplicationContext(), "프로필 업데이트를 성공하였습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUiFailedUpdateProfile() {
        Toast.makeText(getApplicationContext(), "프로필 업데이트를 실패하였습니다.", Toast.LENGTH_SHORT).show();
    }
}
