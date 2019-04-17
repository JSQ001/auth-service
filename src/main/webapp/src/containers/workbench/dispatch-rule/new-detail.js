import React from 'react';
import { connect } from 'dva';
import { Form, Button, Input, message, Switch, Icon, InputNumber, Select } from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
import service from './service';
import { messages } from 'utils/utils';
import Chooser from 'widget/chooser';
import config from 'config';

class NewRuleDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      record: {},
      loading: false,
      isEditor: false,
      businessTypeId: null,
      ruleId: null,
      judgeRuleOptions: [],
      bracketOptions: [{ label: '(', value: '(' }, { label: ')', value: ')' }],
      andOrOptions: [{ label: 'AND', value: 'AND' }, { label: 'OR', value: 'OR' }],
      parameterSelectorItem: {
        title: this.$t('workbench.please.select.a.rule.parameters') /*请选择规则参数*/,
        url: `${config.workbenchUrl}/api/workbench/businessParameter/query`,
        searchForm: [
          {
            type: 'input',
            id: 'parameterCode',
            label: messages('workbench.businessType.parameter.code' /*参数代码*/),
          },
          {
            type: 'input',
            id: 'parameterName',
            label: messages('workbench.businessType.parameter.name' /*参数名称*/),
          },
        ],
        columns: [
          {
            title: messages('workbench.businessType.parameter.code' /*参数代码*/),
            dataIndex: 'parameterCode',
            width: 150,
          },
          {
            title: messages('workbench.businessType.parameter.name' /*参数名称*/),
            dataIndex: 'parameterName',
            width: 150,
          },
        ],
        key: 'id',
      },
    };
  }

  componentWillMount() {
    // 获取值列表
    this.getJudgeRuleOptions();
  }

  getJudgeRuleOptions() {
    let judgeRuleOptions = [];
    this.getSystemValueList('WBC_RULE_CONDITION').then(res => {
      res.data.values.map(data => {
        judgeRuleOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        judgeRuleOptions,
      });
    });
  }

  componentDidMount() {
    const { record, isEditor, ruleId, businessTypeId } = this.props.params;
    this.setState({
      record: record,
      isEditor: isEditor,
      ruleId: ruleId,
      businessTypeId: businessTypeId,
    });
  }

  // 取消
  onCancel = () => {
    this.props.onClose && this.props.onClose(false);
  };

  // 保存
  handleSave = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({ loading: true });
        const { record, ruleId } = this.state;
        let parameterId = values.parameterId[0].id;
        values['parameterId'] = parameterId;
        let params = { ...record, ...values, ruleId: ruleId };
        service
          .saveRuleDeatil(params)
          .then(res => {
            message.success(messages('common.operate.success')); //保存成功
            this.setState({ loading: false });
            // 返回
            this.props.onClose && this.props.onClose(true);
          })
          .catch(e => {
            if (e.response) {
              message.error(
                messages('common.save.filed' /*保存失败*/) + '!' + e.response.data.message
              );
            } else {
              message.error(messages('common.operate.filed'));
            }
            this.setState({ loading: false });
          });
      }
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      record,
      loading,
      businessTypeId,
      isEditor,
      judgeRuleOptions,
      bracketOptions,
      andOrOptions,
      parameterSelectorItem,
    } = this.state;
    const formItemLayout = {
      labelCol: { span: 5 },
      wrapperCol: { span: 13, offset: 1 },
    };
    return (
      <div className="new-payment-requisition-line">
        <Form onSubmit={this.handleSave}>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.dispatch.rule.priority' /*优先级*/)}
          >
            {getFieldDecorator('priority', {
              rules: [
                {
                  required: true,
                  message: messages('common.please.enter'), //请输入
                },
              ],
              initialValue: isEditor ? record.priority : null,
            })(<InputNumber precision={0} placeholder={messages('common.please.enter')} />)}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.dispatch.rule.andOr' /*逻辑操作*/)}
          >
            {getFieldDecorator('andOr', {
              rules: [
                {
                  required: true,
                  message: messages('common.please.select'), //请选择
                },
              ],
              initialValue: isEditor ? record.andOr : null,
            })(
              <Select placeholder={messages('common.please.select')}>
                {andOrOptions.map(option => {
                  return <Option key={option.value}>{option.label}</Option>;
                })}
              </Select>
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.dispatch.rule.leftBracket' /*左括号*/)}
          >
            {getFieldDecorator('leftBracket', {
              initialValue: isEditor ? record.leftBracket : null,
            })(
              <Select placeholder={messages('common.please.select')} allowClear={true}>
                {bracketOptions.map(option => {
                  return <Option key={option.value}>{option.label}</Option>;
                })}
              </Select>
            )}
          </FormItem>

          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.dispatch.rule.parameter' /*规则参数*/)}
          >
            {getFieldDecorator('parameterId', {
              rules: [
                {
                  required: true,
                  message: this.$t('workbench.desc.code2'), // 规则参数不允许为空
                },
              ],
              initialValue: isEditor
                ? [{ id: record.parameterId, parameterName: record.parameterName }]
                : [],
            })(
              <Chooser
                selectorItem={parameterSelectorItem}
                labelKey="parameterName"
                valueKey="id"
                single={true}
                listExtraParams={{ businessTypeId: businessTypeId, enabled: true }}
              />
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.dispatch.rule.judgeRule' /*条件类型*/)}
          >
            {getFieldDecorator('judgeRule', {
              rules: [
                {
                  required: true,
                  message: messages('common.please.select'), //请选择
                },
              ],
              initialValue: isEditor ? record.judgeRule : null,
            })(
              <Select placeholder={messages('common.please.select')}>
                {judgeRuleOptions.map(option => {
                  return <Option key={option.value}>{option.label}</Option>;
                })}
              </Select>
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.dispatch.rule.judgeData' /*值*/)}
          >
            {getFieldDecorator('judgeData', {
              rules: [
                {
                  required: true,
                  message: messages('common.please.enter'), //请输入
                },
              ],
              initialValue: isEditor ? record.judgeData : null,
            })(<Input placeholder={messages('common.please.enter')} />)}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.dispatch.rule.rightBracket' /*右括号*/)}
          >
            {getFieldDecorator('rightBracket', {
              initialValue: isEditor ? record.rightBracket : '',
            })(
              <Select placeholder={messages('common.please.select')} allowClear={true}>
                {bracketOptions.map(option => {
                  return <Option key={option.value}>{option.label}</Option>;
                })}
              </Select>
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.dispatch.rule.sqlParam1' /*sql参数1*/)}
          >
            {getFieldDecorator('sqlParam1', {
              initialValue: isEditor ? record.sqlParam1 : '',
            })(<Input placeholder={messages('common.please.enter')} />)}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.dispatch.rule.sqlParam2' /*sql参数2*/)}
          >
            {getFieldDecorator('sqlParam2', {
              initialValue: isEditor ? record.sqlParam2 : '',
            })(<Input placeholder={messages('common.please.enter')} />)}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.dispatch.rule.sqlParam3' /*sql参数3*/)}
          >
            {getFieldDecorator('sqlParam3', {
              initialValue: isEditor ? record.sqlParam3 : '',
            })(<Input placeholder={messages('common.please.enter')} />)}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.dispatch.rule.sqlParam4' /*sql参数4*/)}
          >
            {getFieldDecorator('sqlParam4', {
              initialValue: isEditor ? record.sqlParam4 : '',
            })(<Input placeholder={messages('common.please.enter')} />)}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.dispatch.rule.sqlParam5' /*sql参数5*/)}
          >
            {getFieldDecorator('sqlParam5', {
              initialValue: isEditor ? record.sqlParam5 : '',
            })(<Input placeholder={messages('common.please.enter')} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={messages('workbench.businessType.enabled')}>
            {getFieldDecorator('enabled', {
              initialValue: isEditor ? record.enabled : true,
              valuePropName: 'checked',
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
              /> // 状态
            )}
          </FormItem>
          <div className="slide-footer">
            <Button type="primary" htmlType="submit" loading={loading}>
              {messages('common.save' /*保存*/)}
            </Button>
            <Button onClick={this.onCancel} loading={loading}>
              {messages('common.cancel' /*取消*/)}
            </Button>
          </div>
        </Form>
      </div>
    );
  }
}
function mapStateToProps(state) {
  return {};
}
const wrappedNewRuleDetail = Form.create()(NewRuleDetail);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedNewRuleDetail);
