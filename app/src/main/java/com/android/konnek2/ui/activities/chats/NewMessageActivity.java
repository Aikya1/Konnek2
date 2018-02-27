package com.android.konnek2.ui.activities.chats;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.android.konnek2.R;
import com.android.konnek2.call.core.core.command.Command;
import com.android.konnek2.call.core.models.AppSession;
import com.android.konnek2.call.core.qb.commands.chat.QBCreatePrivateChatCommand;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.core.utils.ConstsCore;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.db.models.DialogOccupant;
import com.android.konnek2.call.db.utils.DialogTransformUtils;
import com.android.konnek2.call.services.QMUserService;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.ui.activities.base.BaseLoggableActivity;
import com.android.konnek2.ui.adapters.friends.FriendsAdapter;
import com.android.konnek2.utils.KeyboardUtils;
import com.android.konnek2.utils.listeners.simple.SimpleOnRecycleItemClickListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewMessageActivity extends BaseLoggableActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, View.OnClickListener {

    private static final String TAG = NewMessageActivity.class.getSimpleName();


    @Bind(R.id.friends_recyclerview)
    RecyclerView friendsRecyclerView;

    @Bind(R.id.group_layout)
    LinearLayout groupLayout;

    private List<QMUser> usersList;

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
        return R.layout.activity_friends_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setUpActionBarWithUpButton();

        initRecyclerView();
        initCustomListeners();

        addActions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
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

        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;

        if (searchMenuItem != null) {
            searchView = (SearchView) searchMenuItem.getActionView();
            MenuItemCompat.expandActionView(searchMenuItem);

            AutoCompleteTextView searchTextView = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            try {
                searchTextView.setBackgroundColor(getResources().getColor(R.color.white));
                searchTextView.setTextColor(getResources().getColor(R.color.black));
                searchTextView.setHint(getResources().getString(R.string.action_bar_search));
              /*  Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);*/
            } catch (Exception e) {
            }

        }

        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.action_bar_search_hint));
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
        usersList = new ArrayList<>();
    }

    private void initRecyclerView() {
//        List<Friend> friendsList = dataManager.getFriendDataManager().getAllSorted();

        showProgress();

        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        requestBuilder.setPage(page);
        requestBuilder.setPerPage(ConstsCore.FL_FRIENDS_PER_PAGE);


        QMUserService.getInstance().getUserByTag("dev", requestBuilder, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<List<QMUser>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "OnCompleted..");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError.." + e.getMessage());
                    }

                    @Override
                    public void onNext(List<QMUser> qbUsers) {
                        hideProgress();
                        Log.d(TAG, "onNext..");


                        if (qbUsers != null && !qbUsers.isEmpty()) {
                            checkForExcludeMe(qbUsers);
                            usersList.clear();
                            usersList.addAll(qbUsers);
                            updateContactsList(usersList);

                        }
                    }
                });

        friendsAdapter = new FriendsAdapter(NewMessageActivity.this, usersList, false);
        friendsAdapter.setFriendListHelper(friendListHelper);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(NewMessageActivity.this));
        friendsRecyclerView.setAdapter(friendsAdapter);
    }


    private void updateContactsList(List<QMUser> usersList) {
        this.usersList = usersList;
        friendsAdapter.setList(usersList);
    }


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
                checkForOpenChat(user);
            }
        });
    }

    private void addActions() {
        addAction(QBServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION, new CreatePrivateChatSuccessAction());
        addAction(QBServiceConsts.CREATE_PRIVATE_CHAT_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QBServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION);
        removeAction(QBServiceConsts.CREATE_PRIVATE_CHAT_FAIL_ACTION);

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
            case R.id.group_layout:
                NewGroupDialogActivity.start(this);
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
}