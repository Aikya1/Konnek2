package com.aikya.konnek2.ui.activities.base;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aikya.konnek2.App;
import com.aikya.konnek2.R;
import com.aikya.konnek2.base.activity.AppSplashActivity;
import com.aikya.konnek2.call.core.core.command.Command;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.qb.commands.chat.QBInitCallChatCommand;
import com.aikya.konnek2.call.core.qb.commands.chat.QBLoginChatCompositeCommand;
import com.aikya.konnek2.call.core.qb.helpers.QBChatHelper;
import com.aikya.konnek2.call.core.qb.helpers.QBFriendListHelper;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.core.utils.ConnectivityUtils;
import com.aikya.konnek2.call.db.utils.ErrorUtils;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.authorization.LandingActivity;
import com.aikya.konnek2.ui.activities.call.CallActivity;
import com.aikya.konnek2.ui.activities.chats.GroupDialogActivity;
import com.aikya.konnek2.ui.activities.chats.PrivateDialogActivity;
import com.aikya.konnek2.ui.fragments.dialogs.base.ProgressDialogFragment;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.bridges.ActionBarBridge;
import com.aikya.konnek2.utils.bridges.ConnectionBridge;
import com.aikya.konnek2.utils.bridges.LoadingBridge;
import com.aikya.konnek2.utils.bridges.SnackbarBridge;
import com.aikya.konnek2.utils.broadcasts.NetworkChangeReceiver;
import com.aikya.konnek2.utils.helpers.ActivityUIHelper;
import com.aikya.konnek2.utils.helpers.LoginHelper;
import com.aikya.konnek2.utils.helpers.SharedHelper;
import com.aikya.konnek2.utils.helpers.notification.NotificationManagerHelper;
import com.aikya.konnek2.utils.listeners.ServiceConnectionListener;
import com.aikya.konnek2.utils.listeners.UserStatusChangingListener;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements ActionBarBridge, ConnectionBridge,
        LoadingBridge, SnackbarBridge {

    private static final String TAG = BaseActivity.class.getSimpleName();
    protected static boolean appInitialized;

    protected App app;
    protected Toolbar toolbar;
    protected TextView Title, subTitle;
    protected SharedHelper appSharedHelper;
    protected Fragment currentFragment;
    protected FailAction failAction;
    protected SuccessAction successAction;
    protected QBFriendListHelper friendListHelper;
    protected QBChatHelper chatHelper;
    protected QBService service;
    protected LocalBroadcastManager localBroadcastManager;
    protected String title;

    private ProgressBar toolbarProgressBar;
    private View snackBarView;
    private ActionBar actionBar;
    private Snackbar snackbar;
    private SparseArray<Priority> snackbarClientPriority;

    private Map<String, Set<Command>> broadcastCommandMap;
    private Set<UserStatusChangingListener> fragmentsStatusChangingSet;
    private Set<ServiceConnectionListener> fragmentsServiceConnectionSet;
    private Handler handler;
    private BaseBroadcastReceiver broadcastReceiver;
    private GlobalBroadcastReceiver globalBroadcastReceiver;
    private UserStatusBroadcastReceiver userStatusBroadcastReceiver;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private boolean bounded;
    private ServiceConnection serviceConnection;
    private ActivityUIHelper activityUIHelper;
    private boolean isDialogLoading = false;
    private ConnectionListener chatConnectionListener;
    private ViewGroup root;
    private boolean isUIDisabled;

    protected abstract int getContentResId();


    /*Intent onlineServiceIntent;
    private OnlineService onlineService;
    Context ctx;
    public Context getCtx()
    {
        return ctx;
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = (ViewGroup) getLayoutInflater().inflate(getContentResId(), null);

        setContentView(root);
        //setContentView(getContentResId());
        initFields();
        activateButterKnife();


        /*ctx = this;
        onlineService=new OnlineService(getCtx());
        onlineServiceIntent=new Intent(getCtx(),onlineService.getClass());
        if (!isMyServiceRunning(onlineService.getClass()))
        {
            startService(onlineServiceIntent);
        }*/


    }

    /*private  boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService();
    }


    @Override
    protected void onDestroy()
    {
        stopService(onlineServiceIntent);
        super.onDestroy();
    }
*/

    @Override
    protected void onStop() {
        super.onStop();
        unbindService();
    }

    @Override
    public void initActionBar() {

        toolbar = findViewById(R.id.toolbar);
        Title = findViewById(R.id.text_title);
        subTitle = findViewById(R.id.text_sub_title);
        toolbarProgressBar = findViewById(R.id.toolbar_progressbar);
        snackBarView = findViewById(R.id.snackbar_position_coordinatorlayout);
        if (toolbar != null) {

            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }
        actionBar = getSupportActionBar();
    }

    @Override
    public void setActionBarTitle(String title) {
        if (actionBar != null) {
            if (title != null) {
                Spannable text = new SpannableString(title);
                text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                actionBar.setTitle(text);
            }

//            Title.setText(title);
        }
    }

    @Override
    public void setActionBarTitle(@StringRes int title) {
        setActionBarTitle(getString(title));
//        Title.setText(getString(title));
    }

    @Override
    public void setActionBarSubtitle(String subtitle) {
        if (actionBar != null) {
            actionBar.setSubtitle(subtitle);
//            subTitle.setText(subtitle);
        }
    }

    @Override
    public void setActionBarSubtitle(@StringRes int subtitle) {
        setActionBarSubtitle(getString(subtitle));
//        subTitle.setText(getString(subtitle));
    }

    @Override
    public void setActionBarIcon(Drawable icon) {
        if (actionBar != null) {
            // In appcompat v21 there will be no icon if we don't add this display option
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(icon);
        }
    }

    @Override
    public void setActionBarIcon(@DrawableRes int icon) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = getDrawable(icon);
        } else {
            drawable = getResources().getDrawable(icon);
        }

        setActionBarIcon(drawable);
    }

    @Override
    public void setActionBarUpButtonEnabled(boolean enabled) {
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(enabled);
            actionBar.setDisplayHomeAsUpEnabled(enabled);
            toolbar.setNavigationIcon(R.drawable.ic_app_back);
        }
    }

    @Override
    public synchronized void showProgress() {
        ProgressDialogFragment.show(getSupportFragmentManager());
    }

    @Override
    public synchronized void hideProgress() {
        ProgressDialogFragment.hide(getSupportFragmentManager());
    }

    @Override
    public void showActionBarProgress() {
//        toolbarProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideActionBarProgress() {
//        toolbarProgressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean checkNetworkAvailableWithError() {
        if (!isNetworkAvailable()) {
            ToastUtils.longToast(R.string.dlg_fail_connection);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isNetworkAvailable() {

        return ConnectivityUtils.isNetworkAvailable(this);
    }

    @Override
    public void createSnackBar(int titleResId, int duration) {
        if (snackBarView != null) {
            snackbar = Snackbar.make(snackBarView, titleResId, duration);
        }
    }

    @Override
    public void showSnackbar(int titleResId, int duration) {
        if (snackBarView != null) {
            createSnackBar(titleResId, duration);
            snackbar.show();
        }
    }

    @Override
    public void showSnackbar(int titleResId, int duration, Priority priority) {
        snackbarClientPriority.put(titleResId, priority);
        if (snackBarView != null) {
            createSnackBar(titleResId, duration);
            snackbar.show();
        }
    }

    @Override
    public void showSnackbar(String title, int duration, int buttonTitleResId, View.OnClickListener onClickListener) {
        if (snackBarView != null) {
            snackbar = Snackbar.make(snackBarView, title, duration);
            snackbar.setAction(buttonTitleResId, onClickListener);
            snackbar.show();
        }
    }

    @Override
    public void hideSnackBar() {

        if (snackbar != null && !isSnackBarHasMaxPriority()) {
            snackbar.dismiss();
        }
    }

    @Override
    public void hideSnackBar(int titleResId) {
        snackbarClientPriority.remove(titleResId);
        hideSnackBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterBroadcastReceivers();
        removeActions();
        unregisterConnectionListener();
        hideSnackBar(R.string.error_disconnected);
    }

    private void unregisterConnectionListener() {

        QBChatService.getInstance().removeConnectionListener(chatConnectionListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("BaseActivity", "onResume");
        registerBroadcastReceivers();
        registerConnectionListener();
        addActions();
        NotificationManagerHelper.clearNotificationEvent(this);
        checkShowingConnectionError();
    }

    private void registerConnectionListener() {
        if (chatConnectionListener == null) {
            initChatConnectionListener();
        }
        QBChatService.getInstance().addConnectionListener(chatConnectionListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectToService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateToParent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CallActivity.CALL_ACTIVITY_CLOSE) {
            if (resultCode == CallActivity.CALL_ACTIVITY_CLOSE_WIFI_DISABLED) {
                ToastUtils.longToast(R.string.wifi_disabled);
            }
        }
    }

    private void initFields() {

        app = App.getInstance();
        appSharedHelper = App.getInstance().getAppSharedHelper();
        activityUIHelper = new ActivityUIHelper(this);
        failAction = new FailAction();
        successAction = new SuccessAction();
        broadcastReceiver = new BaseBroadcastReceiver();
        globalBroadcastReceiver = new GlobalBroadcastReceiver();
        userStatusBroadcastReceiver = new UserStatusBroadcastReceiver();
        networkBroadcastReceiver = new NetworkBroadcastReceiver();
        broadcastCommandMap = new HashMap<>();
        fragmentsStatusChangingSet = new HashSet<>();
        fragmentsServiceConnectionSet = new HashSet<>();
        serviceConnection = new QBChatServiceConnection();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        snackbarClientPriority = new SparseArray<>();
    }

    protected void setUpActionBarWithUpButton() {
        try {
            initActionBar();
            setActionBarUpButtonEnabled(true);
            setActionBarTitle(title);

        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void addFragmentUserStatusChangingListener(
            UserStatusChangingListener fragmentUserStatusChangingListener) {
        if (fragmentsStatusChangingSet == null) {
            fragmentsStatusChangingSet = new HashSet<>();
        }
        fragmentsStatusChangingSet.add(fragmentUserStatusChangingListener);
    }

    public void removeFragmentUserStatusChangingListener(
            UserStatusChangingListener fragmentUserStatusChangingListener) {
        fragmentsStatusChangingSet.remove(fragmentUserStatusChangingListener);
    }

    public void addFragmentServiceConnectionListener(
            ServiceConnectionListener fragmentServiceConnectionListener) {
        if (fragmentsServiceConnectionSet == null) {
            fragmentsServiceConnectionSet = new HashSet<>();
        }
        fragmentsServiceConnectionSet.add(fragmentServiceConnectionListener);
    }

    public void removeFragmentServiceConnectionListener(
            ServiceConnectionListener fragmentServiceConnectionListener) {
        fragmentsServiceConnectionSet.remove(fragmentServiceConnectionListener);
    }

    public void onChangedUserStatus(int userId, boolean online) {
        notifyChangedUserStatus(userId, online);
    }

    public void notifyChangedUserStatus(int userId, boolean online) {
        if (!fragmentsStatusChangingSet.isEmpty()) {
            Iterator<UserStatusChangingListener> iterator = fragmentsStatusChangingSet.iterator();
            while (iterator.hasNext()) {
                iterator.next().onChangedUserStatus(userId, online);
            }
        }
    }

    public void notifyConnectedToService() {
        if (!fragmentsServiceConnectionSet.isEmpty()) {
            Iterator<ServiceConnectionListener> iterator = fragmentsServiceConnectionSet.iterator();
            while (iterator.hasNext()) {
                iterator.next().onConnectedToService(service);
            }
        }
    }

    public void onConnectedToService(QBService service) {
        if (friendListHelper == null) {
            friendListHelper = (QBFriendListHelper) service.getHelper(QBService.FRIEND_LIST_HELPER);
        }

        if (chatHelper == null) {
            chatHelper = (QBChatHelper) service.getHelper(QBService.CHAT_HELPER);
        }

        notifyConnectedToService();
    }

    private void unbindService() {
        if (bounded) {
            unbindService(serviceConnection);
            bounded = false;
        }
    }

    private void connectToService() {
        Intent intent = new Intent(this, QBService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void registerBroadcastReceivers() {

        IntentFilter globalActionsIntentFilter = new IntentFilter();
        globalActionsIntentFilter.addAction(QBServiceConsts.GOT_CHAT_MESSAGE_LOCAL);
        globalActionsIntentFilter.addAction(QBServiceConsts.GOT_CONTACT_REQUEST);
        globalActionsIntentFilter.addAction(QBServiceConsts.FORCE_RELOGIN);
        globalActionsIntentFilter.addAction(QBServiceConsts.TYPING_MESSAGE);
        IntentFilter networkIntentFilter = new IntentFilter(NetworkChangeReceiver.ACTION_LOCAL_CONNECTIVITY);
        IntentFilter userStatusIntentFilter = new IntentFilter(QBServiceConsts.USER_STATUS_CHANGED_ACTION);

        localBroadcastManager.registerReceiver(globalBroadcastReceiver, globalActionsIntentFilter);
        localBroadcastManager.registerReceiver(userStatusBroadcastReceiver, userStatusIntentFilter);
        localBroadcastManager.registerReceiver(networkBroadcastReceiver, networkIntentFilter);
    }

    private void unregisterBroadcastReceivers() {

        localBroadcastManager.unregisterReceiver(globalBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        localBroadcastManager.unregisterReceiver(userStatusBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(networkBroadcastReceiver);
    }

    private void addActions() {

        addAction(QBServiceConsts.LOGIN_REST_SUCCESS_ACTION, successAction);
        addAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION, new LoginChatCompositeSuccessAction());
        addAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_FAIL_ACTION, new LoginChatCompositeFailAction());
        updateBroadcastActionList();
    }

    private void removeActions() {

        removeAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION);
        removeAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_FAIL_ACTION);
        removeAction(QBServiceConsts.LOGIN_CHAT_SUCCESS_ACTION);
        removeAction(QBServiceConsts.LOGIN_CHAT_FAIL_ACTION);
        updateBroadcastActionList();
    }

    private void navigateToParent() {

        Intent intent = NavUtils.getParentActivityIntent(this);
        if (intent == null) {
            finish();
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    protected void checkShowingConnectionError() {
        if (!isNetworkAvailable()) {
            setActionBarTitle(getString(R.string.dlg_internet_connection_is_missing));
        } else {
            setActionBarTitle(title);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T _findViewById(int viewId) {
        return (T) findViewById(viewId);
    }

    public void setCurrentFragment(Fragment fragment) {
        setCurrentFragment(fragment, null);
    }

    public void setCurrentFragment(Fragment fragment, boolean needAddToBackStack) {
        setCurrentFragment(fragment, null, needAddToBackStack);
    }

    private void setCurrentFragment(Fragment fragment, String tag) {
        currentFragment = fragment;
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = buildTransaction();
        transaction.replace(R.id.container_fragment, fragment, tag);
        transaction.commit();
    }

    private void setCurrentFragment(Fragment fragment, String tag, boolean needAddToBackStack) {

        currentFragment = fragment;
        FragmentTransaction transaction = buildTransaction();
        if (needAddToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container_fragment, fragment, tag);
        transaction.commit();
    }

    public void removeFragment() {

        if (!isFinishing()) {
            getSupportFragmentManager().beginTransaction().remove(
                    getSupportFragmentManager().findFragmentById(R.id.container_fragment)).commitAllowingStateLoss();
        }
    }

    private FragmentTransaction buildTransaction() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        return transaction;
    }

    private boolean needShowReceivedNotification() {
//        boolean isSplashActivity = this instanceof SplashActivity;
        boolean isSplashActivity = this instanceof AppSplashActivity;
        boolean isCallActivity = this instanceof CallActivity;
        return !isSplashActivity && !isCallActivity;
    }

    protected void onSuccessAction(String action) {
    }

    protected void onFailAction(String action) {
    }

    protected void onChatReconnected() {

    }

    protected void onChatDisconnected(Exception e) {

    }

    protected void onReceivedChatMessageNotification(Bundle extras) {
        activityUIHelper.showChatMessageNotification(extras);
    }

    protected void onReceivedContactRequestNotification(Bundle extras) {
        activityUIHelper.showContactRequestNotification(extras);
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    public void addAction(String action, Command command) {
        Set<Command> commandSet = broadcastCommandMap.get(action);
        if (commandSet == null) {
            commandSet = new HashSet<Command>();
            broadcastCommandMap.put(action, commandSet);
        }
        commandSet.add(command);
    }

    public boolean hasAction(String action) {
        return broadcastCommandMap.containsKey(action);
    }

    public void removeAction(String action) {
        broadcastCommandMap.remove(action);
    }

    public void updateBroadcastActionList() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        IntentFilter intentFilter = new IntentFilter();
        for (String commandName : broadcastCommandMap.keySet()) {
            intentFilter.addAction(commandName);
        }
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void onReceiveChatMessageAction(Bundle extras) {
        if (needShowReceivedNotification()) {
            onReceivedChatMessageNotification(extras);
        }
    }


    public void onReceiveContactRequestAction(Bundle extras) {
        if (needShowReceivedNotification()) {
            onReceivedContactRequestNotification(extras);
        }
    }

    public QBService getService() {
        return service;
    }

    public QBFriendListHelper getFriendListHelper() {
        return friendListHelper;
    }

    public QBChatHelper getChatHelper() {
        return chatHelper;
    }

    public FailAction getFailAction() {
        return failAction;
    }

    public boolean isDialogLoading() {
        return isDialogLoading;
    }

    protected void loginChat() {
        isDialogLoading = true;
//        showSnackbar(R.string.dialog_loading_dialogs, Snackbar.LENGTH_INDEFINITE, Priority.MAX);
        QBLoginChatCompositeCommand.start(this);
    }

    protected boolean isAppInitialized() {
        return AppSession.getSession().isSessionExist();
    }

    protected boolean isChatInitializedAndUserLoggedIn() {
        return isAppInitialized() && QBChatService.getInstance().isLoggedIn();
    }

    public void startPrivateChatActivity(QMUser user, QBChatDialog dialog) {
        PrivateDialogActivity.start(this, user, dialog);
    }

    public void startGroupChatActivity(QBChatDialog chatDialog) {
        GroupDialogActivity.start(this, chatDialog);
    }

    protected void startLandingScreen() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        LandingActivity.start(this, intent);
        finish();
    }

    protected void performLoginChatSuccessAction(Bundle bundle) {
        QBInitCallChatCommand.start(this, CallActivity.class);
        hideProgress();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    private void initChatConnectionListener() {

        chatConnectionListener = new ConnectionListener() {
            @Override
            public void connected(XMPPConnection xmppConnection) {
                hideSnackBar(R.string.error_disconnected);
            }

            @Override
            public void authenticated(XMPPConnection xmppConnection, boolean b) {
                hideSnackBar(R.string.error_disconnected);
                blockUI(false);
            }

            @Override
            public void connectionClosed() {

            }

            @Override
            public void connectionClosedOnError(Exception e) {
                onChatDisconnected(e);
                blockUI(true);
                showSnackbar(R.string.error_disconnected, Snackbar.LENGTH_INDEFINITE);
            }

            @Override
            public void reconnectionSuccessful() {
                onChatReconnected();
                hideSnackBar(R.string.error_disconnected);
                blockUI(false);
            }

            @Override
            public void reconnectingIn(int i) {

            }

            @Override
            public void reconnectionFailed(Exception e) {

            }
        };
    }

    private void blockUI(boolean stopUserInteractions) {
        if (isUIDisabled == stopUserInteractions) {
            return;
        }
        isUIDisabled = stopUserInteractions;
        disableEnableControls(!stopUserInteractions, root);
    }

    private void disableEnableControls(boolean enable, ViewGroup vg) {
        if (vg instanceof Toolbar) {
            return;
        }

        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
        }
    }

    private void activateButterKnife() {
        ButterKnife.bind(this);
    }

    private boolean isSnackBarHasMaxPriority() {
        if (snackbarClientPriority.size() == 0) {
            return false;
        }
        for (int i = 0; i < snackbarClientPriority.size(); i++) {
            Log.i(TAG, "snackbar[" + i + ")=" + snackbarClientPriority.valueAt(i));
            if (Priority.MAX == snackbarClientPriority.valueAt(i)) {
                return true;
            }
        }
        return false;
    }

    protected void performLoadChatsSuccessAction(Bundle bundle) {
        // hideSnackBar();
        Log.d(TAG, "BUndle == " + bundle.toString());
        isDialogLoading = false;
    }

    protected void startActivityByName(Class<?> activityName, boolean needClearTask) {
        Intent intent = new Intent(this, activityName);
        if (needClearTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        startActivity(intent);
        finish();
    }

    public class LoadChatsSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            performLoadChatsSuccessAction(bundle);
        }
    }

    public class FailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            Exception e = (Exception) bundle.getSerializable(QBServiceConsts.EXTRA_ERROR);
            ErrorUtils.showError(BaseActivity.this, e);
            hideProgress();
            onFailAction(bundle.getString(QBServiceConsts.COMMAND_ACTION));
        }
    }

    public class SuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            hideProgress();
            onSuccessAction(bundle.getString(QBServiceConsts.COMMAND_ACTION));
        }
    }

    public class LoginChatCompositeSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            QBLoginChatCompositeCommand.setIsRunning(false);
            performLoginChatSuccessAction(bundle);
        }
    }

    public class LoginChatCompositeFailAction implements Command {
        @Override
        public void execute(Bundle bundle) {
            QBLoginChatCompositeCommand.setIsRunning(false);
            blockUI(true);
            hideSnackBar(R.string.dialog_loading_dialogs);
//            showSnackbar(R.string.error_disconnected, Snackbar.LENGTH_INDEFINITE, Priority.MAX);
            hideSnackBar(R.string.error_disconnected);
        }
    }

    private class NetworkBroadcastReceiver extends BroadcastReceiver {

        private boolean loggedIn = false;

        @Override
        public void onReceive(Context context, Intent intent) {

            boolean activeConnection = intent
                    .getBooleanExtra(NetworkChangeReceiver.EXTRA_IS_ACTIVE_CONNECTION, false);

            checkShowingConnectionError();

            if (activeConnection) {

                if (!loggedIn && LoginHelper.isCorrectOldAppSession()) {
                    loggedIn = true;

                    LoginHelper loginHelper = new LoginHelper(BaseActivity.this);
                    loginHelper.makeGeneralLogin(null);
                }
            }
        }
    }

    private class BaseBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {

            String action = intent.getAction();
            if (action != null) {

                final Set<Command> commandSet = broadcastCommandMap.get(action);

                if (commandSet != null && !commandSet.isEmpty()) {
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            for (Command command : commandSet) {
                                try {
                                    command.execute(intent.getExtras());
                                } catch (Exception e) {
                                    ErrorUtils.logError(e);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    private class GlobalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {

            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (intent == null) {
                        return;
                    }

                    if (QBServiceConsts.GOT_CHAT_MESSAGE_LOCAL.equals(intent.getAction())) {
                        onReceiveChatMessageAction(intent.getExtras());
                    } else if (QBServiceConsts.GOT_CONTACT_REQUEST.equals(intent.getAction())) {
                        onReceiveContactRequestAction(intent.getExtras());
                    } else if (QBServiceConsts.FORCE_RELOGIN.equals(intent.getAction())) {
                        //                        onReceiveForceReloginAction(intent.getExtras());
                    } else if (QBServiceConsts.REFRESH_SESSION.equals(intent.getAction())) {
                        //onReceiveRefreshSessionAction(intent.getExtras());
                    }
                }
            });
        }
    }

    private class UserStatusBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int userId = intent.getIntExtra(QBServiceConsts.EXTRA_USER_ID, 0);
            boolean status = intent.getBooleanExtra(QBServiceConsts.EXTRA_USER_STATUS, false);
            onChangedUserStatus(userId, status);
        }
    }

    private class QBChatServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            bounded = true;
            service = ((QBService.QBServiceBinder) binder).getService();
            onConnectedToService(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}