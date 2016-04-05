package com.zwz.android.mobliesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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

    //进入Splash界面的时间
    private int startTime;
    //连接到网络的时间
    private int endTime;

    private static final int MSG_UPDATE_DIALOG = 1;
    private TextView luncher_tv;
    private String code;
    private String apkurl;
    private String des;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_DIALOG:
                    //弹出对话框
                    showTheDialog();
                    break;
            }
        }
    };

    /**
     * 弹出对话框
     */
    private void showTheDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置对话框的标题
        builder.setTitle("新版本:" + code);
        //设置对话框的图标
        builder.setIcon(R.drawable.puppy_dogs_08);
        //设置对话框的描述信息
        builder.setMessage(des);
        //设置对话框不能消失
        builder.setCancelable(false);
        //设置升级&取消的按钮
        builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //1.隐藏对话框
                dialog.dismiss();
                //2.跳转到主界面
                enterHome();
            }
        });
//        builder.create().show();与下面问题一样
        builder.show();
    }

    private void enterHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        //移除splash界面
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startTime = (int) System.currentTimeMillis();

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

            private Message message;
            //细节处理，对话框的显示时间

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
                        //获取连接成功的时间
                        endTime = (int) System.currentTimeMillis();
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

                        Log.d("SplashActivity", "code:" + code + "apkurl:" + apkurl + "des:" + des);
                        //1.2查看是否有最新版本
                        //判断服务器返回的新版本版本号和当前应用程序的版本号是否一致，不一致表示有最新版本
                        if (code.equals(getVersionName())) {
                            //没有新版本

                        } else {
                            //有新版本
                            //2.弹出对话框，提醒用户更新版本
                            message = Message.obtain();
                            //消息标识
                            message.what = MSG_UPDATE_DIALOG;
                        }

                    } else {
                        //链接失败
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    int costTime = endTime-startTime;
                    //处理链接外网链接时间的问题
                    if (costTime <2000){
                        //睡2秒,Thread.sleep(2000);虽然效果是一样的，但是下面那个时间精准
                        SystemClock.sleep(2000-costTime);//始终都是睡2秒钟时间
                    }


                    //不管有没有异常总是执行
                    //发送消息
                    handler.sendMessage(message);
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
