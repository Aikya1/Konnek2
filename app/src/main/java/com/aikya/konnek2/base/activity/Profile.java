package com.aikya.konnek2.base.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aikya.konnek2.Preference.ProfilePrefManager;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.models.UserCustomData;
import com.aikya.konnek2.call.db.models.Attachment;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.base.BaseActivity;
import com.aikya.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.aikya.konnek2.utils.AppConstant;
import com.aikya.konnek2.utils.AppPreference;
import com.aikya.konnek2.utils.AuthUtils;
import com.aikya.konnek2.utils.EmailPhoneValidationUtils;
import com.aikya.konnek2.utils.MediaUtils;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.helpers.MediaPickHelper;
import com.aikya.konnek2.utils.helpers.ServiceManager;
import com.aikya.konnek2.utils.listeners.OnMediaPickedListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.model.QBUser;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

import static com.aikya.konnek2.utils.AppConstant.nonDupLangList;

public class Profile extends BaseActivity implements OnMediaPickedListener, AdapterView.OnItemSelectedListener {


    private static String TAG = Profile.class.getSimpleName();
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button profilebtnNext;
    Spinner spin1;
    Spinner spin2;
    private MediaPickHelper mediaPickHelper;
    private Uri imageUri;
    private boolean isNeedUpdateImage;
    //ImageUtils imageUtils;
    RoundedImageView photoImageView;
    ImageView camimageview;
    Button save;
    private String currentFullName;
    private UserCustomData userCustomData;
    EditText status, firstNameEt, lastNameEt, emailEt, contactnoEt, passwordEt;
    Observable<QMUser> qmUserObservable;
    Observable<QBUser> qmUserSignUpObservable;
    private String firstName, lastName, phNo, userEmail, profileUrl, userStatus, userLanguage, selectedLanguage, signUpType, password, fullName, facebookId;
    private String countryCode;

    StringifyArrayList<String> tags = new StringifyArrayList<String>();
    private ProfilePrefManager profileprefManager;
    File file;

    QBFile userQbFile;

    private String loginType;
    private QBUser qbUser;


    ServiceManager serviceManager;

    @Override
    protected int getContentResId() {
        return com.aikya.konnek2.R.layout.activity_profile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.aikya.konnek2.R.layout.activity_profile);
        // Checking for first time launch - before calling setContentView()
        profileprefManager = new ProfilePrefManager(this);
        if (!profileprefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        phNo = getIntent().getExtras().getString("phNo");
        countryCode = getIntent().getExtras().getString("countryCode");
        serviceManager = ServiceManager.getInstance();
        // setContentView(R.layout.activity_profile);
        viewPager = findViewById(com.aikya.konnek2.R.id.view_pager);
        dotsLayout = findViewById(com.aikya.konnek2.R.id.layoutProfileDots);
        profilebtnNext = findViewById(com.aikya.konnek2.R.id.btn_profile_next);
        mediaPickHelper = new MediaPickHelper();
        // layouts of all profile sliders
        layouts = new int[]{
                com.aikya.konnek2.R.layout.profile_slide1,
                com.aikya.konnek2.R.layout.profile_slide2,
        };
        // adding bottom dots
        addBottomDots(0);
        // making notification bar transparent
        changeStatusBarColor();
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);


        if (phNo != null) {
            myViewPagerAdapter.setContactNumber(phNo);
        }

        profilebtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    //initData();
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });

        loginType = appSharedHelper.getLoginType();
    }

    private void setUpLanguageSpinner() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.custom_spinner_dropdown, nonDupLangList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spin1 != null) {
            spin1.setAdapter(adapter);
            String myString = Locale.getDefault().getDisplayLanguage(); //the value you want the position for
            ArrayAdapter myAdap = (ArrayAdapter) spin1.getAdapter(); //cast to an ArrayAdapter
            int spinnerPosition = myAdap.getPosition(myString);
            //set the default according to value
            spin1.setSelection(spinnerPosition);
            spin1.setOnItemSelectedListener(Profile.this);
//                selectedLanguage = spin1.getSelectedItem().toString();
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(com.aikya.konnek2.R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(com.aikya.konnek2.R.array.array_dot_inactive);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        profileprefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(Profile.this, AppHomeActivity.class));
        finish();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                //profilebtnNext.setText(getString(R.string.start));
                // profilebtnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                // profilebtnNext.setText(getString(R.string.next));
                //  profilebtnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }*/
    }

    @Override
    public void onMediaPicked(int requestCode, Attachment.Type attachmentType, Object attachment) {
        if (Attachment.Type.IMAGE.equals(attachmentType)) {
            startCropActivity(MediaUtils.getValidUri((File) attachment, this));
        }
    }

    @Override
    public void onMediaPickError(int requestCode, Exception e) {

        ToastUtils.shortToast("Error = " + e.getMessage());
    }

    @Override
    public void onMediaPickClosed(int requestCode) {

        ToastUtils.shortToast("MediaPickClosed");
    }


    private void startCropActivity(Uri originalUri) {
        String extensionOriginalUri = originalUri.getPath().substring(originalUri.getPath().lastIndexOf(".")).toLowerCase();
        imageUri = MediaUtils.getValidUri(new File(getCacheDir(), extensionOriginalUri), this);
        Crop.of(originalUri, imageUri).asSquare().start(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            isNeedUpdateImage = true;
            photoImageView.setImageURI(imageUri);
        } else if (resultCode == Crop.RESULT_ERROR) {
            ToastUtils.longToast(Crop.getError(result).getMessage());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (spin1 != null) {
            selectedLanguage = spin1.getSelectedItem().toString();
            Log.d(TAG, "Selected language = " + selectedLanguage);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }


        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }


        public void setContactNumber(String number) {
            if (contactnoEt != null) {
                contactnoEt.setText(number);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            spin1 = findViewById(R.id.spin1);
            //  spin2 = view.findViewById(R.id.spin2);
            photoImageView = findViewById(com.aikya.konnek2.R.id.change_photo_view);
            camimageview = findViewById(com.aikya.konnek2.R.id.image_userStatus);
            save = findViewById(com.aikya.konnek2.R.id.btn_profile_next);
            firstNameEt = findViewById(com.aikya.konnek2.R.id.firstname);
            status = findViewById(com.aikya.konnek2.R.id.etstatus);
            lastNameEt = findViewById(com.aikya.konnek2.R.id.lastname);
            contactnoEt = findViewById(com.aikya.konnek2.R.id.contactno);
            emailEt = findViewById(com.aikya.konnek2.R.id.email);
            passwordEt = findViewById(com.aikya.konnek2.R.id.facebookpwd);
            setUpLanguageSpinner();

            //If user comes by facebook
            if (appSharedHelper.getLoginType().equalsIgnoreCase(AppConstant.LOGIN_TYPE_FACEBOOK) ||
                    appSharedHelper.getLoginType().equalsIgnoreCase(AppConstant.LOGIN_TYPE_GMAIL)) {

                profileUrl = appSharedHelper.getUserProfileUrl();
                fullName = appSharedHelper.getUserFullName();
                userEmail = appSharedHelper.getUserEmail();
//                String gender = appSharedHelper.getUserGender();
                String socialId = appSharedHelper.getSocialId();

                if (firstNameEt != null) {
                    firstNameEt.setText(fullName);
                }

                if (emailEt != null) {
                    emailEt.setText(userEmail);
                    emailEt.setEnabled(false);
                }

            } else if (appSharedHelper.getLoginType().equalsIgnoreCase(AppConstant.LOGIN_TYPE_MANUAL)) {

                if (contactnoEt != null) {
                    contactnoEt.setEnabled(false);
                }
            }


            if (profileUrl != null) {
                Glide.with(Profile.this)
                        .load(profileUrl)
                        .animate(com.aikya.konnek2.R.anim.abc_fade_in)
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .centerCrop()
                        .into(photoImageView);
            }


            if (phNo != null && contactnoEt != null) {
                contactnoEt.setText(phNo);
            }


            camimageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPickHelper.pickAnMedia(Profile.this, MediaUtils.CAMERA_PHOTO_REQUEST_CODE);
                }
            });
            //Onclick of people
            photoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPickHelper.pickAnMedia(Profile.this, MediaUtils.CAMERA_PHOTO_REQUEST_CODE);
                }
            });
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(1);

                    if (validates()) {
                        String unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                        qbUser = new QBUser();
                        userCustomData = new UserCustomData();
                        showProgress();
                        uploadFileToServer();
                        userStatus = status.getText().toString();
                        userLanguage = Locale.getDefault().getLanguage().toString();
                        firstName = firstNameEt.getText().toString().trim();
                        lastName = lastNameEt.getText().toString().trim();
                        userEmail = emailEt.getText().toString().trim();
                        phNo = contactnoEt.getText().toString().trim();
                        signUpType = "Android";
                        StringifyArrayList<String> tags = new StringifyArrayList<String>();
                        tags.add("dev");
                        //to get cropped camera image

                       /* if (file != null) {
                            String s = file.getAbsolutePath();

                            if (s.contains("Crop")) {
                                s1 = s.replace("Crop", "jpg");
                                File file1 = new File(s1);
                                file.renameTo(file1);
                            }
                        }*/


                        String login = countryCode + phNo;
                        if (login.contains("+")) {
                            login = login.replace("+", "");
                        }
                        qbUser.setLogin(login);
                        qbUser.setPassword(AppConstant.USER_PASSWORD);
                        qbUser.setEmail(userEmail);


                        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)) {
                            qbUser.setFullName(firstName + " " + lastName);
                        } else if (!TextUtils.isEmpty(firstName)) {
                            qbUser.setFullName(firstName);
                        } else if (!TextUtils.isEmpty(lastName)) {
                            qbUser.setFullName(lastName);
                        }

                        qbUser.setPhone(countryCode+phNo);
                        qbUser.setCreatedAt(new Date());

                        userCustomData.setFirstName(firstName);
                        if (facebookId != null) {
                            userCustomData.setFacebookId(facebookId);
                        }
                        userCustomData.setLastName(lastName);

                        if (profileUrl != null) {
                            userCustomData.setAvatarUrl(profileUrl);
                        }

                       /* if (s1 != null) {
                            userCustomData.setAvatarUrl(s1);
                        } else if (profileUrl != null) {
                            userCustomData.setAvatarUrl(profileUrl);
                        } else if (imageUri != null) {
                            userCustomData.setAvatarUrl(imageUri.toString());
                        }*/

                        userCustomData.setPrefLanguage(selectedLanguage);
                        userCustomData.setIsLocationToShare(true);
                        userCustomData.setContactno(phNo);
                        userCustomData.setPrefEmail(userEmail);
                        userCustomData.setStatus(status.getText().toString());
                        userCustomData.setSignUpType(signUpType);
                        userCustomData.setDeviceUDid(unique_id);
                        userCustomData.setIsEuropean(appSharedHelper.getIsGdpr());
                        userCustomData.setAddressLine1("");
                        userCustomData.setCity("");
                        userCustomData.setCountry("");
                        userCustomData.setCountryCode(countryCode);
                        userCustomData.setDob("");
                        userCustomData.setPrefSms("");
                        userCustomData.setPrefEmail("");
                        userCustomData.setPrefInApp("");

                        userCustomData.setLastSeen("");


                        Gson gson = new Gson();
                        String userCustomDataStringToSave = gson.toJson(userCustomData);
                        qbUser.setCustomData(userCustomDataStringToSave);
                        qbUser.setTags(tags);


                        serviceManager.signUp(qbUser)
                                .subscribe(manualLoginObserver);
                    }
                }
            });
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    private void uploadFileToServer() {
        if (isNeedUpdateImage && imageUri != null) {
            file = MediaUtils.getCreatedFileFromUri(imageUri);
            userCustomData.setAvatarUrl(imageUri.toString());
        }
    }

    private boolean validates() {

        phNo = contactnoEt.getText().toString();
        userEmail = emailEt.getText().toString();
        password = passwordEt.getText().toString();

        /*if (file == null && imageUri == null) {
            ToastUtils.shortToast("Please upload pic");
            return false;
        } else if (TextUtils.isEmpty(status.getText().toString())) {
            ToastUtils.shortToast("Status is Mandatory");
            return false;
        } else if (TextUtils.isEmpty(firstNameEt.getText().toString())) {
            ToastUtils.shortToast("First Name is Mandatory");
            return false;
        } else if (TextUtils.isEmpty(lastNameEt.getText().toString())) {
            ToastUtils.shortToast("Last Name is Mandatory");
            return false;
        } else if (TextUtils.isEmpty(emailEt.getText().toString())) {
            ToastUtils.shortToast("Email is Mandatory");
            return false;
        } else if (!EmailPhoneValidationUtils.isValidEmail(userEmail)) {
            ToastUtils.shortToast("Email is empty/not valid");
            return false;
        } else */
        if (!EmailPhoneValidationUtils.isValidPhone(phNo) || TextUtils.isEmpty(phNo)) {
            viewPager.setCurrentItem(1);
            ToastUtils.shortToast("Phone No is empty/not valid");
            return false;
        } else if (TextUtils.isEmpty(firstNameEt.getText().toString())) {
            viewPager.setCurrentItem(1);
            ToastUtils.shortToast("First Name is Mandatory");
            return false;
        } /*else if (TextUtils.isEmpty(password)) {
            ToastUtils.shortToast("Password can't be empty.");
            return false;
        } else if (password.length() < 8) {
            ToastUtils.shortToast("Password should be greate than 8 characters");
            return false;
        }*/
        return true;
    }


    /*------------------------------------TEST CODE ---------------------------*/


    private Observer<QBUser> manualLoginObserver = new Observer<QBUser>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            hideProgress();
            AuthUtils.parseExceptionMessage(Profile.this, e.getMessage());
        }

        @Override
        public void onNext(QBUser qbUser) {
            appSharedHelper.saveFirstAuth(true);
            appSharedHelper.saveSavedRememberMe(true);
            final QBUser loginUser = qbUser;

            serviceManager.login(qbUser).subscribe(new Subscriber<QBUser>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    hideProgress();
                    AuthUtils.parseExceptionMessage(Profile.this, e.getMessage());
                }

                @Override
                public void onNext(QBUser user) {
                    hideProgress();
                    performLoginSuccessAction(loginUser);
                }
            });


        }
    };


    private void performLoginSuccessAction(QBUser user) {
        AppPreference.putQBUserId("" + user.getId());
        AppPreference.putQbUser("" + user);
        startMainActivity(user);
    }


    protected void startMainActivity(QBUser user) {
        AppSession.getSession().updateUser(user);
        startMainActivity();
    }

    protected void startMainActivity() {
//        MainActivity.start(BaseAuthActivity.this);
//        startActivity(new Intent(Profile.this, Intro.class));

        appSharedHelper.saveLastOpenActivity(AppHomeActivity.class.getSimpleName());
//        AppHomeActivity.start(Profile.this);
        RegistrationSuccessActivity.start(Profile.this);

        finish();
    }
}
