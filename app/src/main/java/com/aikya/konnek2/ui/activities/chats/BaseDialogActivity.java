package com.aikya.konnek2.ui.activities.chats;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aikya.konnek2.R;
import com.aikya.konnek2.base.activity.Profile;
import com.aikya.konnek2.call.core.core.command.Command;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.models.CombinationMessage;
import com.aikya.konnek2.call.core.models.UserCustomData;
import com.aikya.konnek2.call.core.qb.commands.QBLoadAttachFileCommand;
import com.aikya.konnek2.call.core.qb.commands.chat.QBLoadDialogMessagesCommand;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.utils.ChatUtils;
import com.aikya.konnek2.call.core.utils.ConstsCore;
import com.aikya.konnek2.call.core.utils.Utils;
import com.aikya.konnek2.call.db.managers.DialogDataManager;
import com.aikya.konnek2.call.db.managers.DialogNotificationDataManager;
import com.aikya.konnek2.call.db.managers.MessageDataManager;
import com.aikya.konnek2.call.db.managers.base.BaseManager;
import com.aikya.konnek2.call.db.models.Attachment;
import com.aikya.konnek2.call.db.models.Dialog;
import com.aikya.konnek2.call.db.models.DialogNotification;
import com.aikya.konnek2.call.db.models.DialogOccupant;
import com.aikya.konnek2.call.db.models.Message;
import com.aikya.konnek2.call.db.utils.ErrorUtils;
import com.aikya.konnek2.ui.activities.authorization.LandingActivity;
import com.aikya.konnek2.ui.activities.base.BaseLoggableActivity;
import com.aikya.konnek2.ui.activities.location.MapsActivity;
import com.aikya.konnek2.ui.activities.others.PreviewImageActivity;
import com.aikya.konnek2.ui.adapters.chats.BaseChatMessagesAdapter;
import com.aikya.konnek2.ui.fragments.dialogs.base.TwoButtonsDialogFragment;
import com.aikya.konnek2.ui.views.recyclerview.WrapContentLinearLayoutManager;
import com.aikya.konnek2.utils.AppConversionCallaback;
import com.aikya.konnek2.utils.AppSpeechToTextConvertor;
import com.aikya.konnek2.utils.ClipboardUtils;
import com.aikya.konnek2.utils.KeyboardUtils;
import com.aikya.konnek2.utils.MediaUtils;
import com.aikya.konnek2.utils.StringUtils;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.ValidationUtils;
import com.aikya.konnek2.utils.helpers.SystemPermissionHelper;
import com.aikya.konnek2.utils.image.ImageLoaderUtils;
import com.aikya.konnek2.utils.listeners.AppCommon;
import com.aikya.konnek2.utils.listeners.ChatUIHelperListener;
import com.aikya.konnek2.utils.listeners.OnMediaPickedListener;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.db.managers.DataManager;
import com.aikya.konnek2.utils.helpers.MediaPickHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.exception.QBResponseException;

import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBChatAttachClickListener;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBChatMessageLinkClickListener;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBLinkPreviewClickListener;
import com.quickblox.ui.kit.chatmessage.adapter.media.SingleMediaManager;
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.AudioRecorder;
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.exceptions.MediaRecorderException;
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.listeners.QBMediaRecordListener;
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.view.QBRecordAudioButton;
import com.quickblox.ui.kit.chatmessage.adapter.media.video.ui.VideoPlayerActivity;
import com.quickblox.ui.kit.chatmessage.adapter.models.QBLinkPreview;
import com.quickblox.ui.kit.chatmessage.adapter.utils.QBMessageTextClickMovement;
import com.quickblox.users.model.QBUser;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

import static com.aikya.konnek2.utils.AppConstant.nonDupLangList;


