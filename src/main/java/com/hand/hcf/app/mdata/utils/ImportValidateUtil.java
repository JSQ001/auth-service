package com.hand.hcf.app.mdata.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hcf.app.mdata.contact.enums.UserImportCode;
import com.hand.hcf.app.mdata.system.constant.Constants;
import com.hand.hcf.app.mdata.system.domain.BatchTransactionLog;
import com.hand.hcf.app.mdata.system.enums.ValidateErrorType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.poi.ss.usermodel.Row;

import java.util.regex.Pattern;

/**
 * Created by yangqi on 2017/1/12.
 */
public final class ImportValidateUtil {

    private static Pattern p = Pattern.compile("^\\d+$");

    private ImportValidateUtil() {
    }

    public static void addErrorToJSON(JSONObject error, ValidateErrorType errorType, int rowNum) {
        try {
            if (!error.containsKey(errorType.toString())) {
                error.put(errorType.toString(), new JSONArray());
            }
            error.getJSONArray(errorType.toString()).add("行号：" + (rowNum + 1));
        } /*catch (JSONException e) {*/
        catch (Exception e){

    }
    }

    public static void addErrorToJSON(JSONObject error, String errorMessage, int rowNum) {
        try {
            if (!error.containsKey(errorMessage)) {
                error.put(errorMessage, new JSONArray());
            }
            error.getJSONArray(errorMessage).add("行号：" + (rowNum + 1));
        } /*catch (JSONException e) {*/
        catch (Exception e){

        }
    }

    public static void addErrorToJSON2(JSONObject error, String errorType, int rowNum) {
        try {
            if (!error.containsKey(errorType.toString())) {
                error.put(errorType.toString(), new JSONArray());
            }
            error.getJSONArray(errorType.toString()).add(rowNum);
        } /*catch (JSONException e) {*/
        catch (Exception e){

        }
    }

