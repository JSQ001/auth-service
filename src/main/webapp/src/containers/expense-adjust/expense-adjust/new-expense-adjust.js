/**
 * Created by 13576 on 2017/12/4.
 */
import React from 'react';
import { connect } from 'dva';
import {
  Form,
  Card,
  Input,
  Row,
  Col,
  Affix,
  Button,
  DatePicker,
  Select,
  InputNumber,
  message,
  Spin,
} from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
import config from 'config';
import httpFetch from 'share/httpFetch';
import moment from 'moment';
import Upload from 'widget/upload';
import Chooser from 'widget/chooser';
import expenseAdjustService from 'containers/expense-adjust/expense-adjust/expense-adjust.service';
import { routerRedux } from 'dva/router';
import Lov from 'widget/Template/lov';

class NewExpenseAdjust extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      pageLoading: false,
      user: {},
      fetch: false,
      contractTypeDisabled: true,
      setOfBooksId: null,
      data: [], //编辑的费用维护信息
      currencyOptions: [], //币种
      companyIdOptions: [], //公司
      uploadOids: [], //上传附件的Oids
      employeeOptions: [], //员工选项
      employeeIdOptions: [], //申请人
      unitIdOptions: [], //部门
      currencyLoading: false,
      selectorItem: {},
      extraParams: null,
      deptId: '',
      expenseAdjustType: {},
      model: {},
      fileList: [],
      dimensionList: [],
      defaultUser: {},
    };
  }

  getExpenseAdjustTypeById = () => {
    let expAdjustTypeId = this.props.match.params.expenseAdjustTypeId;
    expenseAdjustService
      .getTypeAndDimension(expAdjustTypeId)
      .then(res => {
        let dimensionList = res.data.dimensions || [];
        this.setState({
          expenseAdjustType: res.data,
          pageLoading: false,
          dimensionList,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /* getDept() {
     expenseAdjustService.getDeptByOid(this.props.user.departmentOid).then(response => {
       this.setState({ deptId: [{ departmentId: response.data.id, name: response.data.name }] });
     });
   }
 */
  componentDidMount() {
    this.getExpenseAdjustTypeById();
    //this.getDept();
    this.getCurrencyOptions();
    if (this.props.match.params.expenseAdjustTypeId) {
      this.listUserByTypeId(this.props.match.params.expenseAdjustTypeId);
    }
    this.props.match.params.id !== 'new' &&
      expenseAdjustService.getExpenseAdjustHeadById(this.props.match.params.id).then(res => {
        let fileList = [];
        if (res.data.attachments) {
          res.data.attachments.map(item => {
            fileList.push({
              ...item,
              uid: item.id,
              name: item.fileName,
              status: 'done',
            });
          });
        }
        let model = res.data;
        model.companyId = [{ id: model.companyId, name: model.companyName }];
        model.unitId = [{ departmentId: model.unitId, name: model.unitName }];
        this.setState(
          {
            model,
            fetch: true,
            fileList,
            uploadOids: res.data.attachmentOidList,
            dimensionList: res.data.dimensions || [],
          },
          () => {
            this.props.form.setFieldsValue({ ...model, employeeId: model.employeeName });
          }
        );
        this.setState({ model, fetch: true, fileList, uploadOids: res.data.attachmentOidList });
      });
  }

  userOrCompanyOrUnitChange = (companyId, unitId, userId) => {
    let oldCompany = this.props.form.getFieldValue('companyId');
    let oldUnit = this.props.form.getFieldValue('unitId');
    let oldUser = this.props.form.getFieldValue('user');
    if (companyId === 'oldId') {
      companyId = oldCompany[0].id;
    }
    if (unitId === 'oldId') {
      unitId = oldUnit[0].departmentId;
    }
    if (userId === 'oldId') {
      userId = oldUser.id;
    }
    this.setDimension(this.state.dimensionList, companyId, unitId, userId);
  };

  companyChange = value => {
    debugger;
    this.userOrCompanyOrUnitChange(value[0].id, 'oldId', 'oldId');
  };

  unitChange = value => {
    this.userOrCompanyOrUnitChange('oldId', value[0].departmentId, 'oldId');
  };

  /**
   * 设置维度
   */
  setDimension = (dimensions, companyId, unitId, userId) => {
    if (dimensions !== null && dimensions.length !== 0) {
      let dimensionIds = dimensions.map(item => item.dimensionId);
      expenseAdjustService
        .getDimensionItemsByIds(dimensionIds, companyId, unitId, userId)
        .then(res => {
          let temp = res.data;
          dimensions.forEach(e => {
            let items = temp.find(o => o.id === e.dimensionId)['subDimensionItemCOS'];
            e.options = items;
            this.props.form.setFieldsValue({ ['dimension-' + e.dimensionId]: undefined });
          });
          this.setState({
            dimensionList: dimensions || [],
          });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  //获取币种
  getCurrencyOptions = () => {
    this.setState({ currencyLoading: true });
    !this.state.currencyOptions.length &&
      this.service.getCurrencyList().then(res => {
        let currencyOptions = res.data;
        this.setState({ currencyOptions, currencyLoading: false });
      });
  };

  //保存
  handleSave = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({ loading: true });
        let dataValue = {
          ...values,
          attachmentOidList: this.state.uploadOids,
          companyId: values.companyId[0].id,
          unitId: values.unitId[0].departmentId,
        };

        let { dimensionList } = this.state;
        Object.keys(values).map(key => {
          if (key.indexOf('-') >= 0) {
            let dimensionId = key.split('-')[1];
            let record = dimensionList.find(o => o.dimensionId == dimensionId);
            record.value = values[key];
          }
        });

        dimensionList.map(o => {
          if (!o.value) {
            o.value = o.defaultValue;
          }
        });

        let method = null;
        if (this.props.match.params.id !== 'new') {
          method = expenseAdjustService.upExpenseAdjustHead;
          dataValue.expAdjustTypeId = this.props.match.params.expenseAdjustTypeId;
          dataValue.id = this.props.match.params.id;
          dataValue.employeeId = this.state.model.employeeId;
          dataValue.dimensions = dimensionList.filter(o => o.headerFlag);
        } else {
          method = expenseAdjustService.addExpenseAdjustHead;
          dataValue = {
            ...dataValue,
            tenantId: this.props.user.tenantId,
            exchangeRate: null,
            status: 1001,
            auditFlag: 1001,
            jeCreationStatus: null,
            jeCreationDate: null,
            expAdjustTypeId: this.props.match.params.expenseAdjustTypeId,
            employeeId: values.user.id,
            dimensions: dimensionList,
          };
        }
        method(dataValue)
          .then(res => {
            message.success(this.$t('common.save.success', { name: '' }));
            this.setState({ loading: false, fileList: [] });
            this.props.dispatch(
              routerRedux.push({
                pathname: '/my-expense-adjust/expense-adjust-detail/:id/:expenseAdjustTypeId/:type'
                  .replace(':expenseAdjustTypeId', res.data.expAdjustTypeId)
                  .replace(':id', res.data.id)
                  .replace(':type', res.data.adjustTypeCategory),
              })
            );
          })
          .catch(e => {
            message.error(`${this.$t('common.save.filed')}，${e.response.data.message}`);
            this.setState({ loading: false });
          });
      }
    });
  };

  userChange = user => {
    expenseAdjustService
      .getUserInfoByTypeId(user.userOid)
      .then(res => {
        let temp = res.data;
        let company = [{ id: temp.companyId, name: temp.companyName }];
        let department = [{ departmentId: temp.departmentId, name: temp.departmentName }];
        this.props.form.setFieldsValue(
          {
            companyId: company,
            unitId: department,
          },
          () => {
            this.userOrCompanyOrUnitChange('oldId', 'oldId', user.id);
          }
        );
      })
      .catch(err => {
        message.error('请求失败，请稍后重试...');
      });
  };

  listUserByTypeId = typeId => {
    expenseAdjustService
      .listUserByTypeId(typeId)
      .then(res => {
        if (res.data) {
          let { defaultUser } = this.state;
          const currentUser = res.data.find(o => o.id === this.props.user.id);
          if (currentUser) {
            defaultUser = currentUser;
          } else {
            defaultUser = res.data[0];
          }
          this.setState({ defaultUser });
          this.userChange(defaultUser);
        }
      })
      .catch(err => {
        message.error('请求失败，请稍后重试...');
      });
  };
  //取消
  onCancel = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname:
          this.props.match.params.id === 'new'
            ? '/my-expense-adjust/my-expense-adjust'
            : `/my-expense-adjust/expense-adjust-detail/${this.props.match.params.id}/${
                this.props.match.params.expenseAdjustTypeId
              }/${this.state.model.adjustTypeCategory}`,
      })
    );
  };
  //上传附件
  handleUpload = Oids => {
    this.setState({ uploadOids: Oids });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      expenseAdjustType,
      model,
      loading,
      fileList,
      deptId,
      pageLoading,
      user,
      contractTypeDisabled,
      data,
      unitIdOptions,
      currencyOptions,
      currencyLoading,
      companyIdOptions,
      selectorItem,
      extraParams,
      defaultUser,
      fetch,
      dimensionList,
    } = this.state;
    const rowLayout = { type: 'flex', gutter: 24, justify: 'center' };
    const formItemLayout = {
      labelCol: {
        xs: { span: 12 },
        sm: { span: 4 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
    };
    let visible = this.props.match.params.id !== 'new' ? fetch : true;
    return visible ? (
      <div className="new-contract" style={{ marginBottom: '40px' }}>
        <Spin spinning={pageLoading}>
          <Form onSubmit={this.handleSave}>
            <Row {...rowLayout}>
              <Col span={10}>
                <FormItem label={this.$t('contract.createdBy')} {...formItemLayout}>
                  {getFieldDecorator('user', {
                    rules: [{ required: true, message: '请选择' }],
                    initialValue:
                      this.props.match.params.id === 'new'
                        ? { id: defaultUser.id, fullName: defaultUser.fullName }
                        : { id: model.employeeId, fullName: model.employeeName },
                  })(
                    <Lov
                      code="adjust_user_authorize"
                      valueKey="id"
                      labelKey="fullName"
                      onChange={this.userChange}
                      allowClear={false}
                      single
                      extraParams={{ adjustTypeId: this.props.match.params.expenseAdjustTypeId }}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row {...rowLayout}>
              <Col span={10}>
                <FormItem label={this.$t('exp.adjust.type')} {...formItemLayout}>
                  {getFieldDecorator('adjustType', {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.select'),
                      },
                    ],
                    initialValue:
                      expenseAdjustType.adjustTypeCategory === '1001'
                        ? this.$t('exp.adjust.exp.detail')
                        : this.$t('exp.adjust.exp.add'),
                  })(
                    <Select
                      style={{ width: '100%' }}
                      disabled={true}
                      placeholder={this.$t('common.please.select')}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row {...rowLayout}>
              <Col span={10}>
                <FormItem
                  label={this.$t('pre.payment.requisition.type.companyName')}
                  {...formItemLayout}
                >
                  {getFieldDecorator('companyId', {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.select'),
                      },
                    ],
                    initialValue:
                      this.props.match.params.id === 'new'
                        ? [{ id: this.props.user.companyId, name: this.props.user.companyName }]
                        : model.id
                          ? model.companyId
                          : [],
                  })(
                    <Chooser
                      type="company"
                      labelKey="name"
                      valueKey="id"
                      single={true}
                      onChange={this.companyChange}
                      listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row {...rowLayout}>
              <Col span={10}>
                <FormItem label={this.$t('budget.occupancy.department')} {...formItemLayout}>
                  {getFieldDecorator('unitId', {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.select'),
                      },
                    ],
                    initialValue:
                      this.props.match.params.id === 'new'
                        ? [
                            {
                              name: this.props.user.departmentName,
                              departmentId: this.props.user.departmentId,
                            },
                          ]
                        : model.id
                          ? model.unitId
                          : [],
                  })(
                    <Chooser
                      type="department"
                      labelKey="name"
                      single={true}
                      valueKey="departmentId"
                      onChange={this.unitChange}
                      listExtraParams={{ tenantId: this.props.user.tenantId }}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row {...rowLayout}>
              <Col span={10}>
                <FormItem label={this.$t('common.currency')} {...formItemLayout}>
                  {getFieldDecorator('currencyCode', {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.select'),
                      },
                    ],
                    initialValue: model.id ? model.currencyCode : 'CNY',
                  })(
                    <Select
                      placeholder={this.$t('common.please.select' /*请选择*/)}
                      //onFocus={this.getCurrencyOptions}
                      disabled={!!model.id}
                      notFoundContent={
                        currencyLoading ? (
                          <Spin size="small" />
                        ) : (
                          this.$t('my.contract.no.result' /*无匹配结果*/)
                        )
                      }
                    >
                      {currencyOptions.map(option => {
                        return (
                          <Option key={option.currency} value={option.currency}>
                            {option.currency}-{option.currencyName}
                          </Option>
                        );
                      })}
                    </Select>
                  )}
                </FormItem>
              </Col>
            </Row>
            {dimensionList.filter(item => item.headerFlag).map(item => {
              return (
                <Row key={item.dimensionId} {...rowLayout}>
                  <Col span={10}>
                    <FormItem label={item.name} {...formItemLayout}>
                      {getFieldDecorator('dimension-' + item.dimensionId, {
                        rules: [
                          {
                            required: item.requiredFlag,
                            message: this.$t('common.please.select'),
                          },
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
              <Col span={10}>
                <FormItem label={this.$t('request.detail.jd.remark')} {...formItemLayout}>
                  {getFieldDecorator('description', {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.enter'),
                      },
                    ],
                    initialValue: model.id ? model.description : '',
                  })(<Input.TextArea placeholder={this.$t('common.please.enter')} />)}
                </FormItem>
              </Col>
            </Row>
            <Row {...rowLayout}>
              <Col span={10}>
                <FormItem label={this.$t('exp.attachment.info')} {...formItemLayout}>
                  {getFieldDecorator('attachmentOid')(
                    <Upload
                      attachmentType="EXP_ADJUST"
                      uploadUrl={`${config.baseUrl}/api/upload/static/attachment`}
                      fileNum={9}
                      uploadHandle={this.handleUpload}
                      defaultFileList={fileList}
                      defaultOids={this.state.uploadOids}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Affix
              offsetBottom={0}
              style={{
                position: 'fixed',
                bottom: 0,
                marginLeft: '-35px',
                width: '100%',
                height: '50px',
                boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.067)',
                background: '#fff',
                lineHeight: '50px',
              }}
            >
              <Button
                type="primary"
                htmlType="submit"
                loading={loading}
                style={{ margin: '0 20px' }}
              >
                {this.$t('my.contract.next')}
              </Button>
              <Button onClick={this.onCancel}>{this.$t('common.cancel')}</Button>
            </Affix>
          </Form>
        </Spin>
      </div>
    ) : null;
  }
}

const wrappedNewExpenseAdjust = Form.create()(NewExpenseAdjust);

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
    languages: state.languages,
  };
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedNewExpenseAdjust);
