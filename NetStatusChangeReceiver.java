package com.test.mytest.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.test.mytest.util.net_status.ApState;
import com.test.mytest.util.net_status.ConnectedWifiState;

/**
 * 创建时间：2018/1/4;
 * 编写人：陈兆辉
 * 功能描述：网络状态变化的广播接收者
 */
public class NetStatusChangeReceiver extends BroadcastReceiver {
    private static final String TAG = NetStatusChangeReceiver.class.getSimpleName();
    private NetStatusUtil netStatusUtil;
    public NetStatusChangeReceiver(NetStatusUtil netStatusUtil){
        this.netStatusUtil = netStatusUtil;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)){
            //wifi连接上与否
            Log.d(TAG, "网络状态改变");
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(info.getState().equals(NetworkInfo.State.DISCONNECTED)){
                netStatusUtil.notifyCurrentStatus(context);
                Log.d(TAG, "wifi网络连接断开");
            } else if (info.getState().equals(NetworkInfo.State.CONNECTED)){
                WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //获取当前wifi名称
                Log.d(TAG, "连接到网络 " + wifiInfo.getSSID());
                netStatusUtil.notifyStatus(ConnectedWifiState.getInstance(context));
            }
        } else if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)){
            //便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
            int state = intent.getIntExtra("wifi_state",  0);
            Log.d(TAG, "热点开关状态：state= "+String.valueOf(state));
            if (state == 13){
                Log.d(TAG, "热点已开启");
                netStatusUtil.notifyStatus(ApState.getInstance(context));
            } else if (state == 11){
                Log.d(TAG, "热点已关闭");
                netStatusUtil.notifyCurrentStatus(context);
            } else if (state == 10){
                Log.d(TAG, "热点正在关闭");
            } else if (state == 12){
                Log.d(TAG, "热点正在开启");
            }

        } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){
            //wifi打开与否
            int wifiEnable = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifiEnable == WifiManager.WIFI_STATE_DISABLED){
                Log.d(TAG, "系统关闭wifi");
            } else if (wifiEnable == WifiManager.WIFI_STATE_ENABLED){
                Log.d(TAG, "系统开启wifi");
            }
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)){
            netStatusUtil.onScanComplete();
            Log.d(TAG, "扫描完成");
        }
    }
}

