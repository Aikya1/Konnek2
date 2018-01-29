package com.android.konnek2.ui.activities.chats;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.konnek2.App;
import com.android.konnek2.R;
import com.android.konnek2.call.core.qb.commands.chat.QBLoadDialogByIdsCommand;
import com.android.konnek2.call.core.qb.helpers.QBFriendListHelper;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.core.utils.OnlineStatusUtils;
import com.android.konnek2.call.core.utils.UserFriendUtils;
import com.android.konnek2.call.services.QMUserCacheImpl;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.base.db.AppCallLogModel;
import com.android.konnek2.ui.activities.call.CallActivity;
import com.android.konnek2.ui.adapters.call.GroupCallUserAdapter;
import com.android.konnek2.ui.adapters.chats.GroupChatMessagesAdapter;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.AppPreference;
import com.android.konnek2.utils.ChatDialogUtils;
import com.android.konnek2.utils.DateUtils;
import com.android.konnek2.utils.ToastUtils;
import com.android.konnek2.utils.listeners.AppCommon;
import com.android.konnek2.utils.listeners.UserListInterface;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.request.QBPagedRequestBuilder;

import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCSessionDescription;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDialogActivity extends BaseDialogActivity implements UserListInterface {

    private static final String TAG = GroupDialogActivity.class.getSimpleName();
    private QBChatDialog qbChatDialogs;
    private List<Integer> OpponentsList;
    private QBPagedRequestBuilder requestBuilder;
    public QMUserCacheImpl qmUserCache;
    private ImageView userStatus;
    List<QBUser> qbUserList;
    List<QBUser> qbUserLists;
    private GroupCallUserAdapter groupCallUserAdapter;
    private HashMap<String, String> userOnlineStatus;
    private QBFriendListHelper qbFriendListHelper;
    protected QBService qbService;
    private boolean callType;
    private TextView textHeader;
    private List<String> seletedUsersLists;
    private android.app.AlertDialog alertDialog;
    private List<QMUser> qmUsers;
    private QMUser qmUserss;
    public QBRTCSessionDescription qbRtcSessionDescription;
    private Map<String, String> groupDialogMap;
    private AppCallLogModel appCallLogModel;
    private ArrayList<AppCallLogModel> appCallLogModelArrayList;
    QBUser CurrentUser;

    public static void start(Context context, ArrayList<QMUser> friends) {
        Intent intent = new Intent(context, GroupDialogActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_FRIENDS, friends);
        context.startActivity(intent);
    }

    public static void start(Context context, QBChatDialog chatDialog) {
        Intent intent = new Intent(context, GroupDialogActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, chatDialog);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        context.startActivity(intent);
    }

    public static void startForResult(Fragment context, QBChatDialog chatDialog, int requestCode) {
        Intent intent = new Intent(context.getActivity(), GroupDialogActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, chatDialog);
        context.startActivityForResult(intent, requestCode);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestBuilder = new QBPagedRequestBuilder();
        try {
            groupDialogMap = new HashMap<>();
            OpponentsList = new ArrayList<>();
            seletedUsersLists = new ArrayList<>();
            appCallLogModel = new AppCallLogModel();
            appCallLogModelArrayList = new ArrayList<AppCallLogModel>();
            qmUserCache = new QMUserCacheImpl(GroupDialogActivity.this);
            qbChatDialogs = (QBChatDialog) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_DIALOG);
            AppPreference.putDialogId(qbChatDialogs.getDialogId());
            groupDialogMap.put(QBServiceConsts.EXTRA_DIALOG_ID, qbChatDialogs.getDialogId());
            QBUser currentUser = getCurrentUser();
            OpponentsList = qbChatDialogs.getOccupants();
            OpponentsList.remove(currentUser.getId());
            qmUsers = qmUserCache.getUsersByIDs(OpponentsList);
            qbUserList = new ArrayList<>(qmUsers.size());
            qbUserList.addAll(UserFriendUtils.createQbUserList(qmUsers));
            actualizeCurrentDialogInfo();
            userOnlineStatus = new HashMap<>();
            qbRtcSessionDescription.setUserInfo(groupDialogMap);

        } catch (Exception e) {
            e.getMessage();
        }


    }

    private void actualizeCurrentDialogInfo() {
        if (currentChatDialog != null) {
            QBLoadDialogByIdsCommand.start(this, new ArrayList<>(Collections.singletonList(currentChatDialog.getDialogId())));
        }
    }

    @Override
    protected void initChatAdapter() {
        messagesAdapter = new GroupChatMessagesAdapter(this, currentChatDialog, combinationMessagesList);
    }

    @Override
    protected void initMessagesRecyclerView() {
        super.initMessagesRecyclerView();
        messagesRecyclerView.addItemDecoration(
                new StickyRecyclerHeadersDecoration(messagesAdapter));
        messagesRecyclerView.setAdapter(messagesAdapter);

        scrollMessagesToBottom(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_dialog_menu, menu);
        return true;
    }

    @Override
    protected void onConnectServiceLocally(QBService service) {
        onConnectServiceLocally();

//        setOnlineStatus(qmUsers.get(0));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (GroupDialogDetailsActivity.UPDATE_DIALOG_REQUEST_CODE == requestCode && GroupDialogDetailsActivity.RESULT_DELETE_GROUP == resultCode) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected Bundle generateBundleToInitDialog() {
        return null;
    }

    @Override
    protected void updateMessagesList() {

    }

    @Override
    protected void checkMessageSendingPossibility() {
        checkMessageSendingPossibility(isNetworkAvailable());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_group_details:
                GroupDialogDetailsActivity.start(this, currentChatDialog.getDialogId());

                break;
            case R.id.action_audio_call:
                callType = false;

                new UserOnLineOffLine().execute(qbUserList);
                break;
            case R.id.action_video_call:
                callType = true;
                new UserOnLineOffLine().execute(qbUserList);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void updateActionBar() {
        if (isNetworkAvailable() && currentChatDialog != null) {
            setActionBarTitle(ChatDialogUtils.getTitleForChatDialog(currentChatDialog, dataManager));
            checkActionBarLogo(currentChatDialog.getPhoto(), R.drawable.placeholder_group);
        }
    }

    @Override
    protected void initFields() {
        super.initFields();
        if (currentChatDialog != null) {
            title = ChatDialogUtils.getTitleForChatDialog(currentChatDialog, dataManager);
        }
    }

    public void sendMessage(View view) {
        sendMessage();
    }

    public void callToUser(List<String> opponentUsers, QBRTCTypes.QBConferenceType qbConferenceType) {

        try {
            if (!isChatInitializedAndUserLoggedIn()) {
                ToastUtils.longToast(R.string.call_chat_service_is_initializing);
                return;
            }
            List<QMUser> qmUsers = qmUserCache.getUsersByIDs(getUserIntegerId(opponentUsers));
            qbUserLists = new ArrayList<>(qmUsers.size());
            qbUserLists.addAll(UserFriendUtils.createQbUserList(qmUsers));
            CallActivity.start(GroupDialogActivity.this, qbUserLists, qbConferenceType, qbRtcSessionDescription);

        } catch (Exception e) {
            e.getMessage();
        }

        CallHistory();
    }

    private void CallHistory() {


        AppPreference.putUserName(getCurrentUser().getFullName());
        AppConstant.OPPONENTS = qbChatDialogs.getName();
        AppConstant.STATUS = AppConstant.CALL_STATUS_DIALED;
        AppConstant.DATE = AppCommon.currentDate();
        AppConstant.TIME = AppCommon.currentTime();

        appCallLogModel.setCallUserName(CurrentUser.getFullName());
        appCallLogModel.setUserId(String.valueOf(getCurrentUser().getId()));
        appCallLogModel.setCallOpponentName(AppConstant.OPPONENTS);
        appCallLogModel.setCallOpponentId(String.valueOf(""));
        appCallLogModel.setCallDate(AppConstant.DATE);
        appCallLogModel.setCallTime(AppConstant.TIME);
        appCallLogModel.setCallStatus(AppConstant.STATUS);
        appCallLogModel.setCallPriority(AppConstant.CALL_PRIORITY);
        appCallLogModel.setCallType(AppConstant.CALL_TYPES);
        appCallLogModelArrayList.add(appCallLogModel);
        App.appcallLogTableDAO.saveCallLog(appCallLogModelArrayList);
    }

    private ArrayList<Integer> getUserIntegerId(List<String> opponentUsers) {
        ArrayList<Integer> userId = new ArrayList<>();
        for (int i = 0; i < opponentUsers.size(); i++) {
            userId.add(Integer.parseInt(opponentUsers.get(i)));
        }

        return userId;
    }

    public static QBUser getCurrentUser() {
        return QBChatService.getInstance().getUser();
    }


    public void groupCallDialog(HashMap<String, String> userOnlineStatus) {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.item_group_list, null);
        final TextView buttonOk = alertLayout.findViewById(R.id.textview_group_ok);
        final TextView buttonCancel = alertLayout.findViewById(R.id.textview_group_cancel);
        final TextView textHeader = alertLayout.findViewById(R.id.textview_group_header);
        final ListView listView = alertLayout.findViewById(R.id.group_list);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setTitle("Info");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();

        if (callType)
            textHeader.setText(getResources().getString(R.string.outgoing_video_conference));
        else
            textHeader.setText(getResources().getString(R.string.outgoing_audio_conference));

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (!seletedUsersLists.isEmpty() && seletedUsersLists != null) {
                        if (callType) {

                            callToUser(seletedUsersLists, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO);
                        } else {

                            callToUser(seletedUsersLists, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO);
                        }
                        dialog.dismiss();
                    } else {
                        AppCommon.displayToast("Select at least one user to make call");
                        dialog.show();
                    }

                } catch (Exception e) {
                    e.getMessage();
                }

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        groupCallUserAdapter = new GroupCallUserAdapter(GroupDialogActivity.this, qbUserList, userOnlineStatus, this, qmUsers);
        listView.setAdapter(groupCallUserAdapter);
        dialog.show();

    }

    @Override
    public void seletedUsers(List<String> seletedUsersList) {
        seletedUsersLists.clear();
        seletedUsersLists.addAll(seletedUsersList);

    }

    private class UserOnLineOffLine extends AsyncTask <List<QBUser>, Void, HashMap<String, String>> {

        @Override
        protected void onPreExecute() {
            userOnlineStatus.clear();
            super.onPreExecute();
        }

        protected HashMap<String, String> doInBackground(List<QBUser>... qbUserList) {
            for (int i = 0; i < qbUserList[0].size(); i++) {

                if (qbUserList != null && !qbUserList[0].isEmpty()) {

                    if (friendListHelper != null) {
                        String offlineStatus = getString(R.string.last_seen, DateUtils.toTodayYesterdayShortDateWithoutYear2(qbUserList[0].get(i).getLastRequestAt().getTime()),
                                DateUtils.formatDateSimpleTime(qbUserList[0].get(i).getLastRequestAt().getTime()));
                        userOnlineStatus.put("" + qbUserList[0].get(i).getId(), OnlineStatusUtils.getOnlineStatus(GroupDialogActivity.this, friendListHelper.isUserOnline(qbUserList[0].get(i).getId()), offlineStatus));

                    }

                }
            }
            return userOnlineStatus;
        }

        protected void onPostExecute(HashMap<String, String> userStatus) {

            groupCallDialog(userStatus);

        }
    }


}