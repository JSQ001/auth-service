import React, { Component } from 'react';
import { Tabs } from 'antd';
import InvoiceDetails from './invoice-details';
import MyWalletTab from './my-wallet-tab';

const TabPane = Tabs.TabPane;

class MyWallet extends Component {
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
        <TabPane tab={this.$t('expense.wallet.myWallet')} key="1">
          <MyWalletTab />
        </TabPane>
        <TabPane tab={this.$t('expense.wallet.invoiceDetail')} key="2">
          <InvoiceDetails />
        </TabPane>
      </Tabs>
    );
  }
}

export default MyWallet;
