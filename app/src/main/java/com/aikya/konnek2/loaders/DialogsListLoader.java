package com.aikya.konnek2.loaders;

import android.content.Context;

import com.aikya.konnek2.call.core.core.loader.BaseLoader;
import com.aikya.konnek2.call.core.models.DialogWrapper;
import com.aikya.konnek2.call.core.qb.commands.chat.QBLoadDialogsCommand;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.core.utils.ConstsCore;
import com.aikya.konnek2.call.db.managers.DataManager;
import com.aikya.konnek2.utils.DialogsUtils;
import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 4/25/17.
 */

public class DialogsListLoader extends BaseLoader<List<DialogWrapper>> {
    private static final String TAG = DialogsListLoader.class.getSimpleName();

    private boolean loadAll;

    private boolean loadFromCache;
    private boolean loadCacheFinished;
    private boolean loadRestFinished;

    private int startRow = 0;
    private int perPage = 0;

    public DialogsListLoader(Context context, DataManager dataManager) {
        super(context, dataManager);
    }

    public boolean isLoadAll() {
        return loadAll;
    }

    public boolean isLoadCacheFinished() {
        return loadCacheFinished;
    }

    public boolean isLoadRestFinished() {
        return loadRestFinished;
    }

    public void setLoadAll(boolean loadAll) {
        this.loadAll = loadAll;
    }

    public void setPagination(int startRow, int perPage) {
        this.startRow = startRow;
        this.perPage = perPage;
    }

    @Override
    protected List<DialogWrapper> getItems() {


        List<QBChatDialog> chatDialogs = loadAll ? dataManager.getQBChatDialogDataManager().getAllSorted() :
                dataManager.getQBChatDialogDataManager().getSkippedSorted(startRow, perPage);

        List<DialogWrapper> dialogWrappers = new ArrayList<>(chatDialogs.size());
        for (QBChatDialog chatDialog : chatDialogs) {
            dialogWrappers.add(new DialogWrapper(getContext(), dataManager, chatDialog));
        }

        checkLoadFinishedFromREST(chatDialogs.size());
        checkLoadFinishedFromCache(chatDialogs.size());

        return dialogWrappers;
    }

    private void checkLoadFinishedFromCache(int size) {
        if(size < ConstsCore.CHATS_DIALOGS_PER_PAGE && loadFromCache) {
            loadFromCache = false;
            loadCacheFinished = true;
        } else {
            loadCacheFinished = false;
        }
    }

    private void checkLoadFinishedFromREST(int size) {
        loadRestFinished = (loadAll && !loadFromCache) || (size < ConstsCore.CHATS_DIALOGS_PER_PAGE && !loadFromCache);
    }

    private void retrieveAllDialogsFromCacheByPages() {
        loadCacheFinished = false;
        long dialogsCount = DataManager.getInstance().getQBChatDialogDataManager().getAllCount();
        boolean isCacheEmpty = dialogsCount <= 0;

        if(isCacheEmpty) {
            QBLoadDialogsCommand.start(getContext(), false);
            return;
        }
        loadFromCache = true;
        DialogsUtils.loadAllDialogsFromCacheByPagesTask(getContext(), dialogsCount, QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION);
    }

    @Override
    public void loadData(){
        
        retrieveAllDialogsFromCacheByPages();
    }
}