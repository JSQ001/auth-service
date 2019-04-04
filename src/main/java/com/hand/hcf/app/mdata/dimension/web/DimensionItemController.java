package com.hand.hcf.app.mdata.dimension.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.DimensionDetailCO;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItem;
import com.hand.hcf.app.mdata.dimension.domain.enums.DimensionItemImportCode;
import com.hand.hcf.app.mdata.dimension.dto.DimensionItemRequestDTO;
import com.hand.hcf.app.mdata.dimension.service.DimensionItemService;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.domain.ExportConfig;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.util.PageUtil;
import com.hand.hcf.core.web.dto.ImportResultDTO;
import com.itextpdf.text.io.StreamUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/dimension/item")
public class DimensionItemController {

    private final Logger log = LoggerFactory.getLogger(DimensionItemController.class);

    @Autowired
    private DimensionItemService dimensionItemService;

    /**
     * @apiDefine DimensionItem
     */

    /**
     * @api {POST} /api/dimension/item 【维值】创建
     * @apiGroup DimensionItem
     * @apiParam {Object} dimensionItemRequestDTO  维值定义请求DTO
     * @apiParam {Object} dimensionItemRequestDTO.dimensionItem  维值定义
     * @apiParam {String} dimensionItemRequestDTO.dimensionItem.dimensionItemCode  维值定义代码
     * @apiParam {String} dimensionItemRequestDTO.dimensionItem.dimensionItemName  维值定义名称
     * @apiParam {Long} dimensionItemRequestDTO.dimensionItem.dimensionId  维度id
     * @apiParam {Boolean} dimensionItemRequestDTO.dimensionItem.enabled  是否启用
     * @apiParam {Integer} dimensionItemRequestDTO.dimensionItem.visibleUserScope  可见人员范围
     * @apiParam {List} dimensionItemRequestDTO.departmentOrUserGroupIdList  部门或人员组id
     * @apiParamExample {json} 请求参数:
     * {
     *      "dimensionItem": {
     *          "dimensionItemCode": "code1",
     *          "dimensionItemName": "name1",
     *          "dimensionId": "23423141242",
     *          "enabled": true,
     *          "visibleUserScope": 1003
     *      },
     *      "departmentOrUserGroupIdList": [
     *          "123",
     *          "2345",
     *          "4534"
     *      ]
     * }
     * @apiSuccessExample {json} 成功返回值:
     * {
     *     "dimensionItem": {
     *         "id": "1077134099691466754",
     *         "createdDate": "2018-12-24T17:29:16.208+08:00",
     *         "createdBy": "1",
     *         "lastUpdatedDate": "2018-12-24T17:29:16.208+08:00",
     *         "lastUpdatedBy": "1",
     *         "versionNumber": 1,
     *         "enabled": true,
     *         "dimensionItemCode": "code1",
     *         "dimensionItemName": "name1",
     *         "dimensionId": "23423141242",
     *         "visibleUserScope": 1003
     *     },
     *     "departmentOrUserGroupIdList": [
     *         "123",
     *         "2345",
     *         "4534"
     *     ],
     *     "departmentOrUserGroupList": null
     * }
     */
    @PostMapping
    public DimensionItemRequestDTO insertDimensionItem(@RequestBody DimensionItemRequestDTO dimensionItemRequestDTO){
        log.debug("REST request to save dimensionItem : {}", dimensionItemRequestDTO);
        return dimensionItemService.insertDimensionItem(dimensionItemRequestDTO);
    }

