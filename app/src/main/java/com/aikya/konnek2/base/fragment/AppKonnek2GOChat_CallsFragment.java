package com.aikya.konnek2.base.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aikya.konnek2.ui.activities.main.MainActivity;
import com.aikya.konnek2.R;

/**
 * Created by usr3 on 14/2/18.
 */

public class AppKonnek2GOChat_CallsFragment extends Fragment
{
    public AppKonnek2GOChat_CallsFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Intent goTochat = new Intent(getActivity(), MainActivity.class);
        //goTochat.putExtra(AppConstant.TAB_POSITION, 1);
        startActivity(goTochat);

        return inflater.inflate(R.layout.fragment_gochat_calls, container, false);



    }
}
