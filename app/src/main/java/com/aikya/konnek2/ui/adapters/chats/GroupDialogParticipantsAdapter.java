package com.aikya.konnek2.ui.adapters.chats;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.qb.helpers.QBFriendListHelper;
import com.aikya.konnek2.call.core.utils.OnlineStatusUtils;
import com.aikya.konnek2.call.services.QMUserService;
import com.aikya.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.aikya.konnek2.utils.DateUtils;
import com.aikya.konnek2.utils.ValidationUtils;
import com.aikya.konnek2.utils.image.ImageLoaderUtils;
import com.aikya.konnek2.utils.listeners.UserOperationListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.model.QBChatDialog;

import java.util.Date;
import java.util.List;

/**
 * Created by rajeev on 7/3/18.
 */

public class GroupDialogParticipantsAdapter extends RecyclerView.Adapter<GroupDialogParticipantsAdapter.MyViewHolder> {

    private UserOperationListener userOperationListener;
    private QBFriendListHelper qbFriendListHelper;
    private List<QMUser> qmUserList;
    Context context;
    private int position;
    private QBChatDialog qbDialog;


    public GroupDialogParticipantsAdapter(Context context, UserOperationListener userOperationListener,
                                          List<QMUser> objectsList, QBChatDialog qbChatDialog) {
        this.userOperationListener = userOperationListener;
        this.qmUserList = objectsList;
        this.context = context;
        this.qbDialog = qbChatDialog;
    }


    public QMUser getItem(int position) {
        return qmUserList.get(position);
    }

    public void setFriendListHelper(QBFriendListHelper qbFriendListHelper) {
        this.qbFriendListHelper = qbFriendListHelper;
        notifyDataSetChanged();
    }


    public void setNewData(List<QMUser> newData) {
        qmUserList = newData;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_participant_item, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final QMUser user = getItem(position);
        holder.nameTv.setText(user.getFullName());
        setStatus(holder, user);


        if (user.getAvatar() != null && !TextUtils.isEmpty(user.getAvatar())) {
            displayAvatarImage(user.getAvatar(), holder.avatarIv);
        }


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userOperationListener.onAddUserClicked(user.getId());
            }
        });

        checkIfGroupAdmin(holder, user);

//        holder.txtView.setText(verticalList.get(position));



        /*holder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, holder.txtView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void checkIfGroupAdmin(MyViewHolder viewHolder, QMUser user) {
        /*if (qbDialog.getUserId().equals(user.getId())) {
            viewHolder.groupAdminIv.setVisibility(View.VISIBLE);
        }*/
    }


    private void setStatus(MyViewHolder viewHolder, QMUser user) {
        boolean online = qbFriendListHelper != null && qbFriendListHelper.isUserOnline(user.getId());

        if (online) {
            viewHolder.statusTv.setText(OnlineStatusUtils.getOnlineStatus(online));
            viewHolder.statusTv.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            QMUser userFromDb = QMUserService.getInstance().getUserCache().get((long) user.getId());
            if (userFromDb != null) {
                user = userFromDb;
            }


            Date date = user.getLastRequestAt();

            if (date != null) {
                viewHolder.statusTv.setText(context.getString(R.string.last_seen,
                        DateUtils.toTodayYesterdayShortDateWithoutYear2(user.getLastRequestAt().getTime()),
                        DateUtils.formatDateSimpleTime(user.getLastRequestAt().getTime())));
                viewHolder.statusTv.setTextColor(context.getResources().getColor(R.color.dark_gray));
            }


        }
    }

    protected void displayAvatarImage(String uri, ImageView imageView) {
        if (ValidationUtils.isNull(uri)) {
            imageView.setImageResource(R.drawable.placeholder_user);
        } else {
            ImageLoader.getInstance().displayImage(uri, imageView, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
        }
    }

    @Override
    public int getItemCount() {
        return qmUserList.size();
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView nameTv, statusTv;
        public RoundedImageView avatarIv;
        public RelativeLayout userLayout;
        public ImageView groupAdminIv;

        public MyViewHolder(View view) {
            super(view);
            nameTv = view.findViewById(R.id.name_textview);
            statusTv = view.findViewById(R.id.status_textview);
            avatarIv = view.findViewById(R.id.avatar_imageview);
            userLayout = view.findViewById(R.id.userLayout);
            groupAdminIv = view.findViewById(R.id.group_admin_icon);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.action_delete,
                    Menu.NONE, "Remove User");
        }


    }
}

