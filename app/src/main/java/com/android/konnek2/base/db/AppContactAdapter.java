package com.android.konnek2.base.db;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.android.konnek2.App;
import com.android.konnek2.R;
import com.android.konnek2.call.core.models.AppSession;
import com.android.konnek2.call.services.QMUserCacheImpl;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.android.konnek2.utils.AppPreference;
import com.android.konnek2.utils.ValidationUtils;
import com.android.konnek2.utils.image.ImageLoaderUtils;
import com.android.konnek2.utils.listeners.ContactInterface;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

import static com.android.konnek2.utils.AppConstant.CONTACT_USERS_LIST;

/**
 * Created by Lenovo on 22-11-2017.
 */

public class AppContactAdapter extends BaseAdapter {


    private List<QBChatDialog> dialogList;
    private Context context;
    private String qbChatDialogId;
    private LayoutInflater inflater = null;
    public QMUserCacheImpl qmUserCache;
    private List<QMUser> qmUsersList;
    private List<Integer> OpponentsList;
    private ContactInterface contactInterface;
    private QBUser qbUser;

    public AppContactAdapter(Context context, List<QBChatDialog> dialogList, ContactInterface contactInterface) {
        this.context = context;
        this.dialogList = dialogList;
        this.contactInterface = contactInterface;
        inflater = (LayoutInflater) App.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CONTACT_USERS_LIST = new ArrayList<>();
        OpponentsList = new ArrayList<>();
        qmUserCache = new QMUserCacheImpl(context);
        qbUser = AppSession.getSession().getUser();
        getCurrentUserId();


    }

    @Override
    public int getCount() {

        return dialogList.isEmpty() ? 0 : dialogList.size();
    }

    @Override
    public Object getItem(int position) {
        return dialogList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        final ViewHolder viewHolder;
        rowView = inflater.inflate(R.layout.item_app_contacts, null);
        viewHolder = new ViewHolder();

        try {
            viewHolder.layoutContactParent = rowView.findViewById(R.id.layout_contact_parnent);
            viewHolder.checkBoxContacts = rowView.findViewById(R.id.check_box_contacts);

            viewHolder.userImage = rowView.findViewById(R.id.avatar_imageview_contact);
            viewHolder.txtName = rowView.findViewById(R.id.textview_contact_name);
            viewHolder.txtStatus = rowView.findViewById(R.id.textview_online_status);
            qmUsersList = getQMUsersFromDialog(dialogList.get(position));
            viewHolder.txtName.setText(dialogList.get(position).getName());
            viewHolder.userImage.setTag(dialogList.get(position).getDialogId());

            viewHolder.checkBoxContacts.setTag(qmUsersList.get(0).getId());
            displayAvatarImage(qmUsersList.get(0).getAvatar(), viewHolder.userImage);

            viewHolder.layoutContactParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    qbChatDialogId = null;
                    qbChatDialogId = viewHolder.userImage.getTag().toString();
                    contactInterface.contactChat(qbChatDialogId);
                }
            });


            viewHolder.checkBoxContacts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {


                        CONTACT_USERS_LIST.add(viewHolder.checkBoxContacts.getTag().toString());
                    } else {

                        CONTACT_USERS_LIST.remove(viewHolder.checkBoxContacts.getTag().toString());

                    }

                    contactInterface.contactUsersList(CONTACT_USERS_LIST);

                }

            });

        } catch (Exception e) {
            e.getMessage();
        }

        return rowView;
    }


    public List<QMUser> getQMUsersFromDialog(QBChatDialog chatDialog) {

        qmUsersList = new ArrayList<>();
        OpponentsList = chatDialog.getOccupants();
        OpponentsList.remove(qbUser.getId());
        qmUsersList.addAll(qmUserCache.getUsersByIDs(OpponentsList));
        return qmUsersList;

    }

    public String getCurrentUserId() {

        return AppPreference.getQBUserId();
    }


    protected void displayAvatarImage(String uri, ImageView imageView) {
        if (ValidationUtils.isNull(uri)) {
            imageView.setImageResource(R.drawable.placeholder_user);
        } else {
            ImageLoader.getInstance().displayImage(uri, imageView, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
        }
    }

    public class ViewHolder {

        RelativeLayout layoutContactParent;
        CheckBox checkBoxContacts;
        TextView txtName;
        TextView txtStatus;
        RoundedImageView userImage;


    }
}
