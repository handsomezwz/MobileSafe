package com.zwz.android.mobliesafe;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.zwz.android.mobliesafe.utils.StreamUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends Activity {

    private TextView luncher_tv;
    private String code;
    private String apkurl;
    private String des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //写入版本号
        luncher_tv = (TextView) findViewById(R.id.luncher_tv);
        luncher_tv.setText("版本号:" + getVersionName());

        //版本检查
        update();
    }


    /**
     * 提醒用户更新版本
     */
    private void update() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL("http://10.0.2.2:8080/updateinfo.html");
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setConnectTimeout(5000);
                    //urlConn.setReadTimeout(5000);
                    urlConn.setRequestMethod("GET");
                    int responseCode = urlConn.getResponseCode();
                    if (responseCode == 200) {
                        //链接成功,获取服务器返回的数据：
                        // code：新版本的版本号，apkurl：新版本的下载路径，des：描述信息
                        //获取数据之前，服务器是如何封装数据，json,xml-->比较安全
                        Log.d("SplashActivity", "链接成功");
                        //获取服务器返回的数据
                        InputStream inputStream = urlConn.getInputStream();
                        //将获取到的流信息转化成字符串，通过工具类进行操作读写
                        String json = StreamUtil.parserStreamUtil(inputStream);
                        //解析json数据
                        JSONObject jsonObject = new JSONObject(json);
                        //获取数据
                        code = jsonObject.getString("code");
                        apkurl = jsonObject.getString("apkurl");
                        des = jsonObject.getString("des");

                        Log.d("SplashActivity","code:"+code+"apkurl:"+apkurl+"des:"+des);

                    } else {
                        //链接失败
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取版本号
     *
     * @return
     */
    private String getVersionName() {
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
