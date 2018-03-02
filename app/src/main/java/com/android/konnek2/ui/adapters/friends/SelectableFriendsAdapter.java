package com.android.konnek2.ui.adapters.friends;

import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.android.konnek2.R;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.ui.activities.base.BaseActivity;
import com.android.konnek2.ui.adapters.base.BaseClickListenerViewHolder;
import com.android.konnek2.utils.listeners.Chats.SelectedUserListListener;
import com.android.konnek2.utils.listeners.SelectUsersListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

public class SelectableFriendsAdapter extends FriendsAdapter {

    //    private SelectUsersListener selectUsersListener;
    private int counterFriends;

    private SelectedUserListListener selectUsersListener;
    private List<QMUser> selectedFriendsList;
    private SparseBooleanArray sparseArrayCheckBoxes;

    private HashMap<Integer, CheckBox> checkBoxMap;

    CheckBox globalCheckBox;

    public SelectableFriendsAdapter(BaseActivity baseActivity, List<QMUser> userList, boolean withFirstLetter) {
        super(baseActivity, userList, withFirstLetter);
        selectedFriendsList = new ArrayList<QMUser>();
        sparseArrayCheckBoxes = new SparseBooleanArray(userList.size());
        checkBoxMap = new HashMap<>();
    }

    @Override
    public BaseClickListenerViewHolder<QMUser> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(this, layoutInflater.inflate(R.layout.item_friend_selectable, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseClickListenerViewHolder<QMUser> baseClickListenerViewHolder, final int position) {
        super.onBindViewHolder(baseClickListenerViewHolder, position);
        final QMUser user = getItem(position);
        final ViewHolder viewHolder = (ViewHolder) baseClickListenerViewHolder;

        viewHolder.deviceTextView.setVisibility(View.GONE);
        String status = user.getStatus();

        if (status != null && !TextUtils.isEmpty(status)) {
            viewHolder.labelTextView.setText(user.getStatus());
        } else {
            viewHolder.labelTextView.setText("No Status");
        }


        viewHolder.selectFriendCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                sparseArrayCheckBoxes.put(position, checkBox.isChecked());
                addOrRemoveSelectedFriend(checkBox.isChecked(), user);
                notifyCounterChanged(checkBox.isChecked());
            }
        });

        boolean checked = sparseArrayCheckBoxes.get(position);
        viewHolder.selectFriendCheckBox.setChecked(checked);
        globalCheckBox = viewHolder.selectFriendCheckBox;
        checkBoxMap.put(user.getId(), globalCheckBox);

    }

    public void setSelectUsersListener(SelectedUserListListener listener) {
        selectUsersListener = listener;
    }

    public void selectFriend(int position) {
        boolean checked = !sparseArrayCheckBoxes.get(position);
        sparseArrayCheckBoxes.put(position, checked);
        addOrRemoveSelectedFriend(checked, getItem(position));
        notifyCounterChanged(checked);
        notifyItemChanged(position);
    }


    private void addOrRemoveSelectedFriend(boolean checked, QMUser user) {
        if (checked) {
            selectedFriendsList.add(user);
        } else if (selectedFriendsList.contains(user)) {
            selectedFriendsList.remove(user);
        }
    }

    private void notifyCounterChanged(boolean isIncrease) {
        changeCounter(isIncrease);
        if (selectUsersListener != null) {
//            String fullNames = ChatUtils.getSelectedFriendsFullNamesFromMap(selectedFriendsList);
            selectUsersListener.onSelectedUsersChanged(counterFriends, selectedFriendsList);
        }
    }

    private void changeCounter(boolean isIncrease) {
        if (isIncrease) {
            counterFriends++;
        } else {
            counterFriends--;
        }
    }

    public void clearSelectedFriends() {
        sparseArrayCheckBoxes.clear();
        selectedFriendsList.clear();
        counterFriends = 0;
        notifyDataSetChanged();
    }

    public void getCbAndChangeStatus(int userId) {

        CheckBox checkBox = checkBoxMap.get(userId);
        if (checkBox != null) {
            checkBox.setChecked(false);
        }

    }


    public List<QMUser> getSelectedFriendsList() {
        return selectedFriendsList;
    }

    protected static class ViewHolder extends FriendsAdapter.ViewHolder {

        @Bind(R.id.selected_friend_checkbox)
        CheckBox selectFriendCheckBox;

        public ViewHolder(FriendsAdapter adapter, View view) {
            super(adapter, view);
        }
    }
}