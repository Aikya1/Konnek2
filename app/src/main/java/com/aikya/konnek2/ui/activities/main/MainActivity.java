package com.aikya.konnek2.ui.activities.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.aikya.konnek2.R;
import com.aikya.konnek2.base.activity.AppHomeActivity;
import com.aikya.konnek2.call.core.core.command.Command;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.models.UserCustomData;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.core.utils.Utils;
import com.aikya.konnek2.call.core.utils.helpers.CoreSharedHelper;
import com.aikya.konnek2.call.db.managers.DataManager;
import com.aikya.konnek2.call.services.QMUserService;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.base.BaseLoggableActivity;
import com.aikya.konnek2.ui.activities.settings.SettingsActivity;
import com.aikya.konnek2.ui.fragments.chats.DialogsListFragment;
import com.aikya.konnek2.utils.MediaUtils;
import com.aikya.konnek2.utils.helpers.ImportFriendsHelper;
import com.aikya.konnek2.utils.helpers.ServiceManager;
import com.aikya.konnek2.utils.image.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;

import java.util.Date;

import rx.Observable;
import rx.Subscriber;


public class MainActivity extends BaseLoggableActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_main;
    }

    @Override
    public void onBackPressed() {
        Intent goToHone = new Intent(getApplicationContext(), AppHomeActivity.class);
        startActivity(goToHone);
        super.onBackPressed();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setUpActionBarWithUpButton();
        if (!isChatInitializedAndUserLoggedIn()) {
            loginChat();
        }

        friendListHelper = getFriendListHelper();
        addDialogsAction();
        launchDialogsListFragment();
        openPushDialogIfPossible();
    }

    private void openPushDialogIfPossible() {
        CoreSharedHelper sharedHelper = CoreSharedHelper.getInstance();
        if (sharedHelper.needToOpenDialog()) {
            QBChatDialog chatDialog = DataManager.getInstance().getQBChatDialogDataManager()
                    .getByDialogId(sharedHelper.getPushDialogId());
            QMUser user = QMUserService.getInstance().getUserCache().get((long) sharedHelper.getPushUserId());
            if (chatDialog != null) {
                startDialogActivity(chatDialog, user);
            }
        }
    }

    private void startDialogActivity(QBChatDialog chatDialog, QMUser user) {
        if (QBDialogType.PRIVATE.equals(chatDialog.getType())) {
            startPrivateChatActivity(user, chatDialog);
        } else {
            startGroupChatActivity(chatDialog);
        }
    }

    private void initFields() {
        AppSession session = AppSession.getSession();
        QBUser user = session.getUser();
        String fullName = AppSession.getSession().getUser().getFullName();

        UserCustomData userCustomData = Utils.customDataToObject(user.getCustomData());
        String phNo = userCustomData.getContactno();

        if (fullName != null && !fullName.equalsIgnoreCase("null")) {
            title = " " + fullName;
        } else {
            title = " " + phNo;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SettingsActivity.REQUEST_CODE_LOGOUT == requestCode && RESULT_OK == resultCode) {
            startLandingScreen();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return false;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
//        actualizeCurrentTitle();
        super.onResume();
//        addActions();
    }

    private void actualizeCurrentTitle() {

        String fullName = AppSession.getSession().getUser().getFullName();
        String phNumber = AppSession.getSession().getUser().getPhone();

        if (!TextUtils.isEmpty(fullName) || fullName != null) {
            title = " " + AppSession.getSession().getUser().getFullName();
        } else {
            title = " " + phNumber;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        removeActions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeDialogsAction();
    }

    @Override
    protected void checkShowingConnectionError() {
        if (!isNetworkAvailable()) {
            setActionBarTitle(getString(R.string.dlg_internet_connection_is_missing));
            setActionBarIcon(null);
        } else {
            setActionBarTitle(title);
            checkVisibilityUserIcon();
        }
    }

    @Override
    protected void performLoginChatSuccessAction(Bundle bundle) {
        super.performLoginChatSuccessAction(bundle);
//        actualizeCurrentTitle();
    }

    private void addDialogsAction() {
        addAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION, new LoginChatCompositeSuccessAction());
        addAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION, new LoadChatsSuccessAction());
    }

    private void removeDialogsAction() {
        removeAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION);
        removeAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION);
    }

    private void addActions() {
//        addAction(QBServiceConsts.IMPORT_FRIENDS_SUCCESS_ACTION, importFriendsSuccessAction);
//        addAction(QBServiceConsts.IMPORT_FRIENDS_FAIL_ACTION, importFriendsFailAction);

//        updateBroadcastActionList();
    }

    private void removeActions() {
//        removeAction(QBServiceConsts.IMPORT_FRIENDS_SUCCESS_ACTION);
//        removeAction(QBServiceConsts.IMPORT_FRIENDS_FAIL_ACTION);

//        updateBroadcastActionList();
    }

    private void checkVisibilityUserIcon() {
        UserCustomData userCustomData = Utils.customDataToObject(AppSession.getSession().getUser().getCustomData());

        if (userCustomData != null) {
            if (!TextUtils.isEmpty(userCustomData.getAvatarUrl())) {
                loadLogoActionBar(userCustomData.getAvatarUrl());
            } else {
                setActionBarIcon(MediaUtils.getRoundIconDrawable(this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.placeholder_user)));
            }
        }
    }

    private void loadLogoActionBar(String logoUrl) {
        ImageLoader.getInstance().loadImage(logoUrl, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS,
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedBitmap) {
                        setActionBarIcon(MediaUtils.getRoundIconDrawable(MainActivity.this, loadedBitmap));
                    }
                });
    }

    private void performImportFriendsSuccessAction() {
        appSharedHelper.saveUsersImportInitialized(true);
        hideProgress();
    }

    private void performImportFriendsFailAction(Bundle bundle) {
        performImportFriendsSuccessAction();
    }

    private void launchDialogsListFragment() {
        setCurrentFragment(DialogsListFragment.newInstance(), true);
//        setCurrentFragment(SearchFragment.newInstance(), true);
    }

    private void startImportFriends() {
        ImportFriendsHelper importFriendsHelper = new ImportFriendsHelper(MainActivity.this);

        /*if (facebookHelper.isSessionOpened()) {
            importFriendsHelper.startGetFriendsListTask(true);
        } else {
            importFriendsHelper.startGetFriendsListTask(false);
        }*/

        hideProgress();
    }

//    @Override
//    public void globalSearchTrigger() {
//        Log.d("FRIENDREQUEST252525", "MainActivity  globalSearchTrigger   ");
//
//        onBackPressed();
//
//    }


    private class ImportFriendsSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            performImportFriendsSuccessAction();
        }
    }

    private class ImportFriendsFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            performImportFriendsFailAction(bundle);
        }
    }


}