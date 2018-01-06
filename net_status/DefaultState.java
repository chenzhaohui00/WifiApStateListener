package com.test.mytest.util.net_status;

import android.content.Context;

/**
 * 创建时间：2018/1/4;
 * 编写人：陈兆辉
 * 功能描述：默认状态，即未开启和连接任何wifi/热点。
 */
public class DefaultState extends NetStatus {

    private static volatile DefaultState instance;
    private DefaultState(Context context) {
        super(context);
    }
    public static DefaultState getInstance(Context context){
        if(instance == null){
            synchronized (DefaultState.class){
                if(instance == null){
                    instance = new DefaultState(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取ip地址，直接返回null
     */
    @Override
    public String getIpAddress() {
        return null;
    }

    /**
     * 获取热点ip地址，直接返回null
     */
    @Override
    public String getApAddress() {
        return null;
    }

    /**
     * 热点是否开启，直接返回false
     */
    @Override
    public boolean isApOpened() {
        return false;
    }

    /**
     * 关闭热点，目前是未开启状态，直接返回true。
     */
    @Override
    public boolean closeAp() {
        return true;
    }

}
