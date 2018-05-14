package com.aikya.konnek2.base.activity;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.models.UserCustomData;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.core.utils.UserFriendUtils;
import com.aikya.konnek2.call.core.utils.Utils;
import com.aikya.konnek2.call.db.utils.ErrorUtils;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.service.OnlineService;
import com.aikya.konnek2.ui.activities.authorization.LandingActivity;
import com.aikya.konnek2.ui.activities.catchup.CatchUpActivity;
import com.aikya.konnek2.ui.activities.base.BaseLoggableActivity;
import com.aikya.konnek2.ui.activities.base.BaseLoggableActivity;
import com.aikya.konnek2.ui.activities.main.MainActivity;
import com.aikya.konnek2.ui.activities.settings.SettingsActivity;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.helpers.FacebookHelper;
import com.aikya.konnek2.utils.helpers.SystemPermissionHelper;
import com.aikya.konnek2.utils.listeners.AppCommon;
import com.aikya.konnek2.R;
import com.aikya.konnek2.ui.activities.profile.MyProfileActivity;
import com.aikya.konnek2.ui.adapters.base.AppHomeAdapter;
import com.aikya.konnek2.ui.fragments.dialogs.base.TwoButtonsDialogFragment;
import com.aikya.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.aikya.konnek2.utils.AppConstant;
import com.aikya.konnek2.utils.helpers.FirebaseAuthHelper;
import com.aikya.konnek2.utils.helpers.ServiceManager;
import com.aikya.konnek2.utils.image.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import rx.Subscriber;


public class AppHomeActivity extends BaseLoggableActivity implements NavigationView.OnNavigationItemSelectedListener {

    //commented
    //Comment by rajeev ..

    GridView gridView;
    Toolbar toolbar;
    public String[] title;
    String[] subtitle = new String[10];
    AppHomeAdapter appHomeAdapter;
    private DrawerLayout drawerLayout;
    RoundedImageView profileImage;
    TypedArray imageId;
    //    private AppFindUserPresenter appFindUserPresenter;
    private ProgressBar progressBar;
    private FacebookHelper facebookHelper;
    private FirebaseAuthHelper firebaseAuthHelper;
    public static final int REQUEST_CODE_LOGOUT = 300;
    private QMUser user;
    ServiceManager serviceManager;
    private UserCustomData userCustomData;
    private SystemPermissionHelper systemPermissionHelper;


    private OnlineService onlineService;
    Intent onlineServiceIntent;


    public static void start(Context context) {
        Intent intent = new Intent(context, AppHomeActivity.class);
        context.startActivity(intent);
    }

    public static void startForResult(Fragment fragment) {
        Intent intent = new Intent(fragment.getActivity(), AppHomeActivity.class);
        fragment.getActivity().startActivityForResult(intent, REQUEST_CODE_LOGOUT);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (systemPermissionHelper.isContactPermissionGranted()) {
            if (serviceManager.friendList == null) {
                serviceManager.uploadAllContacts(this);
            }

        } else {
            systemPermissionHelper.requestPermissionsReadContacts();
//            ToastUtils.shortToast("Please grant permission to synch contacts.");
            Toast.makeText(this, "Please grant permission to synch contacts.", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_app_home;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_home);
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        systemPermissionHelper = new SystemPermissionHelper(this);
        initViews();
        imageId = getResources().obtainTypedArray(R.array.home_image); // String  Values  from resource  files
        title = getResources().getStringArray(R.array.home_title);          //  Image  Values  from resource  files
//        subtitle[6] = AppConstant.SUB_TITLE_ONE;
//        subtitle[2] = AppConstant.SUB_TITLE_TWO;

        gridView = findViewById(R.id.home_rid);
        appHomeAdapter = new AppHomeAdapter(AppHomeActivity.this, title, subtitle, imageId);
        gridView.setAdapter(appHomeAdapter);
        serviceManager = ServiceManager.getInstance();


        onlineService = new OnlineService();
        onlineServiceIntent = new Intent(this, onlineService.getClass());
        if (!isMyServiceRunning(onlineService.getClass())) {
            startService(onlineServiceIntent);
        }


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent goToContact = new Intent(getApplicationContext(), AppContactActivity.class);
                        goToContact.putExtra(AppConstant.TAB_POSITION, 2);
                        startActivity(goToContact);
                        break;

                    case 1:
                        Intent goTochat = new Intent(getApplicationContext(), MainActivity.class);
                        goTochat.putExtra(AppConstant.TAB_POSITION, 1);
                        startActivity(goTochat);
                        break;

                    case 2:
                        Intent goToNavigation = new Intent(getApplicationContext(), AppCallHistoryActivity.class);
                        goToNavigation.putExtra(AppConstant.TAB_POSITION, 0);
                        startActivity(goToNavigation);
                        break;


                    case 3:
                        Intent goToCatchup = new Intent(getApplicationContext(), CatchUpActivity.class);
                        startActivity(goToCatchup);
                        break;

                    case 4:
                        MyProfileActivity.start(AppHomeActivity.this);
                        break;

                    case 5:
//                        InviteFriendsActivity.start(AppHomeActivity.this);
//                        ConnectActivity.start(AppHomeActivity.this);
                        SettingsActivity.start(AppHomeActivity.this);
                        break;


                    case 6:
//
//                        SettingsActivity.start(AppHomeActivity.this);
                        AppCommon.displayToast("Support");
                        break;

                  /*  case 7:
                        Intent goToChatBot = new Intent(getApplicationContext(), AppChatBotActivity.class);
                        startActivity(goToChatBot);

                        break;
                    case 7:
                        Intent goToCharity = new Intent(getApplicationContext(), CharityActivity.class);
                        startActivity(goToCharity);

                        break;
                    case 8:
                        Intent goTomStore = new Intent(getApplicationContext(), AppMobileStoreActivity.class);
                        startActivity(goTomStore);
                        break;

                    case 9:

                        Intent goToHangouts = new Intent(getApplicationContext(), HangoutActivity.class);
                        startActivity(goToHangouts);

                        break;*/

                    default:
                        break;
                }

            }
        });

        if (!isChatInitializedAndUserLoggedIn()) {
            loginChat();
        }

        addDialogsAction();

        /*+++++++++++++++++++++TEST CODE ++++++++++++++++*//*
        if (  serviceManager.friendList == null) {
            serviceManager.uploadAllContacts(this);
        }*/
    }


    private boolean isMyServiceRunning(Class<? extends OnlineService> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        return false;
    }

    private void addDialogsAction() {
        addAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION, new LoginChatCompositeSuccessAction());
        addAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION, new LoadChatsSuccessAction());
    }

    private void removeDialogsAction() {
        removeAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION);
        removeAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION);
    }

    @Override
    protected void performLoginChatSuccessAction(Bundle bundle) {
        super.performLoginChatSuccessAction(bundle);
    }

    public void initViews() {
//        appFindUserPresenter = new AppFindUserPresenter(this);
        facebookHelper = new FacebookHelper(this);
        firebaseAuthHelper = new FirebaseAuthHelper();
        progressBar = findViewById(R.id.progress_home);
        getSupportActionBar().setSubtitle(AppConstant.HOME);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_app_navigation_drawer_icon);
        drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        Menu menuNav = navigationView.getMenu();
        MenuItem nav_text_logout = menuNav.findItem(R.id.delete);
        SpannableString s = new SpannableString(nav_text_logout.getTitle());
