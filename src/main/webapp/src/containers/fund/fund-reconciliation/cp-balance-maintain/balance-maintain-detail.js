import React, { Component } from 'react';
import { Form, Input, Button, message, Select } from 'antd';
import { connect } from 'dva';
import Chooser from 'widget/chooser';
import 'styles/fund/account.scss';
import Lov from 'widget/Template/lov';
// import config from 'config';
import accountService from './cp-balance-maintain.service';
// import { arraysAreEqual } from 'tslint/lib/utils';

// const { Option } = Select;
const FormItem = Form.Item;
const { Option } = Select;

class BalanceMaintainDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false, // 加载中
      isNew: true, // 是否为新建
      model: {}, // 保存数据
      currencyList: [], // 币种
      periodList: [],
      banknumber: {
        key: '',
        label: '',
      }, // 联行号
      bankFetching: false,
      approveStatus: '', // 单据状态
      isTrue: false, // 单据状态为已审批和审批中的不可修改信息
      bankList: [], // 选择后返回的银行信息
      //  saveData:[], // 保存的数据
    };
  }

  componentDidMount() {
    this.getPeriod();
    const { params } = this.props;
    if (params.id) {
      this.setState({
        isNew: false,
      });
    }
    if (params.reviewStatus === 'Y') {
      this.setState({
        isTrue: true,
      });
    }
    // if (params.approveStatus === 'ZJ_APPROVED' || params.approveStatus === 'ZJ_PENGDING') {
    //   this.setState({
    //     isTrue: true,
    //   });
    // }
    //  this.getCurrencyList();
  }

  insave = () => {
    const { save, form, user, params } = this.props;
    const { bankList } = this.state;
    form.validateFields((err, values) => {
      if (!err) {
        const saveData = {
          id: params.id ? params.id : '',
          companyId: user.companyId,
          bankAccount: values.accountnumber.accountNumber,
          bankAccountName: bankList.accountName,
          gatherBank: bankList.openBank,
          period: values.period.label,
          currency: bankList.currencyCode,
          periodInitAmountActual: values.amount,
          // ifDisplay :true,
          // reviewStatus :true,
          versionNumber: params.id ? params.versionNumber : '',
        };
        save(saveData);
      }
    });
  };

  onChange = values => {
    this.setState({
      isNew: true,
      bankList: values,
    });
  };

  /**
   * 点击选择银行
   */
  chooseBank = value => {
    /* eslint-disable */
    this.props.form.setFieldsValue({
      banknumber: {
        key: value[0].bankCode,
        label: value[0].bankCode,
      },
      /*
      openProvince: {
        key: value[0].provinceCode,
        label: value[0].province ? value[0].province : value[0].city,
      },
      openCity: {
        key: value[0].cityCode,
        label: value[0].city ? value[0].city : value[0].province,
      },
      */
    });
    /* eslint-disable */
  };

  /**
   * 获取币种
   */
  getCurrencyList = () => {
    const { company } = this.props;
    accountService
      .getCurrencyList(company.companyOid)
      .then(res => {
        this.setState({ currencyList: res.data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 获取期间数据
   */
  getPeriod = () => {
    accountService.getPeriod().then(res => {
      this.setState({
        periodList: res.data,
      });
    });
  };

  /**
   * 获取开户银行列表
   */
  getAccountBank = () => {
    accountService
      .getAccountBank()
      .then(res => {
        if (res.data.length > 0) {
          this.setState({
            bankOptions: res.data,
            bankFetching: true,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };
  /**
   * 取消
   */
  handleCancel = value => {
    const { onClose } = this.props;
    onClose(value);
  };

  render() {
    const {
      form: { getFieldDecorator },
      user,
      company,
      params,
    } = this.props;
    const {
      loading,
      isNew,
      currencyList,
      fileList,
      model,
      openProvince,
      openCity,
      bankFetching,
      banknumber,
      bankOptions,
      approveStatus,
      bankList,
      isTrue,
      periodList,
    } = this.state;
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 12,
      },
    };

    return (
      <div className="new-contract" style={{ marginBottom: 60, marginTop: 10 }}>
        <Form>
          <FormItem label="公司" {...formItemLayout}>
            {getFieldDecorator('company', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: isNew
                ? [{ id: company.id, name: company.name }]
                : [{ id: params.companyId, name: params.companyName }],
            })(
              <Chooser
                type="company"
                labelKey="name"
                valueKey="id"
                showClear={false}
                single={true}
                listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                disabled
              />
            )}
          </FormItem>

          <FormItem label="银行账号" {...formItemLayout}>
            {getFieldDecorator('accountnumber', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: { id: params.id, accountNumber: params.bankAccount },
            })(
              <Lov
                code="bankaccount_choose"
                valueKey="id"
                labelKey="accountNumber"
                single
                onChange={this.onChange}
                disabled={isTrue}
              />
            )}
          </FormItem>
          <FormItem label="所属银行" {...formItemLayout}>
            {getFieldDecorator('openBank', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: isNew ? bankList.openBankName : params.gatherBankName,
            })(<Input disabled={true} />)}
          </FormItem>
          <FormItem label="银行户名" {...formItemLayout}>
            {getFieldDecorator('currencyCode', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: isNew ? bankList.accountName : params.bankAccountName,
            })(<Input disabled={true} />)}
          </FormItem>

          <FormItem label="币种" {...formItemLayout}>
            {getFieldDecorator('currencyCode', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: isNew ? bankList.currencyCode : params.currency,
            })(<Input disabled={true} />)}
          </FormItem>
          <FormItem label="期间" {...formItemLayout}>
            {getFieldDecorator('period', {
              rules: [{ required: true }],
              initialValue: isNew ? [] : [{ key: params.period, label: params.period }],
            })(
              <Select labelInValue placeholder="请选择">
                {periodList.map(option => {
                  return <Option key={option.periodName}>{option.periodName}</Option>;
                })}
              </Select>
            )}
          </FormItem>

          <FormItem label="余额" {...formItemLayout}>
            {getFieldDecorator('amount', {
              initialValue: isNew ? '' : params.periodInitAmountActual,
            })(<Input disabled={isTrue} />)}
          </FormItem>
          <div className="slide-footer">
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              style={{ margin: '0 20px' }}
              onClick={this.insave}
              // disabled={approveStatus === 'ZJ_APPROVED' || approveStatus === 'ZJ_PENGDING'}
            >
              保存
            </Button>
            <Button onClick={this.handleCancel}>取消</Button>
          </div>
        </Form>
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

export default connect(map)(Form.create()(BalanceMaintainDetail));
