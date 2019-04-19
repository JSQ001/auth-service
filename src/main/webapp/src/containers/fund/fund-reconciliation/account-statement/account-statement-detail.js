import React, { Component } from 'react';
import { Form, Button, Input, DatePicker, message } from 'antd';
import { connect } from 'dva';
import moment from 'moment';
import Lov from 'widget/Template/lov';

const FormItem = Form.Item;

class accountStatementDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isNew: true,
      loading: false,
    };
  }

  componentDidMount() {
    const { params } = this.props;
    console.log('params', params);
    if (params.id) {
      this.setState({
        isNew: false,
      });
    }
  }

  /**
   * 取消
   */
  handleCancel = value => {
    const { onClose } = this.props;
    onClose(value);
  };

  /**
   * 保存数据
   */
  insave = () => {
    const {
      form: { validateFields },
      params,
      save,
    } = this.props;
    validateFields((err, values) => {
      console.log('111', values, params);
      if (!err) {
        const saveData = {
          accountDate: values.accountDate.format('YYYY-MM-DD') || '',
          accountId: values.id.id || '',
          accountNumber: values.id.accountNumber || '',
          debitAmount: values.debitAmount || 0,
          creditAmount: values.creditAmount || 0,
          sinceAmount: values.sinceAmount || '',
          otherAccount: values.otherAccount || '',
          otherAccountName: values.otherAccountName || '',
          bankSn: values.bankSn || '',
          summary: values.summary || '',
        };
        save(saveData);
        console.log('saveData', saveData);
      }
    });
  };

  /**
   * 检查借贷方金额
   */
  checkNum = () => {
    const {
      form: { getFieldValue },
    } = this.props;
    const debitAmount = Number(getFieldValue('debitAmount'));
    const creditAmount = Number(getFieldValue('creditAmount'));
    if (debitAmount > 0 && creditAmount > 0) {
      message.error('贷方金额或借方金额有一个必须为0');
    }
  };

  render() {
    const {
      form: { getFieldDecorator },
      params,
    } = this.props;
    const { isNew, loading } = this.state;
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 12,
      },
    };
    return (
      <div className="model">
        <Form>
          <FormItem label="交易时间" {...formItemLayout}>
            {getFieldDecorator('accountDate', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: isNew ? moment() : moment(params.accountDate),
            })(<DatePicker format="YYYY-MM-DD" />)}
          </FormItem>
          <FormItem label="银行账号" {...formItemLayout}>
            {getFieldDecorator('id', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: { id: params.id, accountNumber: params.id },
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
          <FormItem label="借方金额" {...formItemLayout}>
            {getFieldDecorator('debitAmount', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: !isNew ? params.debitAmount : '',
            })(<Input />)}
          </FormItem>
          <FormItem label="贷方金额" {...formItemLayout}>
            {getFieldDecorator('creditAmount', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: !isNew ? params.creditAmount : '',
            })(<Input onBlur={this.checkNum} />)}
          </FormItem>
          <FormItem label="余额" {...formItemLayout}>
            {getFieldDecorator('sinceAmount', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: !isNew ? params.sinceAmount : '',
            })(<Input />)}
          </FormItem>
          <FormItem label="对方账号" {...formItemLayout}>
            {getFieldDecorator('otherAccount', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: !isNew ? params.otherAccount : '',
            })(<Input />)}
          </FormItem>
          <FormItem label="对方账户" {...formItemLayout}>
            {getFieldDecorator('otherAccountName', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: !isNew ? params.otherAccount : '',
            })(<Input />)}
          </FormItem>
          <FormItem label="交易流水号" {...formItemLayout}>
            {getFieldDecorator('bankSn', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: !isNew ? params.bankSn : '',
            })(<Input />)}
          </FormItem>
          <FormItem label="备注" {...formItemLayout}>
            {getFieldDecorator('summary', {
              rules: [{ message: this.$t('common.please.select') }],
              initialValue: !isNew ? params.summary : '',
            })(<Input />)}
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

export default connect(map)(Form.create()(accountStatementDetail));
