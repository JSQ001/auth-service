import React from 'react';
import { Form, Row, Col, Button, Select, message } from 'antd';
import { connect } from 'dva';
import SlideFrame from 'widget/slide-frame';
import Chooser from 'widget/chooser';
import Lov from 'widget/Template/lov';
import Table from 'widget/table';
import AutoCashierReconciliation from './auto-reconciliation';

import cashierService from './cashier.service';

const FormItem = Form.Item;
const { Option } = Select;
class CashierReconciliation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      slideVisible: false, // 侧滑框
      periodList: [],
      bankDebitAmount: 0, // 借方金额合计
      bankCreditAmount: 0, // 贷方金额合计
      oracleDebitAmount: 0,
      oracleCreditAmount: 0,
      selectedRowKeys1: [],
      selectedRowKeys: [],
      selectedRow: [],
      selectedRow1: [],
      accountId: '',
      periodName: '',
      // loading: true,
      // loading1: true,
      pagination: {
        total: 0, // 数据总数
        page: 0, // 用于计算页数
        pageSize: 10, // 每页条数
        current: 1, // 当前页数
        showSizeChanger: true, // 是否可以改变 pageSize
        showQuickJumper: true, // 是否可以快速跳转至某页
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`, // 用于显示数据总量和当前数据顺序
      },
      pagination1: {
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
          title: '回单编号',
          dataIndex: 'returnNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '摘要',
          dataIndex: 'summary',
          width: '10%',
          align: 'center',
        },
        {
          title: '对方户名',
          dataIndex: 'otherAccountName',
          width: '10%',
          align: 'center',
        },
        {
          title: '借方金额',
          dataIndex: 'debitAmount',
          width: '10%',
          align: 'center',
        },
        {
          title: '贷方金额',
          dataIndex: 'creditAmount',
          width: '10%',
          align: 'center',
        },
        {
          title: '金额',
          dataIndex: 'sinceAmount',
          width: '10%',
          align: 'center',
        },
        {
          title: '银行流水号',
          dataIndex: 'bankSn',
          width: '10%',
          align: 'center',
        },
      ],
      columns1: [
        {
          title: '来源',
          dataIndex: 'source',
          width: '10%',
          align: 'center',
        },
        {
          title: '凭证编号',
          dataIndex: 'voucherNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '凭证日期',
          dataIndex: 'voucherDate',
          width: '10%',
          align: 'center',
        },
        {
          title: '对方名称',
          dataIndex: 'bankAccountName',
          width: '10%',
          align: 'center',
        },
        {
          title: '借方金额',
          dataIndex: 'debitAmount',
          width: '10%',
          align: 'center',
        },
        {
          title: '贷方金额',
          dataIndex: 'creditAmount',
          width: '10%',
          align: 'center',
        },
        {
          title: '银行流水号',
          dataIndex: 'groupId',
          width: '10%',
          align: 'center',
        },
      ],
    };
  }

  componentWillMount() {
    this.getPeriod();
  }

  /**
   * 获取期间数据
   */
  getPeriod = () => {
    cashierService.getPeriod().then(res => {
      this.setState({
        periodList: res.data,
      });
    });
  };

  /**
   * 获取银行流水信息
   */
  getListBank = searchParams => {
    const { pagination } = this.state;
    this.setState({ loading: true, loading1: true });
    const { user } = this.props;
    cashierService
      .getListBank(pagination.page, pagination.pageSize, searchParams, user.id)
      .then(res => {
        let bankDebitAmount = 0;
        let bankCreditAmount = 0;
        res.data.forEach(element => {
          bankDebitAmount += element.debitAmount;
          bankCreditAmount += element.creditAmount;
        });
        this.setState({
          bankDebitAmount, // 借方金额合计
          bankCreditAmount, // 贷方金额合计
          tableBank: res.data,
          loading: false,
          loading1: false,
          pagination: {
            ...pagination,
            total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
            onChange: this.onChangePager,
            current: pagination.page + 1,
          },
        });
      });
  };

  /**
   * 获取oracle银行日记账信息
   */
  getListOracle = searchParams => {
    const { pagination1 } = this.state;
    const { user } = this.props;
    this.setState({ loading1: true, loading: true });
    cashierService
      .getListOracle(pagination1.page, pagination1.pageSize, searchParams, user.id)
      .then(res => {
        let oracleDebitAmount = 0;
        let oracleCreditAmount = 0;
        res.data.forEach(element => {
          oracleDebitAmount += element.debitAmount;
          oracleCreditAmount += element.creditAmount;
        });
        this.setState({
          oracleDebitAmount,
          oracleCreditAmount,
          tableOracle: res.data,
          loading1: false,
          loading: false,
          pagination1: {
            ...pagination1,
            total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
            onChange: this.onChangePager,
            current: pagination1.page + 1,
          },
        });
      });
  };

  /**
   * 搜索
   */
  handleSearch = e => {
    e.preventDefault();
    const {
      form: { getFieldsValue },
    } = this.props;
    const params = getFieldsValue();
    const searchParams = {
      accountId: params.bankAccount.id,
      periodName: params.period.label,
      balanceFlag: params.reconciliationState.key,
    };
    this.setState({
      periodName: params.period.label,
      accountId: '1108254160813469697',
      searchParams,
    });
    this.getListBank(searchParams);
    this.getListOracle(searchParams);
  };

  /**
   * 撤销对账
   */
  revertReconciliation = () => {
    const { selectedRow, selectedRow1, searchParams } = this.state;
    if (selectedRow === [] || selectedRow1 === []) {
      message.error('请选择单据');
    } else if (selectedRow.length !== selectedRow1.length) {
      message.error('请选择对应单据');
    } else {
      const arr = [];
      selectedRow.forEach(element => {
        arr.push(element.groupId);
      });
      selectedRow1.forEach(element => {
        arr.push(element.groupId);
      });
      cashierService.revertReconciliation(arr).then(res => {
        if (res.status === 200) {
          message.success('撤销成功');
          this.getListBank(searchParams);
          this.getListOracle(searchParams);
          this.setState({
            selectedRow: [],
            selectedRow1: [],
          });
        }
      });
    }
  };

  /**
   * 生成余额调节表
   */
  generateBankReconciliation = () => {
    const { accountId, periodName } = this.state;
    if (accountId === '' || periodName === '') {
      message.error('请先查询单据');
    } else {
      cashierService
        .generateBankReconciliation(accountId, periodName)
        .then(res => {
          if (res.status === 200) {
            message.success('余额调节表已生成');
          }
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  /**
   * 自动对账
   */
  autoReconciliation = () => {
    const { accountId, periodName, searchParams } = this.state;
    if (accountId === '' || periodName === '') {
      message.error('请先查询单据');
    } else {
      cashierService
        .autoReconciliation(accountId, periodName)
        .then(res => {
          if (res.status === 200) {
            message.success('自动对账成功');
            this.getListBank(searchParams);
            this.getListOracle(searchParams);
          }
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  /**
   * 手工调节
   */
  manualReconciliation = () => {
    const { selectedRowKeys1, selectedRowKeys, periodName, searchParams } = this.state;
    if (selectedRowKeys.length < 1 || selectedRowKeys1.length < 1) {
      message.error('未选择单据');
    } else {
      const obj = {
        periodName,
        bankList: selectedRowKeys,
        dailyList: selectedRowKeys1,
      };
      cashierService.manualReconciliation(obj).then(res => {
        if (res.status === 200) {
          message.success('操作成功');
          this.getListBank(searchParams);
          this.getListOracle(searchParams);
          this.setState({
            selectedRowKeys1: [],
            selectedRowKeys: [],
          });
        }
      });
    }
  };

  /**
   * bank数据分页点击
   */
  onChangePagerBank = pagination => {
    const { searchParams } = this.state;
    const temp = {};
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getListBank(searchParams);
      }
    );
  };

  /**
   * oracle数据分页点击
   */
  onChangePager = pagination1 => {
    const temp = {};
    const { searchParams } = this.state;
    temp.page = pagination1.current - 1;
    temp.current = pagination1.current;
    temp.pageSize = pagination1.pageSize;
    this.setState(
      {
        pagination1: temp,
      },
      () => {
        this.getListOracle(searchParams);
      }
    );
  };

  /**
   * 搜索条件重置
   */
  searchClear = e => {
    const { form } = this.props;
    e.preventDefault();
    form.resetFields();
  };

  // handleChange = value => {
  //   console.log(value);
  // };

  /**
   * 打印
   */
  print = () => {
    window.document.body.innerHTML = window.document.getElementById('print').innerHTML;
    window.print();
    window.location.reload();
  };

  /**
   * 行选择
   */
  onSelectChangeBank = (selectedRowKeys, selectedRow) => {
    this.setState(
      {
        selectedRowKeys,
        selectedRow,
        // batchDelete: !(selectedRowKeys.length > 0),
      }
      // () => {
      //   if (selectedRowKeys.length > 0) {
      //     this.noticeAlert(selectedRow);
      //   }
      //   //  else {
      //   //   this.setState({
      //   //     noticeAlert: null,
      //   //   });
      //   // }
      // }
    );
  };

  /**
   * 行选择
   */
  onSelectChange = (selectedRowKeys1, selectedRow1) => {
    this.setState(
      {
        selectedRowKeys1,
        selectedRow1,
        // batchDelete: !(selectedRowKeys.length > 0),
      }
      // () => {
      //   if (selectedRowKeys1.length > 0) {
      //     this.noticeAlert(selectedRow1);
      //   }
      //    else {
      //     this.setState({
      //       noticeAlert: null,
      //     });
      //   }
      // }
    );
  };

  render() {
    const {
      columns,
      columns1,
      pagination,
      pagination1,
      tableBank,
      tableOracle,
      loading,
      loading1,
      selectedRowKeys,
      selectedRow,
      selectedRowKeys1,
      selectedRow1,
      slideVisible,
      periodList,
      bankDebitAmount,
      bankCreditAmount,
      oracleDebitAmount,
      oracleCreditAmount,
    } = this.state;
    const {
      form: { getFieldDecorator },
      company,
    } = this.props;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChangeBank,
    };
    const rowSelection1 = {
      selectedRowKeys1,
      selectedRow1,
      onChange: this.onSelectChange,
    };
    return (
      <div className="train">
        <div style={{ background: '#FAFAFA', padding: '15px' }}>
          <Form style={{ marginTop: '-10px' }} onSubmit={this.handleSearch}>
            <Row>
              <Col span={5} offset={1}>
                <FormItem label="公司" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('paymentCompanyName', {
                    initialValue: [{ id: company.id, name: company.name }],
                  })(
                    <Chooser
                      type="company"
                      labelKey="name"
                      valueKey="id"
                      single
                      disabled
                      showClear={false}
                      listExtraParams={{ setOfBooksId: company.setOfBooksId }}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="银行账号">
                  {getFieldDecorator('bankAccount', {
                    // rules: [{ required: true }],
                    // initialValue: { id: editModel.id, accountNumber: editModel.bankAccount },
                  })(
                    <Lov
                      code="bankaccount_choose"
                      valueKey="id"
                      labelKey="accountNumber"
                      single
                      onChange={this.onChange}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="期间" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('period', {
                    // initialValue: moment(billDate) || '',
                  })(
                    <Select labelInValue placeholder="请选择">
                      {periodList.map(option => {
                        return <Option key={option.id}>{option.periodName}</Option>;
                      })}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="对账状态" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('reconciliationState', {
                    initialValue: { key: 'false', label: '未对账' },
                  })(
                    <Select
                      labelInValue
                      style={{ width: 120 }}
                      onChange={this.handleChange}
                      allowClear
                    >
                      <Option key="false">未对账</Option>
                      <Option key="true">已对账</Option>
                    </Select>
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={4} offset={21}>
                <Button type="primary" onClick={this.handleSearch}>
                  搜索
                </Button>&nbsp;&nbsp;&nbsp;
                <Button onClick={this.searchClear}>清空</Button>
              </Col>
            </Row>
          </Form>
        </div>
        <div>
          <Row style={{ marginTop: '20px' }}>
            <Button type="primary" htmlType="submit" onClick={this.autoReconciliation}>
              自动对账
            </Button>
            <Button
              type="primary"
              htmlType="submit"
              style={{ marginLeft: 14 }}
              onClick={this.manualReconciliation}
            >
              人工调节
            </Button>
            <Button
              type="primary"
              htmlType="submit"
              style={{ marginLeft: 14 }}
              onClick={this.revertReconciliation}
            >
              撤销对账
            </Button>
            <Button
              type="primary"
              htmlType="submit"
              style={{ marginLeft: 14 }}
              onClick={this.generateBankReconciliation}
            >
              生成调节表
            </Button>
          </Row>
          <SlideFrame
            title="自动对账条件选择"
            show={slideVisible}
            onClose={() => this.handleClose()}
          >
            <AutoCashierReconciliation onClose={this.handleClose} />
          </SlideFrame>
          <Row style={{ marginTop: '20px' }} id="print">
            <Col span={11}>
              <Table
                rowKey={record => record.id}
                columns={columns}
                dataSource={tableBank}
                pagination={pagination}
                rowSelection={rowSelection}
                loading={loading}
                onChange={this.onChangePagerBank}
                bordered
                scroll={{ x: 800 }}
                size="middle"
              />
              <Row>
                <Col span={6}>
                  <span>贷方合计：{bankDebitAmount}</span>
                </Col>
                <Col span={6} offset={1}>
                  <span>借方合计：{bankCreditAmount}</span>
                </Col>
              </Row>
            </Col>
            <Col span={11} offset={1} style={{ paddingBottom: '20px' }}>
              <Table
                rowKey={record => record.id}
                columns={columns1}
                dataSource={tableOracle}
                pagination={pagination1}
                rowSelection={rowSelection1}
                loading={loading1}
                onChange={this.onChangePager}
                bordered
                scroll={{ x: 800 }}
                size="middle"
              />
              <Row>
                <Col span={6}>
                  <span>贷方合计：{oracleDebitAmount}</span>
                </Col>
                <Col span={6} offset={1}>
                  <span>借方合计：{oracleCreditAmount}</span>
                </Col>
              </Row>
            </Col>
          </Row>
        </div>
      </div>
    );
  }
}

function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
export default connect(map)(Form.create()(CashierReconciliation));
