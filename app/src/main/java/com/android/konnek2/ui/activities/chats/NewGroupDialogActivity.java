package com.android.konnek2.ui.activities.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.konnek2.R;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.ui.activities.others.BaseFriendsListActivity;
import com.android.konnek2.ui.adapters.chats.CreateGroupAdapter;
import com.android.konnek2.ui.adapters.friends.FriendsAdapter;
import com.android.konnek2.ui.adapters.friends.SelectableFriendsAdapter;
import com.android.konnek2.ui.views.recyclerview.SimpleDividerItemDecoration;
import com.android.konnek2.utils.ToastUtils;
import com.android.konnek2.utils.listeners.Chats.SelectedUserListListener;
import com.android.konnek2.utils.listeners.simple.SimpleOnRecycleItemClickListener;

import java.util.List;

import butterknife.Bind;

public class NewGroupDialogActivity extends BaseFriendsListActivity implements SelectedUserListListener {

    //    @Bind(R.id.members_edittext)
//    TextView membersEditText;
    static List<QMUser> selectedFriendsList;
    LinearLayout linearLayout1;

    @Bind(R.id.groupUserRv)
    RecyclerView groupRecyclerView;
    CreateGroupAdapter createGroupAdapter;


    public static void start(Context context) {
        Intent intent = new Intent(context, NewGroupDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        context.startActivity(intent);
    }

    public static void start(Context context, Intent dataIntent) {
        Intent intent = new Intent(context, NewGroupDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        selectedFriendsList = (List<QMUser>) dataIntent.getSerializableExtra("data");
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
        friendsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

    }

    @Override
    protected FriendsAdapter getFriendsAdapter() {
        return new SelectableFriendsAdapter(this, selectedFriendsList, true);
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
    public void onSelectedUsersChanged(int count, List<QMUser> selectedFriendsList) {
//        membersEditText.setText(fullNames);
       /* for (int x = 0; x < count; x++) {
            ImageView image = new ImageView(NewGroupDialogActivity.this);
            image.setBackgroundResource(R.drawable.close_profile_icon);
            linearLayout1.addView(image);
        }*/

        createGroupAdapter = new CreateGroupAdapter(selectedFriendsList);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        groupRecyclerView.setLayoutManager(horizontalLayoutManagaer);
        groupRecyclerView.setAdapter(createGroupAdapter);

    }

    private void initFields() {
        title = getString(R.string.new_group_title);
        linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);


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