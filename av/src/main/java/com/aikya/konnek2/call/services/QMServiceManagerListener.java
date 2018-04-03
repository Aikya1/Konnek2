package com.aikya.konnek2.call.services;

import com.quickblox.users.model.QBUser;

/**
 * Created by Lenovo on 08-11-2017.
 */

public interface QMServiceManagerListener {


    /**
     *  Get user from current session
     *
     *  @return QBUUser instance
     */
    QBUser getCurrentUser();

    /**
     *  Check is current session is authorized
     *
     *  @return true if authorized
     */
    boolean isAuthorized();
}
