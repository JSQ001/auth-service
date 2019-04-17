import React from 'react';
import { Form, Button, message, Modal, Spin } from 'antd';
import { connect } from 'dva';
import 'styles/pre-payment/my-pre-payment/pre-payment-detail.scss';
import { routerRedux } from 'dva/router';

import service from './service';

import Common from './details-common';

const confirm = Modal.confirm;

class ExpenseApplicationDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      dLoading: false,
      headerData: {},
      id: 0,
      getLoading: true,
      //是否可编辑（对人的权限）
      peopleEditable: false,
    };
  }

  componentDidMount() {
    this.getInfo();
  }

  // 获取差旅申请单头信息
  getInfo = () => {
    service
      .getApplicationDetail(this.props.match.params.id)
      .then(res => {
        this.setState({
          headerData: res.data,
          getLoading: false,
          peopleEditable: res.data.createdBy === this.props.user.id,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ getLoading: false });
      });
  };

  // 提交
  onSubmit = () => {
    this.setState({ loading: true });
    const { headerData } = this.state;
    const params = {
      userOid: this.props.user.userOid,
      documentOid: headerData.documentOid,
      documentCategory: 801010,
      countersignApproverOIDs: null,
      documentNumber: headerData.requisitionNumber,
      remark: headerData.description,
      companyId: headerData.companyId,
      amount: headerData.totalAmount,
      currencyCode: headerData.currencyCode,
      documentTypeId: headerData.documentTypeId,
      applicantDate: headerData.requisitionDate,
      documentId: headerData.id,
    };
    service
      .submit(params)
      .then(res => {
        message.success('提交成功！');
        this.setState({ loading: false });
        this.onCancel();
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ loading: false });
      });
  };

  // 删除差旅申请单
  onDelete = () => {
    confirm({
      title: '删除',
      content: '确认删除该差旅申请单？',
      onOk: () => {
        this.setState({ dLoading: true });
        service
          .deleteTravelApplication(this.props.match.params.id)
          .then(res => {
            message.success('删除成功！');
            this.onCancel();
          })
          .catch(err => {
            message.error(err.response.data.message);
          });
      },
    });
  };

  // 取消
  onCancel = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/travel-application/travel-application-form',
      })
    );
  };

  render() {
    const { loading, dLoading, headerData, id, getLoading, peopleEditable } = this.state;
    const newState = (
      <div>
        <Button
          type="primary"
          onClick={this.onSubmit}
          loading={loading}
          style={{ margin: '0 20px' }}
        >
          提 交
        </Button>
        <Button onClick={this.onDelete} loading={dLoading}>
          删 除
        </Button>
        <Button style={{ marginLeft: '20px' }} onClick={this.onCancel}>
          返 回
        </Button>
      </div>
    );
    const otherState = (
      <Button style={{ marginLeft: '20px' }} onClick={this.onCancel}>
        返 回
      </Button>
    );
    return (
      <div style={{ paddingBottom: 100 }} className="pre-payment-detail">
        {getLoading ? <Spin /> : <Common headerData={headerData} contractEdit id={id} />}
        <div className="detail-footer">
          {headerData.status &&
          ((headerData.status === 1001 ||
            headerData.status === 1003 ||
            headerData.status === 1005) &&
            peopleEditable)
            ? newState
            : otherState}
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(Form.create()(ExpenseApplicationDetail));
