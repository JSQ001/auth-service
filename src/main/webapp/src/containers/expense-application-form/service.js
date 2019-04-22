import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 查询当前机构下所有的申请单类型（查询下拉框)
   * @param {*} params
   */
  getApplicationTypeList(params) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/application/type/query/condition/user`,
      params
    );
  },
  /**
   * 查询当前机构下所有已创建的申请单类型（查询下拉框)
   * @param {*} params
   */
  getCreatedApplicationTypeList(params) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/type/query/created`, params);
  },
  /**
   * 获取币种列表
   * @param {*} id
   */
  getCurrencyList(id) {
    // return httpFetch.get(`${config.mdataUrl}/api/currency/rate/company/standard/currency/getAll`);
    return httpFetch.get(
      `${
        config.mdataUrl
      }/api/currency/rate/company/standard/currency/getAll/companyOid?companyOid=${id}`
    );
  },

  /**
   * 获取维度值
   * @param {*} parmas
   */
  getDimensionValues(id, companyId) {
    return httpFetch.get(`${config.mdataUrl}/api/dimension/item/list/By/dimensionId/enabled`, {
      dimensionId: id,
      enabled: true,
      companyId: companyId,
    });
  },

  /**
   * 获取申请单类型详情
   * @param {*} typeId
   */
  getApplicationTypeById(typeId) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/type/query/` + typeId);
  },

  /**
   * 创建一个费用申请单头
   * @param {*} params
   */
  addExpenseApplictionForm(params) {
    return httpFetch.post(`${config.expenseUrl}/api/expense/application/header`, params);
  },

  /**
   * 根据单据头ID查询单据头详情
   * @param {*} id
   */
  getApplicationDetail(id) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/header/` + id);
  },

  /**
   * 删除申请单
   * @param {*} id
   */
  deleteExpenseApplication(id) {
    return httpFetch.delete(`${config.expenseUrl}/api/expense/application/header/` + id);
  },

  /**
   * 根据单据头ID分页查询单据行信息
   * @param {*} params
   */
  getApplicationLines(id, params) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/line/query/` + id, params);
  },

  /**
   * 申请单行创建时查询维度信息默认值
   * @param {*} params
   */
  getNewInfo(params) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/line/query/info`, params);
  },

  /**
   * 新增申请单行
   * @param {*} params
   */
  addApplicationLine(params) {
    return httpFetch.post(`${config.expenseUrl}/api/expense/application/line`, params);
  },

  /**
   * 编辑申请单行
   * @param {*} params
   */
  updateApplicationLine(params) {
    return httpFetch.put(`${config.expenseUrl}/api/expense/application/line`, params);
  },

  /**
   * 获取审批历史
   * @param {*} oid
   */
  getHistory(oid) {
    return httpFetch.get(
      `${config.baseUrl}/api/budget/journa/reports/history?entityType=801009&entityOID=` + oid
    );
  },

  // /**
  // * 校验预算
  // * @param {*} oid
  // */
  // checkBudget(id) {
  //   return httpFetch.post(`${config.expenseUrl}/api/expense/application/header/submit/check/budget?id=` + id);
  // },

  /**
   * 提交
   * @param {*} params
   */
  submit(params, flag) {
    let url = `${config.expenseUrl}/api/expense/application/submit?ignoreWarningFlag=${flag}`;
    return httpFetch.post(url, params);
  },

  /**
   * 校验费用政策
   * @param {*} params
   */
  checkPolicy(id) {
    let url = `${config.expenseUrl}/api/expense/application/header/submit/check/policy?id=${id}`;
    return httpFetch.get(url);
  },

  /**
   * 删除行数据
   * @param {*} id
   */
  deleteLine(id) {
    return httpFetch.delete(`${config.expenseUrl}/api/expense/application/line/` + id);
  },

  /**
   * 根据ID查询申请单头信息(编辑申请单头时调用)
   * @param {*} id
   */
  getEditInfo(id) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/header/query?id=` + id);
  },

  /**
   * 更新申请单头
   * @param {*} params
   */
  updateHeaderData(params) {
    return httpFetch.put(`${config.expenseUrl}/api/expense/application/header`, params);
  },

  /**
   * 撤回
   * @param {*} params
   */
  withdraw(params) {
    return httpFetch.post(`${config.workflowUrl}/api/workflow/withdraw`, params);
  },

  /**
   * 获取动态维度表头信息
   * @param {*} id
   */
  getColumnInfo(id) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/line/column/` + id);
  },

  /**
   * 根据单据id获取申请人信息（新建单据时调用）
   * @param {*} id
   */
  listUserByTypeId(id) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/application/type/users?applicationTypeId=${id}`
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
   * 导出
   * @param {*} params
   * @param {*} searchParams
   */
  export(params, searchParams) {
    let url = `${config.expenseUrl}/api/expense/application/header/closed/export?roleType=TENANT`;
    for (let searchName in searchParams) {
      url += searchParams[searchName] ? `&${searchName}=${searchParams[searchName]}` : '';
    }
    return httpFetch.post(url, params, {}, { responseType: 'arraybuffer' });
  },
  /**
   * 关闭申请单头
   * @param {*} params
   */
  closedFunction(params) {
    let url = `${config.expenseUrl}/api/expense/application/header/closed`;
    return httpFetch.post(url, params);
  },

  /**
   * 查询当前机构下所有已创建的申请单的申请人（查询下拉框)
   * @param {*} params
   */
  getCreatedApplicationUserList() {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/header/query/created`);
  },
  /**
   * 申请单头新建时查询
   * @param {*} typeId
   */
  getHeaderInfoByNew(typeId) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/application/type/query/header/${typeId}`
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
  /**
   * 关闭行
   * @param {*} lineId 行id
   * @param {*} headerId 头id
   * @param {*} message 信息
   */
  closeLine(lineId, headerId, message) {
    let url = `${
      config.expenseUrl
    }/api/expense/application/line/closed/${lineId}?headerId=${headerId}&message=${message}`;
    return httpFetch.post(url, {});
  },

  /**
   * 根据单据头ID分页查询单据行信息
   * @param {*} id
   * @param {*} params
   */
  getCloseApplicationLines(id, params) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/application/line/close/query/` + id,
      params
    );
  },
  /**
   * 根据id获取城市
   * @param {*} id
   */
  getLocalizationCityById(id) {
    return httpFetch.post(`${config.mdataUrl}/api/location/city/ids`, [id]);
  },
  /**
   * 根据id批量获取用户
   * @param {*} ids
   */
  getUserByIds(ids) {
    return httpFetch.post(`${config.mdataUrl}/api/list/user/batch/ids`, ids);
  },
};
