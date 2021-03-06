package com.aikya.konnek2.ui.activities.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.core.command.Command;
import com.aikya.konnek2.call.core.qb.commands.chat.QBCreatePrivateChatCommand;
import com.aikya.konnek2.call.core.qb.commands.chat.QBDeleteChatCommand;
import com.aikya.konnek2.call.core.qb.commands.friend.QBRemoveFriendCommand;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.core.utils.OnlineStatusUtils;
import com.aikya.konnek2.call.core.utils.UserFriendUtils;
import com.aikya.konnek2.call.db.managers.DataManager;
import com.aikya.konnek2.call.db.models.DialogOccupant;
import com.aikya.konnek2.call.db.utils.DialogTransformUtils;
import com.aikya.konnek2.call.services.QMUserCacheImpl;
import com.aikya.konnek2.call.services.QMUserService;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.base.BaseLoggableActivity;
import com.aikya.konnek2.ui.activities.call.CallActivity;
import com.aikya.konnek2.ui.activities.chats.PrivateDialogActivity;
import com.aikya.konnek2.ui.fragments.dialogs.base.TwoButtonsDialogFragment;
import com.aikya.konnek2.utils.DateUtils;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.image.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivacyListsManager;
import com.quickblox.chat.listeners.QBPrivacyListListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBPrivacyList;
import com.quickblox.chat.model.QBPrivacyListItem;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.OnClick;

public class UserProfileActivity extends BaseLoggableActivity implements BaseLoggableActivity.BlockListener {

    private final static String TAG = UserProfileActivity.class.getSimpleName();


    @Bind(com.aikya.konnek2.R.id.avatar_imageview)
    ImageView avatarImageView;

    @Bind(com.aikya.konnek2.R.id.name_textview)
    TextView nameTextView;

    @Bind(com.aikya.konnek2.R.id.timestamp_textview)
    TextView timestampTextView;

    @Bind(R.id.blockTv)
    TextView blockTv;

    /*@Bind(com.aikya.konnek2.R.id.phone_view)
    View phoneView;

    @Bind(com.aikya.konnek2.R.id.name_textview)
    TextView phoneTextView;*/

    private DataManager dataManager;
    private int userId;
    private QMUser user;
    private Observer userObserver;
    private boolean removeContactAndChatHistory;

    //Block chat
    /*ArrayList<QBPrivacyListItem> privacyListItems;
    QBPrivacyListsManager privacyListsManager;

    QBPrivacyList privacyList;

    private static final String privacyListName = "Konnek2_privacy_list_name";


    private static boolean blockFlag = false;*/


    public static void start(Context context, int friendId) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_FRIEND_ID, friendId);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
