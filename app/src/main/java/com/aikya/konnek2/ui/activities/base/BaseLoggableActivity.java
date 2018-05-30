package com.aikya.konnek2.ui.activities.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;


import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.utils.ConstsCore;
import com.aikya.konnek2.base.activity.AppSplashActivity;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.utils.Loggable;
import com.aikya.konnek2.utils.ToastUtils;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivacyListsManager;
import com.quickblox.chat.listeners.QBPrivacyListListener;
import com.quickblox.chat.model.QBPrivacyList;
import com.quickblox.chat.model.QBPrivacyListItem;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseLoggableActivity extends BaseActivity implements Loggable {
    private static final String TAG = BaseLoggableActivity.class.getSimpleName();

    public AtomicBoolean canPerformLogout = new AtomicBoolean(true);


    //Block chat
    ArrayList<QBPrivacyListItem> privacyListItems;
    QBPrivacyListsManager privacyListsManager;
    QBPrivacyList privacyList;
    private QMUser user;
    BlockListener blockListener;

    private static final String privacyListName = "Konnek2_privacy_list_name";


    protected static boolean blockFlag = false;


    /*Block chat Test end*/

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {

        super.onAttachFragment(fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!appInitialized) {
            startSplashActivity();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(CAN_PERFORM_LOGOUT)) {
            canPerformLogout = new AtomicBoolean(savedInstanceState.getBoolean(CAN_PERFORM_LOGOUT));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(CAN_PERFORM_LOGOUT, canPerformLogout.get());
        super.onSaveInstanceState(outState);
    }

    //This method is used for logout action when Activity is going to background
    @Override
    public boolean isCanPerformLogoutInOnStop() {
        return canPerformLogout.get();
    }

    protected void startSplashActivity() {
//        SplashActivity.start(this);
        AppSplashActivity.start(this);
        finish();
    }

    protected boolean isCurrentSessionValid() {
        return AppSession.isSessionExistOrNotExpired(TimeUnit.MINUTES.toMillis(
                ConstsCore.TOKEN_VALID_TIME_IN_MINUTES));
    }


    /*BLOCK CHAT TEST*/

    /*Setting up the privacy list for block/unblock functionality*/
    protected void setUpPrivacyList(QMUser user) {

        this.user = user;
        blockListener = (BlockListener) this;
        privacyListsManager = QBChatService.getInstance().getPrivacyListsManager();
        privacyListsManager.addPrivacyListListener(privacyListListener);
        privacyList = new QBPrivacyList();
        privacyList.setName(privacyListName);
        privacyList.setActiveList(true);
        privacyListItems = new ArrayList<>();
    }

    protected void removefromprivacy() {
        try {
            QBPrivacyList privacyList = privacyListsManager.getPrivacyList(privacyListName);
            Log.d(TAG, "Private List = " + privacyList.toString());

            List<QBPrivacyListItem> blockedItems = privacyList.getItems();
            for (int i = 0; i < blockedItems.size(); i++) {

                String value = blockedItems.get(i).getValueForType();
                String arr[] = value.split("-");
                if (arr[0].equalsIgnoreCase(String.valueOf(user.getId()))) {
                    blockedItems.remove(i);
                    blockFlag = false;
                    blockListener.isUserBlocked(blockFlag);
                }
            }

            privacyListListener.setPrivacyList(privacyListName, blockedItems);
            privacyListListener.updatedPrivacyList(privacyListName);

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }
    }

    protected void addtoprivacy() {
       /* list = new QBPrivacyList();
        list.setName("konnek");
        list.setActiveList(true);
        item1.setAllow(false);
        item1.setType(QBPrivacyListItem.Type.USER_ID);
        item1.setValueForType(String.valueOf(user.getId()));
        item1.setMutualBlock(true);*/
//      items.add(item1);
//        list.setItems(items);


        QBPrivacyListItem qbPrivacyListItem = new QBPrivacyListItem();
        qbPrivacyListItem.setAllow(true);
        qbPrivacyListItem.setType(QBPrivacyListItem.Type.USER_ID);
        qbPrivacyListItem.setMutualBlock(true);
        qbPrivacyListItem.setValueForType(String.valueOf(user.getId()));


        privacyListItems.add(qbPrivacyListItem);
        privacyList.setItems(privacyListItems);
        blockFlag = true;

        ToastUtils.shortToast("Blocked User");

        try {

            privacyListsManager.createPrivacyList(privacyList);
            privacyListsManager.applyPrivacyList(privacyListName);
            blockListener.isUserBlocked(blockFlag);


        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            blockFlag = false;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            blockFlag = false;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            blockFlag = false;
        }
        privacyListListener.setPrivacyList(privacyListName, privacyListItems);

    }

    QBPrivacyListListener privacyListListener = new QBPrivacyListListener() {
        @Override
        public void setPrivacyList(String s, List<QBPrivacyListItem> list) {
            Log.d(TAG, "setPrivacyList = " + s);
            Log.d(TAG, "List of privacy items .. " + list.toString());
        }

        @Override
        public void updatedPrivacyList(String s) {

            Log.d(TAG, "Updated Privacy List = " + s);
        }
    };

    public interface BlockListener {
        void isUserBlocked(boolean blockFlag);
    }
}