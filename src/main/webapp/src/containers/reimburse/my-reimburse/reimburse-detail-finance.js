import React from 'react';
import { connect } from 'dva';
import reimburseService from 'containers/reimburse/my-reimburse/reimburse.service';
import { Form, Affix, Button, message, Popconfirm, Modal } from 'antd';
const confirm = Modal.confirm;

import ReimburseDetailCommon from 'containers/reimburse/my-reimburse/reimburse-detail-common-finance';
import 'styles/contract/my-contract/contract-detail.scss';
import { routerRedux } from 'dva/router';
import backlashService from '../../pay/payment-backlash/pay-backlash.service';

class ReimburseDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      dLoading: false,
      submitAble: false,
      headerData: {},
      submitLoading: false,
    };
  }

  componentWillMount() {
    this.getInfo();
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.match) {
      if (nextProps.match.params.flag && !this.props.match.params.flag) {
        this.getInfo(nextProps.match.params.id);
      }
    } else {
      if (nextProps.params.flag && !this.props.params.flag) {
        this.getInfo(nextProps.params.id);
      }
    }
  }

  //获取报账单信息
  getInfo = id => {
    if (!id) {
      if (this.props.match) {
        id = this.props.match.params.id;
      } else {
        id = this.props.params.id;
      }
    }
    reimburseService.getReimburseDetailById(id).then(res => {
      this.setState({
        headerData: res.data,
      });
    });
  };

  //获取合同状态
  getStatus = status => {
    if (status === 1001 || status === 1005 || status === 1003) {
      this.setState({ submitAble: true });
    }
  };
  //提交
  onSubmit = () => {
    this.setState({ submitLoading: true });
    reimburseService
      .checkBudget(this.props.match.params.id)
      .then(res => {
        if (res.data.passFlag) {
          this.submit(true);
          return;
        }

        if (res.data.code && res.data.code == 'BUD_003') {
          confirm({
            title: '提示',
            content: res.data.message,
            onOk: () => {
              this.submit(true);
            },
            onCancel: () => {
              this.setState({ submitLoading: false });
            },
          });
        } else if (res.data.code && res.data.code == 'BUD_002') {
          message.error(res.data.message);
          this.setState({ submitLoading: false });
        } else if (res.data.code && res.data.code == 'BUD_000') {
          this.submit(false);
        }
      })
      .catch(err => {
        let mess;
        this.setState({ submitLoading: false });
        if (err.response.data.message.indexOf('CONTRACT_STATUS_HOLD') > 0) {
          mess = err.response.data.message.replace('CONTRACT_STATUS_HOLD', this.$t('my.zan.gua'));
        } else if (err.response.data.message.indexOf('CONTRACT_STATUS_CANCEL') > 0) {
          mess = err.response.data.message.replace(
            'CONTRACT_STATUS_CANCEL',
            this.$t('common.cancel')
          );
        } else if (err.response.data.message.indexOf('CONTRACT_STATUS_FINISH')) {
          mess = err.response.data.message.replace(
            'CONTRACT_STATUS_FINISH',
            this.$t('my.contract.state.finish')
          );
        }
        message.error('提交失败：' + mess);
      });
  };

  //提交
  submit = flag => {
    const { headerData } = this.state;
    let params = {
      documentId: headerData.id,
    };
    reimburseService
      .submit(params, 'first')
      .then(res => {
        let passFlag = res.data.passFlag;
        if (!passFlag) {
          if (res.data.code === 'WARNING') {
            Modal.confirm({
              title: '确认提交？',
              content: res.data.message,
              okText: this.$t({ id: 'pay.backlash.ok' }),
              cancelText: this.$t({ id: 'pay.backlash.cancel' }),
              onOk: () => {
                reimburseService
                  .submit(params, true)
                  .then(res => {
                    passFlag = res.data.passFlag;
                  })
                  .catch(e => {
                    message.error(e.response.data.message);
                    this.onCancel(true);
                  });
              },
              onCancel() {},
            });
          }
          if (res.data.code === 'FAILURE') {
            message.error('提交失败：' + res.data.message);
          }
        }
        if (passFlag) {
          message.success('提交成功！');
          this.setState({ submitLoading: false });
          this.onCancel();
        }
      })
      .catch(err => {
        message.error('提交失败：' + err.response.data.message);
        this.setState({ submitLoading: false });
      });
  };

  //删除
  onDelete = () => {
    this.setState({ dLoading: true });
    reimburseService
      .deleteExpReportHeader(this.props.match.params.id)
      .then(res => {
        message.success('删除成功！');
        this.onCancel();
      })
      .catch(e => {
        this.setState({ dLoading: false });
        message.error(`${this.$t('common.operate.filed')}，${e.response.data.message}`);
      });
  };

  //取消
  onCancel = () => {
    const params = this.props.match.params.id.split('?')[1] || '';
    const pathname = params
      ? `/my-wallet/my-wallets/wallet`
      : `/my-reimburse/my-reimburse/my-reimburse`;
    this.props.dispatch(
      routerRedux.push({
        pathname,
      })
    );
  };

  render() {
    const { loading, dLoading, submitAble, headerData, submitLoading } = this.state;
    let isEdit = [1001, 1003, 1005].includes(headerData.status);
    let id = this.props.match ? this.props.match.params.id : this.props.params.id;
    return (
      <div style={{ paddingBottom: 60 }}>
        <ReimburseDetailCommon
          getInfo={this.getInfo}
          headerData={headerData}
          id={id}
          getContractStatus={this.getStatus}
        />
        {/* <div style={{ paddingLeft: 20 }}>
          {this.props.params && this.props.params.refund
            ? ''
            : !isEdit && (
                <Affix
                  offsetBottom={0}
                  style={{
                    position: 'fixed',
                    bottom: 0,
                    marginLeft: '-35px',
                    width: '100%',
                    height: '50px',
                    boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.067)',
                    background: '#fff',
                    lineHeight: '50px',
                    zIndex: 1,
                  }}
                />
              )}
        </div> */}
      </div>
    );
  }
}

const wrappedReimburseDetail = Form.create()(ReimburseDetail);

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
  };
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedReimburseDetail);

//export default wrappedContractDetail;
