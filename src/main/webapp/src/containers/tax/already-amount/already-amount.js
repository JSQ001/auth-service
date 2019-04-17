import React from 'react';
import { connect } from 'dva';
import { Tabs } from 'antd';
const TabPane = Tabs.TabPane;
import DetailsOfCancelledInvoices from './details-of-cancelled-invoices';
import WriteCounterHedgingTransactions from './write-counter-hedging-transactions';

class AlreadyAmount extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }
  render() {
    return (
      <div className="alreadyAmount">
        <Tabs>
          <TabPane tab="核销发票明细" key="1">
            <DetailsOfCancelledInvoices />
          </TabPane>
          <TabPane tab="核销反冲交易" key="2">
            <WriteCounterHedgingTransactions />
          </TabPane>
        </Tabs>
      </div>
    );
  }
}

function callback(key) {}
export default connect()(AlreadyAmount);
