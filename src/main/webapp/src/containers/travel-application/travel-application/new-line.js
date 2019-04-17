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
import config from 'config';
import moment from 'moment';
import service from './service';

const FormItem = Form.Item;

const priceUnitMap = {
  day: '天',
  week: '周',
  month: '月',
  person: '人',
  ge: '个',
  time: '次',
};

class NewExpenseApplicationFromLine extends Component {
  constructor(props) {
    super(props);
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
      dimensionValues: {},
      //差旅大类ID
      travelTypeCategoryId: '',
    };
  }

  componentWillMount() {
    if (this.props.lineId) {
      this.getEditInfo();
    } else {
      this.getNewInfo();
    }
    this.getTravelExpenseTypesCategory();
  }

  //获取差旅大类ID
  getTravelExpenseTypesCategory = () => {
    service.getExpenseTypesCategoryBySetOfBooksId(this.props.headerData.setOfBooksId).then(res => {
      res.data.map(item => {
        if (item.travelTypeFlag) {
          this.setState({
            travelTypeCategoryId: item.id,
          });
        }
      });
    });
  };

  // 获取默认数据
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

  // 获取编辑默认数据
  getEditInfo = () => {
    const { lineId } = this.props;
    service
      .getNewInfo({ headerId: this.props.headerData.id, lineId, isNew: false })
      .then(res => {
        const expenseTypeInfo = {
          entryMode: res.data.entryMode,
          priceUnit: res.data.priceUnit,
          id: res.data.requisitonTypeId,
          name: res.data.expenseTypeName,
        };
        expenseTypeInfo.fields = res.data.fields;
        res.data.travelPeopleDTOList &&
          res.data.travelPeopleDTOList.map(o => {
            (o.id = o.employeeId), (o.fullName = o.employeeName);
          });
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

  // 上传附件
  handleUpload = OIDs => {
    this.setState({
      uploadOIDs: OIDs,
    });
  };

  // 表单提交
  handleSave = e => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) return;

      if (!values.applicationType.id) {
        message.error('请选择申请类型!');
        return;
      }

      this.setState({ loading: true });

      const { expenseTypeInfo, model, isNew, dimensionList } = this.state;

      const fields = [];
      const dimensions = [];

      Object.keys(values).map(key => {
        if (key.indexOf('-') >= 0) {
          const id = key.split('-')[1];
          const type = key.split('-')[0];

          if (type == 'field') {
            const record = expenseTypeInfo.fields.find(o => o.id == id);
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
            const record = dimensionList.find(o => o.dimensionId == id);
            record.value = values[key];
            dimensions.push(record);
          }
        }
      });
      let travelPeopleDTOList = values.travelPeople.map(o => ({ employeeId: o.id }));
      let params = {
        requisitionHeaderId: this.props.headerData.id,
        requisitionDate: moment().format(),
        description: values.description,
        requisitonTypeId: values.applicationType.id,
        companyId: values.company.length > 0 ? values.company[0].id : '',
        unitId: values.department.length > 0 ? values.department[0].departmentId : '',
        dimensions,
        fields,
        travelPeopleDTOList: travelPeopleDTOList,
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
            message.success('操作成功！');
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
            message.success('操作成功！');
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
        this.props.form.setFieldsValue({ price: 0, quantity: 0 });
      } else {
        this.props.form.setFieldsValue({ amount: 0 });
      }
    });
  };

  // 取消
  onCancel = () => {
    this.props.close && this.props.close();
  };

  // 渲染动态组件
  renderFields = field => {
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 10,
      },
    };

    const { isNew } = this.state;
    const { statusEditable } = this.props;
    const { getFieldDecorator } = this.props.form;

    const rowLayout = { type: 'flex', gutter: 24, justify: 'center' };
    switch (field.fieldType) {
      case 'TEXT':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator(`field-${field.id}`, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                  initialValue: field.value,
                })(<Input disabled={!statusEditable} />)}
              </FormItem>
            </Col>
          </Row>
        );
      case 'DATE':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator(`field-${field.id}`, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                  initialValue: field.value ? moment(field.value) : moment(),
                })(<DatePicker disabled={!statusEditable} />)}
              </FormItem>
            </Col>
          </Row>
        );
      case 'DATETIME':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator(`field-${field.id}`, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                  initialValue: field.value ? moment(field.value) : moment(),
                })(<TimePicker disabled={!statusEditable} />)}
              </FormItem>
            </Col>
          </Row>
        );
      case 'MONTH':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator(`field-${field.id}`, {
                  rules: [{ required: field.required, message: this.$t('common.please.select') }],
                  initialValue: field.value ? moment(field.value) : moment(),
                })(<DatePicker.MonthPicker disabled={!statusEditable} />)}
              </FormItem>
            </Col>
          </Row>
        );
      case 'CUSTOM_ENUMERATION':
        return (
          <Row key={field.id} {...rowLayout}>
            <Col span={24}>
              <FormItem label={field.name} {...formItemLayout}>
                {getFieldDecorator(`field-${field.id}`, {
                  rules: [{ required: true, message: this.$t('common.please.select') }],
                  initialValue: field.value,
                })(
                  <Select disabled={!statusEditable}>
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
                    disabled={!statusEditable}
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
                })(<DatePicker.RangePicker disabled={!statusEditable} />)}
              </FormItem>
            </Col>
          </Row>
        );
    }
  };

  // 校验金额
  checkPrice = (rule, value, callback) => {
    if (value > 0) {
      callback();
      return;
    }
    callback('金额不能小于等于0！');
  };

  // 校验数量
  checkCount = (rule, value, callback) => {
    if (value > 0) {
      callback();
      return;
    }
    callback('不能小于等于0！');
  };

  setDimension = (dimensions, companyId, unitId, userId) => {
    if (dimensions !== null && dimensions.length !== 0) {
      const dimensionIds = dimensions.map(item => item.dimensionId);
      service
        .getDimensionItemsByIds(dimensionIds, companyId, unitId, userId)
        .then(res => {
          const temp = res.data;
          dimensions.forEach(e => {
            const items = temp.find(o => o.id === e.dimensionId).subDimensionItemCOS;
            e.options = items;
            this.props.form.setFieldsValue({ [`dimension-${e.dimensionId}`]: undefined });
          });
          this.setState({
            dimensionList: dimensions,
          });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  // 公司改变
  companyChange = value => {
    this.companyOrUnitChange(value.length > 0 ? value[0].id : '', 'oldId');
  };

  unitChange = value => {
    this.companyOrUnitChange('oldId', value.length > 0 ? value[0].departmentId : '');
  };

  companyOrUnitChange = (companyId, unitId) => {
    const { headerData } = this.props;
    let oldCompany = this.props.form.getFieldValue('company');
    let oldUnit = this.props.form.getFieldValue('department');
    if (companyId === 'oldId') {
      companyId = oldCompany.length > 0 ? oldCompany[0].id : '';
    }
    if (unitId === 'oldId') {
      unitId = oldUnit.length > 0 ? oldUnit[0].departmentId : '';
    }
    this.setDimension(
      this.state.dimensionList,
      companyId ? companyId : headerData.companyId,
      unitId ? unitId : headerData.unitId,
      headerData.employeeId
    );
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const rowLayout = { type: 'flex', gutter: 24, justify: 'center' };
    const {
      pageLoading,
      model,
      isNew,
      dimensionValues,
      dimensionList,
      loading,
      expenseTypeInfo,
      travelTypeCategoryId,
    } = this.state;
    const { lineId, statusEditable } = this.props;
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
                <FormItem label="公司" {...formItemLayout}>
                  {getFieldDecorator('company', {
                    initialValue: !model.companyId
                      ? []
                      : [{ id: model.companyId, name: model.companyName }],
                  })(
                    <Chooser
                      type="company"
                      labelKey="name"
                      valueKey="id"
                      onChange={this.companyChange}
                      showClear={true}
                      disabled={!statusEditable}
                      single
                      listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row {...rowLayout}>
              <Col span={24}>
                <FormItem label="部门" {...formItemLayout}>
                  {getFieldDecorator('department', {
                    initialValue: !model.unitId
                      ? []
                      : [
                          {
                            departmentId: model.unitId,
                            path: model.departmentName,
                          },
                        ],
                  })(
                    <Chooser
                      type="department_document"
                      labelKey="path"
                      valueKey="departmentId"
                      onChange={this.unitChange}
                      showClear={true}
                      disabled={!statusEditable}
                      single
                      listExtraParams={{ tenantId: this.props.user.tenantId }}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row {...rowLayout}>
              <Col span={24}>
                <FormItem label="申请类型" {...formItemLayout}>
                  {getFieldDecorator('applicationType', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: expenseTypeInfo,
                  })(
                    <SelectApplicationType
                      url={`${config.expenseUrl}/api/expense/types/chooser/query`}
                      onChange={this.selectApplicationType}
                      disabled={!statusEditable}
                      params={{
                        setOfBooksId: this.props.headerData.setOfBooksId,
                        typeFlag: 0,
                        typeCategoryId: travelTypeCategoryId,
                      }}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            {expenseTypeInfo.fields &&
              expenseTypeInfo.fields.map(item => {
                return this.renderFields(item);
              })}
            <Row {...rowLayout}>
              <Col span={24}>
                <FormItem label="人员" {...formItemLayout}>
                  {getFieldDecorator('travelPeople', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: !lineId ? [] : model.travelPeopleDTOList,
                  })(
                    <Chooser
                      type="travel_line_user"
                      labelKey="fullName"
                      valueKey="id"
                      disabled={!statusEditable}
                      listExtraParams={{ headerId: this.props.headerData.id }}
                      showClear={false}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            {dimensionList.map(item => {
              return (
                <Row key={item.id} {...rowLayout}>
                  <Col span={24}>
                    <FormItem label={item.name} {...formItemLayout}>
                      {getFieldDecorator(`dimension-${item.dimensionId}`, {
                        rules: [
                          { required: item.requiredFlag, message: this.$t('common.please.select') },
                        ],
                        initialValue: item.value ? item.value : undefined,
                      })(
                        <Select
                          allowClear
                          disabled={!statusEditable}
                          placeholder={this.$t('common.please.select')}
                        >
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
                <FormItem label="备注" {...formItemLayout}>
                  {getFieldDecorator('description', {
                    initialValue: model.description,
                  })(<Input.TextArea disabled={!statusEditable} autosize={{ minRows: 3 }} />)}
                </FormItem>
              </Col>
            </Row>
            {statusEditable && (
              <div className="slide-footer">
                <Button type="primary" htmlType="submit" loading={loading}>
                  {this.$t({ id: 'common.save' }) /* 保存 */}
                </Button>
                <Button onClick={this.onCancel}>
                  {this.$t({ id: 'common.cancel' }) /* 取消 */}
                </Button>
              </div>
            )}
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
