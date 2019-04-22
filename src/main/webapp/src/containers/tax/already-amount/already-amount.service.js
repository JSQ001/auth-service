import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // get
  deailCancellInvoice(id) {
    return httpFetch.get(`${config.taxUrl}/tax/vat/tran/write/off/invoice/details/${id}`);
  },
  // get
  getInvoiceTransaction(id) {
    return httpFetch.get(`${config.taxUrl}/tax/vat/tran/write/off/recoil/trading/${id}`);
  },
};
