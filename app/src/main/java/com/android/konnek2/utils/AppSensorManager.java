package com.android.konnek2.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;

/**
 * Created by Lenovo on 01-11-2017.
 */

public class AppSensorManager implements SensorEventListener {

    Context context;
    SensorManager sensorManager;
    Sensor sensor;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    int field = 0x00000020;

    public AppSensorManager(Context context) {
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float distance = event.values[0];
        try {
            if (Math.round(distance) <= 3) {
                if (!wakeLock.isHeld()) {
                    wakeLock.acquire();
                }
            } else {

                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void startSensor() {

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        try {
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }

        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
// wakeLock = powerManager.newWakeLock(field, context.getLocalClassName());
        wakeLock = powerManager.newWakeLock(field, "SensorMger");

        try {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopSensor() {

        try {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
            if (sensorManager != null)
                sensorManager.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
