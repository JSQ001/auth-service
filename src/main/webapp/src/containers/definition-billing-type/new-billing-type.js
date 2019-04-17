import React from 'react';
import { connect } from 'react-redux';
import config from 'config';
import {
  Form,
  Switch,
  Icon,
  Input,
  Select,
  Button,
  Spin,
  Radio,
  Tooltip,
  message,
  Checkbox,
} from 'antd';
import PermissionsAllocation from 'widget/Template/permissions-allocation';
import CustomChooser from 'components/Template/custom-chooser';

import Service from './service';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

const type = {
  1001: 'all',
  1002: 'department',
  1003: 'group',
};
const permissionsType = {
  all: '1001',
  department: '1002',
  group: '1003',
};

class NewBillingType extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      saveLoading: false,
      loading: !!props.params.id,
      setOfBooks: [],
      formList: [],
      record: {},
      applicationFormBasis: '',
      paymentFormBasis: '',
      isDisable: false,
      associateContractDisplay: '',
      payValuesType: [],
      visibleList: [],
      setOfBooksName: '',
      //添加付款用途显示模态框
      addPaymentVisible: false,
      // 费用类型
      costTypeSelectorItem: {
        title: this.$t('adjust.choose.expense.type') /**选择费用类型 */,
        url: `${config.expenseUrl}/api/expense/types/query/by/document/assign`,
        searchForm: [
          {
            type: 'input',
            id: 'code',
            label: this.$t('adjust.expense.type.code') /**费用类型代码 */,
          },
          {
            type: 'input',
            id: 'name',
            label: this.$t('adjust.expense.type.name') /**费用类型名称 */,
          },
          {
            type: 'select',
            id: 'range',
            label: this.$t('expense.watch') /**查看 */,
            options: [
              { value: 'all', label: this.$t('adjust.all.selected') } /**全选 */,
              { value: 'selected', label: this.$t('adjust.selected') } /**已选 */,
              { value: 'notChoose', label: this.$t('adjust.not.selected') } /**未选 */,
            ],
            allowClear: false,
            colSpan: '8',
            defaultValue: 'all',
          },
          {
            type: 'select',
            id: 'typeCategoryId',
            label: this.$t('exp.type.of.category'),
            options: [],
            getUrl: `${config.expenseUrl}/api/expense/types/category`,
            labelKey: 'name',
            valueKey: 'id',
            method: 'get',
            getParams: { setOfBooksId: this.props.setOfBooksId },
            renderOption: data => `${data.name}`,
          },
        ],
        columns: [
          {
            title: this.$t('adjust.expense.type.icon'),
            dataIndex: 'iconUrl',
            align: 'center',
            render: value => {
              return <img src={value} height="24" width="24" />;
            },
          },
          { title: this.$t('adjust.expense.type.code'), dataIndex: 'code' },
          { title: this.$t('adjust.expense.type.name'), dataIndex: 'name' },
          { title: this.$t('expense.type.expense.group.name'), dataIndex: 'typeCategoryName' },
        ],
        key: 'id',
      },
    };
  }

  componentDidMount() {
    this.getInfoById();
    this.getWorkflowList();
    this.getPayTypeOptions();
  }

  // 获取审批流
  getWorkflowList = () => {
    let params = {
      setOfBooksId: this.props.setOfBooksId,
    };
    Service.getWorkflowList(params)
      .then(({ data }) => {
        let list = [];
        data.map(item => {
          list.push({ value: item.formId, label: `${item.formName}`, formType: item.formType });
        });
        this.setState({ formList: list });
      })
      .catch(err => {
        message.error(err.response.message);
      });
  };

  //获取详情
  getInfoById = () => {
    if (this.props.params.id) {
      Service.getInfoById(this.props.params.id)
        .then(res => {
          let record = { ...res.data.expenseReportType };
          record.expenseTypeIdList = {
            radioValue: record.allExpenseFlag,
            chooserValue: res.data.expenseTypeIdList
              ? res.data.expenseTypeIdList.map(o => ({ id: o }))
              : [],
          };
          record.cashTransactionClassIdList = {
            radioValue: record.allCashTransactionClass,
            chooserValue: res.data.cashTransactionClassIdList
              ? res.data.cashTransactionClassIdList.map(o => ({ id: o }))
              : [],
          };
          record.applyEmployee != '1001' &&
            (record.departmentOrUserGroupIdList = {
              type: type[Number(record.applyEmployee)],
              values: res.data.departmentOrUserGroupList
                ? res.data.departmentOrUserGroupList.map(o => ({
                    key: o.id,
                    label: o.name,
                    value: o.id,
                  }))
                : [],
            });

          this.setState({
            loading: false,
            record: record,
            associateContractDisplay: record.associateContract,
          });
        })
        .catch(err => {
          message.error('获取数据失败');
        });
    }
  };

  // 付款方式类型  在线/落地文件/线下
  getPayTypeOptions() {
    Service.getPayValueList(2105).then(res => {
      //状态
      let statusOptions = res.data || [];
      this.setState({
        payValuesType: statusOptions,
      });
    });
  }

  // 费用类型设置

  // 预算款设置

  // 关联申请单依据的单选按钮
  onApplicationFormBasisChange = e => {
    this.setState({ applicationFormBasis: e.target.value });
  };

  //关联合同变化
  associateContractChange = e => {
    if (e.target.value == true) {
      // this.props.form.setFieldsValue({ contractRequired: true });
      this.setState({ isDisable: false, associateContractDisplay: e.target.value });
    } else {
      // this.props.form.setFieldsValue({ contractRequired: true });
      this.state.record.contractRequired = false;
      this.setState({ isDisable: true, associateContractDisplay: e.target.value });
    }
  };

  // 收付款方单选
  paymentChange = e => {
    this.setState({ paymentFormBasis: e.target.value });
  };

  onCancel = () => {
    this.props.close && this.props.close();
  };

  handleSave = e => {
    e.preventDefault();
    let { record } = this.state;
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (err) return;
      this.setState({ saveLoading: true });
      let departmentOrUserGroupIdList = values.departmentOrUserGroupIdList.values.map(o => o.key);
      let cashTransactionClassIdList = [];
      cashTransactionClassIdList = values.cashTransactionClassIdList.chooserValue.map(o => o.id);
      let expenseTypeIdList = [];
      expenseTypeIdList = values.expenseTypeIdList.chooserValue.map(o => o.id);

      values.allExpenseFlag = values.expenseTypeIdList.radioValue;
      values.allCashTransactionClass = values.cashTransactionClassIdList.radioValue;
      values.applyEmployee = permissionsType[values.departmentOrUserGroupIdList.type];
      if (values.allExpenseFlag == false && expenseTypeIdList.length == 0) {
        message.warning('请选择至少一个可用费用');
        this.setState({ saveLoading: false });
        return;
      }
      if (values.allCashTransactionClass == false && cashTransactionClassIdList.length == 0) {
        message.warning('请选择至少一个付款用途');
        this.setState({ saveLoading: false });
        return;
      }
      if (values.applyEmployee === '1002' && departmentOrUserGroupIdList.length == 0) {
        message.warning('请选择至少一个可用部门');
        this.setState({ saveLoading: false });
        return;
      }
      if (values.applyEmployee === '1003' && departmentOrUserGroupIdList.length == 0) {
        message.warning('请选择至少一个可用人员组');
        this.setState({ saveLoading: false });
        return;
      }
      delete values.cashTransactionClassIdList;
      delete values.expenseTypeIdList;
      delete values.departmentOrUserGroupIdList;

      let _values = {
        expenseReportType: { formType: 2, tenantId: this.props.tenantId, ...values },
        departmentOrUserGroupIdList,
        cashTransactionClassIdList,
        expenseTypeIdList,
      };

      let method = Service.addApplicationType;

      if (record.id) {
        method = Service.updateApplicationType;
        _values = {
          expenseReportType: {
            id: record.id,
            formType: 2,
            tenantId: this.props.tenantId,
            versionNumber: record.versionNumber,
            contractRequired: record.contractRequired ? record.contractRequired : false,
            ...values,
          },
          departmentOrUserGroupIdList,
          cashTransactionClassIdList,
          expenseTypeIdList,
        };
      }
      method(_values)
        .then(res => {
          this.setState({ saveLoading: false });
          message.success(
            record.id ? this.$t('common.update.success') : this.$t('exp.create.success')
          );
          this.props.close && this.props.close(true);
        })
        .catch(err => {
          this.setState({ saveLoading: false });
          message.error(err.response.data.message);
        });
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      formList,
      saveLoading,
      loading,
      record,
      isDisable,
      associateContractDisplay,
      payValuesType,
      costTypeSelectorItem,
    } = this.state;
    const { params, setOfBooks, setOfBooksId } = this.props;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };
    const itemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10, offset: 8 },
    };

    const radioStyle = {
      display: 'block',
      height: '40px',
      lineHeight: '42px',
    };
    // 付款用途弹框
    const paymentPurposeItem = {
      title: this.$t('exp.payment.purpose'),
      url: `${config.expenseUrl}/api/expense/report/type/query/transaction/class`,
      searchForm: [
        {
          type: 'select',
          id: 'setOfBooksId',
          label: this.$t('expense.policy.setOfBooksName'),
          colSpan: 8,
          disabled: true,
          options: setOfBooks,
          defaultValue: setOfBooksId,
        },
        {
          type: 'input',
          id: 'name',
          label: '付款用途名称' /**付款用途名称 */,
          colSpan: 8,
          placeholder: this.$t('common.enter'),
        },
        {
          type: 'select',
          id: 'range',
          label: this.$t('expense.watch'),
          options: [
            { value: 'all', label: this.$t('adjust.all.selected') },
            { value: 'selected', label: this.$t('adjust.selected') },
            { value: 'notChoose', label: this.$t('adjust.not.selected') },
          ],
          allowClear: false,
          colSpan: '8',
          defaultValue: 'all',
        },
      ],
      columns: [
        {
          title: this.$t('exp.payment.purpose.code'),
          dataIndex: 'classCode',
          align: 'center',
        },
        {
          title: '付款用途名称',
          dataIndex: 'description',
          align: 'center',
        },
      ],
      key: 'id',
    };

    return (
      <div>
        {loading ? (
          <Spin />
        ) : (
          <Form onSubmit={this.handleSave}>
            {/* 基本信息开始 */}
            <div className="common-item-title">
              {this.$t({ id: 'pre.payment.essential.information' }) /*基本信息*/}
            </div>
            <FormItem
              {...formItemLayout}
              label={this.$t({ id: 'pre.payment.setOfBookName' } /*账套*/)}
            >
              {getFieldDecorator('setOfBooksId', {
                rules: [{ required: true, message: this.$t('common.please.enter') }],
                initialValue: record.id ? record.setOfBooksId : setOfBooksId,
              })(
                <Select disabled>
                  {setOfBooks.map(option => {
                    return (
                      <Option key={option.value} value={option.value}>
                        {option.label}
                      </Option>
                    );
                  })}
                </Select>
              )}
            </FormItem>

            <FormItem {...formItemLayout} label={this.$t('billing.type.code')} /**报账单类型代码 */>
              {getFieldDecorator('reportTypeCode', {
                rules: [{ required: true, message: this.$t('common.please.enter') }],
                initialValue: record.reportTypeCode || '',
              })(<Input disabled={!!params.id} />)}
            </FormItem>

            <FormItem {...formItemLayout} label={this.$t('billing.type.name')} /**报账单类型名称 */>
              {getFieldDecorator('reportTypeName', {
                rules: [{ required: true, message: this.$t('common.please.enter') }],
                initialValue: record.reportTypeName || '',
              })(<Input />)}
            </FormItem>

            <FormItem {...formItemLayout} label={this.$t('billing.exp.flowName')} /**审批流 */>
              {getFieldDecorator('formId', {
                rules: [{ required: true, message: this.$t('common.please.enter') }],
                initialValue: record.formId || '',
              })(
                <Select allowClear>
                  {formList.map(item => {
                    return (
                      <Option key={item.value} value={item.value}>
                        {item.label}
                      </Option>
                    );
                  })}
                </Select>
              )}
            </FormItem>

            <FormItem {...formItemLayout} label={this.$t('common.column.status')} /**状态 */>
              {getFieldDecorator('enabled', {
                initialValue: record.id ? record.enabled : true,
                valuePropName: 'checked',
              })(
                <Switch
                  checkedChildren={<Icon type="check" />}
                  unCheckedChildren={<Icon type="close" />}
                />
              )}
            </FormItem>
            {/* 费用类型设置 */}
            <div className="common-item-title"> {this.$t('adjust.expense.type.set')}</div>
            <FormItem
              {...formItemLayout}
              validateTrigger="onBlur"
              label={this.$t('billing.available.expense.typ')}
            >
              {getFieldDecorator('expenseTypeIdList', {
                initialValue: record.expenseTypeIdList || { radioValue: true, chooserValue: [] },
              })(
                <CustomChooser
                  valueKey="id"
                  labelKey="name"
                  showDetail={false}
                  selectorItem={costTypeSelectorItem}
                  params={{
                    setOfBooksId: setOfBooksId,
                    // range:'all',
                    documentType: 801001,
                    id: record.id ? record.id : '',
                    typeFlag: 1,
                  }}
                />
              )}
            </FormItem>
            {/* 预算管控设置 */}
            <div className="common-item-title">{this.$t('exp.budget.control.settings')} </div>
            <FormItem {...formItemLayout} colon={false} label={<span />}>
              {getFieldDecorator('budgetFlag', {
                initialValue: record.id ? record.budgetFlag : false,
              })(
                <Radio.Group>
                  <Radio value={true}>{this.$t('common.enabled')}</Radio>
                  <Radio value={false}>{this.$t('exp.not.enabled')}</Radio>
                </Radio.Group>
              )}
            </FormItem>
            {/* 关联申请单设置 */}
            <div className="common-item-title">
              {this.$t('exp.relevant.application.form.settings')}
            </div>
            <FormItem
              {...formItemLayout}
              label={this.$t('exp.relevant.application.form.settings')}
              colon={false}
            >
              {getFieldDecorator('applicationFormBasis', {
                initialValue: record.id ? record.applicationFormBasis : 'HEADER_COM',
              })(
                <RadioGroup>
                  <Radio style={radioStyle} value="HEADER_COM">
                    {this.$t('exp.billing.equals.application')}
                  </Radio>
                  <Radio style={radioStyle} value="HEADER_DEPARTMENT">
                    {this.$t('exp.billing.equals.department')}
                  </Radio>
                  <Radio style={radioStyle} value="COM_DEP">
                    {this.$t('exp.billing.and.department.equals.heard')}
                  </Radio>
                  <Radio style={radioStyle} value="HEADER_EMPLOYEE">
                    {this.$t('exp.billing.equals.head')}
                  </Radio>
                </RadioGroup>
              )}
            </FormItem>
            {/* 关联合同设置 */}
            <div className="common-item-title">{this.$t('exp.associated.contract.settin')}</div>
            <FormItem
              {...formItemLayout}
              colon={false}
              label={this.$t('exp.not.or.associate')} /**是否关联 */
            >
              {getFieldDecorator('associateContract', {
                initialValue: record.id ? record.associateContract : false,
              })(
                <Radio.Group onChange={this.associateContractChange}>
                  <Radio value={true}>{this.$t('exp.associated')}</Radio>
                  <Radio value={false}>{this.$t('exp.not.associated')}</Radio>
                </Radio.Group>
              )}
            </FormItem>
            {associateContractDisplay && (
              <FormItem {...formItemLayout} colon={false} label="合同是否必输" /**是否关联 */>
                {getFieldDecorator('contractRequired', {
                  initialValue:
                    record.id && record.contractRequired ? record.contractRequired : false,
                })(
                  <Radio.Group disabled={!!isDisable}>
                    <Radio value={true}>{this.$t('exp.must.input')}</Radio>
                    <Radio value={false}>{this.$t('exp.not.input')}</Radio>
                  </Radio.Group>
                )}
              </FormItem>
            )}
            {/* 收款方设置 */}
            <div className="common-item-title">{this.$t('exp.recipient.settin')}</div>
            <FormItem
              {...formItemLayout}
              colon={false}
              label={this.$t('exp.recipient.settin')} /**收款方设置 */
            >
              {getFieldDecorator('multiPayee', {
                initialValue: record.id ? record.multiPayee : true,
              })(
                <Radio.Group onChange={this.paymentChange}>
                  <Radio value={true}>{this.$t('exp.recipient.more')}</Radio>
                  <Radio value={false}>{this.$t('exp.recipient.single')}</Radio>
                </Radio.Group>
              )}
            </FormItem>
            <FormItem {...formItemLayout} label={this.$t('exp.recipient.attribut')} colon={false}>
              {getFieldDecorator('payeeType', {
                initialValue: record.id ? record.payeeType : 'BOTH',
              })(
                <RadioGroup>
                  <Radio style={radioStyle} value="EMPLOYEE">
                    {this.$t('exp.only.employee')}
                  </Radio>
                  <Radio style={radioStyle} value="VENDER">
                    {this.$t('exp.vender')}
                  </Radio>
                  <Radio style={radioStyle} value="BOTH">
                    {this.$t('exp.employee.and.vender')}
                  </Radio>
                </RadioGroup>
              )}
            </FormItem>
            {/* 付款用途 */}
            <div className="common-item-title"> {this.$t('exp.payment.purpose')}</div>
            <FormItem
              {...formItemLayout}
              validateTrigger="onBlur"
              label={this.$t('exp.payment.purpose')} /**付款用途 */
            >
              {getFieldDecorator('cashTransactionClassIdList', {
                initialValue: record.cashTransactionClassIdList || {
                  radioValue: true,
                  chooserValue: [],
                },
              })(
                <CustomChooser
                  placeholder={this.$t('exp.please.select.payment.purpose')}
                  selectorItem={paymentPurposeItem}
                  showNumber={true}
                  showDetail={false}
                  params={{ setOfBooksId: setOfBooksId, reportTypeId: record.id }}
                  valueKey="id"
                  labelKey="name"
                />
              )}
            </FormItem>
            {/* 付款方式类型 */}
            <div className="common-item-title">{this.$t('exp.payment.way.type')} </div>
            <FormItem
              {...formItemLayout}
              validateTrigger="onBlur"
              label={this.$t('exp.payment.way.type')}
            >
              {getFieldDecorator('paymentMethod', {
                initialValue: params.id ? record.paymentMethod : 'ONLINE_PAYMENT',
              })(
                <Select placeholder={this.$t({ id: 'common.please.select' })}>
                  {payValuesType.map(option => {
                    return <Option key={option.value}>{option.name}</Option>;
                  })}
                </Select>
              )}
            </FormItem>
            {/* 核销依据 */}
            <div className="common-item-title">
              <span>
                {this.$t('exp.verification.basis')}
                &nbsp;
                <Tooltip
                  title={this.$t(
                    'exp.system.already.been.writ'
                  )} /**"系统已有核销依据为单据头公司一致，且收款方一致" */
                >
                  <Icon type="question-circle-o" />
                </Tooltip>
              </span>
            </div>
            <FormItem {...itemLayout} /**关联相同申请单 */>
              {getFieldDecorator('writeOffApplication', {
                initialValue: record.id ? record.writeOffApplication : false,
                valuePropName: 'checked',
              })(<Checkbox>{this.$t('exp.contract.billing')}</Checkbox>)}
            </FormItem>
            <FormItem {...itemLayout} /**关联相同合同 */>
              {getFieldDecorator('writeOffContract', {
                initialValue: record.id ? record.writeOffContract : false,
                valuePropName: 'checked',
              })(<Checkbox>{this.$t('exp.same.contract')}</Checkbox>)}
            </FormItem>
            {/* 权限设置 */}
            <div className="common-item-title">{this.$t('adjust.authority.set')}</div>
            <FormItem
              labelCol={{ span: 8 }}
              wrapperCol={{ span: 16 }}
              label={this.$t('adjust.applicable.personnel')}
            >
              {getFieldDecorator('departmentOrUserGroupIdList', {
                initialValue: record.departmentOrUserGroupIdList || { type: 'all', values: [] },
              })(<PermissionsAllocation params={{ setOfBooksId: params.setOfBooksId }} />)}
            </FormItem>
            <div className="slide-footer">
              <Button type="primary" htmlType="submit" loading={saveLoading}>
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

function mapStateToProps(state) {
  return {
    company: state.login.company,
    tenantId: state.user.company.tenantId,
  };
}
const WrappedNewPrePaymentType = Form.create()(NewBillingType);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewPrePaymentType);
