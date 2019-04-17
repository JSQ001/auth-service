import React, { createElement } from 'react';
import PropTypes from 'prop-types';
import { Layout, message, Spin, Tabs } from 'antd';
import DocumentTitle from 'react-document-title';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { ContainerQuery } from 'react-container-query';
import classNames from 'classnames';
import pathToRegexp from 'path-to-regexp';
import { enquireScreen, unenquireScreen } from 'enquire-js';
import GlobalHeader from '../components/GlobalHeader';
import Exception404 from '../routes/Exception/404';

import SiderMenu from '../components/SiderMenu';
import Authorized from '../utils/Authorized';
import { getMenuData } from '../common/menu';
import logo from '../assets/logo.png';

import { isUrl } from '../utils/utils';

import 'styles/common.scss';

const TabPane = Tabs.TabPane;
const { Content, Header } = Layout;
const { check } = Authorized;

/**
 * 根据菜单取得重定向地址.
 */
const redirectData = [];
const getRedirect = item => {
  if (item && item.children) {
    if (item.children[0] && item.children[0].path) {
      redirectData.push({
        from: `${item.path}`,
        to: `${item.children[0].path}`,
      });
      item.children.forEach(children => {
        getRedirect(children);
      });
    }
  }
};
getMenuData().forEach(getRedirect);

/**
 * 获取面包屑映射
 * @param {Object} menuData 菜单配置
 * @param {Object} routerData 路由配置
 */
const getBreadcrumbNameMap = (menuData, routerData) => {
  const result = {};
  const childResult = {};
  for (const i of menuData) {
    if (!routerData[i.path]) {
      result[i.path] = i;
    }
    if (i.children) {
      Object.assign(childResult, getBreadcrumbNameMap(i.children, routerData));
    }
  }
  return Object.assign({}, routerData, result, childResult);
};

const dynamicWrapper = component => {
  // () => require('module')
  // transformed by babel-plugin-dynamic-import-node-sync
  if (component.toString().indexOf('.then(') < 0) {
    return props => {
      return createElement(component().default, {
        ...props,
      });
    };
  }
};

const query = {
  'screen-xs': {
    maxWidth: 575,
  },
  'screen-sm': {
    minWidth: 576,
    maxWidth: 767,
  },
  'screen-md': {
    minWidth: 768,
    maxWidth: 991,
  },
  'screen-lg': {
    minWidth: 992,
    maxWidth: 1199,
  },
  'screen-xl': {
    minWidth: 1200,
    maxWidth: 1599,
  },
  'screen-xxl': {
    minWidth: 1600,
  },
};

let isMobile;
enquireScreen(b => {
  isMobile = b;
});

class BasicLayout extends React.Component {
  static childContextTypes = {
    location: PropTypes.object,
    breadcrumbNameMap: PropTypes.object,
  };

  state = {
    isMobile,
    menus: [],
    routers: [],
    loading: false,
    path: '',
    panes: [],
    activeKey: '',
    selectKey: '',
    error: false,
    errorContent: {},
    menuList: [],
  };

  group = {};
  functionGroup = {};
  pageMap = {};
  refMap = {};

  getChildContext() {
    const { location, routerData } = this.props;
    return {
      location,
      breadcrumbNameMap: getBreadcrumbNameMap(getMenuData(), routerData),
    };
  }

  async componentDidMount() {
    this.enquireHandler = enquireScreen(mobile => {
      this.setState({
        isMobile: mobile,
      });
    });
    // this.reloadRoutes();
  }

  // componentWillReceiveProps(nextProps) {
  //   if (nextProps.location.pathname === this.props.location.pathname) return;

  //   let panes = this.state.panes;
  //   let path = window.location.hash.replace('#', '');

  //   if (path == '/') {
  //     this.props.dispatch(
  //       routerRedux.push({
  //         pathname: '/dashboard',
  //       })
  //     );
  //     return;
  //   }

  //   let component = this.getContent();

  //   if (!component) return;

  //   let index = panes.findIndex(o => o.routeKey == component.routeKey);

  //   const isFunc = component.type === 'function';
  //   if (index >= 0) {
  //     this.refMap[component.pathname] && this.refMap[component.pathname].componentDidShow();
  //     this.setState({
  //       activeKey: component.routeKey,
  //       selectKey: isFunc ? component.pathname : component.fullUrl,
  //     });
  //     this.setCurrentMenuId(component);
  //     return;
  //   }

  //   if (!this.state.activeKey || !panes.length) {
  //     panes.push(component);
  //     this.setState({
  //       panes,
  //       activeKey: component.routeKey,
  //       selectKey: isFunc ? component.pathname : component.fullUrl,
  //     });
  //     this.setCurrentMenuId(component);
  //     return;
  //   }

  //   //即将跳转的页面是已经打开的页面的父页面
  //   index = panes.findIndex(item => item.parent == component.routeKey);

