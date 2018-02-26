package com.android.konnek2.call.core.qb.commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.konnek2.call.core.core.command.ServiceCommand;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.core.utils.ConstsCore;
import com.android.konnek2.call.core.utils.UserFriendUtils;
import com.android.konnek2.call.services.model.QMUser;
import com.quickblox.core.request.QBPagedRequestBuilder;

import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class QBFindUsersCommand extends ServiceCommand {

    public QBFindUsersCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context, QBUser currentUser, String constraint, int page) {
        Intent intent = new Intent(QBServiceConsts.FIND_USERS_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_USER, currentUser);
        intent.putExtra(QBServiceConsts.EXTRA_CONSTRAINT, constraint);
        intent.putExtra(QBServiceConsts.EXTRA_PAGE, page);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        String constraint = (String) extras.getSerializable(QBServiceConsts.EXTRA_CONSTRAINT);
        QBUser currentUser = (QBUser) extras.getSerializable(QBServiceConsts.EXTRA_USER);
        int page = extras.getInt(QBServiceConsts.EXTRA_PAGE);

        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        requestBuilder.setPage(page);
        requestBuilder.setPerPage(ConstsCore.FL_FRIENDS_PER_PAGE);

        Bundle requestParams = new Bundle();
        List<String> tags = new LinkedList<>();
        tags.add(constraint);


//        Collection<QBUser> userList = QBUsers.getUsersByFullName(constraint, requestBuilder, requestParams).perform();


        ArrayList<QBUser> userList= QBUsers.getUsersByTags(tags,requestBuilder,requestParams).perform();
        Collection<QMUser> userCollection = UserFriendUtils.createUsersList(userList);

        Bundle params = new Bundle();
        params.putString(QBServiceConsts.EXTRA_CONSTRAINT, constraint);
        params.putInt(QBServiceConsts.EXTRA_TOTAL_ENTRIES, requestParams.getInt(QBServiceConsts.EXTRA_TOTAL_ENTRIES));
        params.putSerializable(QBServiceConsts.EXTRA_USERS, (java.io.Serializable) userCollection);

        return params;
    }
}