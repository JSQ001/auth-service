import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * hi hi
   * 根据id 删除联行号
   * @param {*} list
   */
  deleteList(id) {
    return httpFetch.delete(`${config.fundUrl}/api/csh/bank/numbers/deleteById/${id}`);
    // return httpFetch.delete(`http://10.211.97.86:9099/api/csh/bank/numbers/deleteById/${id}`);
  },

  /* eslint-disable */
  /**
   * 联行号维护查询
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getCshBankNumbersList(page, size, searchParams) {
    // let url = `http://10.211.97.86:9099/api/csh/bank/numbers/getBankNumbersList?&page=${page}&size=${size}`;
    let url = `${
      config.fundUrl
    }/api/csh/bank/numbers/getBankNumbersList?&page=${page}&size=${size}`;

    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 插入或更新联行号信息
   * @param {*} record
   */
  createOrUpdate(record) {
    // return httpFetch.post(`${config.fundUrl}/api/cp/transfer/appl/header/createOrUpdate`, record);
    return httpFetch.post(
      //   `http://10.211.97.86:9099/api/csh/bank/numbers/createOrUpdate`,
      `${config.fundUrl}/api/csh/bank/numbers/createOrUpdate`,
      record
    );
  },

  /**
   * 根据ID获取联行号详情
   */
  getModifyAccountDetail(id) {
    //
    return httpFetch.get(`${config.fundUrl}/api/csh/bank/numbers/getBankNumbersById/${id}`);
    // return httpFetch.get(
    //   `http://10.211.97.86:9099/api/csh/bank/numbers/getBankNumbersById/${id}`
    // );
  },

  /**
   * 根据省描述获取code
   */
  getProvinceCode(value) {
    //http://101.132.162.31:9081/fund/api/regionDefines/getPACcond?param=北京
    return httpFetch.get(`${config.fundUrl}/api/regionDefines/getPACcond?param=${value}`);
    // return httpFetch.get(
    //   `http://10.211.97.86:9099/api/csh/bank/numbers/getBankNumbersById/${id}`
    // );
  },

  /**
   * 根据市描述获取code
   */
  getCityCode() {
    // return httpFetch.get(`${config.fundUrl}/api/regionDefines/getCity/${id}`);
    return httpFetch.get(`http://10.211.98.2:9099/api/regionDefines/getProvinces`);
  },

  // 根据国家code获取国家名字
  getCountryNameByCode(code, data) {
    for (let i = 0; i < data.length; i++) {
      if (data[i].code + '' === code) {
        return data[i].country;
      }
    }
    return '';
  },

  // 根据省的code获取省名
  getStateNameByCode(countryCode, stateCode, data) {
    var country = '';
    for (let i = 0; i < data.length; i++) {
      if (data[i].code + '' === countryCode) {
        country = data[i].children;
        break;
      }
    }
    for (let i = 0; i < country.length; i++) {
      if (country[i].code + '' === stateCode) {
        return country[i].state;
      }
    }
    return '';
  },

  // 根据code获取市名
  getCityNameByCode(countryCode, stateCode, cityCode, data) {
    var country = '';
    var state = '';
    for (let i = 0; i < data.length; i++) {
      if (data[i].code + '' === countryCode) {
        country = data[i].children;
        break;
      }
    }
    for (let i = 0; i < country.length; i++) {
      if (country[i].code + '' === stateCode) {
        state = country[i].children;
        break;
      }
    }
    for (let i = 0; i < state.length; i++) {
      if (state[i].code + '' === cityCode) {
        return state[i].city;
      }
    }
    return '';
  },

  // 根据国家的code获取国家省市的数据
  getCountryDataByCode(code, data) {
    console.log(data);
    for (let i = 0; i < data.length; i++) {
      if (data[i].code + '' === code) {
        return data[i].children;
      }
    }
    return '';
  },
};
