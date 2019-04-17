export default {
  namespace: 'activities',

  state: {
    list: [],
  },

  reducers: {
    saveList(state, action) {
      return {
        ...state,
        list: action.payload,
      };
    },
  },
};
