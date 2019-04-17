import React from 'react';
import { Form, Button, message, Modal, Spin } from 'antd';
import { connect } from 'dva';
import 'styles/pre-payment/my-pre-payment/pre-payment-detail.scss';
import { routerRedux } from 'dva/router';

import service from './service';

import Common from './detail-common';

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

  //校验费用政策
  checkPolicy = () => {
    let { headerData } = this.state;
    this.setState({ loading: true });
    service
      .checkPolicy(headerData.id)
      .then(res => {
        if (res.data.passFlag || res.data.code == 'PASS') {
          this.setState({ loading: false });
          this.onSubmit();
        } else if (res.data.code == 'WARNING') {
          Modal.confirm({
            title: this.$t('expense.submissions.confirmed'), // 是否确认提交？
            content: res.data.message,
            onOk: () => {
              this.setState({ loading: false });
              this.onSubmit();
            },
            onCancel: () => {
              this.setState({ loading: false });
            },
          });
        } else if (res.data.code == 'FORBIDDEN') {
          Modal.error({
            title: this.$t('expense.error1'), // 错误！
            content: res.data.message,
          });
          this.setState({ loading: false });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ loading: false });
      });
  };
  //提交
  onSubmit = () => {
    let { headerData } = this.state;
    let params = {
      applicantOid: headerData.applicationOid,
      userOid: this.props.user.userOid,
      formOid: headerData.formOid,
      documentOid: headerData.documentOid,
      documentCategory: 801009,
      countersignApproverOIDs: null,
      documentNumber: headerData.documentNumber,
      remark: headerData.remarks,
      companyId: headerData.companyId,
      unitOid: headerData.departmentOid,
      amount: headerData.amount,
      currencyCode: headerData.currencyCode,
      documentTypeId: headerData.typeId,
      applicantDate: headerData.requisitionDate,
      documentId: headerData.id,
    };
    this.setState({ loading: true });

    if (headerData.budgetFlag) {
      service
        .submit(params, false)
        .then(res => {
          if (res.data.code == 'SUCCESS') {
            message.success(this.$t('expense.submit.successfully1')); // 提交成功！
            this.setState({ loading: false });
            this.onCancel();
          } else if (res.data.code == 'WARNING') {
            Modal.confirm({
              title: this.$t('expense.continue.submit1'), // 是否继续提交？
              content: res.data.message,
              onOk: () => {
                this.submit(true);
              },
              onCancel: () => {
                this.setState({ loading: false });
              },
            });
          } else if (res.data.code == 'FAILURE') {
            Modal.error({
              title: this.$t('expense.error1'), // 错误！
              content: res.data.message,
            });
            this.setState({ loading: false });
          }
        })
        .catch(err => {
          message.error(err.response.data.message);
          this.setState({ loading: false });
        });
    } else {
      this.submit(false);
    }
  };

  submit = flag => {
    let { headerData } = this.state;
    let params = {
      applicantOid: headerData.applicationOid,
      userOid: this.props.user.userOid,
      formOid: headerData.formOid,
      documentOid: headerData.documentOid,
      documentCategory: 801009,
      countersignApproverOIDs: null,
      documentNumber: headerData.documentNumber,
      remark: headerData.remarks,
      companyId: headerData.companyId,
      unitOid: headerData.departmentOid,
      amount: headerData.amount,
      currencyCode: headerData.currencyCode,
      documentTypeId: headerData.typeId,
      applicantDate: headerData.requisitionDate,
      documentId: headerData.id,
      versionNumber: headerData.versionNumber,
    };
    service
      .submit(params, flag)
      .then(res => {
        message.success(this.$t('expense.submit.successfully1')); // 提交成功！
        this.setState({ loading: false });
        this.onCancel();
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ loading: false });
      });
  };

  //删除预付款单
  onDelete = () => {
    confirm({
      title: this.$t('expense.deleted'), // 删除
      content: this.$t('expense.confirm.delete.application.form'), // 确认删除该申请单?
      onOk: () => {
        this.setState({ dLoading: true });
        service
          .deleteExpenseApplication(this.props.match.params.id)
          .then(res => {
            message.success(this.$t('expense.delete.successfully1')); // 删除成功！
            this.onCancel();
          })
          .catch(err => {
            message.error(err.response.data.message);
          });
      },
    });
  };

  //取消
  onCancel = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/expense-application/expense-application/expense-application-form',
      })
    );
  };

  render() {
    const { loading, dLoading, headerData, id, getLoading } = this.state;
    const newState = (
      <div>
        <Button
          type="primary"
          onClick={this.checkPolicy}
          loading={loading}
          style={{ margin: '0 20px' }}
        >
          {this.$t('expense.submit1')}
          {/*提 交*/}
        </Button>
        <Button onClick={this.onDelete} loading={dLoading}>
          {this.$t('expense.deleted1')}
          {/*删 除*/}
        </Button>
        <Button style={{ marginLeft: '20px' }} onClick={this.onCancel}>
          {this.$t('expense.back1')}
          {/*返 回*/}
        </Button>
      </div>
    );
    const otherState = (
      <Button style={{ marginLeft: '20px' }} onClick={this.onCancel}>
        {this.$t('expense.back1')}
        {/*返 回*/}
      </Button>
    );
    return (
      <div style={{ paddingBottom: 100 }} className="pre-payment-detail">
        {getLoading ? <Spin /> : <Common headerData={headerData} contractEdit={true} id={id} />}
        <div className="detail-footer">
          {headerData.status &&
          (headerData.status === 1001 || headerData.status === 1003 || headerData.status === 1005)
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
