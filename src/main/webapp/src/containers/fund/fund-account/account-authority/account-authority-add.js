import React from 'react';
import { connect } from 'dva';
import 'styles/fund/account.scss';
import { Form, Input, Button, Select, Switch, message, Icon } from 'antd';
import Chooser from 'widget/chooser';
import accountService from './account-authority.service';

const FormItem = Form.Item;
const { Option } = Select;

class accountAuthorityAdd extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      validationTypeOptions: [], // 验证类型值列表
      loading: false, // 加载中
      isNew: true, // 是否为新建
      model: {}, // 保存数据
      isTrueXu: true, // 序列号是否需要自己输入
      code: '',
      employeeI: '',
    };
  }

  componentDidMount() {
    this.getValidationTypeOptions();
    const { params } = this.props; // 修改传的值
    if (params.employeeId) {
      this.getAccountHead(params.employeeId);
    }
    if (params.defaultUkeyTypeDesc === 'UKEY') {
      this.setState({
        isTrueXu: false,
      });
    }
  }

  /**
   * 根据ID获取账户头
   */
  getAccountHead = employeeId => {
    accountService.getAccountHead(employeeId).then(res => {
      // console.log(res.data[0])
      this.setState({
        isNew: false, // 是否为新建
        model: res.data[0], // 保存数据
      });
    });
  };

  /**
   * 获取验证类型值列表
   */
  getValidationTypeOptions = () => {
    this.getSystemValueList('ZJ_VALIDATYPE_UKEY')
      .then(res => {
        if (res.data.values.length > 0) {
          this.setState({
            validationTypeOptions: res.data.values,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   *
   * 取消
   * */
  handleCancel = value => {
    const { onClose } = this.props;
    onClose(value);
  };

  // 点击选择员工
  chooseName = value => {
    this.setState({
      code: value[0].userCode,
      employeeI: value[0].userId,
    });
  };

  /**
   * 值列表改变时触发
   * 为动态验证时，不需要输入序列号
   */
  currencyChange = values => {
    if (values.key === 'UkEY') {
      this.setState({ isTrueXu: true });
    } else {
      this.setState({ isTrueXu: false });
    }
  };

  /**
   * 表单提交
   */
  handleSave = e => {
    const { employeeI, isNew, model } = this.state;
    const { form } = this.props; // antd封装的form
    let saveDate = {};
    e.preventDefault();
    /**
     * @values form 表单里面输入控件的值，为一个对象
     */
    form.validateFields((err, values) => {
      if (!isNew) {
        console.log(values.currencyCode);
        if (values.currencyCode.key) {
          saveDate = {
            employeeId: employeeI || model.employeeId,
            defaultUkeyType: values.currencyCode ? values.currencyCode.key : '',
            enabled: values.states,
            defaultUkeyDesc: values.xulie,
            versionNumber: model.versionNumber,
            id: model.id,
          };
        } else {
          saveDate = {
            employeeId: employeeI || model.employeeId,
            defaultUkeyType: values.currencyCode[0] ? values.currencyCode[0].key : '',
            enabled: values.states,
            defaultUkeyDesc: values.xulie,
            versionNumber: model.versionNumber,
            id: model.id,
          };
        }
      } else {
        saveDate = {
          employeeId: employeeI,
          defaultUkeyType: values.currencyCode ? values.currencyCode.key : '',
          enabled: values.states,
          defaultUkeyDesc: values.xulie,
        };
      }
      console.log(saveDate);
      if (saveDate.employeeId) {
        accountService
          .add(saveDate)
          .then(() => {
            message.success(this.$t('fund.save.successful1'));
            this.handleCancel('save');
          })
          .catch(errr => {
            message.error(errr.response.data.message);
            this.setState({ loading: false });
          });
      } else {
        message.error(this.$t('fund.fill.complete.data'));
      }
    });
  };

  render() {
    const {
      form: { getFieldDecorator },
      params,
    } = this.props;
    const {
      loading,
      listExtraParams,
      code,
      isTrueXu,
      isNew,
      model,
      validationTypeOptions,
    } = this.state;
    const { defaultUkeyTypeDesc, defaultUkeyType } = params;
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 12,
      },
    };
    const formItemLayout1 = {
      labelCol: {
        span: 13,
      },
      wrapperCol: {
        span: 2,
      },
    };
    return (
      <div>
        <Form>
          <FormItem label={this.$t('fund.employee.name')} {...formItemLayout}>
            {getFieldDecorator('employeeName', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: [
                isNew ? '' : { userOid: model.employeeId, userName: model.employeeName },
              ],
            })(
              <Chooser
                type="select_authorization_user"
                labelKey="userName"
                valueKey="userOid"
                onChange={this.chooseName}
                listExtraParams={listExtraParams}
                single
              />
            )}
          </FormItem>
          <FormItem label={this.$t('fund.employee.code')} {...formItemLayout}>
            {getFieldDecorator('employeeId', {
              rules: [{ required: true, message: this.$t('fund.employee.code.cannot.empty ') }],
              initialValue: isNew ? code : code || model.employeeCode,
            })(<Input disabled onChange />)}
          </FormItem>
          <FormItem label={this.$t('fund.default.authentication.type')} {...formItemLayout}>
            {getFieldDecorator('currencyCode', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: [{ key: defaultUkeyType, value: defaultUkeyTypeDesc }] || '',
            })(
              <Select
                labelInValue
                placeholder={this.$t('fund.please.choose')}
                onSelect={this.currencyChange}
                allowClear
              >
                {validationTypeOptions.map(option => {
                  return <Option key={option.value}>{option.name}</Option>;
                })}
              </Select>
            )}
          </FormItem>
          <FormItem label={this.$t('fund.serial.number')} {...formItemLayout}>
            {getFieldDecorator('xulie', {
              rules: [{ required: false }],
              initialValue: isNew ? '' : model.defaultUkeyDesc,
            })(<Input disabled={isTrueXu} AUTOCOMPLETE="off" />)}
          </FormItem>
          <FormItem label={this.$t('fund.enable')} {...formItemLayout1}>
            {getFieldDecorator('states', {
              valuePropName: 'checked',
              initialValue: typeof model.id === 'undefined' ? true : model.enabled,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
              />
            )}
          </FormItem>
          {/* 底部表单操作 */}
          <div className="slide-footer">
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              style={{ margin: '0 20px' }}
              onClick={this.handleSave}
            >
              {this.$t('fund.save')}
            </Button>
            <Button onClick={this.handleCancel}>{this.$t('fund.cancel')}</Button>
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

export default connect(mapStateToProps)(Form.create()(accountAuthorityAdd));