    /**
     * @api {PUT} /api/dimension/item 【维值】更新
     * @apiGroup DimensionItem
     * @apiParam {Object} dimensionItemRequestDTO  维值定义请求DTO
     * @apiParamExample {json} 请求参数:
     * {
     *     "dimensionItem": {
     *         "id": "1077134099691466754",
     *         "createdDate": "2018-12-24T17:29:16.208+08:00",
     *         "createdBy": "1",
     *         "lastUpdatedDate": "2018-12-24T17:29:16.208+08:00",
     *         "lastUpdatedBy": "1",
     *         "versionNumber": 1,
     *         "enabled": true,
     *         "dimensionItemCode": "code1",
     *         "dimensionItemName": "name1",
     *         "dimensionId": "23423141242",
     *         "visibleUserScope": 1002
     *     },
     *     "departmentOrUserGroupIdList": [
     *         "245",
     *         "434"
     *     ],
     *     "departmentOrUserGroupList": null
     * }
     * @apiSuccessExample {json} 成功返回值:
     * {
     *     "dimensionItem": {
     *         "id": "1077134099691466754",
     *         "createdDate": "2018-12-24T17:29:16.208+08:00",
     *         "createdBy": "1",
     *         "lastUpdatedDate": "2018-12-24T17:29:16.208+08:00",
     *         "lastUpdatedBy": "1",
     *         "versionNumber": 2,
     *         "enabled": true,
     *         "dimensionItemCode": "code1",
     *         "dimensionItemName": "name1",
     *         "dimensionId": "23423141242",
     *         "visibleUserScope": 1002
     *     },
     *     "departmentOrUserGroupIdList": [
     *         "235",
     *         "454"
     *     ],
     *     "departmentOrUserGroupList": null
     * }
     */
    @PutMapping
    public DimensionItemRequestDTO updateDimensionItem(@RequestBody DimensionItemRequestDTO dimensionItemRequestDTO){
        log.debug("REST request to update dimensionItem : {}", dimensionItemRequestDTO);
        return dimensionItemService.updateDimensionItem(dimensionItemRequestDTO);
    }

    /**
     * @api {DELETE} /api/dimension/item/{dimensionItemId} 【维值】删除
     * @apiGroup DimensionItem
     * @apiParam {Long} dimensionItemId  待删除的id
     */
    @DeleteMapping("/{dimensionItemId}")
    public void deleteDimensionById(@PathVariable(value = "dimensionItemId") Long dimensionItemId){
        log.debug("REST request to delete dimensionItem : {}", dimensionItemId);
        dimensionItemService.deleteById(dimensionItemId);
    }

    /**
     * @api {GET} /api/dimension/item/{dimensionItemId} 【维值】详情查询
     * @apiGroup DimensionItem
     * @apiParam {Long} dimensionItemId  维值id
     * @apiSuccessExample {json} 成功返回值:
     * {
     *     "dimensionItem": {
     *         "id": "1077134099691466754",
     *         "createdDate": "2018-12-24T17:29:16.208+08:00",
     *         "createdBy": "1",
     *         "lastUpdatedDate": "2018-12-24T17:32:12.116+08:00",
     *         "lastUpdatedBy": "1",
     *         "versionNumber": 2,
     *         "enabled": true,
     *         "dimensionItemCode": "code1",
     *         "dimensionItemName": "name1",
     *         "dimensionId": "23423141242",
     *         "visibleUserScope": 1002
     *     },
     *     "departmentOrUserGroupIdList": [
     *         "235",
     *         "454"
     *     ],
     *     "departmentOrUserGroupList": [
     *          {
     *              "id": "235",
     *              "pathOrName": "name1"
     *          },{
     *              "id": "454",
     *              "pathOrName": "name2"
     *          }
     *      ]
     * }
     */
    @GetMapping("/{dimensionItemId}")
    public DimensionItemRequestDTO getDimensionItemById(@PathVariable(value = "dimensionItemId") Long dimensionItemId){
        return dimensionItemService.getDimensionItemById(dimensionItemId);
    }

    /**
     * @api {GET} /api/dimension/item/page/by/cond 【维值】条件查询
     * @apiGroup DimensionItem
     * @apiParam {Long} dimensionId  维度id
     * @apiParam {String} [dimensionItemCode]  维值代码
     * @apiParam {String} [dimensionItemName]  维值名称
     * @apiParam {Boolean} [enabled]  维值代码
     * @apiParam {int} page 分页page
     * @apiParam {int} size 分页size
     * @apiSuccessExample {json} 成功返回值:
     * [
     *     {
     *         "dimensionItem": {
     *             "id": "1077134099691466754",
     *             "createdDate": "2018-12-24T17:29:16.208+08:00",
     *             "createdBy": "1",
     *             "lastUpdatedDate": "2018-12-24T17:32:12.116+08:00",
     *             "lastUpdatedBy": "1",
     *             "versionNumber": 2,
     *             "enabled": true,
     *             "dimensionItemCode": "code1",
     *             "dimensionItemName": "name1",
     *             "dimensionId": "23423141242",
     *             "visibleUserScope": 1002
     *         },
     *         "departmentOrUserGroupIdList": [
     *             "235",
     *             "454"
     *         ],
     *         "departmentOrUserGroupList": [
     *              {
     *                  "id": "235",
     *                  "pathOrName": "name1"
     *              },{
     *                  "id": "454",
     *                  "pathOrName": "name2"
     *              }
     *          ]
     *     }
     * ]
     */
    @GetMapping("/page/by/cond")
    public ResponseEntity<List<DimensionItemRequestDTO>> pageDimensionItemsByDimensionIdAndCond(@RequestParam(value = "dimensionId") Long dimensionId,
                                                                               @RequestParam(value = "dimensionItemCode",required = false) String dimensionItemCode,
                                                                               @RequestParam(value = "dimensionItemName",required = false) String dimensionItemName,
                                                                               @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                               @RequestParam(value = "page",defaultValue = "0") int page,
                                                                               @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<DimensionItemRequestDTO> result = dimensionItemService.pageDimensionItemsByDimensionIdAndCond(dimensionId, dimensionItemCode, dimensionItemName, enabled, queryPage);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(queryPage, "/api/dimension/item/page/by/cond");
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/dimension/item/template 【维值-导入】模板下载
     * @apiGroup DimensionItem
     * @apiSuccess {byte[]} byte excel文件
     */
    @GetMapping(value = "/template")
    public byte[] exportDimensionItemTemplate() {
        InputStream inputStream = null;
        ByteArrayOutputStream bos = null;
        try {
            inputStream = StreamUtil.getResourceStream(DimensionItemImportCode.IMPORT_TEMPLATE_PATH);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            bos.flush();
            workbook.close();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new BizException(RespCode.READ_FILE_FAILED);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                throw new BizException(RespCode.READ_FILE_FAILED);
            }

        }
    }

