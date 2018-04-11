package com.aikya.konnek2.ui.activities.profile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.models.UserCustomData;
import com.aikya.konnek2.call.core.utils.Utils;
import com.aikya.konnek2.call.db.utils.ErrorUtils;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.base.BaseLoggableActivity;
import com.aikya.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.aikya.konnek2.utils.MediaUtils;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.ValidationUtils;
import com.aikya.konnek2.utils.helpers.ServiceManager;
import com.aikya.konnek2.utils.listeners.OnMediaPickedListener;
import com.aikya.konnek2.call.db.models.Attachment;
import com.aikya.konnek2.utils.helpers.MediaPickHelper;
import com.aikya.konnek2.utils.image.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

import static android.support.v4.graphics.TypefaceCompatUtil.getTempFile;

public class MyProfileActivity extends BaseLoggableActivity implements OnMediaPickedListener {

    private static String TAG = MyProfileActivity.class.getSimpleName();

    /*asjkdhaskdghasdhoas*/
    /*ashdiashdioh8q3i234oois*/
    /*ashdashdkjhas7893892380082389238923*/

    /*mohitadsddeqwfe*/

    @Bind(com.aikya.konnek2.R.id.photo_imageview)
    RoundedImageView photoImageView;
    @Bind(R.id.etname)
    EditText etName;
    @Bind(R.id.etstatus)
    EditText etStatus;
    @Bind(R.id.email)
    EditText etEmail;
    @Bind(R.id.contactno)
    EditText contactno;
    @Bind(R.id.camera_image)
    ImageView camera_image;
    @Bind(R.id.rgGender)
    RadioGroup rgGender;
    @Bind(R.id.dob)
    EditText dob;
    @Bind(R.id.house)
    EditText house;
    @Bind(R.id.city)
    EditText city;
    @Bind(R.id.country)
    EditText country;
    @Bind(R.id.postcode)
    EditText postcode;
    @Bind(R.id.checkBox1)
    CheckBox checkBox1;
    @Bind(R.id.checkBox2)
    CheckBox checkBox2;
    @Bind(R.id.checkBox3)
    CheckBox checkBox3;

    private QBUser qbUser;
    private boolean isNeedUpdateImage;
    private UserCustomData userCustomData;
    private String currentFullName, currentEmail, userStatus;
    private String oldFullName;
    private MediaPickHelper mediaPickHelper;
    private Uri imageUri;

