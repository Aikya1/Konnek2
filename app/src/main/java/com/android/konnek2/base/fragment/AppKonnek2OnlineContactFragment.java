
package com.android.konnek2.base.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.konnek2.App;
import com.android.konnek2.R;
import com.android.konnek2.call.core.models.AppSession;
import com.android.konnek2.call.core.utils.ChatUtils;
import com.android.konnek2.call.core.utils.UserFriendUtils;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.db.models.DialogOccupant;
import com.android.konnek2.call.services.QMUserCacheImpl;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.base.activity.MessageEvent;
import com.android.konnek2.base.db.AppCallLogModel;
import com.android.konnek2.base.db.AppOnlineContactAdapter;
import com.android.konnek2.ui.activities.call.CallActivity;
import com.android.konnek2.ui.activities.chats.GroupDialogActivity;
import com.android.konnek2.ui.activities.chats.PrivateDialogActivity;
import com.android.konnek2.ui.fragments.base.BaseFragment;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.AppPreference;
import com.android.konnek2.utils.GlobalBus;
import com.android.konnek2.utils.listeners.AppCommon;
import com.android.konnek2.utils.listeners.ContactInterface;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 12-01-2018.
 */

public class AppKonnek2OnlineContactFragment extends BaseFragment implements ContactInterface {


    public static final int PICK_DIALOG = 100;
    private DataManager dataManager;
    public Bundle returnedBundle;
    public QBRequestGetBuilder qbRequestGetBuilder;
    private AppOnlineContactAdapter appOnlineContactAdapter;
    private List<QBChatDialog> qbDialogsList;
    private List<QBChatDialog> qbPrivateDialogsList;
    private ListView listView;
    private AppCallLogModel appCallLogModel;
    private ArrayList<AppCallLogModel> phoneContactList;
    private List<String> contactUsersList;
    List<String> contactNumberList;
    private List<QBUser> qbUserLists;
    private List<QMUser> qMUserList;
    private ArrayList<AppCallLogModel> appCallLogModelArrayList;
    private int page = 1;
    private QBUser qbUser;
    private MenuItem menuItemVideoCall;
    private MenuItem menuItemAudioCall;
    public QMUserCacheImpl qmUserCache;
    private List<QMUser> qmUsersList;
    private List<Integer> OpponentsList;