  //   if (index >= 0) {
  //     panes[index] = component;
  //     this.setState({
  //       panes,
  //       activeKey: component.routeKey,
  //       selectKey: isFunc ? component.pathname : component.fullUrl,
  //     });

  //     this.setCurrentMenuId(component);
  //     return;
  //   }

  //   index = panes.findIndex(o => o.routeKey == this.state.activeKey);

  //   // 三种情况  不会打开新tab页
  //   // 1.即将跳转的页面是功能页，并且它的父页面是当前页面
  //   // 2.即将跳转的页面是功能页, 并且当前页面也是功能页面，并且当前页面和即将跳转的页面同属于一个菜单
  //   // 3.即将跳转的页面是当前页面的父页面，一般页面的返回按钮

  //   if (
  //     (component.parent &&
  //       (component.parent === panes[index].parent || component.parent === panes[index].routeKey)) ||
  //     panes[index].parent === component.routeKey
  //   ) {
  //     panes[index] = component;
  //   } else {
  //     panes.push(component);
  //   }

  //   this.setState({
  //     panes,
  //     activeKey: component.routeKey,
  //     selectKey: isFunc ? component.pathname : component.fullUrl,
  //   });

  //   this.setCurrentMenuId(component);
  // }

  setCurrentMenuId = component => {
    this.props.dispatch({
      type: 'menu/setCurrentMenuId',
      payload: { menuId: component.functionId, menuParams: component.param },
    });
  };

  getRef = (ref, key) => {
    this.refMap = { [key]: ref };
  };

  reloadRoutes = () => {
    let path = window.location.hash.replace('#', '');

    let { panes } = this.state;

    if (path == '/') {
      this.props.dispatch(
        routerRedux.push({
          pathname: '/dashboard',
        })
      );
      return;
    }

    if (path != '/dashboard') {
      let dashboard = this.getContent('/dashboard');
      if (dashboard) {
        panes.push(dashboard);
        this.setCurrentMenuId(dashboard);
      }
    }

    let component = this.getContent();

    if (component) {
      let index = panes.findIndex(o => o.routeKey == component.routeKey);
      if (index >= 0) {
        this.setState({ activeKey: path, selectKey: path });
      } else {
        panes.push(component);
        this.setState({
          panes,
          activeKey: component.routeKey,
          selectKey: component.fullUrl || component.pathname,
        });
      }
      this.setCurrentMenuId(component);
    } else {
      this.setState({ panes });
    }
  };

  getContent = path => {
    const {
      menu: { routerData, functionMap },
    } = this.props;
    const pathname = path || window.location.hash.replace('#', '');

    const exception404 = {
      component: Exception404,
      routeKey: pathname,
      name: '404',
      pathname,
    };

    const pathKey = Object.keys(routerData || {}).find(key => pathToRegexp(key).test(pathname));

    if (!pathKey) {
      return exception404;
    }

    let keys = [];
    let match = pathToRegexp(pathKey, keys).exec(pathname);
    if (!match) {
      return exception404;
    }

    const [url, ...values] = match;

    if (keys.length !== values.length) {
      return exception404;
    }

    const params = keys.reduce((memo, key, index) => {
      memo[key.name] = values[index];
      return memo;
    }, {});

    let func = {};
    if (routerData[pathKey].functionId) {
      func = functionMap[routerData[pathKey].functionId] || {};
    }
    const isFunc = !func || func.pageId === routerData[pathKey].pageId;
    return {
      routeKey: pathKey,
      component: require('containers/' + routerData[pathKey].filePath).default,
      params: {
        match: { params },
        getRef: ref => {
          this.getRef(ref, url);
        },
      },
      name: routerData[pathKey].name,
      pathname: url,
      fullUrl: func.fullUrl,
      parent: func.parent,
      functionId: routerData[pathKey].functionId,
      param: func.param,
      type: isFunc ? 'function' : '',
    };
  };

  formatter = (data, parentPath = '/', parentAuthority) => {
    return data.map(item => {
      let { path } = item;
      if (!isUrl(path)) {
        path = parentPath + item.path;
      }
      const result = {
        ...item,
        path,
        authority: item.authority || parentAuthority,
      };
      if (item.children) {
        result.children = this.formatter(
          item.children,
          `${parentPath}${item.path}/`,
          item.authority
        );
      }
      return result;
    });
  };

  componentWillUnmount() {
    unenquireScreen(this.enquireHandler);
  }

  getPageTitle() {
    const { routerData, location } = this.props;
    const { pathname } = location;
    let title = '融智汇';
    let currRouterData = null;
    // match params path
    Object.keys(routerData).forEach(key => {
      if (pathToRegexp(key).test(pathname)) {
        currRouterData = routerData[key];
      }
    });
    if (currRouterData && currRouterData.name) {
      title = `${this.$t(currRouterData.name)} - 融智汇`;
    }
    return title;
  }

