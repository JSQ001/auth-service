export default {
  namespace: 'workbench',

  state: {
    businessType: {},
  },

  reducers: {
    setBusinessType(state, action) {
      let { businessType } = action;
      return {
        ...state,
        businessType,
      };
    },
  },
};
