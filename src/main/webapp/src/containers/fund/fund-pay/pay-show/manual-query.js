import React from 'react';
import {
  Row,
  Form,
  Col,
  Alert,
  Card,
  Select,
  Modal,
  Table,
  Icon,
  DatePicker,
  message,
  Button,
} from 'antd';
import { connect } from 'dva';
import moment from 'moment';
import { routerRedux } from 'dva/router';
import PayShowService from './pay-show.service';
import { accAdd, objectEquals } from '../../fund-components/utils';

const { Option } = Select;
const selectWidth = { width: '100%' };

class ManualQuery extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      // goBack: '',
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
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      columns: [
        {
          title: '预警状态',
          dataIndex: 'warningStatusDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '预警信息',
          dataIndex: 'warningData',
          width: 100,
          align: 'center',
        },
        {
          title: '来源单据号',
          dataIndex: 'tradeCode',
          width: 100,
          align: 'center',
        },
        {
          title: '收款户名',
          dataIndex: 'gatherAccountName',
          width: 100,
          align: 'center',
        },
        {
          title: '收款分行',
          dataIndex: 'gatherBranchBankName',
          width: 100,
          align: 'center',
        },
        {
          title: '收款账号',
          dataIndex: 'gatherAccount',
          width: 100,
          align: 'center',
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: 100,
          align: 'center',
        },
        {
          title: '付款用途',
          dataIndex: 'paymentPurposeDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '摘要',
          dataIndex: 'description',
          width: 100,
          align: 'center',
        },
        {
          title: '公私标志',
          dataIndex: 'propFlagDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '卡折标志',
          dataIndex: 'cardSignDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '支付状态',
          dataIndex: 'paymentStatusDesc',
          width: 130,
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
            // record -----> 当前框的值，如支付状态
            // index  -----> 当前行的整个对象
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
            return match.params.status === 'BEING' && paymentMethodDesc !== '银企直连' ? (
              <Select
                placeholder="请选择"
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
              <span>{record || ''}</span>
            );
          },
        },
        {
          title: '支付日期',
          dataIndex: 'paymentDate',
          width: 170,
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
          title: '银行反馈信息',
          dataIndex: 'bankFeedback',
          width: 100,
          align: 'center',
        },
        {
          title: '回单',
          dataIndex: 'receiptNum',
          width: 100,
          align: 'center',
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
        已选择
        <span style={{ fontWeight: 'bold', color: '#108EE9' }}> {rows.length} </span> 项 |共
        <span style={{ fontWeight: 'bold', color: '#108EE9' }}>{totalAmount}</span>元
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

    PayShowService.updateSave(selectedRow)
      .then(res => {
        if (res.data === 'SUCCESS') {
          message.success('保存成功');
          dispatch(
            routerRedux.push({
              pathname: '/fund-pay/pay-show/pay-show/Success',
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
      message.error('请选中要修改的单据！！');
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
            <h3>明细信息:</h3>
          </div>
          <Row style={{ marginTop: '15px' }}>
            <Col span={7}>付款单号：{paymentBatchNumber || ''}</Col>
            <Col span={6}>付款方式：{paymentMethodDesc || ''}</Col>
            <Col span={6}>付款公司：{paymentCompanyName || ''}</Col>
            <Col span={5}>单据日期：{billDateDesc || ''}</Col>
          </Row>
          <Row style={{ marginTop: '15px' }}>
            <Col span={7}>付款账号：{paymentAccount || ''}</Col>
            <Col span={6}>付款银行：{bankCodeName || ''}</Col>
            <Col span={6}>付款账户：{paymentAccountName || ''}</Col>
            <Col span={5}>币种：{currencyCode || ''}</Col>
          </Row>
          <Row style={{ marginTop: '15px' }}>
            <Col span={5}>描述：{description || ''}</Col>
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
                  保存
                </Button>
                <Button
                  style={{ marginRight: '8px', display: showOrHideButton }}
                  disabled={selectedRow.length === 0}
                  type="primary"
                  onClick={this.bulkChanges}
                >
                  批量修改
                </Button>
                {/* <Button type="primary" onClick={this.sendUnlinePay}>
                  跟踪单据
                </Button> */}
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
            title="批量修改"
            visible={showModal}
            onOk={this.handleOk}
            onCancel={this.handleCancel}
          >
            <Form>
              <Row style={{ marginTop: '20px' }}>
                <Col span={4}>
                  <span>支付状态：</span>
                </Col>
                <Col span={20}>
                  <Form.Item>
                    {getFieldDecorator('payStatus', {
                      rules: [{ required: true }],
                      initialValue: '',
                    })(
                      <Select
                        style={{ width: '60%' }}
                        placeholder="请选择"
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
                  <span>支付日期：</span>
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
            <Icon type="rollback" style={{ marginRight: '5px', marginBottom: '15px' }} />返回
          </a>
        </div>
      </div>
    );
  }
}

const wrappedCompanyDistribution = Form.create()(ManualQuery);

export default connect()(wrappedCompanyDistribution);
