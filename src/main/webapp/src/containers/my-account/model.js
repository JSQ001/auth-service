import service from 'containers/my-account/service.js';

export default {
  namespace: 'myAccount',

  state: {
    data: [],
    loading: true,
  },

  effects: {
    *fetchData(params, { call, put }) {
      const response = yield call(() => service.getAccountPages(params));
      yield put({
        type: 'saveData',
        payload: {
          loading: false,
          data: response.data || [],
        },
      });
    },
  },

  reducers: {
    saveData(state, action) {
      return action.payload;
    },
  },
};
