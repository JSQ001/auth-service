package com.hand.hcf.app.mdata.announcement.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.announcement.domain.Carousel;
import com.hand.hcf.app.mdata.announcement.dto.CarouselDTO;
import com.hand.hcf.app.mdata.announcement.service.CarouselService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.system.constant.Constants;
import com.hand.hcf.core.util.PaginationUtil;
import io.micrometer.core.annotation.Timed;
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
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/carousels")
public class CarouselController {
    @Autowired
    private CarouselService carouselService;

    @RequestMapping(value = "/company/{companyOid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CarouselDTO>> listAllByCompanyOidOrderByPreferredDateDesc(@PathVariable UUID companyOid) {
        return ResponseEntity.ok(carouselService.listAllByCompanyOidAndEnabled(companyOid, true));
    }

    /**
     * @api {get} /api/carousels/v2/company/:companyOid 首页-获取Banner
     * @apiDescription 首页中获取公司Banner
     * @apiGroup AppCenter
     * @apiVersion 1.0.0
     * @apiParam {String}companyOid 公司Oid
     * @apiParamExample {String} Request-Example:
     * 2ec774f5-7aba-486c-bd48-cf2ae74c9d9f
     * @apiSuccess {String} title banner标题
     * @apiSuccess {String} fileURL banner图片链接
     * @apiSuccess {String} content banner点击加载内容
     * @apiSuccessExample {json} Response-Example:
     * {"title":"banner title", "content":"banner content", "attachmentDTO":{"fileURL":"banner url"}}
     */
    @RequestMapping(value = "/v2/company/{companyOid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CarouselDTO>> listAllByCompanyOIDAndEnableIsTrueOrderByPreferredDateDesc(@PathVariable UUID companyOid) {
        return ResponseEntity.ok(carouselService.listAllByCompanyOidAndEnabled(companyOid,true));
    }

    @RequestMapping(value = "/enable/company/{companyOid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CarouselDTO>> listAllByCompanyOidAndEnableOrderByPreferredDateDesc(@PathVariable UUID companyOid) {
        return ResponseEntity.ok(carouselService.listAllByCompanyOidAndEnabled(companyOid,true));
    }

    /**
     * @apiDefine CarouselDTO
     * @apiSuccess  {Object} CarouselDTO 公告视图对象
     * @apiSuccess  {String} CarouselDTO.id 公告Id
     * @apiSuccess  {UUID} CarouselDTO.carouselOID 公告OID
     * @apiSuccess  {String} CarouselDTO.title 公告标题
     * @apiSuccess  {String} CarouselDTO.content 公告内容
     * @apiSuccess  {AttachmentDTO} CarouselDTO.attachmentDTO 公告附件实体
     * @apiSuccess  {Boolean} CarouselDTO.enable 是够启用
     * @apiSuccess  {Boolean} CarouselDTO.preferredDate 启用时间
     * @apiSuccess  {String} CarouselDTO.outLink 外链
     *
     * @apiSuccess  {Object} AttachmentDTO  公告附件实体
     * @apiSuccess  {UUID} AttachmentDTO.attachmentOID  附件OID
     * @apiSuccess  {String} AttachmentDTO.fileType  附件类型
     * @apiSuccess  {String} AttachmentDTO.fileURL  附件URL
     * @apiSuccess  {String} AttachmentDTO.link  附件link
     * @apiSuccess  {String} AttachmentDTO.thumbnailUrl  附件缩略图URL
     * @apiSuccess  {String} AttachmentDTO.iconUrl  附件图标URL
     * @apiSuccess  {Long} AttachmentDTO.size  附件大小
     * @apiSuccess  {UUID} AttachmentDTO.createdBy  创建人
     * @apiSuccess  {DateTime} AttachmentDTO.createdDate  创建时间
     * @apiSuccess  {Boolean} AttachmentDTO.public  是否公共
     *
     *
     */