    /**
     * @api {POST} /api/dimension/item/import 【维值-导入】导入维值
     * @apiGroup DimensionItem
     * @apiParam {MultipartFile} file excel文件
     * @apiParam {Long} dimensionItemId 维度id
     * @apiSuccessExample {json} 成功返回值:
     * {
     *     "transactionOid": "34534645354623"
     * }
     */
    @PostMapping(value = "/import")
    public ResponseEntity<Map<String, UUID>> importDimensionItems(
            @RequestParam("file") MultipartFile file,
            @RequestParam("dimensionId") Long dimensionId) throws Exception {
        try(InputStream in = file.getInputStream()) {
            UUID transactionOid = dimensionItemService.importDimensionItems(in, dimensionId);
            Map<String, UUID> result = new HashMap<>();
            result.put("transactionOid", transactionOid);
            return ResponseEntity.ok(result);
        }catch (IOException e){
            throw new BizException(RespCode.READ_FILE_FAILED);
        }
    }

    /**
     * @api {GET} /api/dimension/item/import/query/result/{transactionId} 【维值-导入】查询导入结果
     * @apiGroup DimensionItem
     * @apiParam {String} transactionId 批次ID
     * @apiSuccessExample {json} 成功返回值:
     * {
     *     "successEntities": 12,
     *     "failureEntities": 1,
     *     "errorData": [
     *          {
     *              "index": 1,
     *              "error": "维值代码重复！"
     *          }
     *     ]
     * }
     */
    @GetMapping("/import/query/result/{transactionId}")
    public ResponseEntity queryResultInfo(@PathVariable("transactionId") String transactionId) throws IOException {
        ImportResultDTO importResultDTO = dimensionItemService.queryResultInfo(transactionId);
        return ResponseEntity.ok(importResultDTO);
    }

    /**
     * @api {GET} /api/dimension/item/import/error/export/{transactionId} 【维值-导入】导出错误信息
     * @apiGroup DimensionItem
     * @apiParam {String} transactionId 批次Id
     * @apiSuccess {byte[]} byte excel文件
     */
    @GetMapping("/import/error/export/{transactionId}")
    public ResponseEntity errorExport(@PathVariable("transactionId") String transactionId) throws IOException {
        return ResponseEntity.ok(dimensionItemService.exportFailedData(transactionId));
    }

    /**
     * @api {DELETE} /api/dimension/item/import/delete/{transactionId} 【维值-导入】取消导入
     * @apiGroup DimensionItem
     * @apiParam {String} transactionId 批次ID
     */
    @DeleteMapping("/import/delete/{transactionId}")
    public ResponseEntity deleteImportData(@PathVariable("transactionId") String transactionId){
        return ResponseEntity.ok(dimensionItemService.deleteImportData(transactionId));
    }

    /**
     * @api {POST} /api/dimension/item/import/confirm/{transactionId} 【维值-导入】确定导入
     * @apiGroup DimensionItem
     * @apiParam {String} transactionId 批次ID
     */
    @PostMapping("/import/confirm/{transactionId}")
    public ResponseEntity confirmImport(@PathVariable("transactionId") String transactionId){
        return ResponseEntity.ok(dimensionItemService.confirmImport(transactionId));
    }

