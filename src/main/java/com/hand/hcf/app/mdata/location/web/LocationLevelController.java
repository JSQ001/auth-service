package com.hand.hcf.app.mdata.location.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.location.domain.LocationLevel;
import com.hand.hcf.app.mdata.location.domain.LocationLevelAssign;
import com.hand.hcf.app.mdata.location.dto.LocationDTO;
import com.hand.hcf.app.mdata.location.dto.LocationLevelAssignDTO;
import com.hand.hcf.app.mdata.location.service.LocationDTOService;
import com.hand.hcf.app.mdata.location.service.LocationLevelAssignService;
import com.hand.hcf.app.mdata.location.service.LocationLevelService;
import com.hand.hcf.app.core.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/3/26
 */
@RestController
@RequestMapping("/api/location/level")
@Api(tags = "地点级别定义")
public class LocationLevelController {
    @Autowired
    private LocationLevelService locationLevelService;
    @Autowired
    private LocationDTOService locationDTOService;
    @Autowired
    private LocationLevelAssignService locationLevelAssignService;

    /**
     * 分页查询地点级别信息
     *
     * @param setOfBooksId
     * @param code
     * @param name
     * @param enabled
     * @param pageable
     * @return
     */
    @GetMapping("/query")
    @ApiOperation(value = "分页查询地点级别", notes = "分页查询地点级别信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "setOfBooksId", value = "账套id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "没页多少条", dataType = "int"),
            @ApiImplicitParam(name = "code", value = "地点级别代码", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "地点级别名称", dataType = "String"),
            @ApiImplicitParam(name = "enabled", value = "状态", dataType = "Boolean")
    })
    public ResponseEntity<List<LocationLevel>> queryByCondition(@RequestParam Long setOfBooksId,
                                                                @RequestParam(required = false) String code,
                                                                @RequestParam(required = false) String name,
                                                                @RequestParam(required = false) Boolean enabled,
                                                                @ApiIgnore Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<LocationLevel> result = locationLevelService.queryByCondition(setOfBooksId, code, name, enabled, page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     * 单个查询地点级别
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "单个查询地点级别", notes = "单个查询地点级别信息")
    public ResponseEntity<LocationLevel> getLocationLevelById(@PathVariable Long id) {
        return ResponseEntity.ok(locationLevelService.getLocationLevelById(id));
    }

    /**
     * 新增地点级别
     *
     * @param locationLevel
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增地点级别", notes = "新增地点级别信息")
    public ResponseEntity<LocationLevel> createLocationLevel(@RequestBody LocationLevel locationLevel) {
        locationLevel.setTenantId(OrgInformationUtil.getCurrentTenantId());
        return ResponseEntity.ok(locationLevelService.insertOrUpdateLocationLevel(locationLevel));
    }

    /**
     * @param locationLevel
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改地点级别", notes = "修改地点级别信息")
    public ResponseEntity<LocationLevel> updateLocationLevel(@RequestBody LocationLevel locationLevel) {
        return ResponseEntity.ok(locationLevelService.insertOrUpdateLocationLevel(locationLevel));
    }

    /**
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除地点级别", notes = "删除地点级别信息")
    public ResponseEntity<LocationLevel> deleteLocationLevel(@PathVariable Long id) {
        locationLevelService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 查询地点信息
     *
     * @param countryCode
     * @param stateCode
     * @param cityCode
     * @param type
     * @param code
     * @param description
     * @param pageable
     * @return
     */
    @ApiOperation(value = "查询地点信息", notes = "根据条件查询地点信息")
    @GetMapping(value = "/query/location")
    public ResponseEntity<List<LocationDTO>> queryLocationByCondition(@RequestParam(required = false) String countryCode,
                                                                      @RequestParam(required = false) String stateCode,
                                                                      @RequestParam(required = false) String cityCode,
                                                                      @RequestParam(required = false) String type,
                                                                      @RequestParam(required = false) String code,
                                                                      @RequestParam(required = false) String description,
                                                                      @ApiIgnore Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        String language = OrgInformationUtil.getCurrentLanguage();
        List<LocationDTO> result = locationDTOService.queryLocationByCondition(countryCode, stateCode, cityCode, type, code, description, language, page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    @ApiOperation(value = "查询国家信息", notes = "查询国家信息")
    @GetMapping(value = "/query/country")
    public ResponseEntity<List<LocationDTO>> getCountryList(@ApiIgnore Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        String language = OrgInformationUtil.getCurrentLanguage();
        List<LocationDTO> locationDTOS = locationDTOService.getCountryList(language);
        return new ResponseEntity<>(locationDTOS, httpHeaders, HttpStatus.OK);
    }

    /**
     * 查询省信息
     *
     * @param countryCode
     * @return
     */
    @ApiOperation(value = "查询省信息", notes = "根据国家code查询省信息")
    @GetMapping(value = "/query/state")
    public ResponseEntity<List<LocationDTO>> getStateListByCountryCode(@RequestParam(required = false) String countryCode, @ApiIgnore Pageable pageable) {
        String language = OrgInformationUtil.getCurrentLanguage();
        Page page = PageUtil.getPage(pageable);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        List<LocationDTO> locationDTOS = locationDTOService.getStateListByCountryCode(countryCode, language);
        return new ResponseEntity<>(locationDTOS, httpHeaders, HttpStatus.OK);
    }

    /**
     * 查询城市信息
     *
     * @param stateCode
     * @return
     */
    @ApiOperation(value = "查询城市信息", notes = "根据省code查询城市信息")
    @GetMapping(value = "/query/city")
    public ResponseEntity<List<LocationDTO>> getCityListByStateCode(@RequestParam(required = false) String stateCode,@ApiIgnore Pageable pageable) {
        String language = OrgInformationUtil.getCurrentLanguage();
        Page page = PageUtil.getPage(pageable);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        List<LocationDTO> locationDTOS = locationDTOService.getCityListByStateCode(stateCode, language);
        return new ResponseEntity<>(locationDTOS, httpHeaders, HttpStatus.OK);
    }

    /**
     * 分配地点信息
     *
     * @param locationLevelAssignDTO
     * @return
     */
    @ApiOperation(value = "分配地点信息", notes = "批量分配地点信息")
    @PostMapping(value = "/distribute/location")
    public ResponseEntity distributeLocation(@RequestBody LocationLevelAssignDTO locationLevelAssignDTO) {
        locationLevelAssignService.distributeLocation(locationLevelAssignDTO);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 批量删除地点分配信息
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "批量删除地点分配信息", notes = "批量删除地点分配信息")
    @DeleteMapping(value = "/deleteLocationLevelAssignByIds")
    public ResponseEntity<Boolean> deleteLocationLevelAssignByIds(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(locationLevelAssignService.deleteLocationLevelAssignByIds(ids));
    }

    /**
     * 查询已分配的地点信息
     *
     * @param levelId
     * @param countryCode
     * @param stateCode
     * @param cityCode
     * @param type
     * @param code
     * @param description
     * @param pageable
     * @return
     */
    @ApiOperation(value = "查询已分配的地点信息", notes = "根据条件查询已分配的地点信息")
    @GetMapping(value = "/queryLocationLevelAssign")
    public ResponseEntity<List<LocationLevelAssign>> queryLocationLevelAssign(@RequestParam Long levelId,
                                                                              @RequestParam(required = false) String countryCode,
                                                                              @RequestParam(required = false) String stateCode,
                                                                              @RequestParam(required = false) String cityCode,
                                                                              @RequestParam(required = false) String type,
                                                                              @RequestParam(required = false) String code,
                                                                              @RequestParam(required = false) String description,
                                                                              @ApiIgnore Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);
        String language = OrgInformationUtil.getCurrentLanguage();
        List<LocationLevelAssign> result = locationLevelAssignService.queryLocationLevelAssign(levelId, countryCode, stateCode, cityCode, type, code, description, language, mybatisPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(mybatisPage);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

}
