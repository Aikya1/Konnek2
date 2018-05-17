package com.aikya.konnek2.ui.activities.chats;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.core.command.Command;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.qb.commands.QBFindUsersCommand;
import com.aikya.konnek2.call.db.models.DialogOccupant;
import com.aikya.konnek2.call.db.models.Friend;
import com.aikya.konnek2.call.db.utils.DialogTransformUtils;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.base.BaseLoggableActivity;
import com.aikya.konnek2.ui.adapters.friends.FriendsAdapter;
import com.aikya.konnek2.call.core.qb.commands.chat.QBCreatePrivateChatCommand;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.db.managers.DataManager;
import com.aikya.konnek2.utils.KeyboardUtils;
import com.aikya.konnek2.utils.listeners.simple.SimpleOnRecycleItemClickListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.server.Performer;
import com.quickblox.extensions.RxJavaPerformProcessor;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewMessageActivity extends BaseLoggableActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, View.OnClickListener {

    private static final String TAG = NewMessageActivity.class.getSimpleName();


    @Bind(com.aikya.konnek2.R.id.friends_recyclerview)
    RecyclerView friendsRecyclerView;

    @Bind(com.aikya.konnek2.R.id.group_layout)
    LinearLayout groupLayout;

    private List<QBUser> qbUserLists;
    private List<QMUser> qMUserList;

    private DataManager dataManager;
    private FriendsAdapter friendsAdapter;
    private QMUser selectedUser;
    private int page = 1;

    public static void startForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), NewMessageActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getContentResId() {
        return com.aikya.konnek2.R.layout.activity_friends_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setUpActionBarWithUpButton();

        getRegisteredUsersFromQBAddressBook();
        initRecyclerView();
        initCustomListeners();

        addActions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    private void getRegisteredUsersFromQBAddressBook() {
        showProgress();
        String UDID = "";
        boolean isCompact = false;
        Performer<ArrayList<QBUser>> performer = QBUsers.getRegisteredUsersFromAddressBook(UDID, isCompact);
        Observable<ArrayList<QBUser>> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<QBUser>>() {
                    @Override
                    public void onCompleted() {
                        hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "Error == " + e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<QBUser> qbUsers) {
                        hideProgress();
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

        friendsAdapter = new FriendsAdapter(NewMessageActivity.this, qMUserList, false);
        friendsAdapter.setFriendListHelper(friendListHelper);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(NewMessageActivity.this));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_horizontal));
        friendsRecyclerView.addItemDecoration(divider);
        friendsRecyclerView.setAdapter(friendsAdapter);

        /*friendsAdapter = new FriendsAdapter(baseActivity, qMUserList, false);
        friendsAdapter.setFriendListHelper(friendListHelper);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration divider = new DividerItemDecoration(listView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_horizontal));
        listView.addItemDecoration(divider);

        listView.setAdapter(friendsAdapter);

        initCustomListeners();*/
    }

    private void initRecyclerView() {
//        showProgress();
//        QBFindUsersCommand.start(this, null, "dev", 1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.new_message_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;

        if (searchMenuItem != null) {
            searchView = (SearchView) searchMenuItem.getActionView();
        }

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(this);
        }

        return super.onCreateOptionsMenu(menu);*/

        getMenuInflater().inflate(com.aikya.konnek2.R.menu.search_menu, menu);

        MenuItem searchMenuItem = menu.findItem(com.aikya.konnek2.R.id.action_search);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;

        if (searchMenuItem != null) {
            searchView = (SearchView) searchMenuItem.getActionView();
            MenuItemCompat.expandActionView(searchMenuItem);

            AutoCompleteTextView searchTextView = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            try {
                searchTextView.setBackgroundColor(getResources().getColor(com.aikya.konnek2.R.color.white));
                searchTextView.setTextColor(getResources().getColor(com.aikya.konnek2.R.color.black));
                searchTextView.setHint(getResources().getString(com.aikya.konnek2.R.string.action_bar_search));
                searchTextView.setHintTextColor(getResources().getColor(com.aikya.konnek2.R.color.light_gray));
              /*  Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);*/
            } catch (Exception e) {
            }

        }

        if (searchView != null) {
            searchView.setQueryHint(getString(com.aikya.konnek2.R.string.action_bar_search_hint));
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(this);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           /* case R.id.action_create_group:
                NewGroupDialogActivity.start(this);
                break;*/
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onClose() {

        Log.d(TAG, "Closed");
        cancelSearch();
        finish();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String searchQuery) {
        KeyboardUtils.hideKeyboard(this);
        search(searchQuery);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchQuery) {
        search(searchQuery);
        return true;
    }

    @Override
    public void onConnectedToService(QBService service) {
        super.onConnectedToService(service);
        if (friendListHelper != null && friendsAdapter != null) {
            friendsAdapter.setFriendListHelper(friendListHelper);
        }
    }

    @Override
    public void onChangedUserStatus(int userId, boolean online) {
        super.onChangedUserStatus(userId, online);
        friendsAdapter.notifyDataSetChanged();
    }

    private void initFields() {
//        title = getString(R.string.new_message_title);
        dataManager = DataManager.getInstance();
        groupLayout.setOnClickListener(this);
        qMUserList = new ArrayList<>();
        qbUserLists = new ArrayList<>();
    }

    private void updateContactsList(List<QBUser> usersList) {
        this.qbUserLists = usersList;
        for (int i = 0; i < usersList.size(); i++) {
            qMUserList.add(QMUser.convert(usersList.get(i)));
        }
        friendsAdapter.setList(qMUserList);
    }


   /* private void updateContactsList(List<QMUser> usersList) {
        this.usersList = usersList;
        friendsAdapter.setList(usersList);
    }
*/
    private void checkForExcludeMe(Collection<QMUser> usersCollection) {
        QBUser qbUser = AppSession.getSession().getUser();
        QMUser me = QMUser.convert(qbUser);
        if (usersCollection.contains(me)) {
            usersCollection.remove(me);
        }
    }


    private void initCustomListeners() {
        friendsAdapter.setOnRecycleItemClickListener(new SimpleOnRecycleItemClickListener<QMUser>() {
            @Override
            public void onItemClicked(View view, QMUser user, int position) {
                super.onItemClicked(view, user, position);
                selectedUser = user;
                insertUserAsFriendToDb(selectedUser);
                checkForOpenChat(user);
            }
        });
    }

    private void insertUserAsFriendToDb(QMUser selectedUser) {

        Friend friend = new Friend();
        friend.setUser(selectedUser);

        dataManager.getFriendDataManager().createOrUpdate(friend, true);

    }

    private void addActions() {
        addAction(QBServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION, new CreatePrivateChatSuccessAction());
        addAction(QBServiceConsts.CREATE_PRIVATE_CHAT_FAIL_ACTION, failAction);
        addAction(QBServiceConsts.FIND_USERS_SUCCESS_ACTION, new FindAllUsersSuccessAction());
        addAction(QBServiceConsts.FIND_USERS_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QBServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION);
        removeAction(QBServiceConsts.CREATE_PRIVATE_CHAT_FAIL_ACTION);
        removeAction(QBServiceConsts.FIND_USERS_SUCCESS_ACTION);
        removeAction(QBServiceConsts.FIND_USERS_FAIL_ACTION);

        updateBroadcastActionList();
    }

    private void checkForOpenChat(QMUser user) {
        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupantForPrivateChat(user.getId());
        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
            QBChatDialog chatDialog = DialogTransformUtils.createQBDialogFromLocalDialog(dataManager, dialogOccupant.getDialog());
            startPrivateChat(chatDialog);
        } else {
            if (checkNetworkAvailableWithError()) {
                showProgress();
                QBCreatePrivateChatCommand.start(this, user);
            }
        }
    }

    private void startPrivateChat(QBChatDialog dialog) {
        PrivateDialogActivity.start(this, selectedUser, dialog);
        finish();
    }

    private void search(String searchQuery) {
        if (friendsAdapter != null) {
            friendsAdapter.setFilter(searchQuery);
            friendsAdapter.notifyDataSetChanged();
        }
    }

    private void cancelSearch() {
        Log.d(TAG, "Cancel Search");
        if (friendsAdapter != null) {
            friendsAdapter.clear();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case com.aikya.konnek2.R.id.group_layout:
                Intent intent = new Intent();
                intent.putExtra("data", (Serializable) qMUserList);
                NewGroupDialogActivity.start(this, intent);
                break;
        }
    }

    private class CreatePrivateChatSuccessAction implements Command {
        @Override
        public void execute(Bundle bundle) {
            hideProgress();
            QBChatDialog qbDialog = (QBChatDialog) bundle.getSerializable(QBServiceConsts.EXTRA_DIALOG);
            startPrivateChat(qbDialog);
        }
    }

    private class FindAllUsersSuccessAction implements Command {
        @Override
        public void execute(Bundle bundle) throws Exception {
            hideProgress();
            Collection<QMUser> userCollection = (Collection<QMUser>) bundle.getSerializable(QBServiceConsts.EXTRA_USERS);
            checkForExcludeMe(userCollection);
            qMUserList.clear();
            qMUserList.addAll(userCollection);
            /*for (int i = 0; i < usersList.size(); i++) {
                dataManager.getFriendDataManager().createOrUpdate(usersList.get(i));
            }*/

//            updateContactsList(qMUserList);
        }
    }
}