package com.aikya.konnek2.ui.activities.authorization;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.aikya.konnek2.R;
import com.aikya.konnek2.base.activity.AppHomeActivity;
import com.aikya.konnek2.base.activity.Profile;
import com.aikya.konnek2.call.core.models.LoginType;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.dialogs.GdprCustomDialog;
import com.aikya.konnek2.utils.AppConstant;
import com.aikya.konnek2.utils.AppPreference;
import com.aikya.konnek2.utils.EmailPhoneValidationUtils;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.helpers.ServiceManager;
import com.aikya.konnek2.utils.helpers.SystemPermissionHelper;
import com.facebook.AccessToken;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscriber;

import static com.aikya.konnek2.utils.AppConstant.nonDupLangList;

public class LandingActivity extends BaseAuthActivity implements GoogleApiClient.OnConnectionFailedListener, GdprCustomDialog.OnGdprSelected {

    /*RAJEEV COMMIT*/


    public static final String TAG = LandingActivity.class.getSimpleName();

    Map<String, Locale> langMap = new HashMap<>();
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

    @Bind(R.id.textView2)
    TextView policyTv;

    String countryCode = "", phNumber = "";
    private GoogleApiClient mGoogleApiClient;

    GdprCustomDialog gdprCustomDialog;
    String userLoginType = "";



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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        addLanguagesToList();
        Collections.sort(nonDupLangList);


        //        generateFacebookKeyHash();


        /*for (Locale locale : Locale.getAvailableLocales()) {

            Log.d(TAG,"ISO3Language = "+locale.getISO3Language());
            Log.d(TAG,"ISO3Language = "+locale.getDisplayScript());
            Log.d(TAG,"ISO3Language = "+locale.getDisplayName());
            Log.d(TAG,"ISO3Language = "+locale.getDisplayVariant());



            languageList.add(locale.getDisplayLanguage());
        }

        Iterator<String> dupIter = languageList.iterator();
        while (dupIter.hasNext()) {
            String dupWord = dupIter.next();
            if (nonDupLangList.contains(dupWord)) {
                dupIter.remove();
            } else {
                nonDupLangList.add(dupWord);
            }
        }*/


        //<string name="terms_and_ser_2">I have read Konnek2 <a href="https://www.google.com/">
        //Privacy Policy</a> &amp; agree\nKonnek2 to save my personal data. </string>

        policyTv.setText(Html.fromHtml("I have read Konnek2 " +
                "<a href='http://konnek2.aikya.info/Privacy_Policy.pdf'>Privacy Policy</a> &amp; agree\nKonnek2 to save my personal data."));
        policyTv.setClickable(true);
        policyTv.setMovementMethod(LinkMovementMethod.getInstance());

        facebookLoginBtn.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        callbackManager = CallbackManager.Factory.create();
        setUpGoogle();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    //send user to gdpr screen
                                    String profileUrl = "http://graph.facebook.com/" + object.getString("id") + "/picture?type=large";
                                    appSharedHelper.saveLoginType(AppConstant.LOGIN_TYPE_FACEBOOK);
                                    appSharedHelper.saveUserFullName(object.getString("name"));
                                    appSharedHelper.saveSocialId(object.getString("id"));
                                    appSharedHelper.saveUserEmail(object.getString("email"));
                                    appSharedHelper.saveUserGender(object.getString("gender"));
                                    appSharedHelper.saveIsGdpr("");
                                    appSharedHelper.saveUserProfilePic(profileUrl);
                                    appSharedHelper.saveCountryCode("");
                                    userLoginType = AppConstant.LOGIN_TYPE_FACEBOOK;

                                    showProgress();
                                    serviceManager.checkIfUserExistsEmail(object.getString("email"))
                                            .subscribe(checkIfUserEmailExists);
                                    //                                    gdprCustomDialog.show();

                                    //                                    startIntroActivity(AppConstant.LOGIN_TYPE_FACEBOOK);

                                    //                                    gdprCustomDialog.show();
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

    public static void addLanguagesToList() {
        nonDupLangList.clear();
        nonDupLangList.add("Hindi");
        nonDupLangList.add("Bengali");
        nonDupLangList.add("Punjabi");
        nonDupLangList.add("Telugu");
        nonDupLangList.add("Marathi");
        nonDupLangList.add("Tamil");
        nonDupLangList.add("Urdu");
        nonDupLangList.add("Gujarati");
        nonDupLangList.add("Kannada");
        nonDupLangList.add("Malayalam");

        nonDupLangList.add("Chinese");
        nonDupLangList.add("Spanish");
        nonDupLangList.add("English");
        nonDupLangList.add("Arabic");
        nonDupLangList.add("Portuguese");
        nonDupLangList.add("Russian");
        nonDupLangList.add("Japanese");
        nonDupLangList.add("German");
        nonDupLangList.add("French");
        nonDupLangList.add("Malay");
        nonDupLangList.add("Italian");
        nonDupLangList.add("Polish");
        nonDupLangList.add("Ukrainian");
        nonDupLangList.add("Romanian");
        nonDupLangList.add("Swahili");
    }

    private void generateFacebookKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.aikya.konnek2",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
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

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
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
            String personName = acct.getDisplayName();

