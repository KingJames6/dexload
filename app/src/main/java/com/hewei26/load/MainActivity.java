package com.hewei26.load;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AssetManager assets = this.getAssets();
        InputStream is = null;
        try {
            is = assets.open("plugin/bundle-debug.apk");
            int len = 0;
            byte buffer[] = new byte[1024];
            File file = new File(this.getExternalFilesDir("") ,"bundle.apk");
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            fos.close();

            loadApk(file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadApk(String apkPath) {
        Log.v("loadDexClasses", "Dex Preparing to loadDexClasses!");
        File file = this.getExternalFilesDir("dexOpt");

        DexClassLoader dexClassLoader = new DexClassLoader(apkPath, file.getAbsolutePath(), null, this.getClassLoader());

        Log.v("loadDexClasses", "Searching for class : "                + "com.registry.Registry");


        try {
            Class<?> classLoad = dexClassLoader.loadClass("com.hewei26.beloaded.ClassTobeLoaded");
            Object instance = classLoad.newInstance();
            Method method = instance.getClass().getMethod("called");
            method.invoke(instance);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
