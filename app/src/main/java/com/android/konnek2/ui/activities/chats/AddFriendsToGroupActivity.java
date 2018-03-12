package com.android.konnek2.ui.activities.chats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.konnek2.R;
import com.android.konnek2.call.core.core.command.Command;
import com.android.konnek2.call.core.models.AppSession;
import com.android.konnek2.call.core.qb.commands.QBFindUsersCommand;
import com.android.konnek2.call.core.qb.commands.chat.QBAddFriendsToGroupCommand;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.core.utils.UserFriendUtils;
import com.android.konnek2.call.db.models.DialogOccupant;
import com.android.konnek2.call.db.models.Friend;
import com.android.konnek2.call.services.QMUserService;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.ui.activities.others.BaseFriendsListActivity;
import com.android.konnek2.ui.adapters.friends.FriendsAdapter;
import com.android.konnek2.ui.adapters.friends.SelectableFriendsAdapter;
import com.android.konnek2.utils.ToastUtils;
import com.android.konnek2.utils.listeners.simple.SimpleOnRecycleItemClickListener;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import rx.Observable;

public class AddFriendsToGroupActivity extends BaseFriendsListActivity {

    public static final int RESULT_ADDED_FRIENDS = 9123;

    private QBChatDialog qbDialog;
    private List<Integer> friendIdsList;
    SelectableFriendsAdapter selectableFriendsAdapter;
    private List<QMUser> usersList;


    public static void start(Activity activity, QBChatDialog qbDialog) {
        Intent intent = new Intent(activity, AddFriendsToGroupActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, qbDialog);
        activity.startActivityForResult(intent, RESULT_ADDED_FRIENDS);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_friends_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usersList = new ArrayList<>();
        addActions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initFields();
        setUpActionBarWithUpButton();
        initCustomListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    @Override
    protected List<QMUser> getFriendsList() {
        qbDialog = (QBChatDialog) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_DIALOG);
        qbDialog.initForChat(QBChatService.getInstance());

        List<Friend> friendsList = dataManager.getFriendDataManager().getAllForGroupDetails(qbDialog.getOccupants());
        if (!friendsList.isEmpty()) {
            List<Integer> actualFriendIdsList = UserFriendUtils.getFriendIdsListFromList(friendsList);
            List<DialogOccupant> dialogOccupantsList = dataManager.getDialogOccupantDataManager()
                    .getActualDialogOccupantsByIds(qbDialog.getDialogId(), actualFriendIdsList);
            if (!dialogOccupantsList.isEmpty()) {
                friendsList.removeAll(UserFriendUtils.getFriendsListFromDialogOccupantsList(dialogOccupantsList));
            }
        }
        if (!friendsList.isEmpty()) {
            Collections.sort(friendsList, new FriendsComparator());
        }
        return UserFriendUtils.getUsersFromFriends(friendsList);
    }

    @Override
    protected FriendsAdapter getFriendsAdapter() {
        return new SelectableFriendsAdapter(this, getUserList(), true);

       /* selectableFriendsAdapter = new SelectableFriendsAdapter(this, selectedFriendsList, true);
        return selectableFriendsAdapter;*/

    }

    private List<QMUser> getUserList() {

        qbDialog = (QBChatDialog) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_DIALOG);
        qbDialog.initForChat(QBChatService.getInstance());

