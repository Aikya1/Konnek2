
package com.aikya.konnek2.ui.activities.chats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aikya.konnek2.App;
import com.aikya.konnek2.call.core.core.command.Command;
import com.aikya.konnek2.call.core.qb.commands.friend.QBRejectFriendCommand;
import com.aikya.konnek2.call.db.managers.FriendDataManager;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.utils.AppPreference;
import com.aikya.konnek2.utils.listeners.AppCommon;
import com.aikya.konnek2.R;
import com.aikya.konnek2.base.db.AppCallLogModel;
import com.aikya.konnek2.call.core.qb.commands.friend.QBAcceptFriendCommand;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.core.utils.OnlineStatusUtils;
import com.aikya.konnek2.call.core.utils.UserFriendUtils;
import com.aikya.konnek2.call.db.managers.DataManager;
import com.aikya.konnek2.call.db.models.Friend;
import com.aikya.konnek2.call.services.QMUserService;
import com.aikya.konnek2.ui.activities.call.CallActivity;
import com.aikya.konnek2.ui.activities.profile.UserProfileActivity;
import com.aikya.konnek2.ui.adapters.chats.PrivateChatMessageAdapter;
import com.aikya.konnek2.ui.fragments.dialogs.base.TwoButtonsDialogFragment;
import com.aikya.konnek2.utils.AppConstant;
import com.aikya.konnek2.utils.DateUtils;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.listeners.CallDurationInterface;
import com.aikya.konnek2.utils.listeners.FriendOperationListener;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.OnClick;

public class PrivateDialogActivity extends BaseDialogActivity {

    private FriendOperationAction friendOperationAction;
    private QMUser opponentUser;
    private FriendObserver friendObserver;
    private BroadcastReceiver typingMessageBroadcastReceiver;
    private int operationItemPosition;
    private final String TAG = PrivateDialogActivity.class.getSimpleName();
    private AppCallLogModel appCallLogModel;
    private ArrayList<AppCallLogModel> appCallLogModelArrayList;
    private String callType, time, date;
    private RadioGroup priorityGroup;
    private android.app.AlertDialog alertDialog;
    private ProgressBar progress;
    private String CallingPriority, CallingPrioritytxt;
    private RadioButton highPriority, mediumPriority, lowPriority, other;
    private TextView okTxt, cancelTxt;
    private EditText otherEditTxt;
    //    private QBChatDialog qbChatDialog;
    private CallDurationInterface callDurationInterface;

    public static void start(Context context, QMUser opponent, QBChatDialog chatDialog) {
        Intent intent = getIntentWithExtra(context, opponent, chatDialog);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        context.startActivity(intent);
    }

