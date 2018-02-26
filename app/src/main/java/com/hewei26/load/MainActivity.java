package com.hewei26.load;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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

        TextView tv = findViewById(R.id.tv);
        ImageView iv = findViewById(R.id.iv);

        //复制apk到内部目录
        AssetManager assets = this.getAssets();
        InputStream is = null;
        try {
            is = assets.open("plugin/bundle-debug.apk");
            int len = 0;
            byte buffer[] = new byte[1024];
            File file = new File(this.getExternalFilesDir("") ,"bundle.apk");
            if(file.exists()) file.delete();
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            fos.close();

            loadApk(file.getAbsolutePath());

            /**
             *  插件资源对象
             */
            Resources resources = getBundleResource(this,file.getAbsolutePath());
            /**
             *获取图片资源
             */
            Drawable drawable = resources.getDrawable(resources.getIdentifier("test", "drawable",
                    "com.hewei26.beloaded"));
            /**
             *  获取文本资源
             */
            String text = resources.getString(resources.getIdentifier("text_beload","string",
                    "com.hewei26.beloaded"));

            iv.setImageDrawable(drawable);
            tv.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载插件apk
     * @param apkPath
     */
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

    public Resources getBundleResource(Context context, String apkPath){
        AssetManager assetManager = createAssetManager(apkPath);
        return new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
    }

    private AssetManager createAssetManager(String apkPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            AssetManager.class.getDeclaredMethod("addAssetPath", String.class).invoke(
                    assetManager, apkPath);
            return assetManager;
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }
}
