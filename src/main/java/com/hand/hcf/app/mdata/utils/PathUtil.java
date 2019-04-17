package com.hand.hcf.app.mdata.utils;


import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

public class PathUtil {
    public static String calculatePath(int digitsPerLevel, List<String> siblingPathList, String parentPath, String domainName) {
        if (siblingPathList != null) {
            Collections.sort(siblingPathList);
        }
        String resultPath = (parentPath != null) ? parentPath : "";
        int maxCount = (int) Math.pow(10, digitsPerLevel);
        if (siblingPathList == null || siblingPathList.size() == 0 || Integer.valueOf(siblingPathList.get(0)) % maxCount != 1) {
            return resultPath + intToString(1, digitsPerLevel);
        } else if (siblingPathList.size() >= maxCount - 2) {
            throw new ValidationException(new ValidationError(domainName, "exceed.max.count"));
        } else if (Integer.valueOf(siblingPathList.get(siblingPathList.size() - 1)) % maxCount == siblingPathList.size()) {
            return resultPath + intToString((siblingPathList.size() + 1), digitsPerLevel);
        } else {
            Integer resultPathSequence = null;
            for (int i = 1; i < siblingPathList.size(); i++) {
                Integer currentSiblingPath = Integer.valueOf(siblingPathList.get(i));
                Integer currentLevelSequence = currentSiblingPath % maxCount;
                if (currentLevelSequence != i + 1) {
                    resultPathSequence = i + 1;
                }
            }
            Assert.notNull(resultPathSequence, "a middle sequenceNumber is supposed to be found");
            return resultPath + intToString(resultPathSequence, digitsPerLevel);
        }
    }

    public static String intToString(int num, int digits) {
        StringBuffer s = new StringBuffer(digits);
        int zeroes = digits - (int) (Math.log(num) / Math.log(10)) - 1;
        for (int i = 0; i < zeroes; i++) {
            s.append(0);
        }
        return s.append(num).toString();
    }
}
