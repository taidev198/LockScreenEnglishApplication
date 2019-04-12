package com.framgia.lockscreenenglishapplication;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;

/**
 * Created by superme198 on 10,April,2019
 * in LockScreenEnglishApplication.
 https://stackoverflow.com/questions/24921241/android-facebook-lock-screen-notification
 https://stackoverflow.com/questions/45396426/crash-when-using-constraintlayout-in-notification
 */
public class LockScreenNotification extends Service {
    private static final String CHANNEL_ID = "MY_CHANNEL_ID";
    private static final int ONGOING_NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_ID = 501;
    private WindowManager windowManager;
    private ImageView chatImage;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            buildNotif();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void buildNotif(){

        buildWidget();

        Intent activityIntent = new Intent( this,
                MainActivity.class );
        PendingIntent resultingActivityPendingIntent = getPendingIntent(activityIntent );

        NotificationChannel channel;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID,
                    "Primary channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setLightColor(Color.GREEN);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.enableVibration(true);
            channel.enableLights(true);
            manager.createNotificationChannel(channel);
        }


        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);
        notificationLayout.setTextViewText(R.id.title, "Hello World");
        notificationLayout.setOnClickPendingIntent(R.id.button, getPendingIntent(activityIntent));
        notificationLayoutExpanded.setTextViewText(R.id.title, "Hello World");
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.button, getPendingIntent(activityIntent));
// create notification for foreground service
        NotificationCompat.Builder m_notificationBuilder = new NotificationCompat.Builder( this );
        m_notificationBuilder.setSmallIcon( R.mipmap.ic_launcher )
                            .setTicker( "some text" )
                            .setContentIntent( resultingActivityPendingIntent )
                            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                            .setCustomContentView(notificationLayout)
                            .setCustomBigContentView(notificationLayoutExpanded);
        startForeground( ONGOING_NOTIFICATION_ID,
                m_notificationBuilder.build() );
        manager.notify(NOTIFICATION_ID, m_notificationBuilder.build());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buildWidget() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatImage = new ImageView(this);
        chatImage.setImageResource(R.drawable.ic_launcher_background);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        chatImage.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(v, params);
                        return true;
                }
                return false;
            }
        });
        windowManager.addView(chatImage, params);
    }

    private PendingIntent getPendingIntent(Intent activityIntent) {
        return PendingIntent.getService(this,1, activityIntent, 0);
    }
}