    public static void startWithClearTop(Context context, QMUser opponent, QBChatDialog chatDialog) {
        Intent intent = getIntentWithExtra(context, opponent, chatDialog);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void startForResult(Fragment fragment, QMUser opponent, QBChatDialog chatDialog,
                                      int requestCode) {
        Intent intent = getIntentWithExtra(fragment.getContext(), opponent, chatDialog);
        fragment.startActivityForResult(intent, requestCode);
    }

    private static Intent getIntentWithExtra(Context context, QMUser opponent, QBChatDialog chatDialog) {
        Intent intent = new Intent(context, PrivateDialogActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_OPPONENT, opponent);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, chatDialog);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void addActions() {
        super.addActions();

        addAction(QBServiceConsts.ACCEPT_FRIEND_SUCCESS_ACTION, new AcceptFriendSuccessAction());
        addAction(QBServiceConsts.ACCEPT_FRIEND_FAIL_ACTION, failAction);

        addAction(QBServiceConsts.REJECT_FRIEND_SUCCESS_ACTION, new RejectFriendSuccessAction());
        addAction(QBServiceConsts.REJECT_FRIEND_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    @Override
    protected void updateActionBar() {
        setOnlineStatus(opponentUser);

        checkActionBarLogo(opponentUser.getAvatar(), R.drawable.placeholder_user);
    }

    @Override
    protected void onConnectServiceLocally(QBService service) {
        onConnectServiceLocally();
        setOnlineStatus(opponentUser);
    }

    @Override
    protected Bundle generateBundleToInitDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt(QBServiceConsts.EXTRA_OPPONENT_ID, opponentUser.getId());
        return bundle;
    }

    @Override
    protected void initChatAdapter() {
        messagesAdapter = new PrivateChatMessageAdapter(this, combinationMessagesList, friendOperationAction, currentChatDialog);
    }

    @Override
    protected void initMessagesRecyclerView() {
        super.initMessagesRecyclerView();
        messagesRecyclerView.addItemDecoration(
                new StickyRecyclerHeadersDecoration(messagesAdapter));
        findLastFriendsRequest(true);

        messagesRecyclerView.setAdapter(messagesAdapter);
        scrollMessagesToBottom(0);
    }

    @Override
    protected void updateMessagesList() {
        findLastFriendsRequest(false);
    }

    @Override
    public void notifyChangedUserStatus(int userId, boolean online) {
        super.notifyChangedUserStatus(userId, online);

        if (opponentUser != null && opponentUser.getId() == userId) {
            if (online) {
                //gets opponentUser from DB with updated field 'last_request_at'
                actualizeOpponentUserFromDb();
            }

            setOnlineStatus(opponentUser);
        }
    }

    private void actualizeOpponentUserFromDb() {
        QMUser opponentUserFromDb = QMUserService.getInstance().getUserCache().get((long) opponentUser.getId());

        if (opponentUserFromDb != null) {
            opponentUser = opponentUserFromDb;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.private_dialog_menu, menu);
        return true;
    }


    //Overidden method that will populate the options on the toolbar
    //the user can make a audio/video call by clicking on any of the
    //options. ( Upper layout )
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       /* boolean isFriend = DataManager.getInstance().getFriendDataManager().getByUserId(
                opponentUser.getId()) != null;*/
        if (DataManager.getInstance().getFriendDataManager().getByUserId(opponentUser.getId()) == null) {
            QMUser user = QMUserService.getInstance().getUserCache().get((long) opponentUser.getId());
            Friend friend = new Friend();
            friend.setFriendId(opponentUser.getId());
            friend.setUser(user);
            DataManager.getInstance().getFriendDataManager().createOrUpdate(friend);
        }

        switch (item.getItemId()) {
            case R.id.action_audio_call:
                AppConstant.CALL_TYPES = AppConstant.CALL_AUDIO;
                callToUser(opponentUser, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO);
                break;
            case R.id.switch_camera_toggle:
                AppConstant.CALL_TYPES = AppConstant.CALL_VIDEO;
                callToUser(opponentUser, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO);
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void checkMessageSendingPossibility() {
        boolean enable = dataManager.getFriendDataManager().existsByUserId(opponentUser.getId()) && isNetworkAvailable();
//        checkMessageSendingPossibility(enable);
        checkMessageSendingPossibility(true);
    }

    @OnClick(R.id.toolbar)
    void openProfile(View view) {
        UserProfileActivity.start(this, opponentUser.getId());
    }

    @Override
    protected void initFields() {
        super.initFields();
        appCallLogModel = new AppCallLogModel();
        appCallLogModelArrayList = new ArrayList<AppCallLogModel>();
        friendOperationAction = new FriendOperationAction();
        friendObserver = new FriendObserver();
        typingMessageBroadcastReceiver = new TypingStatusBroadcastReceiver();
        opponentUser = (QMUser) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_OPPONENT);
        title = opponentUser.getFullName();

    }

    @Override
    protected void registerBroadcastReceivers() {
        super.registerBroadcastReceivers();
        localBroadcastManager.registerReceiver(typingMessageBroadcastReceiver,
                new IntentFilter(QBServiceConsts.TYPING_MESSAGE));
    }

    @Override
    protected void unregisterBroadcastReceivers() {
        super.unregisterBroadcastReceivers();
        localBroadcastManager.unregisterReceiver(typingMessageBroadcastReceiver);
    }

    @Override
    protected void addObservers() {
        super.addObservers();
        dataManager.getFriendDataManager().addObserver(friendObserver);
    }

    @Override
    protected void deleteObservers() {
        super.deleteObservers();
        dataManager.getFriendDataManager().deleteObserver(friendObserver);
    }

    private void findLastFriendsRequest(boolean needNotifyAdapter) {
        ((PrivateChatMessageAdapter) messagesAdapter).findLastFriendsRequestMessagesPosition();
        if (needNotifyAdapter) {
            messagesAdapter.notifyDataSetChanged();
        }
    }

    private void setOnlineStatus(QMUser user) {
        if (user != null) {
            if (friendListHelper != null && user.getLastRequestAt() != null) {
                String offlineStatus = getString(R.string.last_seen, DateUtils.toTodayYesterdayShortDateWithoutYear2(user.getLastRequestAt().getTime()),
                        DateUtils.formatDateSimpleTime(user.getLastRequestAt().getTime()));
                setActionBarSubtitle(
                        OnlineStatusUtils.getOnlineStatus(this, friendListHelper.isUserOnline(user.getId()), offlineStatus));
//                setActionBarSubtitle("R.string.last_seen  at 16:38");

            } else {
                setActionBarSubtitle("R.string.last_seen  at 16:38");
            }
        }
    }

    public void sendMessage(View view) {
        sendMessage();
    }


    //Method that will initiate the audio call
    public void callToUser(QMUser user, QBRTCTypes.QBConferenceType qbConferenceType) {

        try {
            if (!isChatInitializedAndUserLoggedIn()) {
                ToastUtils.longToast(R.string.call_chat_service_is_initializing);
                return;
            }
            List<QBUser> qbUserList = new ArrayList<>(1);
            qbUserList.add(UserFriendUtils.createQbUser(user));

            Log.d("PrivateDialogCALL", "callToUser" + qbUserList.get(0).getId());
            CallActivity.start(PrivateDialogActivity.this, qbUserList, qbConferenceType, null);
            CallHistory();

        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void acceptUser(final int userId) {

        if (isNetworkAvailable()) {
            if (!isChatInitializedAndUserLoggedIn()) {
                ToastUtils.longToast(R.string.call_chat_service_is_initializing);
                return;
            }
            showProgress();
            QBAcceptFriendCommand.start(this, userId);
        } else {
            ToastUtils.longToast(R.string.dlg_fail_connection);
            return;
        }
    }

    private void rejectUser(final int userId) {
        if (isNetworkAvailable()) {
            if (!isChatInitializedAndUserLoggedIn()) {
                ToastUtils.longToast(R.string.call_chat_service_is_initializing);
                return;
            }

            showRejectUserDialog(userId);
        } else {
            ToastUtils.longToast(R.string.dlg_fail_connection);
            return;
        }
    }

    private void showRejectUserDialog(final int userId) {
        QMUser user = QMUserService.getInstance().getUserCache().get((long) userId);
        if (user == null) {
            return;
        }

        TwoButtonsDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.dialog_message_reject_friend, user.getFullName()),
                new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        showProgress();
                        QBRejectFriendCommand.start(PrivateDialogActivity.this, userId);
                    }
                });
    }

