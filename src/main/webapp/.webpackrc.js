const path = require('path');

export default {
  entry: 'src/index.js',
  extraBabelPlugins: [['import', { libraryName: 'antd', libraryDirectory: 'es', style: true }]],
  env: {
    development: {
      extraBabelPlugins: ['dva-hmr'],
    },
  },
  externals: {
    rollbar: 'rollbar',
  },
  alias: {
    components: path.resolve(__dirname, 'src/components/'),
    config: path.resolve(__dirname, 'src/config/config.js'),
    images: path.resolve(__dirname, 'src/images/'),
    styles: path.resolve(__dirname, 'src/styles/'),
    routes: path.resolve(__dirname, 'src/routes/'),
    containers: path.resolve(__dirname, 'src/containers/'),
    widget: path.resolve(__dirname, 'src/components/Widget/'),
    utils: path.resolve(__dirname, 'src/utils/'),
    chooserData: path.resolve(__dirname, 'src/chooser-data/index.js'),
    share: path.resolve(__dirname, 'src/share/'),
    services: path.resolve(__dirname, 'src/services/'),
    template: path.resolve(__dirname, 'src/components/Template/'),
    '@': path.resolve(__dirname, 'src'),
  },
  ignoreMomentLocale: true,
  theme: './src/theme.js',
  html: {
    template: './src/index.ejs',
  },
  lessLoaderOptions: {
    javascriptEnabled: true,
  },
  disableDynamicImport: true,
  es5ImcompatibleVersions: true,
  publicPath: '/',
  hash: true,
  proxy: {
    '/api': {
      target: 'http://localhost:9082',
      // target: 'http://localhost:9083',
      changeOrigin: true,
    },
    '/mdata': {
      target: 'http://localhost:9082',
      changeOrigin: true,
      pathRewrite: {
        '^/mdata': '',
      },
    },
    '/workflow': {
      target: 'http://localhost:9082',
      changeOrigin: true,
      pathRewrite: {
        '^/workflow': '',
      },
    },
    // '/api': {
    // target: 'http://localhost:9083',
    //   changeOrigin: true,
    // },
    '/auth': {
      target: 'http://localhost:9082',
      changeOrigin: true,
      pathRewrite: {
        '^/auth': '',
      },
    },
    '/prepayment': {
      target: 'http://localhost:9082',
      changeOrigin: true,
      pathRewrite: {
        '^/prepayment': '',
      },
    },
    '/contract': {
      target: 'http://101.132.162.31:9081',
      changeOrigin: true,
    },
    '/payment': {
      target: 'http://101.132.162.31:9081',
      changeOrigin: true,
    },
    '/job': {
      target: 'http://115.159.108.80:9081',
      changeOrigin: true,
    },
    '/supplier': {
      target: 'http://101.132.162.31:9081',
      changeOrigin: true,
    },
    '/accounting': {
      target: 'http://101.132.162.31:9081',
      changeOrigin: true,
    },
    '/budget': {
      target: 'http://101.132.162.31:9081',
      changeOrigin: true,
    },
    '/brms': {
      target: 'http://115.159.108.80:9081',
      changeOrigin: true,
    },
    '/location': {
      target: 'http://115.159.108.80:9081',
      changeOrigin: true,
    },
    '/invoice': {
      target: 'http://115.159.108.80:9081/artemis-sit',
      changeOrigin: true,
    },
    '/expense': {
      target: 'http://localhost:9082',
      changeOrigin: true,
      pathRewrite: {
        '^/expense': '',
      },
    },
    '/workbench': {
      target: 'http://101.132.162.31:9081',
      changeOrigin: true,
    },
    '/config': {
      target: 'http://115.159.108.80:9081/artemis-sit',
      changeOrigin: true,
    },
    '/mapUrl': {
      target: 'http://apis.map.qq.com',
    },
    '/receipt': {
      target: 'http://106.15.26.10:10080',
      changeOrigin: true,
    },
    '/tx-manager': {
      target: 'http://115.159.108.80:9081',
      changeOrigin: true,
    },
    '/utils': {
      target: 'http://localhost:3000',
      changeOrigin: true,
    },
    '/tax': {
      target: 'http://101.132.162.31:9081',
      changeOrigin: true,
    },
    // '/tax': {
    //   target: 'http://localhost:9100',
    //   changeOrigin: true,
    //   pathRewrite: { '^/tax': '' },
    // },
    '/fund': {
      target: 'http://101.132.162.31:9081',
      // target: 'http://10.211.110.57:8080',
      changeOrigin: true,
    },
    '/todo': {
      target: 'http://101.132.162.31:9081/workflow',
      changeOrigin: true,
    },
  },
};
