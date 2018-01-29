package com.android.konnek2.call.services;

import android.content.Context;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.services.cache.QMUserCache;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.call.services.model.QMUserColumns;
import com.android.konnek2.call.services.utils.ErrorUtils;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lenovo on 08-11-2017.
 */

public class QMUserCacheImpl extends QMAbstractBaseCache<QMUser, Long> implements QMUserCache, QMUserColumns {


    private static final String TAG = QMUserCacheImpl.class.getSimpleName();

    public QMUserCacheImpl(Context context) {
        OBSERVE_KEY = QMUserCacheImpl.class.getSimpleName();
        dao = DataManager.getInstance().getDataHelper().getDaoByClass(QMUser.class);
    }

    @Override
    public void deleteUserByExternalId(String externalId) {
        try {
            DeleteBuilder<QMUser, Long> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq(EXTERNAL_ID,externalId);
            PreparedDelete<QMUser> preparedQuery = deleteBuilder.prepare();
            dao.delete(preparedQuery);
            notifyObservers(OBSERVE_KEY);
        } catch (SQLException e) {
            ErrorUtils.logError(e);
        }
    }

    public List<QMUser> getUsersByIDs(Collection<Integer> idsList) {
        List<QMUser> usersList  = Collections.emptyList();

        try {
            QueryBuilder<QMUser, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().in(ID, idsList);
            PreparedQuery<QMUser> preparedQuery = queryBuilder.prepare();
            usersList = dao.query(preparedQuery);
        } catch (SQLException e) {
            ErrorUtils.logError(e);
        }

        return usersList;
    }

    @Override
    public List<QMUser> getUsersByFilter(Collection<?> filterValue, String filter) {
        return null;
    }

    @Override
    public  QMUser getUserByColumn(String column, String value) {
        QMUser user = null;

        try {
            QueryBuilder<QMUser, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(column, value);
            PreparedQuery<QMUser> preparedQuery = queryBuilder.prepare();
            user = dao.queryForFirst(preparedQuery);
        } catch (SQLException e) {
            ErrorUtils.logError(e);
        }

        return user;
    }


    @Override
    public List<QMUser> getByColumn(String column, Collection<String> values) {
        List<QMUser> usersList  = Collections.emptyList();

        try {
            QueryBuilder<QMUser, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().in(column, values);
            PreparedQuery<QMUser> preparedQuery = queryBuilder.prepare();
            usersList = dao.query(preparedQuery);
        } catch (SQLException e) {
            ErrorUtils.logError(e);
        }

        return usersList;
    }
}