    public static void addErrorToJSON3(JSONArray error, String errorType, int rowNum) {
        try {
            if (error.size() != 0) {
                Boolean flag = false;
                for (int i = 0; i < error.size(); i++) {
                    JSONObject json = error.getJSONObject(i);
                    if (json.containsKey(errorType.toString())) {
                        json.getJSONArray(errorType.toString()).add(rowNum);
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(errorType.toString(), new JSONArray().add(rowNum));
                    error.add(jsonObject);
                }
            } else {
                JSONObject json = new JSONObject();
                json.put(errorType.toString(), new JSONArray());
                json.getJSONArray(errorType.toString()).add(rowNum);
                error.add(json);
            }
        } /*catch (JSONException e) {*/
        catch (Exception e){

        }
    }

    public static boolean validateFieldValueNull(String fieldValue, BatchTransactionLog transactionLog, Row row, ValidateErrorType nullErrorType) {
        boolean isValid = true;
        if (StringUtils.isEmpty(fieldValue)) {
            addErrorToJSON(transactionLog.getErrors(), nullErrorType, row.getRowNum());
            isValid = false;
        }
        if (!isValid) {
            transactionLog.setFailureEntities(transactionLog.getFailureEntities() + 1);
        }
        return isValid;
    }

    public static boolean validateFieldValueNull(String fieldValue, BatchTransactionLog transactionLog, Row row, String errorMessage) {
        boolean isValid = true;
        if (StringUtils.isEmpty(fieldValue)) {
            addErrorToJSON(transactionLog.getErrors(), errorMessage, row.getRowNum());
            isValid = false;
        }
        if (!isValid) {
            transactionLog.setFailureEntities(transactionLog.getFailureEntities() + 1);
        }
        return isValid;
    }

    public static boolean validateFieldValueNotBlank(String fieldValue, BatchTransactionLog transactionLog, Row row, String errorMessage) {
        boolean isValid = true;
        if (StringUtils.isBlank(fieldValue)) {
            addErrorToJSON(transactionLog.getErrors(), errorMessage, row.getRowNum());
            isValid = false;
        }
        if (!isValid) {
            transactionLog.setFailureEntities(transactionLog.getFailureEntities() + 1);
        }
        return isValid;
    }

    public static boolean validateFieldValueMaxLength(String fieldValue, BatchTransactionLog transactionLog, Row row, ValidateErrorType maxErrorType, int maxLength) {
        boolean isValid = true;
        if (fieldValue.length() > maxLength) {
            addErrorToJSON(transactionLog.getErrors(), maxErrorType, row.getRowNum());
            isValid = false;
        }
        if (!isValid) {
            transactionLog.setFailureEntities(transactionLog.getFailureEntities() + 1);
        }
        return isValid;
    }

    public static boolean validateFieldValueMaxLength(String fieldValue, BatchTransactionLog transactionLog, Row row, String errorMessage, int maxLength) {
        boolean isValid = true;
        if (fieldValue.length() > maxLength) {
            addErrorToJSON(transactionLog.getErrors(), errorMessage, row.getRowNum());
            isValid = false;
        }
        if (!isValid) {
            transactionLog.setFailureEntities(transactionLog.getFailureEntities() + 1);
        }
        return isValid;
    }

    public static boolean validateEmailPattern(String email, BatchTransactionLog transactionLog, Row row) {
        if (!EmailValidator.getInstance().isValid(email)) {
            addErrorToJSON(transactionLog.getErrors(), ValidateErrorType.WRONG_EMAIL, row.getRowNum());
            int failEntities = transactionLog.getFailureEntities();
            failEntities++;
            transactionLog.setFailureEntities(failEntities);
            return false;
        } else {
            return true;
        }
    }

    public static boolean validateMobile(String mobile, BatchTransactionLog transactionLog, Row row) {
        if (!p.matcher(mobile).find()) {
            addErrorToJSON(transactionLog.getErrors(), ValidateErrorType.WRONG_MOBILE, row.getRowNum());
            int failEntities = transactionLog.getFailureEntities();
            failEntities++;
            transactionLog.setFailureEntities(failEntities);
            return false;
        } else {
            return true;
        }
    }

    public static boolean validateFullNameFormat(String fullName, BatchTransactionLog transactionLog, Row row, ValidateErrorType errorType) {
        try {
            if (!fullName.matches("^[\\u4e00-\\u9fa5]+$")) {
                //判断英文名格式中是否有/(/不在开头，不在结尾，并且中间只能出现一次)
                if (!fullName.matches("^[^/]*[^/]/{1}[^/]*[^/]$") || fullName.getBytes("UTF-8").length != fullName.length() || !fullName.matches("[^\\d]+")) {
                    addErrorToJSON(transactionLog.getErrors(), errorType, row.getRowNum());
                    int failEntities = transactionLog.getFailureEntities();
                    failEntities++;
                    transactionLog.setFailureEntities(failEntities);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 验证是否启用或是否默认信息
     *
     * @param enabledefault：启用或默认值
     * @param transactionLog：日志
     * @param row：行
     * @return
     */
    public static boolean validateEnableOrDefault(String enabledefault, BatchTransactionLog transactionLog, Row row, ValidateErrorType validateErrorType) {
        boolean isValid = true;
        if (!(enabledefault.equals(Constants.YES) || enabledefault.equals(Constants.NO)
                || enabledefault.equals(Constants.SMALL_YES) || enabledefault.equals(Constants.SMALL_NO))) {
            addErrorToJSON(transactionLog.getErrors(), validateErrorType, row.getRowNum());
            isValid = false;
            transactionLog.setFailureEntities(transactionLog.getFailureEntities() + 1);
        }
        return isValid;
    }

    /**
     * 验证是否启用或是否默认信息
     *
     * @param enabledefault：启用或默认值
     * @param transactionLog：日志
     * @param row：行
     * @return
     */
    public static boolean validateEnableOrDefault(String enabledefault, BatchTransactionLog transactionLog, Row row, String errorMessage) {
        boolean isValid = true;
        if (!(enabledefault.equals(Constants.YES) || enabledefault.equals(Constants.NO)
                || enabledefault.equals(Constants.SMALL_YES) || enabledefault.equals(Constants.SMALL_NO))) {
            addErrorToJSON(transactionLog.getErrors(), errorMessage, row.getRowNum());
            isValid = false;
            transactionLog.setFailureEntities(transactionLog.getFailureEntities() + 1);
        }
        return isValid;
    }

    /**
     * 验证文本是否大于对应长度
     *
     * @param text：文本
     * @param transactionLog：错误日志
     * @param row：行
     * @param validateErrorType：错误类型
     * @return
     */
    public static boolean validateMaxLength(String text, String regex, BatchTransactionLog transactionLog, Row row, ValidateErrorType validateErrorType) {
        boolean isValid = true;
        if (!PatternMatcherUtil.validationPatterMatcherRegex(text, regex)) {
            addErrorToJSON(transactionLog.getErrors(), validateErrorType, row.getRowNum());
            isValid = false;
            transactionLog.setFailureEntities(transactionLog.getFailureEntities() + 1);
        }
        return isValid;
    }

    /**
     * 验证文本是否大于对应长度
     *
     * @param text：文本
     * @param transactionLog：错误日志
     * @param row：行
     * @param errorMessage：错误信息
     * @return
     */
    public static boolean validateMaxLength(String text, String regex, BatchTransactionLog transactionLog, Row row, String errorMessage) {
        boolean isValid = true;
        if (!PatternMatcherUtil.validationPatterMatcherRegex(text, regex)) {
            addErrorToJSON(transactionLog.getErrors(), errorMessage, row.getRowNum());
            isValid = false;
            transactionLog.setFailureEntities(transactionLog.getFailureEntities() + 1);
        }
        return isValid;
    }

    public static boolean validateBankAcountNo(String bankAccountNo, BatchTransactionLog transactionLog, Row row) {
        if (StringUtils.isNotBlank(bankAccountNo) && bankAccountNo.contains("*")) {
            addErrorToJSON(transactionLog.getErrors(), ValidateErrorType.WRONG_BANK_ACCOUNT_NO, row.getRowNum());
            int failEntities = transactionLog.getFailureEntities();
            failEntities++;
            transactionLog.setFailureEntities(failEntities);
            return false;
        } else {
            return true;
        }
    }

    public static boolean validateUserImportStatus(String userViewStatus, BatchTransactionLog transactionLog, Row row, String errorMessage) {
        if (StringUtils.isNotEmpty(userViewStatus) && !UserImportCode.NORMAL_STATUS.equals(userViewStatus)) {
            addErrorToJSON(transactionLog.getErrors(), errorMessage, row.getRowNum());
            transactionLog.setFailureEntities(transactionLog.getFailureEntities() + 1);
            return false;
        } else {
            return true;
        }
    }

    public static String getValidateErrorDetail(ValidateErrorType errorType) {
        String errorDetail = "";
        switch (errorType) {
            case WRONG_EMAIL:
                errorDetail = "邮箱格式不符";
                break;
            case DUPLICATE_EMAIL:
                errorDetail = "邮箱重复";
                break;
            case CUSTOM_FIELD:
                errorDetail = "扩展字段错误";
                break;
            case WRONG_BANK_ACCOUNT_NO:
                errorDetail = "银行卡号格式有误";
                break;
            default:
                errorDetail = "未知错误";
                break;
        }
        return errorDetail;
    }

    public static String getValidateErrorDetail2(String errorType) {
        String errorDetail = "";
        switch (errorType) {
            case "查无此人":
                errorDetail = "notUser";
                break;
            case "已离职":
                errorDetail = "exServingOfficer";
                break;
            case "不在权限内":
                errorDetail = "notAuthorized";
                break;
            case "超过最大参与人人数":
                errorDetail = "exceedParticipants";
                break;
            default:
                errorDetail = "unknownError";
                break;
        }
        return errorDetail;
    }
}
