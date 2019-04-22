import React from 'react';
import { connect } from 'dva';
import { Form, Button, Input, Select, message, Switch, Icon } from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
import businessProcedureService from './business-procedure.service';
import { messages } from 'utils/utils';

class BusinessProcedureDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      record: {},
      loading: false,
      isEditor: false,
      businessTypeId: null,
      disabledFlag: false,
      typeOptions: [],
    };
  }

  getTypeOptions() {
    let typeOptions = [];
    this.getSystemValueList('WBC_PROCEDURE_TYPE').then(res => {
      res.data.values.map(data => {
        typeOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        typeOptions,
      });
    });
  }

  componentDidMount() {
    this.getTypeOptions();
    const { record, isEditor, businessTypeId } = this.props.params;
    let editorRecord = { ...record };
    if (isEditor) {
      businessProcedureService
        .getBusinessProcedureById(editorRecord.id)
        .then(res => {
          if (res.status === 200) {
            editorRecord = { ...res.data };
            this.setState({
              record: editorRecord,
              isEditor: isEditor,
              businessTypeId: businessTypeId,
              disabledFlag: isEditor && editorRecord.systemFlag === 'SYSTEM',
            });
          } else {
            message.error(
              messages('common.error') /*哦呼，服务器出了点问题，请联系管理员或稍后再试:(*/
            );
          }
        })
        .catch(() => {
          message.error(
            messages('common.error') /*哦呼，服务器出了点问题，请联系管理员或稍后再试:(*/
          );
        });
    } else {
      this.setState({
        record: editorRecord,
        isEditor: isEditor,
        businessTypeId: businessTypeId,
        disabledFlag: isEditor && editorRecord.systemFlag === 'SYSTEM',
      });
    }
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
        const { record, businessTypeId } = this.state;
        let params = { ...record, ...values, businessTypeId: businessTypeId };
        businessProcedureService
          .saveBusinessProcedure(params)
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
    const { record, loading, isEditor, disabledFlag, typeOptions } = this.state;
    const formItemLayout = {
      labelCol: { span: 5 },
      wrapperCol: { span: 13, offset: 1 },
    };
    return (
      <div className="new-payment-requisition-line">
        <Form onSubmit={this.handleSave}>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.procedure.type' /*过程类型*/)}
          >
            {getFieldDecorator('procedureType', {
              rules: [
                {
                  required: true,
                  message: messages('common.please.select'), //请选择
                },
              ],
              initialValue: isEditor ? record.procedureType : '',
            })(
              <Select disabled={disabledFlag} placeholder={messages('common.please.select')}>
                {typeOptions.map(option => {
                  return <Option key={option.value}>{option.label}</Option>;
                })}
              </Select>
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.procedure.code' /*过程代码*/)}
          >
            {getFieldDecorator('procedureCode', {
              rules: [
                {
                  required: true,
                  message: messages('common.please.enter'), //请输入
                },
              ],
              initialValue: isEditor ? record.procedureCode : '',
            })(<Input disabled={isEditor} placeholder={messages('common.please.enter')} />)}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.procedure.name' /*过程名称*/)}
          >
            {getFieldDecorator('procedureName', {
              rules: [
                {
                  required: true,
                  message: messages('common.please.enter'), //请输入
                },
              ],
              initialValue: isEditor ? record.procedureName : '',
            })(<Input disabled={disabledFlag} placeholder={messages('common.please.enter')} />)}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={messages('workbench.businessType.procedure.remark' /*过程内容*/)}
          >
            {getFieldDecorator('procedureContent', {
              rules: [
                {
                  required: true,
                  message: messages('common.please.enter'), //请输入
                },
              ],
              initialValue: isEditor ? record.procedureContent : '',
            })(
              <Input.TextArea
                disabled={disabledFlag}
                placeholder={messages('workbench.businessType.procedure.remark.tip')}
                rows={4}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={messages('workbench.businessType.enabled')}>
            {getFieldDecorator('enabled', {
              initialValue: isEditor ? record.enabled : true,
              valuePropName: 'checked',
            })(
              <Switch
                disabled={disabledFlag}
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
              /> // 状态
            )}
          </FormItem>
          <div className="slide-footer">
            {disabledFlag ? (
              ''
            ) : (
              <Button type="primary" htmlType="submit" loading={loading}>
                {this.$t({ id: 'common.save' } /*保存*/)}
              </Button>
            )}
            <Button onClick={this.onCancel} loading={loading}>
              {disabledFlag ? messages('common.back') : messages('common.cancel' /*取消*/)}
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
const wrappedBusinessProcedureDetail = Form.create()(BusinessProcedureDetail);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedBusinessProcedureDetail);
