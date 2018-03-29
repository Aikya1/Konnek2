package com.android.konnek2.ui.fragments.call;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.konnek2.App;
import com.android.konnek2.R;
import com.android.konnek2.call.core.models.StartConversationReason;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.core.utils.UserFriendUtils;
import com.android.konnek2.call.core.utils.call.RingtonePlayer;
import com.android.konnek2.call.services.QMUserCacheImpl;
import com.android.konnek2.call.services.QMUserService;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.base.db.AppCallLogModel;
import com.android.konnek2.ui.activities.call.CallActivity;
import com.android.konnek2.ui.adapters.call.IncomingCallImageAdapter;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.AppPreference;
import com.android.konnek2.utils.helpers.SharedHelper;
import com.android.konnek2.utils.image.ImageLoaderUtils;
import com.android.konnek2.utils.listeners.AppCommon;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.QBChatService;

import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCSessionDescription;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.android.konnek2.utils.AppConstant.PARTICIPANTS_IN_CALL;


public class IncomingCallFragment extends Fragment implements Serializable, View.OnClickListener {

    public static final String TAG = IncomingCallFragment.class.getSimpleName();
    protected SharedHelper appSharedHelper;

    private static final long CLICK_DELAY = TimeUnit.SECONDS.toMillis(2);
    private TextView typeIncCall;
    private TextView callerName;
    private TextView callParticipants;
    private ImageView avatarImageView;
    private ImageButton rejectBtn;
    private ImageButton takeBtn;

    private ArrayList<Integer> opponents;
    private List<QBUser> opponentsFromCall;
    private QBRTCSessionDescription sessionDescription;
    private Vibrator vibrator;
    private QBRTCTypes.QBConferenceType qbConferenceType;
    private View view;
    private long lastClickTime = 0l;
    private RingtonePlayer ringtonePlayer;
    private AppCallLogModel appCallLogModel;
    private ArrayList<AppCallLogModel> appCallLogModelArrayList;
    private String mOpponentName;
    private String mCurrentUserName;
    private String mCurrentUsserId;
    private String callTypes;
    private QMUser opponent;
    private QMUserCacheImpl qmUserCache;
    private List<QMUser> qmUsers;
    private IncomingCallImageAdapter incomingCallImageAdapter;
    private ListView usersList;
    private String opponentCount;
    private String currentqbUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
        currentqbUser = getCurrentUser().getFullName();
        mCurrentUsserId = String.valueOf(getCurrentUser().getId());
        AppPreference.putUserName(currentqbUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {

            opponents = getArguments().getIntegerArrayList(QBServiceConsts.EXTRA_OPPONENTS);
            sessionDescription = (QBRTCSessionDescription) getArguments().getSerializable(QBServiceConsts.EXTRA_SESSION_DESCRIPTION);
            qbConferenceType = (QBRTCTypes.QBConferenceType) getArguments().getSerializable(QBServiceConsts.EXTRA_CONFERENCE_TYPE);
        }

        if (savedInstanceState == null) {
            view = inflater.inflate(R.layout.fragment_income_call, container, false);

            initUI(view);
            setDisplayedTypeCall(qbConferenceType);
            initButtonsListener();
        }
        ringtonePlayer = new RingtonePlayer(getActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        startCallNotification();
    }

    public void onStop() {
        super.onStop();
        stopCallNotification();

    }

    private void initButtonsListener() {

        rejectBtn.setOnClickListener(this);
        takeBtn.setOnClickListener(this);
    }

    private void initUI(View view) {

        appCallLogModel = new AppCallLogModel();
        appCallLogModelArrayList = new ArrayList<AppCallLogModel>();
        typeIncCall = view.findViewById(R.id.type_inc_call);
        callParticipants = view.findViewById(R.id.text_call_participants);

        avatarImageView = view.findViewById(R.id.avatar_imageview);
        usersList = view.findViewById(R.id.list_users);

        callerName = view.findViewById(R.id.calling_to_text_view);
//        callerName.setLines(1);

        rejectBtn = view.findViewById(R.id.rejectBtn);
        takeBtn = view.findViewById(R.id.takeBtn);
        setOpponentAvatarAndName();
        qmUserCache = new QMUserCacheImpl(getActivity());
        opponentsFromCall = new ArrayList<>();
        qmUsers = qmUserCache.getUsersByIDs(opponents);
        opponentsFromCall = new ArrayList<>(qmUsers.size());
        opponentsFromCall.addAll(UserFriendUtils.createQbUserList(qmUsers));
        if (!opponentsFromCall.isEmpty() && opponentsFromCall.size() > 1) {
            opponentCount = String.valueOf(opponentsFromCall.size());
            incomingCallImageAdapter = new IncomingCallImageAdapter(getActivity(), qmUsers);
            usersList.setAdapter(incomingCallImageAdapter);
        }

        getUserName(opponentsFromCall);

    }

    private void setOpponentAvatarAndName() {

        int callerId =  sessionDescription.getCallerID();

        QMUser opponent = QMUserService.getInstance().getUserCache().get((long) callerId);

//        opponent = ((CallActivity) getActivity()).getOpponentAsUserFromDB(sessionDescription.getCallerID());


        Log.d("HistroyImageTest", " InComingCallFragment  setOpponentAvatarAndName:::" + opponent.getId());

        ImageLoader.getInstance().displayImage(opponent.getAvatar(), avatarImageView, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
        callerName.setText(opponent.getFullName());
        mOpponentName = opponent.getFullName();

    }

    public void startCallNotification() {
        ringtonePlayer.play(false);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        long[] vibrationCycle = {0, 1000, 1000};
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationCycle, 1);
        }
    }

