import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 头行新建或更新
   * @param {*} record
   */
  baseInfoCreateOrUpdate(record) {
    // const url = `http://10.211.110.100:9099/api/cp/adjust/formal/baseInfo/createOrUpdate`;
    const url = `${config.fundUrl}/api/cp/adjust/formal/baseInfo/createOrUpdate`;
    return httpFetch.post(url, record);
  },

  /**
   * 查询单个单据明细
   */
  getBaseInfoHead(id) {
    // const url = `http://10.211.110.100:9099/api/cp/adjust/formal/baseInfo/queryByBaseId/?baseId=${id}`;

    const url = `${config.fundUrl}/api/cp/adjust/formal/baseInfo/queryByBaseId/?baseId=${id}`;
    return httpFetch.get(url);
  },

  /**
   * 删除行
   */
  batchDelete(params) {
    const url = `${config.fundUrl}/api/cp/adjust/formal/lineInfo/batchDelete`;
    return httpFetch.delete(url, params);
  },
};
