package com.aikya.konnek2.base.db;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikya.konnek2.App;
import com.aikya.konnek2.utils.listeners.ContactInterface;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.models.InviteFriend;
import com.aikya.konnek2.ui.views.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import static com.aikya.konnek2.utils.AppConstant.CONTACT_USERS_LIST;

/**
 * Created by Lenovo on 15-01-2018.
 */

public class AppNonKonnek2ContactAdpater extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater = null;
    //    ArrayList<AppCallLogModel> appCallLogModels;
    ArrayList<InviteFriend> inviteFriendArrayList;
    private ContactInterface contactInterface;
    private String inviteNumber;

    public AppNonKonnek2ContactAdpater(Context context, ArrayList<InviteFriend> inviteFriendArrayList, ContactInterface contactInterface) {
        this.context = context;
        this.inviteFriendArrayList = inviteFriendArrayList;
        this.contactInterface = contactInterface;
        inflater = (LayoutInflater) App.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return inviteFriendArrayList.isEmpty() ? 0 : inviteFriendArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return inviteFriendArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        final ViewHolder viewHolder;
        rowView = inflater.inflate(R.layout.item_non_konnek2_contacts, null);
        viewHolder = new ViewHolder();

        viewHolder.txtName = rowView.findViewById(R.id.textview_non_contact_name);
        viewHolder.txtNumber = rowView.findViewById(R.id.textview_non_contact_number);
        viewHolder.userImage = rowView.findViewById(R.id.avatar_imageview_non_contact);
        viewHolder.userOnlineSatus = rowView.findViewById(R.id.image_non_userStatus);
        viewHolder.ivewInviteFriend = rowView.findViewById(R.id.image_view_non_contacts);

        viewHolder.txtName.setText(inviteFriendArrayList.get(position).getName());
        viewHolder.txtNumber.setText(inviteFriendArrayList.get(position).getNumber());
        viewHolder.ivewInviteFriend.setTag(inviteFriendArrayList.get(position).getId());

        viewHolder.ivewInviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CONTACT_USERS_LIST.add(viewHolder.ivewInviteFriend.getTag().toString());
                contactInterface.contactUsersList(CONTACT_USERS_LIST);

            }
        });

        return rowView;
    }

    public class ViewHolder {

        TextView txtName;
        TextView txtNumber;
        ImageView ivewInviteFriend;
        ImageView userOnlineSatus;
        RoundedImageView userImage;


    }
}
