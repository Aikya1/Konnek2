package com.aikya.konnek2.base.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aikya.konnek2.call.core.core.command.Command;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.qb.helpers.QBChatHelper;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.base.BaseActivity;
import com.aikya.konnek2.ui.activities.chats.PrivateDialogActivity;
import com.aikya.konnek2.ui.adapters.friends.FriendsAdapter;
import com.aikya.konnek2.ui.fragments.base.BaseFragment;
import com.aikya.konnek2.R;
import com.aikya.konnek2.base.db.AppCallLogModel;
import com.aikya.konnek2.call.core.qb.commands.chat.QBCreatePrivateChatCommand;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.db.managers.DataManager;
import com.aikya.konnek2.call.db.models.DialogOccupant;
import com.aikya.konnek2.call.db.models.Friend;
import com.aikya.konnek2.call.db.utils.DialogTransformUtils;
import com.aikya.konnek2.call.services.QMUserCacheImpl;
import com.aikya.konnek2.utils.listeners.simple.SimpleOnRecycleItemClickListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.server.Performer;
import com.quickblox.extensions.RxJavaPerformProcessor;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 */

public class AppKonnek2ContactFragment extends BaseFragment {

    private static final String TAG = AppKonnek2ContactFragment.class.getSimpleName();
    private DataManager dataManager;
    public Bundle returnedBundle;
    public QBRequestGetBuilder qbRequestGetBuilder;
    private RecyclerView listView;
    private AppCallLogModel appCallLogModel;
    List<String> contactNumberList;
    private List<QBUser> qbUserLists;
    private List<QMUser> qMUserList;
    private ArrayList<AppCallLogModel> appCallLogModelArrayList;
    public QMUserCacheImpl qmUserCache;
    protected BaseActivity.FailAction failAction;
    private QBChatHelper chatHelper;
    private FriendsAdapter friendsAdapter;
    private QMUser selectedUser;
    private Handler mHandler;


    public AppKonnek2ContactFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_konnek2_contact, container, false);
        listView = view.findViewById(R.id.listview_contacts);
        chatHelper = new QBChatHelper(getContext());
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        addActions();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        qmUserCache = new QMUserCacheImpl(getActivity());
        returnedBundle = new Bundle();
        dataManager = DataManager.getInstance();
        qbRequestGetBuilder = new QBRequestGetBuilder();
        contactNumberList = new ArrayList<>();
        qbUserLists = new ArrayList<>();
        qMUserList = new ArrayList<>();
        appCallLogModel = new AppCallLogModel();
        appCallLogModelArrayList = new ArrayList<>();

        getRegisteredUsersFromQBAddressBook();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    /*================+++TEST CODE+++==================*/
    private void getRegisteredUsersFromQBAddressBook() {
        baseActivity.showProgress();
        String UDID = "";
        boolean isCompact = false;
        Performer<ArrayList<QBUser>> performer = QBUsers.getRegisteredUsersFromAddressBook(UDID, isCompact);
        Observable<ArrayList<QBUser>> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<QBUser>>() {
                    @Override
                    public void onCompleted() {
                        baseActivity.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "Error == " + e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<QBUser> qbUsers) {
                        baseActivity.hideProgress();
                        Log.d(TAG, "onNext == ");
                        QBUser qbUser = AppSession.getSession().getUser();
                        if (qbUsers != null && !qbUsers.isEmpty()) {
                            if (qbUsers.contains(qbUser)) {
                                qbUsers.remove(qbUser);
                            }
                            updateContactsList(qbUsers);
                        }

                    }
                });


        friendsAdapter = new FriendsAdapter(baseActivity, qMUserList, false);
        friendsAdapter.setFriendListHelper(friendListHelper);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(friendsAdapter);

        initCustomListeners();
    }

    private void updateContactsList(List<QBUser> usersList) {
        this.qbUserLists = usersList;
        for (int i = 0; i < usersList.size(); i++) {
            qMUserList.add(QMUser.convert(usersList.get(i)));
        }
        friendsAdapter.setList(qMUserList);
    }

    private void initCustomListeners() {
        friendsAdapter.setOnRecycleItemClickListener(new SimpleOnRecycleItemClickListener<QMUser>() {
            @Override
            public void onItemClicked(View view, QMUser user, int position) {
                super.onItemClicked(view, user, position);
                selectedUser = user;
                insertUserAsFriendToDb(selectedUser);
                try {
                    checkForOpenChat(user);
                } catch (QBResponseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkForOpenChat(QMUser user) throws QBResponseException {

        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupantForPrivateChat(user.getId());
        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
            QBChatDialog chatDialog = DialogTransformUtils.createQBDialogFromLocalDialog(dataManager, dialogOccupant.getDialog());
            startPrivateChat(chatDialog);
        } else {
            if (baseActivity.checkNetworkAvailableWithError()) {
                baseActivity.showProgress();
                QBCreatePrivateChatCommand.start(getContext(), user);
            }


          /*  mHandler.post(new Runnable() {
                @Override
                public void run() {
                    QBChatDialog privateDialog = null;
                    try {
                        privateDialog = chatHelper.createPrivateDialogIfNotExist(user.getId());
                    } catch (QBResponseException e) {
                        e.printStackTrace();
                    }
                    startPrivateChat(privateDialog);
                }
            });*/


        }


    }

    private void startPrivateChat(QBChatDialog dialog) {
        PrivateDialogActivity.start(getContext(), selectedUser, dialog);
        getActivity().finish();
    }

    private void insertUserAsFriendToDb(QMUser selectedUser) {
        Friend friend = new Friend();
        friend.setUser(selectedUser);
        dataManager.getFriendDataManager().createOrUpdate(friend, true);
    }

    private class CreatePrivateChatSuccessAction implements Command {
        @Override
        public void execute(Bundle bundle) {
            baseActivity.hideProgress();
            QBChatDialog qbDialog = (QBChatDialog) bundle.getSerializable(QBServiceConsts.EXTRA_DIALOG);
            startPrivateChat(qbDialog);
        }
    }

    private void addActions() {
        baseActivity.addAction(QBServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION, new CreatePrivateChatSuccessAction());
        baseActivity.addAction(QBServiceConsts.CREATE_PRIVATE_CHAT_FAIL_ACTION, failAction);

        baseActivity.updateBroadcastActionList();
    }

    private void removeActions() {
        baseActivity.removeAction(QBServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION);
        baseActivity.removeAction(QBServiceConsts.CREATE_PRIVATE_CHAT_FAIL_ACTION);
        baseActivity.updateBroadcastActionList();
    }

}
