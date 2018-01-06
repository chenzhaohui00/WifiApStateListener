package com.test.mytest.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;

import com.test.mytest.util.net_status.ApState;
import com.test.mytest.util.net_status.ConnectedWifiState;
import com.test.mytest.util.net_status.DefaultState;
import com.test.mytest.util.net_status.NetStatus;

import java.util.List;

/**
 * 创建时间：2018/1/4;
 * 编写人：陈兆辉
 * 功能描述：管理wifi状态的单例工具类
 */
public class WifiUtil{
    private static final String TAG = WifiUtil.class.getSimpleName();
    /** 当前状态对象*/
    private NetStatus currentStatus;
    /** 增删listener时的锁*/
    private final Object listenerLock = new Object();
    /** 状态变化Listener*/
    private OnStateChangeListener listeners[] = {};
    private static volatile WifiUtil instance;
    private Context context;
    private WifiUtil(Context context) {
        this.context = context;
        notifyCurrentStatus(context);
        Log.e(TAG, "初始状态:"+currentStatus.getClass().getSimpleName());
    }

    public static WifiUtil getInstance(Context context){
        if(instance == null){
            synchronized (WifiUtil.class){
                if(instance == null){
                    instance = new WifiUtil(context);
                }
            }
        }
        return instance;
    }

    public NetStatus getCurrentStatus(){
        return currentStatus;
    }

    public boolean openWifi() {
        return currentStatus.openWifi();
    }

    public boolean closeWifi() {
        return currentStatus.closeWifi();
    }

    public boolean isWifiEnable(){
        return currentStatus.isWifiEnable();
    }

    public String getIpAddress() {
        return currentStatus.getIpAddress();
    }

    public String getApIpAddress(){
        return currentStatus.getApAddress();
    }

    public void scanWifiList() {
        currentStatus.scanWifiList();
    }

    public boolean connectWifi(String ssid, String password, int type) {
        return currentStatus.connectWifi(ssid,password,type);
    }

    public boolean isApOpened() {
        return currentStatus.isApOpened();
    }

    public boolean openAp(String ssid, String pwd, boolean isOpen) {
        //todo
        return currentStatus.openAp(ssid,pwd,isOpen);
//        return ApMgr.configApState(context, "ap321");
    }

    public boolean closeAp() {
        return currentStatus.closeAp();
    }

    public boolean disconnectCurrentWifi() {
        return currentStatus.disconnectCurrentWifi();
    }

    /**
     * 通知状态更新，暂时没有做同步，因为目前都是在主线程广播接收者中回调。
     * @param status
     */
    public void notifyStatus(NetStatus status){
//        currentStatus.onExit();
        if(currentStatus instanceof ConnectedWifiState){
            notifyDisconnectWifi();
        }else if(currentStatus instanceof DefaultState){
            notifyExitDefaultState();
        }else if(currentStatus instanceof ApState){
            notifyCloseAp();
        }
        currentStatus = status;
        if(status instanceof ConnectedWifiState){
            notifyConnectWifi();
        }else if(status instanceof DefaultState){
            notifyEnterDefaultState();
        }else if(status instanceof ApState){
            notifyCreateAp();
        }
//        status.onEnter();
    }

    private void notifyConnectWifi() {
        OnStateChangeListener[] listeners = this.listeners;
        for(OnStateChangeListener listener : listeners){
            listener.onConnectWifi();
        }
    }

    private void notifyCreateAp() {
        OnStateChangeListener[] listeners = this.listeners;
        for(OnStateChangeListener listener : listeners){
            listener.onCreateAp();
        }
    }

    private void notifyDisconnectWifi() {
        OnStateChangeListener[] listeners = this.listeners;
        for(OnStateChangeListener listener : listeners){
            listener.onDisconnectWifi();
        }
    }

    private void notifyEnterDefaultState() {
        OnStateChangeListener[] listeners = this.listeners;
        for(OnStateChangeListener listener : listeners){
            listener.onEnterDefaultState();
        }
    }

    private void notifyExitDefaultState() {
        OnStateChangeListener[] listeners = this.listeners;
        for(OnStateChangeListener listener : listeners){
            listener.onExitDefaultState();
        }
    }

    private void notifyCloseAp() {
        OnStateChangeListener[] listeners = this.listeners;
        for(OnStateChangeListener listener : listeners){
            listener.onCloseAp();
        }
    }

    public void onScanComplete(){
        OnStateChangeListener[] listeners = this.listeners;
        for(OnStateChangeListener listener : listeners){
            listener.onScanComplete(currentStatus.getScanResults());
        }
    }

    public void addListener (OnStateChangeListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null.");
        synchronized (listenerLock) {
            OnStateChangeListener[] listeners = this.listeners;
            int n = listeners.length;
            //如果已经存在则直接返回
            for (int i = 0; i < n; i++){
                if (listener == listeners[i]){
                    return;
                }
            }
            OnStateChangeListener[] newListeners = new OnStateChangeListener[n + 1];
            newListeners[0] = listener;
            System.arraycopy(listeners, 0, newListeners, 1, n);
            this.listeners = newListeners;
        }
        Log.d(TAG, "OnStateChangeListener added");
    }

    public void removeListener (OnStateChangeListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null.");
        synchronized (listenerLock) {
            OnStateChangeListener[] listeners = this.listeners;
            int n = listeners.length;
            OnStateChangeListener[] newListeners = new OnStateChangeListener[n - 1];
            for (int i = 0, ii = 0; i < n; i++) {
                OnStateChangeListener copyListener = listeners[i];
                if (listener == copyListener) continue;
                if (ii == n - 1) return;
                newListeners[ii++] = copyListener;
            }
            this.listeners = newListeners;
        }
        Log.d(TAG, "OnStateChangeListener removed");
    }

    public void notifyCurrentStatus(Context context) {
        switch (NetStatus.getCurrentNetType(context)){
            case NetStatus.NET_TYPE_AP:
                currentStatus = ApState.getInstance(context);
                break;
            case NetStatus.NET_TYPE_DEFAULT:
                currentStatus = DefaultState.getInstance(context);
                break;
            case NetStatus.NET_TYPE_WIFI:
                currentStatus = ConnectedWifiState.getInstance(context);
                break;
        }
    }

    public static class OnStateChangeListener{
        /** 进入ApState的回调*/
        public void onCreateAp(){}
        /** 离开ApState的回调*/
        public void onCloseAp(){}
        /** 进入ConnectedWifiState的回调*/
        public void onConnectWifi(){}
        /** 离开ConnectedWifiState的回调*/
        public void onDisconnectWifi(){}
        /** 进入DefaultState的回调*/
        public void onEnterDefaultState(){}
        /** 离开DefaultState的回调*/
        public void onExitDefaultState(){}
        /** 扫描完成*/
        public void onScanComplete(List<ScanResult> results){}
    }

}
