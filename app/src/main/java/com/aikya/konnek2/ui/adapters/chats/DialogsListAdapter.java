package com.aikya.konnek2.ui.adapters.chats;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.base.BaseActivity;
import com.aikya.konnek2.ui.adapters.base.BaseListAdapter;
import com.aikya.konnek2.utils.listeners.ContactInterface;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.models.DialogWrapper;
import com.aikya.konnek2.call.core.utils.ConstsCore;
import com.aikya.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class DialogsListAdapter extends BaseListAdapter<DialogWrapper> {

    private static final String TAG = DialogsListAdapter.class.getSimpleName();

    SimpleDateFormat simpleDateformat = new SimpleDateFormat("E"); //

    public DialogsListAdapter(BaseActivity baseActivity, List<DialogWrapper> objectsList) {
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
            viewHolder.avatarImageView = convertView.findViewById(R.id.avatar_imageview);
            viewHolder.nameTextView = convertView.findViewById(R.id.name_textview);
            viewHolder.lastMessageTextView = convertView.findViewById(R.id.last_message_textview);
            viewHolder.datetimeTextView = convertView.findViewById(R.id.datetime);
            viewHolder.userStatusImageView = convertView.findViewById(R.id.image_userStatus);


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

        /*if (dialogWrapper.getOpponentUser() != null) {
            if (dialogWrapper.getOpponentUser().getLastRequestAt() != null) {
                setUpDateTime(dialogWrapper.getOpponentUser(), viewHolder.datetimeTextView);
                setUpOnlineStatus(dialogWrapper, viewHolder.userStatusImageView);
            }

        }*/


        return convertView;
    }

    /*private void setUpOnlineStatus(DialogWrapper dialogWrapper, ImageView userStatusImageView) {

        try {
            onlineUsers = dialogWrapper.getChatDialog().requestOnlineUsers();
            if (!onlineUsers.contains(dialogWrapper.getOpponentUser().getId())) {
                userStatusImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.offline_20));
            }

        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }

    }

    private void setUpDateTime(QMUser opponentUser, TextView datetimeTextView) {
        String day = simpleDateformat.format(opponentUser.getLastRequestAt());
        datetimeTextView.setText(day);
    }*/

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
        public TextView nameTextView;
        public TextView lastMessageTextView;
        public TextView unreadMessagesTextView;
        public TextView datetimeTextView;
        public ImageView userStatusImageView;

    }
}