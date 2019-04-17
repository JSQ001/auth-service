import React from 'react';
import { Button } from 'antd';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import ReimburseDetailCommon from 'containers/workbench/my-workbench/my-workbench-detail/reimburse-detail-common.js';
import service from './service';

class ReimburseDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      headerData: {},
    };
  }

  componentDidMount() {
    this.getInfo();
  }

  // 获取报账单信息
  getInfo = () => {
    const {
      match: { params },
    } = this.props;
    service.getBase({ expenseReportId: params.documentId }).then(res => {
      this.setState({
        headerData: res.data,
      });
    });
  };

  // 取消
  onCancel = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: '/workbench/my-workbench/my-workbench/READ',
      })
    );
  };

  render() {
    const { headerData } = this.state;
    const barStyle = {
      zIndex: 2,
      position: 'fixed',
      left: '220px',
      right: 0,
      bottom: 0,
      lineHeight: '60px',
      backgroundColor: '#fff',
      boxShadow: '0 -2px 8px rgba(0, 0, 0, 0.15)',
    };
    const {
      match: { params },
    } = this.props;

    return (
      <div style={{ paddingBottom: '75px' }}>
        <ReimburseDetailCommon getInfo={this.getInfo} headerData={headerData} id={params.id} />
        <div style={barStyle}>
          <Button style={{ marginLeft: '40px' }} onClick={this.onCancel}>
            {this.$t('common.back' /* 返回 */)}
          </Button>
        </div>
      </div>
    );
  }
}

export default connect()(ReimburseDetail);
