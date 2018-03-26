package com.android.konnek2.ui.fragments.chats;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.konnek2.App;
import com.android.konnek2.R;
import com.android.konnek2.base.db.AppCallLogModel;
import com.android.konnek2.call.core.core.command.Command;
import com.android.konnek2.call.core.models.AppSession;
import com.android.konnek2.call.core.models.DialogWrapper;
import com.android.konnek2.call.core.qb.commands.chat.QBDeleteChatCommand;
import com.android.konnek2.call.core.qb.commands.chat.QBLoadDialogByIdsCommand;
import com.android.konnek2.call.core.qb.commands.chat.QBLoadDialogsCommand;
import com.android.konnek2.call.core.qb.commands.chat.QBLoginChatCompositeCommand;
import com.android.konnek2.call.core.qb.helpers.QBChatHelper;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.core.utils.ChatUtils;
import com.android.konnek2.call.core.utils.ConstsCore;
import com.android.konnek2.call.core.utils.UserFriendUtils;
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
import com.android.konnek2.ui.activities.call.CallActivity;
import com.android.konnek2.ui.activities.chats.GroupDialogActivity;
import com.android.konnek2.ui.activities.chats.NewMessageActivity;
import com.android.konnek2.ui.activities.chats.PrivateDialogActivity;
import com.android.konnek2.ui.adapters.chats.DialogsListAdapter;
import com.android.konnek2.ui.fragments.base.BaseLoaderFragment;
import com.android.konnek2.ui.fragments.search.SearchFragment;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.AppPreference;
import com.android.konnek2.utils.ToastUtils;
import com.android.konnek2.utils.listeners.AppCommon;
import com.android.konnek2.utils.listeners.ContactInterface;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.helper.CollectionsUtil;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCTypes;

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
import butterknife.OnItemClick;


public class DialogsListFragment extends BaseLoaderFragment<List<DialogWrapper>> implements ContactInterface {

    public static final int PICK_DIALOG = 100;
    public static final int CREATE_DIALOG = 200;
    private static final String TAG = DialogsListFragment.class.getSimpleName();
    private static final int LOADER_ID = DialogsListFragment.class.hashCode();

    @Bind(R.id.chats_listview)
    ListView dialogsListView;

    @Bind(R.id.empty_list_textview)
    TextView emptyListTextView;
    @Bind(R.id.fab_dialogs_new_chat)
    FloatingActionButton floatingActionButton;
    private DialogsListAdapter dialogsListAdapter;
    private DataManager dataManager;
    private QBUser qbUser;
    private Observer commonObserver;
    private DialogsListLoader dialogsListLoader;
    private Queue<LoaderConsumer> loaderConsumerQueue = new ConcurrentLinkedQueue<>();
    Set<String> dialogsIdsToUpdate;

    protected Handler handler = new Handler();
    private State updateDialogsProcess;
    private static boolean adapterFlag;
    public QMUserCacheImpl qmUserCache;
    private List<QBUser> qbUserLists;
    private List<String> contactUsersList;
    private List<Integer> contactGroupDialogList;
    private List<QMUser> contactGroupQMUsersList;
    private List<QMUser> qMUserList;
    private AppCallLogModel appCallLogModel;
    private ArrayList<AppCallLogModel> appCallLogModelArrayList;

    private MenuItem menuItemVideoCall;
    private MenuItem menuItemAudioCall;

    private DeleteDialogSuccessAction deleteDialogSuccessAction;
    private DeleteDialogFailAction deleteDialogFailAction;
    private LoadChatsSuccessAction loadChatsSuccessAction;
    private LoadChatsFailedAction loadChatsFailedAction;
    private UpdateDialogSuccessAction updateDialogSuccessAction;
    private LoginChatCompositeSuccessAction loginChatCompositeSuccessAction;


