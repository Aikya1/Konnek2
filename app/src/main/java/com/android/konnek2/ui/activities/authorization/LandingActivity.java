package com.android.konnek2.ui.activities.authorization;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.konnek2.R;
import com.android.konnek2.base.Model_base.Profile;
import com.android.konnek2.base.activity.Intro;
import com.android.konnek2.base.db.DataHolder;
import com.android.konnek2.call.core.models.LoginType;
import com.android.konnek2.call.core.models.UserCustomData;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.QBCustomObjectsUtils;
import com.android.konnek2.utils.ToastUtils;
import com.android.konnek2.utils.ValidationUtils;
import com.android.konnek2.utils.helpers.SystemPermissionHelper;
import com.android.konnek2.utils.listeners.AppCommon;
import com.firebase.ui.auth.ui.phone.CountryListSpinner;
import com.google.firebase.auth.PhoneAuthProvider;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.Performer;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.extensions.RxJavaPerformProcessor;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LandingActivity extends BaseAuthActivity {

   /* @Bind(R.id.app_version_textview)
    TextView appVersionTextView;

    @Bind(R.id.phone_number_connect_button)
    Button phoneNumberConnectButton;
   */

    CountryListSpinner countryListSpinner;

    private SystemPermissionHelper systemPermissionHelper;


    Button signup;
    EditText phoneno;
    String MobilePattern = "[0-9]{10}";
    String validno = "";
    CheckBox check1, check2;


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
        // initVersionName();

        countryListSpinner = findViewById(R.id.country_isd_list);


        phoneno = findViewById(R.id.etphoneno);
        signup = findViewById(R.id.btnsignups);

        check1 = findViewById(R.id.checkBox1);
        check2 = findViewById(R.id.checkBox2);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String ff = countryListSpinner.getText().toString();
                // ToastUtils.longToast(ff);
                // Log.d("SS",ff);
                if (checkNetworkAvailableWithError()) {
                    if (validatephone() && validatewcheckbox()) {

                        checkRecordPermission();
                        fillfields();


                        String user = "9741628429";
                        String pwd = "1234567890";
                        QBUser qbUser = new QBUser(user, pwd);

                        UserCustomData userCustomData = new UserCustomData();

                        userCustomData.setAge(24);

                        qbUser.setPassword(pwd);
                        qbUser.setLogin(user);
                        qbUser.setCustomDataAsObject(userCustomData);


                        QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                            @Override
                            public void onSuccess(QBUser qbUser, Bundle bundle) {
                                Log.d("QB", qbUser.getCustomData());
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Log.d("QB", e.getMessage());
                            }
                        });


                       /* QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                            @Override
                            public void onSuccess(QBUser qbUser, Bundle bundle) {
                                ToastUtils.shortToast("Done");
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                ToastUtils.shortToast("Not   Done"+e.getMessage());
                            }
                        });
*/


                        //  QBCustomObject qbCustomObject = QBCustomObjectsUtils.createCustomObject(validno);
                        //  QBCustomObject object = new QBCustomObject("LuveyUserData");

                        // put fields
                        //   object.putString("name", "Star Wars");





                        /*Performer<QBCustomObject> performer = QBCustomObjects.createObject(qbCustomObject);
                        Observable<QBCustomObject> observable =
                                performer.convertTo(RxJavaPerformProcessor.INSTANCE);

                        observable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<QBCustomObject>() {
                            @Override
                            public void onCompleted() {

                                ToastUtils.shortToast("Done");

                            }

                            @Override
                            public void onError(Throwable e) {
                              ToastUtils.longToast("Not done "+e);
                            }

                            @Override
                            public void onNext(QBCustomObject qbCustomObject) {
                               // DataHolder.getInstance().addMovieToMap(new Movie(qbCustomObject));
                            }
                        });
*/



                        /*QBCustomObject qbCustomObject = QBCustomObjectsUtils.createCustomObject(validno);
                        Performer<QBCustomObject> performer = QBCustomObjects.createObject(qbCustomObject);
                        Observable<QBCustomObject> observable =
                                performer.convertTo(RxJavaPerformProcessor.INSTANCE);

                        observable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<QBCustomObject>()
                        {

                            @Override
                            public void onCompleted() {
                                ToastUtils.shortToast("done");
                                finish();
                            }

                            @Override
                            public void onError(Throwable e)
                            {
                                ToastUtils.shortToast("not done"+e);
                                finish();
                            }

                            @Override
                            public void onNext(QBCustomObject qbCustomObject) {

                            }
                        });
*/
                        /*QBCustomObjects.createObject(qbCustomObject, new QBEntityCallback<QBCustomObject>(){

                            @Override
                            public void onSuccess(QBCustomObject qbCustomObject, Bundle bundle) {

                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });
*/
                        //  Intent i=new Intent(LandingActivity.this, Intro.class);
                        // startActivity(i);


                    } else {
                        ToastUtils.shortToast("Terms and Conditions not checked");
                    }


                }


            }
        });
    }


    private void fillfields() {

    }

    private boolean validatewcheckbox() {
        if (check1.isChecked() && check2.isChecked()) {
            return true;

        } else if (!check1.isChecked() || !check2.isChecked()) {
            return false;
        }

        return false;
    }

    private boolean validatephone() {
        if (phoneno.getText().toString().matches(MobilePattern)) {
            validno = signup.getText().toString();
            return true;
        } else if (!phoneno.getText().toString().matches(MobilePattern)) {
            phoneno.setError("Phone No is not valid");
            return false;
        }

        return false;
    }


    // @OnClick(R.id.login_button)
    void login(View view) {
//        LoginActivity.start(LandingActivity.this);
//        finish();
        AppCommon.displayToast(AppConstant.TOAST_MESSAGE);

    }


     /*termsone.setText(Html.fromHtml("I have read and agree to the " +
             "<a href='id.web.freelancer.example.TCActivity://Kode'>TERMS AND CONDITIONS</a>"));
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

*/
   /* @OnClick(R.id.phone_number_connect_button)
    void phoneNumberConnect(View view)
    {
        if (checkNetworkAvailableWithError())
        {
            checkRecordPermission();
            loginType = LoginType.FIREBASE_PHONE;
            startSocialLogin();
        }
    }
*/


    /*String phone="9741628429";

    startPhoneNumberVerification(phone)
    {

    }
*/

    @Override
    public void checkShowingConnectionError() {
        // nothing. Toolbar is missing.
    }

    private void startSignUpActivity() {
        SignUpActivity.start(LandingActivity.this);
        finish();
    }

   /* private void initVersionName() {
        appVersionTextView.setText(R.string.about_version);
    }
*/

    private boolean checkRecordPermission() {

        if (systemPermissionHelper.isContactPermissionGranted()) {
            return true;
        } else {
            systemPermissionHelper.requestPermissionsReadContacts();
            return false;
        }
    }
}