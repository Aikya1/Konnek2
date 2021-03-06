package com.aikya.konnek2.ui.adapters.invitefriends;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.aikya.konnek2.call.core.models.InviteFriend;
import com.aikya.konnek2.ui.activities.base.BaseActivity;
import com.aikya.konnek2.ui.adapters.base.BaseListAdapter;
import com.aikya.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.aikya.konnek2.utils.listeners.CounterChangedListener;

import java.util.List;

public class InviteFriendsAdapter extends BaseListAdapter<InviteFriend> {

    private CounterChangedListener counterChangedListener;
    private int counterContacts;

    public InviteFriendsAdapter(BaseActivity activity, List<InviteFriend> objects) {
        super(activity, objects);
    }

    public void setCounterChangedListener(CounterChangedListener listener) {
        counterChangedListener = listener;
    }

    public void setCounterContacts(int counterContacts) {
        this.counterContacts = counterContacts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final InviteFriend data = getItem(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(com.aikya.konnek2.R.layout.item_invite_friend, null);
            viewHolder = new ViewHolder();

            viewHolder.contentRelativeLayout = convertView.findViewById(
                    com.aikya.konnek2.R.id.contentRelativeLayout);
            viewHolder.avatarImageView = convertView.findViewById(com.aikya.konnek2.R.id.avatar_imageview);
            viewHolder.nameTextView = convertView.findViewById(com.aikya.konnek2.R.id.name_textview);
            viewHolder.checkBox = convertView.findViewById(com.aikya.konnek2.R.id.select_user_checkbox);

            convertView.setTag(viewHolder);

            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    CheckBox checkBox = (CheckBox) view;
                    InviteFriend inviteFriend = (InviteFriend) checkBox.getTag();
                    inviteFriend.setSelected(checkBox.isChecked());
                    notifyCounterChanged(checkBox.isChecked());
                }
            });
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nameTextView.setText(data.getName());
        viewHolder.checkBox.setChecked(data.isSelected());
        viewHolder.checkBox.setTag(data);
        if (data.getUri() != null) {
            String uri = data.getUri().toString();
            displayAvatarImage(uri, viewHolder.avatarImageView);
        }

        return convertView;
    }

    private void notifyCounterChanged(boolean isIncrease) {
        counterContacts = getChangedCounter(isIncrease, counterContacts);
        counterChangedListener.onCounterContactsChanged(counterContacts);
    }

    private int getChangedCounter(boolean isIncrease, int counter) {
        if (isIncrease) {
            counter++;
        } else {
            counter--;
        }
        return counter;
    }

    private static class ViewHolder {

        RelativeLayout contentRelativeLayout;
        RoundedImageView avatarImageView;
        TextView nameTextView;
        CheckBox checkBox;
    }
}