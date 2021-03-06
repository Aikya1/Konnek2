package com.aikya.konnek2.ui.activities.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.core.utils.UserFriendUtils;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.base.BaseLoggableActivity;
import com.aikya.konnek2.utils.helpers.FacebookHelper;
import com.aikya.konnek2.utils.helpers.FirebaseAuthHelper;
import com.quickblox.users.model.QBUser;

import butterknife.Bind;

public class SettingsActivity extends BaseLoggableActivity {

    public static final int REQUEST_CODE_LOGOUT = 300;

  /*  @Bind(R.id.avatar_imageview)
    RoundedImageView avatarImageView;

    @Bind(R.id.full_name_edittext)
    TextView fullNameTextView;

    @Bind(R.id.push_notification_switch)
    SwitchCompat pushNotificationSwitch;

    @Bind(R.id.change_password_view)
    RelativeLayout changePasswordView;*/

    private QMUser user;
    private FacebookHelper facebookHelper;
    private FirebaseAuthHelper firebaseAuthHelper;

    @Bind(R.id.nameTv)
    TextView nameTv;
    @Bind(R.id.statusTv)
    TextView statusTv;

//    public static void startForResult(Fragment fragment) {
//        Intent intent = new Intent(fragment.getActivity(), SettingsActivity.class);
//        fragment.getActivity().startActivityForResult(intent, REQUEST_CODE_LOGOUT);
//    }


    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected int getContentResId() {
        return R.layout.activity_settings;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setUpActionBarWithUpButton();

        addActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIData();
    }

    private void updateUIData() {
        user = UserFriendUtils.createLocalUser(AppSession.getSession().getUser());
        fillUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

 /*   @OnClick(R.id.edit_profile_imagebutton)
    void editProfile() {
        MyProfileActivity.start(this);
    }

    @OnCheckedChanged(R.id.push_notification_switch)
    void enablePushNotification(boolean enable) {
        QBSettings.getInstance().setEnablePushNotification(true);
    }

    @OnClick(R.id.invite_friends_button)
    void inviteFriends() {
        InviteFriendsActivity.start(this);
//        shareTextUrl();
    }

    @OnClick(R.id.give_feedback_button)
    void giveFeedback() {
        FeedbackActivity.start(this);
    }

    @OnClick(R.id.change_password_button)
    void changePassword() {
        ChangePasswordActivity.start(this);
    }

    @OnClick(R.id.logout_button)
    void logout() {
        if (checkNetworkAvailableWithError()) {
            TwoButtonsDialogFragment
                    .show(getSupportFragmentManager(), R.string.dlg_logout, R.string.dlg_confirm,
                            new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    showProgress();

                                    facebookHelper.logout();
                                    firebaseAuthHelper.logout();

                                    ServiceManager.getInstance().logout(new Subscriber<Void>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            ErrorUtils.showError(SettingsActivity.this, e);
                                            hideProgress();
                                        }

                                        @Override
                                        public void onNext(Void aVoid) {
                                            setResult(RESULT_OK);
                                            hideProgress();
                                            finish();
                                        }
                                    });
                                }
                            });
        }

    }*/


/*    @OnClick(R.id.delete_my_account_button)
    void deleteAccount() {
        ToastUtils.longToast(R.string.coming_soon);
    }*/

    private void initFields() {
        title = getString(R.string.settings_title);
        user = UserFriendUtils.createLocalUser(AppSession.getSession().getUser());
        facebookHelper = new FacebookHelper(this);
        firebaseAuthHelper = new FirebaseAuthHelper();
    }

    private void fillUI() {

        QBUser qbUser = AppSession.getSession().getUser();
        if (qbUser.getLogin() == null && TextUtils.isEmpty(qbUser.getLogin())) {
            nameTv.setText(qbUser.getPhone());
        } else {
            nameTv.setText(qbUser.getLogin());
        }

        if (qbUser.getEmail() != null && TextUtils.isEmpty(qbUser.getEmail())) {
            statusTv.setText(qbUser.getEmail());
        } else {
            statusTv.setText(qbUser.getFullName());
        }

//        pushNotificationSwitch.setChecked(QBSettings.getInstance().isEnablePushNotification());
      /*  changePasswordView.setVisibility(
                LoginType.EMAIL.equals(AppSession.getSession().getLoginType()) ? View.VISIBLE : View.GONE);
        fullNameTextView.setText(user.getFullName());*/

//        showUserAvatar();
    }

  /*  private void showUserAvatar() {
        ImageLoader.getInstance().displayImage(
                user.getAvatar(),
                avatarImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }*/

    private void addActions() {
        addAction(QBServiceConsts.LOGOUT_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QBServiceConsts.LOGOUT_FAIL_ACTION);

        updateBroadcastActionList();
    }


}