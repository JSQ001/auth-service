export default {
  namespace: 'languages',

  state: {
    languages: {},
    languageType: [],
    local: '',
    languageList: [],
    applicationCode: '',
  },

  reducers: {
    selectLanguage(state, action) {
      let { local, languages } = action.payload;
      return {
        ...state,
        languages,
        local,
      };
    },
    setLanguages(state, action) {
      let { languages } = action.payload;
      return {
        ...state,
        languages,
      };
    },
    setLanguageList(state, action) {
      let { languageList } = action.payload;
      return {
        ...state,
        languageList,
      };
    },
    setLanguageType(state, action) {
      let { languageType } = action.payload;
      return {
        ...state,
        languageType,
      };
    },
    setApplicationCode(state, action) {
      let { applicationCode } = action.payload;
      return {
        ...state,
        applicationCode,
      };
    },
    setLocal(state, action) {
      let { local } = action.payload;
      return {
        ...state,
        local,
      };
    },
  },
};
