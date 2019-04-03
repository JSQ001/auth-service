package com.hand.hcf.app.expense.common.utils;

import com.baomidou.mybatisplus.toolkit.IdWorker;

import java.io.File;
import java.util.UUID;

public class FileUtil {
    private static String insertSql1 = "INSERT INTO EXP_EXPENSE_TYPE_ICON (ID, EXPENSE_TYPE_ICON_OID, ATTACHMENT_ID, ICON_NAME, ICON_URL, ENABLED, DELETED, SEQUENCE, STRING_1, STRING_2) VALUES (";
    private static String insertSql2 = ", '";
    private static String insertSql3 = "', ";
    private static String insertSql4 = ", '";
    private static String insertSql5 = "', 'http://47.101.143.73:9089/upload/expenseIcon/";
    private static String insertSql6 = "-";
    private static String insertSql7 = "', '1', '0', NULL, NULL, NULL);";
    public static void traverseFolder(String path) {
        //int id = 69;
        //int attachmentId = 20775;
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                System.out.println("文件夹是空的!");
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        traverseFolder(file2.getAbsolutePath());
                    } else {
                        //id += 1;
                        //attachmentId += 1;
                        //System.out.println("文件:" + file2.getName());
                        String insertSql =insertSql1 + IdWorker.getId() +insertSql2+ UUID.randomUUID().toString()+insertSql3+IdWorker.getId()+insertSql4+file2.getName().substring(0, file2.getName().lastIndexOf("."))+insertSql5+file2.getName()+insertSql7;
                        System.out.println(insertSql);
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
    }

    public static void main(String[] args) {
        //FileUtil.traverseFolder("C:\\Users\\24166\\Desktop\\icon\\icon\\icon_png\\icon@1x");
        FileUtil.traverseFolder("C:\\Users\\24166\\Desktop\\icon\\icon");
    }

}
