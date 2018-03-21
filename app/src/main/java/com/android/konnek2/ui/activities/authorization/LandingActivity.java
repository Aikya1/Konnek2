package com.android.konnek2.ui.activities.authorization;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.konnek2.R;
import com.android.konnek2.base.activity.AppHomeActivity;
import com.android.konnek2.base.activity.Profile;
import com.android.konnek2.call.core.models.AppSession;
import com.android.konnek2.call.core.models.LoginType;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.ui.dialogs.GdprCustomDialog;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.AppPreference;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Scope;
import com.hbb20.CountryCodePicker;
import com.quickblox.users.model.QBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscriber;

public class LandingActivity extends BaseAuthActivity implements GoogleApiClient.OnConnectionFailedListener, GdprCustomDialog.OnGdprSelected {

    public static final String TAG = LandingActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 007;

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

    String countryCode = "", phNumber = "";
    private GoogleApiClient mGoogleApiClient;


    GdprCustomDialog gdprCustomDialog;

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
        gdprCustomDialog = new GdprCustomDialog(LandingActivity.this);
        gdprCustomDialog.setDialogResult(this);

        facebookLoginBtn.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        callbackManager = CallbackManager.Factory.create();


        setUpGoogle();

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
                                    appSharedHelper.saveSocialId(object.getString("id"));
                                    appSharedHelper.saveUserEmail(object.getString("email"));
                                    appSharedHelper.saveUserGender(object.getString("gender"));
                                    appSharedHelper.saveUserProfilePic(profileUrl);

                                  /*  Intent i = new Intent(LandingActivity.this, GdprActivity.class);
                                    i.putExtra("loginType", AppConstant.LOGIN_TYPE_FACEBOOK);
                                    startActivity(i);*/
                                    gdprCustomDialog.show();

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

    private void setUpGoogle() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


   /* private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
//                        updateUI(false);
                    }
                });
    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String personPhotoUrl = "";
            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String famName = acct.getFamilyName();
            String givenName = acct.getGivenName();
            Account account = acct.getAccount();


            if (acct.getPhotoUrl() != null) {
                personPhotoUrl = acct.getPhotoUrl().toString();
                appSharedHelper.saveUserProfilePic(personPhotoUrl);
            }
            String email = acct.getEmail();


            appSharedHelper.saveLoginType(AppConstant.LOGIN_TYPE_GMAIL);
            appSharedHelper.saveUserFullName(personName);
            appSharedHelper.saveUserEmail(email);
            appSharedHelper.saveSocialId(acct.getId());
            appSharedHelper.saveUserGender("");


      /*      Intent i = new Intent(LandingActivity.this, GdprActivity.class);
            i.putExtra("loginType", AppConstant.LOGIN_TYPE_GMAIL);
            startActivity(i);*/
//            gdprCustomDialog = new GdprCustomDialog(LandingActivity.this);
            gdprCustomDialog.show();


        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        checkRecordPermission();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        /*if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgress();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgress();
                    handleSignInResult(googleSignInResult);
                }
            });
        }*/
    }


    @OnClick(R.id.facebookView)
    public void setUpFbLogin() {
        facebookLoginBtn.callOnClick();
    }


    @OnClick(R.id.btnsignups)
    void phoneNumberConnect(View view) {
        if (checkNetworkAvailableWithError()) {
            if (phoneeval() && checkeval()) {
                showProgress();
                countryCode = countryCodePicker.getDefaultCountryCodeWithPlus();
                phNumber = etphoneno.getText().toString();
                serviceManager.checkIfUserExist(phNumber)
                        .subscribe(checkIfUserExists);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.googleSignInBtn)
    public void setUpGoogleSignIn() {
        signIn();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void loginUser(QBUser user) {
        AppPreference.putQBUserId("" + user.getId());
        AppPreference.putQbUser("" + user);
        startMainActivity(user);
        // send analytics data
    }


    protected void startMainActivity(QBUser user) {
        AppSession.getSession().updateUser(user);
        startMainActivity();
    }

    protected void startMainActivity() {
//        MainActivity.start(BaseAuthActivity.this);
//        startActivity(new Intent(Profile.this, Intro.class));
        appSharedHelper.saveLastOpenActivity(AppHomeActivity.class.getSimpleName());
        AppHomeActivity.start(LandingActivity.this);
        finish();
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
            hideProgress();
            appSharedHelper.saveFirstAuth(true);
            appSharedHelper.saveSavedRememberMe(true);
            appSharedHelper.saveLoginType(AppConstant.LOGIN_TYPE_MANUAL);

            if (userList.size() > 0) {
                //If the user already exists then send user to home page.
                QBUser user = userList.get(0);
                user.setPassword(user.getEmail().toLowerCase() + user.getPhone());

                serviceManager.login(user).subscribe(new Subscriber<QBUser>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "ERROR = " + e.getMessage());
                    }

                    @Override
                    public void onNext(QBUser user) {
                        loginUser(user);
                    }
                });

            } else {
                appSharedHelper.saveUserPhoneNumber(phNumber);
                appSharedHelper.saveCountryCode(countryCode);
                gdprCustomDialog.show();
                Window window = gdprCustomDialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

            }

        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed:" + connectionResult);

    }

    @Override
    public void finish(String result) {
        if (result.equalsIgnoreCase("yes")) {
            loginType = LoginType.FIREBASE_PHONE;
            appSharedHelper.saveIsGdpr("Yes");
            startSocialLogin(phNumber, countryCode);
        } else {
            appSharedHelper.saveIsGdpr("No");
            //No OTP VERIFICATION : GOTO INTRO screen
            //Manual login...
            Intent i = new Intent(LandingActivity.this, Profile.class);
            i.putExtra("phNo", phNumber);
            i.putExtra("countryCode", countryCode);
            startActivity(i);

        }

    }
}