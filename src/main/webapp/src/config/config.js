const config = {
  appEnv: 'dev',

  /** jiu.zhao 蚂蚁定制 环境IP */
  authUrl: 'http://30.50.48.99:9082',
  baseUrl: 'http://30.50.48.99:9082',
  expenseUrl: 'http://30.50.48.99:9082',
  mdataUrl: 'http://30.50.48.99:9082',
  prePaymentUrl: 'http://30.50.48.99:9082',
  workflowUrl: 'http://30.50.48.99:9082',
  /** jiu.zhao 蚂蚁定制 */

  // baseUrl: 'http://139.224.2.45:11024',
  //baseUrl: 'http://101.132.92.213:9083',
  receiptUrl: 'http://106.15.26.10:10080/receipt',
  budgetUrl: '/budget',
  pushUrl: 'http://139.224.2.45:11024/push',
  accountingUrl: '/accounting',
  // payUrl: 'http://116.228.77.183:25297/payment',
  payUrl: '/payment',
  contractUrl: '/contract',
  // vendorUrl:`http://116.228.77.183:25297/vendor`, //供应商url    +  /vendor-info-service
  localUrl: `http://localhost:9095`,
  locationUrl: `/`,
  mapUrl: '/mapUrl',
  ssoUrl: 'http://139.224.2.45:11059',
  wsUrl: 'ws://http://106.15.26.10:10080',
  expAdjustUrl: 'http://116.228.77.183:25297',
  //baseUrl:'http://116.228.77.183:25297',
  jobUrl: '/job',
  vendorUrl: '/supplier',
  brmsUrl: '/',
  // Settings configured here will be merged into the final config object.
  mapKey: 'E5XBZ-LWVWJ-2TUFJ-F73PP-VS5LS-W3FUM',
  txManagerUrl: '/tx-manager',
  workbenchUrl: '/workbench',
  expensePolicyUrl: '/expense-policy',
  taxUrl: '/tax',
  fundUrl: '/fund',
  todoUrl: '/workflow',
  peripheralUrl: '/peripheral',
};

export default config;
