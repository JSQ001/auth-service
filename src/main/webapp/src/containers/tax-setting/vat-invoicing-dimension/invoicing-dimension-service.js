import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 新增开票权限
  insertInvoicingDimension(params) {
    return httpFetch.post(`${config.taxUrl}/api/invoicing/dimension/insert/data`, params);
  },

  // 根据id删除数据
  deleteInvoicingDimensionById(id) {
    return httpFetch.get(`${config.taxUrl}/api/invoicing/dimension/${id}`);
  },
  deleteInvoicingDimensionBatch(ids) {
    return httpFetch.delete(`${config.taxUrl}/api/invoicing/dimension/batch/delete`, ids);
  },

  // 修改开票点
  // updateInvoicingSite(params) {
  //   return httpFetch.put(`${config.taxUrl}/api/invoicing/dimension`, params);
  // },

  // 根据开票点ID获取开票权限信息
  pageInvoicingSiteByCond(params) {
    return httpFetch.get(`${config.taxUrl}/api/invoicing/dimension/pageByCondition`, params);
  },
};
