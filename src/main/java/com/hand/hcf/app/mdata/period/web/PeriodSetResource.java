package com.hand.hcf.app.mdata.period.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.period.domain.PeriodSet;
import com.hand.hcf.app.mdata.period.dto.PeriodSetDTO;
import com.hand.hcf.app.mdata.period.service.PeriodSetService;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PeriodSetResource {

    private final Logger log = LoggerFactory.getLogger(PeriodSetResource.class);

    @Autowired
    private PeriodSetService periodSetService;
    @Autowired
    private BaseI18nService baseI18nService;
    /**
     * @apiDefine PeriodSet
     * @apiParam {Long} id   会计期id
     * @apiParam {String} periodSetCode   会计期代码
     * @apiParam {String} periodSetName   会计期名称
     * @apiParam {Integer} totalPeriodNum   会计期总数
     * @apiParam {String}  periodAdditionalFlag   P:附加前缀,S:附加后缀
     * @apiParam {Long} tenantId   租户id
     *
     */
    /**
     * @api {post} /api/periodset 创建会计期实体
     * @apiGroup PeriodSet
     * @apiSuccess {String} periodSetCode   会计期代码
     * @apiSuccess {String} periodSetName   会计期名称
     * @apiSuccess {Integer} totalPeriodNum   会计期总数
     * @apiSuccess {String} periodAdditionalFlag  P:附加前缀,S:附加后缀
     * @apiSuccess {Long}  tenantId   租户id
     * @apiSuccessExample {json} Success-Result
    {
    "i18n": {"periodSetName": [{
    "language": "zh_cn",
    "value": "汇联易24_部门组"
    },
    {
    "language": "en",
    "value": "hly24_departmentGroup"
    }
    ]
    },
    "id": "925717795065323521",
    "createdDate": "2017-11-01T13:34:56Z",
    "lastUpdatedDate": "2017-11-01T13:34:56Z",
    "createdBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
    "lastUpdatedBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
    "periodSetCode": "09100001_CALL131",
    "periodSetName": "汇联易24_部门组",
    "totalPeriodNum": 12,
    "periodAdditionalFlag": "P",
    "tenantId": "907943971227361281"
    }
     */
    @RequestMapping(value = "/periodset",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PeriodSet> createPeriodSet(@Valid @RequestBody PeriodSet periodSet) throws URISyntaxException {

        if (periodSet.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        PeriodSet result=    periodSetService.addPeriodSet(periodSet);
        return ResponseEntity.ok(result);
    }


    /**
     * @api {put} /api/periodset 更新会计期实体
     * @apiGroup PeriodSet
     * @apiSuccess {String} periodSetCode   会计期代码
     * @apiSuccess {String} periodSetName   会计期名称
     * @apiSuccess {Integer} totalPeriodNum   会计期总数
     * @apiSuccess {String} periodAdditionalFlag  P:附加前缀,S:附加后缀
     * @apiSuccess {Long}  tenantId   租户id
     * @apiSuccessExample {json} Success-Result
    "i18n": {
    "periodSetName": [
    {
    "language": "zh_cn",
    "value": "汇联易24_部门组44"
    },
    {
    "language": "en",
    "value": "hly24_departmentGroup"
    }
    ]
    },
    "id": "925717795065323521",
    "createdDate": "2017-11-01T13:34:56Z",
    "lastUpdatedDate": "2017-11-01T13:34:56Z",
    "createdBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
    "lastUpdatedBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
    "periodSetCode": "09100001_CALL131",
    "periodSetName": "汇联易24_部门组44",
    "totalPeriodNum": 12,
    "periodAdditionalFlag": "P",
    "tenantId": "907943971227361281"
     */
    @RequestMapping(value = "/periodset",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PeriodSet> updatePeriodSet(@Valid @RequestBody PeriodSet periodSet) throws URISyntaxException {

        if (periodSet.getId() == null) {
            return createPeriodSet(periodSet);
        }
        PeriodSet result=   periodSetService.updatePeriodSet(periodSet);
        return ResponseEntity.ok(baseI18nService.selectOneTranslatedTableInfoWithI18n(result.getId(),PeriodSet.class));
    }

    /**
     *
     * @api {delete} /api/periodset/{id} 根据id 逻辑删除会计期间
     * @apiGroup PeriodSet
     * @apiParam {Long} id 会计期id
     */
    @RequestMapping(value = "/periodset/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public  ResponseEntity<Void> deletePeriodSet(@PathVariable  Long id) throws URISyntaxException {
        periodSetService.deletePeriodSet(id);
        return ResponseEntity.ok().build();
    }

    /**
     *
     * @api {get} /api/periodset/{id} 通过id得到一个会计期
     * @apiGroup PeriodSet
     * @apiParam {Long} id 会计期id
     * @apiSuccess {Object} PeriodSet  会计期实体
     * @apiSuccess {Long} id   会计期id
     * @apiSuccess {String} periodSetCode   会计期代码
     * @apiSuccess {String} periodSetName   会计期名称
     * @apiSuccess {Integer} totalPeriodNum   会计期总数
     * @apiSuccess {String} periodAdditionalFlag  P:附加前缀,S:附加后缀
     * @apiSuccess {Long}  tenantId   租户id
     * @apiSuccessExample {json} Success-Result
    {
    "i18n": {
    "periodSetName":
    [
    {
    "language": "en",
    "value": "hly24_departmentGroup"
    },
    {
    "language": "zh_cn",
    "value": "汇联易24_部门组44"
    }
    ]
    },
    "id": "925717795065323521",
    "createdDate": "2017-11-01T13:34:56Z",
    "lastUpdatedDate": "2017-11-01T13:34:56Z",
    "createdBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
    "lastUpdatedBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
    "periodSetCode": "09100001_CALL131",
    "periodSetName": "汇联易24_部门组44",
    "totalPeriodNum": 12,
    "periodAdditionalFlag": "P",
    "tenantId": "907943971227361281"
    }
     */
    @RequestMapping(value = "/periodset/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PeriodSet> getPeriodSet(@PathVariable Long id) {
        PeriodSet periodSet = periodSetService.getPeriodSet(id);
        return Optional.ofNullable(periodSet)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     *
     * @api {get} /api/periodset 查询当前所有有效的会计期间
     * @apiGroup periodSet
     * @apiParam {String} periodSetCode 会计期code
     * @apiParam {pageable} pageable 分页的参数
     * @apiSuccess {Object[]} PeriodSet  会计期实体集合
     * @apiSuccess {Long} id   会计期id
     * @apiSuccess {String} periodSetCode   会计期代码
     * @apiSuccess {String} periodSetName   会计期名称
     * @apiSuccess {Integer} totalPeriodNum   会计期总数
     * @apiSuccess {String} periodAdditionalFlag  P:附加前缀,S:附加后缀
     * @apiSuccess {Long}  tenantId   租户id
     * @apiSuccessExample {json} Success-Result
    [
    {
    "i18n": {},
    "id": "913391163597565954",
    "createdDate": "2017-09-28T13:13:18Z",
    "lastUpdatedDate": "2017-09-28T13:13:18Z",
    "createdBy": "",
    "lastUpdatedBy": "",
    "periodSetCode": "DEFAULT_CAL",
    "periodSetName": "默认会计期",
    "totalPeriodNum": 12,
    "periodAdditionalFlag": "S",
    "tenantId": "907943971227361281"
    }
    ]

     */
    @RequestMapping(value = "/periodset",method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PeriodSetDTO>> findperiodsetByPeriodSetCodeAndTenantId(@RequestParam(name = "periodSetCode", required = false) String periodSetCode,
                                                                                      Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<PeriodSetDTO> result = periodSetService.findperiodsetByPeriodSetCodeAndTenantId(page, periodSetCode);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/periodset");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }
}
