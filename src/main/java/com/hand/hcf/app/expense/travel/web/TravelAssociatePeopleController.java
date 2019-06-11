package com.hand.hcf.app.expense.travel.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.travel.service.TravelAssociatePeopleService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;

/**
 * 差旅申请单关联人员表前端控制器
 * @author zhu.zhao
 * @date 2019/3/11
 */
@Api(tags = "差旅申请单关联人员表前端控制器")
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
    @ApiOperation(value = "新建差旅申请单行时，查询出行人员-分页", notes = "新建差旅申请单行时，查询出行人员-分页 开发:程占华")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity listUsersByHeaderId(@ApiParam(value = "头ID") @RequestParam(value = "headerId") Long headerId,
                                              @ApiParam(value = "用户编码") @RequestParam(value = "userCode",required = false) String userCode,
                                              @ApiParam(value = "全称") @RequestParam(value = "fullName",required = false) String fullName,
                                              @ApiParam(value = "keyWord") @RequestParam(value = "keyWord",required = false) String keyWord,
                                              @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<ContactCO> cotacts = travelAssociatePeopleService.listUsersByHeaderId(headerId,userCode,
                fullName,keyWord,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(cotacts.getRecords(), httpHeaders, HttpStatus.OK);
    }
}
