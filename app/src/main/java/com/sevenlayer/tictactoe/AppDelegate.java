package com.sevenlayer.tictactoe;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.multidex.MultiDexApplication;
import timber.log.Timber;

/**
 * The application instance delegate.
 *
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
public class AppDelegate extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {
    private static AppDelegate sInstance;

    /**
     * Activity counter.
     */
    private int activityReferences = 0;

    /**
     * Flag which indicated if the current activity is changing it's configuration.
     */
    private boolean isActivityChangingConfigurations = false;

    public static AppDelegate getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        sInstance = this;

        Timber.plant(new Timber.DebugTree());

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectCustomSlowCalls()
                    .detectResourceMismatches()
                    .detectUnbufferedIo()
                    .detectAll()
                    .penaltyLog()
                    .build());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .detectActivityLeaks()
                        .detectNonSdkApiUsage()
                        .detectLeakedRegistrationObjects()
                        .detectAll()
                        .penaltyLog()
                        .build());
            } else {
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .detectActivityLeaks()
                        .detectLeakedRegistrationObjects()
                        .detectAll()
                        .penaltyLog()
                        .build());
            }
        }

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
