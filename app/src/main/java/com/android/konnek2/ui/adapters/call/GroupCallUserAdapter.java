package com.android.konnek2.ui.adapters.call;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.konnek2.R;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.ValidationUtils;
import com.android.konnek2.utils.image.ImageLoaderUtils;
import com.android.konnek2.utils.listeners.UserListInterface;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * Created by Lenovo on 16-11-2017.
 */

public class GroupCallUserAdapter extends BaseAdapter {   // by suresh

    private List<QBUser> qbUserList;
    private List<QMUser> qmUserLists;
    private List<String> qbUserSelectedList;
    private HashMap<String, String> userOnlineStatus;
    private LayoutInflater inflater = null;
    private UserListInterface userListInterface;
    private Context context;


    public GroupCallUserAdapter(Context context, List<QBUser> qbUserList, HashMap<String, String> userOnlineStatus, UserListInterface userListInterface, List<QMUser> qmUserLists) {
        this.qbUserList = qbUserList;
        this.qmUserLists = qmUserLists;
        this.context = context;
        this.userOnlineStatus = userOnlineStatus;
        this.userListInterface = userListInterface;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return qbUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        qbUserSelectedList = new ArrayList<>();
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.item_chat_group_call, null);

        try {

            holder.txtOpponentName = (TextView) rowView.findViewById(R.id.txt_opponents_name);
            holder.userImage = (RoundedImageView) rowView.findViewById(R.id.avatar_imageview_group);
            holder.userStatusImage = (ImageView) rowView.findViewById(R.id.image_userStatus);
            holder.userCheckBox = (CheckBox) rowView.findViewById(R.id.user_check_box);
            displayAvatarImage(qmUserLists.get(position).getAvatar(), holder.userImage);
            if (!userOnlineStatus.isEmpty())
                holder.txtOpponentName.setText(qbUserList.get(position).getFullName());
            holder.txtOpponentName.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.userCheckBox.setTag(qbUserList.get(position).getId());

            holder.userCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                        qbUserSelectedList.add(holder.userCheckBox.getTag().toString());
                    } else {

                        qbUserSelectedList.remove(holder.userCheckBox.getTag().toString());
                    }
                    userListInterface.seletedUsers(qbUserSelectedList);
                }


            });

            if (getUserStat(qbUserList.get(position).getId()) != null && getUserStat(qbUserList.get(position).getId()).contains(AppConstant.ONLINE)) {
                holder.userStatusImage.setImageResource(R.drawable.signal_green);
                holder.userCheckBox.setEnabled(true);
            } else {

                holder.userStatusImage.setImageResource(R.drawable.signal_read);
                holder.userCheckBox.setEnabled(false);

            }

        } catch (Exception e) {

            e.getMessage();

        }
        return rowView;
    }


    protected void displayAvatarImage(String uri, ImageView imageView) {
        if (ValidationUtils.isNull(uri)) {
            imageView.setImageResource(R.drawable.placeholder_user);
        } else {
            ImageLoader.getInstance().displayImage(uri, imageView, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
        }
    }

    private String getUserStat(Integer userId) {
        return userOnlineStatus.get(String.valueOf(userId));
    }

    public class Holder {

        TextView txtOpponentName;
        CheckBox userCheckBox;
        RoundedImageView userImage;
        ImageView userStatusImage;

    }

}


