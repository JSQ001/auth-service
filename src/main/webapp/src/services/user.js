import request from '../utils/request';
import fetch from '../utils/fetch';
import config from 'config';

export async function query() {
  return request(config.baseUrl + '/api/users');
}

export async function queryCurrent() {
  return fetch.get('/api/account');
}
