package com.fate.u3dinject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.content.Context.CONTEXT_IGNORE_SECURITY;
import static com.fate.u3dinject.SoListActivity.exusecmd;

/**
 * Created by jy on 2018/12/17.
 */

public class HookMain implements IXposedHookLoadPackage {
    private static final String INJECT = "InjectedApp";
    private static final String TAG = "Fuck";
    private static final String LIB = "fate";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.fate.u3dinject")) {
            XposedHelpers.findAndHookMethod("com.fate.u3dinject.MainActivity", lpparam.classLoader, "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }

        Log.i(TAG, "hook-start!");
        String packageName = FileUtils.getStr("/sdcard/App");
        String soName = FileUtils.getStr("/sdcard/soName");
        if (packageName!=null&&soName!=null) {
            Log.i(TAG, "packageName and soName are not null!");
            int i = soName.indexOf(".");
            final String finalSoName = soName.substring(3, i);
            Log.e(TAG, "soName--" + finalSoName);
            Log.e(TAG, "packageName" + packageName);
            if (packageName != null && soName != null) {
                if (lpparam.packageName.equals(packageName)) {
                    Log.i(TAG, "Ok!");
                    XposedHelpers.findAndHookMethod("dalvik.system.BaseDexClassLoader", ClassLoader.getSystemClassLoader(), "findLibrary", String.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (param.args[0].toString().contains(finalSoName)) {
                                param.args[0] = LIB;
                            }
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Log.i(TAG, "加载lib" + (String) param.args[0]);

                            if (LIB.equals(param.args[0])) {
                                // param.setResult("/data/data/com.fate.avhook/lib" + "/lib" + HOOKLIB + ".so");
                                param.args[0] = LIB;
                                Log.i(TAG, "加载libsucess!");
                            }
                        }
                    });
                }
            }
        }
    }




}

