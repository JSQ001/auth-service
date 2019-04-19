import React, { Component } from 'react';
import {
  Form,
  Input,
  Row,
  Col,
  DatePicker,
  Select,
  InputNumber,
  Button,
  message,
  Spin,
  TimePicker,
} from 'antd';
import { connect } from 'dva';
import Chooser from 'widget/chooser';
import SelectApplicationType from 'widget/select-application-type';
import CustomAmount from 'widget/custom-amount';
import service from './service';
const FormItem = Form.Item;
import moment from 'moment';

class NewExpenseApplicationFromLine extends Component {
  constructor(props) {
    super(props);
    this.priceUnitMap = {
      day: this.$t('expense.day') /*天*/,
      week: this.$t('expense.weeks') /*周*/,
      month: this.$t('expense.weeks') /*周*/,
      person: this.$t('expense.people') /*人*/,
      ge: this.$t('expense.a') /*个*/,
      time: this.$t('expense.time') /*次*/,
    };
    this.state = {
      loading: false,
      pageLoading: true,
      isNew: true,
      model: {},
      currencyList: [],
      dimensionList: [],
      typeId: '',
      uploadOIDs: [],
      expenseTypeInfo: {},
    };
  }

  componentDidMount() {
    if (this.props.lineId) {
      this.getEditInfo();
    } else {
      this.getNewInfo();
    }
  }