    private void stopCallNotification() {
        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private void setDisplayedTypeCall(QBRTCTypes.QBConferenceType conferenceType) {

        try {
            boolean isVideoCall = conferenceType == QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;
            if (isVideoCall) {
                callTypes = AppConstant.CALL_VIDEO;
            } else {
                callTypes = AppConstant.CALL_AUDIO;
            }
            if (!opponentsFromCall.isEmpty() && opponentsFromCall.size() > 1) {

                callParticipants.setText(opponentCount + "  " + PARTICIPANTS_IN_CALL);
                callParticipants.setVisibility(View.VISIBLE);
//                callerName.setText(getUserName(opponentsFromCall));
                if (callTypes == AppConstant.CALL_VIDEO) {

                    typeIncCall.setText(getResources().getString(R.string.incoming_video_conference));
                } else {

                    typeIncCall.setText(getResources().getString(R.string.incoming_audio_conference));
                }
            } else {

                callParticipants.setVisibility(View.GONE);
                callerName.setText(opponent.getFullName());
                typeIncCall.setText(getString(QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO.equals(conferenceType) ?
                        R.string.call_incoming_video_call : R.string.call_incoming_audio_call));
                AppConstant.OPPONENT_ID = String.valueOf(opponent.getId());

            }

        } catch (Exception e) {
            e.getMessage();
        }


    }

    public String getUserName(List<QBUser> userNamesList) {

        String UserNames = null;
        for (int i = 0; i < userNamesList.size(); i++) {
            if (i == 0)
                UserNames = userNamesList.get(i).getFullName();
            else
                UserNames = UserNames + "," + userNamesList.get(i).getFullName();
        }
        return UserNames;
    }

    @Override
    public void onClick(View v) {

        if ((SystemClock.uptimeMillis() - lastClickTime) < CLICK_DELAY) {
            return;
        }
        lastClickTime = SystemClock.uptimeMillis();

        switch (v.getId()) {
            case R.id.rejectBtn:
                reject();
                break;
            case R.id.takeBtn:
                accept();
                break;
            default:
                break;
        }
    }

    private void accept() {

        takeBtn.setClickable(false);
        try {

            stopCallNotification();
            ((CallActivity) getActivity())
                    .checkPermissionsAndStartCall(StartConversationReason.INCOME_CALL_FOR_ACCEPTION);
            Log.d(TAG, "Call is started");
            AppConstant.DATE = AppCommon.currentDate();
            AppConstant.TIME = AppCommon.currentDate();

            appCallLogModel.setCallUserName(currentqbUser);
            appCallLogModel.setUserId(mCurrentUsserId);
            appCallLogModel.setCallOpponentId(AppConstant.OPPONENT_ID);
            appCallLogModel.setCallOpponentName(mOpponentName);
            appCallLogModel.setCallDate(AppCommon.currentDate());
            appCallLogModel.setCallTime(AppCommon.currentTime());
            appCallLogModel.setCallStatus(AppConstant.CALL_STATUS_RECEIVED);
            appCallLogModel.setCallPriority("");
            appCallLogModel.setCallDuration("0");
            appCallLogModel.setCallType(callTypes);
            appCallLogModelArrayList.add(appCallLogModel);
            App.appcallLogTableDAO.saveCallLog(appCallLogModelArrayList);
            ((CallActivity) getActivity()).callDuration(currentqbUser, AppCommon.currentDate(), AppCommon.currentTime());
        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void reject() {
        rejectBtn.setClickable(false);

        try {
            stopCallNotification();
            ((CallActivity) getActivity()).rejectCurrentSession();
            AppConstant.DATE = AppCommon.currentDate();
            AppConstant.TIME = AppCommon.currentDate();
            appCallLogModel.setCallUserName(currentqbUser);
            appCallLogModel.setUserId(mCurrentUsserId);
            appCallLogModel.setCallOpponentName(mOpponentName);
            appCallLogModel.setCallOpponentId(AppConstant.OPPONENT_ID);
            appCallLogModel.setCallDate(AppConstant.DATE);
            appCallLogModel.setCallTime(AppConstant.TIME);
            appCallLogModel.setCallStatus(AppConstant.CALL_STATUS_REJECTED);
            appCallLogModel.setCallPriority("");
            appCallLogModel.setCallDuration("0");
            appCallLogModel.setCallType(callTypes);
            appCallLogModelArrayList.add(appCallLogModel);
            App.appcallLogTableDAO.saveCallLog(appCallLogModelArrayList);
            ((CallActivity) getActivity()).callDuration(currentqbUser, AppCommon.currentDate(), AppCommon.currentTime());
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public QBUser getCurrentUser() {
        return QBChatService.getInstance().getUser();
    }
}