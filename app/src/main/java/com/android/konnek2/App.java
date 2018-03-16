package com.android.konnek2;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.android.konnek2.call.core.service.QBService;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.android.konnek2.call.core.utils.ConstsCore;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.services.QMAuthService;
import com.android.konnek2.call.services.QMUserCacheImpl;
import com.android.konnek2.call.services.QMUserService;
import com.android.konnek2.call.services.cache.QMUserCache;
import com.android.konnek2.base.db.AppCallLogTable;
import com.android.konnek2.base.db.AppDBAdapter;
import com.android.konnek2.utils.ActivityLifecycleHandler;
import com.android.konnek2.utils.AppPreference;
import com.android.konnek2.utils.StringObfuscator;
import com.android.konnek2.utils.helpers.ServiceManager;
import com.android.konnek2.utils.helpers.SharedHelper;
import com.android.konnek2.utils.image.ImageLoaderUtils;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.connections.tcp.QBTcpChatConnectionFabric;
import com.quickblox.chat.connections.tcp.QBTcpConfigurationBuilder;
import com.quickblox.core.LogLevel;
import com.quickblox.core.QBHttpConnectionConfig;
import com.quickblox.core.ServiceZone;


import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

public class App extends MultiDexApplication {

    private static final String TAG = App.class.getSimpleName();
    private static AppDBAdapter appDBAdapter;
    private static App instance;
    private SharedHelper appSharedHelper;
    private SessionListener sessionListener;
    public AppPreference appPreference;
    public static AppCallLogTable appcallLogTableDAO;


    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.i(TAG, "onCreate with update");
        instance = this;
        initFabric();
        initApplication();
        registerActivityLifecycleCallbacks(new ActivityLifecycleHandler());
        createTable();
        initFacebook();
//        appcallLogTableDAO = new AppCallLogTable(appDBAdapter.getDataBase(), instance);
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    private void initFabric() {
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                StringObfuscator.getTwitterConsumerKey(),
                StringObfuscator.getTwitterConsumerSecret());

        Fabric.with(this,
                crashlyticsKit,
                new TwitterCore(authConfig));
    }

    private void initApplication() {
        instance = this;
        appPreference = new AppPreference(getApplicationContext());
        sessionListener = new SessionListener();
        getAppSharedHelper();
        initQb();
        initDb();
        initImageLoader(this);
        initServices();

    }

    private void initQb() {
        QBSettings.getInstance().init(getApplicationContext(),
                StringObfuscator.getApplicationId(),
                StringObfuscator.getAuthKey(),
                StringObfuscator.getAuthSecret());


        QBSettings.getInstance().setAccountKey(StringObfuscator.getAccountKey());
        String chatServerDomain = QBSettings.getInstance().getChatServerDomain();
        String serverApiDomain = QBSettings.getInstance().getServerApiDomain();
        String apiEndPoint = QBSettings.getInstance().getApiEndpoint();
        String chatEndPoint = QBSettings.getInstance().getChatEndpoint();

        Log.d("APP.JAVA", "chat server domain = " + chatServerDomain);
        Log.d("APP.JAVA", "Api server domain" + serverApiDomain);
        Log.d("APP.JAVA", "Api End Point = " + apiEndPoint);
        Log.d("APP JAVA", "Chat End Point" + chatEndPoint);


        QBSettings.getInstance().setLogLevel(LogLevel.DEBUG);  // Local Level Enabled  by suresh
        initDomains();
        initHTTPConfig();

        QBTcpConfigurationBuilder configurationBuilder = new QBTcpConfigurationBuilder()
                .setAutojoinEnabled(false)
                .setSocketTimeout(0);

        QBChatService.setConnectionFabric(new QBTcpChatConnectionFabric(configurationBuilder));

        QBChatService.setDebugEnabled(true);
    }

    private void initDomains() {

        if (!TextUtils.isEmpty(getString(R.string.api_domain))) {
            QBSettings.getInstance().setEndpoints(getString(R.string.api_domain), getString(R.string.chat_domain), ServiceZone.PRODUCTION);
            QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);
        }
    }

    private void initHTTPConfig() {
        QBHttpConnectionConfig.setConnectTimeout(ConstsCore.HTTP_TIMEOUT_IN_SECONDS);
        QBHttpConnectionConfig.setReadTimeout(ConstsCore.HTTP_TIMEOUT_IN_SECONDS);
    }

    private void initDb() {
        DataManager.init(this);
    }

    private void initImageLoader(Context context) {
        ImageLoader.getInstance().init(ImageLoaderUtils.getImageLoaderConfiguration(context));
    }

    private void initServices() {
        QMAuthService.init();
        QMUserCache userCache = new QMUserCacheImpl(this);
        QMUserService.init(userCache);

        ServiceManager.getInstance();
    }

    public synchronized SharedHelper getAppSharedHelper() {
        return appSharedHelper == null
                ? appSharedHelper = new SharedHelper(this)
                : appSharedHelper;
    }

    private void createTable() {   // create Local Table by suresh

        appDBAdapter = AppDBAdapter.getInstance(instance);
        appDBAdapter.open();
        appcallLogTableDAO = new AppCallLogTable(appDBAdapter.getDataBase(), instance);

    }


}