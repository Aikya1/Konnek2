package com.aikya.konnek2.call.db.managers;

import android.os.Bundle;

import com.aikya.konnek2.call.db.managers.base.BaseManager;
import com.aikya.konnek2.call.db.utils.ErrorUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.aikya.konnek2.call.db.models.Attachment;


import java.sql.SQLException;

public class AttachmentManager extends BaseManager<Attachment> {

    private static final String TAG = AttachmentManager.class.getSimpleName();

    public AttachmentManager(Dao<Attachment, Long> attachmentDao) {
        super(attachmentDao, AttachmentManager.class.getSimpleName());
    }

    public Attachment getByAttachmentType(Attachment.Type type) {
        Attachment attachment = null;

        try {
            QueryBuilder<Attachment, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(Attachment.Column.ID, type);
            PreparedQuery<Attachment> preparedQuery = queryBuilder.prepare();
            attachment = dao.queryForFirst(preparedQuery);
        } catch (SQLException e) {
            ErrorUtils.logError(e);
        }

        return attachment;
    }

    @Override
    protected void addIdToNotification(Bundle bundle, Object id) {
        bundle.putString(EXTRA_OBJECT_ID, (String) id);
    }
}