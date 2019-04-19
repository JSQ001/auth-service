package com.hand.hcf.app.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by kai.zhang on 2017-10-18.
 */
public final class TypeConversionUtils {

    public static Long parseLong(Object object){
        try{
            return Long.parseLong(getString(object));
        }catch(Exception e){
            return null;
        }
    }

    public static Integer parseInt(Object object){
        try{
            return Integer.parseInt(getString(object));
        }catch(Exception e){
            return null;
        }
    }

    public static Float parseFloat(Object object){
        try{
            return Float.parseFloat(getString(object));
        }catch(Exception e){
            return null;
        }
    }

    public static Boolean parseBoolean(Object object){
        try{
            return Boolean.parseBoolean(getString(object));
        }catch(Exception e){
            return null;
        }
    }

    public static Double parseDouble(Object object){
        try{
            return Double.parseDouble(getString(object));
        }catch(Exception e){
            return null;
        }
    }

    public static String parseString(Object object){
        if(object == null){
            return null;
        }
        if("null".equals(object.toString())){
            return null;
        }
        // 注意：String类型  null和""
        if("".equals(object.toString())){
            return "";
        }
        return object.toString();
    }

    public static Map<String, List<Map<String, String>>> parseI18nMap(Object object){
        try{
            return (Map<String, List<Map<String, String>>>)object;
        }catch(Exception e){
            return null;
        }
    }

    public static ZonedDateTime parseZonedDateTime(Object object){
        String string = getString(object);
        try{
            return ZonedDateTime.parse(new CharSequence() {
                @Override
                public int length() {
                    return string.length();
                }

                @Override
                public char charAt(int index) {
                    return string.charAt(index);
                }

                @Override
                public CharSequence subSequence(int start, int end) {
                    return string.subSequence(start,end);
                }
            });
        }catch(Exception e){
            return null;
        }
    }

    public static String getString(Object object){
        if(object == null){
            return null;
        }
        if("".equals(object.toString()) || "null".equals(object.toString())){
            return null;
        }
        return object.toString();
    }

    public static <T extends Object> List<T> listAutoAdd(List<T> list,T value){
        return listAutoAdd(false,list,value);
    }

    public static <T extends Object> List<T> listAutoAdd(boolean isUniqueness,List<T> list,T value){
        if(getString(value) == null){
            return list;
        }
        if(list == null){
            list = new ArrayList<>();
        }
        if(isUniqueness){
            if(list.contains(value)){
                return list;
            }
        }
        list.add(value);
        return list;
    }

    public static byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    public static Boolean isEmpty(Object object){
        if(getString(object) == null){
            return true;
        }
        return false;
    }

    public static Boolean isNotEmpty(Object object){
        if(getString(object) == null){
            return false;
        }
        return true;
    }

    public static String timeToString(ZonedDateTime time){
        String date = "";
        date = date + time.getYear() + "-" + String.format("%02d",time.getMonthValue()) + "-" + String.format("%02d",time.getDayOfMonth());
        return date;
    }

    public static Boolean collectionIsNotEmpty(Collection collection){
        if(isNotEmpty(collection)){
            return collection.size() > 0;
        }
        return false;
    }

    /**
     * @Description: 四舍五入保留两位小数
     * @param: aDouble
     * @return: java.lang.Double
     * @Date: Created in 2018/4/20 9:56
     * @Modified by
     */
    public static BigDecimal roundHalfUp(Double aDouble){
        if (aDouble == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal bigDecimal = BigDecimal.valueOf(aDouble);
        bigDecimal= bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);
        return bigDecimal;
    }

    public static BigDecimal roundHalfUp(BigDecimal bigDecimal){
        if (bigDecimal == null) {
            return BigDecimal.ZERO;
        }

        bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bigDecimal;
    }
    /**
     * 获取一天的开始时间
     * @param dateString yyyy-mm-dd
     * @return
     */
    public static ZonedDateTime getStartTimeForDayYYMMDD(String dateString){
        if(isNotEmpty(dateString)){
            String[] split = dateString.split("-");
            return ZonedDateTime.of(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]),0,0,0,0, ZoneId.systemDefault());
        }
        return null;
    }

    /**
     * 获取一天的结束时间
     * @param dateString  yyyy-mm-dd
     * @return
     */
    public static ZonedDateTime getEndTimeForDayYYMMDD(String dateString){
        if(isNotEmpty(dateString)){
            String[] split = dateString.split("-");
            return ZonedDateTime.of(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]),23,59,59,999999999, ZoneId.systemDefault());
        }
        return null;
    }



    public static Double parseDouble(Object object,Integer scale){
        try{
            return BigDecimal.valueOf(Double.parseDouble(getString(object))).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        }catch(Exception e){
            return null;
        }
    }

    /**
     * 获取某月开始时间
     * @param dateString yyyy-mm
     * @return
     */
    public static ZonedDateTime getStartTimeForDayYYMM(String dateString){
        if(isNotEmpty(dateString)){
            String[] split = dateString.split("-");
            return ZonedDateTime.of(Integer.parseInt(split[0]),Integer.parseInt(split[1]),1,0,0,0,0, ZoneId.systemDefault());
        }
        return null;
    }
}