public abstract class BaseDialogActivity extends BaseLoggableActivity implements AppConversionCallaback,
        EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        ChatUIHelperListener, OnMediaPickedListener {

    private static final String TAG = BaseDialogActivity.class.getSimpleName();
    private static final int TYPING_DELAY = 1000;
    private static final int DELAY_SCROLLING_LIST = 300;
    private static final int DELAY_SHOWING_SMILE_PANEL = 200;
    private static final int MESSAGES_PAGE_SIZE = ConstsCore.DIALOG_MESSAGES_PER_PAGE;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int POST_DELAY_VIEW = 3000;
    private static final int DURATION_VIBRATE = 100;
    private static final int DURATION_VIBRATE_TEXT = 300;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static Map<String, Locale> displayNames = new HashMap<String, Locale>();


    @Bind(com.aikya.konnek2.R.id.messages_swiperefreshlayout)
    SwipeRefreshLayout messageSwipeRefreshLayout;

    @Bind(com.aikya.konnek2.R.id.messages_recycleview)
    RecyclerView messagesRecyclerView;

    @Bind(com.aikya.konnek2.R.id.message_edittext)
    EditText messageEditText;

    @Bind(com.aikya.konnek2.R.id.attach_button)
    ImageButton attachButton;

    @Bind(com.aikya.konnek2.R.id.chat_record_audio_button)
    QBRecordAudioButton recordAudioButton;

    @Bind(com.aikya.konnek2.R.id.chat_audio_text)
    QBRecordAudioButton audioTotext;

    @Bind(com.aikya.konnek2.R.id.chat_audio_record_chronometer)
    Chronometer recordChronometer;

    @Bind(com.aikya.konnek2.R.id.chat_audio_record_bucket_imageview)
    ImageView bucketView;

    @Bind(com.aikya.konnek2.R.id.layout_chat_audio_container)
    LinearLayout audioLayout;

    @Bind(com.aikya.konnek2.R.id.chat_audio_record_textview)
    TextView audioRecordTextView;

    @Bind(com.aikya.konnek2.R.id.send_button)
    ImageButton sendButton;

    @Bind(com.aikya.konnek2.R.id.smile_panel_imagebutton)
    ImageButton smilePanelImageButton;

    protected QBChatDialog currentChatDialog;
    protected Resources resources;
    protected DataManager dataManager;
    protected BaseChatMessagesAdapter messagesAdapter;
    protected List<CombinationMessage> combinationMessagesList;
    protected MediaPickHelper mediaPickHelper;
    protected SingleMediaManager mediaManager;
    protected AudioRecorder audioRecorder;
    private int count = 0;


    private MessagesTextViewLinkClickListener messagesTextViewLinkClickListener;
    private LocationAttachClickListener locationAttachClickListener;
    private ImageAttachClickListener imageAttachClickListener;
    private LinkPreviewClickListener linkPreviewClickListener;
    private VideoAttachClickListener videoAttachClickListener;
    private QBMediaRecordListenerImpl recordListener;
    private Handler mainThreadHandler;
    private View emojiconsFragment;
    private LoadAttachFileSuccessAction loadAttachFileSuccessAction;
    private LoadDialogMessagesSuccessAction loadDialogMessagesSuccessAction;
    private LoadDialogMessagesFailAction loadDialogMessagesFailAction;
    private Timer typingTimer;
    private boolean isTypingNow;
    private Observer dialogObserver;
    private Observer messageObserver;
    private Observer dialogNotificationObserver;
    private BroadcastReceiver updatingDialogBroadcastReceiver;
    private SystemPermissionHelper systemPermissionHelper;
    private boolean isLoadingMessages;

    private BlockingQueue<Runnable> threadQueue;
    private ThreadPoolExecutor threadPool;
    private boolean needUpdatePosition;
    private Vibrator vibro;
    private int lastVisiblePosition;
    private MessagesScrollListener messagesScrollListener;
    private int recordType = 0;
    public boolean micFlag = true;
    public boolean mTextFlag = true;
    private View alertLayout;
    private String SelectedLanguageLeft;
    private String SelectedLanguageRight;
    private String ConvertedText;
    private String ConvertedTextOnline;
    private ImageView micImage;
    private RadioGroup radioGroupLeft;
    private RadioGroup radioGroupRight;
    private int selectedRadioButtonIDLeft;
    private int selectedRadioButtonIDRight;
    private boolean speakFlag;
    MyCountDownTimer myCountDownTimer;
    private EditText editTextMessage;
    private Animation shakes;

    // text to Speech
    private static final int STT = 1;
    private static int CURRENT_MODE = -1;
    private String mSaraiCommend = "Sarai";
    private boolean transLate;
//    String[] mSaraiCommend = new String[]{"Sarai send", "Sarai", "sir i", "Sarai sindhu"};

    private Timer T;

    public enum TRANSLATOR_TYPE {TEXT_TO_SPEECH, SPEECH_TO_TEXT}

    private AppSpeechToTextConvertor appSpeechToTextConvertor;


    QBUser qbUser;
    UserCustomData userCustomData;

    RadioButton secLangRadButton = null;

    @Override
    protected int getContentResId() {
        return com.aikya.konnek2.R.layout.activity_dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();

        if (currentChatDialog == null) {
            finish();
        }

        setUpActionBarWithUpButton();

        initCustomUI();
        initCustomListeners();
        initThreads();
        initAudioRecorder();

        getCustomData();

        addActions();
        addObservers();
        registerBroadcastReceivers();

        initChatAdapter();

        initMessagesRecyclerView();
        initMediaManager();

        hideSmileLayout();

        if (isNetworkAvailable()) {
            deleteTempMessagesAsync();
        }

    }

    private void getCustomData() {
        qbUser = AppSession.getSession().getUser();
        userCustomData = Utils.customDataToObject(qbUser.getCustomData());
    }

    @OnTextChanged(com.aikya.konnek2.R.id.message_edittext)
    void messageEditTextChanged(CharSequence charSequence) {
        setActionButtonVisibility(charSequence);
        if (currentChatDialog != null) {
            if (!isTypingNow) {
                isTypingNow = true;
                sendTypingStatus();
            }
            checkStopTyping();
        }
    }

    @OnTouch(com.aikya.konnek2.R.id.message_edittext)
    boolean touchMessageEdit() {
        hideSmileLayout();
        scrollMessagesWithDelay();
        return false;
    }

    @OnClick(com.aikya.konnek2.R.id.smile_panel_imagebutton)
    void smilePanelImageButtonClicked() {
        visibleOrHideSmilePanel();
        scrollMessagesWithDelay();
    }

    @OnClick(com.aikya.konnek2.R.id.attach_button)
    void attachFile(View view) {
        mediaPickHelper.pickAnMedia(this, MediaUtils.IMAGE_VIDEO_LOCATION_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        if (isSmilesLayoutShowing()) {
            hideSmileLayout();
        } else {
            returnResult();
            super.onBackPressed();
        }
        vibro.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        addChatMessagesAdapterListeners();
        addAudioRecorderListener();
        checkPermissionSaveFiles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginToChatOrShowError();
        chechSession();
        restoreDefaultCanPerformLogout();
        loadNextPartMessagesAsync();
        checkMessageSendingPossibility();
        resumeMediaPlayer();
    }


    private void chechSession() {
        AppSession session = AppSession.getSession();
        boolean res = session.isLoggedIn();
        Log.d(TAG, "Result = " + res);
    }


    private void checkLoginToChatOrShowError() {
        //problem is here..restore user session
        boolean res = QBChatService.getInstance().isLoggedIn();
        boolean res3 = isChatInitializedAndUserLoggedIn();
        QBSession qbSession = QBSessionManager.getInstance().getActiveSession();
        boolean res2 = qbSession.isValidToken();


       /* if (!QBChatService.getInstance().isLoggedIn()) {
            showSnackbar(R.string.error_disconnected, Snackbar.LENGTH_INDEFINITE);
        }*/


    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSmileLayout();
        checkStartTyping();
        suspendMediaPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeChatMessagesAdapterListeners();
        clearAudioRecorder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTasks();
        closeCurrentChat();
        removeActions();
        deleteObservers();
        unregisterBroadcastReceivers();
    }

    @Override
    protected void onChatReconnected() {
        loadNextPartMessagesAsync();
        initCurrentDialog();
        checkMessageSendingPossibility();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void returnResult() {
        if (getCallingActivity() != null) {

            Intent intent = new Intent();
            intent.putExtra(QBServiceConsts.EXTRA_DIALOG_ID, currentChatDialog.getDialogId());
            intent.putExtra(QBServiceConsts.EXTRA_DIALOG_UPDATE_POSITION, needUpdatePosition);
            setResult(RESULT_OK, intent);
        }
    }

    @Override
    public void onConnectedToService(QBService service) {
        super.onConnectedToService(service);
        onConnectServiceLocally(service);
        initCurrentDialog();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(messageEditText, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(messageEditText);
    }

    @Override
    public void onMediaPicked(int requestCode, Attachment.Type type, Object attachment) {
        canPerformLogout.set(true);
        if (ValidationUtils.validateAttachment(getSupportFragmentManager(), getResources().getStringArray(com.aikya.konnek2.R.array.supported_attachment_types), type, attachment)) {
            startLoadAttachFile(type, attachment, currentChatDialog.getDialogId());
        }
    }

    @Override
    public void onMediaPickClosed(int requestCode) {
        canPerformLogout.set(true);
    }

    @Override
    public void onMediaPickError(int requestCode, Exception e) {
        canPerformLogout.set(true);
        ErrorUtils.logError(e);
    }

    @Override
    public void onScreenResetPossibilityPerformLogout(boolean canPerformLogout) {
        this.canPerformLogout.set(canPerformLogout);
    }

    @Override
    protected void checkShowingConnectionError() {
        if (!isNetworkAvailable()) {
            setActionBarTitle(getString(com.aikya.konnek2.R.string.dlg_internet_connection_is_missing));
            setActionBarIcon(null);
        } else {
            setActionBarTitle(title);
            updateActionBar();
        }
    }

    private void resumeMediaPlayer() {
        if (mediaManager.isMediaPlayerReady()) {
            mediaManager.resumePlay();
        }
    }

    private void suspendMediaPlayer() {
        if (mediaManager.isMediaPlayerReady()) {
            mediaManager.suspendPlay();
        }
    }

    private void deleteTempMessagesAsync() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    deleteTempMessages();
                } catch (Exception e) {
                    e.getMessage();
                }

            }
        });
    }

    private void loadNextPartMessagesAsync() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                loadNextPartMessagesFromDb(false, true);
            }
        });
    }

    private void cancelTasks() {
        threadPool.shutdownNow();
    }

    private void initThreads() {
        threadQueue = new LinkedBlockingQueue<>();
        threadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, threadQueue);
        threadPool.allowCoreThreadTimeOut(true);
    }

    private void initAudioRecorder() {
        audioRecorder = AudioRecorder.newBuilder()
                // Required
                .useInBuildFilePathGenerator(this)
                .setDuration(ConstsCore.MAX_RECORD_DURATION_IN_SEC)
                .build();
    }

    private void restoreDefaultCanPerformLogout() {
        if (!canPerformLogout.get()) {
            canPerformLogout.set(true);
        }
    }

    private void addChatMessagesAdapterListeners() {
        messagesAdapter.setMessageTextViewLinkClickListener(messagesTextViewLinkClickListener, false);
        messagesAdapter.setAttachLocationClickListener(locationAttachClickListener);
        messagesAdapter.setAttachImageClickListener(imageAttachClickListener);
        messagesAdapter.setAttachVideoClickListener(videoAttachClickListener);
        messagesAdapter.setLinkPreviewClickListener(linkPreviewClickListener, false);
    }

    private void addAudioRecorderListener() {
        audioRecorder.setMediaRecordListener(recordListener);
    }

    private void removeChatMessagesAdapterListeners() {
        if (messagesAdapter != null) {
            messagesAdapter.removeMessageTextViewLinkClickListener();
            messagesAdapter.removeLocationImageClickListener(locationAttachClickListener);
            messagesAdapter.removeAttachImageClickListener(imageAttachClickListener);
            messagesAdapter.removeAttachVideoClickListener(videoAttachClickListener);
            messagesAdapter.removeLinkPreviewClickListener();
        }
    }

    private void clearAudioRecorder() {
        if (audioRecorder != null) {
            audioRecorder.removeMediaRecordListener();
            stopRecordIfNeed();
        }
    }

    private void stopRecordIfNeed() {
        if (audioRecorder.isRecording()) {
            Log.d(TAG, "stopRecordIfNeed");
            stopRecord();
        }
    }

    @Override
    protected void performLoginChatSuccessAction(Bundle bundle) {
        super.performLoginChatSuccessAction(bundle);

        checkMessageSendingPossibility();
        startLoadDialogMessages(false);
    }

    protected void initFields() {
        appSpeechToTextConvertor = new AppSpeechToTextConvertor(this);
        shakes = AnimationUtils.loadAnimation(BaseDialogActivity.this, com.aikya.konnek2.R.anim.shake_record_button);
        mainThreadHandler = new Handler(Looper.getMainLooper());
        resources = getResources();
        dataManager = DataManager.getInstance();
        loadAttachFileSuccessAction = new LoadAttachFileSuccessAction();
        loadDialogMessagesSuccessAction = new LoadDialogMessagesSuccessAction();
        loadDialogMessagesFailAction = new LoadDialogMessagesFailAction();
        typingTimer = new Timer();
        dialogObserver = new DialogObserver();
        messageObserver = new MessageObserver();
        dialogNotificationObserver = new DialogNotificationObserver();
        updatingDialogBroadcastReceiver = new UpdatingDialogBroadcastReceiver();
        appSharedHelper.saveNeedToOpenDialog(false);
        mediaPickHelper = new MediaPickHelper();
        systemPermissionHelper = new SystemPermissionHelper(this);
        messagesTextViewLinkClickListener = new MessagesTextViewLinkClickListener();
        locationAttachClickListener = new LocationAttachClickListener();
        imageAttachClickListener = new ImageAttachClickListener();
        linkPreviewClickListener = new LinkPreviewClickListener();
        videoAttachClickListener = new VideoAttachClickListener();
        recordListener = new QBMediaRecordListenerImpl();
        currentChatDialog = (QBChatDialog) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_DIALOG);
        combinationMessagesList = new ArrayList<>();
        vibro = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        messagesScrollListener = new MessagesScrollListener();
    }

    private void initCustomUI() {
        emojiconsFragment = _findViewById(com.aikya.konnek2.R.id.emojicon_fragment);
    }

    private void initCustomListeners() {
        messageSwipeRefreshLayout.setOnRefreshListener(new RefreshLayoutListener());
        recordAudioButton.setRecordTouchListener(new RecordTouchListener());
//        audioTotext.setOnLongClickListener(new VoiceToTextListener());
        audioTotext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRecordPermission())
                    voiceToText();
            }
        });
        recordChronometer.setOnChronometerTickListener(new ChronometerTickListener());

    }

    protected abstract void initChatAdapter();

    protected void initMessagesRecyclerView() {
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //use deprecated listener for support old devices
        messagesRecyclerView.setOnScrollListener(messagesScrollListener);
    }

    protected void initMediaManager() {
        mediaManager = messagesAdapter.getMediaManagerInstance();
    }

    protected void addActions() {
        addAction(QBServiceConsts.LOAD_ATTACH_FILE_SUCCESS_ACTION, loadAttachFileSuccessAction);
        addAction(QBServiceConsts.LOAD_ATTACH_FILE_FAIL_ACTION, failAction);

        addAction(QBServiceConsts.LOAD_DIALOG_MESSAGES_SUCCESS_ACTION, loadDialogMessagesSuccessAction);
        addAction(QBServiceConsts.LOAD_DIALOG_MESSAGES_FAIL_ACTION, loadDialogMessagesFailAction);

        updateBroadcastActionList();
    }

    protected void removeActions() {
        removeAction(QBServiceConsts.LOAD_ATTACH_FILE_SUCCESS_ACTION);
        removeAction(QBServiceConsts.LOAD_ATTACH_FILE_FAIL_ACTION);

        removeAction(QBServiceConsts.LOAD_DIALOG_MESSAGES_SUCCESS_ACTION);
        removeAction(QBServiceConsts.LOAD_DIALOG_MESSAGES_FAIL_ACTION);

//        removeAction(QBServiceConsts.ACCEPT_FRIEND_SUCCESS_ACTION);
//        removeAction(QBServiceConsts.ACCEPT_FRIEND_FAIL_ACTION);

//        removeAction(QBServiceConsts.REJECT_FRIEND_SUCCESS_ACTION);
//        removeAction(QBServiceConsts.REJECT_FRIEND_FAIL_ACTION);

        updateBroadcastActionList();
    }

    protected void registerBroadcastReceivers() {
        localBroadcastManager.registerReceiver(updatingDialogBroadcastReceiver,
                new IntentFilter(QBServiceConsts.UPDATE_DIALOG));
    }

    protected void unregisterBroadcastReceivers() {
        localBroadcastManager.unregisterReceiver(updatingDialogBroadcastReceiver);
    }

    protected void addObservers() {
        dataManager.getQBChatDialogDataManager().addObserver(dialogObserver);
        dataManager.getMessageDataManager().addObserver(messageObserver);
        dataManager.getDialogNotificationDataManager().addObserver(dialogNotificationObserver);
    }

    protected void deleteObservers() {
        dataManager.getQBChatDialogDataManager().deleteObserver(dialogObserver);
        dataManager.getMessageDataManager().deleteObserver(messageObserver);
        dataManager.getDialogNotificationDataManager().deleteObserver(dialogNotificationObserver);
    }

    protected void updateData() {
        updateActionBar();
        updateMessagesList();
    }

    protected void onConnectServiceLocally() {
    }

    protected void deleteTempMessages() {
        List<DialogOccupant> dialogOccupantsList = dataManager.getDialogOccupantDataManager()
                .getDialogOccupantsListByDialogId(currentChatDialog.getDialogId());
        List<Long> dialogOccupantsIdsList = ChatUtils.getIdsFromDialogOccupantsList(dialogOccupantsList);
        dataManager.getMessageDataManager().deleteTempMessages(dialogOccupantsIdsList);
    }

    private void hideSmileLayout() {
        emojiconsFragment.setVisibility(View.GONE);
        setSmilePanelIcon(com.aikya.konnek2.R.drawable.ic_app_chat_smile);
    }

    private void showSmileLayout() {
        mainThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                emojiconsFragment.setVisibility(View.VISIBLE);
                setSmilePanelIcon(com.aikya.konnek2.R.drawable.ic_keyboard_dark);
            }
        }, DELAY_SHOWING_SMILE_PANEL);
    }

    protected void checkActionBarLogo(String url, int resId) {
        if (!TextUtils.isEmpty(url)) {
            loadActionBarLogo(url);
        } else {
            setDefaultActionBarLogo(resId);
        }
    }

    private void checkPermissionSaveFiles() {

        boolean permissionSaveFileWasRequested = appSharedHelper.isPermissionsSaveFileWasRequested();
        if (!systemPermissionHelper.isAllPermissionsGrantedForSaveFile() && !permissionSaveFileWasRequested) {
            systemPermissionHelper.requestPermissionsForSaveFile();
            appSharedHelper.savePermissionsSaveFileWasRequested(true);
        }
    }

    protected void loadActionBarLogo(String logoUrl) {
        ImageLoader.getInstance().loadImage(logoUrl, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS,
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedBitmap) {
                        setActionBarIcon(
                                MediaUtils.getRoundIconDrawable(BaseDialogActivity.this, loadedBitmap));
                    }
                });
    }

    protected void setDefaultActionBarLogo(int drawableResId) {
        setActionBarIcon(MediaUtils
                .getRoundIconDrawable(this, BitmapFactory.decodeResource(getResources(), drawableResId)));
    }

    protected void startLoadAttachFile(final Attachment.Type type, final Object attachment, final String dialogId) {
        TwoButtonsDialogFragment.show(getSupportFragmentManager(), getString(com.aikya.konnek2.R.string.dialog_confirm_sending_attach,
                StringUtils.getAttachmentNameByType(this, type)), false,
                new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        switch (type) {
                            case LOCATION:
                                sendMessageWithAttachment(dialogId, Attachment.Type.LOCATION, attachment, null);
                                break;
                            case IMAGE:
                            case AUDIO:
                            case VIDEO:
                                showProgress();
                                QBLoadAttachFileCommand.start(BaseDialogActivity.this, (File) attachment, dialogId);
                                break;
                        }
                    }
                });
    }

    protected void startLoadDialogMessages(QBChatDialog chatDialog, long lastDateLoad, boolean isLoadOldMessages) {
        isLoadingMessages = true;
        QBLoadDialogMessagesCommand.start(this, chatDialog, lastDateLoad, isLoadOldMessages);
    }

    private void setActionButtonVisibility(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence) || TextUtils.isEmpty(charSequence.toString().trim())) {
            sendButton.setVisibility(View.GONE);
            attachButton.setVisibility(View.VISIBLE);
            recordAudioButton.setVisibility(View.VISIBLE);
        } else {
            sendButton.setVisibility(View.VISIBLE);
            attachButton.setVisibility(View.GONE);
            recordAudioButton.setVisibility(View.GONE);
        }
    }

    private void checkStopTyping() {
        typingTimer.cancel();
        typingTimer = new Timer();
        typingTimer.schedule(new TypingTimerTask(), TYPING_DELAY);
    }

    private void checkStartTyping() {
        if (currentChatDialog != null) {
            if (isTypingNow) {
                isTypingNow = false;
                sendTypingStatus();
            }
        }
    }

    private void sendTypingStatus() {
        chatHelper.sendTypingStatusToServer(isTypingNow);
    }

    private void setSmilePanelIcon(int resourceId) {
        smilePanelImageButton.setImageResource(resourceId);
    }

    private boolean isSmilesLayoutShowing() {
        return emojiconsFragment.getVisibility() == View.VISIBLE;
    }

    protected boolean needScrollBottom(int countAddedMessages) {
        return lastVisiblePosition + countAddedMessages >= messagesAdapter.getItemCount() - 1;
    }

    protected void scrollMessagesToBottom(int countAddedMessages) {
        if (needScrollBottom(countAddedMessages)) {
            scrollMessagesWithDelay();
        }
    }

    private void scrollMessagesWithDelay() {
        mainThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
            }
        }, DELAY_SCROLLING_LIST);
    }

    protected void sendMessage() {
        boolean error = false;
        try {
            chatHelper.sendChatMessage(messageEditText.getText().toString());
        } catch (QBResponseException e) {
            ErrorUtils.showError(this, e);
            error = true;
        } catch (IllegalStateException e) {
            ErrorUtils.showError(this, this.getString(
                    com.aikya.konnek2.R.string.dlg_not_joined_room));
            error = true;
        } catch (Exception e) {
            ErrorUtils.showError(this, e);
            error = true;
        }

        if (!error) {
            messageEditText.setText(ConstsCore.EMPTY_STRING);
        }
    }

    protected void startLoadDialogMessages(final boolean isLoadOldMessages) {
        if (currentChatDialog == null || isLoadingMessages) {
            return;
        }
        showActionBarProgress();
        long messageDateSent = getMessageDateForLoadByCurrentList(isLoadOldMessages);
        startLoadDialogMessages(currentChatDialog, messageDateSent, isLoadOldMessages);

    }

    protected long getMessageDateForLoad(boolean isLoadOldMessages) {
        long messageDateSent;
        List<DialogOccupant> dialogOccupantsList = dataManager.getDialogOccupantDataManager().getDialogOccupantsListByDialogId(currentChatDialog.getDialogId());
        List<Long> dialogOccupantsIdsList = ChatUtils.getIdsFromDialogOccupantsList(dialogOccupantsList);

        Message message;
        DialogNotification dialogNotification;

        message = dataManager.getMessageDataManager().getMessageByDialogId(isLoadOldMessages, dialogOccupantsIdsList);
        dialogNotification = dataManager.getDialogNotificationDataManager().getDialogNotificationByDialogId(isLoadOldMessages, dialogOccupantsIdsList);
        messageDateSent = ChatUtils.getDialogMessageCreatedDate(!isLoadOldMessages, message, dialogNotification);

        return messageDateSent;
    }

    protected long getMessageDateForLoadByCurrentList(boolean isLoadOld) {
        if (combinationMessagesList.size() == 0) {
            return 0;
        }

        return !isLoadOld
                ? combinationMessagesList.get(combinationMessagesList.size() - 1).getCreatedDate()
                : combinationMessagesList.get(0).getCreatedDate();
    }

    protected void initCurrentDialog() {
        if (service != null) {
            Log.v(TAG, "chatHelper = " + chatHelper + "\n dialog = " + currentChatDialog);
            if (chatHelper != null && currentChatDialog != null) {
                try {
                    chatHelper.initCurrentChatDialog(currentChatDialog, generateBundleToInitDialog());
                } catch (QBResponseException e) {
                    ErrorUtils.showError(this, e.getMessage());
                    finish();
                }
            }
        } else {
            Log.v(TAG, "service == null");
        }
    }

    private void closeCurrentChat() {
        if (chatHelper != null && currentChatDialog != null) {
            chatHelper.closeChat(currentChatDialog, generateBundleToInitDialog());
        }
        currentChatDialog = null;
    }

    protected List<CombinationMessage> buildLimitedCombinationMessagesListByDate(long createDate, boolean moreDate, long limit) {
        if (currentChatDialog == null || currentChatDialog.getDialogId() == null) {
            return new ArrayList<>();
        }

        Log.d(TAG, "selection parameters: createDate = " + createDate + " moreDate = " + moreDate + " limit = " + limit);

        String currentDialogId = currentChatDialog.getDialogId();
        List<Message> messagesList = dataManager.getMessageDataManager()
                .getMessagesByDialogIdAndDate(currentDialogId, createDate, moreDate, limit);
        Log.d(TAG, " messagesList.size() = " + messagesList.size());

        List<DialogNotification> dialogNotificationsList = dataManager.getDialogNotificationDataManager()
                .getDialogNotificationsByDialogIdAndDate(currentDialogId, createDate, moreDate, limit);
        Log.d(TAG, " dialogNotificationsList.size() = " + dialogNotificationsList.size());
        List<CombinationMessage> combinationMessages = ChatUtils.createLimitedCombinationMessagesList(messagesList, dialogNotificationsList, (int) limit);
        return combinationMessages;
    }

    protected void loadNextPartMessagesFromDb(final boolean isLoadOld, final boolean needUpdateAdapter) {
        long messageDate = getMessageDateForLoadByCurrentList(isLoadOld);

        final List<CombinationMessage> requestedMessages = buildLimitedCombinationMessagesListByDate(
                messageDate, !isLoadOld, ConstsCore.DIALOG_MESSAGES_PER_PAGE);

        if (isLoadOld) {
            combinationMessagesList.addAll(0, requestedMessages);
        } else {
            combinationMessagesList.addAll(requestedMessages);
        }

        if (needUpdateAdapter) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateMessagesAdapter(isLoadOld, requestedMessages.size());
                    startLoadDialogMessages(false);
                }
            });
        }
    }

    private void updateMessagesAdapter(boolean isLoadOld, int partSize) {
        messagesAdapter.setList(combinationMessagesList, !isLoadOld);

        if (isLoadOld) {
            messagesAdapter.notifyItemRangeInserted(0, partSize);
        } else {
            scrollMessagesToBottom(partSize);
        }
    }

    private void visibleOrHideSmilePanel() {
        if (isSmilesLayoutShowing()) {
            hideSmileLayout();
            KeyboardUtils.showKeyboard(BaseDialogActivity.this);
        } else {
            KeyboardUtils.hideKeyboard(BaseDialogActivity.this);
            showSmileLayout();
        }
    }

    protected void checkMessageSendingPossibility(boolean enable) {
        messageEditText.setEnabled(enable);
        smilePanelImageButton.setEnabled(enable);
        attachButton.setEnabled(enable);
        recordAudioButton.setEnabled(enable);
    }

    private void replaceMessageInCurrentList(CombinationMessage combinationMessage) {
        if (combinationMessagesList.contains(combinationMessage)) {
            int positionOldMessage = combinationMessagesList.indexOf(combinationMessage);
            combinationMessagesList.set(positionOldMessage, combinationMessage);
        }
    }

    private void updateMessageItemInAdapter(CombinationMessage combinationMessage) {
        replaceMessageInCurrentList(combinationMessage);
        if (combinationMessagesList.contains(combinationMessage)) {
            messagesAdapter.notifyItemChanged(combinationMessagesList.indexOf(combinationMessage));
        } else {
            addMessageItemToAdapter(combinationMessage);
        }
    }

    private void addMessageItemToAdapter(CombinationMessage combinationMessage) {
        combinationMessagesList.add(combinationMessage);
        messagesAdapter.setList(combinationMessagesList, true);
        scrollMessagesToBottom(1);
    }

    private void afterLoadingMessagesActions() {
        messageSwipeRefreshLayout.setRefreshing(false);
        hideActionBarProgress();
        isLoadingMessages = false;
    }

    private boolean checkRecordPermission() {
        if (systemPermissionHelper.isAllAudioRecordPermissionGranted()) {
            return true;
        } else {
            systemPermissionHelper.requestAllPermissionForAudioRecord();
            return false;
        }
    }

    public void startRecord() {

        setMessageAttachViewsEnable(false);
        setRecorderViewsVisibility(View.VISIBLE);
        audioViewVisibility(View.VISIBLE);
        vibrate(DURATION_VIBRATE);
        audioRecorder.startRecord();
        startChronometer();
    }

    public void stopRecordByClick() {

        setMessageAttachViewsEnable(true);
        vibrate(DURATION_VIBRATE);
        stopRecord();
    }

    private void stopRecord() {
        audioViewVisibility(View.INVISIBLE);
        stopChronometer();
        audioRecorder.stopRecord();
    }

    public void cancelRecord() {
        stopChronometer();
        setMessageAttachViewsEnable(true);
        setRecorderViewsVisibility(View.INVISIBLE);
        animateCanceling();
        vibrate(DURATION_VIBRATE);
        audioViewPostDelayVisibility(View.INVISIBLE);
        audioRecorder.cancelRecord();
    }

    private void startChronometer() {
        recordChronometer.setBase(SystemClock.elapsedRealtime());
        recordChronometer.start();
    }

    private void stopChronometer() {
        recordChronometer.stop();
    }

    private void setRecorderViewsVisibility(int visibility) {
        audioRecordTextView.setVisibility(visibility);
        recordChronometer.setVisibility(visibility);
    }

    private void setMessageAttachViewsEnable(boolean enable) {
        messageEditText.setFocusableInTouchMode(enable);
        messageEditText.setFocusable(enable);
        smilePanelImageButton.setEnabled(enable);
        attachButton.setEnabled(enable);
    }

    private void audioViewPostDelayVisibility(final int visibility) {
        audioLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                audioViewVisibility(visibility);
            }
        }, POST_DELAY_VIEW);
    }

    private void audioViewVisibility(int visibility) {
        audioLayout.setVisibility(visibility);
    }

    private void vibrate(int duration) {
        vibro.vibrate(duration);
    }

    private void animateCanceling() {
        Animation shake = AnimationUtils.loadAnimation(this, com.aikya.konnek2.R.anim.shake);
        bucketView.startAnimation(shake);
    }

    protected abstract void updateActionBar();

    protected abstract void onConnectServiceLocally(QBService service);

    protected abstract Bundle generateBundleToInitDialog();

    protected abstract void updateMessagesList();

    protected void sendMessageWithAttachment(String dialogId, Attachment.Type attachmentType, Object attachmentObject, String localPath) {
        if (!dialogId.equals(currentChatDialog.getDialogId())) {
            return;
        }
        try {
            chatHelper.sendMessageWithAttachment(attachmentType, attachmentObject, localPath);
        } catch (QBResponseException exc) {
            ErrorUtils.showError(this, exc);
        }
    }

    protected abstract void checkMessageSendingPossibility();

    @Override
    public void onConversionSuccess(String result) {


        if (result.length() > 0) {
            ConvertedText = "";
            ConvertedText = result;
            editTextMessage.setText(ConvertedText);
            shakes.cancel();
            shakes.reset();
            myCountDownTimer.cancel();
            if (transLate && ConvertedText.length() > 0) {

                if (SelectedLanguageLeft.contains("en_US")) {
                    SelectedLanguageLeft = "en";
                } else {
                    if (SelectedLanguageLeft.contains("es_ES")) {
                        SelectedLanguageLeft = "es";
                    } else {
                        SelectedLanguageLeft = "hi";
                    }
                }
                String textToBeTranslated = ConvertedText;
                String languagePair = SelectedLanguageLeft + "-" + SelectedLanguageRight;
                Translate(textToBeTranslated, languagePair);
            } else {

                ConvertedText = result.toString();
                transLate = false;
            }

        } else {

            editTextMessage.setText(ConvertedText);
            ConvertedText = "";
        }
    }

    public void Translate(String textToBeTranslated, String languagePair) {
        TranslatorBackgroundTask translatorBackgroundTask = new TranslatorBackgroundTask(BaseDialogActivity.this);
        translatorBackgroundTask.execute(textToBeTranslated, languagePair);
    }

    @Override
    public void onConversionCompletion() {
        Log.d("VOICETEXTCON", "OnConversionCompletion");
    }

    @Override
    public void onConversionErrorOccured(String errorMessage) {

        Log.d("VOICETEXTCON", "onConversionErrorOccured  errorMessage " + errorMessage);

    }

    private class MessageObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            Log.i(TAG, "==== MessageObserver  'update' ====");
            if (data != null) {
                Bundle observableData = (Bundle) data;
                int action = observableData.getInt(MessageDataManager.EXTRA_ACTION);
                Message message = (Message) observableData.getSerializable(MessageDataManager.EXTRA_OBJECT);

                if (message != null && message.getDialogOccupant() != null && message.getDialogOccupant().getDialog() != null
                        && currentChatDialog.getDialogId().equals(message.getDialogOccupant().getDialog().getDialogId())) {
                    needUpdatePosition = true;
                    CombinationMessage combinationMessage = new CombinationMessage(message);
                    if (action == MessageDataManager.UPDATE_ACTION) {
                        Log.d(TAG, "updated message = " + message);
                        updateMessageItemInAdapter(combinationMessage);
                    } else if (action == MessageDataManager.CREATE_ACTION) {
                        Log.d(TAG, "created message = " + message);
                        addMessageItemToAdapter(combinationMessage);
                    }
                }

                if (action == MessageDataManager.DELETE_BY_ID_ACTION
                        || action == MessageDataManager.DELETE_ACTION) {
                    combinationMessagesList.clear();
                    loadNextPartMessagesFromDb(false, true);
                }
            }
        }
    }

    private class DialogNotificationObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            Log.i(TAG, "==== DialogNotificationObserver  'update' ====");
            if (data != null) {
                Bundle observableData = (Bundle) data;
                int action = observableData.getInt(DialogNotificationDataManager.EXTRA_ACTION);
                DialogNotification dialogNotification = (DialogNotification) observableData.getSerializable(DialogNotificationDataManager.EXTRA_OBJECT);

                Log.i(TAG, "==== DialogNotificationObserver  'update' ====observableData= " + observableData);
                if (dialogNotification != null && dialogNotification.getDialogOccupant().getDialog().getDialogId().equals(currentChatDialog.getDialogId())) {
                    needUpdatePosition = true;
                    CombinationMessage combinationMessage = new CombinationMessage(dialogNotification);
                    if (action == DialogNotificationDataManager.UPDATE_ACTION) {
                        Log.d(TAG, "updated dialogNotification = " + dialogNotification);
                        updateMessageItemInAdapter(combinationMessage);
                    } else if (action == DialogNotificationDataManager.CREATE_ACTION) {
                        Log.d(TAG, "created dialogNotification = " + dialogNotification);
                        addMessageItemToAdapter(combinationMessage);
                        if (currentChatDialog != null && QBDialogType.PRIVATE.equals(currentChatDialog.getType())) {
                            updateMessagesList();
                            messagesAdapter.notifyDataSetChanged();
                            scrollMessagesToBottom(1);
                        }
                    }

                    if (action == DialogNotificationDataManager.DELETE_BY_ID_ACTION
                            || action == DialogNotificationDataManager.DELETE_ACTION) {
                        combinationMessagesList.clear();
                        loadNextPartMessagesFromDb(false, true);
                    }
                }
            }
        }
    }

    private class DialogObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            Log.i(TAG, "==== DialogObserver  'update' ====");
            if (data != null) {
                String observerKey = ((Bundle) data).getString(DialogDataManager.EXTRA_OBSERVE_KEY);
                if (observerKey.equals(dataManager.getQBChatDialogDataManager().getObserverKey())) {
                    Dialog dialog = (Dialog) ((Bundle) data).getSerializable(BaseManager.EXTRA_OBJECT);
                    currentChatDialog = dataManager.getQBChatDialogDataManager().getByDialogId(currentChatDialog.getDialogId());
                    if (currentChatDialog != null) {
                        if (dialog != null && dialog.getDialogId().equals(currentChatDialog.getDialogId())) {
                            // need init current dialog after getting from DB
                            initCurrentDialog();
                            updateActionBar();
                        }
                    } else {
                        finish();
                    }
                }
            }
        }
    }

    private class TypingTimerTask extends TimerTask {

        @Override
        public void run() {
            isTypingNow = false;
            sendTypingStatus();
        }
    }

    public class LoadAttachFileSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            QBFile file = (QBFile) bundle.getSerializable(QBServiceConsts.EXTRA_ATTACH_FILE);
            String dialogId = (String) bundle.getSerializable(QBServiceConsts.EXTRA_DIALOG_ID);
            String localPath = (String) bundle.getSerializable(QBServiceConsts.EXTRA_FILE_PATH);

            sendMessageWithAttachment(dialogId, StringUtils.getAttachmentTypeByFileName(file.getName()), file, localPath);
            hideProgress();
        }
    }

    public class LoadDialogMessagesSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            final int totalEntries = bundle.getInt(QBServiceConsts.EXTRA_TOTAL_ENTRIES,
                    ConstsCore.ZERO_INT_VALUE);
            final long lastMessageDate = bundle.getLong(QBServiceConsts.EXTRA_LAST_DATE_LOAD_MESSAGES,
                    ConstsCore.ZERO_INT_VALUE);
            final boolean isLoadedOldMessages = bundle.getBoolean(QBServiceConsts.EXTRA_IS_LOAD_OLD_MESSAGES);

            String dialogId = bundle.getString(QBServiceConsts.EXTRA_DIALOG_ID);

            Log.d("BaseDialogActivity", "Loading messages finished" + " totalEntries = " + totalEntries
                    + " lastMessageDate = " + lastMessageDate
                    + " isLoadedOldMessages = " + isLoadedOldMessages
                    + " dialogId = " + dialogId);

            if (messagesAdapter != null
                    && totalEntries != ConstsCore.ZERO_INT_VALUE
                    && dialogId.equals(currentChatDialog.getDialogId())) {

                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPartMessagesFromDb(isLoadedOldMessages, false);
                        mainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateMessagesAdapter(isLoadedOldMessages, totalEntries);
                                if (currentChatDialog != null && QBDialogType.PRIVATE.equals(currentChatDialog.getType())) {
                                    updateMessagesList();
                                }
                                afterLoadingMessagesActions();
                            }
                        });

                    }
                });

            } else {
                afterLoadingMessagesActions();
            }
        }
    }

    public class LoadDialogMessagesFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            afterLoadingMessagesActions();
        }
    }

    private class UpdatingDialogBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(QBServiceConsts.UPDATE_DIALOG)) {
                updateData();
            }
        }
    }

    private class RefreshLayoutListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            if (!isNetworkAvailable()) {
                messageSwipeRefreshLayout.setRefreshing(false);
                return;
            }

            if (!isLoadingMessages) {
                if (combinationMessagesList.size() == 0) {
                    startLoadDialogMessages(true);
                    return;
                }

                long oldestMessageInDb = getMessageDateForLoad(true);
                long oldestMessageInCurrentList = combinationMessagesList.get(0).getCreatedDate();

                if ((oldestMessageInCurrentList - oldestMessageInDb) > 0 && oldestMessageInDb != 0) {
                    loadNextPartMessagesFromDb(true, true);
                    messageSwipeRefreshLayout.setRefreshing(false);
                } else {
                    startLoadDialogMessages(true);
                }
            }
        }
    }

    protected class MessagesTextViewLinkClickListener implements QBChatMessageLinkClickListener {

        @Override
        public void onLinkClicked(String linkText, QBMessageTextClickMovement.QBLinkType qbLinkType, int position) {

            if (!QBMessageTextClickMovement.QBLinkType.NONE.equals(qbLinkType)) {
                canPerformLogout.set(false);
            }
        }

        @Override
        public void onLongClick(String text, int positionInAdapter) {

        }

    }

    protected class LocationAttachClickListener implements QBChatAttachClickListener {

        @Override
        public void onLinkClicked(QBAttachment qbAttachment, int position) {
            MapsActivity.startMapForResult(BaseDialogActivity.this, qbAttachment.getData());
        }
    }

    protected class ImageAttachClickListener implements QBChatAttachClickListener {

        @Override
        public void onLinkClicked(QBAttachment qbAttachment, int position) {
            PreviewImageActivity.start(BaseDialogActivity.this, QBFile.getPrivateUrlForUID(qbAttachment.getId()));
        }
    }

    protected class VideoAttachClickListener implements QBChatAttachClickListener {

        @Override
        public void onLinkClicked(QBAttachment qbAttachment, int position) {
            canPerformLogout.set(false);
            VideoPlayerActivity.start(BaseDialogActivity.this, Uri.parse(QBFile.getPrivateUrlForUID(qbAttachment.getId())));
        }
    }

    protected class RecordTouchListener implements QBRecordAudioButton.RecordTouchEventListener {

        @Override
        public void onStartClick(View v) {

            if (checkRecordPermission()) {
                startRecord();
            }

        }

        @Override
        public void onCancelClick(View v) {

            cancelRecord();
        }

        @Override
        public void onStopClick(View v) {
            stopRecordByClick();
        }
    }

    protected class VoiceToTextListener implements QBRecordAudioButton.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {

            vibrate(DURATION_VIBRATE_TEXT);
            appSpeechToTextConvertor.initialize("Hello There", "en_US", BaseDialogActivity.this);
            Animation shake = AnimationUtils.loadAnimation(BaseDialogActivity.this, com.aikya.konnek2.R.anim.shake_record_button);
            audioTotext.startAnimation(shake);

            return false;
        }
    }


    protected class ChronometerTickListener implements Chronometer.OnChronometerTickListener {
        private long elapsedSecond;

        @Override
        public void onChronometerTick(Chronometer chronometer) {
            setChronometerAppropriateColor();
        }

        private void setChronometerAppropriateColor() {
            elapsedSecond = TimeUnit.MILLISECONDS.toSeconds(SystemClock.elapsedRealtime() - recordChronometer.getBase());
            if (isStartSecond()) {
                setChronometerBaseColor();
            }
            if (isAlarmSecond()) {
                setChronometerAlarmColor();
            }
        }

        private boolean isStartSecond() {
            return elapsedSecond == 0;
        }

        private boolean isAlarmSecond() {
            return elapsedSecond == ConstsCore.CHRONOMETER_ALARM_SECOND;
        }

        private void setChronometerAlarmColor() {
            recordChronometer.setTextColor(ContextCompat.getColor(BaseDialogActivity.this, android.R.color.holo_red_light));
        }

        private void setChronometerBaseColor() {
            recordChronometer.setTextColor(ContextCompat.getColor(BaseDialogActivity.this, android.R.color.white));
        }
    }

    private class QBMediaRecordListenerImpl implements QBMediaRecordListener {

        @Override
        public void onMediaRecorded(File file) {
            audioViewVisibility(View.INVISIBLE);
            if (ValidationUtils.validateAttachment(getSupportFragmentManager(), getResources().getStringArray(com.aikya.konnek2.R.array.supported_attachment_types), Attachment.Type.AUDIO, file)) {
                startLoadAttachFile(Attachment.Type.AUDIO, file, currentChatDialog.getDialogId());
            } else {
                audioRecordErrorAnimate();
            }
        }

        @Override
        public void onMediaRecordError(MediaRecorderException e) {
            audioRecorder.releaseMediaRecorder();
            audioRecordErrorAnimate();
        }

        @Override
        public void onMediaRecordClosed() {
        }

        private void audioRecordErrorAnimate() {
            Animation shake = AnimationUtils.loadAnimation(BaseDialogActivity.this, com.aikya.konnek2.R.anim.shake_record_button);
            recordAudioButton.startAnimation(shake);
        }
    }

    protected class LinkPreviewClickListener implements QBLinkPreviewClickListener {

        @Override
        public void onLinkPreviewClicked(String link, QBLinkPreview linkPreview, int position) {

        }

        @Override
        public void onLinkPreviewLongClicked(String link, QBLinkPreview linkPreview, int position) {
            ClipboardUtils.copySimpleTextToClipboard(BaseDialogActivity.this, link);
            ToastUtils.longToast(com.aikya.konnek2.R.string.link_was_copied);
            Log.v(TAG, linkPreview.toString());
        }
    }

    protected class MessagesScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            WrapContentLinearLayoutManager layoutManager1 = (WrapContentLinearLayoutManager) recyclerView.getLayoutManager();
            lastVisiblePosition = layoutManager1.findLastVisibleItemPosition();
        }
    }


    public void voiceToText() {

        LayoutInflater inflater = getLayoutInflater();
        alertLayout = inflater.inflate(com.aikya.konnek2.R.layout.item_voice_to_text_1, null);
        micImage = alertLayout.findViewById(com.aikya.konnek2.R.id.image_mic_text);
        editTextMessage = alertLayout.findViewById(com.aikya.konnek2.R.id.edit_text_message);
        radioGroupLeft = alertLayout.findViewById(com.aikya.konnek2.R.id.radio_group_left);

        String prefLanguage = userCustomData.getPrefLanguage();


        final TextView headerView = alertLayout.findViewById(com.aikya.konnek2.R.id.text_header);
        final TextView tapToSpeak = alertLayout.findViewById(com.aikya.konnek2.R.id.txt_speak);

        final Button buttonOk = alertLayout.findViewById(com.aikya.konnek2.R.id.text_ok);
        final Button buttonCancel = alertLayout.findViewById(com.aikya.konnek2.R.id.text_cancel);
        secLangRadButton = alertLayout.findViewById(R.id.button_hindi);
        final ImageButton changeLangButton = alertLayout.findViewById(R.id.changeLangBtn);
        final Spinner langSpinner = alertLayout.findViewById(R.id.languageSpinner);

        if (!prefLanguage.equalsIgnoreCase("English")) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
              /*  locale = getLocale(prefLanguage);
                getLocaleString(prefLanguage);*/

                secLangRadButton.setVisibility(View.VISIBLE);
                changeLangButton.setVisibility(View.VISIBLE);

                secLangRadButton.setText(prefLanguage);
                secLangRadButton.setTag(getLanguageCode(prefLanguage));

            }

        }

