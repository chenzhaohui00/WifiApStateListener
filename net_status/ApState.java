package com.test.mytest.util.net_status;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * 创建时间：2018/1/4;
 * 编写人：陈兆辉
 * 功能描述：开启热点状态
 */
public class ApState extends NetStatus {
//    private NetStatusUtil wifiUtil;
    private Context appContext;
    //单例代码
    private volatile static ApState instance;
    private ApState(Context context/*, NetStatusUtil wifiUtil*/) {
        super(context);
        appContext = context.getApplicationContext();
//        this.wifiUtil = wifiUtil;
    }
    public static ApState getInstance(Context context/*, NetStatusUtil wifiUtil*/){
        if(instance == null){
            synchronized (ApState.class){
                if(instance == null){
                    instance = new ApState(context/*, wifiUtil*/);
                }
            }
        }
        return instance;
    }

    /**
     * 开启wifi，成功则变更状态为：默认状态
     * @return
     */
    @Override
    public boolean openWifi() {
        closeAp();
        return super.openWifi();
    }

    /**
     * 关闭wifi，直接返回true
     */
    @Override
    public boolean closeWifi() {
        return true;
    }

    /**
     * 是否连接着wifi，直接返回false
     */
    @Override
    public boolean isWifiEnable() {
        return false;
    }

    /**
     * 获取热点自己的ip
     */
    @Override
    public String getIpAddress() {
        WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        return intToIp(wifiManager.getDhcpInfo().serverAddress);
    }

    /**
     * 连接热点时，获取热点端的ip。直接返回null。
     */
    @Override
    public String getApAddress() {
        return null;
    }

    /**
     * 连接某一wifi，先关闭此热点
     */
    @Override
    public boolean connectWifi(String ssid, String password, int type) {
        closeAp();
        return super.connectWifi(ssid, password, type);
    }

    /**
     * 打开热点，直接返回true
     */
    @Override
    public boolean openAp(String ssid, String pwd, boolean isOpen) {
        return true;
    }

}
