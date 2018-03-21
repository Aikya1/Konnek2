package com.android.konnek2.ui.adapters.chats;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.konnek2.R;
import com.android.konnek2.call.core.models.AppSession;
import com.android.konnek2.call.core.qb.helpers.QBFriendListHelper;
import com.android.konnek2.call.core.utils.OnlineStatusUtils;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.services.QMUserService;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.ui.activities.base.BaseActivity;
import com.android.konnek2.ui.adapters.base.BaseListAdapter;
import com.android.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.android.konnek2.utils.DateUtils;
import com.android.konnek2.utils.listeners.UserOperationListener;
import com.quickblox.users.model.QBUser;

import java.util.List;

public class GroupDialogOccupantsAdapter extends BaseListAdapter<QMUser> {

    private UserOperationListener userOperationListener;
    private QBFriendListHelper qbFriendListHelper;

    public GroupDialogOccupantsAdapter(BaseActivity baseActivity, UserOperationListener userOperationListener, List<QMUser> objectsList) {
        super(baseActivity, objectsList);
        this.userOperationListener = userOperationListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        QMUser user = getItem(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_dialog_friend, null);
            viewHolder = new ViewHolder();

            viewHolder.avatarImageView = convertView.findViewById(R.id.avatar_imageview);
            viewHolder.nameTextView = convertView.findViewById(R.id.name_textview);
            viewHolder.onlineStatusTextView = convertView.findViewById(R.id.status_textview);
            viewHolder.addFriendImageView = convertView.findViewById(R.id.add_friend_imagebutton);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String fullName;
        if (isFriendValid(user)) {
            fullName = user.getFullName();
        } else {
            fullName = String.valueOf(user.getId());
        }
        viewHolder.nameTextView.setText(fullName);

        setStatus(viewHolder, user);

        //this is to ensure that there is no friend concept
        //viewHolder.addFriendImageView.setVisibility(isFriend(user) ? View.GONE : View.VISIBLE);

//        initListeners(viewHolder, user.getId());

        displayAvatarImage(user.getAvatar(), viewHolder.avatarImageView);

        return convertView;
    }

    private void initListeners(ViewHolder viewHolder, final int userId) {
        viewHolder.addFriendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userOperationListener.onAddUserClicked(userId);
            }
        });
    }

    private void setStatus(ViewHolder viewHolder, QMUser user) {
        boolean online = qbFriendListHelper != null && qbFriendListHelper.isUserOnline(user.getId());

        if (online) {
            viewHolder.onlineStatusTextView.setText(OnlineStatusUtils.getOnlineStatus(online));
            viewHolder.onlineStatusTextView.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            QMUser userFromDb = QMUserService.getInstance().getUserCache().get((long) user.getId());
            if (userFromDb != null) {
                user = userFromDb;
            }

            viewHolder.onlineStatusTextView.setText(context.getString(R.string.last_seen,
                    DateUtils.toTodayYesterdayShortDateWithoutYear2(user.getLastRequestAt().getTime()),
                    DateUtils.formatDateSimpleTime(user.getLastRequestAt().getTime())));
            viewHolder.onlineStatusTextView.setTextColor(context.getResources().getColor(R.color.dark_gray));
        }
    }

    public void setFriendListHelper(QBFriendListHelper qbFriendListHelper) {
        this.qbFriendListHelper = qbFriendListHelper;
        notifyDataSetChanged();
    }

    private boolean isFriendValid(QMUser user) {
        return user.getFullName() != null;
    }

    private boolean isFriend(QMUser user) {
        if (isMe(user)) {
            return true;
        } else {
            boolean outgoingUserRequest = DataManager.getInstance().getUserRequestDataManager().existsByUserId(user.getId());
            boolean friend = DataManager.getInstance().getFriendDataManager().getByUserId(user.getId()) != null;
            return friend || outgoingUserRequest;
        }
    }

    private boolean isMe(QMUser inputUser) {
        QBUser currentUser = AppSession.getSession().getUser();
        return currentUser.getId().intValue() == inputUser.getId().intValue();
    }

    private static class ViewHolder {

        RoundedImageView avatarImageView;
        TextView nameTextView;
        ImageView addFriendImageView;
        TextView onlineStatusTextView;
    }
}