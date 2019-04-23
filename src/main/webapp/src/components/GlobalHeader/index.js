import React from 'react';
import config from 'config';
import { Menu, Icon, Tag, Dropdown, Avatar, Divider, Select, message, Modal } from 'antd';
import NoticeIcon from '../NoticeIcon';
import Debounce from 'lodash-decorators/debounce';
import { Link } from 'dva/router';
import styles from './index.less';
import { connect } from 'dva';
import fetch from 'share/httpFetch';

const colors = [
  { color: 'rgb(24, 144, 255)', text: '默认' },
  { color: 'rgb(245, 34, 45)', text: '薄暮' },
  { color: 'rgb(250, 84, 28)', text: '火山' },
  { color: 'rgb(250, 173, 20)', text: '日暮' },
  { color: 'rgb(19, 194, 194)', text: '明青' },
  { color: 'rgb(82, 196, 26)', text: '极光绿' },
  { color: 'rgb(47, 84, 235)', text: '极客蓝' },
  { color: 'rgb(114, 46, 209)', text: '酱紫' },
  { color: '#5867dd', text: '炫紫' },
];
const navThemes = [{ color: 'dark', text: '深色(默认)' }, { color: 'light', text: '浅色' }];

@connect(({ components, languages, setting: { navTheme } }) => ({
  components,
  languages,
  navTheme,
}))
export default class GlobalHeader extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      theme: '',
      total: 0,
      messagesList: [],
      messageTotal: 0,
      noticeTotal: 0,
      noticeList: [],
    };
  }

  componentDidMount() {
    const theme = window.localStorage.getItem('theme');
    if (theme) {
      if (theme != colors[0].color) {
        this.buildIt(theme);
      }
      this.setState({ theme });
    } else {
      window.localStorage.setItem('theme', colors[0].color);
      this.setState({ theme: colors[0].color });
    }
    //update by chenxu
    //this.getNoticeData();
  }

  componentWillUnmount() {
    this.triggerResizeEvent.cancel();
  }

  getNoticeData = () => {
    fetch
      //update by chenxu
      /* .get(`${config.peripheralUrl}/api/messages/query`, {
        size: 9999,
        page: 0,
      })*/
      .then(({ data }) => {
        this.setState({
          messagesList: data.messagesList.map(o => ({
            ...o,
            description: o.messageContent,
            avatar: 'https://gw.alipayobjects.com/zos/rmsportal/ThXAXghbEsBCCSDihZxY.png',
            read: o.readFlag,
          })),
          noticeList: data.noticeList.map(o => ({
            ...o,
            description: o.messageContent,
            avatar: 'https://gw.alipayobjects.com/zos/rmsportal/ThXAXghbEsBCCSDihZxY.png',
            read: o.readFlag,
          })),
          messageTotal: data.total.messageTotal,
          noticeTotal: data.total.noticeTotal,
          total: data.total.total,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  toggle = () => {
    const { collapsed, onCollapse } = this.props;
    onCollapse(!collapsed);
    this.triggerResizeEvent();
  };
  /* eslint-disable*/
  @Debounce(600)
  triggerResizeEvent() {
    const event = document.createEvent('HTMLEvents');
    event.initEvent('resize', true, false);
    window.dispatchEvent(event);
  }

  langChange = value => {
    Modal.confirm({
      title: '确定切换语言环境？',
      content: '切换语言环境会刷新当前页面，请先保存数据，以免造成不必要的损失！',
      onOk: () => {
        const { dispatch } = this.props;
        const hideMessage = message.loading('语言切换中,请稍等...', 0);
        dispatch({
          type: 'languages/selectLanguage',
          payload: { languages: [], local: value },
        });
        fetch.post(config.baseUrl + '/api/user/language/' + value).then(() => {
          hideMessage();
        });
      },
    });
  };

  navThemeChange = value => {
    this.props.dispatch({
      type: 'setting/setNavTheme',
      payload: { navTheme: value },
    });
    window.localStorage.setItem('navTheme', value);
  };

  buildIt = value => {
    let that = this;
    function buildIt() {
      if (!window.less) {
        return;
      }
      setTimeout(() => {
        window.less
          .modifyVars({
            '@primary-color': value,
            '@btn-primary-bg': value,
          })
          .then(() => {
            window.localStorage.setItem('theme', value);
            that.setState({ theme: value });
          });
      }, 200);
    }

    if (!window.lessNodesAppended) {
      // insert less.js and color.less
      const lessStyleNode = document.createElement('link');
      const lessConfigNode = document.createElement('script');
      const lessScriptNode = document.createElement('script');
      lessStyleNode.setAttribute('rel', 'stylesheet/less');
      lessStyleNode.setAttribute('href', '/color.less');
      lessConfigNode.innerHTML = `
      window.less = {
        async: true,
        env: 'production',
        javascriptEnabled: true
      };
    `;
      lessScriptNode.src = 'https://gw.alipayobjects.com/os/lib/less.js/3.8.1/less.min.js';
      lessScriptNode.async = true;
      lessScriptNode.onload = () => {
        buildIt();
        lessScriptNode.onload = null;
      };
      document.body.appendChild(lessStyleNode);
      document.body.appendChild(lessConfigNode);
      document.body.appendChild(lessScriptNode);
      window.lessNodesAppended = true;
    } else {
      buildIt();
    }
  };

  colorChange = value => {
    const hideMessage = message.loading('正在编译主题！', 0);

    let that = this;
    function buildIt() {
      if (!window.less) {
        return;
      }
      setTimeout(() => {
        window.less
          .modifyVars({
            '@primary-color': value,
            '@btn-primary-bg': value,
          })
          .then(() => {
            window.localStorage.setItem('theme', value);
            that.setState({ theme: value });
            hideMessage();
          });
      }, 500);
    }
    if (!window.lessNodesAppended) {
      // insert less.js and color.less
      const lessStyleNode = document.createElement('link');
      const lessConfigNode = document.createElement('script');
      const lessScriptNode = document.createElement('script');
      lessStyleNode.setAttribute('rel', 'stylesheet/less');
      lessStyleNode.setAttribute('href', '/color.less');
      lessConfigNode.innerHTML = `
      window.less = {
        async: true,
        env: 'production',
        javascriptEnabled: true
      };
    `;
      lessScriptNode.src = 'https://gw.alipayobjects.com/os/lib/less.js/3.8.1/less.min.js';
      lessScriptNode.async = true;
      lessScriptNode.onload = () => {
        buildIt();
        lessScriptNode.onload = null;
      };
      document.body.appendChild(lessStyleNode);
      document.body.appendChild(lessConfigNode);
      document.body.appendChild(lessScriptNode);
      window.lessNodesAppended = true;
    } else {
      buildIt();
    }
  };

  // 标记已读
  changeReadState = (clickedItem, tabProps) => {
    const { messagesList, noticeList } = this.state;
    let { noticeTotal, total, messageTotal } = this.state;
    const { id, read } = clickedItem;

    if (read) return;
    //update by chenxu
    /* fetch.post(`${config.peripheralUrl}/api/messages/read/` + id).then(() => {
      if (tabProps.title === '消息') {
        const record = messagesList.find(o => o.id === id);
        if (record) {
          record.read = true;
          messageTotal -= 1;
          total -= 1;
          this.setState({ messagesList, messageTotal, total });
        }
      } else {
        const record = noticeList.find(o => o.id === id);
        if (record) {
          record.read = true;
          noticeTotal -= 1;
          total -= 1;
          this.setState({ noticeList, noticeTotal, total });
        }
      }
    });*/
  };

  clearMessages = () => {
    fetch.post(`${config.peripheralUrl}/api/messages/read/all`).then(res => {
      this.getNoticeData();
    });
  };

  render() {
    const {
      currentUser = {},
      collapsed,
      isMobile,
      logo,
      onMenuClick,
      languages: { local, languageType },
      navTheme,
    } = this.props;

    const { messagesList, messageTotal, total, noticeTotal, noticeList } = this.state;

    const menu = (
      <Menu className={styles.menu} selectedKeys={[]} onClick={onMenuClick}>
        <Menu.Item disabled>
          <Icon type="user" />个人中心
        </Menu.Item>
        <Menu.Item disabled>
          <Icon type="setting" />设置
        </Menu.Item>
        <Menu.Divider />
        <Menu.Item key="logout">
          <Icon type="logout" />退出登录
        </Menu.Item>
      </Menu>
    );

    return (
      <div className={styles.header}>
        {isMobile && [
          <Link to="/" className={styles.logo} key="logo">
            <img src={logo} alt="logo" width="32" />
          </Link>,
          <Divider type="vertical" key="line" />,
        ]}
        <Icon
          className={styles.trigger}
          type={collapsed ? 'menu-unfold' : 'menu-fold'}
          onClick={this.toggle}
        />
        <div className={styles.right}>
          <label style={{ marginRight: 6 }}>菜单主题:</label>
          <Select
            value={navTheme}
            style={{ marginRight: 20, width: 120 }}
            onChange={this.navThemeChange}
          >
            {navThemes.map(item => (
              <Select.Option key={item.color}>
                <div style={{ color: item.color }}>{item.text}</div>
              </Select.Option>
            ))}
          </Select>
          <label style={{ marginRight: 6 }}>全局主题:</label>
          <Select
            value={this.state.theme}
            style={{ marginRight: 20, width: 100 }}
            onChange={this.colorChange}
          >
            {colors.map(item => (
              <Select.Option key={item.color}>
                <div style={{ color: item.color }}>{item.text}</div>
              </Select.Option>
            ))}
          </Select>
          <label style={{ marginRight: 6 }}>语言:</label>
          <Select style={{ width: 110 }} value={local} onChange={this.langChange}>
            {languageType.map(item => (
              <Select.Option key={item.code} value={item.code}>
                {item.value}
              </Select.Option>
            ))}
          </Select>
          <NoticeIcon
            className={styles.action}
            count={total}
            onViewMore={() => message.info('该功能正在开发...')}
            clearClose
            onItemClick={this.changeReadState}
            onClear={this.clearMessages}
            locale={{ emptyText: '没有消息', clear: '清除', viewMore: '显示更多' }}
          >
            <NoticeIcon.Tab
              count={noticeTotal}
              list={noticeList}
              title="通知"
              emptyImage="https://gw.alipayobjects.com/zos/rmsportal/wAhyIChODzsoKIOBHcBk.svg"
              showViewMore
              emptyText="你已查看所有通知"
            />
            <NoticeIcon.Tab
              count={messageTotal}
              list={messagesList}
              title="消息"
              emptyImage="https://gw.alipayobjects.com/zos/rmsportal/sAuJeJzSKbUmHfBQRzmZ.svg"
              showViewMore
              emptyText="您已读完所有消息"
            />
          </NoticeIcon>
          <Dropdown overlay={menu}>
            <span className={`${styles.action} ${styles.account}`}>
              <Avatar
                size="small"
                className={styles.avatar}
                src="https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png"
              />
              <span className={styles.name}>{currentUser.userName}</span>
            </span>
          </Dropdown>
        </div>
      </div>
    );
  }
}
