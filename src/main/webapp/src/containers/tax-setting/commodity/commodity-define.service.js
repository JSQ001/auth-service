/**
 * Created by 5716 on 2019/3/7.
 */
import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取商品(分页)
   * */
  getCommodity(params) {
    return httpFetch.get(`${config.taxUrl}/api/tax/commodity/pageByCondition`, params);
  },

  /**
   * 保存商品
   * */
  addCommodity(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/commodity`, params);
  },

  /**
   * 修改商品
   * */
  upDateCommodity(params) {
    return httpFetch.put(`${config.taxUrl}/api/tax/commodity`, params);
  },
};
