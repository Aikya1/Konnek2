package com.aikya.konnek2.ui.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.ui.activities.base.BaseActivity;
import com.aikya.konnek2.utils.bridges.LoadingBridge;
import com.aikya.konnek2.App;
import com.aikya.konnek2.call.core.qb.helpers.QBChatHelper;
import com.aikya.konnek2.call.core.qb.helpers.QBFriendListHelper;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.utils.bridges.ActionBarBridge;
import com.aikya.konnek2.utils.bridges.ConnectionBridge;
import com.aikya.konnek2.utils.bridges.SnackbarBridge;
import com.aikya.konnek2.utils.listeners.ServiceConnectionListener;
import com.aikya.konnek2.utils.listeners.UserStatusChangingListener;
import com.quickblox.chat.QBChatService;


import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment implements UserStatusChangingListener, ServiceConnectionListener {

    protected App app;
    protected BaseActivity baseActivity;
    protected BaseActivity.FailAction failAction;
    protected ConnectionBridge connectionBridge;
    protected ActionBarBridge actionBarBridge;
    protected LoadingBridge loadingBridge;
    protected SnackbarBridge snackbarBridge;

    protected QBFriendListHelper friendListHelper;
    protected QBChatHelper chatHelper;
    protected QBService service;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        if (activity instanceof BaseActivity) {
            baseActivity = (BaseActivity) activity;
            service = baseActivity.getService();
            friendListHelper = baseActivity.getFriendListHelper();
            chatHelper = baseActivity.getChatHelper();
        }

        if (activity instanceof ConnectionBridge) {
            connectionBridge = (ConnectionBridge) activity;
        }

        if (activity instanceof ActionBarBridge) {
            actionBarBridge = (ActionBarBridge) activity;
        }

        if (activity instanceof LoadingBridge) {
            loadingBridge = (LoadingBridge) activity;
        }

        if (activity instanceof SnackbarBridge) {
            snackbarBridge = (SnackbarBridge) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addListeners();

        app = App.getInstance();
        failAction = baseActivity.getFailAction();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void initActionBar() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        initActionBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeListeners();
    }

    private void addListeners() {
        baseActivity.addFragmentUserStatusChangingListener(this);
        baseActivity.addFragmentServiceConnectionListener(this);
    }

    private void removeListeners() {
        baseActivity.removeFragmentUserStatusChangingListener(this);
        baseActivity.removeFragmentServiceConnectionListener(this);
    }

    protected boolean isExistActivity() {
        return ((!isDetached()) && (baseActivity != null));
    }

    protected void activateButterKnife(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onChangedUserStatus(int userId, boolean online) {

        // nothing by default
    }

    @Override
    public void onConnectedToService(QBService service) {
        // nothing by default
    }



    //
    protected boolean isChatInitializedAndUserLoggedIn() {
        return isAppInitialized() && QBChatService.getInstance().isLoggedIn();
    }

    protected boolean isAppInitialized() {
        return AppSession.getSession().isSessionExist();
    }

}