    /**
     * @api {get} /api/dimension/item/export 【维值-导出】导出维值
     * @apiGroup DimensionItem
     * @apiParam {Long} dimensionId 维度id
     * @apiSuccess {byte[]} byte excel文件
     */
    @PostMapping(value = "/export")
    public void exportDimensionItemData(@RequestParam("dimensionId") Long dimensionId,
                                        @RequestBody ExportConfig exportConfig,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws IOException {
        dimensionItemService.exportDimensionItemData(dimensionId, request, response, exportConfig);
    }

    /**
     * @api {post} /api/dimension/item/list/by/ids 【维值】根据id批量查询维值
     * @apiGroup DimensionItem
     * @apiParam {List} dimensionItemIds 维值id
     */
    @PostMapping("/list/by/ids")
    public List<DimensionItem> listDimensionItemsByIds(@RequestBody List<Long> dimensionItemIds){
        return dimensionItemService.listDimensionItemsByIds(dimensionItemIds);
    }

    /**
     * @api {get} /api/dimension/item/list/By/dimensionId/enabled 【维值】根据维度id和enabled字段查询维值
     * @apiGroup DimensionItem
     * @apiParam {Long} dimensionId 维度id
     * @apiParam {Boolean} [enabled] 是否启用
     */
    @GetMapping("/list/By/dimensionId/enabled")
    public List<DimensionItem> listDimensionItemsByDimensionIdAndEnabled(@RequestParam(value = "dimensionId") Long dimensionId,
                                                                         @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                         @RequestParam(value = "companyId",required = false) Long companyId){
        return dimensionItemService.listDimensionItemsByDimensionIdAndEnabled(dimensionId, enabled, companyId);
    }
    /**
     * @api {GET} /api/dimension/item/page/by/dimensionId 【维值】分页查询
     * @apiGroup DimensionItem
     * @apiParam {Long} dimensionId  维度id
     * @apiParam {int} page 分页page
     * @apiParam {int} size 分页size
     */
    @GetMapping("/page/by/dimensionId")
    public ResponseEntity<List<DimensionItem>> pageDimensionItemsByDimensionId(@RequestParam(value = "dimensionId") Long dimensionId,
                                                                                @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                                @RequestParam(value = "dimensionItemName", required = false) String dimensionItemName,
                                                                                @RequestParam(value = "dimensionItemCode", required = false) String dimensionItemCode,
                                                                                @RequestParam(value = "page",defaultValue = "0") int page,
                                                                                @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<DimensionItem> result = dimensionItemService.pageDimensionItemsByDimensionId(dimensionId, queryPage, enabled, dimensionItemName, dimensionItemCode);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity<>(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {post} /api/dimension/item/list/By/dimensionIds/companyId/enabled 【维值】根据维度id集合和enabled字段查询维值
     * @apiGroup DimensionItem
     * @apiParam {Long} dimensionIds 维度id集合
     * @apiParam {Boolean} [enabled] 是否启用
     */
    @PostMapping("/list/By/dimensionIds/companyId/enabled")
    public List<DimensionDetailCO> listDimensionItemsByDimensionIdAndEnabled(@RequestBody List<Long> dimensionIds,
                                                                             @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                             @RequestParam(value = "companyId",required = false) Long companyId){
        return dimensionItemService.listItemsByDimensionIdsAndEnabled(dimensionIds, enabled, companyId);
    }

    /**
     * @api {GET} /api/dimension/item/page/by/dimensionId 【维值】分页查询
     * @apiGroup DimensionItem
     * @apiParam {Long} dimensionId  维度id
     * @apiParam {int} page 分页page
     * @apiParam {int} size 分页size
     */
        @GetMapping("/page/by/dimensionId/enabled/companyId")
    public ResponseEntity<List<DimensionItem>> pageDimensionItemsByDimensionIdAndEnabledAndCompanyId(@RequestParam(value = "dimensionId") Long dimensionId,
                                                                                                     @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                                                     @RequestParam(value = "companyId",required = false) Long companyId,
                                                                                                     @RequestParam(value = "page",defaultValue = "0") int page,
                                                                                                     @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<DimensionItem> result = dimensionItemService.pageDimensionItemsByDimensionIdEnabledCompanyId(dimensionId,enabled,companyId, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity<>(result,httpHeaders, HttpStatus.OK);
    }
}