        if (!usersList.isEmpty()) {
            List<Integer> actualFriendIdsList = UserFriendUtils.getIdsFromList(usersList);
            List<DialogOccupant> dialogOccupantsList = dataManager.getDialogOccupantDataManager()
                    .getActualDialogOccupantsByIds(qbDialog.getDialogId(), actualFriendIdsList);

            if (usersList.contains(dialogOccupantsList)) {
                usersList.removeAll(dialogOccupantsList);
            }

            /*if (!dialogOccupantsList.isEmpty()) {
                usersList.removeAll(UserFriendUtils.getFriendsListFromDialogOccupantsList(dialogOccupantsList));
            }*/
        }
       /* if (!friendsList.isEmpty()) {
            Collections.sort(friendsList, new FriendsComparator());
        }*/
        return usersList;
    }

    @Override
    protected void performDone() {
        List<QMUser> selectedFriendsList = ((SelectableFriendsAdapter) friendsAdapter).getSelectedFriendsList();
        if (!selectedFriendsList.isEmpty()) {
            if (isChatInitializedAndUserLoggedIn() && checkNetworkAvailableWithError()) {
                boolean joined = chatHelper != null && qbDialog != null && chatHelper.isDialogJoined(qbDialog);
                if (joined) {
                    showProgress();
                    friendIdsList = UserFriendUtils.getFriendIds(selectedFriendsList);

                    QBAddFriendsToGroupCommand.start(this, qbDialog.getDialogId(),
                            (ArrayList<Integer>) friendIdsList);

                } else {
                    ToastUtils.longToast(R.string.chat_service_is_initializing);
                }
            } else {
                ToastUtils.longToast(R.string.chat_service_is_initializing);
            }
        } else {
            ToastUtils.longToast(R.string.add_friends_to_group_no_friends_for_adding);
        }
    }

    private void initFields() {
        title = getString(R.string.add_friends_to_group_title);
        QBFindUsersCommand.start(this, null, "dev", 1);
    }

    private void initCustomListeners() {
        friendsAdapter.setOnRecycleItemClickListener(new SimpleOnRecycleItemClickListener<QMUser>() {

            @Override
            public void onItemClicked(View view, QMUser entity, int position) {
                ((SelectableFriendsAdapter) friendsAdapter).selectFriend(position);
            }
        });
    }

    private void updateContactsList(List<QMUser> usersList) {
        this.usersList = usersList;
        friendsAdapter.setList(usersList);
    }

    private void addActions() {
        addAction(QBServiceConsts.ADD_FRIENDS_TO_GROUP_SUCCESS_ACTION, new AddFriendsToGroupSuccessCommand());
        addAction(QBServiceConsts.FIND_USERS_SUCCESS_ACTION, new FindAllUsersSuccessAction());
        addAction(QBServiceConsts.FIND_USERS_FAIL_ACTION, failAction);
        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QBServiceConsts.ADD_FRIENDS_TO_GROUP_SUCCESS_ACTION);
        removeAction(QBServiceConsts.FIND_USERS_SUCCESS_ACTION);
        removeAction(QBServiceConsts.FIND_USERS_FAIL_ACTION);
        updateBroadcastActionList();
    }

    private class AddFriendsToGroupSuccessCommand implements Command {

        @Override
        public void execute(Bundle bundle) {
            hideProgress();
            Intent intent = new Intent();
            intent.putExtra(QBServiceConsts.EXTRA_FRIENDS, (Serializable) friendIdsList);
            setResult(RESULT_ADDED_FRIENDS, intent);
            finish();
        }
    }

    private class FriendsComparator implements Comparator<Friend> {

        @Override
        public int compare(Friend friend1, Friend friend2) {
            String firstName = friend1.getUser().getFullName();
            String secondName = friend2.getUser().getFullName();
            return firstName.compareToIgnoreCase(secondName);
        }
    }

    private class FindAllUsersSuccessAction implements Command {
        @Override
        public void execute(Bundle bundle) throws Exception {
            hideProgress();
            Collection<QMUser> userCollection = (Collection<QMUser>) bundle.getSerializable(QBServiceConsts.EXTRA_USERS);
            checkForExcludeMe(userCollection);
            usersList.clear();
            usersList.addAll(userCollection);



            /*for (int i = 0; i < usersList.size(); i++) {
                dataManager.getFriendDataManager().createOrUpdate(usersList.get(i));
            }*/
            updateContactsList(usersList);
        }
    }

    private void checkForExcludeMe(Collection<QMUser> usersCollection) {
        QBUser qbUser = AppSession.getSession().getUser();
        QMUser me = QMUser.convert(qbUser);
        List<Integer> idList = qbDialog.getOccupants(); //ids of the users that are in the gorup

        //usersCollection is a collection that contains all the QMUsers in the group
        //need to filter out the users so that we only show the unique users that can be added in the group

        if (usersCollection.contains(me)) {
            usersCollection.remove(me);
        }


    }
}