    public AppKonnek2OnlineContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        GlobalBus.getBus().register(this);
        View view = inflater.inflate(R.layout.fragment_konnek2_online_contact, container, false);
        listView = (ListView) view.findViewById(R.id.listview_online_contacts);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        qmUserCache = new QMUserCacheImpl(getActivity());
        returnedBundle = new Bundle();
        dataManager = DataManager.getInstance();
        qbRequestGetBuilder = new QBRequestGetBuilder();
        phoneContactList = new ArrayList<>();
        contactNumberList = new ArrayList<>();
        qbDialogsList = new ArrayList<>();
        qbPrivateDialogsList = new ArrayList<>();
        contactUsersList = new ArrayList<>();
        qbUserLists = new ArrayList<>();
        qMUserList = new ArrayList<>();
        qmUsersList = new ArrayList<>();
        OpponentsList = new ArrayList<>();
        appCallLogModel = new AppCallLogModel();
        appCallLogModelArrayList = new ArrayList<>();


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }


    @Override
    public void contactUsersList(List<String> usersLists) {

        contactUsersList.clear();
        contactUsersList.addAll(usersLists);
        if (!contactUsersList.isEmpty() && contactUsersList.size() >= 1 && contactUsersList != null) {
            menuItemVideoCall.setVisible(true);
            menuItemAudioCall.setVisible(true);
        } else {
            menuItemVideoCall.setVisible(false);
            menuItemAudioCall.setVisible(false);
        }

    }

    @Override
    public void contactChat(String contactGroupDialog) {

        qbUser = AppSession.getSession().getUser();
        QBChatDialog contactChatDialog;
        String DialogId;
        DialogId = contactGroupDialog;
        if (!DialogId.isEmpty() && DialogId != null) {

            contactChatDialog = dataManager.getQBChatDialogDataManager().getByDialogId(DialogId);
            if (contactChatDialog.getOccupants().size() >= 3) {
                Log.d("", "contactGroup");
                startGroupChatActivity(contactChatDialog);
            } else {
                startPrivateChatActivity(contactChatDialog);
            }

        }

    }


    private void startGroupChatActivity(QBChatDialog chatDialog) {
        GroupDialogActivity.startForResult(this, chatDialog, PICK_DIALOG);
    }

    private void startPrivateChatActivity(QBChatDialog chatDialog) {

        List<DialogOccupant> occupantsList = dataManager.getDialogOccupantDataManager()
                .getDialogOccupantsListByDialogId(chatDialog.getDialogId());

        QMUser opponent = ChatUtils.getOpponentFromPrivateDialog(UserFriendUtils.createLocalUser(qbUser), occupantsList);
        if (!TextUtils.isEmpty(chatDialog.getDialogId())) {
            PrivateDialogActivity.startForResult(this, opponent, chatDialog, PICK_DIALOG);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.contact_users_menu, menu);
        menuItemVideoCall = menu.findItem(R.id.switch_camera_toggle);
        menuItemAudioCall = menu.findItem(R.id.action_audio_call);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_audio_call:

                AppConstant.CALL_TYPES = AppConstant.CALL_AUDIO;
                callToUser(contactUsersList, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO);

                break;
            case R.id.switch_camera_toggle:

                AppConstant.CALL_TYPES = AppConstant.CALL_VIDEO;
                callToUser(contactUsersList, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO);

                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void callToUser(List<String> opponentsList, QBRTCTypes.QBConferenceType qbConferenceType) {


        try {

//            if (!isChatInitializedAndUserLoggedIn()) {
//                ToastUtils.longToast(R.string.call_chat_service_is_initializing);
//                return;
//            }
            qMUserList = qmUserCache.getUsersByIDs(getUserIntegerId(opponentsList));
            qbUserLists = new ArrayList<>(qMUserList.size());
            qbUserLists.addAll(UserFriendUtils.createQbUserList(qMUserList));
            CallActivity.start(getActivity(), qbUserLists, qbConferenceType, null);
            AppConstant.OPPONENTS = qbUserLists.get(0).getFullName();
            AppConstant.OPPONENT_ID = String.valueOf(qbUserLists.get(0).getId());
        } catch (Exception e) {
            e.getMessage();
        }

        CallHistory();
    }

    private ArrayList<Integer> getUserIntegerId(List<String> opponentUsers) {
        ArrayList<Integer> userId = new ArrayList<>();
        for (int i = 0; i < opponentUsers.size(); i++) {
            userId.add(Integer.parseInt(opponentUsers.get(i)));
        }

        return userId;
    }

    @Subscribe
    public void onEvent(MessageEvent event) {

        qbPrivateDialogsList = event.qbChatDialogList;
        if (qbPrivateDialogsList != null && !qbPrivateDialogsList.isEmpty()) {
            appOnlineContactAdapter = new AppOnlineContactAdapter(getActivity(), qbPrivateDialogsList, this);
            listView.setAdapter(appOnlineContactAdapter);
        }

    }


    public void CallHistory() {

        QBUser CurrentUser = AppSession.getSession().getUser();
        AppPreference.putUserName(CurrentUser.getFullName());
        AppConstant.DATE = AppCommon.currentDate();
        AppConstant.TIME = AppCommon.currentTime();

        appCallLogModel.setCallUserName(CurrentUser.getFullName());
        appCallLogModel.setUserId(String.valueOf(CurrentUser.getId()));
        appCallLogModel.setCallOpponentName(AppConstant.OPPONENTS);
        appCallLogModel.setCallOpponentId(String.valueOf(AppConstant.OPPONENT_ID));
        appCallLogModel.setCallDate(AppConstant.DATE);
        appCallLogModel.setCallTime(AppConstant.TIME);
        appCallLogModel.setCallStatus(AppConstant.CALL_STATUS_DIALED);
        appCallLogModel.setCallPriority(" ");
        appCallLogModel.setCallDuration("0");
        appCallLogModel.setCallType(AppConstant.CALL_TYPES);
        appCallLogModelArrayList.add(appCallLogModel);
        App.appcallLogTableDAO.saveCallLog(appCallLogModelArrayList);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GlobalBus.getBus().unregister(this);

    }
}
