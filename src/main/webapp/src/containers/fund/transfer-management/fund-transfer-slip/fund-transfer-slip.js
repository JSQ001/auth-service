import React from 'react';
import { Tabs } from 'antd';
import CreateTransferOrder from './create-transfer-order';
import QueryTransferList from './query-transfer-list';
import BatchTransfer from './batch-transfer';

const { TabPane } = Tabs;
class FundTransFer extends React.Component {
  renderTabContent = tab => {
    switch (tab) {
      case 'create': {
        return <CreateTransferOrder />;
      }
      case 'query': {
        return <QueryTransferList />;
      }
      case 'batch': {
        return <BatchTransfer />;
      }
      default: {
        return <CreateTransferOrder />;
      }
    }
  };

  render() {
    return (
      <div>
        <Tabs defaultActiveKey="create" onChange={this.changeTable}>
          <TabPane tab="调拨单创建" key="create">
            {this.renderTabContent('create')}
          </TabPane>
          <TabPane tab="调拨单查询" key="query">
            {this.renderTabContent('query')}
          </TabPane>
          <TabPane tab="批量调拨" key="batch">
            {this.renderTabContent('batch')}
          </TabPane>
        </Tabs>
      </div>
    );
  }
}

export default FundTransFer;
