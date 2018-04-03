package com.aikya.konnek2.base.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by usr3 on 14/2/18.
 */

public class AppKonnek2GOChat_ChatsFragment extends Fragment
{
    public AppKonnek2GOChat_ChatsFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(com.aikya.konnek2.R.layout.fragment_gochat_chats, container, false);

    }
}