    private void updateCurrentChatFromDB() {
        QBChatDialog updatedDialog = null;
        if (currentChatDialog != null) {
            updatedDialog = dataManager.getQBChatDialogDataManager().getByDialogId(currentChatDialog.getDialogId());
        } else {
            finish();
        }

        if (updatedDialog == null) {
            finish();
        } else {
            currentChatDialog = updatedDialog;
            initCurrentDialog();
        }
    }

    private void showTypingStatus() {
        setActionBarSubtitle(R.string.dialog_now_typing);
    }

    private void hideTypingStatus() {
        setOnlineStatus(opponentUser);
    }

    private class FriendOperationAction implements FriendOperationListener {


        @Override
        public void onAcceptUserClicked(int position, int userId) {
            operationItemPosition = position;
            acceptUser(userId);
        }

        @Override
        public void onRejectUserClicked(int position, int userId) {
            operationItemPosition = position;
            rejectUser(userId);
        }
    }

    private class AcceptFriendSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            ((PrivateChatMessageAdapter) messagesAdapter).clearLastRequestMessagePosition();
            messagesAdapter.notifyItemChanged(operationItemPosition);
            startLoadDialogMessages(false);
            hideProgress();
        }
    }

    private class RejectFriendSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            ((PrivateChatMessageAdapter) messagesAdapter).clearLastRequestMessagePosition();
            messagesAdapter.notifyItemChanged(operationItemPosition);
            startLoadDialogMessages(false);
            hideProgress();
        }
    }

    private class FriendObserver implements Observer {
        @Override
        public void update(Observable observable, Object data) {
            if (data != null) {
                String observerKey = ((Bundle) data).getString(FriendDataManager.EXTRA_OBSERVE_KEY);
                if (observerKey.equals(dataManager.getFriendDataManager().getObserverKey())) {
                    updateCurrentChatFromDB();
                    checkMessageSendingPossibility();
                }
            }
        }
    }

    private class TypingStatusBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            int userId = extras.getInt(QBServiceConsts.EXTRA_USER_ID);
            // TODO: now it is possible only for Private chats
            if (currentChatDialog != null && opponentUser != null && userId == opponentUser.getId()) {
                if (QBDialogType.PRIVATE.equals(currentChatDialog.getType())) {
                    boolean isTyping = extras.getBoolean(QBServiceConsts.EXTRA_IS_TYPING);
                    if (isTyping) {
                        showTypingStatus();
                    } else {
                        hideTypingStatus();
                    }
                }
            }
        }
    }


    public void CallHistory() {

        QBUser CurrentUser = QBChatService.getInstance().getUser();
        AppPreference.putUserName(CurrentUser.getFullName());
        AppConstant.OPPONENTS = opponentUser.getFullName();
        AppConstant.DATE = AppCommon.currentDate();
        AppConstant.TIME = AppCommon.currentTime();

        appCallLogModel.setCallUserName(CurrentUser.getFullName());
        appCallLogModel.setUserId(String.valueOf(CurrentUser.getId()));
        appCallLogModel.setCallOpponentName(AppConstant.OPPONENTS);
        appCallLogModel.setCallOpponentId(String.valueOf(opponentUser.getId()));
        appCallLogModel.setCallDate(AppConstant.DATE);
        appCallLogModel.setCallTime(AppConstant.TIME);
        appCallLogModel.setCallStatus(AppConstant.CALL_STATUS_DIALED);
        appCallLogModel.setCallPriority(" ");
        appCallLogModel.setCallDuration("0");
        appCallLogModel.setCallType(AppConstant.CALL_TYPES);
        appCallLogModelArrayList.add(appCallLogModel);
        App.appcallLogTableDAO.saveCallLog(appCallLogModelArrayList);
        ((CallActivity) getApplicationContext()).callDuration(CurrentUser.getFullName(), AppCommon.currentDate(), AppCommon.currentTime());

    }


    private boolean IsValidate() {
        try {
            if (priorityGroup.getCheckedRadioButtonId() == -1) {
                AppCommon.displayToast("Select call Priority ");
                return false;
            }
//            if (otherEditTxt.getText().toString() == null || otherEditTxt.getText().toString().length() < 0) {
//                AppCommon.displayToast("Please enter message ");
//                return false;
//            }

        } catch (Resources.NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }
}