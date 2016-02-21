package com.awave.apps.droider.Main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.awave.apps.droider.R;

import java.util.Timer;
import java.util.TimerTask;

public class NotifyService extends Service {
    private static final int NOTIFY_ID = 101;
    Notification notification;
    SharedPreferences sp;
    Timer myTimer = new Timer();

    @Override
    public void onCreate() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isPushEnabled()) {
            try {
                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Notify();
                    }
                },NewPush(Interval()), NewPush(Interval()));
                Log.d("NOTIFY", "Repeat in " + Interval() + "hour");
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return START_REDELIVER_INTENT;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public byte Interval() {
        if (Byte.parseByte(sp.getString("interval", "3")) == 3)
            return 3;
        else if (Byte.parseByte(sp.getString("interval", "6")) == 6)
            return  6;
        else if (Byte.parseByte(sp.getString("interval", "12")) == 12)
            return 12;
        else if (Byte.parseByte(sp.getString("interval", "24")) == 24)
            return 24;
        else
            return 1;
    }

    public boolean isPushEnabled() {
        return sp.getBoolean("notify", true);
    }
    public long NewPush(long time) {
        return  (time*1000*60*60);
    }

    private long minutePush(long time){
        return time * 1000*60;
    }

    public void Notify() {
        Intent notifyIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent contextIntent = PendingIntent.getActivity(getApplicationContext(), 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = getApplicationContext().getResources();
        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        builder.setContentIntent(contextIntent)
                .setSmallIcon(R.drawable.ic_stat_dr)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setTicker("Droider")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Droider")
                .setContentText("Проверь, что нового?")
                .setDefaults(Notification.FLAG_SHOW_LIGHTS);



        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            notification  = builder.getNotification();
        }
        else {
            Notification.InboxStyle inboxStyle = new Notification.InboxStyle();

            inboxStyle.setBigContentTitle("Новые статьи");
            inboxStyle.addLine("Привет, не хочешь ли узнать, что нового");
            inboxStyle.addLine(" в этом технологическом мире?");


            builder.setStyle(inboxStyle);
            notification = builder.build();
        }

        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d("Notify ", "Сработало");
        notificationManager.notify(NOTIFY_ID, notification);
    }
}