    public static void start(Context context) {
        Intent intent = new Intent(context, MyProfileActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_my_profile_2;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setUpActionBarWithUpButton();
        initData();
        updateOldData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
        switch (requestCode) {
            case MediaUtils.CAMERA_PHOTO_REQUEST_CODE:

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.aikya.konnek2.R.menu.done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case com.aikya.konnek2.R.id.action_done:
                if (checkNetworkAvailableWithError()) {
//                    updateUser();
                    saveChanges();
                }
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @OnClick(R.id.camera_image)
    void changePhoto(View view) {
//        fullNameTextInputLayout.setError(null);
        mediaPickHelper.pickAnMedia(this, MediaUtils.CAMERA_PHOTO_REQUEST_CODE);

//        Intent chooseImageIntent = mediaPickHelper.getPickImageChooserIntent(this);
//        startActivityForResult(chooseImageIntent, MediaUtils.CAMERA_PHOTO_REQUEST_CODE);

//        mediaPickHelper.pickImageChooser(this,MediaUtils.CAMERA_PHOTO_REQUEST_CODE);

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
        title = getString(com.aikya.konnek2.R.string.profile_title);
        mediaPickHelper = new MediaPickHelper();
        qbUser = AppSession.getSession().getUser();
    }

    private void initData() {
        try {
            currentFullName = qbUser.getFullName();
            currentEmail = qbUser.getEmail();

            initCustomData();
            loadAvatar();
            etName.setText(currentFullName);
            etEmail.setText(currentEmail);
            etStatus.setText(userStatus);
            contactno.setText(qbUser.getPhone());
//            contactno.setEnabled(false);
            if (userCustomData.getGender().equals("Female")) {
                rgGender.check(R.id.rgFemale);
            } else if (userCustomData.getGender().equals("Male")) {
                rgGender.check(R.id.rgMale);
            }
            dob.setText(userCustomData.getDob());
            house.setText(userCustomData.getAddressLine1());
            city.setText(userCustomData.getCity());
            country.setText(userCustomData.getCountry());
            postcode.setText(userCustomData.getPostalcode());
            if (userCustomData.getPrefEmail().equals("true")) {
                checkBox1.setChecked(true);
            }
            if (userCustomData.getPrefInApp().equals("true")) {
                checkBox3.setChecked(true);
            }
            if (userCustomData.getPrefSms().equals("true")) {
                checkBox2.setChecked(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initCurrentData() {
//        currentFullName = fullNameEditText.getText().toString();
        initCustomData();
    }

    private void initCustomData() {
        userCustomData = Utils.customDataToObject(qbUser.getCustomData());
        if (userCustomData == null) {
            userCustomData = new UserCustomData();
        } else {
            userStatus = userCustomData.getStatus();

        }
    }

    private void loadAvatar() {
        if (userCustomData != null && !TextUtils.isEmpty(userCustomData.getAvatarUrl())) {
            ImageLoader.getInstance().displayImage(userCustomData.getAvatarUrl(),
                    photoImageView, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
        }
    }

    private void updateOldData() {
//        oldFullName = fullNameEditText.getText().toString();
        isNeedUpdateImage = false;
    }

    private void resetUserData() {
        qbUser.setFullName(oldFullName);
        isNeedUpdateImage = false;
        initCurrentData();
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            isNeedUpdateImage = true;
            photoImageView.setImageURI(imageUri);
        } else if (resultCode == Crop.RESULT_ERROR) {
            ToastUtils.longToast(Crop.getError(result).getMessage());
        }
        canPerformLogout.set(true);
    }

    private void startCropActivity(Uri originalUri) {
        String extensionOriginalUri = originalUri.getPath().substring(originalUri.getPath().lastIndexOf(".")).toLowerCase();

        canPerformLogout.set(false);
        imageUri = MediaUtils.getValidUri(new File(getCacheDir(), extensionOriginalUri), this);
        Crop.of(originalUri, imageUri).asSquare().start(this);
    }

    private QBUser createUserForUpdating() {
        QBUser newUser = new QBUser();
        newUser.setId(qbUser.getId());
//        newUser.setPassword(qbUser.getPassword());
//        newUser.setOldPassword(qbUser.getOldPassword());
        newUser.setFullName(currentFullName);
        newUser.setFacebookId(qbUser.getFacebookId());

        RadioButton gender = findViewById(rgGender.getCheckedRadioButtonId());
        userCustomData.setGender(gender.getText().toString());
        userCustomData.setDob(dob.getText().toString());
        userCustomData.setAddressLine1(house.getText().toString());
        userCustomData.setCity(city.getText().toString());
        userCustomData.setCountry(country.getText().toString());
//        userCustomData.setContactno(contactno.getText().toString());
        userCustomData.setPostalcode(postcode.getText().toString());
        if (checkBox1.isChecked()) {
            userCustomData.setPrefEmail("true");
        }
        if (checkBox2.isChecked()) {
            userCustomData.setPrefSms("true");
        }
        if (checkBox3.isChecked()) {
            userCustomData.setPrefInApp("true");
        }
        newUser.setCustomData(Utils.customDataToString(userCustomData));
        return newUser;
    }

    private boolean isDataChanged() {
        return isNeedUpdateImage || !oldFullName.equals(currentFullName);
    }

    private boolean isFullNameNotEmpty() {
        return !TextUtils.isEmpty(currentFullName.trim());
    }


    private void updateUser() {
        QBUser user = createUserForUpdating();

       /*initCurrentData();

        if (isDataChanged()) {
           saveChanges();
        } else {
           etName.setError(getString(R.string.profile_full_name_and_photo_not_changed));
        }*/
    }

    /*private void saveChanges() {
//        if (new ValidationUtils(this).isFullNameValid(etName, currentFullName.trim())) {
        showProgress();

        QBUser newUser = createUserForUpdating();
        File file = null;
        if (isNeedUpdateImage && imageUri != null) {
            file = MediaUtils.getCreatedFileFromUri(imageUri);
        }

        Observable<QMUser> qmUserObservable;

        if (file != null) {
            qmUserObservable = ServiceManager.getInstance().updateUser(newUser, file);
        } else {
            qmUserObservable = ServiceManager.getInstance().updateUser(newUser);
        }

        qmUserObservable.subscribe(new Subscriber<QMUser>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                hideProgress();

                if (e != null) {
                    ToastUtils.longToast(e.getMessage());
                }

                resetUserData();
            }

            @Override
            public void onNext(QMUser qmUser) {
                hideProgress();
                AppSession.getSession().updateUser(qmUser);
                updateOldData();

            }
        });
//        }
    }*/

    private void saveChanges() {
//        if (new ValidationUtils(this).isFullNameValid(etName, currentFullName.trim())) {
        showProgress();

        QBUser newUser = createUserForUpdating();
        File file = null;
        if (isNeedUpdateImage && imageUri != null) {
            file = MediaUtils.getCreatedFileFromUri(imageUri);
        }

        Observable<QMUser> qmUserObservable;

        if (file != null) {
            qmUserObservable = ServiceManager.getInstance().updateUser(newUser, file);
        } else {
            qmUserObservable = ServiceManager.getInstance().updateUser(newUser);
        }

        qmUserObservable.subscribe(new Subscriber<QMUser>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                hideProgress();

                if (e != null) {
                    ToastUtils.longToast(e.getMessage());
                }

                resetUserData();
            }

            @Override
            public void onNext(QMUser qmUser) {
                hideProgress();
                AppSession.getSession().updateUser(qmUser);
                updateOldData();

            }
        });
//        }
    }
}