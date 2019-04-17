import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import contractService from 'containers/contract/contract-approve/contract.service';
import { Form, Button, message } from 'antd';

import ContractDetailCommon from 'containers/contract/contract-approve/contract-detail-common';
import ApproveBar from 'components/Widget/Template/approve-bar';

class ContractDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      passLoading: false,
      rejectLoading: false,
      isConfirm: this.props.params, //合同审批是否通过
    };
  }

  //获取合同状态
  getStatus = params => {
    this.setState({
      isConfirm: params === 1004 || params === 6001 || params === 6002 || params === 6003,
    });
  };

  //审批通过
  handleApprovePass = values => {
    this.setState({ passLoading: true });
    contractService
      .contractApprovePass(this.props.match.params.id, values || '')
      .then(res => {
        if (res.status === 200) {
          this.setState({ passLoading: false });
          message.success(this.$t({ id: 'common.operate.success' } /*操作成功*/));
          this.goBack();
        }
      })
      .catch(e => {
        this.setState({ passLoading: false });
        message.error(
          `${this.$t({ id: 'common.operate.filed' } /*操作失败*/)}，${e.response.data.message}`
        );
      });
  };

  //审批驳回
  handleApproveReject = values => {
    this.setState({ rejectLoading: true });
    contractService
      .contractApproveReject(this.props.match.params.id, values)
      .then(res => {
        if (res.status === 200) {
          this.setState({ rejectLoading: false });
          message.success(this.$t({ id: 'common.operate.success' } /*操作成功*/));
          this.goBack();
        }
      })
      .catch(e => {
        this.setState({ rejectLoading: false });
        message.error(
          `${this.$t({ id: 'common.operate.filed' } /*操作失败*/)}，${e.response.data.message}`
        );
      });
  };

  //返回
  goBack = () => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/contract-manager/contract-recheck/contract-recheck`,
      })
    );
  };

  render() {
    const { isConfirm, passLoading, rejectLoading } = this.state;
    return (
      <div className="contract-detail" style={{ margin: '-12px -14px' }}>
        <ContractDetailCommon
          id={this.props.match.params.id}
          isApprovePage={true}
          getContractStatus={this.getStatus}
        />
        {!isConfirm && (
          <div className="bottom-bar bottom-bar-approve">
            <ApproveBar
              passLoading={passLoading}
              style={{ paddingLeft: 20 }}
              backUrl={'/contract-manager/contract-recheck/contract-recheck'}
              rejectLoading={rejectLoading}
              handleApprovePass={this.handleApprovePass}
              handleApproveReject={this.handleApproveReject}
            />
          </div>
        )}
        {isConfirm && (
          <div className="bottom-bar bottom-bar-approve">
            <div style={{ lineHeight: '50px', marginLeft: 30 }}>
              <Button onClick={this.goBack} className="back-btn">
                {this.$t({ id: 'common.back' } /*返回*/)}
              </Button>
            </div>
          </div>
        )}
      </div>
    );
  }
}

const wrappedContractDetail = Form.create()(ContractDetail);
export default connect(
  null,
  null,
  null,
  { withRef: true }
)(wrappedContractDetail);