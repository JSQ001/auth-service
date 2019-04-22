import React from 'react';
import {
  Row,
  Form,
  Col,
  Alert,
  Card,
  Select,
  Modal,
  Input,
  Icon,
  DatePicker,
  message,
  Button,
  Popover,
} from 'antd';
import { connect } from 'dva';
import moment from 'moment';
import { routerRedux } from 'dva/router';
import Table from 'widget/table';
import PayShowService from './pay-show.service';
import { accAdd, objectEquals } from '../../fund-components/utils';

const { Option } = Select;
const selectWidth = { width: '100%' };

class ManualQuery extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      selectValue: {},
      payUpdateValue: {}, // 存放支付状态和支付日期的修改值
      showOrHideButton: 'none',
      selectedRowKeys: [],
      selectedRow: [],
      doingSelecRow: {}, // 正在操作的行
      noticeAlert: null, // 提示信息
      showModal: false, // 退回修改框
      payList: [], // 存放支付状态值列表
      tableData: [], // 数据列表
      loading: false,
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total,
          }),
      },
      columns: [
        {
          title: this.$t('fund.early.warning.state') /* 预警状态 */,
          dataIndex: 'warningStatusDesc',
          width: 100,
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          title: this.$t('fund.early.warning.information') /* 预警信息 */,
          dataIndex: 'warningData',
          width: 120,
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          title: this.$t('fund.source.document.number') /* 来源单据号 */,
          dataIndex: 'tradeCode',
          width: 200,
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          title: this.$t('fund.name.receiver') /* 收款户名 */,
          dataIndex: 'gatherAccountName',
          width: 100,
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          title: this.$t('fund.collection.branch') /* 收款分行 */,
          dataIndex: 'gatherBranchBankName',
          width: 180,
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          title: this.$t('fund.receiving.account') /* 收款账号 */,
          dataIndex: 'gatherAccount',
          width: 190,
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          title: this.$t('fund.amount') /* 金额 */,
          dataIndex: 'amount',
          width: 140,
          render: value => <div style={{ textAlign: 'right' }}>{this.filterMoney(value)}</div>,
        },
        {
          title: this.$t('fund.payment.purpose') /* 付款用途 */,
          dataIndex: 'paymentPurposeDesc',
          width: 100,
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          title: this.$t('fund.abstract') /* 摘要 */,
          dataIndex: 'description',
          width: 100,
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          title: this.$t('fund.public.private.signs') /* 公私标志 */,
          dataIndex: 'propFlagDesc',
          width: 100,
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          title: this.$t('fund.kashe.logo') /* 卡折标志 */,
          dataIndex: 'cardSignDesc',
          width: 100,
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          title: this.$t('fund.payment.status') /* 支付状态 */,
          dataIndex: 'paymentStatusDesc',
          width: 110,
          align: 'center',
          render: (record, index) => {
            const {
              payList,
              payUpdateValue,
              selectValue,
              paymentMethodDesc,
              selectedRow,
              doingSelecRow,
            } = this.state;
            const { match } = this.props;
            let value = {};
            if (selectedRow.includes(index) && JSON.stringify(payUpdateValue) !== '{}') {
              value = { key: payUpdateValue.payStatus.key, value: payUpdateValue.payStatus.label };
            } else if (JSON.stringify(selectValue) !== '{}' && objectEquals(index, doingSelecRow)) {
              value = selectValue;
            } else {
              value = {
                key: index.paymentStatus,
                value: index.paymentStatusDesc,
              };
            }
            return match.params.status === 'BEING' &&
              paymentMethodDesc !== '银企直联' /* 银企直连 */ ? (
              /* eslint-disable */
              <Select
                placeholder={this.$t('fund.please.choose')} /* 请选择 */
                style={selectWidth}
                labelInValue
                value={value}
                onChange={e => this.onSelectValue(e, index)}
              >
                {payList.length > 0 &&
                  payList.map(item => {
                    return <Option key={item.value}>{item.name}</Option>;
                  })}
              </Select>
            ) : (
              /* eslint-disable */
              <span>{record || ''}</span>
            );
          },
        },
        {
          title: this.$t('fund.date.of.payment') /* 支付日期 */,
          dataIndex: 'paymentDate',
          width: 110,
          align: 'center',
          render: (record, index) => {
            const { match } = this.props;
            const { paymentMethodDesc } = this.state;
            return match.params.status === 'BEING' && paymentMethodDesc !== '银企直连' ? (
              <DatePicker
                onChange={e => this.selectDate(e, index)}
                value={index.paymentDate ? moment(index.paymentDate) : ''}
              />
            ) : (
              <span>{index.paymentDateDesc}</span>
            );
          },
        },
        {
          title: this.$t('fund.bank.feedback.information') /* 银行反馈信息 */,
          dataIndex: 'bankFeedback',
          width: 100,
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          title: this.$t('fund.the.receipt') /* 回单 */,
          dataIndex: 'receiptNum',
          width: 100,
          align: 'center',
          render: (record, index) => {
            // console.log(record)
            const { paymentMethodDesc, billTypeDesc } = this.state;
            const { match } = this.props;
            return match.params.status === 'BEING' &&
              paymentMethodDesc !== '银企直连' &&
              billTypeDesc === '手工付款单' ? (
              <Input value={index.receiptNum} onChange={e => this.inputData(e, index)} />
            ) : (
              <span>{record || ''}</span>
            );
          },
        },
      ],
    };
  }

  componentWillMount() {
    const { match } = this.props;
    if (match.params.status === 'BEING') {
      this.setState({
        showOrHideButton: 'inline',
      });
    }
  }

  componentDidMount() {
    const { match } = this.props;
    if (match.params.id) {
      this.setState({
        baseId: match.params.id,
      });
      this.getList(match.params.id);
      this.getUserList(match.params.paymentBatchNumber);
      this.getPayOptions(); // 获取支付值列表
    }
  }

  /**
   * 数据列表
   */
  getList = id => {
    const { pagination } = this.state;
    this.setState({ loading: true });
    PayShowService.getManualList(pagination.page, pagination.pageSize, id).then(response => {
      const { data } = response;
      this.setState({
        tableData: data,
        loading: false,
        pagination: {
          ...pagination,
          total: Number(response.headers['x-total-count'])
            ? Number(response.headers['x-total-count'])
            : 0,
          onChange: this.onChangePager,
          current: pagination.page + 1,
          onShowSizeChange: this.onShowSizeChange,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) =>
            this.$t('common.show.total', { range0: `${range[0]}`, range1: `${range[1]}`, total }),
        },
      });
    });
  };

  /**
   * 获取支付状态值列表
   */
  getPayOptions = () => {
    this.getSystemValueList('ZJ_PAY_STATUS')
      .then(res => {
        if (res.data.values.length > 0) {
          this.setState({
            payList: res.data.values,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 根据头id获取数据
   * @param {*} number
   */
  getUserList(number) {
    const { pagination } = this.state;
    const batchNumber = { paymentBatchNumber: number };
    PayShowService.getPaymentQueryList(pagination.page, pagination.pageSize, batchNumber).then(
      response => {
        const { data } = response;
        this.setState({
          paymentBatchNumber: data[0].paymentBatchNumber || '', // 付款单号
          paymentMethodDesc: data[0].paymentMethodDesc || '', // 付款方式
          paymentCompanyName: data[0].paymentCompanyName || '', // 公司名称
          paymentAccountName: data[0].paymentAccountName || '',
          paymentAccount: data[0].paymentAccount || '', //
          currencyCode: data[0].currencyCode || '', // 币种
          description: data[0].description || '', // 描述
          bankCodeName: data[0].bankCodeName || '', // 付款银行名称
          billDateDesc: data[0].billDateDesc, // 单据日期
          billTypeDesc: data[0].billTypeDesc, // 回单
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            onChange: this.onChangePager,
            current: pagination.page + 1,
          },
        });
      }
    );
  }

  /**
   * 行选择
   */
  onSelectChange = (selectedRowKeys, selectedRow) => {
    this.setState(
      {
        selectedRowKeys,
        selectedRow,
      },
      () => {
        if (selectedRowKeys.length > 0) {
          this.noticeAlert(selectedRow);
        } else {
          this.setState({
            noticeAlert: null,
          });
        }
      }
    );
  };

  /**
   * 提示框显示
   */
  noticeAlert = rows => {
    const initialAmount = 0;
    const totalAmount = rows.reduce(
      (accumulator, currentValue) => accAdd(accumulator, currentValue.amount),
      initialAmount
    );
    const noticeAlert = (
      <span>
        {this.$t('fund.selected')}
        {/* 已选择 */}
        <span style={{ fontWeight: 'bold', color: '#108EE9' }}> {rows.length} </span>{' '}
        {this.$t('fund.desc.code7')}
        <span style={{ fontWeight: 'bold', color: '#108EE9' }}>{totalAmount}</span>
        {this.$t('fund.yuan')}
        {/* 元 */}
      </span>
    );
    this.setState({
      noticeAlert: rows.length ? noticeAlert : null,
    });
  };

  /**
   * 分页点击
   */
  onChangePager = pagination => {
    const temp = {};
    const { baseId } = this.state;
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList(baseId);
      }
    );
  };

  /**
   * 改变每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    const temp = {};
    temp.page = current - 1;
    temp.pageSize = pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 返回上一页
   */
  handleBack = () => {
    const { dispatch, match } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/fund-pay/pay-show/pay-show/${match.params.status}`,
      })
    );
  };

  /**
   * 修改保存确定按钮
   */
  confirmButton = () => {
    const { selectedRow } = this.state;
    const { dispatch } = this.props;
    // console.log('保存', selectedRow)
    PayShowService.updateSave(selectedRow)
      .then(res => {
        // console.log(res)
        if (res.data === 'SUCCESS') {
          message.success(this.$t('fund.save.successful')); /* 保存成功 */
          dispatch(
            routerRedux.push({
              pathname: `/fund-pay/pay-show/pay-show/${'SUCCESS'}`,
            })
          );
        } else {
          message.error('保存失败');
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 批量修改按钮
   */
  bulkChanges = () => {
    const { selectedRow } = this.state;
    if (selectedRow.length > 0) {
      this.setState({
        showModal: true,
      });
    } else {
      message.error(this.$t('')); // 请选中要修改的单据！
    }
  };

  /**
   * 选中修改后支付状态值列表的值
   */
  onSelectValue = (e, index) => {
    const { tableData } = this.state;
    tableData.map(item => {
      if (item.id === index.id) {
        /* eslint-disable */
        item.paymentStatus = e.key;
        item.paymentStatusDesc = e.label;
        /* eslint-disable */
      }
      return item;
    });
    this.setState({
      payUpdateValue: {},
      selectValue: e,
      doingSelecRow: index,
      tableData: tableData,
    });
  };

  /**
   * 回单
   */
  inputData = (e, index) => {
    const { tableData } = this.state;
    tableData.map(item => {
      if (item.id === index.id) {
        /* eslint-disable */
        item.receiptNum = e.target.value;
        /* eslint-disable */
      }
      return item;
    });
    this.setState({
      tableData: tableData,
    });
  };

  /**
   * 选中修改后支付日期的值
   */
  selectDate = (e, index) => {
    const { tableData } = this.state;
    tableData.map(item => {
      if (item.id === index.id) {
        /* eslint-disable */
        item.paymentDate = e;
        /* eslint-disable */
      }
      return item;
    });
    this.setState({
      tableData: tableData,
    });
  };

  /**
   * 弹框确定
   */
  handleOk = () => {
    const { form } = this.props;
    const { selectedRow, tableData } = this.state;
    const values = form.getFieldsValue();
    tableData.map(item => {
      if (selectedRow.includes(item)) {
        /* eslint-disable */
        item.paymentStatus = values.payStatus.key;
        item.paymentStatusDesc = values.payStatus.key.label;
        item.paymentDate = values.payTime;
        /* eslint-disable */
      }
      return item;
    });
    this.setState({
      showModal: false,
      payUpdateValue: values,
      tableData: tableData,
    });
  };

  /**
   * 弹框取消
   */
  handleCancel = () => {
    this.setState({
      showModal: false,
    });
  };

  render() {
    const {
      showOrHideButton,
      noticeAlert,
      payList,
      showModal,
      loading,
      selectedRowKeys,
      selectedRow,
      tableData,
      pagination,
      columns,
      paymentBatchNumber,
      paymentMethodDesc,
      paymentCompanyName,
      paymentAccountName,
      billDateDesc,
      paymentAccount,
      currencyCode,
      description,
      bankCodeName,
    } = this.state;
    const {
      form: { getFieldDecorator },
    } = this.props;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div>
        <Card
          style={{
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
            marginRight: 15,
            marginLeft: 15,
          }}
        >
          <div style={{ borderBottom: '1px solid rgb(236, 236, 236)', marginTop: '-20px' }}>
            <h3>{this.$t('fund.the.detail.information')}:</h3>
            {/*明细信息*/}
          </div>
          <Row style={{ marginTop: '15px' }}>
            <Col span={7}>
              {this.$t('fund.payment.order.no1')}
              {paymentBatchNumber || ''}
            </Col>
            {/*付款单号：*/}
            <Col span={6}>
              {this.$t('fund.terms.of.payment:')}
              {paymentMethodDesc || ''}
            </Col>
            {/*付款方式：*/}
            <Col span={6}>
              {this.$t('fund.payment.companies:')}
              {paymentCompanyName || ''}
            </Col>
            {/*付款公司：*/}
            <Col span={5}>
              {this.$t('fund.date.of.documents:')}
              {billDateDesc || ''}
            </Col>
            {/*单据日期：*/}
          </Row>
          <Row style={{ marginTop: '15px' }}>
            <Col span={7}>
              {this.$t('fund.payment.account:')}
              {paymentAccount || ''}
            </Col>
            {/*付款账号：*/}
            <Col span={6}>
              {this.$t('fund.paying.bank:')}
              {bankCodeName || ''}
            </Col>
            {/*付款银行：*/}
            <Col span={6}>
              {this.$t('fund.payment.account:')}
              {paymentAccountName || ''}
            </Col>
            {/*付款账户：*/}
            <Col span={5}>
              {this.$t('fund.currency:')}
              {currencyCode || ''}
            </Col>
            {/*币种：*/}
          </Row>
          <Row style={{ marginTop: '15px' }}>
            <Col span={5}>
              {this.$t('fund.description:')}
              {description || ''}
            </Col>
            {/*描述：*/}
          </Row>
        </Card>
        <div style={{ marginTop: '30px' }}>
          {/* <h3>跟踪单据：</h3> */}
          <div style={{ marginBottom: '10px' }}>
            <Row>
              <Col span={10}>
                <Button
                  style={{ marginRight: '8px', display: showOrHideButton }}
                  type="primary"
                  disabled={selectedRow.length === 0}
                  onClick={this.confirmButton}
                >
                  {this.$t('fund.save')}
                  {/*保存*/}
                </Button>
                <Button
                  style={{ marginRight: '8px', display: showOrHideButton }}
                  disabled={selectedRow.length === 0}
                  type="primary"
                  onClick={this.bulkChanges}
                >
                  {this.$t('fund.bulk.changes')}
                  {/*批量修改*/}
                </Button>
              </Col>
            </Row>
          </div>
          {noticeAlert ? (
            <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
          ) : (
            ''
          )}
          <Table
            rowKey={record => record.id}
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            loading={loading}
            onChange={this.onChangePager}
            rowSelection={rowSelection}
            bordered
            size="middle"
            scroll={{ x: 1500 }}
          />
          <Modal
            title={this.$t('fund.bulk.changes')} /*批量修改*/
            visible={showModal}
            onOk={this.handleOk}
            onCancel={this.handleCancel}
          >
            <Form>
              <Row style={{ marginTop: '20px' }}>
                <Col span={4}>
                  <span>{this.$t('fund.payment.condition:')}</span>
                  {/*支付状态：*/}
                </Col>
                <Col span={20}>
                  <Form.Item>
                    {getFieldDecorator('payStatus', {
                      rules: [{ required: true }],
                      initialValue: '',
                    })(
                      <Select
                        style={{ width: '60%' }}
                        placeholder={this.$t('fund.please.choose')} /*请选择*/
                        labelInValue
                        onChange={this.getPayOptions}
                      >
                        {payList.length > 0 &&
                          payList.map(item => {
                            return <Option key={item.value}>{item.name}</Option>;
                          })}
                      </Select>
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row>
                <Col span={4}>
                  <span>{this.$t('fund.date.of.payment:')}</span>
                  {/*支付日期：*/}
                </Col>
                <Col span={20}>
                  <Form.Item>
                    {getFieldDecorator('payTime', {
                      rules: [{ required: true }],
                      initialValue: moment(new Date()),
                    })(<DatePicker />)}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Modal>
          <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleBack}>
            <Icon type="rollback" style={{ marginRight: '5px', marginBottom: '15px' }} />
            {this.$t('fund.back')}
            {/*返回*/}
          </a>
        </div>
      </div>
    );
  }
}

const wrappedCompanyDistribution = Form.create()(ManualQuery);

export default connect()(wrappedCompanyDistribution);