            if (acct.getPhotoUrl() != null) {
                personPhotoUrl = acct.getPhotoUrl().toString();
                appSharedHelper.saveUserProfilePic(personPhotoUrl);
            }
            String email = acct.getEmail();

            showProgress();
            serviceManager.checkIfUserExistsEmail(acct.getEmail())
                    .subscribe(checkIfUserEmailExists);
            appSharedHelper.saveLoginType(AppConstant.LOGIN_TYPE_GMAIL);
            appSharedHelper.saveUserFullName(personName);
            appSharedHelper.saveUserEmail(email);
            appSharedHelper.saveSocialId(acct.getId());
            appSharedHelper.saveUserGender("");
            appSharedHelper.saveIsGdpr("");
            appSharedHelper.saveCountryCode("");
            userLoginType = AppConstant.LOGIN_TYPE_GMAIL;

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
        if (checkNetworkAvailableWithError()) {
            if (checkeval()) {
                if (!isLoggedIn()) {
                    facebookLoginBtn.callOnClick();
                } else {
                    userLoginType = AppConstant.LOGIN_TYPE_FACEBOOK;
                    gdprCustomDialog.show();
                }
            }
        }

    }


    @OnClick(R.id.btnsignups)
    void phoneNumberConnect(View view) {
        if (checkNetworkAvailableWithError()) {
            if (phoneeval() && checkeval()) {
                showProgress();
                countryCode = countryCodePicker.getSelectedCountryCode();
                phNumber = etphoneno.getText().toString();
                serviceManager.checkIfUserExist(countryCode+phNumber)
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
        if (checkNetworkAvailableWithError()) {
            if (checkeval()) {
                signIn();
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
        if (!EmailPhoneValidationUtils.isValidPhone(phoneNumber) || TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.shortToast("Phone No is empty/not valid");
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
    }


    protected void startMainActivity(QBUser user) {
        startMainActivity();
    }

    protected void startMainActivity() {
        appSharedHelper.saveLastOpenActivity(AppHomeActivity.class.getSimpleName());
        AppHomeActivity.start(LandingActivity.this);
        finish();
    }

    private Observer<List<QMUser>> checkIfUserExists = new Observer<List<QMUser>>() {
        @Override
        public void onCompleted() {
            hideProgress();
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "User List == " + e.getMessage());
        }

        @Override
        public void onNext(List<QMUser> userList) {

            appSharedHelper.saveFirstAuth(true);
            appSharedHelper.saveSavedRememberMe(true);
            userLoginType = AppConstant.LOGIN_TYPE_MANUAL;
            appSharedHelper.saveLoginType(AppConstant.LOGIN_TYPE_MANUAL);

            if (userList.size() > 0) {

                sendUserToHomePage(userList.get(0));


            } else {
                appSharedHelper.saveUserPhoneNumber(phNumber);
                appSharedHelper.saveCountryCode(countryCode);
                gdprCustomDialog.show();
                Window window = gdprCustomDialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }

        }
    };

    private void sendUserToHomePage(QMUser qmUser) {
        //If the user already exists then send user to home page.
        QBUser user = qmUser;
        user.setPassword(AppConstant.USER_PASSWORD);

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
    }


    private Observer<QMUser> checkIfUserEmailExists = new Observer<QMUser>() {

        @Override
        public void onCompleted() {
            hideProgress();
        }

        @Override
        public void onError(Throwable e) {
            hideProgress();
            gdprCustomDialog.show();
        }

        @Override
        public void onNext(QMUser qmUser) {
            if (qmUser != null) {
                sendUserToHomePage(qmUser);
            }
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    public void startProfileActivity() {
        Intent profileIntent = new Intent(LandingActivity.this, Profile.class);
        profileIntent.putExtra("phNo", phNumber);
        profileIntent.putExtra("countryCode", countryCode);
        startActivity(profileIntent);

    }

    @Override
    public void finish(String result) {
        if (userLoginType.equals(AppConstant.LOGIN_TYPE_FACEBOOK) ||
                userLoginType.equals(AppConstant.LOGIN_TYPE_GMAIL) && result.equalsIgnoreCase("yes")) {
            appSharedHelper.saveIsGdpr(result);
            appSharedHelper.saveUserPhoneNumber("");
            startProfileActivity();

        } else if (userLoginType.equals(AppConstant.LOGIN_TYPE_MANUAL) && result.equalsIgnoreCase("yes")) {
            appSharedHelper.saveIsGdpr(result);
            loginType = LoginType.FIREBASE_PHONE;
            startSocialLogin(phNumber, countryCode);
        } else {
            appSharedHelper.saveIsGdpr(result);
            startProfileActivity();
        }




            /*if (result.equalsIgnoreCase("yes")) {


                loginType = LoginType.FIREBASE_PHONE;
                appSharedHelper.saveIsGdpr("Yes");
                startSocialLogin(phNumber, countryCode);
            } else {
                appSharedHelper.saveIsGdpr("No");
                //No OTP VERIFICATION : GOTO INTRO screen
                //Manual login...
                Intent i = new Intent(LandingActivity.this, Intro.class);
                startActivity(i);
            }*/
    }
}