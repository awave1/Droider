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
    private Notification notification;
    private SharedPreferences sp;
    private Timer myTimer = new Timer();
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate();
        notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
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
                }, NewPush(Interval()), NewPush(Interval()));
                Log.d("NOTIFY", "Repeat in " + Interval() + "hour");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private byte Interval() {
        if (Byte.parseByte(sp.getString("interval", "3")) == 3)
            return 3;
        else if (Byte.parseByte(sp.getString("interval", "6")) == 6)
            return 6;
        else if (Byte.parseByte(sp.getString("interval", "12")) == 12)
            return 12;
        else if (Byte.parseByte(sp.getString("interval", "24")) == 24)
            return 24;
        else
            return 1;
    }

    private boolean isPushEnabled() {
        return sp.getBoolean("notify", false);
    }

    private long NewPush(long time) {
        return (time * 1000 * 60 * 60);
    }  //time * 1000 * 60 * 60 = time часов

    private void Notify() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent notifyIntent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent contextIntent = PendingIntent.getActivity(getApplicationContext(), 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                Resources res = getApplicationContext().getResources();
                Notification.Builder builder = new Notification.Builder(getApplicationContext());

                builder.setContentIntent(contextIntent)
                        .setSmallIcon(R.drawable.ic_stat_dr)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_large_notify))
                        .setTicker("Droider")
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentTitle("Новые статьи")
                        .setContentText("Проверь, что нового?");

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    notification = builder.getNotification();
                } else {
                    Notification.InboxStyle inboxStyle = new Notification.InboxStyle();

                    inboxStyle.setBigContentTitle("");
                    inboxStyle.addLine("Привет, не хочешь ли узнать, что нового");
                    inboxStyle.addLine(" в этом технологическом мире?");


                    builder.setStyle(inboxStyle);
                    notification = builder.build();
                }
                notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;

                Log.d("Notify ", "Сработало");
                notificationManager.notify(NOTIFY_ID, notification);
            }
        }).start();
    }
}
