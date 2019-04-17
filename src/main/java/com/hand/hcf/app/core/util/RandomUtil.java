package com.hand.hcf.app.core.util;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtil {

    private static final int DEF_COUNT = 6;

    private RandomUtil() {
    }

    /**
     * Generates a password.
     *
     * @return the generated password
     */
    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
    }

    /**
     * Generates an activation key.
     *
     * @return the generated activation key
     */
    public static String generateActivationKey() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }

    /**
    * Generates a reset key.
    *
    * @return the generated reset key
    */
    public static String generateResetKey() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }

    public static String generateSMSToken() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }

    public static String generateNumeric() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }

    public static String generateNumeric(int count) {
        return RandomStringUtils.randomNumeric(count > 0 ? count : DEF_COUNT);
    }

    public static String generateGroupCompanyAcountCode(){
        return  RandomStringUtils.randomAlphabetic(4).toLowerCase() + RandomStringUtils.randomNumeric(4);
    }
    public static String generateCompanyAcountCode(){
        return  RandomStringUtils.randomAlphabetic(4).toLowerCase() + RandomStringUtils.randomNumeric(4);
    }
}
