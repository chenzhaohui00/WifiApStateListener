package com.test.mytest.util.net_status;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 创建时间：2018/1/4;
 * 编写人：陈兆辉
 * 功能描述：
 */
public abstract class NetStatus {
    /**
     * 网络类型
     */
    public static final int NET_TYPE_WIFI = 0;
    public static final int NET_TYPE_AP = 1;
    public static final int NET_TYPE_DEFAULT = 2;
    /**
     * 创建WifiConfiguration的类型
     */
    public static final int WIFI_CIPHER_NOPWD = 1;
    public static final int WIFI_CIPHER_WEP = 2;
    public static final int WIFI_CIPHER_WPA = 3;

    private WifiManager mWifiManager;
    public NetStatus(Context context){
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }
    /**
     * 开启wifi
     */
    public boolean openWifi(){
        return mWifiManager.setWifiEnabled(true);
    }

    /**
     * 关闭wifi
     */
    public boolean closeWifi(){
        return mWifiManager.setWifiEnabled(false);
    }

    /**
     * wifi是否在开启
     */
    public boolean isWifiEnable(){
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 获取自身ip地址
     */
    public abstract String getIpAddress();

    /**
     * 获取热点ip地址
     */
    public String getApAddress(){
        /* IP/netmask: 192.168.43.1/255.255.255.0 */
        return intToIp(mWifiManager.getDhcpInfo().gateway);
    }

    /**
     * 扫描wifi列表
     */
    public void scanWifiList(){
        mWifiManager.startScan();
    }

    /**
     * 返回扫描列表
     */
    public List<ScanResult> getScanResults(){
        return mWifiManager.getScanResults();
    }

    /**
     * 连接wifi
     */
    public boolean connectWifi(String ssid, String password, int type){
        //断开当前的连接
        disconnectCurrentWifi();
        //连接新的连接
        int netId = mWifiManager.addNetwork(createWifiCfg(ssid,password,type));
        return mWifiManager.enableNetwork(netId, true);
    }

    /**
     * 热点是否开启
     * @return
     */
    public boolean isApOpened(){
        try {
            Method method = mWifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (boolean) method.invoke(mWifiManager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    private static boolean isApOpened(Context context){
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            Method method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (boolean) method.invoke(wifiManager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    /**
     * 开启热点
     */
    public boolean openAp(String ssid, String pwd, boolean isOpen){
        Method method1=null;
        try {
            method1=mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class,boolean.class);
            WifiConfiguration netConfig=new WifiConfiguration();

            netConfig.SSID=ssid;
            netConfig.preSharedKey=pwd;
            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            if (isOpen) {
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            }else {
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            }
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            return (boolean)method1.invoke(mWifiManager,netConfig,true);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 关闭热点
     */
    public boolean closeAp(){
        if (isApOpened()){
            try {
                Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
                Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                return (boolean) method2.invoke(mWifiManager, config, false);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 关闭当前的Wifi网络
     */
    public boolean disconnectCurrentWifi(){
        if(mWifiManager != null && mWifiManager.isWifiEnabled()){
            int netId = mWifiManager.getConnectionInfo().getNetworkId();
            mWifiManager.disableNetwork(netId);
            return mWifiManager.disconnect();
        }
        return false;
    }

    /**
     * 创建WifiConfiguration
     *
     * @param ssid
     * @param password
     * @param type
     * @return
     */
    public static WifiConfiguration createWifiCfg(String ssid, String password, int type){
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        config.SSID = "\"" + ssid + "\"";

        if(type == WIFI_CIPHER_NOPWD){
//            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;

//            无密码连接WIFI时，连接不上wifi，需要注释两行代码
//            config.wepKeys[0] = "";
//            config.wepTxKeyIndex = 0;
        }else if(type == WIFI_CIPHER_WEP){
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }else if(type == WIFI_CIPHER_WPA){
            config.preSharedKey = "\""+password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    /**
     * 获取当前的网络类型
     * @return 返回以下三种网络类型：NET_TYPE_WIFI，NET_TYPE_DEFAULT，NET_TYPE_AP
     */
    public static int getCurrentNetType(Context context) {
        int type = NET_TYPE_DEFAULT;
        if (context != null) {
            // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取NetworkInfo对象
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //判断NetworkInfo对象是否为空 并且类型是否为WIFI
            if(networkInfo != null && networkInfo.isAvailable()){
                if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                    type = NET_TYPE_WIFI;
                }else if(isApOpened(context)){
                    type = NET_TYPE_AP;
                }
            }
        }
        return type;
    }

    /**
     * 将int形式的ip地址转换为String形式
     * @param address int形式的ip地址
     * @return String形式的ip地址
     */
    @NonNull
    protected String intToIp(int address) {
        return ((address & 0xFF)
                + "." + ((address >> 8) & 0xFF)
                + "." + ((address >> 16) & 0xFF)
                + "." + ((address >> 24) & 0xFF));
    }

    /**
     * 进入此状态
     */
//    public abstract void onEnter();

    /**
     * 离开此状态
     */
//    public abstract void onExit();
}