//        radioGroupRight = alertLayout.findViewById(R.id.radio_group_right);
//        final CheckBox lanuageTranslate = alertLayout.findViewById(R.id.check_box_translate);

        selectedRadioButtonIDLeft = 0;
        selectedRadioButtonIDRight = 0;
        SelectedLanguageLeft = "en_US";
        myCountDownTimer = new MyCountDownTimer(60000, 1000);

        /*lanuageTranslate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    transLate = true;
                    radioGroupRight.setVisibility(View.VISIBLE);
                } else {
                    transLate = false;
                    radioGroupRight.setVisibility(View.GONE);
                }
            }
        });*/


        changeLangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpLanguageSpinner(langSpinner);
            }
        });

        radioGroupLeft.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {


                selectedRadioButtonIDLeft = radioGroupLeft.getCheckedRadioButtonId();
                if (selectedRadioButtonIDLeft != -1) {

                    RadioButton selectedRadioButton = alertLayout.findViewById(selectedRadioButtonIDLeft);
                    String selectedRadioButtonTag = String.valueOf(selectedRadioButton.getTag());

                    SelectedLanguageLeft = selectedRadioButtonTag;
                } else {


                }
            }
        });

        /*radioGroupRight.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                selectedRadioButtonIDRight = radioGroupRight.getCheckedRadioButtonId();
                if (selectedRadioButtonIDRight != -1) {

                    RadioButton selectedRadioButton = (RadioButton) alertLayout.findViewById(selectedRadioButtonIDRight);
                    String selectedRadioButtonTag = String.valueOf(selectedRadioButton.getTag());
                    SelectedLanguageRight = selectedRadioButtonTag;

                } else {

                }

            }
        });*/

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                /*if (count == 3)
                    lanuageTranslate.setVisibility(View.VISIBLE);*/
            }
        });
        micImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!speakFlag) {
                    myCountDownTimer.start();
                    speakFlag = true;
                    tapToSpeak.setText("Tap Mic to Stop");
                } else {
                    tapToSpeak.setText("Tap Mic to Start");
                    speakFlag = false;
                    myCountDownTimer.cancel();
                }
            }
        });

