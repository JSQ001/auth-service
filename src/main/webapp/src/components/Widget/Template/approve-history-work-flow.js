/**
 * Created by fdb on 2018/1/25.
 */
import React from 'react';
import { Collapse, Timeline, Spin, Row, Col, Icon, Empty, message } from 'antd';
import moment from 'moment';
import PropTypes from 'prop-types';
import httpFetch from 'share/httpFetch';
import config from 'config';

/**
 * 审批历史
 */
class ApproveHistoryWorkFlow extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      historyData: [],
      loading: false,
    };
  }

  componentDidMount() {
    this.getHistoryData();
  }

  componentWillReceiveProps(nextProps) {
    if (
      (this.props.infoData && this.props.infoData.length != nextProps.infoData.length) ||
      nextProps.type != this.props.type ||
      nextProps.oid != this.props.oid
    ) {
      this.getHistoryData(nextProps);
    }
  }

  getHistoryData = (props = this.props) => {
    const { type, oid, infoData } = props;
    if (!infoData.length) {
      if (!type || !oid) return;
      this.setState({ loading: true });
      httpFetch
        .get(
          `${config.workflowUrl}/api/workflow/approval/history?entityType=${type}&entityOid=${oid}`
        )
        .then(res => {
          this.setState({ historyData: res.data, loading: false });
        })
        .catch(err => {
          message.error(err.response.data.message);
          this.setState({ loading: false });
        });
    } else {
      this.setState({
        historyData: infoData,
        loading: false,
      });
    }
  };
  getHistory = () => {
    const { historyData } = this.state;
    let children = [];
    historyData.map((item, i) => {
      children.push(this.getHistoryRender(item, i));
    });
    return children;
  };

  getColor = value => {
    let model = {};
    switch (value.operationType) {
      case 1000:
        if (value.operation === 1001) {
          model.color = 'cyan';
          model.text = this.$t('common.create'); //新建
        } else if (value.operation === 1002) {
          model.color = 'blue';
          model.text = this.$t('common.submit'); //提交
          model.dot = 'up-circle-o';
        } else if (value.operation === 1003) {
          model.color = 'red';
          model.text = this.$t('common.withdraw'); //撤回
          model.dot = 'down-circle-o';
        } else if (value.operation === 1004) {
          model.color = 'green';
          model.text = this.$t('common.approve.pass'); // 审批通过
          model.dot = 'check-circle-o';
        } else if (value.operation === 1005) {
          model.color = 'red';
          model.text = this.$t('common.approve.rejected'); //审批驳回
          model.dot = 'close-circle-o';
        } else if (value.operation === 6001) {
          model.color = 'yellow';
          model.text = this.$t('my.zan.gua'); //暂挂中
        } else if (value.operation === 6002) {
          model.color = 'red';
          model.text = this.$t('common.cancel'); //已取消
        } else if (value.operation === 6003) {
          model.color = 'green';
          model.text = this.$t('my.contract.state.finish'); //已完成
        } else if (value.operation === 6004) {
          model.color = 'green';
          model.text = this.$t('my.contract.cancel.hold'); //取消暂挂
        } else if (value.operation === 7001) {
          model.color = 'red';
          model.text = this.$t('request.detail.loan.close'); //关闭
          model.dot = 'close-circle';
        } else if (value.operation === 9001) {
          model.color = 'blue';
          model.text = this.$t('menu.pay'); //支付
          model.dot = 'pay-circle-o';
        } else if (value.operation === 9002) {
          model.color = 'blue';
          model.text = this.$t('acp.payment.return'); //退款
          model.dot = 'down-circle-o';
        } else if (value.operation === 9003) {
          model.color = 'blue';
          model.text = this.$t('acp.payment.refund'); //退票
          model.dot = 'down-circle-o';
        } else if (value.operation === 9004) {
          model.color = 'blue';
          model.text = this.$t('acp.payment.reserved'); //反冲
          model.dot = 'clock-circle-o';
        } else {
          model.color = 'grey';
          model.text = this.$t('expense.invoice.type.unknown'); //未知
        }
        break;
      case 1001:
        if (value.operation === 1001) {
          model.color = 'blue';
          model.text = this.$t('common.submit'); //提交
          model.dot = 'up-circle-o';
        } else if (value.operation === 1002) {
          model.color = 'red';
          model.text = this.$t('common.withdraw'); //撤回
          model.dot = 'down-circle-o';
        } else if (value.operation === 5004) {
          model.color = 'blue';
          model.text = this.$t('my.return.submit'); //还款提交
          model.dot = 'up-circle-o';
        } else if (value.operation === 2001) {
          model.color = 'green';
          model.text = this.$t('common.approve.pass'); //审批通过
          model.dot = 'check-circle-o';
        } else if (value.operation === 5009) {
          model.color = 'grey';
          model.text = this.$t('my.add.hui.qian'); //添加会签
          model.dot = 'check-circle-o';
        }
        break;
      case 1002:
        if (value.operation === 2001) {
          model.color = 'green';
          model.text = this.$t('common.approve.pass'); //审批通过
          model.dot = 'check-circle-o';
        } else if (value.operation === 2002) {
          model.color = 'red';
          model.text = this.$t('common.approve.rejected'); //审批驳回
          model.dot = 'close-circle-o';
        } else if (value.operation === 5009) {
          model.color = 'grey';
          model.text = this.$t('my.add.hui.qian'); //添加会签
          model.dot = 'check-circle-o';
        }
        break;
      case 1003:
        if (value.operation === 3001) {
          model.color = 'green';
          model.text = this.$t('batch.print.approved'); //审核通过
          model.dot = 'check-circle-o';
        } else if (value.operation === 4001) {
          model.color = 'grey';
          model.text = this.$t('constants.approvelHistory.auditPay'); //财务付款
        } else if (value.operation === 3002) {
          model.color = 'red';
          model.text = this.$t('constants.documentStatus.audit.rejected'); //审核驳回
          model.dot = 'close-circle-o';
        } else if (value.operation === 4001) {
          model.color = 'grey';
          model.text = this.$t('constants.approvelHistory.auditPay'); //财务付款
        } else if (value.operation === 4000) {
          model.color = 'grey';
          model.text = this.$t('constants.approvelHistory.auditPaying'); //财务付款中
        } else if (value.operation === 7001) {
          model.color = 'grey';
          model.text = this.$t('constants.approvelHistory.amountEdit'); //核定金额修改
        } else if (value.operation === 7002) {
          model.color = 'grey';
          model.text = this.$t('constants.approvelHistory.rateEdit'); //核定汇率修改
        } else if (value.operation === 7003) {
          model.color = 'grey';
          model.text = this.$t('constants.approvelHistory.amountAndRateEdit'); //核定金额和汇率修改
        } else if (value.operation === 5009) {
          model.color = 'grey';
          model.text = this.$t('my.add.hui.qian'); //添加会签
          model.dot = 'close-circle-o';
        }
        break;
      case 1004:
        if (value.operation === 4011) {
          model.color = 'green';
          model.text = this.$t('constants.documentStatus.invoice.pass'); //开票通过
          model.dot = 'check-circle-o';
        } else if (value.operation === 4002) {
          model.color = 'red';
          model.text = this.$t('constants.approvelHistory.invoiceFail'); //开票驳回
        } else if (value.operation === 3002) {
          model.color = 'grey';
          model.text = this.$t('my.add.hui.qian');
        }
    }
    return model;
  };
  getHistoryRender = (item, i) => {
    if (item) {
      let model = this.getColor(item);
      return (
        <Timeline.Item dot={model.dot ? <Icon type={model.dot} /> : ''} color={model.color} key={i}>
          <Row>
            <Col span={3}>
              <div style={{ fontWeight: 'bold' }}>{model.text}</div>
            </Col>
            <Col span={4}>
              <div style={{ color: 'rgba(0,0,0,0.5)' }}>
                {moment(item.lastUpdatedDate).format('YYYY-MM-DD HH:mm:ss')}
              </div>
            </Col>
            <Col span={5}>
              <div style={{}}>
                {' '}
                {item.employeeName}-{item.employeeId}{' '}
              </div>
            </Col>
            <Col span={12}>
              <div>{item.operationDetail}</div>
            </Col>
          </Row>
        </Timeline.Item>
      );
    }
    return '';
  };
  render() {
    const { loading, historyData } = this.state;
    return (
      <Spin spinning={loading}>
        <div className="approve-history">
          <div className="collapse">
            <Collapse bordered defaultActiveKey={['1']}>
              <Collapse.Panel header={this.$t('expense.approval.history')} key="1">
                <div style={{ paddingTop: 10, paddingLeft: 15 }}>
                  {historyData.length ? (
                    <Timeline>{this.getHistory()}</Timeline>
                  ) : (
                    <Empty style={{ textAlign: 'center' }} />
                  )}
                </div>
              </Collapse.Panel>
            </Collapse>
          </div>
        </div>
      </Spin>
    );
  }
}

ApproveHistoryWorkFlow.propTypes = {
  type: PropTypes.string.isRequired, //单据类型
  oid: PropTypes.string.isRequired, //单据oid
  infoData: PropTypes.array.isRequired, //单据历史数据
};

ApproveHistoryWorkFlow.defaultProps = {
  type: '',
  oid: '',
  infoData: [],
};

export default ApproveHistoryWorkFlow;
