import React, { Component } from 'react';
import { LocaleProvider, message, notification } from 'antd';
import { IntlProvider, injectIntl, addLocaleData } from 'react-intl';
import { _setIntlObject } from 'utils/locale';
import pathToRegexp from 'path-to-regexp';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import zhCN from 'antd/lib/locale-provider/zh_CN';
import enUS from 'antd/lib/locale-provider/en_US';

import en from 'react-intl/locale-data/en';
import zh from 'react-intl/locale-data/zh';

import BasicLayout from './layouts/BasicLayout';
import Loading from 'widget/loading';
import fetch from 'utils/fetch';
import config from 'config';

import Exception404 from './routes/Exception/404';

const InjectedWrapper = injectIntl(props => {
  _setIntlObject(props.intl);
  return props.children;
});

addLocaleData([...en, ...zh]);

@connect(({ user: { locales, company, organization }, languages }) => ({
  locales,
  languages,
  company,
  organization,
}))
class Contanier extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      routerData: {},
      functionMap: {},
      panes: [],
      activeKey: '',
      selectKey: '',
      messages: {},
      local: props.languages.local,
      openKey: '',
    };
    this.localeInfo = {
      'en-us': enUS,
      'zh-cn': zhCN,
    };
    this.group = {};
    this.functionGroup = {};
    this.pageMap = {};
    this.refMap = {};
    this.applicationIds = {};
    this.ws = null;
  }

  async componentDidMount() {
    if (!window.sessionStorage.getItem('token')) {
      this.props.dispatch({
        type: 'login/logout',
      });
      return;
    }

    this.hideMessage = message.loading('正在获取用户信息...', 0);

    Promise.all([this.getMenus(), this.getUser()])
      .then(async () => {
        this.reloadRoutes();
        this.reloadTheme();
        this.setState({ loading: false });
        this.hideMessage();
      })
      .catch(err => {
        this.hideMessage();
      });

    this.connectService();
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.location.pathname !== this.props.location.pathname) {
      this.onRouterChange();
    }
    if (nextProps.languages.local !== this.props.languages.local) {
      this.onlocalChange(nextProps.languages.local);
    }
  }

  componentWillUnmount() {
    if (this.hideMessage) {
      this.hideMessage();
    }
    if (this.ws) {
      this.ws.close();
    }
  }

  connectService() {
    if (this.ws) return;

    this.ws = new WebSocket(
      `${config.wsUrl}/peripheral/api/websocket?access_token=` + sessionStorage.getItem('token')
    );

    this.ws.onmessage = evt => {
      var received_msg = JSON.parse(evt.data);
      notification.open({
        message: received_msg.title,
        description: received_msg.messages,
      });
    };
  }

  onlocalChange = local => {
    const { activeKey, panes } = this.state;
    if (!panes.length) return;
    const component = panes.find(o => o.routeKey === activeKey);
    this.getDefaultLanguageList(local).then(() => {
      this.getLanguageList(component.applicationId, local, component.applicationCode)
        .then(res => {
          this.setState({ message: res, local });
        })
        .catch(error => {
          message.error(error.response.data.message);
        });
    });
  };

  reloadRoutes = () => {
    let path = window.location.hash.replace('#', '');

    let { panes, local } = this.state;

    if (path == '/') {
      this.props.dispatch(
        routerRedux.push({
          pathname: '/dashboard',
        })
      );
      return;
    }

    if (path != '/dashboard') {
      let dashboard = this.getComponent('/dashboard');
      if (dashboard) {
        panes.push(dashboard);
      }
    }

    const component = this.getComponent();

    if (component) {
      this.getLanguageList(component.applicationId, local, component.applicationCode).then(() => {
        panes.push(component);
        this.setState({
          panes,
          activeKey: component.routeKey,
          selectKey: component.functionId,
          openKey: component.contentId,
        });
        this.setCurrentMenuId(component);
      });
    }
  };

  onRouterChange = () => {
    const { panes, local } = this.state;
    const { dispatch, company } = this.props;
    let path = window.location.hash.replace('#', '');

    if (path == '/') {
      dispatch(
        routerRedux.push({
          pathname: '/dashboard',
        })
      );
      return;
    }

    const component = this.getComponent();

    let index = panes.findIndex(o => o.routeKey == component.routeKey);

    // 三种情况  不会打开新tab页
    // 1.即将跳转的页面是功能页，并且它的父页面是当前页面
    // 2.即将跳转的页面是功能页, 并且当前页面也是功能页面，并且当前页面和即将跳转的页面同属于一个菜单
    // 3.即将跳转的页面是当前页面的父页面，一般页面的返回按钮
    if (index >= 0) {
      panes[index] = component;
      this.setState({
        panes,
        activeKey: component.routeKey,
        selectKey: component.functionId,
        openKey: component.contentId,
      });
    } else {
      this.getLanguageList(component.applicationId, local, component.applicationCode).then(() => {
        if (component.applicationCode === 'budget') {
          this.getOrganizationBySetOfBooksId(company.setOfBooksId).then(() => {
            panes.push(component);
            this.setState({
              panes,
              activeKey: component.routeKey,
              selectKey: component.functionId,
              openKey: component.contentId,
            });
            this.setCurrentMenuId(component);
          });
        } else {
          panes.push(component);
          this.setState({
            panes,
            activeKey: component.routeKey,
            selectKey: component.functionId,
            openKey: component.contentId,
          });
          this.setCurrentMenuId(component);
        }
      });
    }
  };

  setCurrentMenuId = component => {
    const { dispatch } = this.props;
    dispatch({
      type: 'menu/setCurrentMenuId',
      payload: { menuId: component.functionId, menuParams: component.param },
    });
  };

  //加载主题
  reloadTheme = () => {
    let navTheme = window.localStorage.getItem('navTheme');
    if (navTheme) {
      this.props.dispatch({
        type: 'setting/setNavTheme',
        payload: { navTheme },
      });
    }
  };

  //获取用户信息
  getUser = () => {
    const { dispatch } = this.props;
    return new Promise(async (resolve, reject) => {
      try {
        const result = await fetch.get('/api/account');
        dispatch({
          type: 'user/saveCurrentUser',
          payload: result,
        });
        dispatch({
          type: 'languages/setLocal',
          payload: { local: result.language },
        });
        this.setState({ local: result.language }, async () => {
          await this.getCompany();
          await this.getLocale();
          await this.getDepartment();
          await this.getDefaultLanguageList(result.language);
          resolve();
        });
      } catch (error) {
        message.error(error.response.data.message);
        this.setState({ loading: false });
        reject();
      }
    });
  };

  // 获取菜单
  getMenus = () => {
    const { dispatch } = this.props;
    return new Promise(async resolve => {
      try {
        const {
          contentFunctionDTOList = [],
          functionIdList = [],
          functionPageDTOList = [],
        } = await fetch.get('/api/role/function');

        this.pageMap = functionPageDTOList.reduce((temp, item) => {
          temp[item.pageId] = {
            ...item,
          };
          return temp;
        }, {});

        const functionMap = {};
        const functions = contentFunctionDTOList
          .reduce((temp, item) => {
            if (functionIdList.indexOf(item.functionId) >= 0) {
              const page = this.pageMap[item.pageId];
              if (page) {
                temp.push({ ...item, pageUrl: page ? page.pageUrl : '', ...page });
                functionMap[item.functionId] = {
                  ...item,
                  parent: page.parent,
                  fullUrl: page.fullUrl,
                };
              }
            }
            return temp;
          }, [])
          .filter(item => item);

        const contents = functions
          .reduce((temp, item) => {
            if (temp.findIndex(o => o.contentId === item.contentId) >= 0 || !item.contentId)
              return temp;
            let obj = contentFunctionDTOList.find(
              o => o.contentId === item.contentId && !o.functionId
            );
            obj.id = obj.contentId;
            temp.push(obj);
            return temp;
          }, [])
          .filter(item => item);

        const contentParents = [];
        contents
          .map(item => {
            if (!item.parentId) return;
            let content = contentFunctionDTOList.find(o => o.contentId === item.parentId);
            content.id = content.contentId;
            contentParents.push(content);
            while (content.parentId) {
              content = contentFunctionDTOList.find(o => o.contentId === content.parentId);
              content.id = content.contentId;
              contentParents.push(content);
            }
          })
          .filter(item => item);

        this.group = this.groupByParent([...functions, ...contents, ...contentParents]);

        const root = this.group.root ? [...this.group.root] : [];
        this.getChildrenMenu(root);

        const routerData = functionPageDTOList.reduce((temp, item) => {
          const router = item.fullRouter;
          temp[router] = {
            ...item,
            name: item.pageName,
            path: router,
            parent: (item.contentRouter || '') + item.functionRouter,
            id: item.functionId || item.contentId,
          };
          return temp;
        }, {});

        root.unshift({
          level: 1,
          name: '仪表盘',
          icon: 'dashboard',
          parentKey: '',
          path: '/dashboard',
          routerKey: '/dashboard',
          id: '-1',
          functionId: '-1',
        });

        routerData['/dashboard'] = {
          filePath: 'dashboard',
          name: '仪表盘',
          id: '-1',
          functionId: '-1',
        };

        dispatch({
          type: 'menu/setMenu',
          payload: { menuList: [...root], routerData, functionMap },
        });

        this.setState({ routerData, functionMap }, resolve);
      } catch (error) {
        console.log(error);
        message.error(error.response.data.message);
        resolve();
      }
    });
  };

  // 递归拼接子菜单
  getChildrenMenu = (data = [], level = 1, parent = {}) => {
    data.map(item => {
      item.level = level;
      item.name = item.functionId ? item.functionName : item.contentName;
      item.icon = level > 1 ? '' : item.functionIcon || item.icon;
      item.parentKey =
        (parent.path ? parent.path : '') +
        (item.functionId ? item.functionRouter : item.contentRouter);
      if (item.fullUrl) {
        item.path = item.fullUrl;
      }
      // item.path =
      //   (parent.path ? parent.path : '') +
      //   (item.functionId ? item.functionRouter + '/' + item.pageUrl : item.contentRouter);
      item.id = item.functionId || item.contentId;
      if (item.functionId) return;
      item.children = [
        ...(this.group[item.contentId] || []),
        ...(this.functionGroup[item.contentId] || []),
      ];

      this.getChildrenMenu(item.children, level + 1, item);
    });
  };

  //按父级分组
  groupByParent = data => {
    return data.reduce((temp, item) => {
      if ((!item.parentId && !item.functionId) || (!item.contentId && item.functionId)) {
        if (temp.root) {
          temp.root.push(item);
        } else {
          temp.root = [item];
        }
      } else if (item.functionId) {
        if (this.functionGroup[item.contentId]) {
          this.functionGroup[item.contentId].push(item);
        } else {
          this.functionGroup[item.contentId] = [item];
        }
      } else {
        if (temp[item.parentId]) {
          temp[item.parentId].push(item);
        } else {
          temp[item.parentId] = [item];
        }
      }
      return temp;
    }, {});
  };

  // 获取公司和预算组织
  getCompany = () => {
    const { dispatch } = this.props;
    return new Promise(async resolve => {
      let result = await fetch.get(`${config.mdataUrl}/api/my/companies`);
      dispatch({
        type: 'user/saveCompany',
        payload: result,
      });
      resolve();
      // try {
      //   await this.getOrganizationBySetOfBooksId(result.setOfBooksId);
      //   resolve();
      // } catch (e) {
      //   resolve();
      // }
    });
  };

  //获取公司和预算组织
  getDepartment = () => {
    const { dispatch } = this.props;
    return new Promise(async resolve => {
      try {
        const result = await fetch.get(`${config.mdataUrl}/api/my/department`);
        dispatch({
          type: 'user/saveDepartment',
          payload: result,
        });
        resolve();
      } catch (e) {
        message.error(e.response.data.message);
      }
    });
  };

  //获取预算组织
  getOrganizationBySetOfBooksId = id => {
    const { dispatch, organization } = this.props;
    return new Promise(async (resolve, reject) => {
      if (Object.keys(organization).length) {
        resolve();
        return;
      }
      fetch
        .get(`${config.budgetUrl}/api/budget/organizations/default/${id}`)
        .then(result => {
          dispatch({
            type: 'user/saveOrganization',
            payload: result,
          });
          resolve();
        })
        .catch(e => {
          resolve();
        });
    });
  };

  //获取语言信息
  getLanguageList = (applicationId = '0', lang = 'zh_cn', applicationCode) => {
    return new Promise(async resolve => {
      try {
        if (this.applicationIds[lang].indexOf(applicationId) < 0) {
          const hideMessage = message.loading('正在获取当前页面国际化信息...', 0);
          this.applicationIds[lang].push(applicationId);
          let { messages } = this.state;
          fetch.get(`/api/front/locale/query/map/by/cond`, { applicationId, lang }).then(data => {
            messages = { ...messages, ...data };
            this.props.dispatch({
              type: 'languages/setApplicationCode',
              payload: { applicationCode },
            });
            hideMessage();
            this.setState(
              {
                messages,
              },
              resolve
            );
          });
        } else {
          resolve();
        }
      } catch (e) {
        message.error(e.response.data.message);
        resolve();
      }
    });
  };

  // 获取base模块语言信息
  getDefaultLanguageList = (lang = 'zh_cn', applicationId = '1104603306175856641') => {
    return new Promise(async resolve => {
      try {
        this.applicationIds[lang] = [];
        if (this.applicationIds[lang].indexOf(applicationId) < 0) {
          this.applicationIds[lang].push(applicationId);
          let { messages } = this.state;
          fetch.get(`/api/front/locale/query/map/by/cond`, { applicationId, lang }).then(data => {
            messages = { ...data };
            const { dispatch } = this.props;
            dispatch({
              type: 'languages/setLanguages',
              payload: { languages: messages },
            });
            this.setState(
              {
                messages,
              },
              resolve
            );
          });
        } else {
          resolve();
        }
      } catch (e) {
        message.error(e.response.data.message);
        resolve();
      }
    });
  };

  //获取用户语言环境
  getLocale = () => {
    const { dispatch } = this.props;
    return new Promise(async (resolve, reject) => {
      try {
        fetch.post(`${config.baseUrl}/api/lov/language/zh_cn`).then(res => {
          dispatch({
            type: 'languages/setLanguageType',
            payload: { languageType: res },
          });
          res.map(item => {
            this.applicationIds[item.code] = [];
          });
          resolve();
        });
      } catch (e) {
        message.error(e.response.data.message);
        resolve();
      }
    });
  };

  getComponent = path => {
    const { routerData, functionMap } = this.state;
    const pathname = path || window.location.hash.replace('#', '');

    const exception404 = {
      component: Exception404,
      routeKey: pathname,
      name: '404',
      pathname,
    };
    const pathKey = Object.keys(routerData || {})
      .filter(key => key)
      .find(key => pathToRegexp(key).test(pathname));

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
      routeKey: routerData[pathKey].functionId,
      component: require('containers/' + routerData[pathKey].filePath).default,
      params: {
        match: { params },
        getRef: ref => {
          this.getRef(ref, url);
        },
      },
      name: routerData[pathKey].name,
      pathname: url,
      functionId: routerData[pathKey].functionId,
      contentId: routerData[pathKey].contentId,
      param: func.param,
      type: isFunc ? 'function' : '',
      applicationId: func.applicationId,
      applicationCode: func.applicationCode,
      id: routerData[pathKey].id,
    };
  };

  getRef = (ref, key) => {
    this.refMap = { [key]: ref };
  };

  handleTabChange = activeKey => {
    const { panes } = this.state;
    const { dispatch } = this.props;
    const path = panes.find(o => o.routeKey == activeKey).pathname;
    dispatch(
      routerRedux.push({
        pathname: path,
      })
    );
    this.setState({ activeKey });
  };

  handleTabEdit = (targetKey, action) => {
    if (action === 'remove') {
      this.remove(targetKey);
    }
  };

  remove = targetKey => {
    let { activeKey, panes } = this.state;
    const { dispatch } = this.props;
    let lastIndex;
    panes.forEach((pane, i) => {
      if (pane.routeKey === targetKey) {
        lastIndex = i - 1;
      }
    });
    panes = panes.filter(pane => pane.routeKey !== targetKey);
    if (lastIndex >= 0 && activeKey === targetKey) {
      activeKey = panes[lastIndex].routeKey;
    }

    this.setState({ panes, activeKey }, () => {
      let path = this.state.panes.find(o => o.routeKey == this.state.activeKey).pathname;
      dispatch(
        routerRedux.push({
          pathname: path,
        })
      );
    });
  };

  render() {
    // const { languages: { local } } = this.props;
    const { loading, activeKey, selectKey, panes, messages, local, openKey } = this.state;
    const langTypes = {
      zh_cn: 'zh',
      en_us: 'en',
    };
    return loading ? (
      <Loading />
    ) : (
      <LocaleProvider locale={this.localeInfo[local] || zhCN}>
        <IntlProvider key={local} onError={() => {}} locale={langTypes[local]} messages={messages}>
          <InjectedWrapper>
            <BasicLayout
              onTabChange={this.handleTabChange}
              onTabEdit={this.handleTabEdit}
              activeKey={activeKey}
              selectKey={selectKey}
              openKey={openKey}
              panes={panes}
              {...this.props}
            />
          </InjectedWrapper>
        </IntlProvider>
      </LocaleProvider>
    );
  }
}

export default Contanier;