    @Override
    public void contactUsersList(List<String> contactQMUserList) {
        contactUsersList.clear();
        contactGroupQMUsersList.clear();
        contactUsersList.addAll(contactQMUserList);
        if (!contactUsersList.isEmpty() && contactUsersList.size() >= 1 && contactUsersList != null) {
            menuItemVideoCall.setVisible(true);
            menuItemAudioCall.setVisible(true);
        } else {
            menuItemVideoCall.setVisible(false);
            menuItemAudioCall.setVisible(false);
        }
    }

    @Override
    public void contactChat(String contactGroupDialog) {
        QBChatDialog contactChatDialog;
        String DialogId;
        DialogId = contactGroupDialog;
        if (!DialogId.isEmpty() && DialogId != null) {
            contactChatDialog = dataManager.getQBChatDialogDataManager().getByDialogId(DialogId);
            if (contactChatDialog.getOccupants().size() >= 3) {
                Log.d("", "contactGroup");
                startGroupChatActivity(contactChatDialog);
            } else {
                startPrivateChatActivity(contactChatDialog);
            }
        }
    }

    //    private AppRefreshDialogList appRefreshDialogList;
    enum State {

        started, stopped, finished
    }

    public static DialogsListFragment newInstance() {
        return new DialogsListFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        initFields();
        initChatsDialogs();
        initActions();
        addObservers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_dialogs_list, container, false);
        activateButterKnife(view);

//        progress.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFFFFF"), android.graphics.PorterDuff.Mode.SRC_ATOP);
        setHasOptionsMenu(true);
        registerForContextMenu(dialogsListView);


        dialogsListView.setAdapter(dialogsListAdapter);


