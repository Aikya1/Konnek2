package com.android.konnek2.base.db;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.konnek2.App;
import com.android.konnek2.R;
import com.android.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.android.konnek2.utils.listeners.ContactInterface;

import java.util.ArrayList;

import static com.android.konnek2.utils.AppConstant.CONTACT_USERS_LIST;

/**
 * Created by Lenovo on 15-01-2018.
 */

public class AppNonKonnek2ContactAdpater extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater = null;
    ArrayList<AppCallLogModel> appCallLogModels;
    private ContactInterface contactInterface;
    private String inviteNumber;

    public AppNonKonnek2ContactAdpater(Context context, ArrayList<AppCallLogModel> appCallLogModels, ContactInterface contactInterface) {
        this.context = context;
        this.appCallLogModels = appCallLogModels;
        this.contactInterface = contactInterface;
        inflater = (LayoutInflater) App.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return appCallLogModels.isEmpty() ? 0 : appCallLogModels.size();
    }

    @Override
    public Object getItem(int position) {
        return appCallLogModels.get(position);
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

        viewHolder.txtName = (TextView) rowView.findViewById(R.id.textview_non_contact_name);
        viewHolder.txtNumber = (TextView) rowView.findViewById(R.id.textview_non_contact_number);
        viewHolder.userImage = (RoundedImageView) rowView.findViewById(R.id.avatar_imageview_non_contact);
        viewHolder.userOnlineSatus = (ImageView) rowView.findViewById(R.id.image_non_userStatus);
        viewHolder.ivewInviteFriend = (ImageView) rowView.findViewById(R.id.image_view_non_contacts);

        viewHolder.txtName.setText(appCallLogModels.get(position).getContactName());
        viewHolder.txtNumber.setText(appCallLogModels.get(position).getContactNumber());
        viewHolder.ivewInviteFriend.setTag(appCallLogModels.get(position).getContactNumber());

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
