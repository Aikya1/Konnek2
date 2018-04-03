package com.aikya.konnek2.utils.helpers;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;


import com.aikya.konnek2.utils.DeviceInfoUtils;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.models.InviteFriend;
import com.aikya.konnek2.call.core.utils.ConstsCore;

import java.util.ArrayList;
import java.util.List;

public class EmailHelper {

    public static void sendInviteEmail(Context context, String[] selectedFriends) {

        Resources resources = context.getResources();
        // code by suresh
        Intent emailIntent = new Intent();
        PackageManager pm = context.getPackageManager();
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
                    StringBuilder builder = new StringBuilder();
                    for (String s : selectedFriends) {
                        builder.append(s);
                    }

                    String str = builder.toString();
                    intent.setData(Uri.parse("sms:"));
                    intent.setType("vnd.android-dir/mms-sms");
                    intent.putExtra("address", str);
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
        context.startActivity(openInChooser);

    }

    public static void sendInviteSMS(Context context, String[] selectedFriends) {
        Resources resources = context.getResources();
        StringBuilder builder = new StringBuilder();
        for (String s : selectedFriends) {
            builder.append(s);
        }
        String str = builder.toString();
        Intent intentt = new Intent(Intent.ACTION_VIEW);
        intentt.setData(Uri.parse("sms:"));
        intentt.setType("vnd.android-dir/mms-sms");
        intentt.putExtra("address", str);
        intentt.putExtra("sms_body", resources.getText(R.string.invite_friends_body_of_invitation));
        context.startActivity(intentt);
    }

    public static void sendFeedbackEmail(Context context, String feedbackType) {

        Resources resources = context.getResources();
        Intent intentEmail = new Intent(Intent.ACTION_SEND);
        intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{resources.getText(R.string.feedback_support_email)
                .toString()});
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, feedbackType);
        intentEmail.putExtra(Intent.EXTRA_TEXT,
                (java.io.Serializable) DeviceInfoUtils.getDeviseInfoForFeedback());
        intentEmail.setType(ConstsCore.TYPE_OF_EMAIL);
        context.startActivity(Intent.createChooser(intentEmail, resources.getText(
                R.string.feedback_choose_email_provider)));
    }

    public static List<InviteFriend> getContactsWithEmail(Context context) {
        List<InviteFriend> friendsContactsList = new ArrayList<InviteFriend>();
        Uri uri = null;

        ContentResolver contentResolver = context.getContentResolver();

        String[] PROJECTION = new String[]{ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID};

        String order = "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC";

        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";

        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION,
                filter, null, order);

        if (cursor != null && cursor.moveToFirst()) {
            String id;
            String name;
            String email;
            do {
                name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                if (ContactsContract.Contacts.CONTENT_URI != null) {
                    uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(
                            id));
                    uri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                }
                friendsContactsList.add(new InviteFriend(email, name, null, InviteFriend.VIA_CONTACTS_TYPE,
                        uri, false));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        return friendsContactsList;
    }

    public static List<InviteFriend> getMobileContacts(Context context) {

        List<InviteFriend> friendsContactsList = new ArrayList<InviteFriend>();
        Uri uri = null;

        ContentResolver contentResolver = context.getContentResolver();

        String[] PROJECTION = new String[]{ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI, ContactsContract.CommonDataKinds.Phone.NUMBER};

//        String order = "upper("+ContactsContract.Contacts.DISPLAY_NAME + ") ASC";
//
//        String filter = ContactsContract.Contacts.DISPLAY_NAME + " NOT LIKE ''";

        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION,
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String id;
            String name;
            String number;
            String email;
            do {
                name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID));
                if (ContactsContract.Contacts.CONTENT_URI != null) {
                    uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(
                            id));
                    uri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                }
                friendsContactsList.add(new InviteFriend(null, name, number, InviteFriend.VIA_CONTACTS_TYPE,
                        uri, false));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        return friendsContactsList;
    }


    public static List<InviteFriend> getContactList(Context context) {

        List<InviteFriend> friendList = new ArrayList<>();

        InviteFriend inviteFriend;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    inviteFriend = new InviteFriend();
                    inviteFriend.setName(name);
                    inviteFriend.setId(id);


                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        inviteFriend.setNumber(phoneNumber);
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
                }
            }

        }

        return friendList;
    }
}
