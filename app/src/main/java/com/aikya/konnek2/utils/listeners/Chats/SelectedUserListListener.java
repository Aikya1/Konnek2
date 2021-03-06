package com.aikya.konnek2.utils.listeners.Chats;

import com.aikya.konnek2.call.services.model.QMUser;

import java.util.List;

/**
 * Created by rajeev on 1/3/18.
 */

public interface SelectedUserListListener {

    void onSelectedUsersChanged(int count, List<QMUser> selectedUsers);

    void removeSelectedUser(int position, int userId);

}
