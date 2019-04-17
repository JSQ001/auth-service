package com.hand.hcf.app.core.redisLock.util;



import com.hand.hcf.app.core.redisLock.domain.RedisValueObject;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class LocationUtil {

    private static final String hardwareAddress = "0000-0000-0000-0000";

    public static String serializeCurrentRequest(long expireTime) {
        return RedisValueObject.builder().expires(expireTime).macAddress(getLocalMac()).jvmPid(getJvmProcessId()).threadId(getThreadId()).build().toString();
    }

    public static RedisValueObject deserializeCurrentRequest(String str) {
        return RedisValueObject.fromString(str);
    }

    public static String getLocalMac() {
        InetAddress ia = null;
        try {
            ia = InetAddress.getLocalHost();

            //获取网卡，获取地址
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

            StringBuffer sb = new StringBuffer("");
            if(mac==null){return hardwareAddress;}
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                //字节转换为整数
                int temp = mac[i] & 0xff;
                String str = Integer.toHexString(temp);
                if (str.length() == 1) {
                    sb.append("0" + str);
                } else {
                    sb.append(str);
                }
            }
            return sb.toString().toUpperCase();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("获取本机mac地址异常!");
    }

    public static long getJvmProcessId() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Long.valueOf(runtimeMXBean.getName().split("@")[0])
            .intValue();
    }

    public static long getThreadId() {
        return Thread.currentThread().getId();
    }

    /*
        public static void main(String[] args) {
            System.out.println(getLocalMac());
            System.out.println(getJvmProcessId());
            System.out.println(getThreadId());
        }
    */
}
