package com.zwz.android.mobliesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 伟洲 on 2016/4/6.
 */
public class HomeTextView extends TextView{
    public HomeTextView(Context context) {
        super(context);
    }
    public HomeTextView(Context context,AttributeSet attrs){
        super(context,attrs);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
