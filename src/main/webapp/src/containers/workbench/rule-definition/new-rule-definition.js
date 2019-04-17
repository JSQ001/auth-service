import React, { Component } from 'react';
import { Form, Select, Switch, Button, Spin, message } from 'antd';
import service from './rule-definition-service';

const FormItem = Form.Item;
const Option = Select.Option;

class NewRuleDefinition extends Component {
  constructor(props) {
    super(props);
    this.state = {
      wbcBusinessTypeList: [],
      endProcedureList: [],
      saveLoading: false,
      selectLoading: false,
    };
  }

  componentDidMount() {
    const businessTypeId = this.props.params.businessTypeId;
    businessTypeId && this.businessTypeChange({ key: businessTypeId }, false);
    this.getParams();
  }

  // 获取 业务类型 下拉菜单
  getParams = () => {
    service
      .getBusinessType()
      .then(res => {
        const wbcBusinessTypeList = res.data.map(item => {
          return {
            key: item.id,
            label: `${item.businessTypeCode}-${item.businessTypeName}`,
          };
        });
        this.setState({ wbcBusinessTypeList });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 保存
  onSubmit = e => {
    e.preventDefault();
    const { form, toDetail, params, onClose } = this.props;
    form.validateFields((error, values) => {
      if (!error) {
        const data = {
          ...values,
          businessTypeId: values.businessTypeId.key,
          endProcedureId: values.endProcedureId.key,
          id: params.id,
        };
        this.setState({ saveLoading: true });
        service
          .saveRuleDefinition(data)
          .then(res => {
            message.success(this.$t('structure.saveSuccess')); // 保存成功
            if (params.id) {
              return onClose(true);
            }
            res.data.id && toDetail && toDetail(res.data);
          })
          .catch(err => {
            message.error(err.response.data.message);
            this.setState({ saveLoading: false });
          });
      }
    });
  };

  // 业务类型切换 获取完成时执行 下拉菜单
  businessTypeChange = (value, flag) => {
    this.setState({ selectLoading: true, endProcedureList: [] });
    flag && this.props.form.setFieldsValue({ endProcedureId: undefined });
    service
      .getEndProcedure({ businessTypeId: value.key, procedureType: 'FINISH' })
      .then(res => {
        const endProcedureList = res.data.map(item => {
          return {
            key: item.id,
            label: `${item.procedureCode}-${item.procedureName}`,
          };
        });
        this.setState({ endProcedureList, selectLoading: false });
      })
      .catch(err => message.error(err.response.data.message));
  };

  render() {
    const { wbcBusinessTypeList, endProcedureList, saveLoading, selectLoading } = this.state;
    const { params, setOfBooksId, setOfBooksIdList, onClose } = this.props;
    const { getFieldDecorator } = this.props.form;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };

    return (
      <Form onSubmit={this.onSubmit} style={{ marginTop: '40px' }}>
        <FormItem {...formItemLayout} label={this.$t('chooser.data.setOfBooks') /* 账套 */}>
          {getFieldDecorator('setOfBookId', {
            initialValue: setOfBooksId,
          })(
            <Select disabled>
              {setOfBooksIdList.map(item => <Option key={item.value}>{item.label}</Option>)}
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label={this.$t('workbench.rule.businessType') /* 业务类型 */}>
          {getFieldDecorator('businessTypeId', {
            rules: [
              {
                required: true,
                message: this.$t('common.please.select'),
              },
            ],
            initialValue: params.businessTypeId && {
              key: params.businessTypeId,
              label: params.businessTypeName,
            },
          })(
            <Select
              placeholder={this.$t('common.please.select')}
              onChange={this.businessTypeChange}
              labelInValue
            >
              {wbcBusinessTypeList.map(item => <Option key={item.key}>{item.label}</Option>)}
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label={this.$t('workbench.rule.execute') /* 完成时执行 */}>
          {getFieldDecorator('endProcedureId', {
            rules: [
              {
                required: true,
                message: this.$t('common.please.select'),
              },
            ],
            initialValue: params.endProcedureId && {
              key: params.endProcedureId,
              label: params.procedureName,
            },
          })(
            <Select
              placeholder={this.$t('common.please.select')}
              loading={selectLoading}
              notFoundContent={
                selectLoading ? <Spin size="small" /> : this.$t('agency.setting.no.result')
              }
              labelInValue
            >
              {endProcedureList.map(item => <Option key={item.key}>{item.label}</Option>)}
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label={this.$t('common.column.status')}>
          {getFieldDecorator('enabled', {
            valuePropName: 'checked',
            initialValue: params.id ? params.enabled : true,
          })(<Switch />)}
          <span style={{ paddingLeft: '10px' }}>
            {this.props.form.getFieldValue('enabled')
              ? this.$t('common.status.enable')
              : this.$t('common.status.disable')}
          </span>
        </FormItem>
        <div className="slide-footer">
          <Button type="primary" htmlType="submit" loading={saveLoading}>
            {this.$t('common.save')}
          </Button>
          <Button onClick={() => onClose()}>{this.$t('common.cancel')}</Button>
        </div>
      </Form>
    );
  }
}

const WrappedNewRuleDefinition = Form.create()(NewRuleDefinition);

export default WrappedNewRuleDefinition;
