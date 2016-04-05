package com.zwz.android.mobliesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zwz.android.mobliesafe.utils.StreamUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends Activity {
    private static final int MSG_ENTER_HOME_ACTIVITY = 2;
    private static final int MSG_SERVER_ERROR = 3;
    private static final int MSG_IO_ERROR = 4;
    private static final int MSG_JSON_ERROR = 5;
    //进入Splash界面的时间
    private int startTime;
    //连接到网络的时间
    private int endTime;
    private TextView splash_tv;
    private static final int MSG_UPDATE_DIALOG = 1;
    private TextView luncher_tv;
    private String code;
    private String apkurl;
    private String des;

    //主线程的Handler
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_DIALOG:
                    //弹出对话框
                    showTheDialog();
                    break;
                case MSG_ENTER_HOME_ACTIVITY:
                    enterHome();
                    break;
                case MSG_SERVER_ERROR:
                    Toast.makeText(getApplicationContext(), "服务器异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MSG_IO_ERROR:
                    Toast.makeText(getApplicationContext(), "亲，网络没有连接", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MSG_JSON_ERROR:
                    Toast.makeText(getApplicationContext(), "错误号:"+MSG_JSON_ERROR, Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
            }
        }
    };

    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startTime = (int) System.currentTimeMillis();

        //写入版本号
        luncher_tv = (TextView) findViewById(R.id.luncher_tv);
        luncher_tv.setText("版本号:" + getVersionName());
        //进度显示TextView
        splash_tv = (TextView) findViewById(R.id.splash_tv);

        //版本检查
        update();
    }

    //onActivityResult


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterHome();
    }

    /**
     * 1.弹出对话框
     */
    private void showTheDialog() {
        //在使用跟对话框祥光的操作的时候必须使用Activity.this
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
                download();
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

    /**
     * 3.下载更新
     * 利用框架吧
     */
    private void download() {
        HttpUtils httpUtils = new HttpUtils();
        //判断SD卡是否挂载
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            /**
             * url:新版本下载的路径-->apkurl
             * target：保存新版本的目录
             * callback：RequestCallBack
             */
            httpUtils.download(apkurl, "/mnt/sdcard/app-debug.apk", new RequestCallBack<File>() {
                //下载成功调用的方法
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //4.安装最新版本
                    installApk();
                }

                //下载失败调用的方法
                @Override
                public void onFailure(HttpException e, String s) {
                    Log.d("onFailure", "失败了");
                }

                /*显示当前下载进度操作
                total:下载总进度
                current:下载的当前进度
                isUploading:是否支持断点续传*/
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    //设置显示下载进度的textview可见，同事设置相应的下载进度
                    Log.d("onLoding", "执行了");
                    splash_tv.setVisibility(View.VISIBLE);
                    splash_tv.setText(current + "/" + total);
                }
            });
        }
    }

    /**
     * 4.安装最新版本
     * <intent-filter>
     * <action android:name="android.intent.action.VIEW" />
     * <category android:name="android.intent.category.DEFAULT" />
     * <data android:scheme="content" /> //content : 从内容提供者中获取数据  content://
     * <data android:scheme="file" /> // file : 从文件中获取数据
     * <data android:mimeType="application/vnd.android.package-archive" />
     * </intent-filter>
     */
    private void installApk() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        /*
        intent.setData(Uri.fromFile(new File("/mnt/sdcard/app-debug.apk")));
        intent.setType("application/vnd.android.package-archive");
        这样写会相互覆盖，要使用intent.setDataAndType()*/
        intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/app-debug.apk")),
                "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    /**
     * 进入主页
     */
    private void enterHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        //移除splash界面
        finish();
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

                        Log.d("SplashActivity", "code:" + code + " apkurl:" + apkurl + " des:" + des);
                        //1.2查看是否有最新版本
                        //判断服务器返回的新版本版本号和当前应用程序的版本号是否一致，不一致表示有最新版本
                        if (code.equals(getVersionName())) {
                            //没有新版本
                            message.what = MSG_ENTER_HOME_ACTIVITY;

                        } else {
                            //有新版本
                            //2.弹出对话框，提醒用户更新版本
                            message = Message.obtain();
                            //消息标识
                            message.what = MSG_UPDATE_DIALOG;
                        }

                    } else {
                        //链接失败
                        message.what = MSG_SERVER_ERROR;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    message.what = MSG_IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    message.what = MSG_JSON_ERROR;
                } finally {
                    int costTime = endTime - startTime;
                    //处理链接外网链接时间的问题
                    if (costTime < 2000) {
                        //睡2秒,Thread.sleep(2000);虽然效果是一样的，但是下面那个时间精准
                        SystemClock.sleep(2000 - costTime);//始终都是睡2秒钟时间
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
