package com.framgia.lockscreenenglishapplication;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;


/**
 * Created by superme198 on 11,April,2019
 * in LockScreenEnglishApplication.
 */
public class FloatingWidget extends Service implements View.OnClickListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        View.OnTouchListener,
        MediaListener {

    private final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    private View collapsedView;
    private View expandedView;
    private MediaPlayer mMediaPlayer;
    private WindowManager mWindowManager;
    private View mFloatingView;
    private Display mDisplay;
    private int mHeight;
    private int mWidth;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private ImageView mOpenButton;
    private ImageView mCloseWidget;
    private RelativeLayout mLayoutContainer;
    private ImageView mPlaySound;
    private ImageView mButtonCloseCollapsed;
    private Context mContext;

    @Override
    public IBinder onBind(Intent intent) {
        return new FloatingBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDisplay = getScreenSize();
        mHeight = mDisplay.getHeight();
        mWidth = mDisplay.getWidth();
        initComponents();
        initData();
        initMediaPlayer();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initComponents() {
        mContext = getApplicationContext();
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget, null);
        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;//Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;
        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);
        collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        expandedView = mFloatingView.findViewById(R.id.expanded_container);
        mButtonCloseCollapsed = mFloatingView.findViewById(R.id.close_btn);
        mLayoutContainer = mFloatingView.findViewById(R.id.root_container);
        mCloseWidget = mFloatingView.findViewById(R.id.button_close_widget);
        mOpenButton = mFloatingView.findViewById(R.id.button_open_app);
        mPlaySound = mFloatingView.findViewById(R.id.play_btn);
        mButtonCloseCollapsed.setOnClickListener(this);
        mOpenButton.setOnClickListener(this);
        mPlaySound.setOnClickListener(this);
        mLayoutContainer.setOnTouchListener(this);

    }

    public void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_open_app:
                Intent intent = new Intent(FloatingWidget.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //close the service and remove view from the view hierarchy
                stopSelf();
                break;
            case R.id.button_close_widget:
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                break;
            case R.id.close_btn:
                stopSelf();
                break;
            case R.id.play_btn:
                if (isPlaying()) {
                    stop();
                } else {
                    start();
                }
                break;
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.stop();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    private Display getScreenSize() {
        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        return display;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //remember the initial position.
                initialX = params.x;
                initialY = params.y;
                //get the touch location
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                //Calculate the X and Y coordinates of the view.
                params.x = initialX + (int) (event.getRawX() - initialTouchX);
                params.y = initialY + (int) (event.getRawY() - initialTouchY);

                System.out.println(params.x
                        + " y:" + params.y);
                //Update the layout with new X & Y coordinate
                mWindowManager.updateViewLayout(mFloatingView, params);
                return true;
            case MotionEvent.ACTION_UP:
                int Xdiff = (int) (event.getRawX() - initialTouchX);
                int Ydiff = (int) (event.getRawY() - initialTouchY);

                //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                //So that is click event.
                if (Xdiff < 10 && Ydiff < 10) {
                    if (isViewCollapsed()) {
                        //When user clicks on the image view of the collapsed layout,
                        //visibility of the collapsed layout will be changed to "View.GONE"
                        //and expanded view will become visible.
                        collapsedView.setVisibility(View.GONE);
                        expandedView.setVisibility(View.VISIBLE);
//                                params.x = (mWidth - expandedView.getMeasuredWidth())/2;
//                                params.y = (mHeight - expandedView.getMeasuredHeight())/2;
//                                System.out.println(params.x
//                                        + " y:" + params.y);
//                                mWindowManager.updateViewLayout(mFloatingView, params);
                    }
                }
                return true;
        }
        return false;
    }

    public void initMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

        }
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.reset();
//        mMediaPlayer.prepareAsync();
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }


    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    @Override
    public void start() {
        if (mMediaPlayer != null) {
            play("");
        }
    }

    @Override
    public void play(String uriString) {
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.notification_sound);
        mMediaPlayer.setVolume(50,50);

        mMediaPlayer.start();
    }

    private void destroyMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public class FloatingBinder extends Binder {

    }
}
