package com.aikya.konnek2.utils.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.aikya.konnek2.App;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.models.InviteFriend;
import com.aikya.konnek2.call.core.models.LoginType;
import com.aikya.konnek2.call.core.models.UserCustomData;
import com.aikya.konnek2.call.core.utils.UserFriendUtils;
import com.aikya.konnek2.call.core.utils.Utils;
import com.aikya.konnek2.call.core.utils.helpers.CoreSharedHelper;
import com.aikya.konnek2.call.services.QMAuthService;
import com.aikya.konnek2.call.services.QMUserService;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.call.db.managers.DataManager;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.server.Performer;
import com.quickblox.extensions.RxJavaPerformProcessor;
import com.quickblox.messages.services.QBPushManager;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBAddressBookContact;
import com.quickblox.users.model.QBAddressBookResponse;
import com.quickblox.users.model.QBUser;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by pelipets on 1/6/17.
 */

public class ServiceManager {

    public static final String TAG = ServiceManager.class.getSimpleName();
    private static final String TAG_ANDROID = "android";
    public static ArrayList<InviteFriend> friendList;


    private static ServiceManager instance;

    private Context context;

    private QMAuthService authService;
    private QMUserService userService;


    public static ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

    private ServiceManager() {
        this.context = App.getInstance();
        authService = QMAuthService.getInstance();
        userService = QMUserService.getInstance();
    }


    public Observable<List<QMUser>> checkIfUserExist(String phNumber) {

        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1);
        pagedRequestBuilder.setPerPage(50);

        ArrayList<String> phones = new ArrayList<String>();
        phones.add(phNumber);

