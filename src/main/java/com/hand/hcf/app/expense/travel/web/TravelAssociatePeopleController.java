package com.hand.hcf.app.expense.travel.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.expense.travel.service.TravelAssociatePeopleService;
import com.hand.hcf.app.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * 差旅申请单关联人员表前端控制器
 * @author zhu.zhao
 * @date 2019/3/11
 */
@RestController
@RequestMapping("/api/travel/associate/people")
public class TravelAssociatePeopleController {

    @Autowired
    private TravelAssociatePeopleService travelAssociatePeopleService;
    /**
     * 新建差旅申请单行时，查询出行人员-分页
     *
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity listUsersByHeaderId(@RequestParam(value = "headerId") Long headerId,
                                              @RequestParam(value = "userCode",required = false) String userCode,
                                              @RequestParam(value = "fullName",required = false) String fullName,
                                              @RequestParam(value = "keyWord",required = false) String keyWord,
                                              Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<ContactCO> cotacts = travelAssociatePeopleService.listUsersByHeaderId(headerId,userCode,
                fullName,keyWord,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(cotacts.getRecords(), httpHeaders, HttpStatus.OK);
    }
}
