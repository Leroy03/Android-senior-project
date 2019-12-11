package com.example.shoes;

import android.app.Application;
import android.view.WindowManager;

public class MyApplication extends Application {
    /**
     * 創建全局變數
     * 註意在AndroidManifest.xml中的Application節點添加android:name=".MyApplication"屬性
     *
     */
    private WindowManager.LayoutParams wmParams=new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getMywmParams(){
        return wmParams;
    }
}
