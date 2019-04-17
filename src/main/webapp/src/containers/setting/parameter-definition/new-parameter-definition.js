/**
 * Created by 14306 on 2018/12/26.
 */
import React from 'react';
import { connect } from 'dva';
import moment from 'moment';
import {
  Form,
  Input,
  Switch,
  Button,
  Col,
  Row,
  Select,
  DatePicker,
  Alert,
  notification,
  Icon,
  message,
  InputNumber,
} from 'antd';
import parameterService from 'containers/setting/parameter-definition/parameter-definition.service';
import config from 'config';

import 'styles/budget-setting/budget-organization/budget-versions/new-budget-versions.scss';
import Chooser from 'components/Widget/chooser';
import CustomAmount from 'components/Widget/custom-amount';
const Option = Select.Option;
const FormItem = Form.Item;

class NewParameterDefinition extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      versionCodeError: false,
      statusError: false,
      newData: [],
      moduleOptions: [],
      paramsOptions: [],
      paramCode: {},
      paramValueOptions: [],
      version: {},
      statusOptions: [],
      checkoutCodeData: [],
      loading: false,
      parameterIdDisabled: true,
    };
  }

  componentWillMount() {
    if (this.props.params.record.id) {
      this.setState({
        paramCode: {
          parameterValueType: this.props.params.record.parameterValueType,
        },
      });
      this.handleParamValue(true, this.props.params.record.parameterCode);
    }
  }

  handleSave = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({ loading: true });
        const record = this.props.params.record;
        let method;
        let flag = !!this.props.params.record.id;
        values.parameterLevel =
          this.props.params.nowTab === '1'
            ? 'SOB'
            : this.props.params.nowTab === '2'
              ? 'COMPANY'
              : 'TENANT';
        values.tenantId = this.props.company.tenantId;
        values.setOfBooksId && (values.setOfBooksId = values.setOfBooksId.key);
        values.companyId && (values.companyId = values.companyId.key);
        if (flag) {
          //编辑
          method = parameterService.updateParameter;
          values.versionNumber = record.versionNumber;
          values.id = record.id;
          values.moduleCode = record.moduleCode;
          values.parameterId === record.parameterCode && (values.parameterId = record.parameterId);
          values.parameterValueId === record.parameterValue &&
            (values.parameterValueId = record.parameterValueId);
          values.parameterValueId instanceof Array &&
            (values.parameterValueId = values.parameterValueId[0].id);
        } else {
          method = parameterService.newParameter;

          if (
            this.state.paramCode.parameterValueType === 'VALUE_LIST' &&
            Number(values.parameterValueId).toString() === 'NaN'
          ) {
            values.parameterValueId = this.state.paramValueOptions.find(
              item => item.code === values.parameterValueId
            ).id;
          }
          this.props.params.nowTab === '1' && (values.setOfBooksId = this.props.params.sob.id);
          values.parameterValueId instanceof Array &&
            (values.parameterValueId = values.parameterValueId[0].id);
        }
        method(values)
          .then(res => {
            if (res.status === 200) {
              this.props.onClose(true);
              message.success(this.$t('common.save.success', { name: '' }));
            }
          })
          .catch(e => {
            this.setState({ loading: false });
            message.error(this.$t('common.save.filed'));
          });
      }
    });
  };

  handleModule = () => {
    this.state.moduleOptions.length === 0 &&
      parameterService.getModule().then(res => {
        this.setState({
          moduleOptions: res.data,
        });
      });
  };

  //模块代码改变时，重置相关值
  handleModuleChange = value => {
    if (value) {
      this.setState(
        {
          paramCode: {},
          parameterIdDisabled: false,
        },
        () => {
          let params = {
            parameterLevel:
              this.props.params.nowTab === '1'
                ? 'SOB'
                : this.props.params.nowTab === '2'
                  ? 'COMPANY'
                  : 'TENANT',
            moduleCode: value,
            selectId:
              this.props.params.nowTab === '1'
                ? this.props.params.sob.id
                : this.props.params.nowTab === '2'
                  ? this.props.params.company.id
                  : null,
          };
          parameterService.getParamByModuleCode(params).then(res => {
            this.setState({
              paramsOptions: res.data,
            });
          });

          this.props.form.setFieldsValue({
            parameterId: null,
            parameterName: null,
            parameterValueId: null,
            parameterValueDesc: null,
          });
        }
      );
    }
  };

  handleParamChange = value => {
    let param = this.state.paramsOptions.find(item => item.id === value);
    let defaultParamValueCode = null;
    param.defaultParamValueDto &&
      (defaultParamValueCode =
        param.parameterValueType === 'API'
          ? [{ id: param.defaultParamValueDto.id, code: param.defaultParamValueDto.code }]
          : param.defaultParamValueDto.code);
    this.handleParamValue(value);
    this.props.form.resetFields(['parameterValueId']);
    this.setState({ paramCode: param }, () => {
      this.props.form.setFieldsValue({
        parameterName: param.parameterName,
        parameterValueId: defaultParamValueCode,
        parameterValueDesc: param.defaultParamValueDto ? param.defaultParamValueDto.name : null,
      });
    });
  };

  handleParamValue = (param, code) => {
    let parameterId = this.props.form.getFieldValue('parameterId');
    parameterId === this.props.params.record.parameterCode &&
      (parameterId = this.props.params.record.parameterId);
    param && (parameterId = param);
    let params = {
      //parameterValueType: this.state.paramsOptions.find( item=> item.id === parameterId ).parameterValueType,
      parameterCode: code
        ? code
        : this.state.paramsOptions.find(item => item.id === parameterId).parameterCode,
    };
    parameterService.getParamValues(params).then(res => {
      this.setState({
        paramValueOptions: res.data,
      });
    });
  };

  handleParamValueChange = value => {
    this.props.form.setFieldsValue({
      parameterValueDesc: this.state.paramValueOptions.find(
        item => item.id.toString() === value.toString()
      ).name,
    });
  };

  onCancel = () => {
    this.props.form.resetFields();
    this.props.onClose();
  };

  handleAPI = value => {
    value.length && this.props.form.setFieldsValue({ parameterValueDesc: value[0].name });
    if (value && value.length === 0) {
      this.props.form.setFieldsValue({ parameterValueId: null });
    }
  };

  //根据所选参数代码渲染不同参数值框
  renderParamValue() {
    const { paramCode, paramValueOptions } = this.state;
    const record = this.props.params.record;
    const disabled = record.id ? false : !this.props.form.getFieldValue('parameterId');
    switch (paramCode.parameterValueType) {
      case 'API': {
        let selectorItem = {
          title: '参数值',
          url: `${config.mdataUrl}/api/parameter/values/api/by/parameterValueType`,
          searchForm: [
            {
              type: 'input',
              id: 'code',
              label: this.$t('common.code'),
            },
            { type: 'input', id: 'name', label: this.$t('common.name') },
          ],
          columns: [
            { title: this.$t('common.code'), dataIndex: 'code' },
            { title: this.$t('common.name'), dataIndex: 'name' },
          ],
          key: 'id',
        };

        let parameterId = this.props.form.getFieldValue('parameterId');
        parameterId === this.props.params.record.parameterCode &&
          (parameterId = this.props.params.record.parameterId);

        let parameterCode = this.props.params.record.id
          ? this.props.params.record.parameterCode
          : this.state.paramsOptions.find(item => item.id === parameterId).parameterCode;

        let params;
        switch (this.props.params.nowTab) {
          case '1': {
            params = {
              parameterCode: parameterCode,
              parameterLevel: 'SOB',
              setOfBooksId: this.props.params.sob.id,
            };
            break;
          }
          case '2': {
            params = {
              parameterCode: parameterCode,
              parameterLevel: 'COMPANY',
              companyId: this.props.params.company.id,
            };
            break;
          }
          case '0': {
            params = {
              parameterCode: parameterCode,
              parameterLevel: 'TENANT',
            };
            break;
          }
        }
        return (
          <Chooser
            showClear={false}
            single={true}
            labelKey="code"
            valueKey={this.props.params.nowTab === '0' ? 'code' : 'id'}
            disabled={disabled}
            onChange={this.handleAPI}
            listExtraParams={params}
            selectorItem={selectorItem}
          />
        );
      }
      case 'VALUE_LIST': {
        return (
          <Select
            placeholder={this.$t('common.please.select')}
            disabled={disabled}
            onChange={this.handleParamValueChange}
            //onFocus={this.handleParamValue}
          >
            {paramValueOptions.map(item => {
              return <Option key={item.id}>{item.code}</Option>;
            })}
          </Select>
        );
      }
      case 'TEXT': {
        return <Input.TextArea placeholder={this.$t('common.please.enter')} />;
      }
      case 'NUMBER': {
        return (
          <InputNumber style={{ width: '100%' }} placeholder={this.$t('common.please.enter')} />
        );
      }
      case 'DATE': {
        return <DatePicker style={{ width: '100%' }} />;
      }
      case 'DOUBLE': {
        return <CustomAmount style={{ width: '100%' }} />;
      }
    }

    return <Select placeholder={this.$t('common.please.select')} disabled />;
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { record, sob, nowTab, company } = this.props.params;
    const { moduleOptions, paramsOptions, paramCode, parameterIdDisabled } = this.state;

    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10, offset: 0 },
    };

    let isVisible = {
      API: true,
      VALUE_LIST: true,
    };

    const defaultParameterValue = record.parameterValue
      ? record.parameterValueType === 'DATE'
        ? moment(record.parameterValue)
        : record.parameterValueType === 'API'
          ? [
              {
                id: record.parameterValueId,
                code: record.parameterValue,
                name: record.parameterValueDesc,
              },
            ]
          : record.parameterValue || ''
      : null;

    return (
      <div className="new-parameter-definition" style={{ paddingTop: 25 }}>
        <Form onSubmit={this.handleSave}>
          <FormItem {...formItemLayout} label={this.$t({ id: 'parameter.definition.model' })}>
            {getFieldDecorator('moduleCode', {
              initialValue: record.moduleName || '',
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.select' }),
                },
              ],
            })(
              <Select
                disabled={!!record.id}
                onChange={this.handleModuleChange}
                placeholder={this.$t({ id: 'common.please.select' })}
                onFocus={this.handleModule}
              >
                {moduleOptions.map(item => (
                  <Option key={item.moduleCode}>{item.moduleName}</Option>
                ))}
              </Select>
            )}
          </FormItem>
          {nowTab === '1' && (
            <FormItem {...formItemLayout} label={this.$t({ id: 'workflow.set.of.books' })}>
              {getFieldDecorator('setOfBooksId', {
                initialValue: sob,
              })(<Select labelInValue disabled />)}
            </FormItem>
          )}
          {nowTab === '2' && (
            <FormItem {...formItemLayout} label={this.$t({ id: 'exp.company' })}>
              {getFieldDecorator('companyId', {
                initialValue: { key: company.id, label: company.name },
              })(<Select labelInValue disabled />)}
            </FormItem>
          )}
          <FormItem {...formItemLayout} label={this.$t({ id: 'budget.parameterCode' })}>
            {getFieldDecorator('parameterId', {
              initialValue: record.parameterCode || '',
              rules: [{ required: true, message: this.$t({ id: 'common.please.enter' }) }],
            })(
              <Select
                disabled={parameterIdDisabled}
                placeholder={this.$t({ id: 'common.please.select' })}
                onChange={this.handleParamChange}
              >
                {paramsOptions.map(item => <Option key={item.id}>{item.parameterCode}</Option>)}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t({ id: 'budget.parameterName' })}>
            {getFieldDecorator('parameterName', {
              initialValue: record.parameterName || '',
              //rules: [{required: true, message: this.$t({id: "common.please.enter"})}],
            })(<Input disabled placeholder={this.$t({ id: 'common.please.enter' })} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t({ id: 'budget.balance.params.value' })}>
            {getFieldDecorator('parameterValueId', {
              initialValue: defaultParameterValue,
            })(this.renderParamValue())}
          </FormItem>
          {isVisible[paramCode.parameterValueType] && (
            <FormItem {...formItemLayout} label={this.$t({ id: 'chooser.data.description' })}>
              {getFieldDecorator('parameterValueDesc', {
                initialValue: record.parameterValueDesc,
              })(<Input disabled placeholder={this.$t({ id: 'common.please.enter' })} />)}
            </FormItem>
          )}
          <div className="slide-footer">
            <Button type="primary" htmlType="submit" loading={this.state.loading}>
              {this.$t({ id: 'common.save' })}
            </Button>
            <Button onClick={this.onCancel}>{this.$t({ id: 'common.cancel' })}</Button>
          </div>
        </Form>
      </div>
    );
  }
}

const WrappedNewParameterDefinition = Form.create()(NewParameterDefinition);

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewParameterDefinition);
