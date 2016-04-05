package com.zwz.android.mobliesafe;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class HomeActivity extends Activity {

    private GridView gv_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        gv_home = (GridView) findViewById(R.id.gv_home);
        gv_home.setAdapter(new MyAdaper());
    }

    private class MyAdaper extends BaseAdapter{

        private View view;
        private ImageView iv_home_item;
        private TextView tv_home_item;

        private int[] imageId_item = {R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
        R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
        R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings};

        private String[] name_item = {"手机防盗","通讯卫士","软件管理","进程管理"
                ,"流量统计","手机杀毒","缓存清理","高级工具","设置中心"};

        @Override
        public int getCount() {
            return 9;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            view = View.inflate(getApplicationContext(), R.layout.home_item, null);
            iv_home_item = (ImageView) view.findViewById(R.id.iv_home_item);
            tv_home_item = (TextView) view.findViewById(R.id.tv_home_item);
            iv_home_item.setImageResource(imageId_item[position]);
            tv_home_item.setText(name_item[position]);
            return view;
        }
    }
}
