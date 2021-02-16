package com.sevenlayer.tictactoe;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;
import timber.log.Timber;

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
public class AppDelegate extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {
    private static AppDelegate sInstance;

    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    public static AppDelegate getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        sInstance = this;

        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            Timber.d("The app is in foreground");
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations();

        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            Timber.d("The app is in background");
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
