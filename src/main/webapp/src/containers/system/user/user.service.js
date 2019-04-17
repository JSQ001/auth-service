import config from 'config';
import httpFetch from 'share/httpFetch';
import errorMessage from 'share/errorMessage';

export default {
  // 新建用户
  newUser(params) {
    return new Promise((resolve, reject) => {
      httpFetch
        .post(`${config.baseUrl}/api/user`, params)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          errorMessage(err.response);
          reject(err.response);
        });
    });
  },
  // 编辑用户信息
  updateUser(params) {
    return new Promise((resolve, reject) => {
      httpFetch
        .put(`${config.baseUrl}/api/user`, params)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          errorMessage(err.response);
          reject(err.response);
        });
    });
  },
  // 获取用户列表
  getUserList(page, size, searchParams) {
    let url = `${config.baseUrl}/api/user/search/all?page=${page}&size=${size}`;
    for (const searchName in searchParams) {
      if ({}.hasOwnProperty.call(searchParams, searchName)) {
        url += searchParams[searchName] ? `&${searchName}=${searchParams[searchName]}` : '';
      }
    }
    return httpFetch.get(url);
  },
  // 根据id获取值用户信息
  getUser(id) {
    return new Promise((resolve, reject) => {
      httpFetch
        .get(`${config.baseUrl}/api/user/${id}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          errorMessage(err.response);
          reject(err.response);
        });
    });
  },
};
