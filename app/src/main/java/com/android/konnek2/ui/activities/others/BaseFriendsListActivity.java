package com.android.konnek2.ui.activities.others;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;


import com.android.konnek2.R;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.utils.UserFriendUtils;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.db.models.Friend;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.ui.activities.base.BaseLoggableActivity;
import com.android.konnek2.ui.adapters.friends.FriendsAdapter;

import java.util.List;

import butterknife.Bind;

public abstract class BaseFriendsListActivity extends BaseLoggableActivity {

    @Bind(R.id.friends_recyclerview)
    protected RecyclerView friendsRecyclerView;

    protected FriendsAdapter friendsAdapter;
    protected DataManager dataManager;

    @Override
    protected void onStart() {
        super.onStart();
        initFields();
        initRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (checkNetworkAvailableWithError()) {
                    performDone();
                }
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onConnectedToService(QBService service) {
        super.onConnectedToService(service);
        if (friendListHelper != null) {
            friendsAdapter.setFriendListHelper(friendListHelper);
        }
    }

    @Override
    public void onChangedUserStatus(int userId, boolean online) {
        super.onChangedUserStatus(userId, online);
        friendsAdapter.notifyDataSetChanged();
    }

    private void initFields() {
        dataManager = DataManager.getInstance();
    }

    protected void initRecyclerView() {
        friendsAdapter = getFriendsAdapter();
        friendsAdapter.setFriendListHelper(friendListHelper);
        friendsRecyclerView.setAdapter(friendsAdapter);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected List<QMUser> getFriendsList() {
        List<Friend> friendsList = dataManager.getFriendDataManager().getAllSorted();
        return UserFriendUtils.getUsersFromFriends(friendsList);
    }

    protected abstract FriendsAdapter getFriendsAdapter();

    protected abstract void performDone();
}