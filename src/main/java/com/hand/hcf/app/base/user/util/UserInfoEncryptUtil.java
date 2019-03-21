package com.hand.hcf.app.base.user.util;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Created by Transy on 2017/6/26.
 */
public class UserInfoEncryptUtil {

    public static final String DES = "AES"; // optional value AES/DES/DESede

    public static final String CIPHER_ALGORITHM = "AES"; // optional value AES/DES/DESede


    private static Key getSecretKey(String key) throws Exception{
        SecretKey securekey = null;
        if(key == null){
            key = "";
        }
        KeyGenerator keyGenerator = KeyGenerator.getInstance(DES);
//        keyGenerator.init(new SecureRandom(key.getBytes()));
//        securekey = keyGenerator.generateKey();

        SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(key.getBytes("UTF-8"));
        keyGenerator.init(128, random);
        securekey = keyGenerator.generateKey();
        return securekey;
    }

    public static String encrypt(String data,String key) throws Exception {
        SecureRandom sr = new SecureRandom();
        Key securekey = getSecretKey(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        byte[] bt = cipher.doFinal(data.getBytes("UTF-8"));
        String strs = Base64.getEncoder().encodeToString(bt);
        return strs;
    }

    public static String encryptWithoutLimit(String data){
        if(StringUtils.isBlank(data)){
            return data;
        }
        try {
            data = encrypt(data,"");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    public static String encrypt(String data){
        if(StringUtils.isBlank(data)){
            return data;
        }
        //防止身份证和银行卡号重复加密
        if(data != null && data.contains("=")){
            return  data;
        }
        try {
            data = encrypt(data,"");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    public static String detrypt(String message,String key) throws Exception{
        SecureRandom sr = new SecureRandom();
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        Key securekey = getSecretKey(key);
        cipher.init(Cipher.DECRYPT_MODE, securekey,sr);
        byte[] res = Base64.getDecoder().decode(message);
        res = cipher.doFinal(res);
        return new String(res,"UTF-8");
    }

    public static String detrypt(String data){
        if(StringUtils.isBlank(data)){
            return data;
        }
        if(!data.endsWith("=")){
            return data;
        }
        try {
            data = detrypt(data,"");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    public static String detryptWithoutLimit(String data){
        if(StringUtils.isBlank(data)){
            return data;
        }
        try {
            data = detrypt(data,"");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    /**
     *
     * @param data 字符串原文
     * @param prefix 显示前面几个字符
     * @param postfix 显示后边几个字符
     * @param starNum 中间显示*的数量，如果指定为负数，则中间的每一位字符会被替换成一个*
     * @return 替换后的字符串
     */
    public static String display(String data,int prefix,int postfix,int starNum){
        if(prefix < 0 || postfix < 0){
            return  data;
        }
        if(data == null || data.length() <= (prefix + postfix)){
            return  data;
        }

        int len = data.length();
        if(starNum < 0){
            starNum = len - (prefix + postfix);
        }

        String regex = String.format("(.{%d}).*(.{%d})",prefix,postfix);
        String stars = repeat("*",starNum);
        data = data.replaceAll(regex,String.format("$1%s$2",stars));

        return data;
    }

    private  static String repeat(String data,int times){
        if(times <= 1){
            return  data;
        }

        StringBuffer sBuffer = new StringBuffer();
        for(int i=0;i<times;i++){
            sBuffer.append(data);
        }

        return  sBuffer.toString();
    }

    public static String displayBankNo(String bankNo,boolean isDetrypt){
        if(isDetrypt){
            bankNo = UserInfoEncryptUtil.detrypt(bankNo);
        }
        return display(bankNo,4,4,-1);
    }
    public static String displayCardNo(String idCard, boolean isDetrypt){
        if(isDetrypt){
            idCard = UserInfoEncryptUtil.detrypt(idCard);
        }
        return display(idCard,3,4,-1);
    }

   /* public static void main(String[] args) {
        String data = "Bc3uJCO1MCBzKKpvpQFNmCXmzMks924WfeHbHkky37s=";
        System.out.println(display(data,3,4,5));
        System.out.println(display(data,3,4,-1));
        System.out.println(display(data,3,4,8));

        System.out.println(displayBankNo(data,true));
        System.out.println(displayCardNo(data,true));

        String encode = encrypt(data);
        String decode = detrypt(encode);
        System.out.println(encode);
        System.out.println(decode);
    }*/
}
