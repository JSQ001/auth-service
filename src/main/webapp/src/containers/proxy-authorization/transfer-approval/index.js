import React, { Component } from 'react';
import { Tabs } from 'antd';
import MyPassing from './my-passing';
import MyAgent from './my-agent';

const TabPane = Tabs.TabPane;

class Transfer extends Component {
  constructor(props) {
    super(props);
    this.state = {
      tabKey: '1',
    };
  }

  // tab选项卡切换
  tabChange = tabKey => {
    this.setState({ tabKey });
  };

  render() {
    const { tabKey } = this.state;
    return (
      <Tabs defaultActiveKey={tabKey} onChange={this.tabChange}>
        <TabPane tab="我的转交" key="1">
          <MyPassing />
        </TabPane>
        <TabPane tab="我的代理" key="2">
          <MyAgent />
        </TabPane>
      </Tabs>
    );
  }
}

export default Transfer;
