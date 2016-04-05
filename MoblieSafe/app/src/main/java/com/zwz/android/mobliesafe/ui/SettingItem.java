package com.zwz.android.mobliesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by 伟洲 on 2016/4/6.
 */
public class SettingItem extends RelativeLayout {
    //在代码中使用的时候调用
    public SettingItem(Context context) {
        super(context);
        init();
    }

    //在布局文件中使用的时候调用
    public SettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //在布局文件中使用的时候调用，添加了样式
    public SettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 添加控件用的
     */
    private void init(){

    }
}
