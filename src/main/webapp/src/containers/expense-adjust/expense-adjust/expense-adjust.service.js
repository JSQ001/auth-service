/**
 * Created by 13576 on 2018/3/23.
 */
import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 查询费用调整单头（分页）
   * @param ={
   *  expAdjustHeaderNumber:"",
   *  expAdjustTypeId:"",
   *  status:"",
   *  dateTimeFrom:"",
   *  dateTimeTo:"",
   *  amountMin:"",
   *  amountMax:"",
   *  employeeId:"",
   *  language:"",
   *  page:"",
   *  size:"",
   * }
   * */
  getExpenseAdjustHead(params) {
    return httpFetch.get(`${config.baseUrl}/api/expense/adjust/headers/query/dto`, params);
  },

  /**
   * 查询费用调整单头(单个)
   * */
  getExpenseAdjustHeadById(id) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/adjust/headers/query/id?expAdjustHeaderId=${id}`
    );
  },

  /**
   * 添加费用调整单头
   * */
  addExpenseAdjustHead(params) {
    return httpFetch.post(`${config.expenseUrl}/api/expense/adjust/headers`, params);
  },

  /**
   * 修改费用调整单头
   * */
  upExpenseAdjustHead(params) {
    return httpFetch.put(`${config.expenseUrl}/api/expense/adjust/headers`, params);
  },

  /**
   * 删除费用调整单
   * */
  deleteExpenseAdjustHead(id) {
    return httpFetch.delete(`${config.expenseUrl}/api/expense/adjust/headers/${id}`);
  },

  /**
   * 查询费用调整单行(分页)
   * @param = {
   *  expAdjustHeaderId:"",
   *  page:0,
   *  size:10
   * }
   * */
  getExpenseAdjustLine(params) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/adjust/lines/query/dto/by/header/id`,
      params
    );
  },

  /**
   * 查询费用调整单行(单个)
   * */
  getExpenseAdjustLineById(id) {
    return httpFetch.get(`${config.baseUrl}/api/expense/adjust/lines/query/dto/by/id?id=${id}`);
  },

  /**
   * 删除费用调整单行
   * */
  deleteExpenseAdjustLine(id) {
    return httpFetch.delete(`${config.expenseUrl}/api/expense/adjust/lines/${id}`);
  },

  /**
   * 添加费用调整单行
   * */
  addExpenseAdjustLine(params) {
    return httpFetch.post(`${config.expenseUrl}/api/expense/adjust/lines`, params);
  },

  //获取费用类型
  getExpenseTypes(params) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/adjust/types/queryExpenseAdjustType`,
      params
    );
  },

  /*
  * 获取维度和对应的维值
  * */
  getDimensionAndValue(headerId) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/adjust/headers/query/dimension/dto?headerId=${headerId}`
    );
  },

  /*根据id查询费用类型*/
  getExpenseAdjustTypeById(id) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/adjust/types/${id}`);
  },

  getDeptByOid(oid) {
    return httpFetch.get(`${config.mdataUrl}/api/departments/${oid}`);
  },

  /**
   * 提交前检查预算
   * @param {*} id
   */
  checkBudgetAndSubmit(id) {
    let url = `${config.baseUrl}/api/expense/adjust/headers/check/budget/${id}`;
    return httpFetch.post(url);
  },

  //提交费用调整单单据（走工作流）
  submitOnWorkflow(id, ignoreBudgetWarningFlag) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/adjust/headers/preSubmit?headerId=${id}`
    );
  },
  //提交费用调整单单据（走工作流）
  forceSubmitOnWorkflow(params) {
    return httpFetch.post(`${config.expenseUrl}/api/expense/adjust/headers/submit`, params);
  },

  /**
   * 撤回
   * @param {*} params
   */
  withdraw(params) {
    let url = `${config.workflowUrl}/api/workflow/withdraw`;
    return httpFetch.post(url, params);
  },

  /**
   * 拒绝
   * @param {*} params
   */
  reject(params) {
    let url = `${config.workflowUrl}/api/workflow/reject`;
    return httpFetch.post(url, params);
  },

  /**
   * 通过
   * @param {*} params
   */
  pass(params) {
    let url = `${config.workflowUrl}/api/workflow/pass`;
    return httpFetch.post(url, params);
  },

  /**
   * 获取走工作流的审批历史
   * @param {*} entityOid
   */
  getApproveHistoryWorkflow(entityOid) {
    let url = `${
      config.workflowUrl
    }/api/workflow/approval/history?entityType=801006&entityOid=${entityOid}`;

    return httpFetch.get(url);
  },
  /**
   * 获取审批费用调整单待审批列表
   * @param {*} params
   */
  getAdjustApproveList(params) {
    let url = `${config.expenseUrl}/api/expense/adjust/headers/approvals/filters?`;
    for (let key in params) {
      if (params[key] || params[key] == 0) {
        url += `&${key}=${params[key]}`;
      }
    }
    return httpFetch.get(url);
  },

  //获取凭证信息
  getVoucherInfo(params) {
    return httpFetch.post(
      `${
        config.accountingUrl
      }/api/accounting/gl/journal/lines/query/by/transaction/number?tenantId=${
        params.tenantId
      }&sourceTransactionType=${params.sourceTransactionType}&transactionNumber=${
        params.transactionNumber
      }&page=${params.page}&size=${params.size}`
    );
  },

  //获取导入成功后的分摊行信息
  getImportDetailData(oid) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/adjust/lines/query/temp/by/${oid}`);
  },
  //导入完成
  importData(oid, id) {
    return httpFetch.post(
      `${config.expenseUrl}/api/expense/adjust/lines/import/confirm/${id}/${oid}`
    );
  },
  /**
   * 根据单据id获取申请人信息（新建单据时调用）
   * @param {*} id
   */
  listUserByTypeId(id) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/adjust/types/users?adjustTypeId=${id}`);
  },
  /**
   * 根据用户oid获取公司及部门信息（新建单据时调用）
   * @param {*} userOid
   */
  getUserInfoByTypeId(userOid) {
    return httpFetch.get(`${config.mdataUrl}/api/users/v2/` + userOid);
  },

  /**
   * 查询当前机构下所有已创建的调整单的申请人（查询下拉框)
   * @param {*} params
   */
  getCreatedUserList() {
    return httpFetch.get(`${config.expenseUrl}/api/expense/adjust/headers/query/created`);
  },
  /**
   * 根据单据类型id获得单据类型和维度信息
   * @param {*} expAdjustTypeId
   */
  getTypeAndDimension(expAdjustTypeId) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/adjust/types/query/typeAndDimension/${expAdjustTypeId}`
    );
  },
  /**
   * 根据维度id集合和公司id查询启用的维值
   * @param {*} diemensionIds
   * @param {*} companyId
   */
  getDimensionItemsByIds(diemensionIds, companyId, unitId, userId) {
    let url = `${
      config.mdataUrl
    }/api/dimension/item/list/By/dimensionIds/companyId/enabled?companyId=${companyId}&unitId=${unitId}&userId=${userId}&enabled=true`;
    return httpFetch.post(url, diemensionIds);
  },
};