//        return com.aikya.konnek2.R.layout.activity_user_profile;
        return R.layout.activity_view_contact;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setUpActionBarWithUpButton();

        initUIWithUsersData();
        setUpPrivacyList(user);
        addActions();
    }

    private void initFields() {
        title = getString(com.aikya.konnek2.R.string.user_profile_title);
        dataManager = DataManager.getInstance();
        canPerformLogout.set(true);
        userId = getIntent().getExtras().getInt(QBServiceConsts.EXTRA_FRIEND_ID);
        user = QMUserService.getInstance().getUserCache().get((long) userId);
        userObserver = new UserObserver();
    }

    private void initUIWithUsersData() {
        loadAvatar();
        setName();
//        setPhone();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addObservers();
        setOnlineStatus();
        setBlockText();
    }

    /*Method that will set the text block/unblock
     * depending on the blockFlag's value..*/
    private void setBlockText() {
        if (blockFlag) {
            blockTv.setText(this.getString(R.string.unblockText));
        } else {
            blockTv.setText(this.getString(R.string.blockText));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        deleteObservers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    @OnClick(com.aikya.konnek2.R.id.send_message_button)
    void sendMessage(View view) {
        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupantForPrivateChat(user.getId());
        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
            QBChatDialog chatDialog = DialogTransformUtils.createQBDialogFromLocalDialog(dataManager, dialogOccupant.getDialog());
            PrivateDialogActivity.startWithClearTop(UserProfileActivity.this, user, chatDialog);
        } else {
            showProgress();
            QBCreatePrivateChatCommand.start(this, user);
        }
    }

    @OnClick(com.aikya.konnek2.R.id.audio_call_button)
    void audioCall(View view) {
        callToUser(QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO);
    }

    @OnClick(com.aikya.konnek2.R.id.video_call_button)
    void videoCall(View view) {
        callToUser(QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO);
    }

    @OnClick(com.aikya.konnek2.R.id.delete_chat_history_button)
    void deleteChatHistory(View view) {
        if (checkNetworkAvailableWithError()) {
            removeContactAndChatHistory = false;
            showRemoveChatHistoryDialog();
        }
    }

    /*@OnClick(com.aikya.konnek2.R.id.remove_contact_and_chat_history_button)
    void removeContactAndChatHistory(View view) {
        if (checkNetworkAvailableWithError()) {
            removeContactAndChatHistory = true;
            showRemoveContactAndChatHistoryDialog();
        }
    }*/

    @Override
    public void notifyChangedUserStatus(int userId, boolean online) {
        super.notifyChangedUserStatus(userId, online);
        if (user.getId() == userId) {
            if (online) {
                user.setLastRequestAt(new Date(System.currentTimeMillis()));
            }
            setOnlineStatus(online);
        }
    }

    @Override
    public void onConnectedToService(QBService service) {
        super.onConnectedToService(service);
        setOnlineStatus();
    }

    private void addObservers() {
        ((Observable) QMUserService.getInstance().getUserCache()).addObserver(userObserver);
    }

    private void deleteObservers() {
        ((Observable) QMUserService.getInstance().getUserCache()).deleteObserver(userObserver);
    }

    private void addActions() {
        addAction(QBServiceConsts.REMOVE_FRIEND_SUCCESS_ACTION, new RemoveFriendSuccessAction());
        addAction(QBServiceConsts.REMOVE_FRIEND_FAIL_ACTION, failAction);

        addAction(QBServiceConsts.DELETE_DIALOG_SUCCESS_ACTION, new RemoveChatSuccessAction());
        addAction(QBServiceConsts.DELETE_DIALOG_FAIL_ACTION, failAction);

        addAction(QBServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION, new CreatePrivateChatSuccessAction());
        addAction(QBServiceConsts.CREATE_PRIVATE_CHAT_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QBServiceConsts.REMOVE_FRIEND_SUCCESS_ACTION);
        removeAction(QBServiceConsts.REMOVE_FRIEND_FAIL_ACTION);

        removeAction(QBServiceConsts.DELETE_DIALOG_SUCCESS_ACTION);
        removeAction(QBServiceConsts.DELETE_DIALOG_FAIL_ACTION);

        removeAction(QBServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION);
        removeAction(QBServiceConsts.CREATE_PRIVATE_CHAT_FAIL_ACTION);

        updateBroadcastActionList();
    }

    private void setOnlineStatus() {
        if (friendListHelper != null) {
            setOnlineStatus(friendListHelper.isUserOnline(user.getId()));
        }
    }

    private void setOnlineStatus(boolean online) {

        if (user.getLastRequestAt() != null) {
            String offlineStatus = getString(com.aikya.konnek2.R.string.last_seen,
                    DateUtils.toTodayYesterdayShortDateWithoutYear2(user.getLastRequestAt().getTime()),
                    DateUtils.formatDateSimpleTime(user.getLastRequestAt().getTime()));
            timestampTextView.setText(OnlineStatusUtils.getOnlineStatus(this, online, offlineStatus));
        }
    }

    private void setName() {
        nameTextView.setText(user.getFullName());
    }

    /*private void setPhone() {
        if (user.getPhone() != null) {
            phoneView.setVisibility(View.VISIBLE);
        } else {
            phoneView.setVisibility(View.GONE);
        }
        phoneTextView.setText(user.getPhone());
    }*/

    private void loadAvatar() {
        String url = user.getAvatar();
        if (url != null && !TextUtils.isEmpty(url)) {
            ImageLoader.getInstance().displayImage(url, avatarImageView,
                    ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
        }
    }

    private void showRemoveContactAndChatHistoryDialog() {
        TwoButtonsDialogFragment.show(getSupportFragmentManager(),
                getString(com.aikya.konnek2.R.string.user_profile_remove_contact_and_chat_history, user.getFullName()),
                new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        showProgress();
                        if (isUserFriendOrUserRequest()) {
                            QBRemoveFriendCommand.start(UserProfileActivity.this, user.getId());
                        } else {
                            deleteChat();
                        }
                    }
                });
    }

    private void showRemoveChatHistoryDialog() {
        if (isChatExists()) {
            TwoButtonsDialogFragment.show(
                    getSupportFragmentManager(),
                    getString(com.aikya.konnek2.R.string.user_profile_delete_chat_history, user.getFullName()),
                    new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            showProgress();
                            deleteChat();
                        }
                    });
        } else {
            ToastUtils.longToast(com.aikya.konnek2.R.string.user_profile_chat_does_not_exists);
        }
    }

    private void deleteChat() {
        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupantForPrivateChat(user.getId());
        if (dialogOccupant == null) {
            finish();
        } else {
            QBChatDialog chatDialog = dataManager.getQBChatDialogDataManager().getByDialogId(dialogOccupant.getDialog().getDialogId());
            if (chatDialog != null) {
                QBDeleteChatCommand.start(this, chatDialog.getDialogId(), chatDialog.getType().getCode());
            }
        }
    }

    private void startPrivateChat(QBChatDialog qbDialog) {
        PrivateDialogActivity.start(UserProfileActivity.this, user, qbDialog);
    }

    private boolean isUserFriendOrUserRequest() {
        boolean isFriend = dataManager.getFriendDataManager().existsByUserId(user.getId());
        boolean isUserRequest = dataManager.getUserRequestDataManager().existsByUserId(user.getId());
        return isFriend || isUserRequest;
    }

    private boolean isChatExists() {
        return dataManager.getDialogOccupantDataManager().getDialogOccupantForPrivateChat(user.getId()) != null;
    }

    private void callToUser(QBRTCTypes.QBConferenceType qbConferenceType) {
        if (!isChatInitializedAndUserLoggedIn()) {
            ToastUtils.longToast(com.aikya.konnek2.R.string.call_chat_service_is_initializing);
            return;
        }

        boolean isFriend = DataManager.getInstance().getFriendDataManager().existsByUserId(user.getId());
        if (!isFriend) {
            ToastUtils.longToast(com.aikya.konnek2.R.string.dialog_user_is_not_friend);
            return;
        }

        List<QBUser> qbUserList = new ArrayList<>(1);
        qbUserList.add(UserFriendUtils.createQbUser(user));
        CallActivity.start(this, qbUserList, qbConferenceType, null);
    }

    private class UserObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            if (data != null && data.equals(QMUserCacheImpl.OBSERVE_KEY)) {
                user = QMUserService.getInstance().getUserCache().get((long) userId);
                initUIWithUsersData();
            }
        }
    }

    private class RemoveFriendSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            deleteChat();
        }
    }

    private class RemoveChatSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            hideProgress();

            if (removeContactAndChatHistory) {
                ToastUtils.longToast(getString(com.aikya.konnek2.R.string.user_profile_success_contacts_deleting, user.getFullName()));
                finish();
            } else {
                ToastUtils.longToast(getString(com.aikya.konnek2.R.string.user_profile_success_chat_history_deleting, user.getFullName()));
            }
        }
    }

    private class CreatePrivateChatSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            hideProgress();
            QBChatDialog qbDialog = (QBChatDialog) bundle.getSerializable(QBServiceConsts.EXTRA_DIALOG);
            startPrivateChat(qbDialog);
        }
    }

    /*Block/Unblock user in private chat.*/

    @OnClick(R.id.blockTv)
    void block(View view) {
        if (!blockFlag) {
            addtoprivacy();
        } else {
            removefromprivacy();
        }
    }


    @Override
    public void isUserBlocked(boolean blockFlag) {
        if (blockFlag) {
            blockTv.setText(this.getString(R.string.unblockText));
        } else {
            blockTv.setText(this.getString(R.string.blockText));
        }
    }

}