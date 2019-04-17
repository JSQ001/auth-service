import React from 'react';
import { Form, Button, message, Modal, Spin } from 'antd';
import { connect } from 'dva';
import 'styles/pre-payment/my-pre-payment/pre-payment-detail.scss';
import { routerRedux } from 'dva/router';

import service from './service';

import Common from './detail-common-readonlyfinancial';

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
    };
  }

  componentDidMount() {
    this.getInfo();
  }

  //获取费用申请单头信息
  getInfo = () => {
    service
      .getApplicationDetail(this.props.match.params.id)
      .then(res => {
        this.setState({ headerData: res.data, getLoading: false });
      })
      .catch(err => {
        console.error(err);
        message.error(err.response.data.message);
        this.setState({ getLoading: false });
      });
  };

  //取消
  onCancel = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/financial-view/expense-application/application-view',
      })
    );
  };

  render() {
    const { loading, dLoading, headerData, id, getLoading } = this.state;
    const otherState = (
      <Button style={{ marginLeft: '20px' }} onClick={this.onCancel}>
        返 回
      </Button>
    );
    return (
      <div style={{ paddingBottom: 100 }} className="pre-payment-detail">
        {getLoading ? <Spin /> : <Common headerData={headerData} contractEdit={true} id={id} />}
        <div className="detail-footer">{otherState}</div>
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
