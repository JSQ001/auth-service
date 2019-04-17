import React from 'react';
import { Form, Row, Col, Input, Select, DatePicker, Alert, Button, message } from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import Chooser from 'widget/chooser';
import Table from 'widget/table';
import 'styles/fund/account.scss';
import moment from 'moment';
import paymentWorkbenchService from './payment-workbench-service';

const FormItem = Form.Item;
const { Option } = Select;
class PaymentWorkbenchEdit extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      formItemLayout: {
        labelCol: {
          span: 8,
        },
        wrapperCol: {
          span: 16,
        },
      },
      descrptionFormItemLayout: {
        labelCol: {
          span: 4,
        },
        wrapperCol: {
          span: 20,
        },
      },
      noticeAlert: null, // 提示信息
      loading: false, // loading状态
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
      buttonsDisableClick: true, // 基本按钮是否可点击
      tableData: [], // table数据
      paymentMethods: [], // 付款方式集合
      paymentMethod: '', // 当前付款方式
      paymentAccountList: [],
      deleteAllDisableClick: true, // 删除整单是否可点击
      submitDisableClick: true, // 提交按钮是否可点击
      headerId: '', // 第二次及以后保存需要的headerid
      batchNumber: '', // 付款批号
      paymentBank: '', // 付款银行
      paymentAccountName: '', // 付款账户
      currency: '', // 币种
      bankCode: '', // 付款账号银行code,
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
          width: 150,
          align: 'center',
        },
        {
          title: '来源单据编号',
          dataIndex: 'sourceDocumentNum',
          width: 150,
          align: 'center',
        },
        {
          title: '付款用途',
          dataIndex: 'paymentAccountName',
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
          width: 150,
          align: 'center',
        },
        {
          title: '收款账号',
          dataIndex: 'gatherAccount',
          width: 200,
          align: 'center',
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: 100,
          align: 'center',
        },
        {
          title: '摘要',
          dataIndex: 'summary',
          width: 200,
          align: 'center',
        },
        {
          title: '公私标志',
          dataIndex: 'propFlagDesc',
          width: 80,
          align: 'center',
        },
        {
          title: '是否付款',
          dataIndex: 'isPay',
          width: 80,
          align: 'center',
        },
      ],
    };
  }

  componentDidMount() {
    this.getCreateBatchList();
  }

  /**
   * 获取值列表
   * @param code :值列表代码
   * @param name :值列表名称
   */
  getValueList = (code, name) => {
    name.splice(0, name.length);
    this.getSystemValueList(code).then(response => {
      response.data.values.map(item => {
        const option = {
          key: item.value,
          id: item.value,
          value: item.name,
        };
        name.addIfNotExist(option);
        return option;
      });
      /* eslint-disable */
      this.setState({
        name,
      });
      /* eslint-disable */
    });
  };

  /**
   * 获取付款账户列表
   */
  getPaymentAccountList = () => {
    const {
      form: { getFieldValue },
    } = this.props;
    const { paymentMethod } = this.state;
    const paymentCompany = getFieldValue('paymentCompany');
    let params = {
      flag: 'PAY',
      companyId: paymentCompany ? paymentCompany[0].id : '',
      directFlag: paymentMethod === 'INTERFACE' ? true : null,
    };
    if (paymentCompany) {
      paymentWorkbenchService
        .accountInformationList(0, 100, params)
        .then(response => {
          this.setState({
            paymentAccountList: response.data,
          });
        })
        .catch(error => {
          message.error(error.response.data.message);
        });
    }
  };

  /**
   * 选取付款账号自动带出银行，付款账户，币种
   */
  setAcountInfo = value => {
    const { paymentAccountList } = this.state;
    const chooseAccount = paymentAccountList.filter(item => {
      return item.accountNumber === value;
    });
    console.log(chooseAccount);
    this.setState({
      paymentBank: chooseAccount[0].openBankName,
      paymentAccountName: chooseAccount[0].accountName,
      currency: chooseAccount[0].currencyCode,
      bankCode: chooseAccount[0].openBank,
    });
  };

  /**
   * 重新选择公司后重置账号相关信息
   */
  resetCompay = () => {
    const {
      form: { resetFields },
    } = this.props;
    resetFields('paymentAccount', []);
    this.setState({
      paymentAccountList: [],
      paymentBank: '',
      paymentAccountName: '',
      currency: '',
      bankCode: '',
    });
  };

  /**
   * 获取列表数据
   */
  getCreateBatchList = () => {
    const { pagination } = this.state;
    this.setState({ loading: true, noticeAlert: null, selectedRowKeys: [] });
    paymentWorkbenchService
      .getCreateBatchList()
      .then(response => {
        this.setState({
          tableData: response.data.paymentInInterfaceDTOS,
          loading: false,
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            current: pagination.page + 1,
            pageSize: pagination.pageSize,
            onChange: this.onChangePaper,
            onShowSizeChange: this.onShowSizeChange,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              this.$t('common.show.total', { range0: `${range[0]}`, range1: `${range[1]}`, total }),
          },
        });
      })
      .catch(error => {
        message.error(error.response.data.message);
      });
  };

  /**
   * 提示框显示
   */
  noticeAlert = rows => {
    const initialAmount = 0;
    const totalAmount = rows.reduce(
      (accumulator, currentValue) => accumulator + currentValue.amount,
      initialAmount
    );
    const noticeAlert = (
      <span>
        已选择<span style={{ fontWeight: 'bold', color: '#108EE9' }}> {rows.length} </span> 项 |共<span
          style={{ fontWeight: 'bold', color: '#108EE9' }}
        >
          {totalAmount}
        </span>元
      </span>
    );
    this.setState({
      noticeAlert: rows.length ? noticeAlert : null,
    });
  };

  /**
   * 行选择
   */
  onSelectChange = (selectedRowKeys, selectedRow) => {
    this.setState(
      {
        selectedRowKeys,
        selectedRow,
        buttonsDisableClick: !(selectedRowKeys.length > 0),
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
   * 删除行
   */
  generateBatchDelete = () => {
    const { selectedRowKeys } = this.state;
    paymentWorkbenchService
      .generateBatchDelete(selectedRowKeys)
      .then(response => {
        if (response.data === true) {
          message.success('删除成功');
          this.getCreateBatchList();
        }
      })
      .catch(error => {
        message.error(error.response.data.message);
      });
  };

  /**
   * 保存
   */
  generateBatchSave = () => {
    const {
      form: { validateFields },
    } = this.props;
    const { headerId, bankCode, paymentAccountName } = this.state;
    validateFields((err, values) => {
      if (!err) {
        const param = {
          headerId: headerId,
          hfmPaymentMethod: values.paymentMethod,
          hfmPaymentCompany: values.paymentCompany[0].companyCode,
          paymentAccount: values.paymentAccount,
          currency: values.currency,
          description: values.description,
          billDate: moment(values.documentDate),
          bankCode: bankCode,
          paymentAccountName: paymentAccountName,
        };
        this.setState({
          loading: true,
        });
        paymentWorkbenchService
          .generateBatchSave(param)
          .then(response => {
            this.setState({
              headerId: response.data.headerId,
              batchNumber: response.data.batchNumber,
            });
            message.success('保存成功');
            this.setState({
              loading: false,
              deleteAllDisableClick: false,
              submitDisableClick: false,
            });
          })
          .catch(error => {
            message.error(error.response.data.message);
          });
      }
    });
  };

  /**
   * 整单删除
   */
  generateBatchDeleteAll = () => {
    const { dispatch } = this.props;
    paymentWorkbenchService
      .generateBatchDeleteAll()
      .then(response => {
        if (response.data === true) {
          message.success('删除成功');
          dispatch(
            routerRedux.push({
              pathname: `/payment-management/payment-workbench/payment-workbench`,
            })
          );
        }
      })
      .catch(error => {
        message.error(error.response.data.message);
      });
  };

  /**
   * 提交
   */
  submit = () => {
    const { dispatch } = this.props;
    const { headerId } = this.state;
    paymentWorkbenchService.generateSubmit(headerId).then(response => {
      if (response.status === 200) {
        message.success('提交成功');
      }
      dispatch(
        routerRedux.push({
          pathname: `/payment-management/payment-workbench/payment-workbench`,
        })
      );
    });
  };

  /**
   * 返回
   */
  goback = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/payment-management/payment-workbench/payment-workbench`,
      })
    );
  };

  render() {
    const {
      form: { getFieldDecorator },
      company,
    } = this.props;
    const {
      formItemLayout,
      descrptionFormItemLayout,
      paymentMethods,
      paymentAccountList,
      loading,
      pagination,
      noticeAlert,
      columns,
      tableData,
      buttonsDisableClick,
      submitDisableClick,
      deleteAllDisableClick,
      selectedRowKeys,
      selectedRow,
      batchNumber,
      paymentBank,
      paymentAccountName,
      currency,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div className="train">
        <div className="common-top-area">
          <Form>
            <Row>
              <Col span={6}>
                <FormItem label="付款批号" {...formItemLayout}>
                  {getFieldDecorator('batchNumber', {
                    initialValue: batchNumber,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="付款方式" {...formItemLayout}>
                  {getFieldDecorator('paymentMethod', {
                    initialValue: '',
                    rules: [{ required: true, message: '付款方式不能为空' }],
                  })(
                    <Select
                      onFocus={() => this.getValueList('ZJ_PAYMENT_TYPE', paymentMethods)}
                      placeholder="请选择"
                      allowClear
                      onSelect={value => {
                        this.setState({
                          paymentMethod: value,
                        });
                      }}
                    >
                      {paymentMethods.map(item => {
                        return <Option key={item.id}>{item.value}</Option>;
                      })}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="付款公司" {...formItemLayout}>
                  {getFieldDecorator('paymentCompany', {
                    initialValue: '',
                    rules: [{ required: true, message: '付款公司不能为空' }],
                  })(
                    <Chooser
                      type="company"
                      labelKey="name"
                      valueKey="id"
                      // showClear={false}
                      allowClear
                      single
                      listExtraParams={{ setOfBooksId: company.setOfBooksId }}
                      onChange={this.resetCompay}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="单据日期" {...formItemLayout}>
                  {getFieldDecorator('documentDate', {
                    initialValue: moment(new Date()),
                    rules: [{ required: true, message: '单据日期不能为空' }],
                  })(<DatePicker format="YYYY-MM-DD" />)}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={6}>
                <FormItem label="付款账号" {...formItemLayout}>
                  {getFieldDecorator('paymentAccount', {
                    initialValue: '',
                    rules: [{ required: true, message: '付款账号不能为空' }],
                  })(
                    <Select
                      onFocus={() => this.getPaymentAccountList()}
                      onSelect={this.setAcountInfo}
                      placeholder="请选择"
                      allowClear
                    >
                      {paymentAccountList.map(item => {
                        return (
                          <Option key={item.accountNumber} value={item.accountNumber}>
                            {item.accountNumber}
                          </Option>
                        );
                      })}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="付款银行" {...formItemLayout}>
                  {getFieldDecorator('paymentBank', {
                    initialValue: paymentBank,
                    rules: [{ required: true, message: '付款银行不能为空' }],
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="付款账户" {...formItemLayout}>
                  {getFieldDecorator('paymentAccountName', {
                    initialValue: paymentAccountName,
                    rules: [{ required: true, message: '付款账户不能为空' }],
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="币种" {...formItemLayout}>
                  {getFieldDecorator('currency', {
                    initialValue: currency,
                    rules: [{ required: true, message: '币种不能为空' }],
                  })(<Input disabled />)}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={12}>
                <FormItem label="单据描述" {...descrptionFormItemLayout}>
                  {getFieldDecorator('description', {
                    initialValue: '',
                  })(<Input allowClear />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
        </div>
        <div className="table-header">
          <div className="table-header-buttons">
            <Button
              type="danger"
              disabled={buttonsDisableClick}
              onClick={e => {
                e.preventDefault();
                e.stopPropagation();
                this.generateBatchDelete();
              }}
            >
              删除行
            </Button>
            <Button
              type="primary"
              onClick={e => {
                e.preventDefault();
                e.stopPropagation();
                this.generateBatchSave();
              }}
            >
              保存
            </Button>
            <Button
              type="danger"
              disabled={deleteAllDisableClick}
              onClick={e => {
                e.preventDefault();
                e.stopPropagation();
                this.generateBatchDeleteAll();
              }}
            >
              整单删除
            </Button>
            <Button
              type="primary"
              disabled={submitDisableClick}
              onClick={e => {
                e.preventDefault();
                e.stopPropagation();
                this.submit();
              }}
            >
              提交
            </Button>
            <Button
              type="primary"
              onClick={e => {
                e.preventDefault();
                e.stopPropagation();
                this.goback();
              }}
            >
              返回
            </Button>
          </div>
          {noticeAlert ? (
            <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
          ) : (
            ''
          )}
          <Table
            rowKey={record => record.id}
            dataSource={tableData}
            loading={loading}
            columns={columns}
            pagination={pagination}
            scroll={{ x: 1500 }}
            rowSelection={rowSelection}
          />
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

export default connect(mapStateToProps)(Form.create()(PaymentWorkbenchEdit));
