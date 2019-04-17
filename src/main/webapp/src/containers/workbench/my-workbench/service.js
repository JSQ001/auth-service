import config from 'config';
import httpFetch from 'share/httpFetch';

const partUrl = '/workbench/api/workbench/business/data';

export default {
  /**
   * 分页条件查询
   * @param {*} page 当前页
   * @param {*} size 每页大小
   * @param {*} businessTypeId 业务类型Id
   * @param {*} searchParams 查询参数
   */
  queryWorkBenchTableData(params) {
    const url = `${config.aliyun || ''}${partUrl}/query`;
    return httpFetch.get(url, params);
  },

  /**
   * 开始工作/结束工作
   * @param {*} bool true开始工作 false结束工作
   */
  changeWorkState(bool) {
    const url = `${config.aliyun || ''}${partUrl}/start/${bool}`;
    return httpFetch.post(url);
  },

  /**
   * 取单
   */
  startGetOrder() {
    const url = `${config.aliyun || ''}${partUrl}`;
    return httpFetch.get(url);
  },

  /**
   * 自动阅单
   * @param {*} bool true自动阅单 false取消自动阅单
   */
  autoCheckOrder(bool) {
    const url = `${config.aliyun || ''}${partUrl}/start/automatic/${bool}`;
    return httpFetch.post(url);
  },

  /**
   * 取消暂挂
   * @param {*} params 参数
   */
  cancelHold(params) {
    const url = `${config.workbenchUrl || ''}/api/workbench/business/data/batch/cancelHold`;
    return httpFetch.post(url, params);
  },

  /**
   * 初始化工作状态
   */
  getInitStatus() {
    const url = `${partUrl}/get/status`;
    return httpFetch.get(url);
  },

  /**
   * 工作台查询已完成订单
   */
  queryHadDealOrder(params) {
    const url = `${config.aliyun || ''}${partUrl}/query/handle`;
    return httpFetch.get(url, params);
  },
};
