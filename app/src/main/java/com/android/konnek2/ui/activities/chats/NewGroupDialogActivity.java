package com.android.konnek2.ui.activities.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import com.android.konnek2.R;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.ui.activities.others.BaseFriendsListActivity;
import com.android.konnek2.ui.adapters.friends.FriendsAdapter;
import com.android.konnek2.ui.adapters.friends.SelectableFriendsAdapter;
import com.android.konnek2.ui.views.recyclerview.SimpleDividerItemDecoration;
import com.android.konnek2.utils.ToastUtils;
import com.android.konnek2.utils.listeners.SelectUsersListener;
import com.android.konnek2.utils.listeners.simple.SimpleOnRecycleItemClickListener;

import java.util.List;

import butterknife.Bind;

public class NewGroupDialogActivity extends BaseFriendsListActivity implements SelectUsersListener {

    @Bind(R.id.members_edittext)
    EditText membersEditText;

    public static void start(Context context) {
        Intent intent = new Intent(context, NewGroupDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_new_group;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpActionBarWithUpButton();
        initCustomListeners();
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        ((SelectableFriendsAdapter) friendsAdapter).setSelectUsersListener(this);
        friendsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));;
    }

    @Override
    protected FriendsAdapter getFriendsAdapter() {
        return new SelectableFriendsAdapter(this, getFriendsList(), true);
    }

    @Override
    protected void performDone() {
        List<QMUser> selectedFriendsList = ((SelectableFriendsAdapter) friendsAdapter).getSelectedFriendsList();
        if (!selectedFriendsList.isEmpty()) {
            CreateGroupDialogActivity.start(this, selectedFriendsList);
        } else {
            ToastUtils.longToast(R.string.new_group_no_friends_for_creating_group);
        }
    }

    @Override
    public void onSelectedUsersChanged(int count, String fullNames) {
        membersEditText.setText(fullNames);
    }

    private void initFields() {
        title = getString(R.string.new_group_title);
    }

    private void initCustomListeners() {
        friendsAdapter.setOnRecycleItemClickListener(new SimpleOnRecycleItemClickListener<QMUser>() {

            @Override
            public void onItemClicked(View view, QMUser entity, int position) {
                ((SelectableFriendsAdapter) friendsAdapter).selectFriend(position);
            }
        });
    }
}