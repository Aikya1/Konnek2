package com.aikya.konnek2.utils;


import android.annotation.SuppressLint;

import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 15-10-2017.
 */

public class AppConstant {


    public static final String USER_PASSWORD = "aikya@123";
    public static String APP_NAME = "Konnek2";

    public static String LOGIN_TYPE_FIREBASE = "FIREBASE";
    public static String LOGIN_TYPE_MANUAL = "MANUAL";
    public static String LOGIN_TYPE_FACEBOOK = "FACEBOOK";
    public static String LOGIN_TYPE_GMAIL = "GMAIL";

    //Tab Header
    public static String TAB_ONE = "Tab 1";
    public static String TAB_TWO = "Tab 2";
    public static String TAB_THREE = "Tab 3";
    public static String TAB_CALL_HISTORY = "CALL HISTORY";
    public static String TAB_CHAT = "CHAT";
    public static String TAB_CONTACTS = "CONTACTS";
    public static String TAB_CONTACTS_KONNEK2_USERS = "Konnek2 Users ";
    public static String TAB_CONTACTS_NON_KONNEK2 = " Others";
    public static String TAB_CONTACTS_ONLINE_KONNEK2 = "  Online Users";

    public static String GOCHAT_TAB_ONE = "GOCHAT_CONTACTS";
    public static String GOCHAT_TAB_TWO = "GOCHAT_CHATS";
    public static String GOCHAT_TAB_THREE = "GOCHAT_CALLS";


    // TabView Title
    public static String HOME = "Home";
    public static String CONTECT = "Contact";
    public static String CONNECT = "Connect";
    public static String GREATER_THAN = " > ";
    public static String CHARITY = "Charity";
    public static String ADMIN = "Admin";
    public static String SARAI = "Sarai";
    public static String HANGOUTS = "Hangouts";
    public static String M_STORE = "m-Store";
    public static String MONEY = "Money";
    public static String HISTORY = "History";
    public static String TRAVEL = "Travel";
    public static String SUB_TITLE_ONE = "chat bot";
    public static String SUB_TITLE_TWO = "chat & call";

    public static String GOCHAT = "GoChat";
    public static String USER_PROFILE = "User Profile";
    public static String ADMIN_NAME = "admin name";
    public static String OTP_RECEIVED_BROADCAST_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    // Splash screen
    public static int SPLASH_TIME = 3000;


    // Toast Messgae
    public static String TOAST_MESSAGE = "In progress";
    public static String TOAST_MESSAGE_COMMING_SOON = "Coming Soon";
    public static String TOAST_DATE_OF_BIRTH = "Enter the valid Data of Birth";
    public static String TOAST_PROFILE_IMAGE_FAILURER = "Profile set failure";
    public static String TOAST_FILE_FORMAT_NOT_SUPPORT = "File Format not Supported";
    public static String TOAST_NO_INTERNET_CONNECTION = "No InterNet Connection";
    public static String TOAST_LOGIN_CHAT_ERROR = "Login Chat error. Please relogin";
    public static String TOAST_VALID_OTP = "Enter Valid OTP";
    public static String TOAST_USER_NAME = "Enter the User Name";
    public static String TOAST_VALID_MOBILE_NUMBER = "Enter 10 digits Valid Mobile Number";


    // Admin Process
    public static String ADMIN_CONTACTS = "admin_contacts";
    public static String ADMIN_CONNECT = "admin_connect";
    public static String ADMIN_CHARITY = "admin_charity";
    public static String ADMIN_M_STORE = "admin_m_store";
    public static String ADMIN_SARAI = "admin_sarai";
    public static String ADMIN_HANGOUT = "admin_hangout";
    public static String ADMIN_STATUS_ZERO = "0";
    public static String ADMIN_STATUS_ONE = "1";
    public static String ADMIN_STATUS = "0";
    public static String ADMIN_ACCESS_STATUS = "You are not authorized to access this service";

    public static final String CHAT_BOT_URL = "http://avivglobaltech.azurewebsites.net/";
    public static String RESPONSE_CODE = "0";
    public static String TAB_POSITION = "100";

    // Preference
    public static final String SIGN_PREFERENCE = "sign_preference";
    public static final String PROFILE_PREFERENCE = "profile_preference";
    public static final String ADMIN_PREFERENCE = "admin_preference";
    public static final String QUICKBLOX_PREFERENCE = "quickblox_preference";
    public static final String IS_ADMIN = "is_admin";

    //Refer Friend Message
    public static String REFER_FRIEND_MESSGAE = "Hi I am using Konnek2 app please join with me";
    public static String PLAY_STORE_LINK = "\n App Link:" + "https://play.google.com/store/apps";
    public static String REFER_FRIEND_ACTION_TYPE = "text/plain";

    // CllHistory

    public static String CALL_VIDEO = "call_video";
    public static String CALL_AUDIO = "call_audio";
    public static String CALL_PRIORITY = "call_priority";
    public static String CALL_STATUS_RECEIVED = "call_received";
    public static String CALL_STATUS_REJECTED = "call_rejected";
    public static String CALL_PRIORITY_HIGH = "High Priority";
    public static String CALL_PRIORITY_MEDIUM = "Medium Priority";
    public static String CALL_STATUS_DIALED = "call_dialed";


    @SuppressLint("SdCardPath")
    public static String DB_FULL_PATH = "/data/data/com.aviv.konnek2/databases/";
    public static String DB_NAME = "groupchatwebrtcDB";
    public static String DATABASE_CALL_NAME = "calllog";


    // for Callling process

    public static String USER_ID = "user_id";
    public static String QB_USER_ID = "qb_user_id";
    public static String QB_USER = "qb_user";
    public static QBUser QB_USERS = null;
    public static String OPPONENT_ID = "opponent_id";
    public static String USER_NAME = "user_name";
    public static String DATE = "date";
    public static String TIME = "time";
    public static String STATUS = "status";
    public static String OPPONENTS = "opponent";
    public static String GROUP_CALL = "Group Call";
    public static int CHAT_CALL;
    public static int CHAT_CALL_STATUS = 1;
    public static boolean CALL_TYPE = false;
    public static String CALL_TYPES = "call_types";
    public static List<QBUser> QB_USER_LIST;
    public static String OFFLINE = "Offline";
    public static String ONLINE = "Online";
    public static String ONLINE_USER_TIRGGER = "Online_user_trigger";
    public static String PARTICIPANTS_IN_CALL = "Participants in call";
    public static String PREFERENCE_DIALOG_ID = "quickblox_dialog_id";
    public static List<String> CONTACT_USERS_LIST = new ArrayList<>();
    public static List<String> CONTACT_QM_USERS_LIST = null;

    // BroadcastReceiver
    public static String NOTIFY_CALL_TRIGGER = "notify_call_trigger";
    public static QBRTCSession EXTRA_SESSION = null;

    public static String CALL_DURATION_NAME = "call_duration_name";
    public static String CALL_DURATION_DATE = "call_duration_date";
    public static String CALL_DURATION_TIME = "call_duration_time";
    public static String QB_USER_NAME = "qb_user_name";

    //QuickBlox Custom Objects
    public static String PROFILE_CLASS_NAME = "Profile";

}
