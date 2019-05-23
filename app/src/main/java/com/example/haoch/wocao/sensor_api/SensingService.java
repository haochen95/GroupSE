package com.example.haoch.wocao.sensor_api;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.sensingkit.sensingkitlib.SKException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by haoch on 2017/12/26.
 */

public class SensingService extends Service {



    @SuppressWarnings("unused")
    protected static final String TAG = "SensingService";

    public enum SensingServiceStatus {
        Stopped,
        Start,
    }

    private final IBinder mBinder = new LocalBinder();
    private PowerManager.WakeLock mWakeLock;  // when the screen shut down, I can still collect data
    // Sensing Session
    private SensingSession mSensingSession;
    private SensingServiceStatus mStatus = SensingServiceStatus.Stopped;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {  // create a binder to communicate with main activity (to show results)
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.startSensing();  //唯一的多余语句
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        // Set the status

        try {
            mSensingSession.stop();
            mSensingSession.close();

        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

//        this.stopSensing();
        super.onDestroy();
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SensingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SensingService.this;
        }
    }


    public SensingSession createSensingSession() {   // register sensor and use local time as folder name to create a folder

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.KOREA);
        String folderName = dateFormat.format(new Date());

        sendMessageToBLE(folderName);

        SensingSession session;

        try {
            session = new SensingSession(this, folderName);
        }
        catch (SKException ex) {
            Log.e(TAG, ex.getMessage());
            session = null;
        }

        return session;
    }

    // --- Wake Lock

    private void acquireWakeLock() {    // Service still run the smartphone's light shut down
        if ((mWakeLock == null) || (!mWakeLock.isHeld())) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
            mWakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    public void startSensing() {

        // Set the status
        mStatus = SensingServiceStatus.Start;

        if (mSensingSession != null) {
            Log.e(TAG, "Sensing Session is already created!");
        }

        mSensingSession = createSensingSession();

        try {
            acquireWakeLock();
            mSensingSession.start();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

    }

    public void stopSensing() {

        // Set the status
        mStatus = SensingServiceStatus.Stopped;

        try {

            if (mSensingSession.isSensing()) {
                mSensingSession.stop();
            }

            mSensingSession.close();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

        releaseWakeLock();

        mSensingSession = null;
    }

    private void sendMessageToBLE(String foldername){
        Intent intent = new Intent("foldername");
        intent.putExtra("get_folder_name", foldername);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

