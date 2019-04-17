export default {
  namespace: 'menu',

  state: {
    menuList: [],
    routerData: {},
    currentMenuId: '',
    currentMenuParams: '',
    functionMap: {},
  },

  reducers: {
    setMenu(state, { payload }) {
      return {
        ...state,
        menuList: payload.menuList,
        routerData: payload.routerData,
        functionMap: payload.functionMap,
      };
    },
    setCurrentMenuId(state, { payload }) {
      return {
        ...state,
        currentMenuId: payload.menuId,
        currentMenuParams: payload.menuParams,
      };
    },
  },
};
