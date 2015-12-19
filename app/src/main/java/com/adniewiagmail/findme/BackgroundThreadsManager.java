package com.adniewiagmail.findme;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adaś on 2015-11-29.
 */
public class BackgroundThreadsManager {

    public static final int defaultThreadUpdateInterval = 30 * 1000;
    public static final int mapUpdateInterval = 5 * 1000;
    private static List<TerminatableThread> threads = new ArrayList<TerminatableThread>();

    public static void startThread(TerminatableThread thread) {
        Log.d("BACKGROUND_THREAD", "Strating new thread: " + thread.getClass().getName());
        thread.start();
        threads.add(thread);
    }

    public static void stopAll() {
        Log.d("BACKGROUND_THREAD", "Stoping background threads: " + threads.size() + " threads");
        for (TerminatableThread thread : threads) {
            thread.terminate();
        }
    }

    public static void stopThread(TerminatableThread thread) {
        Log.d("BACKGROUND_THREAD", "Stopping thread: " + thread.getClass().getName());
        thread.terminate();
        threads.remove(thread);
    }

}
