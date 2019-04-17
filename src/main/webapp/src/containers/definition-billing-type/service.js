import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 根据id获得维度设置信息
   * @param {*} id
   */
  getInfoById(id) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/report/type/${id}`);
  },
  /**
   * 根据id获得分摊设置信息
   * @param {*} id
   */
  getApportionmentById(id) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/report/type/dist/setting/${id}`);
  },
  /**
   * 获取审批流
   * @param {*} id
   */
  getWorkflowList(id) {
    return httpFetch.get(
      `${config.workflowUrl}/api/custom/forms/setOfBooks/my/available/all?formTypeId=801001`,
      id
    );
  },
  /**
   * 获取分配的公司
   * @param {*} id
   */
  getDistributiveCompany(reportTypeId) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/report/type/company/query?reportTypeId=${reportTypeId}`
    );
  },

  /**
   * 更改公司分配状态
   * @param {*} parmas
   */
  updateAssignCompany(parmas) {
    return httpFetch.put(`${config.expenseUrl}/api/expense/report/type/company`, parmas);
  },

  /**
   * 批量分配公司
   * @param {*} parmas
   */
  batchDistributeCompany(parmas) {
    return httpFetch.post(`${config.expenseUrl}/api/expense/report/type/company/batch`, parmas);
  },

  /**
   * 获取维度列表
   * @param {*} id
   */
  getDimensionById(params) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/report/type/dimension/query/by/cond?reportTypeId=${params}`
    );
  },

  /**
   * 获取可关联表单类型
   * @param {*} setOfBooksId
   */
  getFormList(setOfBooksId) {
    return httpFetch.get(
      `${
        config.workflowUrl
      }/api/custom/forms/setOfBooks/my/available/all?formTypeId=801009&setOfBooksId=${setOfBooksId}`
    );
  },

  /**
   * 新建报账单类型
   * @param {*} params
   */
  addApplicationType(params) {
    return httpFetch.post(`${config.expenseUrl}/api/expense/report/type`, params);
  },

  /**
   * 更新报账单单类型
   * @param {*} params
   */
  updateApplicationType(params) {
    // console.log('update',params); return;
    return httpFetch.put(`${config.expenseUrl}/api/expense/report/type`, params);
  },
  /**
   * 获取未分配的维度
   * @param {*} params
   */
  getUndistributedDimensionList(id, params) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/application/type/${id}/dimensions/query/filter`,
      params
    );
  },

  /**
   * 获取报账单未分配的维度
   * @param {*} params
   */
  getReportTypeDimensionList(id, params) {
    return httpFetch.get(
      `${
        config.expenseUrl
      }/api/expense/report/type/dimension/query/not/assign/dimension?reportTypeId=${id}`
    );
  },
  /**
   * 获取维度
   * @param {*} params
   */
  getDimensionList(params) {
    return httpFetch.get(`${config.mdataUrl}/api/dimension/page/by/cond`, params);
  },
  /**
   * 获取维度下的维值
   * @param {*} params
   */
  getDimensionValueList(dimensionId) {
    return httpFetch.get(
      `${
        config.mdataUrl
      }/api/dimension/item/list/By/dimensionId/enabled?dimensionId=${dimensionId}&enabled=true`
    );
  },
  /**
   * 保存维度
   */
  saveDimensionValue(params) {
    return httpFetch.post(`${config.expenseUrl}/api/expense/report/type/dimension`, params);
  },
  /**
   * 删除维度
   */
  deleteDimensionValue(id) {
    return httpFetch.delete(`${config.expenseUrl}/api/expense/report/type/dimension/${id}`);
  },
  /**
   * 修改维度
   */
  editDimensionValue(params) {
    return httpFetch.put(`${config.expenseUrl}/api/expense/report/type/dimension`, params);
  },
  /**
   * 获取付款方式类型列表
   */
  getPayValueList(code) {
    return httpFetch.get(`${config.baseUrl}/api/custom/enumerations/template/by/type?type=${code}`);
  },
  /**
   * 新建分配设置
   */
  saveApportionmentSetting(params) {
    return httpFetch.post(`${config.expenseUrl}/api/expense/report/type/dist/setting`, params);
  },
  /**
   * 编辑分配设置
   */
  editApportionmentSetting(params) {
    return httpFetch.put(`${config.expenseUrl}/api/expense/report/type/dist/setting`, params);
  },
  /**
   * 公司获取默认值
   */
  getCompanyDefaultValue() {
    return httpFetch.get(
      `${
        config.expenseUrl
      }/api/expense/report/type/dist/setting/query/company/by/company/dist/range?companyDistRange=CUSTOM_RANGE&page=0&size=100000`
    );
  },
  /**
   * 部门获取默认值
   */
  getDepartmentDefaultValue() {
    return httpFetch.get(
      `${
        config.expenseUrl
      }/api/expense/report/type/dist/setting/query/department/by/department/dist/range?departmentDistRange=ALL_DEP_IN_TENANT&page=0&size=100000`
    );
  },
  /**
   * 责任中心获取默认值
   */
  getResDefaultValue(id) {
    return httpFetch.get(
      `${
        config.mdataUrl
      }/api/responsibilityCenter/query/default?setOfBooksId=${id}&page=0&size=100000`
    );
  },
};
