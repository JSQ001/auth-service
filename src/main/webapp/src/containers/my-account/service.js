import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 分页查询账本
   */
  getAccountPages(params) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/book/query`, params);
  },
  //添加,修改账本
  addAccount(type, params) {
    return httpFetch[type](`${config.expenseUrl}/api/expense/book`, params);
  },
  //删除关联
  deleteLinkInvoice(params) {
    return httpFetch.delete(
      `${config.expenseUrl}/api/expense/book?expenseBookId=${params.expenseBookId}&invoiceHeadId=${
        params.invoiceHeadId
      }&invoiceLineId=${params.invoiceLineId}`
    );
  },
};
