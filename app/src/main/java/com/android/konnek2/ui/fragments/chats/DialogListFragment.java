package com.android.konnek2.ui.fragments.chats;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.konnek2.R;
import com.android.konnek2.base.db.AppCallLogModel;
import com.android.konnek2.call.core.core.command.Command;
import com.android.konnek2.call.core.models.AppSession;
import com.android.konnek2.call.core.models.DialogWrapper;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.core.utils.ConstsCore;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.db.managers.DialogNotificationDataManager;
import com.android.konnek2.call.db.managers.base.BaseManager;
import com.android.konnek2.call.db.models.Dialog;
import com.android.konnek2.call.db.models.DialogNotification;
import com.android.konnek2.call.db.models.DialogOccupant;
import com.android.konnek2.call.db.models.Message;
import com.android.konnek2.call.services.QMUserCacheImpl;
import com.android.konnek2.call.services.QMUserService;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.loaders.DialogsListLoader;
import com.android.konnek2.ui.adapters.chats.DialogsListAdapter;
import com.android.konnek2.ui.fragments.base.BaseLoaderFragment;
import com.android.konnek2.utils.ToastUtils;
import com.android.konnek2.utils.listeners.ContactInterface;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rajeev on 21/2/18.
 */

public class DialogListFragment extends BaseLoaderFragment<List<DialogWrapper>> implements ContactInterface {
    private static final String TAG = DialogListFragment.class.getSimpleName();

    @Bind(R.id.chats_listview)
    ListView dialogsListView;
    @Bind(R.id.fab_dialogs_new_chat)
    FloatingActionButton floatingActionButton;
    private DialogsListAdapter dialogsListAdapter;
    private DataManager dataManager;
    private QBUser qbUser;
    private Observer commonObserver;
    private DialogsListLoader dialogsListLoader;
    private Queue<LoaderConsumer> loaderConsumerQueue = new ConcurrentLinkedQueue<>();
    Set<String> dialogsIdsToUpdate;


    private DeleteDialogSuccessAction deleteDialogSuccessAction;
    private DeleteDialogFailAction deleteDialogFailAction;
    private LoadChatsSuccessAction loadChatsSuccessAction;
    private LoadChatsFailedAction loadChatsFailedAction;
    private UpdateDialogSuccessAction updateDialogSuccessAction;

    public QMUserCacheImpl qmUserCache;
    private List<QBUser> qbUserLists;
    private List<String> contactUsersList;
    private List<Integer> contactGroupDialogList;
    private List<QMUser> contactGroupQMUsersList;
    private List<QMUser> qMUserList;
    private AppCallLogModel appCallLogModel;
    private ArrayList<AppCallLogModel> appCallLogModelArrayList;
    private State updateDialogsProcess;

    private static boolean adapterFlag;

    public static DialogListFragment newInstance() {
        return new DialogListFragment();
    }


