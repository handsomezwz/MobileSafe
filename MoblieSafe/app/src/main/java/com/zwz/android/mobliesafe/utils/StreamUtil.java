package com.zwz.android.mobliesafe.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Created by 伟洲 on 2016/4/4.
 */
public class StreamUtil {
    /**
     * 将流信息转化成字符串
     */
    public static String parserStreamUtil(InputStream inputStream)throws IOException{
        //字符流，字节流转化成字符流
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        //写入流
        StringWriter stringWriter = new StringWriter();
        /**
         * 读写操作
         */
        //数据缓冲区
        String str = null;
        while ((str = br.readLine())!=null){
            //写入操作
            stringWriter.write(str);
        }
        //关流
        stringWriter.close();
        br.close();
        return stringWriter.toString();
    }
}
