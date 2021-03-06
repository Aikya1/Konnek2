package com.aikya.konnek2.ui.activities.chats;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.others.BaseFriendsListActivity;
import com.aikya.konnek2.ui.adapters.friends.FriendsAdapter;
import com.aikya.konnek2.utils.listeners.OnMediaPickedListener;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.core.command.Command;
import com.aikya.konnek2.call.core.qb.commands.QBLoadAttachFileCommand;
import com.aikya.konnek2.call.core.qb.commands.chat.QBCreateGroupDialogCommand;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.db.models.Attachment;
import com.aikya.konnek2.call.db.utils.ErrorUtils;
import com.aikya.konnek2.ui.activities.profile.UserProfileActivity;
import com.aikya.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.aikya.konnek2.utils.MediaUtils;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.helpers.MediaPickHelper;
import com.aikya.konnek2.utils.listeners.simple.SimpleOnRecycleItemClickListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.content.model.QBFile;

import com.quickblox.users.model.QBUser;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class CreateGroupDialogActivity extends BaseFriendsListActivity implements OnMediaPickedListener {

    private static final String EXTRA_FRIENDS_LIST = "friends_list";

    @Bind(R.id.photo_imageview)
    RoundedImageView photoImageView;

    @Bind(R.id.group_name_edittext)
    EditText groupNameEditText;

    @Bind(R.id.participants_count_textview)
    TextView participantsCountTextView;


    private QBFile qbFile;
    private List<QMUser> friendsList;
    private MediaPickHelper mediaPickHelper;
    private Uri imageUri;
    private QBUser currentUser;


    public static void start(Context context, List<QMUser> selectedFriendsList) {
        Intent intent = new Intent(context, CreateGroupDialogActivity.class);
        intent.putExtra(EXTRA_FRIENDS_LIST, (Serializable) selectedFriendsList);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_create_group_dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        addActions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpActionBarWithUpButton();
    }

    @Override
    protected FriendsAdapter getFriendsAdapter() {
        return new FriendsAdapter(this, friendsList, false);
    }

    @Override
    protected void initRecyclerView() {

        super.initRecyclerView();
        /*friendsAdapter = getFriendsAdapter();
        friendsAdapter.setFriendListHelper(friendListHelper);
        friendsRecyclerView.setAdapter(friendsAdapter);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));*/
        friendsAdapter.setOnRecycleItemClickListener(new SimpleOnRecycleItemClickListener<QMUser>() {

            @Override
            public void onItemClicked(View view, QMUser entity, int position) {
                UserProfileActivity.start(CreateGroupDialogActivity.this, entity.getId());
            }
        });
    }

    @OnClick(R.id.photo_imageview)
    void selectPhoto(View view) {
        mediaPickHelper.pickAnMedia(this, MediaUtils.IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    @Override
    protected void performDone() {
        groupNameEditText.setError(null);

        if (!TextUtils.isEmpty(groupNameEditText.getText())) {
            checkForCreatingGroupChat();
        } else {
            groupNameEditText.setError(getString(R.string.create_group_empty_group_name));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMediaPicked(int requestCode, Attachment.Type attachmentType, Object attachment) {
        if (Attachment.Type.IMAGE.equals(attachmentType)) {
            canPerformLogout.set(true);
            startCropActivity(MediaUtils.getValidUri((File) attachment, this));
        }

    }

    @Override
    public void onMediaPickError(int requestCode, Exception e) {
        canPerformLogout.set(true);
        ErrorUtils.showError(this, e);
    }

    @Override
    public void onMediaPickClosed(int requestCode) {
        canPerformLogout.set(true);
    }

    private void initFields() {
        title = getString(R.string.create_group_title);
        currentUser = AppSession.getSession().getUser();
        friendsList = (List<QMUser>) getIntent().getExtras().getSerializable(EXTRA_FRIENDS_LIST);
        participantsCountTextView.setText(getString(R.string.create_group_participants, friendsList.size()));
        mediaPickHelper = new MediaPickHelper();
    }

    private void addActions() {
        addAction(QBServiceConsts.LOAD_ATTACH_FILE_SUCCESS_ACTION, new LoadAttachFileSuccessAction());
        addAction(QBServiceConsts.LOAD_ATTACH_FILE_FAIL_ACTION, failAction);

        addAction(QBServiceConsts.CREATE_GROUP_CHAT_SUCCESS_ACTION, new CreateGroupChatSuccessAction());
        addAction(QBServiceConsts.CREATE_GROUP_CHAT_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QBServiceConsts.LOAD_ATTACH_FILE_SUCCESS_ACTION);
        removeAction(QBServiceConsts.LOAD_ATTACH_FILE_FAIL_ACTION);

        removeAction(QBServiceConsts.CREATE_GROUP_CHAT_SUCCESS_ACTION);
        removeAction(QBServiceConsts.CREATE_GROUP_CHAT_FAIL_ACTION);

        updateBroadcastActionList();
    }

    private void checkForCreatingGroupChat() {
        if (isChatInitializedAndUserLoggedIn()) {
            if (friendsList != null && !friendsList.isEmpty()) {
                showProgress();

                if (imageUri != null) {
                    QBLoadAttachFileCommand.start(CreateGroupDialogActivity.this, MediaUtils.getCreatedFileFromUri(imageUri), null);
                } else {
                    createGroupChat();
                }
            }
        } else {
            ToastUtils.longToast(R.string.chat_service_is_initializing);
        }
    }

    private void createGroupChat() {
        String photoUrl = qbFile != null ? qbFile.getPublicUrl() : null;

        QBCreateGroupDialogCommand.start(this, groupNameEditText.getText().toString(), (ArrayList<QMUser>) friendsList,
                photoUrl, currentUser);
    }

    private void startCropActivity(Uri originalUri) {
        canPerformLogout.set(false);

        String extensionOriginalUri = originalUri.getPath().substring(originalUri.getPath().lastIndexOf(".")).toLowerCase();
        imageUri = MediaUtils.getValidUri(new File(getCacheDir(), extensionOriginalUri), this);
        Crop.of(originalUri, imageUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            if (imageUri != null) {
                photoImageView.setImageURI(imageUri);
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            ToastUtils.longToast(Crop.getError(result).getMessage());
        }
        canPerformLogout.set(true);
    }

    private class LoadAttachFileSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            qbFile = (QBFile) bundle.getSerializable(QBServiceConsts.EXTRA_ATTACH_FILE);
            createGroupChat();
        }
    }

    private class CreateGroupChatSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            hideProgress();
            QBChatDialog dialog = (QBChatDialog) bundle.getSerializable(QBServiceConsts.EXTRA_DIALOG);

            if (dialog != null) {
                GroupDialogActivity.start(CreateGroupDialogActivity.this, dialog);
                finish();
            } else {
                ErrorUtils.showError(CreateGroupDialogActivity.this, getString(R.string.dlg_fail_create_groupchat));
            }
        }
    }
}