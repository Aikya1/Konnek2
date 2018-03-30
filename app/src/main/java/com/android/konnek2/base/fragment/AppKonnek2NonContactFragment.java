package com.android.konnek2.base.fragment;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.konnek2.R;
import com.android.konnek2.call.core.utils.ConstsCore;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.services.QMUserService;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.base.db.AppCallLogModel;
import com.android.konnek2.base.db.AppNonKonnek2ContactAdpater;
import com.android.konnek2.ui.fragments.base.BaseFragment;
import com.android.konnek2.utils.helpers.ServiceManager;
import com.android.konnek2.utils.listeners.ContactInterface;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Lenovo on 10-01-2018.
 */

public class AppKonnek2NonContactFragment extends BaseFragment implements ContactInterface {

    private DataManager dataManager;
    public QBRequestGetBuilder qbRequestGetBuilder;
    private int page = 1;
    List<String> contactNumberList;
    private AppCallLogModel appCallLogModel;
    private ArrayList<AppCallLogModel> phoneContactModel;
    private AppNonKonnek2ContactAdpater appNonKonnek2ContactAdpater;
    private ListView nonKonnek2Listiview;
    private ProgressBar progressBar;
    private ServiceManager serviceManager;

    public AppKonnek2NonContactFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_konnek2_non_contact, container, false);
        nonKonnek2Listiview = view.findViewById(R.id.listview_non_konnek2_contacts);
        progressBar = view.findViewById(R.id.progress_non_contact);
        serviceManager = ServiceManager.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactNumberList = new ArrayList<>();

        dataManager = DataManager.getInstance();
        phoneContactModel = new ArrayList<>();
        qbRequestGetBuilder = new QBRequestGetBuilder();
        loadContacts();

//        new getAllContactsAsyn().execute();
//        searchUsers();
    }

    @Override
    public void contactUsersList(List<String> usersLists) {

        if (!usersLists.get(0).isEmpty() && usersLists.get(0) != null) {
//            sendInviteSMS(usersLists.get(0));


            shareTextUrl();


        }

    }

    @Override
    public void contactChat(String contactGroupDialog) {

    }


    public void sendInviteSMS(String selectedFriends) {
        Resources resources = getActivity().getResources();
        // code by suresh
        Intent emailIntent = new Intent();
        PackageManager pm = getActivity().getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");

        Intent openInChooser = Intent.createChooser(emailIntent, "Share via");
        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if (packageName.contains("com.whatsapp")
                    || packageName.contains("com.facebook.katana")
                    || packageName.contains("sms")
                    || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if (packageName.contains("com.whatsapp")) {
                    Log.d("CREATE_CHOOSER", "WHATSAPP");
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getText(R.string.invite_friends_body_of_invitation));
                } else if (packageName.contains("com.facebook.katana")) {
                    Log.d("CREATE_CHOOSER", "FACEBOOK");
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getText(R.string.invite_friends_body_of_invitation));
                } else if (packageName.contains("sms")) {
                    Log.d("CREATE_CHOOSER", "SMS");

                    intent.setData(Uri.parse("sms:"));
                    intent.setType("vnd.android-dir/mms-sms");
                    intent.putExtra("address", selectedFriends);
                    intent.putExtra("sms_body", resources.getText(R.string.invite_friends_body_of_invitation));
//                    intent.putExtra(Intent.EXTRA_TEXT, resources.getText(R.string.invite_friends_body_of_invitation));
                } else if (packageName.contains("android.gm")) {
                    Log.d("CREATE_CHOOSER", "EMAIL ");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, selectedFriends);
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getText(R.string.invite_friends_body_of_invitation));
                    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getText(R.string.invite_friends_subject_of_invitation));
                    intent.setType("message/rfc822");
                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        getActivity().startActivity(openInChooser);
    }


    private class getAllContactsAsyn extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            getAllContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressBar.setVisibility(View.INVISIBLE);
//            loadContacts();

        }
    }

    public void loadContacts() {
        try {

            if (ServiceManager.friendList != null && ServiceManager.friendList.size() != 0) {
                appNonKonnek2ContactAdpater = new AppNonKonnek2ContactAdpater(getActivity(),
                        ServiceManager.friendList, this);
                if (!appNonKonnek2ContactAdpater.isEmpty() && appNonKonnek2ContactAdpater != null) {
                    nonKonnek2Listiview.setAdapter(appNonKonnek2ContactAdpater);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void searchUsers() {

        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        requestBuilder.setPage(page);
        requestBuilder.setPerPage(ConstsCore.FL_FRIENDS_PER_PAGE);

        QMUserService.getInstance().getUsersByPhoneNumbers(contactNumberList, requestBuilder, true).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<List<QMUser>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<QMUser> qbUsers) {

                    }
                });

    }

    private void getAllContacts() {


        String Number = null;

        if (getActivity() != null) {

            ContentResolver contentResolver = getActivity().getContentResolver();


            String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
                    + ("1") + "'";
            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                    + " COLLATE LOCALIZED ASC";


            Cursor cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI, null, selection
                            + " AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER
                            + "=1", null, sortOrder);
//        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id},
                                null);
                        if (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Number = phoneNumber;
                        }

                        phoneCursor.close();
                        String formattedNumber = PhoneNumberUtils.formatNumber(Number);
                        appCallLogModel = new AppCallLogModel();
                        contactNumberList.add(formattedNumber);
                        appCallLogModel.setContactName(name);
                        appCallLogModel.setContactNumber(formattedNumber);
                        phoneContactModel.add(appCallLogModel);

                    }
                }
                cursor.close();

            }

        }
    }


    /*TEST CODE ====== to Share app */
    public void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Konnek2");
        share.putExtra(Intent.EXTRA_TEXT, "http://www.codeofaninja.com");
        startActivity(Intent.createChooser(share, "Share App"));

    }
}
