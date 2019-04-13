package com.framgia.lockscreenenglishapplication;

/**
 * Created by superme198 on 12,April,2019
 * in LockScreenEnglishApplication.
 */
interface MediaListener {
    boolean isPlaying();

    void stop();

    void release();

    void start();

    void play(String uriString);

    void playStream(String url);

}
