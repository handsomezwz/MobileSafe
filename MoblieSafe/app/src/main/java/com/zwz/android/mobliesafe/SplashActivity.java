package com.zwz.android.mobliesafe;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private TextView luncher_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        luncher_tv = (TextView) findViewById(R.id.luncher_tv);

        luncher_tv.setText("版本号:"+getVersionName());


    }

    private String getVersionName(){
        PackageManager pm = getPackageManager();
        try {
            //注意了这里，要返回一个字符串是要在try里面返回。
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
