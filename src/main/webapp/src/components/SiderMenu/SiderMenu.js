import React from 'react';
import { Layout, Menu, Icon, Input } from 'antd';
import pathToRegexp from 'path-to-regexp';
import { Link } from 'dva/router';
import styles from './index.less';
import { urlToList } from '../_utils/pathTools';
import { messages } from '../../utils/utils';

import debounce from 'lodash.debounce';

const { Sider } = Layout;
const { SubMenu } = Menu;

// Allow menu.js config icon as string or ReactNode
//   icon: 'setting',
//   icon: 'http://demo.com/icon.png',
//   icon: <Icon type="setting" />,
const getIcon = icon => {
  if (typeof icon === 'string' && icon.indexOf('http') === 0) {
    return <img src={icon} alt="icon" className={`${styles.icon} sider-menu-item-img`} />;
  }
  if (typeof icon === 'string') {
    return <Icon type={icon} />;
  }
  return icon;
};

/**
 * Recursively flatten the data
 * [{path:string},{path:string}] => {path,path2}
 * @param  menu
 */
export const getFlatMenuKeys = menu =>
  menu.reduce((keys, item) => {
    keys.push(item.path);
    if (item.children) {
      return keys.concat(getFlatMenuKeys(item.children));
    }
    return keys;
  }, []);

/**
 * Find all matched menu keys based on paths
 * @param  flatMenuKeys: [/abc, /abc/:id, /abc/:id/info]
 * @param  paths: [/abc, /abc/11, /abc/11/info]
 */
export const getMenuMatchKeys = (flatMenuKeys, paths) => {
  return paths.reduce(
    (matchKeys, path) =>
      matchKeys.concat(flatMenuKeys.filter(item => pathToRegexp(item).test(path))),
    []
  );
};

export default class SiderMenu extends React.Component {
  constructor(props) {
    super(props);
    this.menus = props.menuData;
    this.state = {
      openKeys: [],
      menuList: [...props.menuData],
      lastOpenKeys: [],
      searchValue: '',
      openKey: props.openKey,
      menuMap: {},
    };
    this.search = debounce(this.search, 400);
  }

  componentDidMount() {}

  componentWillReceiveProps(nextProps) {
    if (this.props.openKey !== nextProps.openKey) {
      this.setState({ openKey: nextProps.openKey });
    }
  }

  /**
   * Convert pathname to openKeys
   * /list/search/articles = > ['list','/list/search']
   * @param  props
   */
  getDefaultCollapsedSubMenus(props) {
    const {
      location: { pathname },
    } =
      props || this.props;

    return getMenuMatchKeys(this.flatMenuKeys, urlToList(pathname));
  }

  /**
   * 判断是否是http链接.返回 Link 或 a
   * Judge whether it is http link.return a or Link
   * @memberof SiderMenu
   */
  getMenuItemPath = item => {
    const itemPath = this.conversionPath(item.path);

    let icon = null;
    if (item.level == 1) {
      icon = getIcon(item.icon);
    }

    const { target, name } = item;
    // Is it a http link
    if (/^https?:\/\//.test(itemPath)) {
      return (
        <a href={itemPath} target={target}>
          {icon}
          <span>{messages(name)}</span>
        </a>
      );
    }

    const { location, isMobile, onCollapse } = this.props;

    let nameDom = <span>{messages(name)}</span>;
    if (this.state.searchValue) {
      let strs = messages(name).split(this.state.searchValue);

      //todo name中出现两个关键字情况暂不支持 ps:"123132" key: "1"
      nameDom = (
        <span>
          <span>{strs[0]}</span>
          <span style={{ color: '#FF9966' }}>{this.state.searchValue}</span>
          <span>{strs[1]}</span>
        </span>
      );
    }

    return (
      <Link
        to={itemPath}
        target={target}
        replace={itemPath === location.pathname}
        onClick={
          isMobile
            ? () => {
                onCollapse(true);
              }
            : undefined
        }
      >
        {icon}
        {nameDom}
      </Link>
    );
  };