  getBaseRedirect = () => {
    // According to the url parameter to redirect
    // 这里是重定向的,重定向到 url 的 redirect 参数所示地址
    const urlParams = new URL(window.location.href);

    const redirect = urlParams.searchParams.get('redirect');

    if (redirect) {
      urlParams.searchParams.delete('redirect');
      window.history.replaceState(null, 'redirect', urlParams.href);
    } else {
      const { routerData } = this.props;
      const authorizedPath = Object.keys(routerData).find(
        item => check(routerData[item].authority, item) && item !== '/'
      );
      return authorizedPath;
    }
    return redirect;
  };

  handleMenuCollapse = collapsed => {
    const { dispatch } = this.props;
    dispatch({
      type: 'global/changeLayoutCollapsed',
      payload: collapsed,
    });
  };

  handleNoticeClear = type => {
    message.success(`清空了${type}`);
    const { dispatch } = this.props;
    dispatch({
      type: 'global/clearNotices',
      payload: type,
    });
  };

  handleMenuClick = ({ key }) => {
    const { dispatch } = this.props;
    if (key === 'triggerError') {
      dispatch(routerRedux.push('/exception/trigger'));
      return;
    }
    if (key === 'logout') {
      dispatch({
        type: 'login/logout',
      });
    }
  };

  handleNoticeVisibleChange = visible => {
    const { dispatch } = this.props;
    if (visible) {
      dispatch({
        type: 'global/fetchNotices',
      });
    }
  };

  onChange = activeKey => {
    this.props.onTabChange(activeKey);
  };

  onEdit = (targetKey, action) => {
    this.props.onTabEdit(targetKey, action);
  };

  render() {
    const {
      currentUser,
      collapsed,
      fetchingNotices,
      notices,
      location,
      menu,
      navTheme,
      panes,
      selectKey,
      activeKey,
      openKey,
    } = this.props;

    const { isMobile: mb, loading } = this.state;

    const layout = (
      <Layout>
        <SiderMenu
          logo={logo}
          Authorized={Authorized}
          menuData={menu.menuList}
          collapsed={collapsed}
          location={location}
          isMobile={mb}
          onCollapse={this.handleMenuCollapse}
          activeKey={selectKey}
          navTheme={navTheme}
          openKey={openKey}
        />
        <Layout>
          <Header style={{ padding: 0 }}>
            <GlobalHeader
              logo={logo}
              currentUser={currentUser}
              fetchingNotices={fetchingNotices}
              notices={notices}
              collapsed={collapsed}
              isMobile={mb}
              onNoticeClear={this.handleNoticeClear}
              onCollapse={this.handleMenuCollapse}
              onMenuClick={this.handleMenuClick}
              onNoticeVisibleChange={this.handleNoticeVisibleChange}
            />
          </Header>
          <Content style={{ margin: '10px 10px 0' }}>
            {!loading && (
              <Tabs
                hideAdd
                onChange={this.onChange}
                activeKey={activeKey}
                type="editable-card"
                onEdit={this.onEdit}
                tabBarGutter={2}
              >
                {panes.map(pane => (
                  <TabPane
                    closable={pane.routeKey != '/dashboard'}
                    forceRender={false}
                    tab={this.$t(pane.name)}
                    key={pane.routeKey}
                  >
                    <div
                      style={{ padding: '12px 14px', paddingBottom: 0, backgroundColor: '#fff' }}
                    >
                      {React.createElement(pane.component, pane.params)}
                    </div>
                  </TabPane>
                ))}
              </Tabs>
            )}
          </Content>
          {/* <Footer style={{ padding: 0 }}>
            <GlobalFooter
              links={[
                {
                  key: 'Pro 首页',
                  title: 'Pro 首页',
                  href: 'http://pro.ant.design',
                  blankTarget: true,
                },
                {
                  key: 'github',
                  title: <Icon type="github" />,
                  href: 'https://github.com/ant-design/ant-design-pro',
                  blankTarget: true,
                },
                {
                  key: 'Ant Design',
                  title: 'Ant Design',
                  href: 'http://ant.design',
                  blankTarget: true,
                },
              ]}
              copyright={
                <Fragment>
                  Copyright <Icon type="copyright" /> 2018 蚂蚁金服体验技术部出品
                </Fragment>
              }
            />
          </Footer> */}
        </Layout>
      </Layout>
    );

    return (
      <DocumentTitle title={this.getPageTitle()}>
        <Spin spinning={loading} size="large">
          <ContainerQuery query={query}>
            {params => <div className={classNames(params)}>{layout}</div>}
          </ContainerQuery>
        </Spin>
      </DocumentTitle>
    );
  }
}

export default connect(
  ({ user, global = {}, loading, languages, menu, setting: { navTheme } }) => ({
    currentUser: user.currentUser,
    collapsed: global.collapsed,
    fetchingNotices: loading.effects['global/fetchNotices'],
    notices: global.notices,
    languages: languages,
    menu: menu,
    organization: user.organization,
    navTheme,
  })
)(BasicLayout);
