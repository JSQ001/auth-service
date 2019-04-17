import React from 'react';
import { Form, message, Spin } from 'antd';
import { connect } from 'dva';
import service from './service';
import 'styles/pre-payment/my-pre-payment/pre-payment-detail.scss';
import Common from './detail-common';

class ExpenseApplicationDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      headerData: {},
      id: 0,
      getLoading: true,
    };
  }

  componentDidMount() {
    this.getInfo();
  }

  // 获取费用申请单头信息
  getInfo = () => {
    // eslint-disable-next-line react/destructuring-assignment
    service
      .getApplicationDetail(this.props.id)
      .then(res => {
        this.setState({ headerData: res.data, getLoading: false });
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ getLoading: false });
      });
  };

  render() {
    const { headerData, id, getLoading } = this.state;
    return getLoading ? <Spin /> : <Common headerData={headerData} id={id} />;
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(Form.create()(ExpenseApplicationDetail));