//        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        nav_text_logout.setTitle(s);
        View header = navigationView.getHeaderView(0);
        TextView tv_name = header.findViewById(R.id.tv_email_navigation);
        TextView tv_mobile = header.findViewById(R.id.tv_mobile_navigation);
        profileImage = header.findViewById(R.id.navigation_profile_image);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        user = UserFriendUtils.createLocalUser(AppSession.getSession().getUser());

        userCustomData = Utils.customDataToObject(user.getCustomData());

        if (user != null) {
            tv_name.setText(user.getFullName());
            if (userCustomData.getCountryCode() != null || !TextUtils.isEmpty(userCustomData.getCountryCode())) {
                String title = userCustomData.getCountryCode() + " " + user.getPhone();
                tv_mobile.setText(title);
            } else {
                tv_mobile.setText(user.getPhone());
            }
            showUserAvatar();
        } else {
            tv_name.setText("");
            tv_mobile.setText("");
        }

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();


        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logouts:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                drawerLayout.closeDrawers();
                break;

            case R.id.profile:
                MyProfileActivity.start(AppHomeActivity.this);
                drawerLayout.closeDrawers();
                break;

            case R.id.nave_settings:
                SettingsActivity.start(AppHomeActivity.this);
                drawerLayout.closeDrawers();
                break;

      /*      case R.id.logout:
                logout();
                break;
*/
        /*    case R.id.delete:
                AppCommon.displayToast(AppConstant.TOAST_MESSAGE_COMMING_SOON);
                drawerLayout.closeDrawers();
                break;*/
        }
        DrawerLayout drawer = findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

   /* public void logout() {
        if (AppCommon.checkAvailability(AppHomeActivity.this)) {
            TwoButtonsDialogFragment
                    .show(getSupportFragmentManager(), R.string.dlg_logout, R.string.dlg_confirm,
                            new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
//                                    showProgress();
                                    progressBar.setVisibility(View.VISIBLE);
                                    facebookHelper.logout();
                                    firebaseAuthHelper.logout();

                                    ServiceManager.getInstance().logout(new Subscriber<Void>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            ErrorUtils.showError(AppHomeActivity.this, e);
//                                            hideProgress();
                                            progressBar.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onNext(Void aVoid) {
                                            setResult(RESULT_OK);
//                                            hideProgress();
                                            progressBar.setVisibility(View.GONE);
                                            Intent goToLanding = new Intent(getApplicationContext(), LandingActivity.class);
                                            startActivity(goToLanding);
                                        }
                                    });
                                }
                            });
        }
    }*/

    private void showUserAvatar() {
        ImageLoader.getInstance().displayImage(
                user.getAvatar(),
                profileImage,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
