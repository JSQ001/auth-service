import React, { Component } from 'react';
import { Tabs } from 'antd';
import PageManager from './page';
import FunctionManager from './function';
import ContentManager from './content';

const TabPane = Tabs.TabPane;

class Menu extends Component {
  constructor(props) {
    super(props);
    this.state = {
      activeKey: 'content',
    };
  }

  tabChange = activeKey => {
    this.setState({ activeKey });
  };

  render() {
    const { activeKey } = this.state;
    return (
      <Tabs activeKey={activeKey} onChange={this.tabChange}>
        <TabPane key="content" tab={this.$t('base.directory.management')}>
          {/*目录管理*/}
          <ContentManager />
        </TabPane>
        <TabPane key="function" tab={this.$t('base.function.management')}>
          {/*功能管理*/}
          <FunctionManager />
        </TabPane>
        <TabPane key="page" tab={this.$t('base.page.management')}>
          {/*页面管理*/}
          <PageManager />
        </TabPane>
      </Tabs>
    );
  }
}

export default Menu;
