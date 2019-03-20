package com.hand.hcf.app.auth.web;

import com.hand.hcf.app.auth.dto.AuthenticationCode;
import com.hand.hcf.app.auth.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qingsheng.chen
 * @date 2017/12/29 11:58
 * @description 扫码登录
 */
@RestController
@RequestMapping("api/qr/authorization")
public class AuthenticationController {
    private static final String RESPONSE_LINK = "link";

    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * @api {put} /api/qr/authorization PC-首页-扫码-扫一扫-扫码登录二维码
     * @apiDescription 返回的link为二维码内容，function固定等于PC_LOGIN，用于app识别URL为扫码登录，UUID为唯一登录标识，UUID有效期默认300秒
     * @apiGroup AppCenter
     * @apiVersion 1.0.0
     * @apiSuccess {String} link 二维码链接
     * @apiSuccessExample {json} Response-Example:
     *      {"link": "http://downloads.huilianyi.com/app/sit?UUID=35d776c5-2f73-4d46-8411-c02f8afab2b7&function=PC_LOGIN"}
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> getAuthentication() {
        Map<String, String> linkMap = new HashMap<>(1);
        linkMap.put(RESPONSE_LINK, authenticationService.getAuthentication());
        return ResponseEntity.ok(linkMap);
    }

    /**
     * @api {put} /api/qr/authorization/{uuid} PC-首页-扫码-扫一扫-当前扫码状态
     * @apiDescription 获取二维码当前状态，该接口默认阻塞8秒，如果在阻塞中二维码状态发生编码，该接口会立即返回最新数据，PC端应循环该请求直至用户完成登录或者该UUID过期，如果UUID过期，HTTP状态码为404，无内容
     * @apiGroup AppCenter
     * @apiVersion 1.0.0
     * @apiParam {String} UUID 二维码URL中的UUID参数
     * @apiParamExample {String} Request-Example:
     *      35d776c5-2f73-4d46-8411-c02f8afab2b7
     * @apiSuccess {String} uuid 登录唯一标识
     * @apiSuccess {String} status 二维码状态[INITIAL(初始状态),WAITING(用户已扫描二维码),LOGGED(用户确定登录)]
     * @apiSuccess {String} username 用户名，仅当二维码状态为[WAITING,LOGGED]
     * @apiSuccess {String} companyName 公司名称，仅当二维码状态为[WAITING,LOGGED]
     * @apiSuccess {String} accessToken 同登录接口返回，仅当二维码状态为[LOGGED]
     * @apiSuccessExample {json} Response-Example:
     *      {"uuid": "35d776c5-2f73-4d46-8411-c02f8afab2b7","status": "LOGGED","username": "李佳易","companyName": "三全科技","accessToken": {"access_token": "26d359d2-ffa1-4d51-b5cb-21548779d370","token_type": "bearer","refresh_token": "904f4787-d031-47b5-a28c-ff3334d1d19e","expires_in": 86394,"scope": "write","isDeviceValidate": true}}
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<AuthenticationCode> getAuthentication(@PathVariable String uuid) {
        AuthenticationCode authenticationCode = authenticationService.getAuthentication(uuid);
        if (authenticationCode != null) {
            return ResponseEntity.ok(authenticationCode);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * @api {post} /api/qr/authorization/prior 首页-扫码-扫一扫-预登录
     * @apiDescription APP扫描二维码之后立即调用该接口，标识用户扫描了二维码
     * @apiGroup AppCenter
     * @apiVersion 1.0.0
     * @apiParam {String} UUID 二维码URL中的UUID参数
     * @apiParamExample {String} Request-Example:
     *      35d776c5-2f73-4d46-8411-c02f8afab2b7
     */
    @PostMapping("/prior/{uuid}")
    public ResponseEntity postAuthenticationPreLogin(@PathVariable String uuid) {
        if (authenticationService.preLogin(uuid)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * @api {post} /api/qr/authorization 首页-扫码-扫一扫-确认登录
     * @apiDescription 用户扫描二维码，在页面选择确认登录
     * @apiGroup AppCenter
     * @apiVersion 1.0.0
     * @apiParam {String} UUID 二维码URL中的UUID参数
     * @apiParamExample {String} Request-Example:
     *      35d776c5-2f73-4d46-8411-c02f8afab2b7
     */
    @PostMapping("/{uuid}")
    public ResponseEntity postAuthenticationLogin(@PathVariable String uuid){
        if (authenticationService.login(uuid)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
