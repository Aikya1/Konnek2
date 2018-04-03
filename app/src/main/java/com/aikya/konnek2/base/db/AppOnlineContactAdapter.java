package com.aikya.konnek2.base.db;

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

import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.App;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.qb.helpers.QBFriendListHelper;
import com.aikya.konnek2.call.core.utils.OnlineStatusUtils;
import com.aikya.konnek2.call.services.QMUserCacheImpl;
import com.aikya.konnek2.call.services.QMUserService;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.aikya.konnek2.utils.ValidationUtils;
import com.aikya.konnek2.utils.image.ImageLoaderUtils;
import com.aikya.konnek2.utils.listeners.ContactInterface;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

import static com.aikya.konnek2.utils.AppConstant.CONTACT_USERS_LIST;

/**
 * Created by Lenovo on 12-01-2018.
 */

public class AppOnlineContactAdapter extends BaseAdapter {

    private List<QBChatDialog> onlineDialogList;
    private Context context;
    private LayoutInflater inflater = null;
    private List<QMUser> qmUsersList;
    private List<Integer> OpponentsList;
    public QMUserCacheImpl qmUserCache;
    private QBUser qbUser;
    private QBFriendListHelper qbFriendListHelper;
    private ContactInterface contactInterface;
    private String qbChatDialogId;

    public AppOnlineContactAdapter(Context context, List<QBChatDialog> onlineDialogList, ContactInterface contactInterface) {
        this.context = context;
        this.onlineDialogList = onlineDialogList;
        this.contactInterface = contactInterface;
        OpponentsList = new ArrayList<>();
        qmUserCache = new QMUserCacheImpl(context);
        inflater = (LayoutInflater) App.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        qbUser = AppSession.getSession().getUser();
        CONTACT_USERS_LIST = new ArrayList<>();

    }

    @Override
    public int getCount() {
        return onlineDialogList.isEmpty() ? 0 : onlineDialogList.size();
    }

    @Override
    public Object getItem(int position) {
        return onlineDialogList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        final ViewHolder viewHolder;
        rowView = inflater.inflate(R.layout.item_app_online_contacts, null);
        viewHolder = new ViewHolder();

        viewHolder.layoutContactParent = rowView.findViewById(R.id.layout_contact_online_parnent);
        viewHolder.checkBoxContacts = rowView.findViewById(R.id.check_box_online_contacts);
        viewHolder.userImage = rowView.findViewById(R.id.avatar_imageview_online_contact);
        viewHolder.userOnlineSatus = rowView.findViewById(R.id.image_online_userStatus);
        viewHolder.txtName = rowView.findViewById(R.id.textview_online_contact_name);
        viewHolder.txtStatus = rowView.findViewById(R.id.textview_online_contact_status);
        try {
            qmUsersList = getQMUsersFromDialog(onlineDialogList.get(position));
            viewHolder.txtName.setText(onlineDialogList.get(position).getName());
            viewHolder.userImage.setTag(onlineDialogList.get(position).getDialogId());
            setStatus(viewHolder, (qmUsersList.get(0)));
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

    protected void displayAvatarImage(String uri, ImageView imageView) {
        if (ValidationUtils.isNull(uri)) {
            imageView.setImageResource(R.drawable.placeholder_user);
        } else {
            ImageLoader.getInstance().displayImage(uri, imageView, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
        }
    }


    private void setStatus(ViewHolder viewHolder, QMUser user) {

        boolean online = qbFriendListHelper != null && qbFriendListHelper.isUserOnline(user.getId());
        if (online) {
            viewHolder.txtStatus.setText(OnlineStatusUtils.getOnlineStatus(online));
            viewHolder.txtStatus.setTextColor(context.getResources().getColor(R.color.green));
            viewHolder.userOnlineSatus.setImageResource(R.drawable.signal_green);

        } else {

            QMUser userFromDb = QMUserService.getInstance().getUserCache().get((long) user.getId());
            if (userFromDb != null) {
                user = userFromDb;
            }

//            viewHolder.txtStatus.setText(context.getString(R.string.last_seen,
//                    DateUtils.toTodayYesterdayShortDateWithoutYear2(user.getLastRequestAt().getTime()),
//                    DateUtils.formatDateSimpleTime(user.getLastRequestAt().getTime())));
            viewHolder.txtStatus.setTextColor(context.getResources().getColor(R.color.dark_gray));
            viewHolder.userOnlineSatus.setImageResource(R.drawable.signal_green);
        }
    }

    public void setFriendListHelper(QBFriendListHelper qbFriendListHelper) {
        this.qbFriendListHelper = qbFriendListHelper;
        notifyDataSetChanged();
    }


    public class ViewHolder {

        RelativeLayout layoutContactParent;
        CheckBox checkBoxContacts;
        ImageView userOnlineSatus;
        TextView txtName;
        TextView txtStatus;
        RoundedImageView userImage;


    }
}
