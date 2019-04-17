import httpFetch from 'share/httpFetch';
import config from 'config';
import { messages } from 'utils/utils';
import { message } from 'antd/lib/index';

export default {
  //获取一级部门列表
  getFirstlevelDep(flagDep) {
    let url = `${config.mdataUrl}/api/departments/root/v2?flag=${flagDep}`;
    return httpFetch.get(url);
  },

  getChildlevelDep(flagDep, departmentOid) {
    let url = `${config.mdataUrl}/api/department/child/${departmentOid}?flag=${flagDep}`;
    return new Promise(function(resolve) {
      httpFetch
        .get(url)
        .then(function(res) {
          resolve(res);
        })
        .catch(function() {
          message.error(messages('common.error1'));
        });
    });
  },

  getSearchResult(keyword) {
    let url = `${
      config.mdataUrl
    }/api/department/user/keyword?keyword=${keyword}&needEmployeeId=false`;
    return httpFetch.get(url);
  },
};
