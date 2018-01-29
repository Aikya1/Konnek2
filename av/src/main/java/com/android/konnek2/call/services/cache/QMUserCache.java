package com.android.konnek2.call.services.cache;

import com.android.konnek2.call.services.QMBaseCache;
import com.android.konnek2.call.services.model.QMUser;

import java.util.Collection;
import java.util.List;

/**
 * Created by Lenovo on 08-11-2017.
 */

public interface QMUserCache extends QMBaseCache<QMUser, Long> {


    void deleteUserByExternalId(String externalId);

    List<QMUser> getUsersByIDs(Collection<Integer> idsList);

    QMUser getUserByColumn(String column, String value);

    List<QMUser> getUsersByFilter(Collection<?> filterValue, String filter);
}
