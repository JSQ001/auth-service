import { message } from 'antd';
import { queryMessages } from '../services/api';
export default {
  namespace: 'user',

  state: {
    currentUser: {},
    company: {},
    organization: {},
    proFile: {},
    locales: {
      messages: {},
      locale: 'zh-CN',
      loading: true,
    },
    token: '',
  },

  effects: {
    *changeLocale({ payload }, { call, put }) {
      message.loading('正在切换语言...');
      const { data } = yield call(queryMessages, payload);

      const messages = data.reducer((temp, item) => {
        temp[item.keyCode] = item.descriptions;
        return temp;
      }, {});

      yield put({
        type: 'setLocales',
        payload: { messages, local: payload },
      });

      message.destroy();
    },
    *queryMessages({ payload }, { call, put }) {
      message.loading('正在获取语言信息...');
      const { data } = yield call(queryMessages, payload.locale);
      const messages = data.reduce((temp, item) => {
        temp[item.keyCode] = item.descriptions;
        return temp;
      }, {});

      yield put({
        type: 'setLocales',
        payload: { messages, local: payload },
      });
      message.destroy();
    },
  },

  reducers: {
    saveCurrentUser(state, action) {
      return {
        ...state,
        currentUser: action.payload || {},
      };
    },
    saveCompany(state, action) {
      return {
        ...state,
        company: action.payload || {},
        currentUser: {
          ...state.currentUser,
          companyId: action.payload.id,
          companyName: action.payload.name,
        },
      };
    },
    saveDepartment(state, action) {
      return {
        ...state,
        currentUser: {
          ...state.currentUser,
          departmentId: action.payload.id,
          departmentName: action.payload.name,
          departmentPath: action.payload.path,
          departmentOid: action.payload.departmentOid,
        },
      };
    },

    saveOrganization(state, action) {
      return {
        ...state,
        organization: action.payload || {},
      };
    },
    saveProfile(state, action) {
      return {
        ...state,
        proFile: action.payload || {},
      };
    },
    setToken(state, { token }) {
      return {
        ...state,
        token,
      };
    },
    setLocales(state, { payload }) {
      return {
        ...state,
        locales: {
          ...state.locales,
          messages: payload.messages,
          loading: false,
        },
      };
    },
  },
};
