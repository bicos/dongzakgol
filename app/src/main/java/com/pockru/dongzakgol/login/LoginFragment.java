package com.pockru.dongzakgol.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pockru.dongzakgol.databinding.FragmentLoginBinding;

/**
 * Created by raehyeong.park on 2017. 2. 15..
 */

public class LoginFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentLoginBinding viewBinding = FragmentLoginBinding.inflate(inflater, container, false);

        viewBinding.setViewModel(new LoginViewModel(getActivity()));

        return viewBinding.getRoot();
    }
}