  //获取默认数据
  getNewInfo = () => {
    service
      .getNewInfo({ headerId: this.props.headerData.id, lineId: '', isNew: true })
      .then(res => {
        this.setState({ model: res.data, pageLoading: false });
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ pageLoading: false });
      });
  };

  //获取编辑默认数据
  getEditInfo = () => {
    const { lineId } = this.props;
    service
      .getNewInfo({ headerId: this.props.headerData.id, lineId: lineId, isNew: false })
      .then(res => {
        let expenseTypeInfo = {
          entryMode: res.data.entryMode,
          priceUnit: res.data.priceUnit,
          id: res.data.expenseTypeId,
          name: res.data.expenseTypeName,
        };
        expenseTypeInfo.fields = res.data.fields;
        this.setState(
          { model: res.data, pageLoading: false, isNew: false, expenseTypeInfo },
          () => {
            expenseTypeInfo.fields.map(o => {
              if (o.fieldType === 'GPS' && o.value) {
                service.getLocalizationCityById(o.value).then(reso => {
                  const description = reso.data.length > 0 ? reso.data[0].description : '';
                  this.props.form.setFieldsValue({
                    [`field-${o.id}`]: [{ id: o.value, description }],
                  });
                });
              } else if (o.fieldType == 'PARTICIPANTS' || o.fieldType == 'PARTICIPANT') {
                const userIdList = o.value ? o.value.split(',') : [];
                service.getUserByIds(userIdList).then(reso => {
                  let userList = reso.data ? reso.data : [];
                  userList.map(user => {
                    user.userId = user.id;
                    user.userName = user.fullName;
                  });
                  this.props.form.setFieldsValue({ [`field-${o.id}`]: userList });
                });
              }
            });
          }
        );
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ pageLoading: false });
      });
  };

  //取消
  onCancel = () => {
    this.props.close && this.props.close();
  };

  //渲染动态组件
  renderFields = field => {
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 10,
      },
    };

    const { getFieldDecorator } = this.props.form;

    const rowLayout = { type: 'flex', gutter: 24, justify: 'center' };
    switch (field.fieldType) {
      case 'TEXT':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator('field-' + field.id, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                  initialValue: field.value,
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
        );
      case 'DATE':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator('field-' + field.id, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                  initialValue: field.value ? moment(field.value) : moment(),
                })(<DatePicker disabled />)}
              </FormItem>
            </Col>
          </Row>
        );
      case 'DATETIME':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator('field-' + field.id, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                  initialValue: field.value ? moment(field.value) : moment(),
                })(<TimePicker disabled />)}
              </FormItem>
            </Col>
          </Row>
        );
      case 'MONTH':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator('field-' + field.id, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                  initialValue: field.value ? moment(field.value) : moment(),
                })(<DatePicker.MonthPicker disabled />)}
              </FormItem>
            </Col>
          </Row>
        );
      case 'CUSTOM_ENUMERATION':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator('field-' + field.id, {
                  rules: [{ required: true, message: this.$t('common.please.select') }],
                  initialValue: field.value,
                })(
                  <Select disabled>
                    {field.options &&
                      field.options.map(o => {
                        return <Select.Option key={o.value}>{o.label}</Select.Option>;
                      })}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
        );
      case 'GPS':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator(`field-${field.id}`, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                })(
                  <Chooser
                    type="select_city"
                    labelKey="description"
                    valueKey="id"
                    single
                    showClear={false}
                    disabled
                  />
                )}
              </FormItem>
            </Col>
          </Row>
        );
      case 'START_DATE_AND_END_DATE':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator(`field-${field.id}`, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                  initialValue: field.value
                    ? [moment(field.value.split(',')[0]), moment(field.value.split(',')[1])]
                    : '',
                })(<DatePicker.RangePicker disabled />)}
              </FormItem>
            </Col>
          </Row>
        );
      case 'PARTICIPANTS':
      case 'PARTICIPANT':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator(`field-${field.id}`, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                })(
                  <Chooser
                    type="select_authorization_user"
                    labelKey="userName"
                    valueKey="userId"
                    listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                    showClear={false}
                    disabled
                  />
                )}
              </FormItem>
            </Col>
          </Row>
        );
      case 'LONG':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator(`field-${field.id}`, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                  initialValue: field.value ? field.value : '',
                })(<InputNumber disabled precision={0} style={{ width: '100%' }} />)}
              </FormItem>
            </Col>
          </Row>
        );
      case 'DOUBLE':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator(`field-${field.id}`, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                  initialValue: field.value ? field.value : '',
                })(<CustomAmount disabled />)}
              </FormItem>
            </Col>
          </Row>
        );
      case 'POSITIVE_INTEGER':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator(`field-${field.id}`, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                  initialValue: field.value ? field.value : '',
                })(<InputNumber disabled min={0} precision={0} style={{ width: '100%' }} />)}
              </FormItem>
            </Col>
          </Row>
        );
    }
  };

  //校验金额
  checkPrice = (rule, value, callback) => {
    if (value > 0) {
      callback();
      return;
    }
    callback(this.$t('expense.amount.less.than.or.equal.to0')); // 金额不能小于等于0！
  };

  //校验数量
  checkCount = (rule, value, callback) => {
    if (value > 0) {
      callback();
      return;
    }
    callback(this.$t('expense.less.than.or.equal.to0')); // 不能小于等于0！
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const rowLayout = { type: 'flex', gutter: 24, justify: 'center' };
    const {
      pageLoading,
      model,
      isNew,
      currencyList,
      dimensionList,
      loading,
      expenseTypeInfo,
    } = this.state;
    const { lineId } = this.props;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };
    return (
      <div style={{ marginBottom: 60, marginTop: 10 }}>
        {pageLoading ? (
          <Spin />
        ) : (
          <Form onSubmit={this.handleSave}>
            <Row {...rowLayout}>
              <Col span={24}>
                <FormItem label={this.$t('expense.the.company')} {...formItemLayout}>
                  {/*公司*/}
                  {getFieldDecorator('company', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: [{ id: model.companyId, name: model.companyName }],
                  })(
                    <Chooser
                      disabled
                      type="company"
                      labelKey="name"
                      valueKey="id"
                      single={true}
                      listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row {...rowLayout}>
              <Col span={24}>
                <FormItem label={this.$t('expense.department')} {...formItemLayout}>
                  {/*部门*/}
                  {getFieldDecorator('department', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: [
                      {
                        departmentId: model.departmentId,
                        path: model.departmentName,
                      },
                    ],
                  })(
                    <Chooser
                      type="department_document"
                      disabled
                      labelKey="path"
                      valueKey="departmentId"
                      single={true}
                      listExtraParams={{ tenantId: this.props.user.tenantId }}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row {...rowLayout}>
              <Col span={24}>
                <FormItem label={this.$t('expense.responsibility.center')} {...formItemLayout}>
                  {/*责任中心*/}
                  {getFieldDecorator('responsibilityCenter', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: model.responsibilityCenterId
                      ? [
                          {
                            id: model.responsibilityCenterId,
                            responsibilityCenterCodeName: model.responsibilityCenterCodeName,
                          },
                        ]
                      : '',
                  })(
                    <Chooser
                      placeholder={this.$t(
                        'expense.please.select.the.default.responsibility.center'
                      )} /*请选择默认责任中心*/
                      disabled
                      type="responsibility_default"
                      labelKey="responsibilityCenterCodeName"
                      valueKey="id"
                      single={true}
                      listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row {...rowLayout}>
              <Col span={24}>
                <FormItem label={this.$t('expense.application.type')} {...formItemLayout}>
                  {/*申请类型*/}
                  {getFieldDecorator('applicationType', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: expenseTypeInfo,
                  })(
                    <SelectApplicationType
                      disabled
                      onChange={this.selectApplicationType}
                      applicationTypeId={this.props.headerData.typeId}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row {...rowLayout}>
              <Col span={24}>
                <FormItem label={this.$t('expense.application.date')} {...formItemLayout}>
                  {/*申请日期*/}
                  {getFieldDecorator('requisitionDate', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: lineId ? moment(model.requisitionDate) : moment(),
                  })(<DatePicker disabled />)}
                </FormItem>
              </Col>
            </Row>
            {!expenseTypeInfo.entryMode && (
              <Row {...rowLayout}>
                <Col span={24}>
                  <FormItem label={this.$t('expense.apply.amount')} {...formItemLayout}>
                    {/*申请金额*/}
                    {getFieldDecorator('amount', {
                      rules: [{ validator: this.checkPrice }],
                      initialValue: model.amount,
                    })(<CustomAmount disabled />)}
                  </FormItem>
                </Col>
              </Row>
            )}
            {expenseTypeInfo.entryMode && (
              <Row {...rowLayout}>
                <Col span={24}>
                  <FormItem label={this.$t('expense.the.unit.price')} {...formItemLayout}>
                    {/*单价*/}
                    {getFieldDecorator('price', {
                      rules: [{ validator: this.checkPrice }],
                      initialValue: model.price || '',
                    })(<InputNumber disabled />)}
                  </FormItem>
                </Col>
              </Row>
            )}
            {expenseTypeInfo.entryMode && (
              <Row {...rowLayout}>
                <Col span={24}>
                  <FormItem
                    label={this.priceUnitMap[expenseTypeInfo.priceUnit]}
                    {...formItemLayout}
                  >
                    {getFieldDecorator('quantity', {
                      rules: [{ validator: this.checkCount }],
                      initialValue: model.quantity,
                    })(<InputNumber disabled precision={0} />)}
                  </FormItem>
                </Col>
              </Row>
            )}
            {expenseTypeInfo.fields &&
              expenseTypeInfo.fields.map(item => {
                return this.renderFields(item);
              })}

            {dimensionList.map(item => {
              return (
                <Row key={item.id} {...rowLayout}>
                  <Col span={24}>
                    <FormItem label={item.dimensionName} {...formItemLayout}>
                      {getFieldDecorator(item.id, {
                        rules: [{ required: true, message: this.$t('common.please.select') }],
                        initialValue: isNew ? item.defaultValue : model.defaultValue,
                      })(
                        <Select disabled>
                          {currencyList.map(item => {
                            return (
                              <Select.Option key={item.currency} value={item.currency}>
                                {item.currency}-{item.currencyName}
                              </Select.Option>
                            );
                          })}
                        </Select>
                      )}
                    </FormItem>
                  </Col>
                </Row>
              );
            })}
            <Row {...rowLayout}>
              <Col span={24}>
                <FormItem label={this.$t('expense.reverse.remark')} {...formItemLayout}>
                  {/*备注*/}
                  {getFieldDecorator('remarks', {
                    initialValue: model.remarks,
                  })(<Input.TextArea disabled autosize={{ minRows: 3 }} />)}
                </FormItem>
              </Col>
            </Row>
            <div
              style={{
                position: 'fixed',
                bottom: 0,
                marginLeft: '-15px',
                width: '100%',
                height: '50px',
                boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.067)',
                background: '#fff',
                lineHeight: '50px',
                textAlign: 'center',
              }}
            >
              <Button onClick={this.props.close}>{this.$t('expense.cancel')}</Button>
            </div>
          </Form>
        )}
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

export default connect(map)(Form.create()(NewExpenseApplicationFromLine));
