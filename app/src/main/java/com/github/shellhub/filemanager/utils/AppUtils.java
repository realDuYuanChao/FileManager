package com.github.shellhub.filemanager.utils;

import android.annotation.SuppressLint;
import android.app.Application;

import java.lang.reflect.InvocationTargetException;

public class AppUtils {
    private static Application mApplication;

    @SuppressLint("PrivateApi")
    public static Application getApp() {
        if (mApplication != null) return mApplication;

        try {
            mApplication = (Application) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return mApplication;
    }
}