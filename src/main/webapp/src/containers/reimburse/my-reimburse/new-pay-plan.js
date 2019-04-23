import React from 'react';

import { Form, Button, Input, Row, Col, Select, DatePicker, message, Alert, Switch } from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
const { TextArea } = Input;

import ListSelector from 'widget/list-selector';
import moment from 'moment';
import CustomAmount from 'widget/custom-amount';
import reimburseService from 'containers/reimburse/my-reimburse/reimburse.service';
import SelectContract from 'containers/reimburse/my-reimburse/select-contract-line';
import SelectReceivables from 'widget/select-receivables';
import { connect } from 'dva';
import Chooser from '../../../components/Widget/chooser';

class NewPayPlan extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      currency: null,
      banksInfo: [],
      contractCategoryValue: '',
      partnerCategoryOptions: [], //合同方类型选项
      venderOptions: [], //供应商选项,
      companyId: '',
      partnerId: '',
      partnerName: '',
      receivables: [],
      reTypeDetail: {},
      value: '',
      accountList: [],
      payWayTypeList: [],
      bankLocationCode: '',
      bankLocationName: '',
      flag: false,
      payeeId: '',
      payeeName: '',
      showSelectContract: false,
      contractParams: {},
      contractInfo: {},
      saveLoading: false,
      headerData: {},
      cashTransactionClassList: [],
      record: props.params.record || {},
      isNew: true,
      fetching: false,
      selectedData: [],
      showPayee: false,
    };
  }
  componentDidMount() {
    //编辑
    let record = this.props.params.record;
    if (record.id) {
      this.setState(
        {
          record,
          headerData: this.props.params.headerData,
          payeeId: record.payeeId,
          payeeName: record.partnerName,
          value: record.partnerName,
          bankLocationCode: record.bankLocationCode,
          bankLocationName: record.bankLocationName,
          selectedData: record.contractHeaderLineMessage
            ? [record.contractHeaderLineMessage.lineId]
            : [],
          contractInfo: record.contractHeaderLineMessage
            ? {
                contractId: record.contractHeaderLineMessage.headerId,
                contractLineId: record.contractHeaderLineMessage.lineId,
                lineNumber: record.contractHeaderLineMessage.lineNumber,
                contractLineAmount: record.contractHeaderLineMessage.contractAmount,
                dueDate: record.contractHeaderLineMessage.dueDate,
                contractNumber: record.contractHeaderLineMessage.contractNumber,
              }
            : {},
        },
        () => {
          this.props.form.setFieldsValue({
            paymentScheduleDate: moment(record.schedulePaymentDate),
          });
          this.getReceivables(record.payeeId, record.payeeCategory);
          this.queryCashTransactionClassForForm();
        }
      );
    } else {
      this.setState({ isNew: true, headerData: this.props.params.headerData }, () => {
        const { headerData } = this.state;
        if (headerData.multipleReceivables === false) {
          this.setState({
            payeeId: headerData.partnerId,
            payeeName: headerData.partnerName,
            contractHeaderId: headerData.contractHeaderId,
            contractInfo: headerData.contractHeaderId
              ? {
                  contractId: headerData.contractHeaderId,
                  contractLineId: headerData.contractHeaderLineDTO.lineId,
                  lineNumber: headerData.contractHeaderLineDTO.lineNumber,
                  contractLineAmount: headerData.contractLineAmount,
                  dueDate: headerData.contractHeaderLineDTO.dueDate,
                  contractNumber: headerData.contractNumber,
                }
              : {},
          });
        }
        this.queryCashTransactionClassForForm();
      });
    }
    this.getPayWayTypeList();
    this.getReTypeDetail();
  }

  //获取报账单类型信息
  getReTypeDetail = () => {
    const { headerData, record } = this.props.params;
    let params = {
      expenseReportTypeId: headerData.documentTypeId,
      headerId: headerData.id,
    };
    reimburseService.getConfigDetail(params).then(res => {
      let reTypeDetail = res.data || {};

      this.setState({ reTypeDetail });
      //类型为单收款方，付款行付款信息设置为头单付款信息
      let payeeCategory = !reTypeDetail.multiPayee
        ? headerData.payeeCategory
        : reTypeDetail.payeeType;

      let values = {
        payeeCategory: {
          key: payeeCategory === 'VENDER' ? 'VENDER' : 'EMPLOYEE',
          label: payeeCategory === 'VENDER' ? '供应商' : '员工',
        },
      };
      if (!reTypeDetail.multiPayee) {
        values.payeeId = {
          key: headerData.payeeId,
          label: headerData.payName,
        };
        values.accountNumber = {
          key: headerData.accountNumber,
          label: headerData.accountNumber,
        };
        values.accountName = headerData.accountName;
      }
      record.id && delete values.payeeCategory;
      this.props.form.setFieldsValue(values);
    });
  };

  //获取付款方式类型
  getPayWayTypeList = () => {
    this.getSystemValueList(2105).then(res => {
      this.setState({ payWayTypeList: res.data.values });
    });
  };

  //获取付款用途
  queryCashTransactionClassForForm = () => {
    let params = {
      expenseReportTypeId: this.props.params.headerData.documentTypeId,
    };
    reimburseService
      .queryCashTransactionClassForForm(params)
      .then(res => {
        this.setState({ cashTransactionClassList: res.data });
      })
      .catch(err => {
        message.error('获取付款用途列表失败！');
      });
  };

  onCancel = () => {
    this.props.close();
  };

  //保存
  handleSave = e => {
    e.preventDefault();

    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({ saveLoading: true });
        const { contractInfo, record } = this.state;

        let { headerData } = this.props.params;
        values.id = record.id;
        values.schedulePaymentDate &&
          (values.schedulePaymentDate = values.schedulePaymentDate.format('YYYY-MM-DD'));
        values.expReportHeaderId = headerData.id;
        values.bankLocationCode = this.state.bankLocationCode;
        values.bankLocationName = this.state.bankLocationName;
        values.companyId = headerData.companyId;
        values.departmentId = headerData.departmentId;
        values.payeeCategory = values.payeeCategory.key;
        values.accountNumber = values.accountNumber.key;
        values.payeeId = values.payeeId.key;
        values.exchangeRate = 1; //todo
        values.functionalAmount = values.amount; //todo
        values.tenantId = this.props.user.tenantId;
        values.setOfBooksId = this.props.company.setOfBooksId;
        values.applicantId = headerData.applicantId;
        values.currencyCode = headerData.currencyCode;
        values.frozenFlag = values.frozenFlag ? 'Y' : 'N';
        values.paymentMethod = values.paymentMethod.key;
        values.id && (values.versionNumber = record.versionNumber);
        if (values.cshTransactionClassId && values.cshTransactionClassId.length) {
          values.cshTransactionClassId = values.cshTransactionClassId[0].id;
        }
        //jiu.zhao 付款方式不需要 默认给0
        values.cshTransactionClassId = '0';

        if (contractInfo.contractId) {
          values.contractHeaderId = contractInfo.contractId;
          values.contractLineId = contractInfo.contractLineId;
          values.contractLineAmount = contractInfo.availableAmount;
          values.conPaymentScheduleLineId = contractInfo.contractLineId;
        }

        reimburseService
          .newPayLine(values)
          .then(res => {
            message.success('操作成功！');
            this.setState({ saveLoading: false });
            this.props.close(true);
          })
          .catch(err => {
            message.error('操作失败：' + err.response.data.message);
            this.setState({ saveLoading: false });
          });
      }
    });
  };

  checkPrice = (rule, value, callback) => {
    if (value > 0) {
      callback();
      return;
    }
    callback('金额不能小于等于0！');
  };

  //搜索
  receivablesSerarch = value => {
    if (!value) {
      this.setState({ receivables: [] });
      return;
    }

    let type = 1003;
    let payeeCategory = this.props.form.getFieldValue('payeeCategory');

    if (payeeCategory == 'EMPLOYEE') {
      type = 1001;
    } else if (payeeCategory == 'VENDER') {
      type = 1002;
    }

    this.setState({ fetching: true });

    reimburseService.getReceivables(value, type).then(res => {
      this.setState({ receivables: res.data, value, accountList: [], fetching: false });
    });
  };

  //获取收款方
  getReceivables = (value, payeeCategory) => {
    if (!value) return;
    let accountList = [];
    if (payeeCategory == 'EMPLOYEE') {
      reimburseService.getAccountByUserId(value).then(res => {
        res.data &&
          res.data.map(item => {
            if (item.enable) {
              accountList.push({
                bankAccountNo: item.bankAccountNo,
                accountName: item.bankAccountName,
                bankName: item.bankName,
                bankCode: item.bankCode,
              });
            }
          });
        this.setState({ accountList });
      });
    } else if (payeeCategory == 'VENDER') {
      reimburseService.getAccountByVendorId(value).then(res => {
        res.data.body &&
          res.data.body.map(item => {
            accountList.push({
              bankAccountNo: item.bankAccount,
              accountName: item.venBankNumberName,
              bankName: item.bankName,
              bankCode: item.bankCode,
            });
          });

        this.setState({ accountList });
      });
    }
  };

  //银行账户选取改变
  accountNumberChange = value => {
    let bankInfo = this.state.banksInfo.find(
      item => (item.bankAccount || bankAccountNo) === value.key
    );
    this.props.form.setFieldsValue({
      accountName: bankInfo.bankAccountName || bankInfo.venBankNumberName,
    });
  };

  //选定合同后
  handleListOk = values => {
    if (values && values.result[0]) {
      let contractLine = values.result[0];
      this.setState({
        contractInfo: {
          ...contractLine,
        },
        showSelectContract: false,
        contractParams: {},
        selectedData: [contractLine.contractLineId],
      });
    }
  };

  //显示选择合同
  showSelectContract = () => {
    const payeeId = this.props.form.getFieldValue('payeeId');
    if (!payeeId) {
      message.warning('请先选择收款方！');
      return;
    }

    this.refs.contractSelect.blur();
    let record = {
      companyId: this.props.params.headerData.companyId,
      partnerCategory: this.state.headerData.payeeCategory,
      partnerId: this.state.payeeId,
      documentType: 'PUBLIC_REPORT',
      currency: this.state.headerData.currencyCode,
      contractNumber: this.state.headerData.contractNumber
        ? this.state.headerData.contractNumber
        : '-',
    };

    if (this.state.headerData.relatedContract) {
      record.contractHeaderId = this.state.headerData.contractHeaderId;
    }

    this.setState({ showSelectContract: true, contractParams: record });
  };

  //四舍五入 保留两位小数
  toDecimal2 = x => {
    var f = parseFloat(x);
    if (isNaN(f)) {
      return false;
    }
    var f = Math.round(x * 100) / 100;
    var s = f.toString();
    var rs = s.indexOf('.');
    if (rs < 0) {
      rs = s.length;
      s += '.';
    }
    while (s.length <= rs + 2) {
      s += '0';
    }
    return s;
  };

  checkCost = () => {
    let cost = this.props.form.getFieldValue('amount');
    cost = this.toDecimal2(cost);
    this.props.form.setFieldsValue({ amount: cost });
  };

  //格式化金额
  formatMoney = x => {
    var f = parseFloat(x);
    if (isNaN(f)) {
      return '0.00';
    }
    var f = Math.round(x * 100) / 100;
    var s = f.toString();
    var rs = s.indexOf('.');
    if (rs < 0) {
      rs = s.length;
      s += '.';
    }
    while (s.length <= rs + 2) {
      s += '0';
    }
    return s;
  };

  //付款方类型改变清掉已经选择的收款方
  payeeCategoryChange = () => {
    //this.props.form.setFieldsValue({ payeeId: {} });
    this.setState({
      payeeId: '',
      payeeName: '',
      bankLocationName: '',
      bankLocationCode: '',
      accountList: [],
      contractInfo: {},
    });
    this.props.form.setFieldsValue({
      accountNumber: '',
      accountName: '',
      payeeId: { key: '', label: '' },
    });
  };

  //显示选取收款方列表
  showPayee = () => {
    this.setState({ showPayee: true });
  };

  //获取收款方code
  getPayeeCategoryCode = payeeCategory => {
    let type = 1003;
    if (payeeCategory == 'EMPLOYEE') {
      type = 1001;
    } else if (payeeCategory == 'VENDER') {
      type = 1002;
    }
    return type;
  };

  //收款方回调
  handlePayeeId = value => {
    let type = this.props.form.getFieldValue('payeeCategory');
    this.setState({
      payeeId: value.key,
      payeeName: value.value,
      contractInfo: {},
    });
    this.getReceivables(value.key, type && type.key);
  };

  //获取收款方
  getReceivables = (value, payeeCategory) => {
    if (!value) return;
    let flag = payeeCategory === 'EMPLOYEE';
    let method = flag ? 'getAccountByUserId' : 'getAccountByVendorId';
    let defaultBank = false;
    reimburseService[method](value).then(res => {
      defaultBank = res.data.find(item => item[flag ? 'primary' : 'primaryFlag']);
      res.data.length === 0 &&
        message.warning('该收款方没有银行信息，请先维护改收款方下银行信息！');
      if (!!defaultBank) {
        this.props.form.setFieldsValue({
          accountNumber: {
            key: defaultBank.bankAccount || defaultBank.bankAccountNo,
            label: defaultBank.bankCode + '-' + defaultBank.bankName,
          },
          accountName: defaultBank.bankAccountName || defaultBank.venBankNumberName,
        });
      }
      this.setState({ banksInfo: res.data });
    });
  };

  //收款方类型
  getContractType = () => {
    this.getSystemValueList(2107).then(res => {
      let partnerCategoryOptions = res.data.values || [];
      this.setState({ partnerCategoryOptions });
    });
  };
  //选择收款方类型
  handlePartnerCategory = record => {
    this.props.form.setFieldsValue({ payeeId: {}, accountNumber: {}, accountName: '' });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      loading,
      isNew,
      record,
      headerData,
      cashTransactionClassList,
      showSelectContract,
      contractParams,
      payWayTypeList,
      receivables,
      accountList,
      currency,
      partnerCategoryOptions,
      venderOptions,
      contractCategoryValue,
      companyId,
      partnerId,
      banksInfo,
      reTypeDetail,
      partnerName,
      contractInfo,
      showPayee,
    } = this.state;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10, offset: 1 },
    };
    return (
      <div className="new-pay-plan">
        <Row>
          <Col span={20} offset={2} style={{ marginTop: -20, marginBottom: 20 }}>
            <Alert
              message={`报账总金额: ${headerData.currencyCode &&
                `${headerData.currencyCode}  ${this.formatMoney(headerData.totalAmount)}`}
                 /  付款总金额: ${headerData.functionalAmount &&
                   `${headerData.currencyCode}  ${this.formatMoney(
                     headerData.functionalAmount || 0
                   )}`}
                 /  核销总金额: ${this.formatMoney(headerData.writeOffAmount || 0)}`}
              type="info"
            />
          </Col>
        </Row>

        {this.props.params.visible && (
          <Form onSubmit={this.handleSave}>
            <Row>
              <Col span={8} className="ant-form-item-label label-style">
                付款金额：{' '}
              </Col>
              <Col span={4} className="ant-col-offset-1">
                <FormItem>
                  {getFieldDecorator('currency', {
                    initialValue: headerData.currencyCode,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={6} style={{ marginLeft: 3 }}>
                <FormItem className="ant-col-offset-1">
                  {getFieldDecorator('amount', {
                    initialValue: record.amount,
                    rules: [{ validator: this.checkPrice }],
                  })(<CustomAmount style={{ width: '100%' }} />)}
                </FormItem>
              </Col>
            </Row>
            <FormItem {...formItemLayout} label="收款方类型">
              {getFieldDecorator('payeeCategory', {
                rules: [
                  {
                    required: true,
                    message: this.$t({ id: 'common.please.enter' }),
                  },
                  {
                    validator: (item, value, callback) => {
                      if (!value.key) {
                        callback(this.$t({ id: 'common.please.select' }));
                      }
                      callback();
                    },
                  },
                ],
                initialValue: {
                  key: record.payeeCategory || 'EMPLOYEE',
                  label: { EMPLOYEE: '员工', VENDER: '供应商' }[record.payeeCategory || 'EMPLOYEE'],
                },
              })(
                <Select
                  disabled={!reTypeDetail.multiPayee}
                  labelInValue
                  onChange={this.handlePartnerCategory}
                  onDropdownVisibleChange={
                    partnerCategoryOptions.length === 0 ? this.getContractType : () => {}
                  }
                  placeholder={this.$t({ id: 'common.please.select' })}
                >
                  {partnerCategoryOptions.map(item => (
                    <Option key={item.value}>{item.name}</Option>
                  ))}
                </Select>
              )}
            </FormItem>
            {/*this.$t('pay.backlash.sign')*/}
            <FormItem {...formItemLayout} label={'收款方'}>
              {getFieldDecorator('payeeId', {
                initialValue: {
                  key: record.payeeId ? record.payeeId : '',
                  label: record.payeeId ? record.payeeName || '假的' : '',
                },
                rules: [
                  {
                    required: true,
                    message: this.$t({ id: 'common.please.enter' }),
                  },
                  {
                    validator: (item, value, callback) => {
                      if (!value.key) {
                        callback(this.$t({ id: 'common.please.select' }));
                      }
                      callback();
                    },
                  },
                ],
              })(
                <SelectReceivables
                  disabled={!reTypeDetail.multiPayee}
                  onChange={this.handlePayeeId}
                  companyId={headerData.companyId}
                  type={
                    this.props.form.getFieldValue('payeeCategory')
                      ? this.props.form.getFieldValue('payeeCategory').key
                      : ''
                  }
                />
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="收款方银行账户">
              {getFieldDecorator('accountNumber', {
                initialValue: { key: record.accountNumber || '', label: record.accountNumber },
                rules: [
                  {
                    required: true,
                    message: '请选择',
                  },
                ],
              })(
                <Select
                  labelInValue
                  disabled={!reTypeDetail.multiPayee}
                  onChange={this.accountNumberChange}
                >
                  {banksInfo.map(item => (
                    <Option key={item.bankAccount || item.bankAccountNo}>
                      {item.bankAccount || item.bankAccountNo}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="收款方户名">
              {getFieldDecorator('accountName', {
                initialValue: record.accountName,
                rules: [
                  {
                    required: false,
                    message: '请输入',
                  },
                ],
              })(<Input disabled />)}
            </FormItem>
            <FormItem {...formItemLayout} label="付款用途">
              {getFieldDecorator('cshTransactionClassId', {
                initialValue: record.cshTransactionClassId
                  ? [
                      {
                        id: record.cshTransactionClassId,
                        description: record.cshTransactionClassName,
                      },
                    ]
                  : [],
                rules: [{ message: '请输入', required: false }],
              })(
                <Chooser
                  type="pay_source_from"
                  single={true}
                  labelKey="description"
                  valueKey="id"
                  listExtraParams={{
                    expenseReportTypeId: headerData.documentTypeId,
                  }}
                />
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="付款方式类型">
              {getFieldDecorator('paymentMethod', {
                initialValue: {
                  key: reTypeDetail.paymentMethod,
                  label: reTypeDetail.paymentMethodName,
                },
                rules: [{ message: '请输入', required: true }],
              })(
                <Select labelInValue disabled onFocus={this.getPayWayTypeList}>
                  {payWayTypeList.map(o => {
                    return <Option key={o.value}>{o.messageKey}</Option>;
                  })}
                </Select>
              )}
            </FormItem>
            <FormItem
              {...formItemLayout}
              label={this.$t({ id: 'my.contract.plan.pay.date' } /*计划付款日期*/)}
            >
              {getFieldDecorator('paymentScheduleDate', {
                initialValue: record.schedulePaymentDate
                  ? moment(record.schedulePaymentDate)
                  : null,
                rules: [
                  {
                    required: true,
                    message: this.$t({ id: 'common.please.select' } /*请选择*/),
                  },
                ],
              })(<DatePicker style={{ width: '100%' }} />)}
            </FormItem>
            <FormItem {...formItemLayout} label="备注">
              {getFieldDecorator('description', {
                initialValue: record.description || '',
              })(
                <TextArea
                  autosize={{ minRows: 2 }}
                  style={{ minWidth: '100%' }}
                  placeholder={this.$t({ id: 'common.please.enter' } /*请输入*/)}
                />
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="延后支付">
              {getFieldDecorator('frozenFlag', {
                initialValue: record.frozenFlag === 'Y',
                valuePropName: 'checked',
              })(<Switch />)}
            </FormItem>

            {reTypeDetail.associateContract && (
              <div>
                <div className="common-item-title">合同信息</div>
                <FormItem {...formItemLayout} label="关联合同">
                  <Select
                    allowClear
                    ref="contractSelect"
                    onChange={e => {
                      this.setState({
                        contractInfo: {},
                      });
                    }}
                    onDropdownVisibleChange={this.showSelectContract}
                    defaultValue={
                      isNew ? '' : contractInfo.contractLineId ? contractInfo.contractNumber : ''
                    }
                    value={contractInfo.contractLineId ? contractInfo.contractNumber : ''}
                    dropdownStyle={{ display: 'none' }}
                  />
                  <div style={{ marginTop: '4px', fontSize: '12px' }}>
                    {!contractInfo.contractLineId
                      ? '注：根据收款方选择合同'
                      : `序号：${contractInfo.lineNumber} | 付款计划日期：${moment(
                          contractInfo.dueDate
                        ).format('YYYY-MM-DD')}`}
                  </div>

                  {/*<Col span={4} style={{ textAlign: 'left' }} className="ant-form-item-label">
                      {contractInfo.contractId && (
                        <a onClick={() => this.detail(contract.contractId)}>查看详情</a>
                      )}
                    </Col>*/}
                </FormItem>
              </div>
            )}

            <div className="slide-footer">
              <Button loading={this.state.saveLoading} type="primary" htmlType="submit">
                {this.$t({ id: 'common.save' } /*保存*/)}
              </Button>
              <Button onClick={this.onCancel}>{this.$t({ id: 'common.cancel' } /*取消*/)}</Button>
            </div>
          </Form>
        )}

        <SelectContract
          visible={showSelectContract}
          onCancel={() => {
            this.setState({ showSelectContract: false, contractParams: {} });
          }}
          onOk={this.handleListOk}
          single={true}
          params={contractParams}
          selectedData={this.state.selectedData}
        />

        <ListSelector
          single={true}
          visible={showPayee}
          type="select_payee"
          labelKey="name"
          valueKey="id"
          onCancel={() => {
            this.setState({ showPayee: false });
          }}
          onOk={this.payeeHandleListOk}
          extraParams={{
            empFlag: this.getPayeeCategoryCode(this.props.form.getFieldValue('payeeCategory')),
            name: '',
            pageFlag: true,
          }}
          selectedData={
            this.state.payeeId
              ? [{ id: this.state.payeeId, name: this.state.payeeName }]
              : [{ key: '', label: '' }]
          }
        />
      </div>
    );
  }
}
function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

const wrappedNewPayPlan = Form.create()(NewPayPlan);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedNewPayPlan);