        Observable<List<QMUser>> result = userService.getUsersByPhoneNumbers(phones, pagedRequestBuilder, true)
                .subscribeOn(Schedulers.io())
                .map(new Func1<List<QMUser>, List<QMUser>>() {
                    @Override
                    public List<QMUser> call(List<QMUser> userList) {
                        return userList;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
        return result;
    }


    public Observable<QMUser> checkIfUserExistsEmail(String email) {
        Observable<QMUser> result2 = userService.getUserByEmail(email)
                .subscribeOn(Schedulers.io())
                .map(new Func1<QMUser, QMUser>() {
                    @Override
                    public QMUser call(QMUser qmUser) {
                        return qmUser;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        return result2;
    }

    /*public Observable<QMUser> checkIfUserExist_2(String email) {

        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1);
        pagedRequestBuilder.setPerPage(50);

        ArrayList<String> phones = new ArrayList<String>();
        phones.add(email);
        Observable<QMUser> result = userService.getUserByEmail(email)
                .subscribeOn(Schedulers.io())
                .map(new Func1<QMUser, QMUser>() {
                    @Override
                    public QMUser call(QMUser qmUser) {
                        return qmUser;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
        return result;
    }*/


    public Observable<QBUser> signUp(QBUser user) {

        final String password = user.getPassword();

        Observable<QBUser> result = authService.signup(user)
                .subscribeOn(Schedulers.io())
                .map(new Func1<QBUser, QBUser>() {
                    @Override
                    public QBUser call(QBUser qbUser) {
                        saveOwnerUser(qbUser);
                        AppSession.startSession(qbUser);
                        qbUser.setPassword(password);
                        return qbUser;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        return result;
    }

    public Observable<QBUser> login(QBUser user) {
        final String userPassword = user.getPassword();

        Observable<QBUser> result = authService.login(user)
                .subscribeOn(Schedulers.io())
                .map(new Func1<QBUser, QBUser>() {
                    @Override
                    public QBUser call(QBUser qbUser) {
                        CoreSharedHelper.getInstance().saveUsersImportInitialized(true);
                        String password = userPassword;

                        if (!hasUserCustomData(qbUser)) {
                            qbUser.setOldPassword(password);
                            try {
                                updateUserSync(qbUser);
                            } catch (QBResponseException e) {
                                Log.d(TAG, "updateUser " + e.getMessage());
                                throw Exceptions.propagate(e);
                            }
                        }

                        qbUser.setPassword(password);

                        saveOwnerUser(qbUser);

                        AppSession.startSession(qbUser);

                        return qbUser;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        return result;
    }

    public Observable<QBUser> login(final String socialProvider, final String accessToken, final String accessTokenSecret) {
        Observable<QBUser> result = authService.login(socialProvider, accessToken, accessTokenSecret).subscribeOn(Schedulers.io())
                .map(new Func1<QBUser, QBUser>() {
                    @Override
                    public QBUser call(QBUser qbUser) {
                        Log.d(TAG, "login observer call " + qbUser);
//                        UserCustomData userCustomData = Utils.customDataToObject(qbUser.getCustomData());
                        if (QBProvider.FACEBOOK.equals(socialProvider) /*&& TextUtils.isEmpty(userCustomData.getAvatarUrl())*/) {
                            //Actions for first login via Facebook
                            CoreSharedHelper.getInstance().saveUsersImportInitialized(false);
                            getFBUserWithAvatar(qbUser);
                        } else if (QBProvider.FIREBASE_PHONE.equals(socialProvider) && TextUtils.isEmpty(qbUser.getFullName())) {
                            //Actions for first login via Firebase phone
                            CoreSharedHelper.getInstance().saveUsersImportInitialized(false);
                            getUserWithFullNameAsPhone(qbUser);
                        }
                        /*try {
                            updateUserSync(qbUser);
                        } catch (QBResponseException e) {
                            Log.d(TAG, "updateUser " + e.getMessage());
                            throw Exceptions.propagate(e);
                        }*/

//                        qbUser.setPassword(QBSessionManager.getInstance().getToken());

                        qbUser.setPassword(qbUser.getPhone());
//                        qbUser.setPassword();
                        //  qbUser.setCustomDataAsObject();
//                        saveOwnerUser(qbUser);

                        AppSession.startSession(qbUser);

                        return qbUser;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        return result;
    }

    public void logout(final Subscriber<Void> subscriber) {
        if (QBPushManager.getInstance().isSubscribedToPushes()) {
            QBPushManager.getInstance().addListener(new QBPushManager.QBSubscribeListener() {
                @Override
                public void onSubscriptionCreated() {
                }

                @Override
                public void onSubscriptionError(Exception e, int i) {
                }

                @Override
                public void onSubscriptionDeleted(boolean b) {
                    logoutInternal(subscriber);
                }
            });
            SubscribeService.unSubscribeFromPushes(context);
        } else {
            logoutInternal(subscriber);
        }
    }

    private void logoutInternal(final Subscriber<Void> subscriber) {
        QMAuthService.getInstance().logout()
                .subscribeOn(Schedulers.io())
                .map(new Func1<Void, Void>() {
                    @Override
                    public Void call(Void aVoid) {
                        clearDataAfterLogOut();
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public Observable<Void> resetPassword(String email) {
        return QMAuthService.getInstance().resetPassword(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<QMUser> changePasswordUser(QBUser inputUser) {
        final String password = inputUser.getPassword();
        QMUser qmUser = QMUser.convert(inputUser);
        Observable<QMUser> result = QMUserService.getInstance().updateUser(qmUser)
                .subscribeOn(Schedulers.io())
                .map(new Func1<QBUser, QMUser>() {
                    @Override
                    public QMUser call(QBUser qbUser) {
                        QMUser user = QMUser.convert(qbUser);
                        user.setPassword(password);
                        return user;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        return result;
    }


    public Observable<QMUser> updateUser(QBUser inputUser) {
        Observable<QMUser> result = null;
        final String password = inputUser.getPassword();

        /*UserCustomData userCustomDataNew = getUserCustomData(inputUser);
        inputUser.setCustomData(Utils.customDataToString(userCustomDataNew));

        inputUser.setPassword(null);
        inputUser.setOldPassword(null);*/
        QMUser qmUser = QMUser.convert(inputUser);
        result = QMUserService.getInstance().updateUser(qmUser)
                .subscribeOn(Schedulers.io())
                .map(new Func1<QMUser, QMUser>() {
                    @Override
                    public QMUser call(QMUser qmUser) {
                        if (LoginType.EMAIL.equals(AppSession.getSession().getLoginType())) {
                            qmUser.setPassword(password);
                        } else {
                            qmUser.setPassword(QBSessionManager.getInstance().getToken());
                        }
                        return qmUser;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        return result;
    }

    public Observable<QMUser> updateUser(final QBUser user, final File file) {

        Observable<QMUser> result = null;

        Performer<QBFile> performer = QBContent.uploadFileTask(file, true, null);
        final Observable<QBFile> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        result = observable
                .subscribeOn(Schedulers.io())

                .flatMap(new Func1<QBFile, Observable<QMUser>>() {
                    @Override
                    public Observable<QMUser> call(QBFile qbFile) {
                        QBUser newUser = new QBUser();

                        newUser.setId(user.getId());
                        newUser.setPassword(user.getPassword());
                        newUser.setFileId(qbFile.getId());
                        newUser.setFullName(user.getFullName());

                        UserCustomData userCustomData = getUserCustomData(user);
                        userCustomData.setAvatarUrl(qbFile.getPublicUrl());
                        newUser.setCustomData(Utils.customDataToString(userCustomData));

                        return updateUser(newUser);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        return result;
    }


    //mohita
    /*public Observable<QBUser> updateUser2(final QBUser user, final File file) {

        Observable<QBUser> result = null;

        Performer<QBFile> performer = QBContent.uploadFileTask(file, true, null);
        final Observable<QBFile> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        result = observable
                .subscribeOn(Schedulers.io())

                .flatMap(new Func1<QBFile, Observable<QBUser>>() {
                    @Override
                    public Observable<QBUser> call(QBFile qbFile) {
                        QBUser newUser = new QBUser();

                        newUser.setId(user.getId());
                        newUser.setPassword(user.getPassword());
                        newUser.setFileId(qbFile.getId());
                        newUser.setFullName(user.getFullName());

                        UserCustomData userCustomData = getUserCustomData(user);
                        userCustomData.setAvatarUrl(qbFile.getPublicUrl());
                        newUser.setCustomData(Utils.customDataToString(userCustomData));

                        return updateUser2(newUser);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        return result;
    }

    public Observable<QBUser> updateUser2(QBUser inputUser) {
        Observable<QBUser> result = null;
        final String password = inputUser.getPassword();

        *//*UserCustomData userCustomDataNew = getUserCustomData(inputUser);
        inputUser.setCustomData(Utils.customDataToString(userCustomDataNew));

        inputUser.setPassword(null);
        inputUser.setOldPassword(null);*//*

        result = QMUserService.getInstance().updateUser2(inputUser)
                .subscribeOn(Schedulers.io())
                .map(new Func1<QMUser, QMUser>() {
                    @Override
                    public QMUser call(QMUser qmUser) {
                        if (LoginType.EMAIL.equals(AppSession.getSession().getLoginType())) {
                            qmUser.setPassword(password);
                        } else {
                            qmUser.setPassword(QBSessionManager.getInstance().getToken());
                        }
                        return qmUser;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        return result;
    }*/

    public QBUser updateUserSync(QBUser inputUser) throws QBResponseException {
        QBUser user;
        String password = inputUser.getPassword();

        UserCustomData userCustomDataNew = getUserCustomData(inputUser);
        inputUser.setCustomData(Utils.customDataToString(userCustomDataNew));

        inputUser.setPassword(null);
        inputUser.setOldPassword(null);

        QMUser qmUser = QMUser.convert(inputUser);
        user = QMUserService.getInstance().updateUserSync(qmUser);

        if (LoginType.EMAIL.equals(AppSession.getSession().getLoginType())) {
            user.setPassword(password);
        } else {
            user.setPassword(QBSessionManager.getInstance().getToken());
        }

        return user;
    }

    private void clearDataAfterLogOut() {

        AppSession.getSession().closeAndClear();
        DataManager.getInstance().clearAllTables();
        CoreSharedHelper.getInstance().clearAll();

    }

    private void saveOwnerUser(QBUser qbUser) {

        QMUser user = UserFriendUtils.createLocalUser(qbUser);
        QMUserService.getInstance().getUserCache().createOrUpdate(user);
    }


    private boolean hasUserCustomData(QBUser user) {

        if (TextUtils.isEmpty(user.getCustomData())) {
            return false;
        }
        UserCustomData userCustomData = Utils.customDataToObject(user.getCustomData());
        return userCustomData != null;
    }


    private UserCustomData getUserCustomData(QBUser user) {
        if (TextUtils.isEmpty(user.getCustomData())) {
            return new UserCustomData();
        }

        UserCustomData userCustomData = Utils.customDataToObject(user.getCustomData());

        if (userCustomData != null) {
            return userCustomData;
        } else {
            return new UserCustomData();
        }
    }

    private QBUser getFBUserWithAvatar(QBUser user) {
        String avatarUrl = context.getString(com.aikya.konnek2.R.string.url_to_facebook_avatar, user.getFacebookId());
        return user;
    }


    /*private UserCustomData getUserCustomData(QBUser user) {
        if (TextUtils.isEmpty(user.getCustomData())) {
            return new UserCustomData();
        }
        String data = user.getCustomData();
        UserCustomData userCustomData = Utils.customDataToObject(user.getCustomData());
        if (userCustomData != null) {
            return userCustomData;
        } else {
            return new UserCustomData();
        }
    }
*/
    private QBUser getUserWithFullNameAsPhone(QBUser user) {
        user.setFullName(user.getPhone());
        return user;
    }



    /*+========================TEST CODE==========================*/


    public void uploadAllContacts(Context context) {

//        List<InviteFriend> friendList = getContactList(context);


       /* phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        LoadContact loadContact = new LoadContact();
        loadContact.execute();*/

//        uploadToContactBook(friendList);

        LoadContact loadContact = new LoadContact();
        loadContact.execute();


    }

    class LoadContact extends AsyncTask<Void, Void, List<InviteFriend>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<InviteFriend> doInBackground(Void... voids) {
            InviteFriend inviteFriend;
            ArrayList<QBAddressBookContact> contactsGlobal = new ArrayList<>();
            friendList = new ArrayList<>();
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        QBAddressBookContact contact1 = new QBAddressBookContact();

                        inviteFriend = new InviteFriend();
                        inviteFriend.setName(name);
                        inviteFriend.setId(id);
//                        inviteFriend.setId(id);
                        contact1.setName(name);

                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id},
                                null);
                        if (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneNumber = formatNumber(phoneNumber.trim());
//                            phoneNumber = phoneNumber.replaceFirst("[]","");
                            inviteFriend.setNumber(phoneNumber);
                            contact1.setPhone(phoneNumber);
                        }

                        phoneCursor.close();

                   /* Cursor emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }*/
                        friendList.add(inviteFriend);
                        contactsGlobal.add(contact1);
                    }
                }
                uploadToQbAddressBook(contactsGlobal);
            }
            return friendList;
        }

        @Override
        protected void onPostExecute(List<InviteFriend> friendList) {
            super.onPostExecute(friendList);


            Log.d(TAG, "List SIZE == " + friendList.size());

        }
    }

    private String formatNumber(String phoneNumber) {
        String res = "";
        if (phoneNumber.startsWith("+")) {
            res = phoneNumber.replaceFirst("[-+.^:,]", "");
        }
        if (phoneNumber.startsWith("00")) {
            res = phoneNumber.substring(2, phoneNumber.length());
        } else if (phoneNumber.startsWith("0")) {
            res = phoneNumber.replaceFirst("0", "");
        }

        return res;
    }

    private void uploadToQbAddressBook(ArrayList<QBAddressBookContact> contactsGlobal) {
        String UDID = "";
        boolean force = true;

        Performer<QBAddressBookResponse> performer = QBUsers.uploadAddressBook(contactsGlobal, UDID, force);
        Observable<QBAddressBookResponse> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<QBAddressBookResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "Error =-= " + e.getMessage());
                    }

                    @Override
                    public void onNext(QBAddressBookResponse qbAddressBookResponse) {

                        int createdCount = qbAddressBookResponse.getCreatedCount();
                        int updatedCount = qbAddressBookResponse.getUpdatedCount();
                        Log.d(TAG, "Response ==  " + qbAddressBookResponse.toString());
                    }
                });


    }


}
