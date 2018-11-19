package com.helioscloud.atlantis.web;

import com.helioscloud.atlantis.domain.ErrorMessage;
import com.helioscloud.atlantis.service.ErrorMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @description: 报错信息controller
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2018/10/18
 */
@RestController
@RequestMapping("/api/error/message")
public class ErrorMessageController {
    private ErrorMessageService errorMessageService;

    public ErrorMessageController (ErrorMessageService errorMessageService){
        this.errorMessageService = errorMessageService;
    }

    @PostMapping("/create")
    public ResponseEntity<ErrorMessage> createErrorMessage(@Valid @RequestBody ErrorMessage errorMessage){
        return ResponseEntity.ok(errorMessageService.createErrorMessage(errorMessage));
    }

    @PutMapping("/update")
    public ResponseEntity<ErrorMessage> updateErrorMessage(@RequestBody ErrorMessage errorMessage){
        return ResponseEntity.ok(errorMessageService.updateErrorMessage(errorMessage));
    }

    @GetMapping("/query/{id}")
    public ResponseEntity<ErrorMessage> getErrorMessageById(@PathVariable Long id){
        return ResponseEntity.ok(errorMessageService.selectById(id));
    }
}
