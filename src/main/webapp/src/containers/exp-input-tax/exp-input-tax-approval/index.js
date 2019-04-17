import React, { Component } from 'react';
import { Tabs } from 'antd';
import ForApproval from './for-approval';
import config from 'config';

const TabPane = Tabs.TabPane;

class InputTaxApproval extends Component {
  constructor(props) {
    super(props);
    this.state = {
      tabKey: '1',
      urlWaitForApproval: `${config.expenseUrl}/api/input/header/query?status=1002`,
      urlHadApproval: `${config.expenseUrl}/api/input/header/query?status=1004`,
    };
  }

  // tab选项卡切换
  tabChange = tabKey => {
    this.setState({ tabKey });
  };

  render() {
    const { tabKey, urlWaitForApproval, urlHadApproval } = this.state;
    return (
      <Tabs defaultActiveKey={tabKey} onChange={this.tabChange}>
        <TabPane tab={this.$t('finance.audit.dueAudit')} key="1">
          <ForApproval urlValue={urlWaitForApproval} />
        </TabPane>
        <TabPane tab={this.$t('finance.audit.audited')} key="2">
          <ForApproval urlValue={urlHadApproval} />
        </TabPane>
      </Tabs>
    );
  }
}

export default InputTaxApproval;
