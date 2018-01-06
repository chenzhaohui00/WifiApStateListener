package com.test.mytest.util.net_status;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.test.mytest.util.WifiUtil;

/**
 * 创建时间：2018/1/4;
 * 编写人：陈兆辉
 * 功能描述：连接wifi状态
 */
public class ConnectedWifiState extends NetStatus {
    private WifiUtil util;
    private Context appContext;

    private volatile static ConnectedWifiState instance;
    private ConnectedWifiState(Context context/*, WifiUtil wifiUtil*/){
        super(context);
        appContext = context.getApplicationContext();
//        util = wifiUtil;
    }

    public static ConnectedWifiState getInstance(Context context/*, WifiUtil wifiUtil*/){
        if(instance == null){
            synchronized (ConnectedWifiState.class){
                if(instance == null){
                    instance = new ConnectedWifiState(context/*, wifiUtil*/);
                }
            }
        }
        return instance;
    }

    /**
     * 开启wifi，无需操作和修改状态
     */
    @Override
    public boolean openWifi() {
        return true;
    }

    /**
     * 获取ip地址
     * @return
     */
    @Override
    public String getIpAddress() {
        WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        int address = wifiManager.getDhcpInfo().ipAddress;
        return intToIp(address);
    }

    /**
     * 开启AP，成功则通知状态变更 -> 热点状态
     * @param ssid 热点名称
     * @param pwd 热点密码
     * @param isOpen 是否为开放热点
     * @return
     */
    @Override
    public boolean openAp(String ssid, String pwd, boolean isOpen) {
        closeWifi();
        return super.openAp(ssid,pwd,isOpen);
    }

    /**
     * 关闭热点，无需操作和修改状态
     * @return
     */
    @Override
    public boolean closeAp() {
        return true;
    }
}