    /**
     * @return
     * @api {GET} /api/carousels/all?roleType={roleType}&enabled={enabled} 查询当前租户或公司下的公告模板信息
     * @apiGroup Carousel
     * @apiParam {String} [roleType] 集团（TENANT）或公司模式
     * @apiParam {Boolean} [enabled] 是否启用
     * @apiUse CarouselDTO
     * @apiSuccessExample Success-Response:
     * [
     * {
     * "id": 1,
     * "carouselOID": "781d1961-5706-488e-a3c1-6b47dfc28411",
     * "title": "1",
     * "attachmentDTO": {
     * "attachmentOID": "d38e35f4-348f-41ae-aa4c-104356f7323b",
     * "fileName": "68f37ca6855256eb60d52a9ed5597cd.png",
     * "fileType": "IMAGE",
     * "fileURL": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/carrousel/d38e35f4-348f-41ae-aa4c-104356f7323b-68f37ca6855256eb60d52a9ed5597cd.png?Expires=1509680425&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=E8VRBSrM3vInVBe5hgtkFxd16eI%3D",
     * "link": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/carrousel/d38e35f4-348f-41ae-aa4c-104356f7323b-68f37ca6855256eb60d52a9ed5597cd.png?Expires=1509680425&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=E8VRBSrM3vInVBe5hgtkFxd16eI%3D",
     * "thumbnailUrl": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/carrousel/d38e35f4-348f-41ae-aa4c-104356f7323b-68f37ca6855256eb60d52a9ed5597cd.png?Expires=1509680425&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=8eLICUnGj52HEaghOhShKad6hRI%3D&x-oss-process=image%2Fresize%2Ch_200",
     * "iconUrl": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/carrousel/d38e35f4-348f-41ae-aa4c-104356f7323b-68f37ca6855256eb60d52a9ed5597cd.png?Expires=1509680425&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=76hzTJ9r57d%2B0bI0x4ifoy9Z9DA%3D&x-oss-process=image%2Fresize%2Ch_80",
     * "size": 22800,
     * "createdBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
     * "createdDate": "2017-09-06T03:14:53Z",
     * "public": false
     * },
     * "enable": true,
     * "preferredDate": "2017-06-22T07:29:42Z",
     * "outLink": false
     * }
     * ]
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CarouselDTO>> getEnableCarousels(@RequestParam(value = "roleType", required = false) String roleType,
                                                                @RequestParam(value = "enabled", required = false) Boolean enabled) {
        List<CarouselDTO> result;
        if (roleType != null && Constants.ROLE_TENANT.equals(roleType)) {
            result = carouselService.listAllByByTenantIdAndCompanyOidIsNullAndEnabled(OrgInformationUtil.getCurrentTenantId(), enabled);
        } else {
            result = carouselService.listAllByCompanyOidAndEnabled(OrgInformationUtil.getCurrentCompanyOid(), enabled);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/{carouselOid}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Carousel> getCarouselByCarouselOid(@PathVariable UUID carouselOid) {
        return ResponseEntity.ok(carouselService.getCarouselByCarouselOid(carouselOid));
    }

    /**
     * @api {POST} /api/carousel?roleType={roleType} 创建公告
     * @apiGroup Carousel
     * @apiParam {String} [roleType] 集团（TENANT）或公司模式
     * @apiParamExample Request-Param :
     * {
     * "attachmentOID": "e2e4a653-f223-433c-812c-6fbd3c3e0241",
     * "content": "<ul><li>vfggxxxx</li></ul>",
     * "enable": true,
     * "title": "test"
     * }
     * @apiParam {Object} CarouselDTO 公告视图对象
     * @apiParam {String} CarouselDTO.title 公告标题
     * @apiParam {String} CarouselDTO.content 公告内容
     * @apiParam {AttachmentDTO} CarouselDTO.attachmentOID 公告附件OID
     * @apiParam {boolean} CarouselDTO.enable 启用
     * @apiUse CarouselDTO
     * @apiSuccessExample Success-Response:
     * {
     * "attachmentDTO": {
     * "attachmentOID": "e2e4a653-f223-433c-812c-6fbd3c3e0241",
     * "createdBy": "60f041a4-ced5-4e25-82df-729dbbe21443",
     * "createdDate": "2017-11-03T06:00:40Z",
     * "fileName": "Desert.jpg",
     * "fileType": "IMAGE",
     * "fileURL": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/8cfdcebe-dae7-493e-9bf2-a7c4d109539e/carrousel/e2e4a653-f223-433c-812c-6fbd3c3e0241-Desert.jpg?Expires=1509692439&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=M0RxWKhKx2mOKGRzg%2BWQ87R%2FtBk%3D",
     * "iconUrl": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/8cfdcebe-dae7-493e-9bf2-a7c4d109539e/carrousel/e2e4a653-f223-433c-812c-6fbd3c3e0241-Desert.jpg?Expires=1509692439&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=DoSUSimNg01Rutlv67c%2FDvp0wYE%3D&x-oss-process=image%2Fresize%2Ch_80",
     * "link": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/8cfdcebe-dae7-493e-9bf2-a7c4d109539e/carrousel/e2e4a653-f223-433c-812c-6fbd3c3e0241-Desert.jpg?Expires=1509692439&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=M0RxWKhKx2mOKGRzg%2BWQ87R%2FtBk%3D",
     * "public": false,
     * "size": 96916,
     * "thumbnailUrl": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/8cfdcebe-dae7-493e-9bf2-a7c4d109539e/carrousel/e2e4a653-f223-433c-812c-6fbd3c3e0241-Desert.jpg?Expires=1509692439&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=ir858BxxRCm5dpk%2BYWa8t4%2BMO2o%3D&x-oss-process=image%2Fresize%2Ch_200"
     * },
     * "carouselOID": "6f77e3a6-c1f6-4430-b90f-3dbd121182b7",
     * "companyOID": "8cfdcebe-dae7-493e-9bf2-a7c4d109539e",
     * "content": "<ul><li>vfggxxxx</li></ul>",
     * "createdDate": "2017-11-03T06:00:39Z",
     * "enable": true,
     * "lastModifiedDate": "2017-11-03T06:00:39Z",
     * "outLink": false,
     * "preferredDate": "2017-11-03T06:00:39Z",
     * "title": "test"
     * }
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Carousel> createCarousel(@RequestParam(value = "roleType", required = false) String roleType,
                                                   @RequestBody @Valid Carousel carousel) {
        Carousel result;
        if (roleType != null && Constants.ROLE_TENANT.equals(roleType)){

        }
        else{
            carousel.setCompanyOid(OrgInformationUtil.getCurrentCompanyOid());
        }
        result = carouselService.createCarousel(carousel);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<CarouselDTO> updateCarousel(@RequestBody @Valid CarouselDTO carouselDTO,
                                                      @RequestParam(value = "roleType", required = false) String roleType) {
        carouselDTO.setCompanyOid(OrgInformationUtil.getCurrentCompanyOid());
        return ResponseEntity.ok(carouselService.updateCarousel(carouselDTO));
    }

    @RequestMapping(value = "/{carouselOid}", method = RequestMethod.DELETE)
    public void deleteCarousel(@PathVariable UUID carouselOid,
                               @RequestParam(value = "roleType", required = false) String roleType) {
        carouselService.deleteCarousel(carouselOid);
    }

    /**
     * @api {PUT} /api/carousels/relevance/company?carouselOIDs={carouselOIDs}&companyOIDs={companyOIDs} 集团为公司分配公告接口
     * @apiGroup Carousel
     * @apiParam {UUID[]} carouselOIDs 要分配的公告集合
     * @apiParam {UUID[]} companyOIDs 要分配给的公司集合
     */
    @RequestMapping(value = "/relevance/company", method = RequestMethod.PUT)
    public ResponseEntity<Void> deployCarouselToCompany(@RequestParam("carouselOids") List<UUID> carouselOids,
                                                        @RequestParam("companyOids") List<UUID> companyOids) {
        carouselService.deployTenantCarouselToCompanyLogic(carouselOids, companyOids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/carousels/company/find/distribution?carouselOID={carouselOID}&page=page&size={size} 查看当前公告分配情况
     * @apiGroup Carousel
     * @apiParam {UUID} carouselOID 要查看的公告OID
     * @apiParam {int} page 分页开始页
     * @apiParam {int} size 每页记录数
     * @apiSuccessExample Success-Response:
     * [
     * {
     * "id": 138,
     * "companyOID": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
     * "name": "三全科技",
     * "expenseTypes": null,
     * "logo": {
     * "id": 18075,
     * "attachmentOID": "42d05896-cd9c-4a71-9dd0-274cba8bbad0",
     * "name": "服务台.png",
     * "mediaTypeID": 1001,
     * "path": "https://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//company/logo/2ec774f5-7aba-486c-bd48-cf2ae74c9d9f-服务台.png",
     * "thumbnailPath": "https://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//company/logo/2ec774f5-7aba-486c-bd48-cf2ae74c9d9f-服务台.png?x-oss-process=image/resize,h_80",
     * "iconPath": null,
     * "size": 5403,
     * "createdDate": "2017-09-06T03:14:53Z",
     * "createdBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
     * "checked": false,
     * "public": true,
     * "mediaType": "IMAGE",
     * "legacy": true
     * },
     * "createdBy": "system",
     * "createdDate": "2016-09-22T11:29:33Z",
     * "doneRegisterLead": true,
     * "taxId": "2121",
     * "groupCompanyOID": null,
     * "tenantId": 907943971227361281,
     * "setOfBooksId": 913384538644148226,
     * "legalEntityId": 913727947525468161,
     * "companyCode": null,
     * "address": null,
     * "companyLevelId": null,
     * "parentCompanyId": null,
     * "companyTypeId": null,
     * "startDateActive": null,
     * "endDateActive": null,
     * "enabled": false,
     * "initFinance": true
     * }
     * ]
     * @apiSuccess {Object[]} Company 公司集合
     * @apiSuccess {Long} Company.id 公司id
     * @apiSuccess {UUID} Company.groupCompanyOID 公司组OID
     * @apiSuccess {UUID} Company.companyOID 公司OID
     * @apiSuccess {String} Company.name 公司名
     * @apiSuccess {String} Company.logoURL 公司logoURL路径
     * @apiSuccess {DateTime} Company.createdDate 创建日期
     * @apiSuccess {boolean} Company.doneRegisterLead 是否创建引导完毕
     * @apiSuccess {String} Company.taxId 税号
     * @apiSuccess {int} Company.noticeType 绑定类型
     * @apiSuccess {int} Company.dimissionDelayDays 离职延迟天数
     * @apiSuccess {String} Company.passwordRule 密码失效日期
     * @apiSuccess {int} Company.passwordLengthMin 密码最小长度
     * @apiSuccess {int} Company.passwordLengthMax 密码最大长度
     * @apiSuccess {int} Company.passwordRepeatTimes 密码多久时间内允许重复,0即为允许重复
     * @apiSuccess {int} Company.createDataType 数据导入方式:1001-手工创建和接口导入，1002-excel导入
     * @apiSuccess {String} Company.companyCode 公司代码
     * @apiSuccess {String} Company.address 公司地址
     * @apiSuccess {Long} Company.companyLevelId 公司级别Id
     * @apiSuccess {String} Company.companyLevelName 公司级别名
     * @apiSuccess {Long} Company.parentCompanyId 父部门Id
     * @apiSuccess {String} Company.parentCompanyName 父部门名
     * @apiSuccess {DateTime} Company.startDateActive 有效开始日期
     * @apiSuccess {DateTime} Company.endDateActive 有效结束日期
     * @apiSuccess {Long} Company.companyTypeId 公司类型id
     * @apiSuccess {String} Company.companyTypeName 公司类型名称
     * @apiSuccess {Long} Company.setOfBooksId 账套id
     * @apiSuccess {String} Company.setOfBooksName 账套名称
     * @apiSuccess {Long} Company.legalEntityId 法人实体id
     * @apiSuccess {String} Company.legalEntityName 法人实体名称
     * @apiSuccess {String} Company.baseCurrency 本位币
     * @apiSuccess {String} Company.baseCurrencyName 本位币名称
     * @apiSuccess {Long} Company.tenantId 租户id
     */
    //
    @RequestMapping(value = "/company/find/distribution", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<Company>> getCarouselDistribution(@RequestParam("carouselOid") UUID carouselOid, Pageable pageable) throws URISyntaxException {

        Page<Company> page = carouselService.findCarouselCompanyDeploy(carouselOid, OrgInformationUtil.getCurrentTenantId(), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/carousel/find/distribution");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }


    @RequestMapping(value = "/enable/company", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CarouselDTO>> getEnableCarouselsCompany(@RequestParam(value = "roleType", required = false) String roleType,
                                                                       @RequestParam(value = "enabled", required = false) Boolean enabled) {
        List<CarouselDTO> result = null;
        if (roleType != null && Constants.ROLE_TENANT.equals(roleType)) {
            result = carouselService.listAllByByTenantIdAndCompanyOidIsNullAndEnabled(OrgInformationUtil.getCurrentTenantId(), true);
        } else {
            result = carouselService.listAllByCompanyOidAndEnabled(OrgInformationUtil.getCurrentCompanyOid(), enabled);
        }
        return ResponseEntity.ok(result);
    }


}
