import React from 'react';
import { messages } from 'utils/utils';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import 'styles/fund/pay.scss';
import { Tabs, Form } from 'antd';
import Pay from './pay';

import 'styles/pay/pay-workbench/pay-workbench.scss';

const { TabPane } = Tabs;

class PayShow extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      nowStatus: 'UNPAID', // 初始化默认状态
      tabs: [
        // tab页的切换
        { key: 'UNPAID', name: messages('pay.unpay') }, // 未支付Unpaid
        { key: 'BEING', name: messages('pay.paying') }, // 支付中Paying
        { key: 'SUCCESS', name: '已支付' }, // 已支付Success
        // { key: 'Success', name: messages('pay.pay.success') }, // 已支付
      ],
      payShow: '/fund-pay/pay-show/pay-show', // 付款单支付
    };
  }

  componentWillMount() {
    // const { nowStatus } = this.state;
    // console.log('--父组件中tabs的状态nowStatus---', nowStatus);
    const { match } = this.props;
    console.log('--父组件中的match.params.tab---', match.params.tab);
    if (match.params.tab) {
      this.setState({ nowStatus: match.params.tab });
    }
  }

  /**
   *  tab页的改变
   */
  onChangeTabs = key => {
    console.log('000');
    console.log('key', key);
    const { dispatch } = this.props;
    const { payShow } = this.state;
    // match.params.subTab = undefined;
    this.setState(
      {
        nowStatus: key,
      },
      () => {
        // console.log('--父组件中的nowStatus---', this.state.nowStatus)
        // console.log('--父组件中的nowStatus---', match.params.tab)
        dispatch(
          routerRedux.replace({
            pathname: `${payShow}/${key}`,
          })
        );
      }
    );
  };

  renderContent = nowStatus => {
    let content = null;
    const { match } = this.props;
    // console.log('父组件中的match===', match)
    switch (nowStatus) {
      case 'UNPAID':
        content = <Pay nowStatus={nowStatus} />;
        break;
      case 'BEING': // change={this.onChangeTabs(nowStatus)}
        content = <Pay nowStatus={nowStatus} />;
        break;
      case 'SUCCESS':
        content = <Pay nowStatus={nowStatus} subTab={match.params.subTab} />;
        break;
      default:
        content = <Pay nowStatus={nowStatus} />;
    }
    return content;
  };

  render() {
    const { tabs, nowStatus } = this.state;
    // const { match } = this.props;
    console.log('--父组件render中的nowStatus---', nowStatus, tabs);
    return (
      <div className="pay-show">
        <Tabs onChange={this.onChangeTabs} defaultActiveKey={nowStatus}>
          {tabs.map(tab => {
            return <TabPane tab={tab.name} key={tab.key} />;
          })}
        </Tabs>
        {this.renderContent(nowStatus)}
      </div>
    );
  }
}

const wrappedCompanyDistribution = Form.create()(PayShow);

export default connect()(wrappedCompanyDistribution);
