import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 新增转交
   * @param {*} params
   */
  getProjectType(params) {
    return httpFetch.get(`${config.contractUrl}/api/project/type/query`, params);
  },
  createProjectType(params) {
    return httpFetch.post(
      `${config.contractUrl}/api/project/type/${params[0].setOfBooksId}`,
      params
    );
  },
  updateProjectType(params) {
    return httpFetch.put(`${config.contractUrl}/api/project/type/${params.setOfBooksId}`, params);
  },
};