        return view;
    }

    @Override
    public void initActionBar() {
        super.initActionBar();
        actionBarBridge.setActionBarUpButtonEnabled(false);
        loadingBridge.hideActionBarProgress();
    }

    private void initFields() {

        dataManager = DataManager.getInstance();
        commonObserver = new CommonObserver();
        qbUser = AppSession.getSession().getUser();

        //        qmUserCache = new QMUserCacheImpl(getActivity());
        qbUserLists = new ArrayList<>();
        contactUsersList = new ArrayList<>();
        contactGroupDialogList = new ArrayList<>();
        contactGroupQMUsersList = new ArrayList<>();
        appCallLogModel = new AppCallLogModel();
        appCallLogModelArrayList = new ArrayList<AppCallLogModel>();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        baseActivity.showSnackbar(R.string.dialog_loading_dialogs, Snackbar.LENGTH_INDEFINITE);
        baseActivity.showProgress();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dialogs_list_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                addChat();
//                launchContactsFragment();
//                MainActivity.start(getActivity());
                break;
            default:
                return super.onOptionsItemSelected(item);


        }


        return true;
    }

    //On long click of an item in a listview, Show a delete option to the user
    //From home activity when user goes to chat & connect, the list of all the chats will be
    //shown to them. Here, on long click delete a private/group chat..
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        MenuInflater menuInflater = baseActivity.getMenuInflater();
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        QBChatDialog chatDialog = dialogsListAdapter.getItem(adapterContextMenuInfo.position).getChatDialog();
        if (chatDialog.getType().equals(QBDialogType.GROUP)) {
            menuInflater.inflate(R.menu.dialogs_list_group_ctx_menu, menu);
        } else {
            menuInflater.inflate(R.menu.dialogs_list_private_ctx_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (baseActivity.checkNetworkAvailableWithError() && checkDialogsLoadFinished()) {
                    QBChatDialog chatDialog = dialogsListAdapter.getItem(adapterContextMenuInfo.position).getChatDialog();
                    deleteDialog(chatDialog);
                }
                break;
        }
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addActions();
        if (dialogsListAdapter.getCount() == 0) {
            initDataLoader(LOADER_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (dialogsListAdapter != null) {
            checkVisibilityEmptyLabel();
        }

        if (dialogsListAdapter != null) {
            dialogsListAdapter.notifyDataSetChanged();
        }

        checkLoaderConsumerQueue();
        checkUpdateDialogs();

        if (State.finished == updateDialogsProcess) {
            baseActivity.hideSnackBar(R.string.dialog_loading_dialogs);
            baseActivity.hideProgress();
        }
    }


    private void checkUpdateDialogs() {

//        check if needs update dialog list
        if (!CollectionsUtil.isEmpty(dialogsIdsToUpdate)) {
            QBLoadDialogByIdsCommand.start(getContext(), new ArrayList<>(dialogsIdsToUpdate));
            dialogsIdsToUpdate.clear();
        }
    }

    private void checkLoaderConsumerQueue() {
//        check if the update process can be proceeded
        if (State.stopped == updateDialogsProcess) {
            updateDialogsListFromQueue();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        setStopStateUpdateDialogsProcess();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActions();
        deleteObservers();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult " + requestCode + ", data= " + data);
        if (PICK_DIALOG == requestCode && data != null) {
            String dialogId = data.getStringExtra(QBServiceConsts.EXTRA_DIALOG_ID);
            checkDialogsIds(dialogId);
            updateOrAddDialog(dialogId, data.getBooleanExtra(QBServiceConsts.EXTRA_DIALOG_UPDATE_POSITION, false));
        } else if (CREATE_DIALOG == requestCode && data != null) {
            updateOrAddDialog(data.getStringExtra(QBServiceConsts.EXTRA_DIALOG_ID), true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setStopStateUpdateDialogsProcess() {
        if (updateDialogsProcess != State.finished) {
            updateDialogsProcess = State.stopped;
        }
    }

    private boolean checkDialogsLoadFinished() {
        if (updateDialogsProcess != State.finished) {
            ToastUtils.shortToast(R.string.chat_service_is_initializing);
            return false;
        }
        return true;
    }

    private void checkDialogsIds(String dialogId) {
//       no need update dialog cause it's already updated
        if (dialogsIdsToUpdate != null) {
            for (String dialogIdToUpdate : dialogsIdsToUpdate) {
                if (dialogIdToUpdate.equals(dialogId)) {
                    dialogsIdsToUpdate.remove(dialogId);
                    break;
                }
            }
        }
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

    //OnClick of an item in the listview, This will fire up the chat window
    //depending if its a private or a group chat..
    @OnItemClick(R.id.chats_listview)
    void startChat(int position) {
        QBChatDialog chatDialog = dialogsListAdapter.getItem(position).getChatDialog();
        if (!baseActivity.checkNetworkAvailableWithError() && isFirstOpeningDialog(chatDialog.getDialogId())) {
            return;
        }
        if (QBDialogType.PRIVATE.equals(chatDialog.getType())) {
            startPrivateChatActivity(chatDialog);
        } else {
            startGroupChatActivity(chatDialog);
        }

    }


    //Fab button to start a chat...
    @OnClick(R.id.fab_dialogs_new_chat)
    public void onAddChatClick(View view) {
        addChat();

    }

    private boolean isFirstOpeningDialog(String dialogId) {
        return !dataManager.getMessageDataManager().getTempMessagesByDialogId(dialogId).isEmpty();
    }

    @Override
    public void onConnectedToService(QBService service) {
        if (chatHelper == null) {
            if (service != null) {
                chatHelper = (QBChatHelper) service.getHelper(QBService.CHAT_HELPER);
            }
        }
    }

    @Override
    protected Loader<List<DialogWrapper>> createDataLoader() {
        dialogsListLoader = new DialogsListLoader(getActivity(), dataManager);
        return dialogsListLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<DialogWrapper>> loader, List<DialogWrapper> dialogsList) {
        if (dialogsList.size() > 0) {
            updateDialogsProcess = State.started;
            if (dialogsListLoader.isLoadCacheFinished()) {
                //clear queue after loading all dialogs from cache before updating all dialogs from REST
                loaderConsumerQueue.clear();
            } else {
                updateDialogsListFromQueue();
            }

            updateDialogsAdapter(dialogsList);

            checkEmptyList(dialogsListAdapter.getCount());

            if (!baseActivity.isDialogLoading()) {
                baseActivity.hideSnackBar(R.string.dialog_loading_dialogs);
                baseActivity.hideProgress();
            }

//        startForResult load dialogs from REST when finished loading from cache
            if (dialogsListLoader.isLoadCacheFinished()) {
                QBLoadDialogsCommand.start(getContext(), true);
            }
        }
    }

    private void updateDialogsAdapter(List<DialogWrapper> dialogsList) {
        if (dialogsListLoader.isLoadAll()) {
            dialogsListAdapter.setNewData(dialogsList);
        } else {
            dialogsListAdapter.addNewData((ArrayList<DialogWrapper>) dialogsList);
        }

        if (dialogsListLoader.isLoadRestFinished()) {
            updateDialogsProcess = State.finished;
            Log.d(TAG, "onLoadFinished isLoadRestFinished updateDialogsProcess= " + updateDialogsProcess);
        }
        Log.d(TAG, "onLoadFinished dialogsListAdapter.getCount() " + dialogsListAdapter.getCount());
    }

    private void addChat() {
//        boolean hasFriends = !dataManager.getFriendDataManager().getAll().isEmpty();
        if (isFriendsLoading()) {
            ToastUtils.longToast(R.string.chat_service_is_initializing);
        } /*else if (!hasFriends) {
            ToastUtils.longToast(R.string.new_message_no_friends_for_new_message);
        }*/ else {
            NewMessageActivity.startForResult(this, CREATE_DIALOG);
        }
    }

    private boolean isFriendsLoading() {
        return QBLoginChatCompositeCommand.isRunning();
    }

    private void checkVisibilityEmptyLabel() {
//        emptyListTextView.setVisibility(dialogsListAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void addObservers() {

        dataManager.getQBChatDialogDataManager().addObserver(commonObserver);
        dataManager.getMessageDataManager().addObserver(commonObserver);
        dataManager.getDialogOccupantDataManager().addObserver(commonObserver);
        dataManager.getDialogNotificationDataManager().addObserver(commonObserver);
        ((Observable) QMUserService.getInstance().getUserCache()).addObserver(commonObserver);
    }

    private void deleteObservers() {
        if (dataManager != null) {
            dataManager.getQBChatDialogDataManager().deleteObserver(commonObserver);
            dataManager.getMessageDataManager().deleteObserver(commonObserver);
            dataManager.getDialogOccupantDataManager().deleteObserver(commonObserver);
            dataManager.getDialogNotificationDataManager().deleteObserver(commonObserver);
            ((Observable) QMUserService.getInstance().getUserCache()).deleteObserver(commonObserver);
        }
    }

    private void removeActions() {

        baseActivity.removeAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION);
        baseActivity.removeAction(QBServiceConsts.DELETE_DIALOG_SUCCESS_ACTION);
        baseActivity.removeAction(QBServiceConsts.DELETE_DIALOG_FAIL_ACTION);
        baseActivity.removeAction(QBServiceConsts.UPDATE_CHAT_DIALOG_ACTION);
        baseActivity.removeAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION);
        baseActivity.removeAction(QBServiceConsts.LOAD_CHATS_DIALOGS_FAIL_ACTION);

        baseActivity.updateBroadcastActionList();
    }

    private void addActions() {

        baseActivity.addAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION, loginChatCompositeSuccessAction);
        baseActivity.addAction(QBServiceConsts.DELETE_DIALOG_SUCCESS_ACTION, deleteDialogSuccessAction);
        baseActivity.addAction(QBServiceConsts.DELETE_DIALOG_FAIL_ACTION, deleteDialogFailAction);
        baseActivity.addAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION, loadChatsSuccessAction);
        baseActivity.addAction(QBServiceConsts.LOAD_CHATS_DIALOGS_FAIL_ACTION, loadChatsFailedAction);
        baseActivity.addAction(QBServiceConsts.UPDATE_CHAT_DIALOG_ACTION, updateDialogSuccessAction);

        baseActivity.updateBroadcastActionList();
    }

    private void initChatsDialogs() {

        List<DialogWrapper> dialogsList = new ArrayList<>();
        dialogsListAdapter = new DialogsListAdapter(baseActivity, dialogsList, adapterFlag, DialogsListFragment.this);
    }

    private void initActions() {
        loginChatCompositeSuccessAction = new LoginChatCompositeSuccessAction();
        deleteDialogSuccessAction = new DeleteDialogSuccessAction();
        deleteDialogFailAction = new DeleteDialogFailAction();
        loadChatsSuccessAction = new LoadChatsSuccessAction();
        loadChatsFailedAction = new LoadChatsFailedAction();
        updateDialogSuccessAction = new UpdateDialogSuccessAction();
    }

    private void startPrivateChatActivity(QBChatDialog chatDialog) {
        List<DialogOccupant> occupantsList = dataManager.getDialogOccupantDataManager()
                .getDialogOccupantsListByDialogId(chatDialog.getDialogId());
        QMUser opponent = ChatUtils.getOpponentFromPrivateDialog(UserFriendUtils.createLocalUser(qbUser), occupantsList);
        if (!TextUtils.isEmpty(chatDialog.getDialogId())) {
            PrivateDialogActivity.startForResult(this, opponent, chatDialog, PICK_DIALOG);
        }
    }

    private void startGroupChatActivity(QBChatDialog chatDialog) {
        GroupDialogActivity.startForResult(this, chatDialog, PICK_DIALOG);
    }

    private void updateDialogsList(int startRow, int perPage) {
        if (!loaderConsumerQueue.isEmpty()) {
            loaderConsumerQueue.offer(new LoaderConsumer(startRow, perPage));
            return;
        }

//        if Loader is in loading process, we don't fire onChangedData, cause we do not want interrupt current load task
        if (dialogsListLoader.isLoading) {
            Log.d(TAG, "updateDialogsList dialogsListLoader.isLoading");
            loaderConsumerQueue.offer(new LoaderConsumer(startRow, perPage));
        } else {
//        we don't have tasks in queue, so load dialogs by pages
            if (!isResumed()) {
                loaderConsumerQueue.offer(new LoaderConsumer(startRow, perPage));
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
            loaderConsumerQueue.offer(new LoaderConsumer(true));
            return;
        }

        if (dialogsListLoader.isLoading) {
            Log.d(TAG, "updateDialogsList dialogsListLoader.isLoading");
            loaderConsumerQueue.offer(new LoaderConsumer(true));
        } else {
//        load All dialogs
            if (!isResumed()) {
                Log.d(TAG, "updateDialogsList !isResumed() offer");
                loaderConsumerQueue.offer(new LoaderConsumer(true));
            } else {
                dialogsListLoader.setLoadAll(true);
                onChangedData();
            }
        }
    }

    private void updateDialogsListFromQueue() {
        if (!loaderConsumerQueue.isEmpty()) {
            LoaderConsumer consumer = loaderConsumerQueue.poll();
            handler.post(consumer);
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

    private void deleteDialog(QBChatDialog chatDialog) {

        if (chatDialog == null || chatDialog.getDialogId() == null) {
            return;
        }

        baseActivity.showProgress();
        QBDeleteChatCommand.start(baseActivity, chatDialog.getDialogId(), chatDialog.getType().getCode());
    }

    private void checkEmptyList(int listSize) {
        if (listSize > 0) {
//            emptyListTextView.setVisibility(View.GONE);
        } else {
//            emptyListTextView.setVisibility(View.VISIBLE);
        }
    }

    private void launchContactsFragment() {

        baseActivity.setCurrentFragment(SearchFragment.newInstance(), true);
    }

    private void updateDialogIds(String dialogId) {

        if (dialogsIdsToUpdate == null) {
            dialogsIdsToUpdate = new HashSet<>();
        }
        dialogsIdsToUpdate.add(dialogId);
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


    private class LoginChatCompositeSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            Log.i(TAG, "LoginChatCompositeSuccessAction bundle= " + bundle);
//            if (dialogsListLoader.isLoadCacheFinished()) {
            QBLoadDialogsCommand.start(getContext(), true);
//            }
        }
    }

    private class LoadChatsFailedAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
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


    public void callToUser(List<String> opponentsList, QBRTCTypes.QBConferenceType qbConferenceType) {

        try {
            if (!isChatInitializedAndUserLoggedIn()) {
                ToastUtils.longToast(R.string.call_chat_service_is_initializing);
                return;
            }
            qMUserList = qmUserCache.getUsersByIDs(getUserIntegerId(opponentsList));
            qbUserLists = new ArrayList<>(qMUserList.size());
            qbUserLists.addAll(UserFriendUtils.createQbUserList(qMUserList));
            CallActivity.start(getActivity(), qbUserLists, qbConferenceType, null);
            AppConstant.OPPONENTS = qbUserLists.get(0).getFullName();
            AppConstant.OPPONENT_ID = String.valueOf(qbUserLists.get(0).getId());
        } catch (Exception e) {
            e.getMessage();
        }

        CallHistory();
    }

    public void ContactGroupCall(List<QMUser> contactQMUsers, QBRTCTypes.QBConferenceType qbConferenceType) {

        try {
            if (!isChatInitializedAndUserLoggedIn()) {
                ToastUtils.longToast(R.string.call_chat_service_is_initializing);
                return;
            }
            qbUserLists = new ArrayList<>(contactQMUsers.size());
            qbUserLists.addAll(com.android.konnek2.call.core.utils.UserFriendUtils.createQbUserList(contactQMUsers));
            CallActivity.start(getActivity(), qbUserLists, qbConferenceType, null);

        } catch (Exception e) {
            e.getMessage();
        }

//        CallHistory();
    }

    private ArrayList<Integer> getUserIntegerId(List<String> opponentUsers) {
        ArrayList<Integer> userId = new ArrayList<>();
        for (int i = 0; i < opponentUsers.size(); i++) {
            userId.add(Integer.parseInt(opponentUsers.get(i)));
        }

        return userId;
    }

    protected boolean isChatInitializedAndUserLoggedIn() {

        return isAppInitialized() && QBChatService.getInstance().isLoggedIn();
    }

    protected boolean isAppInitialized() {

        return AppSession.getSession().isSessionExist();
    }

    public QBUser getCurrentUser() {

        return QBChatService.getInstance().getUser();
    }

    public void CallHistory() {

        QBUser CurrentUser = QBChatService.getInstance().getUser();
        AppPreference.putUserName(CurrentUser.getFullName());
        AppConstant.DATE = AppCommon.currentDate();
        AppConstant.TIME = AppCommon.currentTime();

        appCallLogModel.setCallUserName(CurrentUser.getFullName());
        appCallLogModel.setUserId(String.valueOf(CurrentUser.getId()));
        appCallLogModel.setCallOpponentName(AppConstant.OPPONENTS);
        appCallLogModel.setCallOpponentId(String.valueOf(AppConstant.OPPONENT_ID));
        appCallLogModel.setCallDate(AppConstant.DATE);
        appCallLogModel.setCallTime(AppConstant.TIME);
        appCallLogModel.setCallStatus(AppConstant.CALL_STATUS_DIALED);
        appCallLogModel.setCallPriority(" ");
        appCallLogModel.setCallDuration("0");
        appCallLogModel.setCallType(AppConstant.CALL_TYPES);
        appCallLogModelArrayList.add(appCallLogModel);
        App.appcallLogTableDAO.saveCallLog(appCallLogModelArrayList);

    }


}