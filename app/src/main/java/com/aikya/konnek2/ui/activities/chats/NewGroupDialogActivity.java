package com.aikya.konnek2.ui.activities.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aikya.konnek2.ui.activities.others.BaseFriendsListActivity;
import com.aikya.konnek2.ui.adapters.friends.FriendsAdapter;
import com.aikya.konnek2.ui.adapters.friends.SelectableFriendsAdapter;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.adapters.chats.CreateGroupAdapter;
import com.aikya.konnek2.ui.views.recyclerview.SimpleDividerItemDecoration;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.listeners.Chats.SelectedUserListListener;
import com.aikya.konnek2.utils.listeners.simple.SimpleOnRecycleItemClickListener;

import java.util.List;

import butterknife.Bind;

public class NewGroupDialogActivity extends BaseFriendsListActivity implements SelectedUserListListener {

    //    @Bind(R.id.members_edittext)
//    TextView membersEditText;
    static List<QMUser> selectedFriendsList;
    @Bind(R.id.horizontal_line_view)
    View horizontalLineView;

    @Bind(R.id.groupUserRv)
    RecyclerView groupRecyclerView;
    CreateGroupAdapter createGroupAdapter;
    SelectableFriendsAdapter selectableFriendsAdapter;


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
        selectableFriendsAdapter = new SelectableFriendsAdapter(this, selectedFriendsList, true);
        return selectableFriendsAdapter;
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
        createGroupAdapter = new CreateGroupAdapter(selectedFriendsList, this);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        groupRecyclerView.setLayoutManager(horizontalLayoutManagaer);
        groupRecyclerView.setAdapter(createGroupAdapter);

        updateView();
    }

    @Override
    public void removeSelectedUser(int position, int userId) {
        createGroupAdapter.removeItem(position);
//        selectableFriendsAdapter.setCheckedStatus(position);
        selectableFriendsAdapter.getCbAndChangeStatus(userId);
        updateView();
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

    public void updateView() {
        if (createGroupAdapter.getItemCount() > 0) {
            horizontalLineView.setVisibility(View.VISIBLE);

        } else {
            horizontalLineView.setVisibility(View.GONE);
        }
    }
}