package com.android.konnek2.ui.adapters.chats;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.konnek2.R;
import com.android.konnek2.call.core.models.DialogWrapper;
import com.android.konnek2.call.core.utils.ConstsCore;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.services.QMUserCacheImpl;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.ui.activities.base.BaseActivity;
import com.android.konnek2.ui.adapters.base.BaseListAdapter;
import com.android.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.listeners.ContactInterface;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.android.konnek2.utils.AppConstant.CONTACT_USERS_LIST;


public class DialogsListAdapter extends BaseListAdapter<DialogWrapper> {

    private static final String TAG = DialogsListAdapter.class.getSimpleName();


    public DialogsListAdapter(BaseActivity baseActivity, List<DialogWrapper> objectsList) {
        super(baseActivity, objectsList);
    }

    public DialogsListAdapter(BaseActivity baseActivity, List<DialogWrapper> objectsList,
                              boolean typeFlag, ContactInterface contactInterface) {
        super(baseActivity, objectsList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        DialogWrapper dialogWrapper = getItem(position);
        final QBChatDialog currentDialog = dialogWrapper.getChatDialog();

        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.item_dialog, null);
            viewHolder = new ViewHolder();
            viewHolder.contactsLayout = convertView.findViewById(R.id.layout_user_contacts);
            viewHolder.avatarImageView = convertView.findViewById(R.id.avatar_imageview);
            viewHolder.nameTextView = convertView.findViewById(R.id.name_textview);
            viewHolder.lastMessageTextView = convertView.findViewById(R.id.last_message_textview);
            viewHolder.unreadMessagesTextView = convertView.findViewById(
                    R.id.unread_messages_textview);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (QBDialogType.PRIVATE.equals(currentDialog.getType())) {
            QMUser opponentUser = dialogWrapper.getOpponentUser();
            if (opponentUser.getFullName() != null) {
                viewHolder.nameTextView.setText(opponentUser.getFullName());
                displayAvatarImage(opponentUser.getAvatar(), viewHolder.avatarImageView);
            } else {
                viewHolder.nameTextView.setText(resources.getString(R.string.deleted_user));
            }
        } else {
            viewHolder.nameTextView.setText(currentDialog.getName());
            viewHolder.avatarImageView.setImageResource(R.drawable.placeholder_group);
            displayGroupPhotoImage(currentDialog.getPhoto(), viewHolder.avatarImageView);
        }
        long totalCount = dialogWrapper.getTotalCount();
        if (totalCount > ConstsCore.ZERO_INT_VALUE) {
            viewHolder.unreadMessagesTextView.setText(totalCount + ConstsCore.EMPTY_STRING);
            viewHolder.unreadMessagesTextView.setVisibility(View.VISIBLE);

        } else {
            viewHolder.unreadMessagesTextView.setVisibility(View.GONE);
        }
        viewHolder.lastMessageTextView.setText(dialogWrapper.getLastMessage());
        return convertView;
    }

    public void updateItem(DialogWrapper dlgWrapper) {
        Log.i(TAG, "updateItem = " + dlgWrapper.getChatDialog().getUnreadMessageCount());
        int position = -1;
        for (int i = 0; i < objectsList.size(); i++) {
            DialogWrapper dialogWrapper = objectsList.get(i);
            if (dialogWrapper.getChatDialog().getDialogId().equals(dlgWrapper.getChatDialog().getDialogId())) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            Log.i(TAG, "find position = " + position);
            objectsList.set(position, dlgWrapper);
        } else {
            addNewItem(dlgWrapper);
        }
    }

    public void moveToFirstPosition(DialogWrapper dlgWrapper) {

        if (objectsList.size() != 0 && !objectsList.get(0).equals(dlgWrapper)) {
            objectsList.remove(dlgWrapper);
            objectsList.add(0, dlgWrapper);
            notifyDataSetChanged();
        }
    }

    public void removeItem(String dialogId) {
        Iterator<DialogWrapper> iterator = objectsList.iterator();
        while (iterator.hasNext()) {
            DialogWrapper dialogWrapper = iterator.next();
            if (dialogWrapper.getChatDialog().getDialogId().equals(dialogId)) {
                iterator.remove();
                notifyDataSetChanged();
                break;
            }
        }

    }

    private static class ViewHolder {

        public RoundedImageView avatarImageView;
        public RelativeLayout contactsLayout;
        public TextView nameTextView;
        public TextView lastMessageTextView;
        public TextView unreadMessagesTextView;

    }
}