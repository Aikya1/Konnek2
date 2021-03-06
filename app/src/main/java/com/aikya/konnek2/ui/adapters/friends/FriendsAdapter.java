package com.aikya.konnek2.ui.adapters.friends;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.aikya.konnek2.utils.helpers.TextViewHelper;
import com.quickblox.users.model.QBUser;

import java.util.List;

import butterknife.Bind;

public class FriendsAdapter extends BaseFilterAdapter<QMUser, BaseClickListenerViewHolder<QMUser>> {

    private boolean withFirstLetter;
    private QBFriendListHelper qbFriendListHelper;

    public FriendsAdapter(BaseActivity baseActivity, List<QMUser> usersList, boolean withFirstLetter) {
        super(baseActivity, usersList);
        this.withFirstLetter = withFirstLetter;
    }

    @Override
    protected boolean isMatch(QMUser item, String query) {
        return item.getFullName() != null && item.getFullName().toLowerCase().contains(query);
    }


    @Override
    public BaseClickListenerViewHolder<QMUser> onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("APP.JAVA", "View Type = " + viewType);
        return new ViewHolder(this, layoutInflater.inflate(R.layout.item_friend_1, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(BaseClickListenerViewHolder<QMUser> baseClickListenerViewHolder, final int position) {

        QMUser user = getItem(position);
        ViewHolder viewHolder = (ViewHolder) baseClickListenerViewHolder;


       /* if (withFirstLetter) {
            initFirstLetter(viewHolder, position, user);
        } else {
            viewHolder.firstLatterTextView.setVisibility(View.GONE);
        }*/

        if (!isMe(user)) {
            viewHolder.deviceTextView.setVisibility(View.VISIBLE);
            viewHolder.labelTextView.setVisibility(View.VISIBLE);
            viewHolder.nameTextView.setVisibility(View.VISIBLE);

            if (user.getFullName() != null) {
                viewHolder.nameTextView.setText(user.getFullName());

            } else {
                viewHolder.nameTextView.setText(user.getPhone());
            }


            viewHolder.avatarImageView.setVisibility(View.VISIBLE);

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

    /*private void initFirstLetter(ViewHolder viewHolder, int position, QMUser user) {
        if (TextUtils.isEmpty(user.getFullName())) {
            return;
        }

        viewHolder.firstLatterTextView.setVisibility(View.INVISIBLE);

        Character firstLatter = user.getFullName().toUpperCase().charAt(0);
        if (position == 0) {
            setLetterVisible(viewHolder, firstLatter);
        } else {
            Character beforeFirstLatter;
            QMUser beforeUser = getItem(position - 1);
            if (beforeUser != null && beforeUser.getFullName() != null) {
                beforeFirstLatter = beforeUser.getFullName().toUpperCase().charAt(0);

                if (!firstLatter.equals(beforeFirstLatter)) {
                    setLetterVisible(viewHolder, firstLatter);
                }
            }
        }
    }*/

 /*   private void setLetterVisible(ViewHolder viewHolder, Character character) {
        viewHolder.firstLatterTextView.setText(String.valueOf(character));
        viewHolder.firstLatterTextView.setVisibility(View.VISIBLE);
    }*/

    private void setLabel(ViewHolder viewHolder, QMUser user) {
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
//            viewHolder.labelTextView.setTextColor(baseActivity.getResources().getColor(R.color.colorPrimary));
        }
    }

    private boolean isMe(QMUser inputUser) {
        QBUser currentUser = AppSession.getSession().getUser();
        return currentUser.getId().intValue() == inputUser.getId().intValue();
    }

    protected static class ViewHolder extends BaseViewHolder<QMUser> {

     /*   @Bind(R.id.first_latter_textview)
        TextView firstLatterTextView;*/

        @Bind(R.id.avatar_imageview)
        RoundedImageView avatarImageView;


        @Bind(R.id.device_tv)
        TextView deviceTextView;

        @Bind(R.id.name_textview)
        TextView nameTextView;

        @Bind(R.id.label_textview)
        TextView labelTextView;

        public ViewHolder(FriendsAdapter adapter, View view) {
            super(adapter, view);
        }
    }
}