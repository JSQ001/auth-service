/*eslint-disable*/
import React from 'react';
import { connect } from 'dva';
import { Tabs } from 'antd';
const TabPane = Tabs.TabPane;
import RegisterBasic from './register-basic';
import CitInformation from './cit-information';
import VatInformation from './vat-information';

class TaxLevel extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }
  render() {
    return (
      <div className="VatInformation">
        <Tabs defaultActiveKey="1" onChange={callback}>
          <TabPane tab="行政层级" key="1">
            <RegisterBasic />
          </TabPane>
          <TabPane tab="企业所得税层级" key="2">
            <CitInformation />
          </TabPane>
          <TabPane tab="增值税层级" key="3">
            <VatInformation />
          </TabPane>
        </Tabs>
      </div>
    );
  }
}
function callback(key) {}
export default connect()(TaxLevel);
