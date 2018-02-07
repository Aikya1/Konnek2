package com.android.konnek2.base.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.konnek2.App;
import com.android.konnek2.R;
import com.android.konnek2.call.core.models.AppSession;
import com.android.konnek2.call.core.utils.ChatUtils;
import com.android.konnek2.call.core.utils.ConstsCore;
import com.android.konnek2.call.core.utils.UserFriendUtils;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.db.models.DialogOccupant;
import com.android.konnek2.call.services.QMUserCacheImpl;
import com.android.konnek2.call.services.QMUserService;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.base.db.AppCallLogModel;
import com.android.konnek2.base.db.AppContactAdapter;
import com.android.konnek2.ui.activities.call.CallActivity;
import com.android.konnek2.ui.activities.chats.GroupDialogActivity;
import com.android.konnek2.ui.activities.chats.PrivateDialogActivity;
import com.android.konnek2.ui.fragments.base.BaseFragment;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.AppPreference;
import com.android.konnek2.utils.listeners.AppCommon;
import com.android.konnek2.utils.listeners.ContactInterface;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Lenovo on 10-01-2018.
 */

public class AppKonnek2ContactFragment extends BaseFragment implements ContactInterface {

    public static final int PICK_DIALOG = 100;
    private DataManager dataManager;
    public Bundle returnedBundle;
    public QBRequestGetBuilder qbRequestGetBuilder;
    private AppContactAdapter appContactAdapter;
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
    private ProgressBar progressBar;

    public AppKonnek2ContactFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_konnek2_contact, container, false);
        listView = (ListView) view.findViewById(R.id.listview_contacts);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_contact);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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


        try {
            new getAllContactsAsyn().execute();
            new getDialogListAsyn().execute();
            searchUsers();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //?
    private void searchUsers() {

        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        requestBuilder.setPage(page);
        requestBuilder.setPerPage(ConstsCore.FL_FRIENDS_PER_PAGE);


        //??
        QMUserService.getInstance().getUsersByPhoneNumbers(contactNumberList, requestBuilder, true).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<List<QMUser>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<QMUser> qbUsers) {

                    }
                });

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
    public void contactChat(String dialogId) {


        qbUser = AppSession.getSession().getUser();
        QBChatDialog qbChatDialog = null;
        String DialogId = dialogId;
        if (!DialogId.isEmpty() && DialogId != null) {
            qbChatDialog = dataManager.getQBChatDialogDataManager().getByDialogId(DialogId);
            if (qbChatDialog.getOccupants().size() >= 3) {
                Log.d("", "contactGroup");
                startGroupChatActivity(qbChatDialog);
            } else {
                startPrivateChatActivity(qbChatDialog);
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
//            PrivateDialogActivity.startForResult(this, opponent, chatDialog, PICK_DIALOG);
            PrivateDialogActivity.start(getContext(), opponent, chatDialog);
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


    private class getDialogListAsyn extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            try {

                getDialogs(qbRequestGetBuilder, returnedBundle);
            } catch (QBResponseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressBar.setVisibility(View.GONE);
            privateDialogUsersList();

        }
    }


    public void privateDialogUsersList() {

        try {

            for (int i = 0; i < qbDialogsList.size(); i++) {
                if (qbDialogsList.get(i).getType().equals(QBDialogType.PRIVATE)) {
                    qbPrivateDialogsList.add(qbDialogsList.get(i));
                }
            }
            appContactAdapter = new AppContactAdapter(getActivity(), qbPrivateDialogsList, AppKonnek2ContactFragment.this);
            if (appContactAdapter.getCount() > 0) {
                listView.setAdapter(appContactAdapter);
            }
        } catch (Exception e) {
            e.getMessage();
        }


    }

    public List<QBChatDialog> getDialogs(QBRequestGetBuilder qbRequestGetBuilder, Bundle returnedBundle) throws QBResponseException {

        qbDialogsList = QBRestChatService.getChatDialogs(null, qbRequestGetBuilder).perform();
        return qbDialogsList;
    }

    //??
    private class getAllContactsAsyn extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... arg0) {

            getAllContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

        }
    }


    //??
    private void getAllContacts() {     // get Mobile Contacts

        String Number = null;
        if (getActivity() != null) {
            ContentResolver contentResolver = getActivity().getContentResolver();
            String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
                    + ("1") + "'";
            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                    + " COLLATE LOCALIZED ASC";


            Cursor cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI, null, selection
                            + " AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER
                            + "=1", null, sortOrder);
//        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id},
                                null);
                        if (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Number = phoneNumber;
                        }

                        phoneCursor.close();
                        String formattedNumber = PhoneNumberUtils.formatNumber(Number);
                        contactNumberList.add(formattedNumber);
                        appCallLogModel.setContactName(name);
                        appCallLogModel.setContactNumber(formattedNumber);
                        phoneContactList.add(appCallLogModel);
                    }
                }

            }

        }
    }
}
