package com.hand.hcf.app.mdata.department.web;

import com.hand.hcf.app.mdata.department.domain.DepartmentGroupDetail;
import com.hand.hcf.app.mdata.department.service.DepartmentGroupDetailService;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/DepartmentGroupDetail")
public class DepartmentGroupDetailResource {

    private final DepartmentGroupDetailService departmentGroupDetailService;

    public DepartmentGroupDetailResource(DepartmentGroupDetailService departmentGroupDetailService) {
        this.departmentGroupDetailService = departmentGroupDetailService;
    }

    /**
     * 根据部门组明细IdList批量删除
     * @param ids
     * @return
     */
    /**
     * @api {delete} /api/DepartmentGroupDetail/BatchDeleteByIds 批量删除部门组明细
     * @apiGroup DepartmentGroupDetail
     * @apiParam {Array} [ids]  待删除的id数组
     * @apiParamExample {json} Request-Param
     * [905365677773193217,905978182350041090]
     * @apiSuccess {Boolean} success 是否成功
     */

    @Timed
    @RequestMapping(value = "/BatchDeleteByIds",method = RequestMethod.DELETE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteByIds(@RequestBody List<Long> ids){
        return ResponseEntity.ok(departmentGroupDetailService.deleteDepartmentGroupBatch(ids));
    }


    /**
     * @api {post} /api/DepartmentGroupDetail/BatchDeleteByIds 批量新增部门组明细
     * @apiGroup DepartmentGroupDetail
     * @apiParam {Object} list 待新增的实体数组
     * @apiParam {Long} departmentGroupId 部门组id
     * @apiParam {Long} departmentId 部门id
     * @apiParamExample {json} Request-Param
     * [
           {
            "departmentGroupId":"910099842662412289",
            "departmentId":"1"
            },
            {
            "departmentGroupId":"910099842662412289",
            "departmentId":"4"
            }
     * ]
     * @apiSuccess {Boolean} success 是否成功
     */
    @Timed
    @RequestMapping(value="/BatchAddDepartmentGroupDetail",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> batchAdd(@RequestBody List<DepartmentGroupDetail> list){
        return ResponseEntity.ok(departmentGroupDetailService.addDepartmentDetailBatch(list));
    }

}
