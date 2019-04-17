package com.hand.hcf.app.core.util;


import com.hand.hcf.app.core.exception.BizException;

import java.math.BigDecimal;

/**
 * Created by kai.zhang on 2017-11-01.
 * 金额运算
 */
public final class OperationUtil {

    private OperationUtil() {
    }

    /**
     * Double 数组求和
     * @param values
     * @return
     */
    public static Double sum(Double[] values) {
        return sum(false,false,values);
    }

    /**
     * Double 数组求和
     * @param isZero 结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param values
     * @return
     */
    public static Double sumOfZero(boolean isZero,Double[] values) {
        return sum(false,isZero,values);
    }

    /**
     * Double 数组求和
     * @param isAbs 结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param values
     * @return
     */
    public static Double sumOfAbs(boolean isAbs,Double[] values) {
        return sum(isAbs,false,values);
    }

    private static Double sum(boolean isAbs,boolean isZero,Double[] values) {
        BigDecimal sumValue = BigDecimal.ZERO;
        for (Double value : values) {
            sumValue = sumValue.add((value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value)));
        }
        if(isAbs){
            sumValue = sumValue.abs();
        }else if(isZero){
            sumValue = (sumValue.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : sumValue);
        }
        return sumValue.doubleValue();
    }

    /**
     * Double两数求和
     *
     * @param value1
     * @param value2
     * @return
     */
    public static Double sum(Double value1, Double value2) {
        return sum(false,false,value1,value2);
    }

    /**
     * Double 两数求和
     * @param isZero     结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param value1
     * @param value2
     * @return
     */
    public static Double sumOfZero(boolean isZero,Double value1, Double value2) {
        return sum(isZero,false,value1,value2);
    }

    /**
     * Double 两数求和
     * @param isAbs      结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param value1
     * @param value2
     * @return
     */
    public static Double sumOfAbs(boolean isAbs,Double value1, Double value2) {
        return sum(false,isAbs,value1,value2);
    }

    /**
     * Double 两数求和
     * @param isZero     结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param isAbs      结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param value1
     * @param value2
     * @return
     */
    private static Double sum(boolean isZero,boolean isAbs,Double value1, Double value2) {
        BigDecimal sumValue = BigDecimal.ZERO;
        sumValue = (value1 == null ? BigDecimal.ZERO : BigDecimal.valueOf(value1))
                .add((value2 == null ? BigDecimal.ZERO : BigDecimal.valueOf(value2)));
        if(isAbs){
            sumValue = (sumValue.compareTo(BigDecimal.ZERO) == -1 ?
                    sumValue.abs() : sumValue);
        }else if(isZero){
            sumValue = (sumValue.compareTo(BigDecimal.ZERO) == -1 ?
                    BigDecimal.ZERO : sumValue);
        }
        return sumValue.doubleValue();
    }

    /**
     * Double 两数求差
     * @param isZero     减法结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param minuend    被减数
     * @param subtractor 减数
     * @return
     */
    public static Double subtractOfZero(boolean isZero, Double minuend, Double subtractor) {
        return subtract(isZero,false,minuend,subtractor);
    }

    /**
     * Double 两数求差
     *
     * @param minuend    被减数
     * @param subtractor 减数
     * @return
     */
    public static Double subtract(Double minuend, Double subtractor) {
        return subtract(false,false, minuend, subtractor);
    }

    /**
     * Double 两数求差
     * @param isAbs      结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param minuend    被减数
     * @param subtractor 减数
     * @return
     */
    public static Double subtractOfAbs(boolean isAbs, Double minuend, Double subtractor) {
        return subtract(false,isAbs,minuend,subtractor);
    }

    /**
     * Double 两数求差
     * @param isZero    减法结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param isAbs      结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param minuend    被减数
     * @param subtractor 减数
     * @return
     */
    private static Double subtract(boolean isZero,boolean isAbs, Double minuend, Double subtractor) {
        BigDecimal minuendDecimal = (minuend == null ? BigDecimal.ZERO : BigDecimal.valueOf(minuend));
        BigDecimal subtract = minuendDecimal
                .subtract((subtractor == null ? BigDecimal.ZERO : BigDecimal.valueOf(subtractor)));
        if(isAbs){
            subtract = (subtract.compareTo(BigDecimal.ZERO) == -1 ?
                    subtract.abs() : subtract);
        }else if(isZero){
            subtract = (subtract.compareTo(BigDecimal.ZERO) == -1 ?
                    BigDecimal.ZERO : subtract);
        }
        return subtract.doubleValue();
    }

    /**
     * Double 两数相除
     * @param dividend 被除数
     * @param divisor 除数
     * @param scale 精度:为空时默认精度为2
     * @return
     */
    public static Double safeDivide(Double dividend, Double divisor,Integer scale) {
        return safeDivide(false,false,true,dividend,divisor,scale);
    }

    /**
     * Double 两数相除,返回全部小数精度
     * @param dividend 被除数
     * @param divisor 除数
     * @return
     */
    public static Double safeDivide(Double dividend, Double divisor) {
        return safeDivide(false,false,false,dividend,divisor,null);
    }

    /**
     * Double 两数相除
     * @param isZero
     * @param isRound
     * @param dividend
     * @param divisor
     * @param scale
     * @return
     */
    public static Double safeDivide(boolean isZero,boolean isRound,Double dividend, Double divisor,Integer scale) {
        return safeDivide(isZero,false,isRound,dividend,divisor,scale);
    }

    /**
     * Double 两数相除
     * @param isRound
     * @param dividend
     * @param divisor
     * @param scale
     * @param isAbs
     * @return
     */
    public static Double safeDivide(boolean isRound,Double dividend, Double divisor,Integer scale,boolean isAbs) {
        return safeDivide(false,isAbs,isRound,dividend,divisor,scale);
    }

    /**
     * Double 两数相除 - 如果不能整除，防止程序报错，默认返回6位小数
     * @param isAbs 结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param isZero 结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param isRound 是否四舍五入
     * @param dividend 被除数
     * @param divisor 除数
     * @param scale 精度
     * @return
     */
    private static Double safeDivide(boolean isZero,boolean isAbs,boolean isRound,Double dividend, Double divisor,Integer scale) {
        if(dividend == null || divisor == null){
            throw new BizException(RespCode.SYS_OPERATION_DATA_NOT_BE_EMPTY);
        }
        BigDecimal returnValue = BigDecimal.valueOf(dividend);
        if(isRound){
            //为空时默认精度为2
            if(scale == null){
                scale = 2;
            }
            returnValue = returnValue.divide(BigDecimal.valueOf(divisor),scale,BigDecimal.ROUND_HALF_UP);
        }else{
            try {
                returnValue = returnValue.divide(BigDecimal.valueOf(divisor));
            }catch(Exception e){
                returnValue = returnValue.divide(BigDecimal.valueOf(divisor),6,BigDecimal.ROUND_HALF_UP);
            }
        }
        if(isZero){
            returnValue = (returnValue.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : returnValue);
        }else if(isAbs){
            returnValue = (returnValue.compareTo(BigDecimal.ZERO) == -1 ? returnValue.abs() : returnValue);
        }
        return returnValue.doubleValue();
    }

    /**
     * Double 两数相乘
     * @param value1
     * @param value2
     * @return
     */
    public static Double safeMultiply(Double value1, Double value2,Integer scale){
        return safeMultiply(false,false,true,value1,value2,scale);
    }

    /**
     * Double 两数相乘
     * @param value1
     * @param value2
     * @return
     */
    public static Double safeMultiply(Double value1, Double value2){
        return safeMultiply(false,false,value1,value2,null);
    }

    /**
     * Double 两数相乘
     * @param isZero
     * @param isRound
     * @param value1
     * @param value2
     * @param scale
     * @return
     */
    public static Double safeMultiply(boolean isZero,boolean isRound,Double value1, Double value2,Integer scale){
        return safeMultiply(isZero,false,isRound,value1,value2,scale);
    }

    /**
     * Double 两数相乘
     * @param isRound
     * @param value1
     * @param value2
     * @param scale
     * @param isAbs
     * @return
     */
    public static Double safeMultiply(boolean isRound,Double value1, Double value2,Integer scale,boolean isAbs){
        return safeMultiply(false,isAbs,false,value1,value2,scale);
    }

    /**
     * Double 两数相乘
     * @param isAbs 结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param isZero 结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param isRound 是否四舍五入
     * @param value1 乘数
     * @param value2 乘数
     * @param scale 精度
     * @return
     */
    private static Double safeMultiply(boolean isZero,boolean isAbs,boolean isRound,Double value1, Double value2,Integer scale){
        if(value1 == null || value2 == null){
            throw new BizException(RespCode.SYS_OPERATION_DATA_NOT_BE_EMPTY);
        }
        BigDecimal returnValue = BigDecimal.valueOf(value1);
        if(isRound){
            //为空时默认精度为2
            if(scale == null){
                scale = 2;
            }
            returnValue = returnValue.multiply(BigDecimal.valueOf(value2)).setScale(scale,BigDecimal.ROUND_HALF_UP);
        }else{
            returnValue = returnValue.multiply(BigDecimal.valueOf(value2));
        }
        if(isZero){
            returnValue = (returnValue.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : returnValue);
        }else if(isAbs){
            returnValue = (returnValue.compareTo(BigDecimal.ZERO) == -1 ? returnValue.abs() : returnValue);
        }

        return returnValue.doubleValue();
    }

    /**
     * Double 数值比较
     * value1大返回1，value2大返回-1，相等返回0
     * @param value1
     * @param value2
     * @param isAbs 是否比较绝对值
     * @return
     */
    public static Integer compare(Double value1,Double value2,boolean isAbs){
        return (isAbs ? BigDecimal.valueOf(value1).abs():BigDecimal.valueOf(value1))
                .compareTo(isAbs ? BigDecimal.valueOf(value2).abs():BigDecimal.valueOf(value2));
    }

    /**
     * Double 数值比较
     * value1大返回1，value2大返回-1，相等返回0
     * @param value1
     * @param value2
     * @return
     */
    public static Integer compare(Double value1,Double value2){
        return compare(value1,value2,false);
    }

    /**
     * 设置精度
     * @param value
     * @param scale
     * @return
     */
    public static Double setScale(Double value, Integer scale){
        if(value == null){
            return null;
        }
        return BigDecimal.valueOf(value).setScale(scale == null ? 2 : scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * Double 数组求和
     * @param values
     * @return
     */
    public static BigDecimal sum(BigDecimal[] values) {
        return sum(false,false,values);
    }

    /**
     * Double 数组求和
     * @param isZero 结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param values
     * @return
     */
    public static BigDecimal sumOfZero(boolean isZero,BigDecimal[] values) {
        return sum(false,isZero,values);
    }

    /**
     * Double 数组求和
     * @param isAbs 结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param values
     * @return
     */
    public static BigDecimal sumOfAbs(boolean isAbs,BigDecimal[] values) {
        return sum(isAbs,false,values);
    }

    private static BigDecimal sum(boolean isAbs,boolean isZero,BigDecimal[] values) {
        BigDecimal sumValue = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            sumValue = sumValue.add((value == null ? BigDecimal.ZERO : value));
        }
        if(isAbs){
            sumValue = sumValue.abs();
        }else if(isZero){
            sumValue = (sumValue.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : sumValue);
        }
        return sumValue;
    }

    /**
     * Double两数求和
     *
     * @param value1
     * @param value2
     * @return
     */
    public static BigDecimal sum(BigDecimal value1, BigDecimal value2) {
        return sum(false,false,value1,value2);
    }

    /**
     * Double 两数求和
     * @param isZero     结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param value1
     * @param value2
     * @return
     */
    public static BigDecimal sumOfZero(boolean isZero,BigDecimal value1, BigDecimal value2) {
        return sum(isZero,false,value1,value2);
    }

    /**
     * Double 两数求和
     * @param isAbs      结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param value1
     * @param value2
     * @return
     */
    public static BigDecimal sumOfAbs(boolean isAbs,BigDecimal value1, BigDecimal value2) {
        return sum(false,isAbs,value1,value2);
    }

    /**
     * Double 两数求和
     * @param isZero     结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param isAbs      结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param value1
     * @param value2
     * @return
     */
    private static BigDecimal sum(boolean isZero,boolean isAbs,BigDecimal value1, BigDecimal value2) {
        BigDecimal sumValue = BigDecimal.ZERO;
        sumValue = (value1 == null ? BigDecimal.ZERO : value1)
                .add((value2 == null ? BigDecimal.ZERO : value2));
        if(isAbs){
            sumValue = (sumValue.compareTo(BigDecimal.ZERO) == -1 ?
                    sumValue.abs() : sumValue);
        }else if(isZero){
            sumValue = (sumValue.compareTo(BigDecimal.ZERO) == -1 ?
                    BigDecimal.ZERO : sumValue);
        }
        return sumValue;
    }

    /**
     * Double 两数求差
     * @param isZero     减法结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param minuend    被减数
     * @param subtractor 减数
     * @return
     */
    public static BigDecimal subtractOfZero(boolean isZero, BigDecimal minuend, BigDecimal subtractor) {
        return subtract(isZero,false,minuend,subtractor);
    }

    /**
     * Double 两数求差
     *
     * @param minuend    被减数
     * @param subtractor 减数
     * @return
     */
    public static BigDecimal subtract(BigDecimal minuend, BigDecimal subtractor) {
        return subtract(false,false, minuend, subtractor);
    }

    /**
     * Double 两数求差
     * @param isAbs      结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param minuend    被减数
     * @param subtractor 减数
     * @return
     */
    public static BigDecimal subtractOfAbs(boolean isAbs, BigDecimal minuend, BigDecimal subtractor) {
        return subtract(false,isAbs,minuend,subtractor);
    }

    /**
     * Double 两数求差
     * @param isZero    减法结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param isAbs      结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param minuend    被减数
     * @param subtractor 减数
     * @return
     */
    private static BigDecimal subtract(boolean isZero,boolean isAbs, BigDecimal minuend, BigDecimal subtractor) {
        BigDecimal minuendDecimal = (minuend == null ? BigDecimal.ZERO : minuend);
        BigDecimal subtract = minuendDecimal
                .subtract((subtractor == null ? BigDecimal.ZERO : subtractor));
        if(isAbs){
            subtract = (subtract.compareTo(BigDecimal.ZERO) == -1 ?
                    subtract.abs() : subtract);
        }else if(isZero){
            subtract = (subtract.compareTo(BigDecimal.ZERO) == -1 ?
                    BigDecimal.ZERO : subtract);
        }
        return subtract;
    }

    /**
     * Double 两数相除
     * @param dividend 被除数
     * @param divisor 除数
     * @param scale 精度:为空时默认精度为2
     * @return
     */
    public static BigDecimal safeDivide(BigDecimal dividend, BigDecimal divisor,Integer scale) {
        return safeDivide(false,false,true,dividend,divisor,scale);
    }

    /**
     * Double 两数相除,返回全部小数精度
     * @param dividend 被除数
     * @param divisor 除数
     * @return
     */
    public static BigDecimal safeDivide(BigDecimal dividend, BigDecimal divisor) {
        return safeDivide(false,false,false,dividend,divisor,null);
    }

    /**
     * Double 两数相除
     * @param isZero
     * @param isRound
     * @param dividend
     * @param divisor
     * @param scale
     * @return
     */
    public static BigDecimal safeDivide(boolean isZero,boolean isRound,BigDecimal dividend, BigDecimal divisor,Integer scale) {
        return safeDivide(isZero,false,isRound,dividend,divisor,scale);
    }

    /**
     * Double 两数相除
     * @param isRound
     * @param dividend
     * @param divisor
     * @param scale
     * @param isAbs
     * @return
     */
    public static BigDecimal safeDivide(boolean isRound,BigDecimal dividend, BigDecimal divisor,Integer scale,boolean isAbs) {
        return safeDivide(false,isAbs,isRound,dividend,divisor,scale);
    }

    /**
     * Double 两数相除 - 如果不能整除，防止程序报错，默认返回6位小数
     * @param isAbs 结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param isZero 结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param isRound 是否四舍五入
     * @param dividend 被除数
     * @param divisor 除数
     * @param scale 精度
     * @return
     */
    private static BigDecimal safeDivide(boolean isZero,boolean isAbs,boolean isRound,BigDecimal dividend, BigDecimal divisor,Integer scale) {
        if(dividend == null || divisor == null){
            throw new BizException(RespCode.SYS_OPERATION_DATA_NOT_BE_EMPTY);
        }
        BigDecimal returnValue ;
        if(isRound){
            //为空时默认精度为2
            if(scale == null){
                scale = 2;
            }
            returnValue = dividend.divide(divisor,scale,BigDecimal.ROUND_HALF_UP);
        }else{
            try {
                returnValue = dividend.divide(divisor);
            }catch(Exception e){
                returnValue = dividend.divide(divisor,6,BigDecimal.ROUND_HALF_UP);
            }
        }
        if(isZero){
            returnValue = (returnValue.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : returnValue);
        }else if(isAbs){
            returnValue = (returnValue.compareTo(BigDecimal.ZERO) == -1 ? returnValue.abs() : returnValue);
        }
        return returnValue;
    }

    /**
     * Double 两数相乘
     * @param value1
     * @param value2
     * @return
     */
    public static BigDecimal safeMultiply(BigDecimal value1, BigDecimal value2,Integer scale){
        return safeMultiply(false,false,true,value1,value2,scale);
    }

    /**
     * Double 两数相乘
     * @param value1
     * @param value2
     * @return
     */
    public static BigDecimal safeMultiply(BigDecimal value1, BigDecimal value2){
        return safeMultiply(false,false,value1,value2,null);
    }

    /**
     * Double 两数相乘
     * @param isZero
     * @param isRound
     * @param value1
     * @param value2
     * @param scale
     * @return
     */
    public static BigDecimal safeMultiply(boolean isZero,boolean isRound,BigDecimal value1, BigDecimal value2,Integer scale){
        return safeMultiply(isZero,false,isRound,value1,value2,scale);
    }

    /**
     * Double 两数相乘
     * @param isRound
     * @param value1
     * @param value2
     * @param scale
     * @param isAbs
     * @return
     */
    public static BigDecimal safeMultiply(boolean isRound,BigDecimal value1, BigDecimal value2,Integer scale,boolean isAbs){
        return safeMultiply(false,isAbs,false,value1,value2,scale);
    }

    /**
     * Double 两数相乘
     * @param isAbs 结果为负数时是否返回绝对值，true是返回绝对值，false是返回负数结果
     * @param isZero 结果为负数时是否返回0，true是返回0，false是返回负数结果
     * @param isRound 是否四舍五入
     * @param value1 乘数
     * @param value2 乘数
     * @param scale 精度
     * @return
     */
    private static BigDecimal safeMultiply(boolean isZero,boolean isAbs,boolean isRound,BigDecimal value1, BigDecimal value2,Integer scale){
        if(value1 == null || value2 == null){
            throw new BizException(RespCode.SYS_OPERATION_DATA_NOT_BE_EMPTY);
        }
        BigDecimal returnValue;
        if(isRound){
            //为空时默认精度为2
            if(scale == null){
                scale = 2;
            }
            returnValue = value1.multiply(value2).setScale(scale,BigDecimal.ROUND_HALF_UP);
        }else{
            returnValue = value1.multiply(value2);
        }
        if(isZero){
            returnValue = (returnValue.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : returnValue);
        }else if(isAbs){
            returnValue = (returnValue.compareTo(BigDecimal.ZERO) == -1 ? returnValue.abs() : returnValue);
        }

        return returnValue;
    }

    /**
     * Double 数值比较
     * value1大返回1，value2大返回-1，相等返回0
     * @param value1
     * @param value2
     * @param isAbs 是否比较绝对值
     * @return
     */
    public static Integer compare(BigDecimal value1,BigDecimal value2,boolean isAbs){
        return (isAbs ? value1.abs() : value1)
                .compareTo(isAbs ? value2.abs() : value2);
    }

    /**
     * Double 数值比较
     * value1大返回1，value2大返回-1，相等返回0
     * @param value1
     * @param value2
     * @return
     */
    public static Integer compare(BigDecimal value1,BigDecimal value2){
        return compare(value1,value2,false);
    }

    /**
     * 设置精度
     * @param value
     * @param scale
     * @return
     */
    public static BigDecimal setScale(BigDecimal value, Integer scale){
        if(value == null){
            return null;
        }
        return value.setScale(scale == null ? 2 : scale,BigDecimal.ROUND_HALF_UP);
    }
}
