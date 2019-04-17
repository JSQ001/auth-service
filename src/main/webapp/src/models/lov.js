export default {
  namespace: 'lov',

  state: {
    data: {},
  },

  reducers: {
    addLovData(state, action) {
      return {
        ...state,
        data: { ...state.data, ...action.payload },
      };
    },
  },
};
