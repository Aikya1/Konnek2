package com.android.konnek2.ui.activities.call;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Chronometer;

import com.android.konnek2.App;
import com.android.konnek2.R;
import com.android.konnek2.call.core.models.StartConversationReason;
import com.android.konnek2.call.core.qb.helpers.QBCallChatHelper;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.core.utils.UserFriendUtils;
import com.android.konnek2.call.core.utils.call.RingtonePlayer;
import com.android.konnek2.call.core.utils.call.SettingsUtil;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.db.models.Friend;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.base.db.AppCallLogModel;
import com.android.konnek2.ui.activities.base.BaseLoggableActivity;
import com.android.konnek2.ui.fragments.call.ConversationCallFragment;
import com.android.konnek2.ui.fragments.call.IncomingCallFragment;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.StringUtils;
import com.android.konnek2.utils.ToastUtils;
import com.android.konnek2.utils.helpers.SystemPermissionHelper;
import com.android.konnek2.utils.listeners.CallDurationInterface;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;

import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.BaseSession;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCSessionDescription;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.QBSignalingSpec;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionConnectionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSignalingCallback;
import com.quickblox.videochat.webrtc.exception.QBRTCSignalException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

public class CallActivity extends BaseLoggableActivity implements
        QBRTCClientSessionCallbacks, QBRTCSessionConnectionCallbacks, QBRTCSignalingCallback, CallDurationInterface {

    public static final int CALL_ACTIVITY_CLOSE = 1000;
    public static final int CALL_ACTIVITY_CLOSE_WIFI_DISABLED = 1001;

    public void checkPermissionsAndStartCall(StartConversationReason startConversationReason) {

        if (systemPermissionHelper.isAllPermissionsGrantedForCallByType(qbConferenceType)) {
            startConversationFragment(startConversationReason);
        } else {
            systemPermissionHelper.requestPermissionsForCallByType(qbConferenceType);
        }
    }

    public static final String EXTRA_QB_SESSION = "extra_qb_session";

    private static final String TAG = CallActivity.class.getSimpleName();

    @Bind(R.id.timer_chronometer)
    Chronometer timerChronometer;
    private QBRTCTypes.QBConferenceType qbConferenceType;
    private List<QBUser> opponentsList;
    private QBChatDialog qbChatDialogs;
    private Runnable showIncomingCallWindowTask;
    private Handler showIncomingCallWindowTaskHandler;
    private BroadcastReceiver wifiStateReceiver;
    private boolean closeByWifiStateAllow = true;
    private String hangUpReason;
    private boolean isInComingCall;
    private boolean isInFront;
    private QBRTCClient qbRtcClient;
    private boolean wifiEnabled = true;
    private RingtonePlayer ringtonePlayer;

    private boolean isStarted = false;
    private QBRTCSessionUserCallback qbRtcSessionUserCallback;
    private QBCallChatHelper qbCallChatHelper;
    private StartConversationReason startConversationReason;
    private QBRTCSessionDescription qbRtcSessionDescription;
    private ActionBar actionBar;
    private AudioStreamReceiver audioStreamReceiver;
    private String ACTION_ANSWER_CALL = "action_answer_call";
    private SystemPermissionHelper systemPermissionHelper;
    private AppCallLogModel appCallLogModel;
    private ArrayList<AppCallLogModel> appCallLogModelArrayList;

    private String Name, Date, Times;

    public static void start(Activity activity, List<QBUser> qbUsersList, QBRTCTypes.QBConferenceType qbConferenceType,
                             QBRTCSessionDescription qbRtcSessionDescription) {
        Intent intent = new Intent(activity, CallActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_OPPONENTS, (Serializable) qbUsersList);
        intent.putExtra(QBServiceConsts.EXTRA_CONFERENCE_TYPE, qbConferenceType);
        intent.putExtra(QBServiceConsts.EXTRA_START_CONVERSATION_REASON_TYPE, StartConversationReason.OUTCOME_CALL_MADE);
        intent.putExtra(QBServiceConsts.EXTRA_SESSION_DESCRIPTION, qbRtcSessionDescription);
        activity.startActivityForResult(intent, CALL_ACTIVITY_CLOSE);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_call;
    }

    public void initActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_call);
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
//            toolbar.setNavigationIcon(R.drawable.ic_app_back);
        }
        actionBar = getSupportActionBar();
    }

    public void setCallActionBarTitle(String title) {
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    public void hideCallActionBar() {
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void showCallActionBar() {
        if (actionBar != null) {
            actionBar.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        canPerformLogout.set(false);
        initFields();

        audioStreamReceiver = new AudioStreamReceiver();
        initWiFiManagerListener();

        if (ACTION_ANSWER_CALL.equals(getIntent().getAction())) {
            checkPermissionsAndStartCall(StartConversationReason.INCOME_CALL_FOR_ACCEPTION);
        }
    }

    private void initCallFragment() {

        if (startConversationReason != null) {
            switch (startConversationReason) {
                case INCOME_CALL_FOR_ACCEPTION:

                    if (qbRtcSessionDescription != null) {
                        addIncomingCallFragment(qbRtcSessionDescription);
                        isInComingCall = true;
                        initIncomingCallTask();
                    }
                    break;
                case OUTCOME_CALL_MADE:

                    checkPermissionsAndStartCall(StartConversationReason.OUTCOME_CALL_MADE);
                    break;
            }

        }


    }

    @Override
    protected void onStop() {


        super.onStop();
        unregisterReceiver(wifiStateReceiver);
        unregisterReceiver(audioStreamReceiver);
    }

    @Override
    protected void onPause() {


        isInFront = false;
        super.onPause();
        appSharedHelper.saveLastOpenActivity(getClass().getName());
    }

    @Override
    protected void onResume() {


        isInFront = true;
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        registerReceiver(wifiStateReceiver, intentFilter);
        registerReceiver(audioStreamReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.call_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        opponentsList = null;
        if (qbCallChatHelper != null) {
            qbCallChatHelper.removeRTCSessionUserCallback();
            qbCallChatHelper.releaseCurrentSession(CallActivity.this, CallActivity.this);
        }
        appSharedHelper.saveLastOpenActivity(null);
    }

    @Override
    public void onSuccessSendingPacket(QBSignalingSpec.QBSignalCMD qbSignalCMD, Integer integer) {


    }

    @Override
    public void onErrorSendingPacket(QBSignalingSpec.QBSignalCMD qbSignalCMD, Integer userId,
                                     QBRTCSignalException e) {


        ToastUtils.longToast(R.string.dlg_signal_error);
    }

    @Override
    public void onReceiveNewSession(final QBRTCSession session) {

    }

    @Override
    public void onUserNotAnswer(QBRTCSession session, Integer userID) {


        if (!session.equals(getCurrentSession())) {
            return;
        }

        if (qbRtcSessionUserCallback != null) {
            qbRtcSessionUserCallback.onUserNotAnswer(session, userID);
        }

        ringtonePlayer.stop();
    }

    @Override
    public void onCallRejectByUser(QBRTCSession session, Integer userID, Map<String, String> userInfo) {


        if (!session.equals(getCurrentSession())) {
            return;
        }

        if (qbRtcSessionUserCallback != null) {
            qbRtcSessionUserCallback.onCallRejectByUser(session, userID, userInfo);
        }

        ringtonePlayer.stop();
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo) {

        if (!session.equals(getCurrentSession())) {
            return;
        }

        if (qbRtcSessionUserCallback != null) {
            qbRtcSessionUserCallback.onCallAcceptByUser(session, userId, userInfo);
        }

        ringtonePlayer.stop();
    }

    @Override
    public void onReceiveHangUpFromUser(final QBRTCSession session, final Integer userID, Map<String, String> map) {


        if (session.equals(getCurrentSession())) {

            if (qbRtcSessionUserCallback != null) {
                qbRtcSessionUserCallback.onReceiveHangUpFromUser(session, userID);

            }
            final String participantName = UserFriendUtils.getUserNameByID(userID, opponentsList);

            ToastUtils.longToast("User " + participantName + " " + getString(
                    R.string.call_hung_up) + " conversation");
        }
    }

    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {


        startIncomeCallTimer(0);
    }

    @Override
    public void onSessionClosed(final QBRTCSession session) {
        if (session.equals(getCurrentSession())) {
            Fragment currentFragment = getCurrentFragment();
            if (isInComingCall) {
                stopIncomeCallTimer();
                if (currentFragment instanceof IncomingCallFragment) {
                    removeFragment();
                    finish();
                }
            }

            Log.d(TAG, "Stop session");

            if (qbCallChatHelper != null) {
                qbCallChatHelper.releaseCurrentSession(CallActivity.this, CallActivity.this);
            }

            stopTimer();
            closeByWifiStateAllow = true;
            finish();
        }
    }

    @Override
    public void onSessionStartClose(final QBRTCSession session) {

        session.removeSessionCallbacksListener(CallActivity.this);
        if (currentFragment instanceof ConversationCallFragment && session.equals(getCurrentSession())) {
            ((ConversationCallFragment) currentFragment).actionButtonsEnabled(false);
        }
    }

    @Override
    public void onStartConnectToUser(QBRTCSession session, Integer userID) {


    }

    @Override
    public void onStateChanged(QBRTCSession qbrtcSession, BaseSession.QBRTCSessionState qbrtcSessionState) {


    }

    @Override
    public void onConnectedToUser(QBRTCSession session, final Integer userID) {

        forbiddenCloseByWifiState();
        if (isInComingCall) {
            stopIncomeCallTimer();
        }
        Log.d(TAG, "onConnectedToUser() is started");
    }

    @Override
    public void onConnectionClosedForUser(QBRTCSession session, Integer userID) {
        // Close app after session close of network was disabled

        if (hangUpReason != null && hangUpReason.equals(QBServiceConsts.EXTRA_WIFI_DISABLED)) {
            Intent returnIntent = new Intent();
            setResult(CALL_ACTIVITY_CLOSE_WIFI_DISABLED, returnIntent);
        }
        finish();
    }

    @Override
    public void onDisconnectedFromUser(QBRTCSession session, Integer userID) {


    }

    @Override
    public void onDisconnectedTimeoutFromUser(QBRTCSession session, Integer userID) {


    }

    @Override
    public void onConnectionFailedWithUser(QBRTCSession session, Integer userID) {


    }

    @Override
    public void onConnectedToService(QBService service) {

        super.onConnectedToService(service);
        if (qbCallChatHelper == null) {
            qbCallChatHelper = (QBCallChatHelper) service.getHelper(QBService.CALL_CHAT_HELPER);
            systemPermissionHelper = new SystemPermissionHelper(CallActivity.this);
            qbCallChatHelper.addRTCSessionUserCallback(CallActivity.this);
            initCallFragment();
        }
    }

    private void initFields() {
        try {

            appCallLogModel = new AppCallLogModel();
            appCallLogModelArrayList = new ArrayList<>();
            opponentsList = (List<QBUser>) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_OPPONENTS);
            qbConferenceType = (QBRTCTypes.QBConferenceType) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_CONFERENCE_TYPE);
            startConversationReason = (StartConversationReason) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_START_CONVERSATION_REASON_TYPE);
            qbRtcSessionDescription = (QBRTCSessionDescription) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_SESSION_DESCRIPTION);
            qbChatDialogs = (QBChatDialog) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_DIALOG);
            ringtonePlayer = new RingtonePlayer(this, R.raw.beep);
            // Add activity as callback to RTCClient
            qbRtcClient = QBRTCClient.getInstance(CallActivity.this);

        } catch (Exception e) {
            e.getMessage();
        }


    }

    private void initWiFiManagerListener() {

        wifiStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "WIFI was changed");
                processCurrentWifiState(context);
            }
        };
    }

    public QMUser getOpponentAsUserFromDB(int opponentId) {

        DataManager dataManager = DataManager.getInstance();
        Friend friend = dataManager.getFriendDataManager().getByUserId(opponentId);


        return friend.getUser();
    }

    private void processCurrentWifiState(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(WIFI_SERVICE);
        if (wifiEnabled != wifi.isWifiEnabled()) {
            wifiEnabled = wifi.isWifiEnabled();
            ToastUtils.longToast("Wifi " + (wifiEnabled ? "enabled" : "disabled"));
        }
    }

    private void disableConversationFragmentButtons() {
        if (currentFragment instanceof ConversationCallFragment) {
            ((ConversationCallFragment) currentFragment).actionButtonsEnabled(false);
        }
    }

    private void initIncomingCallTask() {

        showIncomingCallWindowTaskHandler = new Handler(Looper.myLooper());
        showIncomingCallWindowTask = new Runnable() {
            @Override
            public void run() {
                if (currentFragment instanceof ConversationCallFragment) {
                    disableConversationFragmentButtons();
                    ringtonePlayer.stop();

                    hangUpCurrentSession();
                } else {

                    rejectCurrentSession();
                }

                ToastUtils.longToast("Call was stopped by timer");
            }
        };
    }

    public void rejectCurrentSession() {
        if (qbCallChatHelper != null && qbCallChatHelper.getCurrentRtcSession() != null) {
            qbCallChatHelper.getCurrentRtcSession().rejectCall(new HashMap<String, String>());
        }
    }

    public void hangUpCurrentSession() {
        ringtonePlayer.stop();
        if (qbCallChatHelper != null && qbCallChatHelper.getCurrentRtcSession() != null) {
            qbCallChatHelper.getCurrentRtcSession().hangUp(new HashMap<String, String>());
        }
    }

    private void startIncomeCallTimer(long time) {

        showIncomingCallWindowTaskHandler
                .postAtTime(showIncomingCallWindowTask, SystemClock.uptimeMillis() + time);
    }

    private void stopIncomeCallTimer() {

        showIncomingCallWindowTaskHandler.removeCallbacks(showIncomingCallWindowTask);
    }

    private void forbiddenCloseByWifiState() {

        closeByWifiStateAllow = false;
    }

    private Fragment getCurrentFragment() {

        return getSupportFragmentManager().findFragmentById(R.id.container_fragment);
    }

    private void addIncomingCallFragment(QBRTCSessionDescription qbRtcSessionDescription) {

        if (isInFront) {
            Fragment fragment = new IncomingCallFragment();
            Bundle bundle = new Bundle();

            bundle.putSerializable(QBServiceConsts.EXTRA_SESSION_DESCRIPTION, qbRtcSessionDescription);
            bundle.putIntegerArrayList(QBServiceConsts.EXTRA_OPPONENTS, new ArrayList<>(qbRtcSessionDescription.getOpponents()));
            bundle.putSerializable(QBServiceConsts.EXTRA_CONFERENCE_TYPE,
                    qbRtcSessionDescription.getConferenceType());
            fragment.setArguments(bundle);
            setCurrentFragment(fragment);
        } else {

        }
    }

    public void addConversationCallFragment() {

        QBRTCSession newSessionWithOpponents = qbRtcClient.createNewSessionWithOpponents(
                UserFriendUtils.getFriendIdsList(opponentsList), qbConferenceType);
        SettingsUtil.setSettingsStrategy(this, opponentsList);
        if (qbCallChatHelper != null) {
            qbCallChatHelper.initCurrentSession(newSessionWithOpponents, this, this);

            ConversationCallFragment fragment = ConversationCallFragment
                    .newInstance(opponentsList, opponentsList.get(0).getFullName(), qbConferenceType,
                            StartConversationReason.OUTCOME_CALL_MADE,
                            qbCallChatHelper.getCurrentRtcSession().getSessionID());

            setCurrentFragment(fragment);
            ringtonePlayer.play(true);
        } else {

            throw new NullPointerException("qbCallChatHelper is not initialized");
        }
    }

    public List<QBUser> getOpponentsList() {
        return opponentsList;
    }

    public void setOpponentsList(List<QBUser> qbUsers) {
        this.opponentsList = qbUsers;
    }

    public void addConversationFragmentReceiveCall() {
        if (qbCallChatHelper != null) {

            QBRTCSession session = qbCallChatHelper.getCurrentRtcSession();

            if (session != null) {

                Integer myId = QBChatService.getInstance().getUser().getId();
                ArrayList<Integer> opponentsWithoutMe = new ArrayList<>(session.getOpponents());
                opponentsWithoutMe.remove(new Integer(myId));
                opponentsWithoutMe.add(session.getCallerID());

                ArrayList<QBUser> newOpponents = (ArrayList<QBUser>) UserFriendUtils
                        .getUsersByIDs(opponentsWithoutMe.toArray(new Integer[opponentsWithoutMe.size()]),
                                opponentsList);
                SettingsUtil.setSettingsStrategy(this, newOpponents);
                ConversationCallFragment fragment = ConversationCallFragment.newInstance(newOpponents,
                        UserFriendUtils.getUserNameByID(session.getCallerID(), newOpponents),
                        session.getConferenceType(), StartConversationReason.INCOME_CALL_FOR_ACCEPTION,
                        session.getSessionID());
                // Start conversation fragment
                setCurrentFragment(fragment);
            }
        }
    }

    public void startTimer() {


        if (!isStarted) {
            timerChronometer.setVisibility(View.VISIBLE);
            timerChronometer.setBase(SystemClock.elapsedRealtime());
            timerChronometer.start();
            isStarted = true;
        }
    }

    private void stopTimer() {


        if (timerChronometer != null) {
            timerChronometer.stop();
            isStarted = false;

            appCallLogModel.setCallUserName(AppConstant.CALL_DURATION_NAME);
            appCallLogModel.setCallTime(AppConstant.CALL_DURATION_TIME);
            appCallLogModel.setCallDate(AppConstant.CALL_DURATION_DATE);
            appCallLogModel.setCallDuration(timerChronometer.getText().toString());
            appCallLogModelArrayList.add(appCallLogModel);
            App.appcallLogTableDAO.UpdateCallLog(appCallLogModelArrayList);
        }
    }

    public QBRTCSession getCurrentSession() {

        if (qbCallChatHelper != null) {
//            Log.d("AVCALLFUNCATION", " CallActivity getCurrentRtcSession"+qbCallChatHelper.getCurrentRtcSession());
            return qbCallChatHelper.getCurrentRtcSession();

        } else {
            return null;
        }
    }

    public void addVideoTrackCallbacksListener(QBRTCClientVideoTracksCallbacks videoTracksCallbacks) {

        if (getCurrentSession() != null) {
            getCurrentSession().addVideoTrackCallbacksListener(videoTracksCallbacks);
        }
    }

    @Override
    public void onBackPressed() {
        //blocked back button
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SystemPermissionHelper.PERMISSIONS_FOR_CALL_REQUEST: {
                if (grantResults.length > 0) {
                    if (!systemPermissionHelper.isAllPermissionsGrantedForCallByType(qbConferenceType)) {
                        showToastDeniedPermissions(permissions, grantResults);
                    }
                }
                //postDelayed() is temp fix before fixing this bug https://code.google.com/p/android/issues/detail?id=190966
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startConversationFragment(startConversationReason);
                    }
                }, 500);
            }
        }
    }

    private void startConversationFragment(StartConversationReason startConversationReason) {

        if (StartConversationReason.OUTCOME_CALL_MADE.equals(startConversationReason)) {
            addConversationCallFragment();
        } else {
            addConversationFragmentReceiveCall();
        }
    }

    private void showToastDeniedPermissions(String[] permissions, int[] grantResults) {
        ArrayList<String> deniedPermissions = systemPermissionHelper
                .collectDeniedPermissionsFomResult(permissions, grantResults);

        ToastUtils.longToast(
                getString(R.string.denied_permission_message, StringUtils.createCompositeString(deniedPermissions)));
    }

    public void addTCClientConnectionCallback(QBRTCSessionConnectionCallbacks clientConnectionCallbacks) {

        if (getCurrentSession() != null) {
            getCurrentSession().addSessionCallbacksListener(clientConnectionCallbacks);
        }
    }

    public void removeRTCClientConnectionCallback(QBRTCSessionConnectionCallbacks clientConnectionCallbacks) {


        if (getCurrentSession() != null) {
            getCurrentSession().removeSessionCallbacksListener(clientConnectionCallbacks);
        }
    }

    public void addRTCSessionUserCallback(QBRTCSessionUserCallback qbRtcSessionUserCallback) {


        this.qbRtcSessionUserCallback = qbRtcSessionUserCallback;
    }

    public void removeRTCSessionUserCallback() {
        this.qbRtcSessionUserCallback = null;
    }

    @Override
    public void callDuration(String name, String date, String Time) {


        AppConstant.CALL_DURATION_NAME = name;
        AppConstant.CALL_DURATION_DATE = date;
        AppConstant.CALL_DURATION_TIME = Time;

    }

    private class AudioStreamReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getAction().equals(AudioManager.ACTION_HEADSET_PLUG)) {
            } else if (intent.getAction().equals(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)) {
                Log.d(TAG, "ACTION_SCO_AUDIO_STATE_UPDATED " + intent.getIntExtra("EXTRA_SCO_AUDIO_STATE", -2));
            }

            invalidateOptionsMenu();
        }
    }

    public interface QBRTCSessionUserCallback {

        void onUserNotAnswer(QBRTCSession session, Integer userId);

        void onCallRejectByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo);

        void onCallAcceptByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo);

        void onReceiveHangUpFromUser(QBRTCSession session, Integer userId);

    }


}