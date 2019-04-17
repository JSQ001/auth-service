import React from 'react';
import { Button, Modal, Input, message } from 'antd';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import ReimburseDetailCommon from 'containers/workbench/my-workbench/my-workbench-detail/reimburse-detail-common.js';
import service from './service';

class ReimburseDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      headerData: {},
      operatorText: '',
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
        pathname: '/workbench/my-workbench/my-workbench/PENDING',
      })
    );
  };

  // 取消暂挂弹框
  cancelPending = () => {
    const content = (
      <div style={{ marginTop: '20px', marginRight: '38px' }}>
        <Input placeholder="请输入取消暂挂原因" onChange={this.operatorTextChange} />
      </div>
    );
    const confirm = this.confirmCancel;
    Modal.confirm({
      title: '请输入取消暂挂的原因',
      content,
      onOk() {
        confirm();
      },
    });
  };

  // 取消原因
  operatorTextChange = e => {
    this.setState({ operatorText: e.target.value });
  };

  // 确认取消
  confirmCancel = () => {
    const { operatorText } = this.state;
    const {
      match: { params },
    } = this.props;
    const value = {
      operatorText,
      id: params.id,
    };
    service
      .cancelhold(value)
      .then(() => {
        message.success('操作成功');
        this.onCancel();
      })
      .catch(err => message.error(err.response.data.message));
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
        <ReimburseDetailCommon getInfo={this.getInfo} headerData={headerData} id={params.id}>
          <Button
            onClick={this.cancelPending}
            style={{ marginBottom: '14px', float: 'right' }}
            type="primary"
          >
            取消暂挂
          </Button>
        </ReimburseDetailCommon>
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
