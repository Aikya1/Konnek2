package com.aikya.konnek2.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aikya.konnek2.base.activity.Profile;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.models.UserCustomData;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.core.utils.Utils;
import com.aikya.konnek2.call.services.QMUserService;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.call.services.model.QMUserCustomData;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.helpers.ServiceManager;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.helper.CustomDataObjectParserHelper;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class OnlineService extends Service
{
    private Timer timer;
    private TimerTask timerTask;
    private int userId;

    private QMUser qmUser;
    private QBUser qbUser;

    ServiceManager serviceManager;

    public OnlineService(Context ctx)
    {
        super();

    }


    public OnlineService()
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    private void startTimer()
    {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
      //  timer.schedule(timerTask, 1000, 1000); //
        timer.schedule(timerTask, 20000, 20000); //20sec
    }

    private void initializeTimerTask()
    {
        timerTask = new TimerTask()
        {

            @Override
            public void run()
            {
                qbUser= AppSession.getSession().getUser();

                serviceManager = ServiceManager.getInstance();


                //Log.i("in timer", "in timer ++++  "+ (counter++));

                userId=20465;//in redmi testuser

               // userId=19887;//in hptab secuser19887




              // qmUser = QMUserService.getInstance().getUserCache().get((long) userId);

              // qbUser.setLastRequestAt(new Date(System.currentTimeMillis()));
                Date date = new Date(System.currentTimeMillis());

                Log.d("data","id is "+qbUser.getId());

                qbUser.setId(qbUser.getId());
                UserCustomData userCustomData= Utils.customDataToObject(qbUser.getCustomData());
                userCustomData.setLastSeen(date.toString());
                qbUser.setCustomData(Utils.customDataToString(userCustomData));

              //  serviceManager.updateUser(qbUser).subscribe(onlineObserver);













               /*
                //---------------------

               //ServiceManager.getInstance().updateUserSync(qbUser);
               //ServiceManager.getInstance().updateUserSync(qbUser);


               //
              //  qbUser.getCustomDataAsObject();

               // JSONObject person =  jsonArray.getJSONObject(0).getJSONObject("person");
                //person.put("name", "Sammie");


               String m= qbUser.getCustomData();

                String name="Anand";
               JSONObject jsonObject= (JSONObject) qbUser.getCustomDataAsObject();
                try {
                    jsonObject.put("lastSeen",name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //-------------------------------
                               AppSession.getSession().updateUser(qbUser);

               */



              //  ToastUtils.shortToast("Running");

                Log.i("in timer","Running");
            }
        };

    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