//        micImage.setOnLongClickListener(new VoiceToTextListener());
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setTitle("Info");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (transLate) {
                        if (ConvertedTextOnline.length() > 0) {
                            messageEditText.setText(ConvertedTextOnline);
                        } else {
                            messageEditText.setText("");

                        }
                        transLate = false;
                    } else {
                        if (editTextMessage.length() > 0 && editTextMessage != null) {

                            chatHelper.sendChatMessage(editTextMessage.getText().toString());
                        } else {

                        }

                    }
                    count = 0;
                    ConvertedText = "";
                    myCountDownTimer.cancel();
                    speakFlag = false;
                    vibro.cancel();
                    dialog.dismiss();
                } catch (Exception e) {
                    e.getMessage();
                }

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                myCountDownTimer.cancel();
                messageEditText.setText("");
                speakFlag = false;
                vibro.cancel();
                dialog.dismiss();
            }
        });

        dialog.show();


        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                userCustomData.setPrefLanguage(nonDupLangList.get(position));
                secLangRadButton.setChecked(false);
                secLangRadButton.setText(nonDupLangList.get(position));
                secLangRadButton.setTag(getLanguageCode(nonDupLangList.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void setUpLanguageSpinner(Spinner langSpinner) {
//        langSpinner.setVisibility(View.VISIBLE);
        LandingActivity.addLanguagesToList();
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.custom_spinner_dropdown, nonDupLangList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (langSpinner != null) {
            langSpinner.setAdapter(adapter);
            String myString = Locale.getDefault().getDisplayLanguage(); //the value you want the position for
            ArrayAdapter myAdap = (ArrayAdapter) langSpinner.getAdapter(); //cast to an ArrayAdapter
            int spinnerPosition = myAdap.getPosition(myString);
            //set the default according to value
//            langSpinner.setSelection(spinnerPosition);
            langSpinner.performClick();
//                selectedLanguage = spin1.getSelectedItem().toString();
        }

    }

    private String getLanguageCode(String prefLanguage) {

        String result = "";
        /*hi-IN-translit”, “bn_IN”, “pu”, “te”, “mr_IN”, “ta_IN”, “ur_IN”, “gu_IN”, “kn”, “ml_IN*/
        switch (prefLanguage) {
            case "Hindi":
                result = "hi_IN";
                break;
            case "Bengali":
                result = "bn_IN";
                break;
            case "Punjabi":
                result = "pa_IN";
                break;
            case "Telugu":
                result = "te_IN";
                break;
            case "Marathi":
                result = "mr_IN";
                break;
            case "Tamil":
                result = "ta_IN";
                break;
            case "Urdu":
                result = "ur_IN";
                break;
            case "Gujarati":
                result = "gu_IN";
                break;
            case "Kannada":
                result = "kn_IN";
                break;
            case "Malayalam":
                result = "ml_IN";
                break;
            case "Chinese":
                result = "zh_";
                break;
            case "Spanish":
                result = "es";
                break;
            case "Arabic":
                result = "ks_IN";
                break;
            case "Portugese":
                result = "pt_PT";
                break;
            case "Russian":
                result = "ru_RU";
                break;
            case "Japanese":
                result = "ja_JP";
                break;
            case "German":
                result = "de";
                break;
            case "French":
                result = "fr";
                break;
            case "Malay":
                result = "ta_IN";
                break;
            case "Italian":
                result = "it_IT";
                break;
            case "Polish":
                result = "pl_PL";
                break;

            case "Ukrainian":
                result = "uk_UA";
                break;
            case "Romanian":
                result = "ro_RO";
                break;
            case "Swahii":
                result = "sw";
                break;
        }

        return result;
    }


    public class TranslatorBackgroundTask extends AsyncTask<String, Void, String> {


        //Declare Context
        Context ctx;

        //Set Context
        public TranslatorBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            //String variables
            String textToBeTranslated = params[0];
            String languagePair = params[1];

            String jsonString;

            try {

                //Set up the translation call URL
                String yandexKey = "trnsl.1.1.20171212T094728Z.cba9269f4436ebb5.7988ba1559f3f1b5836983f2c722635ac20570eb";
                String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                        + "&text=" + textToBeTranslated + "&lang=" + languagePair;
                URL yandexTranslateURL = new URL(yandexUrl);


                //Set Http Conncection, Input Stream, and Buffered Reader
                HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
                InputStream inputStream = httpJsonConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                //Set string builder and insert retrieved JSON result into it
                StringBuilder jsonStringBuilder = new StringBuilder();
                while ((jsonString = bufferedReader.readLine()) != null) {
                    jsonStringBuilder.append(jsonString + "\n");
                }

                //Close and disconnect
                bufferedReader.close();
                inputStream.close();
                httpJsonConnection.disconnect();

                //Making result human readable
                String resultString = jsonStringBuilder.toString().trim();
                //Getting the characters between [ and ]
                resultString = resultString.substring(resultString.indexOf('[') + 1);
                resultString = resultString.substring(0, resultString.indexOf("]"));
                //Getting the characters between " and "
                resultString = resultString.substring(resultString.indexOf("\"") + 1);
                resultString = resultString.substring(0, resultString.indexOf("\""));

                ConvertedTextOnline = resultString;

                return jsonStringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    public class MyCountDownTimer extends CountDownTimer {

        int counts = 0;

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {


            if (speakFlag && counts == 0) {
                selectedRadioButtonIDLeft = 1;
                if (selectedRadioButtonIDLeft == 0 && selectedRadioButtonIDRight == 0) {

                    AppCommon.displayToast("Please Select any One Language");
                } else {
                    counts++;
                    appSpeechToTextConvertor.initialize("Hello There", String.valueOf(SelectedLanguageLeft), BaseDialogActivity.this);
                    micImage.startAnimation(shakes);

                }
            } else {
                micImage.startAnimation(shakes);

            }

        }

        @Override
        public void onFinish() {
            finish();
        }
    }
}