  /**
   * get SubMenu or Item
   */
  getSubMenuOrItem = item => {
    if (item.children && item.children.some(child => child.name)) {
      const childrenItems = this.getNavMenuItems(item.children);
      // 当无子菜单时就不展示菜单
      if (childrenItems && childrenItems.length > 0) {
        return (
          <SubMenu
            title={
              item.icon && (item.level == 1 || !item.level) ? (
                <span>
                  {getIcon(item.icon)}
                  <span>{this.$t(item.name)}</span>
                </span>
              ) : (
                this.$t(item.name)
              )
            }
            key={item.id}
          >
            {childrenItems}
          </SubMenu>
        );
      }
      return null;
    } else {
      return <Menu.Item key={item.id}>{this.getMenuItemPath(item)}</Menu.Item>;
    }
  };

  /**
   * 获得菜单子节点
   * @memberof SiderMenu
   */
  getNavMenuItems = menusData => {
    if (!menusData) {
      return [];
    }

    return menusData
      .filter(item => item.name)
      .map(item => {
        // make dom
        return this.getSubMenuOrItem(item);
      })
      .filter(item => item);
  };

  // Get the currently selected menu
  getSelectedMenuKeys = () => {
    const {
      location: { pathname },
    } = this.props;
    return getMenuMatchKeys(this.flatMenuKeys, urlToList(pathname));
  };

  // conversion Path
  // 转化路径
  conversionPath = path => {
    if (path && path.indexOf('http') === 0) {
      return path;
    } else {
      return `/${path || ''}`.replace(/\/+/g, '/');
    }
  };

  // permission to check
  checkPermissionItem = (authority, ItemDom) => {
    const { Authorized } = this.props;
    if (Authorized && Authorized.check) {
      const { check } = Authorized;
      return check(authority, ItemDom);
    }
    return ItemDom;
  };

  isMainMenu = key => {
    return this.menus.some(item => key && (item.key === key || item.path === key));
  };

  handleOpenChange = openKeys => {
    const lastOpenKey = openKeys[openKeys.length - 1];
    // const moreThanOne = openKeys.filter(openKey => this.isMainMenu(openKey)).length > 1;
    this.setState({
      openKey: lastOpenKey,
    });
  };

  search = value => {
    if (!value) {
      this.setState({ menuList: [...this.menus], searchValue: value });
      return;
    }
    //todo  三级搜索暂不支持 后面加一下
    let result = [];
    let menus = JSON.parse(JSON.stringify(this.menus));
    menus.map(item => {
      if (item.children) {
        let children = item.children.filter(o => this.$t(o.name).includes(value));
        if (children && children.length) {
          item.children = [...children];
          result.push(item);
        }
      } else {
        if (this.$t(item.name).includes(value)) {
          result.push(item);
        }
      }
    });

    this.setState({
      menuList: result,
      openKeys: result.map(item => item.contentId),
      searchValue: value,
    });
  };

  select = ({ key }) => {
    // this.setState({
    //   openKey: this.state.menuMap[key],
    // });
  };

  render() {
    const { logo, collapsed, onCollapse, activeKey, navTheme } = this.props;
    const { openKeys, menuList, searchValue, openKey } = this.state;
    const menuProps = collapsed ? {} : { openKeys: searchValue ? openKeys : [openKey] };

    const headerStyle =
      navTheme == 'light'
        ? { background: '#fff', boxShadow: '1px 1px 0 0 #e8e8e8', color: '#000' }
        : null;

    return (
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        breakpoint="lg"
        onCollapse={onCollapse}
        width={220}
        className={styles.sider}
        theme={navTheme}
      >
        <div className={styles.logo} style={headerStyle} key="logo">
          <Link to="/dashboard">
            <img src={logo} alt="logo" />
            <h1 style={{ color: navTheme == 'light' ? '#1890FF' : '#fff' }}>融智汇</h1>
          </Link>
        </div>
        <div className="menu-container">
          {!collapsed && (
            <div style={{ padding: '18px 32px 0px 16px' }}>
              <Input.Search placeholder="功能查询" onChange={e => this.search(e.target.value)} />
            </div>
          )}
          <div style={{ padding: '16px 0', width: '100%', overflowX: 'hidden' }}>
            <Menu
              key="Menu"
              theme={navTheme}
              mode="inline"
              {...menuProps}
              onOpenChange={this.handleOpenChange}
              selectedKeys={[activeKey]}
              onSelect={this.select}
            >
              {this.getNavMenuItems(menuList)}
            </Menu>
          </div>
        </div>
      </Sider>
    );
  }
}
