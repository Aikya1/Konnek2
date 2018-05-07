package com.aikya.konnek2.ui.adapters.friends;


import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.qb.helpers.QBFriendListHelper;
import com.aikya.konnek2.call.core.utils.OnlineStatusUtils;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.base.BaseActivity;
import com.aikya.konnek2.ui.adapters.base.BaseClickListenerViewHolder;
import com.aikya.konnek2.ui.adapters.base.BaseFilterAdapter;
import com.aikya.konnek2.ui.adapters.base.BaseViewHolder;
import com.aikya.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.aikya.konnek2.utils.DateUtils;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.helpers.TextViewHelper;
import com.aikya.konnek2.utils.listeners.UserListInterface;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class FriendsOnlineAdapter extends BaseFilterAdapter<QMUser, BaseClickListenerViewHolder<QMUser>> {
    private boolean withFirstLetter;
    private QBFriendListHelper qbFriendListHelper;

    private List<QMUser> qmUserLists;

    private List<QBUser> qbUserLists;

    private List<String> qbUserSelectedList;

    private UserListInterface userListInterface;


    public FriendsOnlineAdapter(BaseActivity baseActivity, List<QMUser> qmUserLists, boolean withFirstLetter, UserListInterface userListInterface) {
        super(baseActivity, qmUserLists);
        this.withFirstLetter = withFirstLetter;

        this.qmUserLists = qmUserLists;

        this.userListInterface = userListInterface;


    }

    @Override
    protected boolean isMatch(QMUser item, String query) {
        return item.getFullName() != null && item.getFullName().toLowerCase().contains(query);
    }

    @Override
    public BaseClickListenerViewHolder<QMUser> onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("APP.JAVA", "View Type = " + viewType);


        return new FriendsOnlineAdapter.ViewHolder(this, layoutInflater.inflate(R.layout.item_friend_selectable, parent, false));

    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(BaseClickListenerViewHolder<QMUser> baseClickListenerViewHolder, final int position) {

        QMUser user = getItem(position);
        FriendsOnlineAdapter.ViewHolder viewHolder = (FriendsOnlineAdapter.ViewHolder) baseClickListenerViewHolder;

        qbUserSelectedList = new ArrayList<>();


        if (!isMe(user)) {
            viewHolder.deviceTextView.setVisibility(View.GONE);
            viewHolder.labelTextView.setVisibility(View.GONE);
            viewHolder.nameTextView.setVisibility(View.VISIBLE);
            viewHolder.nameTextView.setText(user.getFullName());
            viewHolder.avatarImageView.setVisibility(View.VISIBLE);


            //

            /*viewHolder.selectFriendCheckBox.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    if (((CheckBox) v).isChecked())
                    {
                        ToastUtils.shortToast("Checkbox clicked");

                        //qbUserSelectedList.add(holder.userCheckBox.getTag().toString());

                        qbUserLists.add(viewHolder.selectFriendCheckBox.getTag());



                    }

                    if (!((CheckBox) v).isChecked())
                    {
                       // ToastUtils.shortToast("Checkbox un clicked");

                    }

                }
            });
*/
            //

            //

            viewHolder.selectFriendCheckBox.setTag(qmUserLists.get(position).getId());

            viewHolder.selectFriendCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ToastUtils.shortToast("Checkbox clicked");
                        qbUserSelectedList.add(viewHolder.selectFriendCheckBox.getTag().toString());

                    } else {
                        qbUserSelectedList.remove(viewHolder.selectFriendCheckBox.getTag().toString());

                    }

                    userListInterface.seletedUsers(qbUserSelectedList);
                }


            });


            //

            if (user.getAvatar() != null || !TextUtils.isEmpty(user.getAvatar())) {
                displayAvatarImage(user.getAvatar(), viewHolder.avatarImageView);
            }

            if (!TextUtils.isEmpty(query)) {
                TextViewHelper.changeTextColorView(baseActivity, viewHolder.nameTextView, query);
            }

            setLabel(viewHolder, user);
        }


    }

    public void setFriendListHelper(QBFriendListHelper qbFriendListHelper) {
        this.qbFriendListHelper = qbFriendListHelper;
        notifyDataSetChanged();
    }


    private void setLabel(FriendsOnlineAdapter.ViewHolder viewHolder, QMUser user) {
        boolean online = qbFriendListHelper != null && qbFriendListHelper.isUserOnline(user.getId());

        if (isMe(user)) {
            online = true;
        }

        if (online) {
            viewHolder.labelTextView.setText(OnlineStatusUtils.getOnlineStatus(online));
            viewHolder.labelTextView.setTextColor(baseActivity.getResources().getColor(R.color.green));
        } else if (user.getLastRequestAt() != null) {
            viewHolder.labelTextView.setText(baseActivity.getString(R.string.last_seen,
                    DateUtils.toTodayYesterdayShortDateWithoutYear2(user.getLastRequestAt().getTime()),
                    DateUtils.formatDateSimpleTime(user.getLastRequestAt().getTime())));

        }
    }

    private boolean isMe(QMUser inputUser) {
        QBUser currentUser = AppSession.getSession().getUser();
        return currentUser.getId().intValue() == inputUser.getId().intValue();
    }


    protected static class ViewHolder extends BaseViewHolder<QMUser> {


        @Bind(R.id.avatar_imageview)
        RoundedImageView avatarImageView;


        @Bind(R.id.device_tv)
        TextView deviceTextView;

        @Bind(R.id.name_textview)
        TextView nameTextView;

        @Bind(R.id.label_textview)
        TextView labelTextView;

        @Bind(R.id.selected_friend_checkbox)
        CheckBox selectFriendCheckBox;


        public ViewHolder(FriendsOnlineAdapter adapter, View view) {
            super(adapter, view);
        }
    }
}


