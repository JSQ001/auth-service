import React, { Component } from 'react';
import {
  Form,
  Input,
  Row,
  Col,
  Button,
  DatePicker,
  Select,
  InputNumber,
  message,
  Spin,
  TimePicker,
} from 'antd';
import { connect } from 'dva';
import Chooser from 'widget/chooser';
import SelectApplicationType from 'widget/select-application-type';
import CustomAmount from 'widget/custom-amount';
import service from './service';
import config from 'config';
const FormItem = Form.Item;
import moment from 'moment';
import { toDecimal } from 'utils/utils';

class NewExpenseApplicationFromLine extends Component {
  constructor(props) {
    super(props);
    this.priceUnitMap = {
      day: this.$t('expense.day'),
      week: this.$t('expense.weeks'),
      month: this.$t('expense.month'),
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
      companyId: '',
      departmentId: '',
      uploadOIDs: [],
      expenseTypeInfo: {},
      dimensionValues: {},
    };
  }

  componentWillMount() {
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
        this.setState({ model: res.data, pageLoading: false, dimensionList: res.data.dimensions });
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
          {
            model: res.data,
            pageLoading: false,
            isNew: false,
            dimensionList: res.data.dimensions,
            expenseTypeInfo,
          },
          () => {
            expenseTypeInfo.fields.map(o => {
              if (o.fieldType === 'GPS') {
                service.getLocalizationCityById(o.value).then(reso => {
                  const city = reso.data.length > 0 ? reso.data[0].city : '';
                  this.props.form.setFieldsValue({ [`field-${o.id}`]: [{ id: o.value, city }] });
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

  //上传附件
  handleUpload = OIDs => {
    this.setState({
      uploadOIDs: OIDs,
    });
  };

  //表单提交
  handleSave = e => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) return;

      if (!values.applicationType.id) {
        message.error('请选择申请类型!');
        return;
      }

      this.setState({ loading: true });

      let { expenseTypeInfo, model, isNew, dimensionList } = this.state;

      let fields = [];
      let dimensions = [];

      Object.keys(values).map(key => {
        if (key.indexOf('-') >= 0) {
          let id = key.split('-')[1];
          let type = key.split('-')[0];

          if (type == 'field') {
            let record = expenseTypeInfo.fields.find(o => o.id == id);
            if (
              record.fieldType == 'DATE' ||
              record.fieldType == 'DATETIME' ||
              record.fieldType == 'MONTH'
            ) {
              record.value = values[key].format();
            } else if (record.fieldType == 'START_DATE_AND_END_DATE') {
              record.value =
                values[key].length > 0
                  ? values[key][0].format('YYYY-MM-DD') + ',' + values[key][1].format('YYYY-MM-DD')
                  : '';
            } else if (record.fieldType == 'GPS') {
              record.value = values[key].length > 0 ? values[key][0].id : '';
            } else {
              record.value = values[key];
            }
            fields.push(record);
          } else {
            let record = dimensionList.find(o => o.dimensionId == id);
            record.value = values[key];
            dimensions.push(record);
          }
        }
      });

      // Object.keys(values).map(key => {
      //   if (key.indexOf("-") >= 0) {
      //     let dimensionId = key.split("-")[1];
      //     let record = dimensionList.find(o => o.dimensionId == dimensionId);
      //     record.value = values[key].key;
      //   }
      // })
      let params = {
        headerId: this.props.headerData.id,
        requisitionDate: values.requisitionDate.format(),
        remarks: values.remarks,
        expenseTypeId: values.applicationType.id,
        companyId: values.company[0].id,
        departmentId: values.department[0].departmentId,
        responsibilityCenterId: values.responsibilityCenter[0].id,
        dimensions,
        fields,
        amount: values.amount,
      };

      if (expenseTypeInfo.entryMode) {
        params.price = values.price;
        params.quantity = values.quantity;
      } else {
        params.price = 0;
        params.quantity = 0;
      }

      if (!this.state.isNew) {
        params = { ...model, ...params };
        service
          .updateApplicationLine(params)
          .then(res => {
            message.success(this.$t('expense.operation.is.successful1')); /*操作成功！*/
            this.setState({ loading: false });
            this.props.close && this.props.close(true);
          })
          .catch(err => {
            this.setState({ loading: false });
            message.error(err.response.data.message);
          });
      } else {
        service
          .addApplicationLine(params)
          .then(res => {
            message.success(this.$t('expense.operation.is.successful1')); /*操作成功！*/
            this.setState({ loading: false });
            this.props.close && this.props.close(true);
          })
          .catch(err => {
            this.setState({ loading: false });
            message.error(err.response.data.message);
          });
      }
    });
  };

  // 选择申请单
  selectApplicationType = item => {
    console.log(item);
    this.setState({ expenseTypeInfo: item }, () => {
      if (item.entryMode) {
        this.props.form.setFieldsValue({ price: null, quantity: null, amount: null });
      } else {
        this.props.form.setFieldsValue({ amount: null });
      }
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
                })(<Input />)}
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
                })(<DatePicker />)}
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
                })(<TimePicker />)}
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
                })(<DatePicker.MonthPicker />)}
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
                  <Select>
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
                  rules: [{ required: true, message: this.$t('common.please.select') }],
                })(
                  <Chooser
                    type="select_city"
                    labelKey="city"
                    valueKey="id"
                    single
                    listExtraParams={{ code: 'CHN000000000' }}
                    showClear={false}
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
                })(<DatePicker.RangePicker />)}
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

  setDimension = (dimensions, companyId, unitId, userId) => {
    if (dimensions !== null && dimensions.length !== 0) {
      let dimensionIds = dimensions.map(item => item.dimensionId);
      service
        .getDimensionItemsByIds(dimensionIds, companyId, unitId, userId)
        .then(res => {
          let temp = res.data;
          dimensions.forEach(e => {
            let items = temp.find(o => o.id === e.dimensionId)['subDimensionItemCOS'];
            e.options = items;
            this.props.form.setFieldsValue({ ['dimension-' + e.dimensionId]: undefined });
          });
          this.setState({
            dimensionList: dimensions,
          });
        })
        .catch(err => {
          console.log(err);
          message.error(err.response.data.message);
        });
    }
  };

  //公司改变
  companyChange = value => {
    this.setState({ companyId: value[0].id });
    this.companyOrUnitChange(value[0].id, 'oldId');
    this.setExpenseTypeNull();
  };
  // 部门改变
  departmentChange = value => {
    this.setState({ departmentId: value[0].departmentId });
    this.companyOrUnitChange('oldId', value[0].departmentId);
    this.setExpenseTypeNull();
  };

  companyOrUnitChange = (companyId, unitId) => {
    const { headerData } = this.props;
    let oldCompany = this.props.form.getFieldValue('company');
    let oldUnit = this.props.form.getFieldValue('department');
    if (companyId === 'oldId') {
      companyId = oldCompany[0].id;
    }
    if (unitId === 'oldId') {
      unitId = oldUnit[0].departmentId;
    }
    this.setDimension(this.state.dimensionList, companyId, unitId, headerData.employeeId);
  };

  setExpenseTypeNull = () => {
    this.props.form.setFieldsValue({ applicationType: {} });
    this.selectApplicationType({});
  };
  // 单价改变
  priceChange = value => {
    const quantity = this.props.form.getFieldValue('quantity');
    if (!quantity || !value) {
      this.props.form.setFieldsValue({ amount: 0 });
    } else {
      const amount = toDecimal(quantity * value);
      this.props.form.setFieldsValue({ amount });
    }
  };

  // 数量改变
  quantityChange = value => {
    const price = this.props.form.getFieldValue('price');
    if (!price || !value) {
      this.props.form.setFieldsValue({ amount: 0 });
    } else {
      this.props.form.setFieldsValue({ amount: toDecimal(price * value) });
    }
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const rowLayout = { type: 'flex', gutter: 24, justify: 'center' };
    const { pageLoading, model, dimensionList, loading, expenseTypeInfo } = this.state;
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
                      type="company"
                      labelKey="name"
                      valueKey="id"
                      onChange={this.companyChange}
                      showClear={false}
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
                      labelKey="path"
                      valueKey="departmentId"
                      onChange={this.departmentChange}
                      showClear={false}
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
                      url={`${config.expenseUrl}/api/expense/application/type/query/expense/type`}
                      onChange={this.selectApplicationType}
                      params={{
                        applicationTypeId: this.props.headerData.typeId,
                        companyId: this.state.companyId || model.companyId,
                        departmentId: this.state.departmentId || model.departmentId,
                        employeeId: this.props.headerData.employeeId,
                      }}
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
                  })(<DatePicker style={{ width: '100%' }} />)}
                </FormItem>
              </Col>
            </Row>
            {expenseTypeInfo.entryMode && (
              <Row {...rowLayout}>
                <Col span={24}>
                  <FormItem label={this.$t('expense.the.unit.price')} {...formItemLayout}>
                    {/*单价*/}
                    {getFieldDecorator('price', {
                      rules: [{ validator: this.checkPrice }],
                      initialValue: model.price || '',
                    })(<InputNumber onChange={this.priceChange} style={{ width: '100%' }} />)}
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
                    })(
                      <InputNumber
                        onChange={this.quantityChange}
                        precision={0}
                        style={{ width: '100%' }}
                      />
                    )}
                  </FormItem>
                </Col>
              </Row>
            )}
            <Row {...rowLayout}>
              <Col span={24}>
                <FormItem label={this.$t('expense.apply.amount')} {...formItemLayout}>
                  {/*申请金额*/}
                  {getFieldDecorator('amount', {
                    rules: [{ validator: this.checkPrice }],
                    initialValue: model.amount,
                  })(<CustomAmount disabled={expenseTypeInfo.entryMode} />)}
                </FormItem>
              </Col>
            </Row>
            {expenseTypeInfo.fields &&
              expenseTypeInfo.fields.map(item => {
                return this.renderFields(item);
              })}
            {dimensionList.map(item => {
              return (
                <Row key={item.id} {...rowLayout}>
                  <Col span={24}>
                    <FormItem label={item.name} {...formItemLayout}>
                      {getFieldDecorator('dimension-' + item.dimensionId, {
                        rules: [
                          { required: item.requiredFlag, message: this.$t('common.please.select') },
                        ],
                        initialValue: item.value ? item.value : undefined,
                      })(
                        <Select allowClear={true} placeholder={this.$t('common.please.select')}>
                          {item.options.map(option => {
                            return (
                              <Select.Option key={option.id}>
                                {option.dimensionItemName}
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
                  })(<Input.TextArea autosize={{ minRows: 3 }} />)}
                </FormItem>
              </Col>
            </Row>
            <div className="slide-footer">
              <Button type="primary" htmlType="submit" loading={loading}>
                {this.$t({ id: 'common.save' }) /* 保存 */}
              </Button>
              <Button onClick={this.onCancel}>{this.$t({ id: 'common.cancel' }) /* 取消 */}</Button>
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
