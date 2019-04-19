package com.hand.hcf.app.mdata.period.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.period.domain.Periods;
import com.hand.hcf.app.mdata.period.dto.PeriodsDTO;
import com.hand.hcf.app.mdata.period.service.PeriodsService;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PeriodsResource {

    private final Logger log = LoggerFactory.getLogger(PeriodsResource.class);

    @Autowired
    private PeriodsService periodsService;
    @Autowired
    RedisTemplate redisTemplate;
    /**
     * @api {get} /api/periods/query/close 分页查询关闭的期间数据
     * @apiGroup Periods
     * @apiParam  {Long} periodSetId 会计期id
     * @apiSuccess {Object[]} Periods  总账期间实体集合
     * @apiSuccess {Long} id   总账期间id
     * @apiSuccess {Long} periodSetId   会计期id
     * @apiSuccess {Integer} periodYear   年
     * @apiSuccess {Integer} periodNum   月份
     * @apiSuccess {String} periodName   期间
     * @apiSuccess {Integer} periodSeq   期间序号
     * @apiSuccess {Integer} startDate  日期从
     * @apiSuccess {Integer} endDate  日期到
     * @apiSuccess {Integer} quarterNum  季度
     * @apiSuccess {Long}  tenantId   租户id
     * @apiSuccessExample {json} Success-Result
    [
    {
    "id": "920172474916663298",
    "createdDate": "2017-10-17T06:19:49Z",
    "lastUpdatedDate": "2017-10-17T06:19:49Z",
    "createdBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
    "lastUpdatedBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
    "periodSetId": "908907570419539970",
    "periodYear": 2016,
    "periodNum": 2,
    "periodName": "02-2016",
    "periodSeq": 20160002,
    "startDate": "2016-01-31T16:00:00Z",
    "endDate": "2016-02-28T16:00:00Z",
    "quarterNum": 1,
    "tenantId": "907943971227361281",
    "periodStatusCode": "N",
    "enabled": true,
    "deleted": false
    }]
     */
    @RequestMapping(value = "/periods/query/close",method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PeriodsDTO>> findClosePeriodsByTenantIdAndPeriodSetCode(@RequestParam(name = "periodSetId") Long periodSetId,
                                                                                       @RequestParam(name = "setOfBooksId") Long setOfBooksId,
                                                                                       Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<PeriodsDTO> result = periodsService.findClosePeriodsByTenantIdAndPeriodSetId(page,periodSetId,setOfBooksId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/periods/close");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }
    /**
     * @api {get} /api/periods/query/open 分页查询打开的期间数据
     * @apiGroup Periods
     * @apiParam {Long} periodSetId
     * @apiSuccess {Object[]} Periods  总账期间实体集合
     * @apiSuccess {Long} id   总账期间id
     * @apiSuccess {Long} periodSetId   会计期id
     * @apiSuccess {Integer} periodYear   年
     * @apiSuccess {Integer} periodNum   月份
     * @apiSuccess {String} periodName   期间
     * @apiSuccess {Integer} periodSeq   期间序号
     * @apiSuccess {Integer} startDate  日期从
     * @apiSuccess {Integer} endDate  日期到
     * @apiSuccess {Integer} quarterNum  季度
     * @apiSuccess {Long}  tenantId   租户id
     * @apiSuccessExample {json} Success-Result
    [
    {
    "id": "920172474883108865",
    "createdDate": "2017-10-18T12:45:03Z",
    "lastUpdatedDate": "2017-10-18T12:45:03Z",
    "createdBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
    "lastUpdatedBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
    "periodSetId": "908907570419539970",
    "periodYear": 2016,
    "periodNum": 1,
    "periodName": "01-2016",
    "periodSeq": 20160001,
    "startDate": "2015-12-31T16:00:00Z",
    "endDate": "2016-01-30T16:00:00Z",
    "quarterNum": 1,
    "tenantId": "907943971227361281",
    "periodStatusCode": "C",
    "enabled": true,
    "deleted": false
    }]
     */
    @RequestMapping(value = "/periods/query/open",method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PeriodsDTO>> findOpenPeriodsByTenantIdAndPeriodSetCode(@RequestParam(name = "periodSetId") Long periodSetId,
                                                                                      @RequestParam(name = "setOfBooksId") Long setOfBooksId,
                                                                                      Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<PeriodsDTO> result = periodsService.findOpenPeriodsByTenantIdAndPeriodSetCode(page,periodSetId,setOfBooksId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/periods/close");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     *
     * @api {post} /api/periods/open/periods 打开期间
     * @apiGroup Periods
     * @apiParam {Long} periodId 总账期间id
     * @apiParam {Long} periodSetId 会计期id
     * @apiParam {Long} setOfBooksId 账套id
     */
    @RequestMapping(value = "/periods/open/periods",method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> openPeriods(@RequestParam(name = "periodId") Long periodID, //总账期间id
                                               @RequestParam(name = "periodSetId") Long periodSetId,//会计期id
                                               @RequestParam(name = "setOfBooksId")  Long setOfBooksId//账套id
                                                     ) throws URISyntaxException {
        Boolean result=false;
        byte[] lockKey = (OrgInformationUtil.getCurrentUserOid() + String.valueOf(periodID)).getBytes(Charset.forName("utf8"));
        Boolean locked = false;
        try {
            //check
            locked = (Boolean) redisTemplate.execute(new RedisCallback() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    Boolean result = connection.setNX(lockKey, "TRUE".getBytes(Charset.forName("utf8")));
                    connection.expire(lockKey,  3);
                    return result;
                }
            });
            if (!locked) {
                try {
                    log.error("operation is locked , lockKey : {}", new String(lockKey,"UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                throw new BizException(RespCode.REQUEST_FREQUENCY_TOO_FAST, "处理中，请勿连续点击");
            }
            result=  periodsService.openPeriodByPeriodIdAndPeriodSetIdAndTenantId(periodID,periodSetId,setOfBooksId);
        } finally {
            if (locked) {
                redisTemplate.execute(new RedisCallback() {
                    @Override
                    public Object doInRedis(RedisConnection connection) throws DataAccessException {
                        connection.del(lockKey);
                        return null;
                    }
                });
            }
        }
        return ResponseEntity.ok(result);
    }
    /**
     * @api {post} /api/periods/close/periods 关闭期间
     * @apiGroup Periods
     * @apiParam {Long} periodId 总账期间id
     * @apiParam {Long} periodSetId 会计期id
     * @apiParam {Long} setOfBooksId 账套id
     */
    @RequestMapping(value = "/periods/close/periods",method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> closePeriods(@RequestParam(name = "periodId") Long periodId, //总账期间id
                                                @RequestParam(name = "periodSetId") Long periodSetId,//会计期id
                                                @RequestParam(name = "setOfBooksId")  Long setOfBooksId//账套id
    ) throws URISyntaxException {
        return ResponseEntity.ok(periodsService.closePeriodByPeriodIdAndPeriodSetIdAndTenantId(periodId,periodSetId,setOfBooksId));
    }

    /**
     * @api {post} /api/periods/batch/create/periods 根据输入的会计期代码,年度从,年度到批量创建会计期间
     * @apiGroup Periods
     * @apiParam {String} periodSetCode 会计期code
     * @apiParam {Integer} yearFrom 年度从
     * @apiParam {Integer} yearTo 年度到
     */
    @RequestMapping(value = "/periods/batch/create/periods",method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> createPeriodsBatch(@RequestParam String periodSetCode,
                                                      @RequestParam Integer yearFrom,
                                                      @RequestParam Integer yearTo) throws URISyntaxException {
        return ResponseEntity.ok(periodsService.createPeriodsBatch(periodSetCode,yearFrom,yearTo));
    }

    /**
     * 预算项目查询  根据账套ID查询打开的期间信息
     *
     * xiaoting.pan
     * @param setOfBooksId
     *
     *
     * @return
     */
    @RequestMapping(value = "/periods/query/open/periods/by/setOfBook/id",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Periods>> findOpenPeriodsByID(@RequestParam Long setOfBooksId, Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<Periods> result = periodsService.findOpenPeriodsByBookID(setOfBooksId,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/periods/query/open/periods/by/setOfBook/id");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 根据账套id查询年度集合（去重）
     * @param setOfBooksId
     * @param pageable
     * @return
     */
    @Timed
    @RequestMapping(value = "/periods/select/years/by/setOfBooksId",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Periods>> selectYearsBySetOfBooksId(@RequestParam Long setOfBooksId, Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<Periods> result = periodsService.selectYearsBySetOfBooksId(setOfBooksId,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/periods/select/years/by/setOfBooksId");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);

    }

    /**
     * @api {GET} /api/periods/get/years/by/setOfBooksId 根据账套查询会计期间年度
     * @apiGroup Periods
     * @apiParam {Long} setOfBooksId 账套
     * @apiSuccessExample {json} Success-Result
     * [2018,2019,2020]
     */
    @Timed
    @GetMapping(value = "/periods/get/years/by/setOfBooksId")
    public ResponseEntity<List<Integer>> getPeriodYearsForSetOfBooksId(@RequestParam("setOfBooksId") Long setOfBooksId){
        return ResponseEntity.ok(periodsService.getPeriodYearsForSetOfBooksId(setOfBooksId));
    }

    /**
     * 预算项目查询 通过账套ID期间Name查询总账期间信息
     * @param setOfBooksId
     * @param periodName
     * @param periodYear
     * @return
     * @throws URISyntaxException
     */
    @GetMapping(value = "/query/budget/periods")
    @Timed
    public ResponseEntity<List<Periods>> findPeriodsByIdAndName(@RequestParam Long setOfBooksId, @RequestParam(required = false) String periodName, @RequestParam(required = false) Integer periodYear) throws URISyntaxException {
        return ResponseEntity.ok(periodsService.findPeriodsByIdAndName(setOfBooksId,periodName,periodYear, OrgInformationUtil.getCurrentTenantId()));
    }
}
