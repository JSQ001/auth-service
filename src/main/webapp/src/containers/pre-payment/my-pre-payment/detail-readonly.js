/* eslint-disable */

import React from 'react';

// import menuRoute from 'routes/menuRoute'
import { Form, Spin, message, Modal } from 'antd';

import { connect } from 'dva';
import PrePaymentCommon from 'containers/pre-payment/my-pre-payment/pre-payment-common';
import 'styles/pre-payment/my-pre-payment/pre-payment-detail.scss';
import prePaymentService from 'containers/pre-payment/my-pre-payment/me-pre-payment.service';

class PrePaymentDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      headerData: {},
      id: 0,
    };
  }

  componentDidMount() {
    this.getInfo();
  }

  //获取预付款头信息
  getInfo = () => {
    const { id } = this.props;
    prePaymentService
      .getHeadById(id)
      .then(res => {
        if (!res.data) {
          message.error('该单据不存在！');
        }
        this.setState({
          headerData: res.data,
          id: id,
          loading: false,
        });
      })
      .catch(e => {
        console.log(`获取预付款头信息失败：${e}`);
        message.error('预付款单据数据加载失败，请重试');
      });
  };

  render() {
    const { headerData, id, loading } = this.state;
    return (
      <Spin spinning={loading}>
        <PrePaymentCommon params={headerData} contractEdit={true} id={id} />
      </Spin>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

const wrappedPrePaymentDetail = Form.create()(PrePaymentDetail);
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedPrePaymentDetail);
