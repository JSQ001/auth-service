import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import businessTypeService from './business-type.service';
import { Tabs, Affix, Icon, Spin } from 'antd';
const TabPane = Tabs.TabPane;

import BusinessProcedure from 'containers/workbench/business-procedure/business-procedure';
import BusinessPage from 'containers/workbench/business-page/business-page';
import BusinessParameter from 'containers/workbench/business-parameter/business-parameter';
import DispatchRule from 'containers/workbench/dispatch-rule/dispatch-rule';

class BusinessTypeDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tab: 'PROCEDURE',
      spin: true,
      businessType: this.props.businessType.id ? this.props.businessType : {},
      tabs: [
        { key: 'PROCEDURE', name: this.$t('workbench.process.approach') }, // 过程方法
        { key: 'PAGE', name: this.$t('workbench.rule.page') }, // 操作界面
        { key: 'PARAMETER', name: this.$t('workbench.businessType.dispatch.rule.parameter') }, // 规则参数
        { key: 'RULE', name: this.$t('workbench.rule.task') }, // 派工规则
      ],
      businessTypeDetail: '/workbench/business-type/business-type/detail/:id/:tab', // 详情
      businessTypeUrl: '/workbench/business-type/business-type', // 业务类型定义
    };
  }

  componentWillMount() {
    // redux中没有数据则设置
    if (!this.props.businessType.id) {
      businessTypeService.getBusinessTypeById(this.props.match.params.id).then(res => {
        this.setState({ businessType: res.data, spin: false });
        this.props.dispatch({
          type: 'workbench/setBusinessType',
          businessType: res.data,
        });
      });
    }
    if (this.state.businessType.id) {
      this.setState({ loading: true, spin: false });
    }
    if (this.props.match.params.tab !== ':tab') {
      this.setState({ tab: this.props.match.params.tab });
    }
  }

  //渲染Tabs
  renderTabs() {
    return this.state.tabs.map(tab => {
      return <TabPane tab={tab.name} key={tab.key} />;
    });
  }
  // 更改标签页
  onChangeTabs = key => {
    let path = this.state.businessTypeDetail
      .replace(':id', this.state.businessType.id)
      .replace(':tab', key);
    this.props.dispatch(
      routerRedux.replace({
        pathname: path,
      })
    );
    this.setState({
      tab: key,
    });
  };
  // 返回
  handleBack = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: this.state.businessTypeUrl,
      })
    );
  };
  // 渲染界面
  renderContent = () => {
    let content = null;
    const { tab, businessType } = this.state;

    switch (tab) {
      case 'PROCEDURE':
        content = BusinessProcedure;
        break;
      case 'PAGE':
        content = BusinessPage;
        break;
      case 'PARAMETER':
        content = BusinessParameter;
        break;
      case 'RULE':
        content = DispatchRule;
        break;
    }
    return this.props.match.params.id
      ? React.createElement(
          content,
          Object.assign({}, this.props.match.params, { businessType: businessType })
        )
      : null;
  };

  render() {
    const { businessType, tab } = this.state;
    return (
      <Spin spinning={this.state.spin}>
        <div style={{ paddingBottom: 60 }}>
          <h3 className="header-title">{businessType.businessTypeName}</h3>
          <Tabs onChange={this.onChangeTabs} defaultActiveKey={tab}>
            {this.renderTabs()}
          </Tabs>
          {this.renderContent()}
          <Affix
            className="bottom-bar-approve"
            style={{
              height: '50px',
              boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.067)',
              background: '#fff',
              lineHeight: '50px',
              zIndex: 1,
              paddingLeft: 20,
            }}
          >
            <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleBack}>
              <Icon type="rollback" style={{ marginRight: '5px' }} />
              {this.$t({ id: 'common.back' })}
            </a>
          </Affix>
        </div>
      </Spin>
    );
  }
}
function mapStateToProps(state) {
  return {
    businessType: state.workbench.businessType,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(BusinessTypeDetail);
