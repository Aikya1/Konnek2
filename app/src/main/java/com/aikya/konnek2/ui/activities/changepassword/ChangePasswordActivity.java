package com.aikya.konnek2.ui.activities.changepassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;


import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.core.command.Command;
import com.aikya.konnek2.call.core.qb.commands.chat.QBLoginChatCompositeCommand;
import com.aikya.konnek2.call.core.qb.commands.chat.QBLogoutAndDestroyChatCommand;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.core.utils.ConstsCore;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.base.BaseLoggableActivity;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.ValidationUtils;
import com.aikya.konnek2.utils.helpers.ServiceManager;
import com.quickblox.users.model.QBUser;

import butterknife.Bind;
import butterknife.OnTextChanged;
import rx.Subscriber;

public class ChangePasswordActivity extends BaseLoggableActivity {

    @Bind(R.id.old_password_textinputlayout)
    TextInputLayout oldPasswordTextInputLayout;

    @Bind(R.id.old_password_edittext)
    EditText oldPasswordEditText;

    @Bind(R.id.new_password_textinputlayout)
    TextInputLayout newPasswordTextInputLayout;

    @Bind(R.id.new_password_edittext)
    EditText newPasswordEditText;

    private QBUser qbUser;
    private String oldPasswordText;

    public static void start(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_change_password;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setUpActionBarWithUpButton();
        addActions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    @OnTextChanged(R.id.old_password_edittext)
    void onTextChangedOldPassword(CharSequence text) {
        oldPasswordTextInputLayout.setError(null);
    }

    @OnTextChanged(R.id.new_password_edittext)
    void onTextChangedNewPassword(CharSequence text) {
        newPasswordTextInputLayout.setError(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (checkNetworkAvailableWithError()) {
                    changePassword();
                }
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onFailAction(String action) {
        super.onFailAction(action);
        if (QBServiceConsts.LOGOUT_CHAT_FAIL_ACTION.equals(action) || QBServiceConsts.LOGIN_FAIL_ACTION.equals(action)) {
            hideProgress();
            finish();
        }
    }

    private void initFields() {
        title = getString(R.string.change_password_title);
        canPerformLogout.set(false);
        qbUser = AppSession.getSession().getUser();
    }

    private void addActions() {
        addAction(QBServiceConsts.LOGOUT_CHAT_SUCCESS_ACTION, new LogoutChatSuccessAction());
        addAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION, new LoginChatSuccessAction());

        addAction(QBServiceConsts.LOGOUT_CHAT_FAIL_ACTION, failAction);
        addAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    private void removeActions() {

        removeAction(QBServiceConsts.LOGOUT_CHAT_SUCCESS_ACTION);
        removeAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION);

        removeAction(QBServiceConsts.LOGOUT_CHAT_FAIL_ACTION);
        removeAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_FAIL_ACTION);

        updateBroadcastActionList();
    }

    private void changePassword() {
        oldPasswordText = oldPasswordEditText.getText().toString();
        String newPasswordText = newPasswordEditText.getText().toString();
        if (new ValidationUtils(this)
                .isChangePasswordDataValid(oldPasswordTextInputLayout, newPasswordTextInputLayout,
                        oldPasswordText, newPasswordText)) {
            updatePasswords(oldPasswordText, newPasswordText);
            showProgress();
            ServiceManager.getInstance().changePasswordUser(qbUser).subscribe(new Subscriber<QMUser>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    if (e != null) {
                        ToastUtils.longToast(e.getMessage());
                    }

                    updatePasswords(oldPasswordText, oldPasswordText);
                    clearFieldNewPassword();

                    hideProgress();
                }

                @Override
                public void onNext(QMUser qmUser) {
                    saveUserCredentials(qmUser);
                    ToastUtils.longToast(R.string.dlg_password_changed);
                    logoutChat();
                }
            });
        }
    }

    private void updatePasswords(String oldPasswordText, String newPasswordText) {
        qbUser.setOldPassword(oldPasswordText);
        qbUser.setPassword(newPasswordText);
    }

    private void clearFields() {
        oldPasswordEditText.setText(ConstsCore.EMPTY_STRING);
        newPasswordEditText.setText(ConstsCore.EMPTY_STRING);
    }

    private void clearFieldNewPassword() {
        newPasswordEditText.setText(ConstsCore.EMPTY_STRING);
    }

    private void saveUserCredentials(QBUser user) {
        user.setPassword(newPasswordEditText.getText().toString());
        AppSession.getSession().updateUser(user);
    }

    private void logoutChat(){
        showProgress();
        QBLogoutAndDestroyChatCommand.start(this, true);
    }

    private class LogoutChatSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            QBLoginChatCompositeCommand.start(ChangePasswordActivity.this);
        }
    }

    private class LoginChatSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            hideProgress();
            finish();
        }
    }
}