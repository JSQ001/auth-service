/* eslint-disable no-undef */
function setLocale(lang) {
  if (lang !== undefined && !/^([a-z]{2})-([A-Z]{2})$/.test(lang)) {
    // for reset when lang === undefined
    throw new Error('setLocale lang format error');
  }
  if (getLocale() !== lang) {
    window.localStorage.setItem('umi_locale', lang || '');
    window.location.reload();
  }
}

function getLocale() {
  return window.g_lang;
}

let intl = {
  formatMessage: () => {
    return null;
  },
};

/* eslint-disable */
function _setIntlObject(theIntl) {
  intl = theIntl;
}

function formatMessage() {
  return intl.formatMessage.call(intl, ...arguments);
}

export * from 'react-intl';

export { formatMessage, setLocale, getLocale, _setIntlObject };
