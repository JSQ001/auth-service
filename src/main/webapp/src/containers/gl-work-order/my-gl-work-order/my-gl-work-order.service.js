import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 首页查询
   */
  workOrderHeadQuery(params) {
    return httpFetch.get(
      `${config.accountingUrl}/api/general/ledger/work/order/head/query`,
      params
    );
  },
  /**
   * 获取核算工单类型集合
   */
  getTypeList(userId) {
    return httpFetch.get(
      `${
        config.accountingUrl
      }/api/general/ledger/work/order/types/queryByEmployeeId?userId=${userId}&enabled=true`
    );
  },
  /**
   * 获取币种集合
   */
  getCurrency(setOfBooksId, tenantId) {
    return httpFetch.get(
      `${
        config.mdataUrl
      }/api/currency/rate/list?enable=true&setOfBooksId=${setOfBooksId}&tenantId=${tenantId}`
    );
  },
  /**
   * 根据oid获取id-部门
   */
  getDepartmentId(departmentOid) {
    return httpFetch.get(`${config.mdataUrl}/api/departments/${departmentOid}`);
  },
  /**
   * 核算工单新增更新
   */
  orderInsert(params) {
    return httpFetch.post(`${config.accountingUrl}/api/general/ledger/work/order/head`, params);
  },
  /**
   * 根据头id获取单据头信息
   */
  getHeaderData(id, page, size) {
    return httpFetch.get(
      `${config.accountingUrl}/api/general/ledger/work/order/head/${id}?page=${page}&size=${size}`
    );
  },
  /**
   * 核算工单行保存更新
   */
  saveLineData(params) {
    return httpFetch.post(
      `${config.accountingUrl}/api/general/ledger/work/order/head/insertOrUpdateLine`,
      params
    );
  },
  /**
   * 核算工单行删除
   */
  delLineData(lineId) {
    return httpFetch.delete(
      `${config.accountingUrl}/api/general/ledger/work/order/head/delete/line/${lineId}`
    );
  },
  /**
   * 获取审批历史
   */
  getHistory(documentOid) {
    return httpFetch.get(
      `${
        config.workflowUrl
      }/api/workflow/approval/history?entityType=801008&entityOid=${documentOid}`
    );
  },
  /**
   * 核算工单整单删除
   */
  delDocument(headerId) {
    return httpFetch.delete(
      `${config.accountingUrl}/api/general/ledger/work/order/head/delete/head/line/by/${headerId}`
    );
  },
  /**
   * 核算工单整单提交
   */
  submitDocument(params) {
    return httpFetch.post(
      `${config.accountingUrl}/api/general/ledger/work/order/head/submit`,
      params
    );
  },
  /**
   *工作流撤回
   */
  approvalsWithdraw(params) {
    return httpFetch.post(`${config.workflowUrl}/api/workflow/withdraw`, params);
  },
  /**
   * 导入确定
   */
  importOk(transactionID) {
    return httpFetch.post(
      `${
        config.accountingUrl
      }/api/general/ledger/work/order/head/import/new/confirm/${transactionID}`
    );
  },
  /**
   * 根据单据id获取申请人信息（新建单据时调用）
   * @param {*} id
   */
  listUserByTypeId(id) {
    return httpFetch.get(
      `${config.accountingUrl}/api/general/ledger/work/order/types/users?workOrderTypeId=${id}`
    );
  },
  /**
   * 根据用户oid获取公司及部门信息（新建单据时调用）
   * @param {*} userOid
   */
  getUserInfoByTypeId(userOid) {
    return httpFetch.get(`${config.mdataUrl}/api/users/oid/` + userOid);
  },

  /**
   * 查询当前机构下所有已创建的预付款单的申请人（查询下拉框)
   * @param {*} params
   */
  getCreatedUserList() {
    return httpFetch.get(
      `${config.accountingUrl}/api/general/ledger/work/order/head/query/created`
    );
  },
};
