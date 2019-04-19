import React from 'react';
import { connect } from 'dva';
import { Tabs, Button } from 'antd';
const TabPane = Tabs.TabPane;
import DetailsOfCancelledInvoices from './details-of-cancelled-invoices';
import WriteCounterHedgingTransactions from './write-counter-hedging-transactions';
import { routerRedux } from 'dva/router';
class AlreadyAmount extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }
  //返回列表页
  // HandleReturn = () => {
  //   const { dispatch } = this.props;
  //   dispatch(
  //     routerRedux.push({
  //       pathname: `./`,
  //     })
  //   );
  // };
  //返回到开票点管理页面
  HandleReturn = e => {
    e.preventDefault();
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/tax/issuance-sales-invoices/waiting-Invoice-trading-flow`,
      })
    );
  };
  render() {
    const {
      match: {
        params: { id },
      },
    } = this.props;
    return (
      <div className="alreadyAmount">
        <Tabs>
          <TabPane tab="核销发票明细" key="1">
            <DetailsOfCancelledInvoices id={id} />
          </TabPane>
          <TabPane tab="核销反冲交易" key="2">
            <WriteCounterHedgingTransactions id={id} />
          </TabPane>
        </Tabs>
        <Button
          type="primary"
          className="button-return"
          style={{ marginLeft: '20px', marginRight: '8px' }}
          onClick={this.HandleReturn}
        >
          返回
        </Button>
      </div>
    );
  }
}

function callback(key) {}
export default connect()(AlreadyAmount);
