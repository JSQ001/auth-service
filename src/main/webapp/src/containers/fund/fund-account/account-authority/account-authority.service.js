import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取开户申请头列表
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getAccountHeadList(page, size) {
    const url = `${
      config.fundUrl
    }/api/account/right/base/pageByCondition?page=${page}&size=${size}`;
    // console.log(searchParams);
    // const url = `http://192.168.1.188:9099/api/account/right/base/pageByCondition?&page=${page}&size=${size}`;
    return httpFetch.get(url);
  },

  /**
   * 搜索
   */
  searchList(page, size, searchParams) {
    const url = `${
      config.fundUrl
    }/api/account/right/base/pageByCondition?page=${page}&size=${size}&employeeId=${
      searchParams.employeeId
    }`;
    const arr = [];
    arr.push(searchParams.employeeId);

    // const url = `http://192.168.1.188:9099/api/account/right/base/pageByCondition?&page=${page}&size=${size}`;
    return httpFetch.get(url);
  },

  searchList1(searchParams) {
    const url = `${
      config.fundUrl
    }/api/account/right/base/pageByCondition?&employeeId=${searchParams}`;
    // const url = `http://192.168.1.188:9099/api/account/right/base/pageByCondition?&page=${page}&size=${size}`;
    return httpFetch.get(url);
  },

  /**
   * 新增
   */
  add(params) {
    // console.log(params)
    const url = `${config.fundUrl}/api/account/right/base`;
    // const url = `http://192.168.1.188:9099/api/account/right/base`;
    return httpFetch.post(url, params);
  },

  /**
   *
   * @param {*} employeeId
   * 根据employeeId获取头
   */
  getAccountHead(employeeId) {
    const url = `${config.fundUrl}/api/account/right/base/pageByCondition?employeeId=${employeeId}`;

    // const url = `http://192.168.1.188:9099/api/account/right/base/pageByCondition?&page=${page}&size=${size}`;
    return httpFetch.get(url);
  },

  /**
   * 得到用户的银行账号信息
   */
  getBankData(name) {
    return httpFetch.get(
      `${
        config.fundUrl
      }/prepayment/api/cash/prepayment/requisitionHead/getReceivablesByName?name=${name}`
    );
  },

  /**
   * 获取银行账户列表
   */
  getAccountOpenMaintenanceList(page, size, data = '') {
    const url = `${
      config.fundUrl
    }/api/account/maintain/normal/pageByCondition?page=${page}&size=${size}&accountNumber=${data}`;
    return httpFetch.get(url);
  },

  /**
   * 分配行查询
   */
  getDistributionList(baseId, data = '') {
    const url = `${
      config.fundUrl
    }/api/account/right/line/pageByCondition?baseId=${baseId}&accountNumber=${data}`;
    // const url = `http://10.211.110.100:9099/api/account/right/line/pageByCondition?baseId=${baseId}&accountNumber=${data}`;
    return httpFetch.get(url);
  },

  /**
   * 分配行新建
   */
  distributionSave(params) {
    const url = `${config.fundUrl}/api/account/right/line`;
    // const url = `http://10.211.110.100:9099/api/account/right/line`;
    return httpFetch.post(url, params);
  },
  /**
   * 分配行删除
   * @param {*} selectedRowKeys
   */
  batchDeleteAccount(selectedRowKeys) {
    const url = `${config.fundUrl}/api/account/right/line/batch/delete`;
    return httpFetch.delete(url, selectedRowKeys);
  },
};
