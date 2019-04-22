import React from 'react';
import { connect } from 'dva';
import Lov from 'widget/Template/lov';
import moment from 'moment';
import { Form, InputNumber, Button, Icon, Switch, Input, DatePicker, message } from 'antd';
import AccountTransferSettingService from './account-transfer-setting.service';

const FormItem = Form.Item;

class AccountTransferSlide extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      // checkStatus: 1,
      selectModalRowValue: {}, // 选中madal中的行值
      loading: false,
      isNew: true, // 是否为新建
    };
  }

  componentWillMount() {
    const { rowpParams } = this.props;
    if (rowpParams.id) {
      // id存在，为修改状态
      this.setState({
        isNew: false,
      });
    }
  }

  componentDidMount() {}

  /**
   * Lov组件中，modal中选中元素的那一行值
   */
  changeValue = values => {
    this.setState({
      selectModalRowValue: values,
    });
  };

  /**
   * 表单取消
   */
  handleCancel = value => {
    const { onClose } = this.props;
    onClose(value);
  };

  /**
   * 侧栏框中的Form表单的保存提交
   */
  handleSave = e => {
    const { form, rowpParams } = this.props;
    e.preventDefault();
    form.validateFields((err, values) => {
      let saveDatas = {};
      if (err) return;
      this.setState({ loading: true });
      const { selectModalRowValue, isNew } = this.state;
      if (isNew) {
        // 新建保存
        saveDatas = {
          id: '',
          accountId: selectModalRowValue.id,
          enabled: values.states === true ? 1 : 0 || rowpParams.enabled, // 状态
          // enabled: values.states, // 状态
          priority: values.priority, // 序号
          bankAccount: selectModalRowValue.accountNumber, // 资金池母账号
          bankAccountName: selectModalRowValue.accountName, // 资金池母账户
          bankCode: selectModalRowValue.openBank, // 所属银行
          companyId: selectModalRowValue.companyId, // 所属公司
        };
      } else {
        // 修改保存
        saveDatas = {
          id: rowpParams.id,
          priority: values.priority,
          accountId: selectModalRowValue.id,
          enabled: Number(values.states), // 状态
          // enabled: (values.states === 'true') ? 1 : 0 || rowpParams.enabled, // 状态
          bankAccount: selectModalRowValue.accountNumber, // 资金池母账号
          bankAccountName: selectModalRowValue.accountName, // 资金池母账户
          bankCode: selectModalRowValue.openBank, // 所属银行
          companyId: selectModalRowValue.companyId, // 所属公司
        };
      }
      // console.log('保存数据', saveDatas);
      AccountTransferSettingService.createOrUpdateSave(saveDatas)
        .then(() => {
          message.success('保存成功！');
          this.handleCancel('save');
          this.setState({ loading: false, isNew: false });
        })
        .catch(error => {
          message.error(error.response.data.message);
          // message.error('保存失败！');
          this.setState({ loading: false });
        });
    });
  };

  render() {
    const {
      form: { getFieldDecorator },
      rowpParams,
      priority,
      user,
    } = this.props;
    const { loading, isNew, selectModalRowValue } = this.state;
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 12,
      },
    };
    const formItemLayoutSwitch = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        offset: 3,
        span: 3,
      },
    };
    return (
      <div>
        <Form>
          <FormItem label="序号" {...formItemLayout}>
            {getFieldDecorator('priority', {
              rules: [{ required: true, message: '序号不能为空' }],
              // initialValue: rowpParams.priority,
              initialValue: isNew ? priority : rowpParams.priority,
            })(
              <InputNumber
                disabled
                style={{ width: '343px', height: '32px' }}
                placeholder="请输入"
              />
            )}
          </FormItem>
          <FormItem label="资金池母账号" {...formItemLayout}>
            {getFieldDecorator('poolAccountNum', {
              rules: [{ required: true, message: '资金池母账号不能为空' }],
              initialValue: { id: rowpParams.id, accountNumber: rowpParams.bankAccount },
            })(
              <Lov
                code="bankaccount_choose"
                valueKey="id"
                labelKey="accountNumber"
                single
                // extraParams={{}}
                onChange={this.changeValue}
              />
            )}
          </FormItem>
          <FormItem label="资金池母账户" {...formItemLayout}>
            {getFieldDecorator('poolAccountName', {
              rules: [{ required: true, message: '资金池母账户不能为空' }],
              initialValue: selectModalRowValue.accountName || (rowpParams.bankAccountName || ''),
              // initialValue: isNew ? (selectModalRowValue ? selectModalRowValue.accountName : '') : (rowpParams.bankAccountName || ''),
            })(<Input disabled />)}
          </FormItem>
          <FormItem label="所属银行" {...formItemLayout}>
            {getFieldDecorator('bank', {
              rules: [{ required: true, message: '所属银行不能为空' }],
              // initialValue: isNew ? (selectModalRowValue ? selectModalRowValue.openBankName : '') : (rowpParams.bankName || ''),
              initialValue: selectModalRowValue.openBankName || (rowpParams.bankName || ''),
            })(<Input disabled />)}
          </FormItem>
          <FormItem label="所属公司" {...formItemLayout}>
            {getFieldDecorator('company', {
              rules: [{ required: true, message: '所属公司不能为空' }],
              // initialValue: (selectModalRowValue ? selectModalRowValue.companyName : '') || (rowpParams.companyName || ''),
              initialValue: selectModalRowValue.companyName || (rowpParams.companyName || ''),
            })(<Input disabled />)}
          </FormItem>
          <FormItem label="创建人" {...formItemLayout}>
            {getFieldDecorator('creater', {
              rules: [{ required: true, message: '创建人不能为空' }],
              initialValue: isNew ? user.userName : rowpParams.createdName,
              // initialValue: isNew ? user.userName : {id: rowpParams.createdBy, name: rowpParams.createdName},
            })(<Input placeholder="请输入" disabled />)}
          </FormItem>
          <FormItem label="创建日期" {...formItemLayout}>
            {getFieldDecorator('createDate', {
              rules: [{ required: true, message: '创建日期不能为空' }],
              // initialValue: isNew ? '' : moment(rowpParams.createdDate),
              initialValue: moment(rowpParams.createdDate),
            })(
              <DatePicker
                style={{ width: '343px', height: '32px' }}
                placeholder="请选择"
                disabled
              />
            )}
          </FormItem>
          <FormItem label="状态" {...formItemLayoutSwitch}>
            {getFieldDecorator('states', {
              valuePropName: 'checked',
              initialValue: typeof rowpParams.id === 'undefined' ? true : rowpParams.enabled,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
              />
            )}
          </FormItem>
          <div className="slide-footer">
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              style={{ margin: '0 20px' }}
              onClick={this.handleSave}
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

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
export default connect(mapStateToProps)(Form.create()(AccountTransferSlide));
