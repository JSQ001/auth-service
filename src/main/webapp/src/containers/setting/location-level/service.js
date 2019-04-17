import config from 'config';
import httpFetch from 'share/httpFetch';
import SobService from 'containers/finance-setting/set-of-books/set-of-books.service';

export default {
  /**
   * 获得当前租户下所有账套
   */
  getTenantAllSob(params) {
    return new Promise(function(resolve, reject) {
      SobService.getTenantAllSob(params)
        .then(function(res) {
          resolve(res);
        })
        .catch(function(err) {
          errorMessage(err.response);
          reject(err.response);
        });
    });
  },
  /**
   * 获得地点级别列表
   */
  getLocationLevelList(params) {
    return httpFetch.get(`${config.mdataUrl}/api/location/level/query`, params);
  },

  /**
   * 保存地点级别
   */
  createLocationLevel(params) {
    return httpFetch.post(`${config.mdataUrl}/api/location/level`, params);
  },

  /**
   * 更新地点级别
   * @param {*} params
   */
  updateLocationLevel(params) {
    return httpFetch.put(`${config.mdataUrl}/api/location/level`, params);
  },
  /**
   * 获取国家列表
   */
  getCountryList() {
    return httpFetch.get(`${config.mdataUrl}/api/location/level/query/country`);
  },
  /**
   * 获取省列表
   * @param {*} params
   */
  getStateListByCountryCode(countryCode) {
    return httpFetch.get(
      `${config.mdataUrl}/api/location/level/query/state?countryCode=${countryCode}`
    );
  },
  /**
   * 获取市列表
   * @param {*} params
   */
  getCityListByStateCode(stateCode) {
    return httpFetch.get(`${config.mdataUrl}/api/location/level/query/city?stateCode=${stateCode}`);
  },

  //分配地点
  distributeLocation(params) {
    return httpFetch.post(`${config.mdataUrl}/api/location/level/distribute/location`, params);
  },

  deleteLocationLevelAssignByIds(ids) {
    return httpFetch.delete(
      `${config.mdataUrl}/api/location/level/deleteLocationLevelAssignByIds`,
      ids
    );
  },
};