    enum State {
        started, stopped, finished
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_dialogs_list, container, false);
        activateButterKnife(view);
//        progress.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFFFFF"), android.graphics.PorterDuff.Mode.SRC_ATOP);
        setHasOptionsMenu(true);
        registerForContextMenu(dialogsListView);
//        dialogsListView.setAdapter(dialogsListAdapter);


        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        initFields();
        initChatsDialogs();
//        initActions();
//        addObservers();
    }

    private void initFields() {

        dataManager = DataManager.getInstance();
        commonObserver = new CommonObserver();
        qbUser = AppSession.getSession().getUser();
        qmUserCache = new QMUserCacheImpl(getActivity());
        qbUserLists = new ArrayList<>();
        contactUsersList = new ArrayList<>();
        contactGroupDialogList = new ArrayList<>();
        contactGroupQMUsersList = new ArrayList<>();
        appCallLogModel = new AppCallLogModel();
        appCallLogModelArrayList = new ArrayList<AppCallLogModel>();

    }


    @OnClick(R.id.fab_dialogs_new_chat)
    public void startChat() {
        ToastUtils.shortToast("clicked...");
    }

    private void initActions() {
        deleteDialogSuccessAction = new DeleteDialogSuccessAction();
        deleteDialogFailAction = new DeleteDialogFailAction();
        loadChatsSuccessAction = new LoadChatsSuccessAction();
        loadChatsFailedAction = new LoadChatsFailedAction();
        updateDialogSuccessAction = new UpdateDialogSuccessAction();
    }


    private void addObservers() {

        dataManager.getQBChatDialogDataManager().addObserver(commonObserver);
        dataManager.getMessageDataManager().addObserver(commonObserver);
        dataManager.getDialogOccupantDataManager().addObserver(commonObserver);
        dataManager.getDialogNotificationDataManager().addObserver(commonObserver);
        ((Observable) QMUserService.getInstance().getUserCache()).addObserver(commonObserver);
    }


    private class DeleteDialogSuccessAction implements Command {


        @Override
        public void execute(Bundle bundle) {

            baseActivity.hideProgress();
            dialogsListAdapter.removeItem(bundle.getString(QBServiceConsts.EXTRA_DIALOG_ID));
        }
    }

    private class DeleteDialogFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            ToastUtils.longToast(R.string.dlg_internet_connection_error);
            baseActivity.hideProgress();
        }
    }


    private class LoadChatsSuccessAction implements Command {
        @Override
        public void execute(Bundle bundle) {

            if (bundle != null) {
                if (isLoadPerPage(bundle)) {
                    updateDialogsList(bundle.getInt(ConstsCore.DIALOGS_START_ROW), bundle.getInt(ConstsCore.DIALOGS_PER_PAGE));
                } else if (bundle.getBoolean(ConstsCore.DIALOGS_UPDATE_ALL)) {
                    updateDialogsList();
                }
            }
        }

        private boolean isLoadPerPage(Bundle bundle) {
            return bundle.get(ConstsCore.DIALOGS_START_ROW) != null && bundle.get(ConstsCore.DIALOGS_PER_PAGE) != null;
        }
    }


    private void updateDialogsList(int startRow, int perPage) {

        if (!loaderConsumerQueue.isEmpty()) {
//            loaderConsumerQueue.offer(new DialogsListFragment.LoaderConsumer(startRow, perPage));
            return;
        }

//        if Loader is in loading process, we don't fire onChangedData, cause we do not want interrupt current load task
        if (dialogsListLoader.isLoading) {
            Log.d(TAG, "updateDialogsList dialogsListLoader.isLoading");
//            loaderConsumerQueue.offer(new DialogsListFragment.LoaderConsumer(startRow, perPage));
        } else {
//        we don't have tasks in queue, so load dialogs by pages
            if (!isResumed()) {
//                loaderConsumerQueue.offer(new DialogsListFragment.LoaderConsumer(startRow, perPage));
            } else {
                Log.d(TAG, "updateDialogsList onChangedData");
                dialogsListLoader.setPagination(startRow, perPage);
                onChangedData();
            }
        }
    }

    private void updateDialogsList() {

        if (!loaderConsumerQueue.isEmpty()) {

            Log.d(TAG, "updateDialogsList loaderConsumerQueue.add");
//            loaderConsumerQueue.offer(new DialogsListFragment.LoaderConsumer(true));
            return;
        }

        if (dialogsListLoader.isLoading) {
            Log.d(TAG, "updateDialogsList dialogsListLoader.isLoading");
//            loaderConsumerQueue.offer(new DialogsListFragment.LoaderConsumer(true));
        } else {
//        load All dialogs
            if (!isResumed()) {
                Log.d(TAG, "updateDialogsList !isResumed() offer");
//                loaderConsumerQueue.offer(new DialogsListFragment.LoaderConsumer(true));
            } else {
                dialogsListLoader.setLoadAll(true);
                onChangedData();
            }
        }
    }

    private class LoadChatsFailedAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            baseActivity.hideProgress();
            updateDialogsProcess = State.finished;
        }
    }


    private class UpdateDialogSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {

            baseActivity.hideProgress();
            if (bundle != null) {
                updateDialogIds((String) bundle.get(QBServiceConsts.EXTRA_DIALOG_ID));
            }
        }
    }

    private void updateDialogIds(String dialogId) {

        if (dialogsIdsToUpdate == null) {
            dialogsIdsToUpdate = new HashSet<>();
        }
        dialogsIdsToUpdate.add(dialogId);
    }


    private void initChatsDialogs() {
        List<DialogWrapper> dialogsList = new ArrayList<>();
        dialogsListAdapter = new DialogsListAdapter(baseActivity, dialogsList, adapterFlag, DialogListFragment.this);
    }

    private class CommonObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {

            if (data != null) {
                if (data instanceof Bundle) {
                    String observeKey = ((Bundle) data).getString(BaseManager.EXTRA_OBSERVE_KEY);

                    if (observeKey.equals(dataManager.getMessageDataManager().getObserverKey())
                            && (((Bundle) data).getSerializable(BaseManager.EXTRA_OBJECT) instanceof Message)) {
                        int action = ((Bundle) data).getInt(BaseManager.EXTRA_ACTION);

                        Message message = getObjFromBundle((Bundle) data);
                        if (message.getDialogOccupant() != null && message.getDialogOccupant().getDialog() != null) {
                            boolean updatePosition = message.isIncoming(AppSession.getSession().getUser().getId());

                            updateOrAddDialog(message.getDialogOccupant().getDialog().getDialogId(), action == BaseManager.CREATE_ACTION);
                        }
                    } else if (observeKey.equals(dataManager.getQBChatDialogDataManager().getObserverKey())) {
                        int action = ((Bundle) data).getInt(BaseManager.EXTRA_ACTION);
                        if (action == BaseManager.DELETE_ACTION
                                || action == BaseManager.DELETE_BY_ID_ACTION) {
                            return;
                        }
                        Dialog dialog = getObjFromBundle((Bundle) data);
                        if (dialog != null) {
                            updateOrAddDialog(dialog.getDialogId(), false);
                        }
                    } else if (observeKey.equals(dataManager.getDialogOccupantDataManager().getObserverKey())) {
                        DialogOccupant dialogOccupant = getObjFromBundle((Bundle) data);
                        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
                            updateOrAddDialog(dialogOccupant.getDialog().getDialogId(), false);
                        }
                    } else if (observeKey.equals(dataManager.getDialogNotificationDataManager().getObserverKey())) {
                        Bundle observableData = (Bundle) data;
                        DialogNotification dialogNotification = (DialogNotification) observableData.getSerializable(DialogNotificationDataManager.EXTRA_OBJECT);
                        if (dialogNotification != null) {
                            updateOrAddDialog(dialogNotification.getDialogOccupant().getDialog().getDialogId(), true);
                        }
                    }
                } else if (data.equals(QMUserCacheImpl.OBSERVE_KEY)) {

//                    updateDialogsList();
                }
            }
        }
    }

    private <T> T getObjFromBundle(Bundle data) {
        return (T) (data).getSerializable(BaseManager.EXTRA_OBJECT);
    }

    private void updateOrAddDialog(String dialogId, boolean updatePosition) {
        QBChatDialog qbChatDialog = dataManager.getQBChatDialogDataManager().getByDialogId(dialogId);
        DialogWrapper dialogWrapper = new DialogWrapper(getContext(), dataManager, qbChatDialog);
        Log.i(TAG, "updateOrAddDialog dialogWrapper= " + dialogWrapper.getTotalCount());
        if (updateDialogsProcess == State.finished || dialogsListAdapter.getCount() != 0) {
            dialogsListAdapter.updateItem(dialogWrapper);
        }

        if (updatePosition) {
            dialogsListAdapter.moveToFirstPosition(dialogWrapper);
        }

        int start = dialogsListView.getFirstVisiblePosition();
        for (int i = start, j = dialogsListView.getLastVisiblePosition(); i <= j; i++) {
            DialogWrapper result = (DialogWrapper) dialogsListView.getItemAtPosition(i);
            if (result.getChatDialog().getDialogId().equals(dialogId)) {
                View view = dialogsListView.getChildAt(i - start);
                dialogsListView.getAdapter().getView(i, view, dialogsListView);
                break;
            }
        }
    }


    private class LoaderConsumer implements Runnable {
        boolean loadAll;
        int startRow;
        int perPage;

        LoaderConsumer(boolean loadAll) {
            this.loadAll = loadAll;
        }

        LoaderConsumer(int startRow, int perPage) {
            this.startRow = startRow;
            this.perPage = perPage;
        }

        @Override
        public void run() {

            dialogsListLoader.setLoadAll(loadAll);
            dialogsListLoader.setPagination(startRow, perPage);
            onChangedData();
        }
    }

    @Override
    protected Loader<List<DialogWrapper>> createDataLoader() {
        Log.d(TAG, "createDataLoader");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<DialogWrapper>> loader, List<DialogWrapper> data) {
        Log.d(TAG, "onLoadFinished");
    }

    @Override
    public void contactUsersList(List<String> usersLists) {
        Log.d(TAG, "contactUsersList");
    }

    @Override
    public void contactChat(String contactGroupDialog) {
        Log.d(TAG, "contactChat");
    }
}
