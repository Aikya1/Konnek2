package com.android.konnek2.base.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.konnek2.R;

/**
 * Created by usr3 on 14/2/18.
 */

public class AppKonnek2GOChat_ContactsFragment extends Fragment
{

    public AppKonnek2GOChat_ContactsFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gochat_contact, container, false);

    }
}
