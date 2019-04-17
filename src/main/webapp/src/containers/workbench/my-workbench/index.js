import React, { Component } from 'react';
import { Tabs } from 'antd';
import WaitDeal from './wait-deal.js';
import HadDeal from './had-deal.js';
import './index';

const TabPane = Tabs.TabPane;

class MyWorkBench extends Component {
  constructor(props) {
    super(props);
    this.state = {
      tab: '1',
      type: 'WORKING',
    };
  }

  componentWillMount() {
    let type = this.props.match.params.type;
    if (type === 'WORKING' || type === 'PENDING') {
      this.setState({ type: type, tab: '1' });
    } else {
      this.setState({ tab: '2' });
    }
  }

  handleTabChange = e => {
    this.setState({ tab: e });
  };

  render() {
    const { tab, type } = this.state;
    return (
      <div className="my-workbench">
        <Tabs defaultActiveKey={tab} onChange={this.handleTabChange}>
          <TabPane tab="待处理" key="1">
            <WaitDeal type={type} />
          </TabPane>
          <TabPane tab="已处理" key="2">
            <HadDeal />
          </TabPane>
        </Tabs>
      </div>
    );
  }
}

export default MyWorkBench;
