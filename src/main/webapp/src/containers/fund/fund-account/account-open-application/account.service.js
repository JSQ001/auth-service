import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取开户银行列表
   */
  getAccountBank() {
    return httpFetch.get(`${config.fundUrl}/api/account/open/codeList/ZJ_OPEN_BANK`);
  },
  /**
   * 根据开户申请头ID集合，批量删除开户申请
   * @param {*} ids
   */
  batchDeleteAccount(list) {
    return httpFetch.delete(`${config.fundUrl}/api/account/open/batch/delete`, list);
  },
  /**
   * hi hi
   * 根据开户申请头ID，删除开户申请
   * @param {*} id
   */
  deleteAccount(id) {
    return httpFetch.delete(`${config.fundUrl}/api/account/open/${id}`);
  },

  /**
   * 获取开户申请头列表
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getAccountHeadList(page, size, searchParams) {
    let url = `${config.fundUrl}/api/account/open/pageByCondition?&page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 根据开户申请头ID，获取开户申请头信息
   * @param {*} headerId
   */
  getAccountHead(headerId) {
    return httpFetch.get(`${config.fundUrl}/api/account/open/${headerId}`);
  },

  /**
   * 根据账套ID，获取账套下公司列表
   * @param {*} setOfBooksId
   */
  getCompanyListByBooksId(setOfBooksId) {
    return httpFetch.get(`${config.baseUrl}/api/company/by/condition?setOfBooksId=${setOfBooksId}`);
  },

  /**
   * 提交开户申请
   * @param {*} list
   */
  submitAccount(list) {
    return httpFetch.put(`${config.fundUrl}/api/account/open/batchSubmit`, list);
  },

  /**
   * 更新开户申请头
   * @param {*} record
   */
  updateHeader(record) {
    return httpFetch.put(`${config.fundUrl}/api/account/open/`, record);
  },

  /**
   * 插入开户申请头
   * @param {*} record
   */
  insertHeader(record) {
    return httpFetch.post(`${config.fundUrl}/api/account/open/`, record);
  },

  /**
   * 获取币种列表
   * @param {*} id
   */
  getCurrencyList(id) {
    // return httpFetch.get(`${config.baseUrl}/api/company/standard/currency/getAll`);
    return httpFetch.get(
      `${
        config.mdataUrl
      }/api/currency/rate/company/standard/currency/getAll/companyOid?companyOid=${id}`
    );
  },

  /**
   * 根据用户oid获取公司及部门信息（新建单据时调用）
   * @param {*} userOid
   */
  getUserInfoByTypeId(userOid) {
    return httpFetch.get(`${config.baseUrl}/api/users/oid/${userOid}`);
  },

  // 根据国家的code获取国家省市的数据
  getCountryDataByCode(code, data) {
    for (let i = 0; i < data.length; i += 1) {
      if (`${data[i].code}` === code) {
        return data[i].children;
      }
    }
    return '';
  },
  // 根据国家code获取国家名字
  getCountryNameByCode(code, data) {
    for (let i = 0; i < data.length; i += 1) {
      if (`${data[i].code}` === code) {
        return data[i].country;
      }
    }
    return '';
  },
  // 根据省的code获取省名
  getStateNameByCode(countryCode, stateCode, data) {
    let country = '';
    for (let i = 0; i < data.length; i += 1) {
      if (`${data[i].code}` === countryCode) {
        country = data[i].children;
        break;
      }
    }
    for (let i = 0; i < country.length; i += 1) {
      if (`${country[i].code}` === stateCode) {
        return country[i].state;
      }
    }
    return '';
  },
  // 根据code获取市名
  getCityNameByCode(countryCode, stateCode, cityCode, data) {
    let country = '';
    let state = '';
    for (let i = 0; i < data.length; i += 1) {
      if (`${data[i].code}` === countryCode) {
        country = data[i].children;
        break;
      }
    }
    for (let i = 0; i < country.length; i += 1) {
      if (`${country[i].code}` === stateCode) {
        state = country[i].children;
        break;
      }
    }
    for (let i = 0; i < state.length; i += 1) {
      if (`${state[i].code}` === cityCode) {
        return state[i].city;
      }
    }
    return '';
  },
  // 获取所有国家列表
  getCountries() {
    const params = {
      // language: lang === 'zh_cn' ? "zh_cn" : "en_us",
      page: 0,
      size: 1000,
    };
    return new Promise((resolve, reject) => {
      httpFetch
        .get(`${config.mdataUrl}/api/localization/query/country`, params)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          // errorMessage(err.response);
          reject(err.response);
        });
    });
  },

  // 获取省
  getStates(params) {
    // let params = {
    //   language: "zh_cn",
    //   code: "",
    //   vendorType: "standard"
    // }
    return new Promise((resolve, reject) => {
      httpFetch
        .get(`${config.baseUrl}/api/localization/query/state`, params)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          // errorMessage(err.response);
          reject(err.response);
        });
    });
  },
  // 获取市
  getCities(params) {
    // let params = {
    //   language: "zh_cn",
    //   code: "",
    //   vendorType: "standard"
    // }
    return new Promise((resolve, reject) => {
      httpFetch
        .get(`${config.baseUrl}/api/localization/query/city`, params)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          // errorMessage(err.response);
          reject(err.response);
        });
    });
  },

  // 获取省和市
  getStatesAndCitys(params) {
    return new Promise((resolve, reject) => {
      httpFetch
        .get(`${config.mdataUrl}/api/localization/query/stateAndCity`, params)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          // errorMessage(err.response);
          reject(err.response);
        });
    });
  },
  // 获取系统代码
  getSysCodes(params) {
    return new Promise((resolve, reject) => {
      httpFetch
        .get(`${config.baseUrl}/api/implement/sysCodeValue/get/by/code`, params)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          // errorMessage(err.response);
          reject(err.response);
        });
    });
  },
  // 测试
  test() {
    return httpFetch.get(`/mock/users`);
  },
};
