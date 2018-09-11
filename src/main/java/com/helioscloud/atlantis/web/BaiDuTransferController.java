package com.helioscloud.atlantis.web;

import com.alibaba.fastjson.JSONArray;
import com.helioscloud.atlantis.config.BaiDuTransferConfig;
import com.helioscloud.atlantis.dto.TransferDTO;
import com.helioscloud.atlantis.util.HttpGet;
import com.helioscloud.atlantis.util.TransferMd5;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by houyin.zhang@hand-china.com on 2018/9/10.
 */

@RestController
@RequestMapping("/api/transfer")
public class BaiDuTransferController {
    @Autowired
    private BaiDuTransferConfig baiDuTransferConfig;
    /**
     * @api {POST} /api/transfer【系统框架】自动翻译
     * @apiDescription 自动翻译接口，将languages集合内容 翻译成to对应的语言，
     * zh/zh_CN 中文，en/en_US 英语，jp/ja_jp 日语
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {List} languages  String类型的集合，需要翻译的原内容
     * @apiParam (请求参数) {String} to  目标语言
     * @apiParamExample {json} 请求报文:
     * {
     * "to":"en",
     * "languages":[
     * "保存",
     * "删除",
     * "更新"
     * ]
     * }
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "src": "保存",
     * "dst": "Preservation"
     * },
     * {
     * "src": "删除",
     * "dst": "delete"
     * },
     * {
     * "src": "更新",
     * "dst": "To update"
     * }
     * ]
     */
    @PostMapping
    public String getTransResult(@RequestBody(required = true) TransferDTO transfer) {

        String to = transfer.getTo();
        if ("en_US".equalsIgnoreCase(to)) {
            to = "en";
        } else if ("zh_CN".equalsIgnoreCase(to)) {
            to = "zh";
        } else if ("ja_jp".equalsIgnoreCase(to)) {
            to = "jp";
        }
        JSONArray result = null;
        if (transfer != null) {
            StringBuffer sb = new StringBuffer();
            List<String> languages = transfer.getLanguages();
            if (CollectionUtils.isNotEmpty(languages)) {
                languages.stream().forEach(language -> {
                    if (sb.length() == 0) {
                        sb.append(language);
                    } else {
                        sb.append("\n").append(language);
                    }
                });
            }
            Map<String, String> params = buildParams(sb.toString(), "auto", to);
            result = HttpGet.get(baiDuTransferConfig.getTransferApiHost(), params);
        }
        if (result != null) {
            return result.toString();
        }
        return null;
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("appid", baiDuTransferConfig.getAppId());
        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);
        // 签名
        String src = baiDuTransferConfig.getAppId() + query + salt + baiDuTransferConfig.getSecurityKey(); // 加密前的原文
        params.put("sign", TransferMd5.md5(src));
        return params;
    }
}
