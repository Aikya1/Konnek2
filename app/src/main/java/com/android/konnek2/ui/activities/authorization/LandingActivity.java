package com.android.konnek2.ui.activities.authorization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.konnek2.R;
import com.android.konnek2.base.activity.GdprActivity;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.ToastUtils;
import com.android.konnek2.utils.helpers.ServiceManager;
import com.android.konnek2.utils.helpers.SystemPermissionHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observer;

public class LandingActivity extends BaseAuthActivity {

    public static final String TAG = LandingActivity.class.getSimpleName();

    private SystemPermissionHelper systemPermissionHelper;
    CheckBox check1, check2;
    EditText etphoneno;

    private String phoneNumber;
    ServiceManager serviceManager;

    @Bind(R.id.country_isd_list)
    CountryCodePicker countryCodePicker;

    @Bind(R.id.login_button)
    LoginButton facebookLoginBtn;

    @Bind(R.id.facebookView)
    Button facebookManualBtn;

    String countryCode, phNumber;


/*    @Bind(R.id.login_button)
    LoginButton fbLoginBtn;*/


    public static void start(Context context) {
        Intent intent = new Intent(context, LandingActivity.class);
        context.startActivity(intent);
    }

    public static void start(Context context, Intent intent) {
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_landing;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        systemPermissionHelper = new SystemPermissionHelper(this);
        check1 = findViewById(R.id.checkBox1);
        check2 = findViewById(R.id.checkBox2);
        etphoneno = findViewById(R.id.etphoneno);
        serviceManager = ServiceManager.getInstance();

        facebookLoginBtn.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        callbackManager = CallbackManager.Factory.create();


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "FB Success");

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    //send user to gdpr screen
                                    String profileUrl = "http://graph.facebook.com/" + object.getString("id") + "/picture?type=large";
                                    appSharedHelper.saveLoginType(AppConstant.LOGIN_TYPE_FACEBOOK);
                                    appSharedHelper.saveUserFullName(object.getString("name"));
                                    appSharedHelper.saveFacebookId(object.getString("id"));
                                    appSharedHelper.saveUserEmail(object.getString("email"));
                                    appSharedHelper.saveUserGender(object.getString("gender"));
                                    appSharedHelper.saveUserProfilePic(profileUrl);

                                    Intent i = new Intent(LandingActivity.this, GdprActivity.class);
                                    i.putExtra("loginType", AppConstant.LOGIN_TYPE_FACEBOOK);
                                    startActivity(i);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "FB Error == " + error.getMessage());

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        checkRecordPermission();
    }


    @OnClick(R.id.facebookView)
    public void setUpFbLogin() {

        facebookLoginBtn.callOnClick();
    }


    @OnClick(R.id.btnsignups)
    void phoneNumberConnect(View view) {
        if (checkNetworkAvailableWithError()) {
            /*loginType = LoginType.FIREBASE_PHONE;
            startSocialLogin();*/
            if (phoneeval() && checkeval()) {
                //  loginType = LoginType.FIREBASE_PHONE;
                //    startSocialLogin();
                //Go to gdpr page
               /* serviceManager.checkIfUserExist(etphoneno.getText().toString())
                        .subscribe(checkIfUserExists);*/

                showProgress();
                countryCode = countryCodePicker.getDefaultCountryCodeWithPlus();
                String ccWithoutPlus = countryCodePicker.getDefaultCountryCode();
                phNumber = etphoneno.getText().toString();

                serviceManager.checkIfUserExist(ccWithoutPlus + phNumber)
                        .subscribe(checkIfUserExists);
/*
                appSharedHelper.saveUserPhoneNumber(phNo);
                appSharedHelper.saveCountryCode(cc);


                Intent i = new Intent(LandingActivity.this, GdprActivity.class);
                i.putExtra("phNo", etphoneno.getText().toString());
                i.putExtra("countryCode", cc);
                startActivity(i);*/
            }

        }
    }


    private boolean checkeval() {
        if (!check1.isChecked()) {
            ToastUtils.shortToast("Terms and conditions is Mandatory");
            return false;
        } else if (!check2.isChecked()) {
            ToastUtils.shortToast("Privacy Policy is Mandatory");
            return false;
        }
        return true;
    }

    private boolean phoneeval() {
        phoneNumber = etphoneno.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.shortToast("Phone number is Mandatory");
            return false;
        }
        return true;
    }

    @Override
    public void checkShowingConnectionError() {
        // nothing. Toolbar is missing.
    }

    private void startSignUpActivity() {
        SignUpActivity.start(LandingActivity.this);
        finish();
    }


    private boolean checkRecordPermission() {

        if (systemPermissionHelper.isContactPermissionGranted()) {
            return true;
        } else {
            systemPermissionHelper.requestPermissionsReadContacts();
            return false;
        }
    }


    private Observer<List<QMUser>> checkIfUserExists = new Observer<List<QMUser>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "User List == " + e.getMessage());
        }

        @Override
        public void onNext(List<QMUser> userList) {

            Log.d(TAG, "User List == " + userList.toString());

            if (userList.size() > 0) {
                //If the user already exists then send user to home page.

                String ph = userList.get(0).getPhone();
                String pass = userList.get(0).getPassword();
                Log.d(TAG, "Pass = " + pass);


            } else {
                appSharedHelper.saveUserPhoneNumber(phNumber);
                appSharedHelper.saveCountryCode(countryCode);
                Intent i = new Intent(LandingActivity.this, GdprActivity.class);
                i.putExtra("loginType", AppConstant.LOGIN_TYPE_MANUAL);
                i.putExtra("phNo", etphoneno.getText().toString());
                i.putExtra("countryCode", countryCode);
                startActivity(i);
            }

        }
    };
}