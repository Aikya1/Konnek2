package com.aikya.konnek2.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.models.UserCustomData;
import com.aikya.konnek2.call.core.utils.Utils;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.utils.helpers.ServiceManager;
import com.quickblox.users.model.QBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observer;

public class OnlineService extends Service {
    private Timer timer;
    private TimerTask timerTask;
    private int userId;

    private QMUser qmUser;
    private QBUser qbUser;

    ServiceManager serviceManager;

    public OnlineService(Context ctx) {
        super();

    }


    public OnlineService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    private void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        //  timer.schedule(timerTask, 1000, 1000); //
        timer.schedule(timerTask, 20000, 20000); //20sec
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {

            @Override
            public void run() {
                qbUser = AppSession.getSession().getUser();

                serviceManager = ServiceManager.getInstance();

                userId = 20465;//in redmi testuser
                Date date = new Date(System.currentTimeMillis());
                qbUser.setId(qbUser.getId());
                qbUser.getCustomData();
                String jsonstring = qbUser.getCustomData();
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(jsonstring);
                    jObject.put("lastSeen", date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("json object", "jsonobject is" + jObject);
                if (jObject != null) {
                    qbUser.setCustomData(jObject.toString());
                }

                Log.d("now", "now" + qbUser);

                //  AppSession.getSession().updateUser(qbUser);

                serviceManager.updateUser(qbUser).subscribe(updateUserObserver);
                UserCustomData userCustomData = Utils.customDataToObject(qbUser.getCustomData());


            }
        };

    }


    private Observer<QBUser> updateUserObserver = new Observer<QBUser>() {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(QBUser qbUser) {



        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
