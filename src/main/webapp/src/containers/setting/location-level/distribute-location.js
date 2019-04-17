import React, { Component } from 'react';
import { Tabs } from 'antd';
import AddLocationTab from './add-location-tab';
import LocationDetailTab from './location-detail-tab';

const TabPane = Tabs.TabPane;

class DistributeLocation extends Component {
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
  // 地点添加后调用
  addSuccess = () => {
    this.setState({ tabKey: '2' });
  };

  render() {
    const { tabKey } = this.state;
    const { params } = this.props.match;
    return (
      <Tabs defaultActiveKey={tabKey} activeKey={tabKey} onChange={this.tabChange}>
        <TabPane tab={'添加地点' /*this.$t('expense.wallet.myWallet')*/} key="1">
          <AddLocationTab onAddSuccess={this.addSuccess} params={params} />
        </TabPane>

        <TabPane tab={'详细信息' /*this.$t('expense.wallet.invoiceDetail')*/} key="2">
          {tabKey === '2' && <LocationDetailTab params={params} />}
        </TabPane>
      </Tabs>
    );
  }
}

export default DistributeLocation;
