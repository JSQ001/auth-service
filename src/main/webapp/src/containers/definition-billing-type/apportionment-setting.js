import React from 'react';
import { connect } from 'react-redux';
import config from 'config';
import Chooser from 'components/Widget/chooser';
import { Form, Switch, Icon, Input, Select, Button, Spin, Radio, message, Checkbox } from 'antd';
import Service from './service';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const CheckboxGroup = Checkbox.Group;

class NewApportionmentSetting extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      saveLoading: false,
      loading: false,
      companySwitch: false,
      applicationDepartment: '',
      departmentSwitch: '',
      applicationResponsibility: '',
      responsibilitySwitch: '',
      companyValue: 'CURRENT_COM_&_SUB_COM',
      departmentValue: '',
      responsibilityValue: 'DEP_RES_CENTER',
      record: {},
      visibleList: [],
      resDisabled: true,
      //公司默认值显示
      companyDisable: true,
      companyList: [],
      filterCompanyList: [],
      allCompany: [],
      // 部门默认值显示
      departmentList: [],
      filterDepartmentList: [],
      allDepartment: [],
      // 责任中心默认值显示
      resList: [],
      filterResList: [],
      allRes: [],
      // 责任中心
      resSelectorItem: {
        // title: itemTitle,
        url: `${config.mdataUrl}/api/responsibilityCenter/query/default?setOfBooksId=${
          this.props.params.setOfBooksId
        }`,
        searchForm: [
          {
            type: 'input',
            id: 'info',
            label: this.$t({ id: 'structure.responsibilityCenter' }) /*责任中心 */,
            colSpan: 8,
            placeholder: this.$t('components.search.dimension.please.enter.item'),
          },
          {
            type: 'input',
            id: 'codeFrom',
            label: this.$t({ id: 'structure.responsibilityCenterFrom' }) /*责任中心代码从 */,
            colSpan: 8,
          },
          {
            type: 'input',
            id: 'codeTo',
            label: this.$t({ id: 'structure.responsibilityCenterTo' }) /*责任中心代码至 */,
            colSpan: 8,
          },
        ],
        columns: [
          {
            title: this.$t({ id: 'structure.responsibilityCenterCode' }) /*责任中心代码 */,
            dataIndex: 'responsibilityCenterCode',
            align: 'center',
          },
          {
            title: this.$t({ id: 'structure.responsibilityCenterName' }) /*责任中心名称 */,
            dataIndex: 'responsibilityCenterName',
            align: 'center',
          },
        ],
        key: 'id',
      },
      // 公司弹框
      companySelectorItem: {
        title: this.$t('chooser.data.company'),
        url: `${
          config.expenseUrl
        }/api/expense/report/type/dist/setting/query/company/by/company/dist/range`,
        searchForm: [
          {
            type: 'input',
            id: 'companyCode',
            label: this.$t('billing.expense.companyCode') /**公司代码 */,
            colSpan: 8,
            placeholder: '请输入公司代码',
          },
          {
            type: 'input',
            id: 'companyName',
            label: this.$t('billing.expense.companyName') /**公司名称 */,
            colSpan: 8,
          },
          {
            type: 'input',
            id: 'companyCodeFrom',
            label: this.$t('billing.expense.companyCode.from') /**公司代码从 */,
            colSpan: 8,
          },
          {
            type: 'input',
            id: 'companyCodeTo',
            label: this.$t('billing.expense.companyCode.to') /**公司代码至 */,
            colSpan: 8,
          },
        ],
        columns: [
          {
            title: this.$t('billing.expense.companyCode'),
            dataIndex: 'companyCode',
            align: 'center',
          },
          {
            title: this.$t('billing.expense.companyName'),
            dataIndex: 'name',
            align: 'center',
          },
          {
            title: this.$t('billing.expense.companyType') /**公司类型 */,
            dataIndex: 'companyTypeName',
            align: 'center',
          },
        ],
        key: 'id',
      },
      // 部门弹框
      departmentSelectorItem: {
        title: this.$t('billing.expense.departmentChoose') /**选择部门 */,
        url: `${
          config.expenseUrl
        }/api/expense/report/type/dist/setting/query/department/by/department/dist/range`,
        searchForm: [
          {
            type: 'input',
            id: 'departmentCode',
            label: this.$t('billing.expense.departmentCode') /**部门代码 */,
            colSpan: 8,
            placeholder: '请输入部门代码',
          },
          {
            type: 'input',
            id: 'departmentName',
            label: this.$t('billing.expense.departmentName') /**部门名称 */,
            colSpan: 8,
          },
          {
            type: 'input',
            id: 'departmentCodeFrom',
            label: this.$t('billing.expense.departmentCode.from') /*部门代码从 */,
            colSpan: 8,
          },
          {
            type: 'input',
            id: 'departmentCodeTo',
            label: this.$t('billing.expense.departmentCode.to') /*部门代码至 */,
            colSpan: 8,
          },
        ],
        columns: [
          {
            title: this.$t('billing.expense.departmentCode') /**部门代码 */,
            dataIndex: 'departmentCode',
            align: 'center',
          },
          {
            title: this.$t('billing.expense.departmentName') /**部门名称 */,
            dataIndex: 'name',
            align: 'center',
          },
        ],
        key: 'id',
      },
    };
  }

  componentDidMount() {
    this.getApportionmentById();
    this.getSettingVisible();
  }

  // 获取显示可见
  getSettingVisible = () => {
    Service.getPayValueList('EXPENSE_REPORT_TYPE_DIST_SETTING_VISIBLE')
      .then(res => {
        let visibleList = [];
        res.data.map(item => {
          visibleList.push({ name: item.name, key: item.id, value: item.value });
        });
        this.setState({ visibleList: visibleList });
      })
      .catch(err => {
        message.error('数据获取失败');
      });
  };

  // 获取详情信息
  getApportionmentById = () => {
    if (this.props.params.id) {
      Service.getApportionmentById(this.props.params.id).then(res => {
        let record = { ...res.data.expenseReportTypeDistSetting };
        record.companyIdList = res.data.companyIdList
          ? res.data.companyCOList.map(o => ({
              id: o.id,
              companyCode: o.companyCode,
              name: o.name,
            }))
          : [];
        record.departmentIdList = res.data.departmentIdList
          ? res.data.departmentCOList.map(o => ({
              id: o.id,
              departmentCode: o.departmentCode,
              name: o.name,
            }))
          : [];
        record.resIdList = res.data.resIdList
          ? res.data.responsibilityCenterCOList.map(o => ({
              id: o.id,
              responsibilityCenterCode: o.responsibilityCenterCode,
              responsibilityCenterName: o.responsibilityCenterName,
            }))
          : [];
        this.setState(
          {
            loading: false,
            record: record,
            companySwitch: record.companyDistFlag,
            companyValue: record.companyDistRange,
            departmentSwitch: record.departmentDistFlag,
            departmentValue: record.departmentDistRange,
            responsibilitySwitch: record.resCenterDistFlag,
            responsibilityValue: record.resDistRange,
            filterCompanyList: record.companyIdList,
            filterDepartmentList: record.departmentIdList,
            filterResList: record.resIdList,
          },
          () => {
            let flag = record.companyDistRange === 'CUSTOM_RANGE' ? true : false;
            this.getCompanyDefaultValue(flag);
            let depart = record.departmentDistRange === 'CUSTOM_RANGE' ? true : false;
            this.getDepartmentDefaultValue(depart);
            let one = record.resDistRange === 'CUSTOM_RANGE' ? true : false;
            this.getResDefaultValue(one);
          }
        );
      });
    }
  };

  // 保存
  handleSave = e => {
    e.preventDefault();
    const { record } = this.state;
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (err) return;
      this.setState({ saveLoading: true });
      console.log(values);
      let companyIdList = values.companyIdList ? values.companyIdList.map(o => o.id) : [];
      let departmentIdList = values.departmentIdList ? values.departmentIdList.map(o => o.id) : [];
      let resIdList = values.resIdList ? values.resIdList.map(o => o.id) : [];
      if (
        values.companyDistFlag &&
        values.companyVisible !== 'EDITABLE' &&
        !(
          values.companyDistRange === 'SUB_COM' ||
          values.companyDistRange === 'CURRENT_COM_&_SUB_COM'
        ) &&
        !values.companyDefaultId
      ) {
        message.warning('分摊公司需要有默认值');
        this.setState({ saveLoading: false });
        return;
      }
      if (
        values.departmentDistFlag &&
        values.departmentVisible !== 'EDITABLE' &&
        !values.departmentDefaultId
      ) {
        message.warning('分摊部门需要有默认值');
        this.setState({ saveLoading: false });
        return;
      }
      if (
        values.resCenterDistFlag &&
        values.resVisible !== 'EDITABLE' &&
        values.resDistRange !== 'DEP_RES_CENTER' &&
        (!values.resDefaultId || values.resDefaultId === '')
      ) {
        message.warning('分摊责任中心需要有默认值');
        this.setState({ saveLoading: false });
        return;
      }
      let _params = {
        expenseReportTypeDistSetting: {
          reportTypeId: this.props.params.id,
          ...values,
        },
        companyIdList,
        departmentIdList,
        resIdList,
      };
      let method = Service.saveApportionmentSetting;
      if (record.id) {
        method = Service.editApportionmentSetting;
        _params.expenseReportTypeDistSetting = {
          id: record.id,
          reportTypeId: record.reportTypeId ? record.reportTypeId : '',
          versionNumber: record.versionNumber,
          ...values,
        };
        companyIdList;
        departmentIdList;
        resIdList;
      }
      method(_params)
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

  onCancel = () => {
    this.props.close && this.props.close();
  };

  // 公司参与分摊
  apportionmentCompany = e => {
    this.setState({
      companySwitch: e,
    });
  };

  clearCompanyData = e => {
    if (e == false) {
      const { record } = this.state;
      record.companyDistRange = null;
      record.companyIdList = '';
      record.companyDefaultId = '';
      this.props.form.setFieldsValue({
        companyIdList: '',
        companyDefaultId: '',
        companyVisible: 'EDITABLE',
      });
    } else {
      this.setState({ companyDisable: true, companyValue: 'CURRENT_COM_&_SUB_COM' });
      this.props.form.setFieldsValue({
        companyVisible: 'EDITABLE',
      });
    }
  };

  // 公司自定义范围
  companyRadioChange = e => {
    this.setState({
      companyValue: e.target.value,
    });
    this.resCompanyDefault();
    if (e.target.value == 'CUSTOM_RANGE') {
      const { filterCompanyList } = this.state;
      this.setState({ companyList: filterCompanyList });
    } else {
      const { allCompany } = this.state;
      this.setState({ companyList: allCompany });
    }
    if (e.target.value === 'SUB_COM' || e.target.value === 'CURRENT_COM_&_SUB_COM') {
      this.setState({ companyDisable: true });
    } else {
      this.setState({ companyDisable: false });
    }
  };

  // 获取公司默认值
  getCompanyDefaultValue = flag => {
    let list = [];
    Service.getCompanyDefaultValue()
      .then(res => {
        res.data.map(item => {
          list.push({
            id: item.id,
            key: item.id,
            companyCode: item.companyCode,
            name: item.name,
            companyTypeName: item.companyTypeName,
          });
        });
        const { filterCompanyList } = this.state;
        this.setState({
          allCompany: list,
          companyList: flag ? filterCompanyList : list,
        });
      })
      .catch(err => {
        message.error('获取值失败');
      });
  };

  // 公司自定义值转给默认值
  companyValueTran = value => {
    if (value.length === 0) {
      message.warn(this.$t('common.select.one.more'));
      return;
    }
    this.setState({ companyList: value });
    this.resCompanyDefault();
  };

  resCompanyDefault = () => {
    this.props.form.setFieldsValue({ companyDefaultId: undefined });
  };

  // 部门参与分摊
  apportionmentDepartment = e => {
    this.setState({
      departmentSwitch: e,
    });
  };

  clearDepartmentData = e => {
    if (e == false) {
      const { record } = this.state;
      record.departmentDistRange = 'ALL_DEP_IN_TENANT';
      record.departmentIdList = '';
      record.departmentDefaultId = '';
      this.props.form.setFieldsValue({
        departmentIdList: '',
        departmentDefaultId: '',
        departmentVisible: 'EDITABLE',
      });
    } else {
      this.setState({ departmentValue: 'ALL_DEP_IN_TENANT' });
      this.props.form.setFieldsValue({
        departmentVisible: 'EDITABLE',
      });
    }
  };

  // 部门自定义范围
  departmentRadioChange = e => {
    this.props.form.setFieldsValue({
      departmentDefaultId: undefined,
    });
    this.setState({ departmentValue: e.target.value });
    if (e.target.value == 'CUSTOM_RANGE') {
      const { filterDepartmentList } = this.state;
      this.setState({ departmentList: filterDepartmentList });
    } else {
      const { allDepartment } = this.state;
      this.setState({ departmentList: allDepartment });
    }
  };

  // 部门自定义值转给默认值
  departmentValueTran = value => {
    if (value.length === 0) {
      message.warn(this.$t('common.select.one.more'));
      return;
    }
    this.setState({
      departmentList: value,
    });
    this.props.form.setFieldsValue({
      departmentDefaultId: undefined,
    });
  };

  // 获取部门默认值
  getDepartmentDefaultValue = depart => {
    let list = [];
    Service.getDepartmentDefaultValue()
      .then(res => {
        res.data.map(item => {
          list.push({
            id: item.id,
            key: item.id,
            departmentCode: item.departmentCode,
            name: item.name,
          });
        });
        const { filterDepartmentList } = this.state;
        this.setState({
          allDepartment: list,
          departmentList: depart ? filterDepartmentList : list,
        });
      })
      .catch(err => {
        message.error('获取值失败');
      });
  };

  // 获取责任中心默认值
  getResDefaultValue = one => {
    let list = [];
    Service.getResDefaultValue(this.props.params.setOfBooksId)
      .then(res => {
        res.data.map(item => {
          list.push({
            id: item.id,
            key: item.id,
            responsibilityCenterCode: item.responsibilityCenterCode,
            responsibilityCenterName: item.responsibilityCenterName,
          });
        });
        const { filterResList } = this.state;
        this.setState({
          allRes: list,
          resList: one ? filterResList : list,
        });
      })
      .catch(err => {
        message.error('数据获取失败');
      });
  };

  // 责任中心自定义值转给默认值
  resValueTran = value => {
    if (value.length === 0) {
      message.warn(this.$t('common.select.one.more'));
      return;
    }
    this.setState({
      resList: value,
    });
    this.props.form.setFieldsValue({
      resDefaultId: '',
    });
  };

  // 责任中心参与分摊
  apportionmentResponsibility = e => {
    this.setState({
      responsibilitySwitch: e,
    });
  };

  clearResData = e => {
    if (e == false) {
      const { record } = this.state;
      record.resDistRange = 'DEP_RES_CENTER';
      record.resIdList = '';
      record.resDefaultId = '';
      this.props.form.setFieldsValue({
        resIdList: '',
        resDefaultId: '',
        resVisible: 'EDITABLE',
      });
    } else {
      this.setState({ resDisabled: true, responsibilityValue: 'DEP_RES_CENTER' });
      this.props.form.setFieldsValue({
        resVisible: 'EDITABLE',
      });
    }
  };

  // 责任中心自定义范围
  responsibilityRadioChange = e => {
    this.props.form.setFieldsValue({ resDefaultId: '' });
    this.setState({ responsibilityValue: e.target.value });
    if (e.target.value === 'DEP_RES_CENTER') {
      this.setState({ resDisabled: true });
    } else {
      this.setState({ resDisabled: false });
    }
    if (e.target.value == 'CUSTOM_RANGE') {
      const { filterResList } = this.state;
      this.setState({ resList: filterResList });
    } else {
      const { allRes } = this.state;
      this.setState({ resList: allRes });
    }
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      saveLoading,
      loading,
      companySwitch,
      departmentSwitch,
      responsibilitySwitch,
      companyValue,
      departmentValue,
      responsibilityValue,
      record,
      visibleList,
      companySelectorItem,
      departmentSelectorItem,
      companyList,
      departmentList,
      resList,
      resDisabled,
      companyDisable,
    } = this.state;
    const { params } = this.props;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };
    const radioStyle = {
      display: 'block',
      height: '30px',
      lineHeight: '42px',
    };
    return (
      <div>
        {loading ? (
          <Spin />
        ) : (
          <Form onSubmit={this.handleSave}>
            <FormItem {...formItemLayout} label={this.$t('billing.type.code')} /**报账单类型代码 */>
              {getFieldDecorator('reportTypeCode', {
                initialValue: params.reportTypeCode,
              })(<Input disabled />)}
            </FormItem>
            <FormItem {...formItemLayout} label={this.$t('billing.type.name')} /**报账单类型名称 */>
              {getFieldDecorator('reportTypeName', {
                initialValue: params.reportTypeName,
              })(<Input disabled />)}
            </FormItem>
            {/* 公司 */}
            <div className="common-item-title"> {this.$t('detail.company')}</div>
            <FormItem
              {...formItemLayout}
              label={this.$t('billing.expense.participationSharing')} /**参与分摊 */
            >
              {getFieldDecorator('companyDistFlag', {
                initialValue: record.companyDistFlag ? record.companyDistFlag : false,
                valuePropName: 'checked',
              })(
                <Switch
                  checkedChildren={<Icon type="check" />}
                  unCheckedChildren={<Icon type="close" />}
                  onChange={value => this.clearCompanyData(value)}
                  onClick={this.apportionmentCompany}
                />
              )}
            </FormItem>
            {companySwitch && (
              <FormItem
                {...formItemLayout}
                label={this.$t('billing.expense.apportionedRange')}
                /**分摊范围 */ colon={false}
              >
                {getFieldDecorator('companyDistRange', {
                  initialValue:
                    record.id && record.companyDistRange !== null
                      ? record.companyDistRange
                      : 'CURRENT_COM_&_SUB_COM',
                })(
                  <RadioGroup onChange={this.companyRadioChange}>
                    <Radio style={radioStyle} value="ALL_COM_IN_SOB">
                      {this.$t('exp.setOfBooks.company')}
                    </Radio>
                    <Radio style={radioStyle} value="CURRENT_COM_&_SUB_COM">
                      {this.$t('exp.company.or.subordinate.company')}
                    </Radio>
                    <Radio style={radioStyle} value="SUB_COM">
                      {this.$t('exp.subordinate.company')}
                    </Radio>
                    <Radio style={radioStyle} value="CUSTOM_RANGE">
                      {this.$t('exp.custom.range')}
                    </Radio>
                  </RadioGroup>
                )}
              </FormItem>
            )}
            {/* 公司自定义范围 */}
            {companySwitch &&
              companyValue === 'CUSTOM_RANGE' && (
                <FormItem {...formItemLayout} label={this.$t('exp.custom.range')}>
                  {getFieldDecorator('companyIdList', {
                    rules: [
                      {
                        required: true,
                        message: '请输入自定义范围',
                      },
                    ],
                    initialValue: record.companyIdList || [],
                  })(
                    <Chooser
                      placeholder="请选择自定义公司"
                      selectorItem={companySelectorItem}
                      labelKey="${companyCode}-${name}"
                      valueKey="id"
                      single={false}
                      showNumber={true}
                      showDetail={false}
                      onChange={value => this.companyValueTran(value)}
                      listExtraParams={{
                        setOfBooksId: this.props.params.setOfBooksId,
                        companyDistRange: this.props.form.getFieldValue('companyDistRange'),
                      }}
                    />
                  )}
                </FormItem>
              )}
            {/* 公司默认值 */}
            {companySwitch && (
              <FormItem {...formItemLayout} label={this.$t('exp.default.value')}>
                {getFieldDecorator('companyDefaultId', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.select'),
                    },
                  ],
                  initialValue: record.companyDefaultId ? record.companyDefaultId : null,
                })(
                  <Select
                    disabled={
                      companyDisable &&
                      (companyValue == 'CURRENT_COM_&_SUB_COM' ||
                        record.companyDistRange == null ||
                        companyValue == 'SUB_COM')
                        ? true
                        : false
                    }
                  >
                    {companyList.map(option => {
                      return (
                        <Option key={option.key} value={option.id}>
                          {option.companyCode}-{option.name}
                        </Option>
                      );
                    })}
                  </Select>
                )}
              </FormItem>
            )}
            <FormItem
              {...formItemLayout}
              validateTrigger="onBlur"
              label={this.$t('exp.display.settings')}
            >
              {getFieldDecorator('companyVisible', {
                // initialValue: record.companyVisible
                //   ? record.companyDistRange == 'CURRENT_COM_&_SUB_COM' ||
                //     record.companyDistRange == 'SUB_COM' ||
                //     record.companyDistRange == null
                //     ? 'EDITABLE'
                //     : record.companyVisible
                //   : companySwitch ||
                //     companyValue == 'CURRENT_COM_&_SUB_COM' ||
                //     companyValue == 'SUB_COM'
                //     ? 'EDITABLE'
                //     : 'READ_ONLY',
                initialValue: record.companyVisible ? record.companyVisible : 'READ_ONLY',
              })(
                <Select
                  placeholder={this.$t({ id: 'common.please.select' })}
                  disabled={
                    companyDisable &&
                    companySwitch &&
                    (companyValue == 'CURRENT_COM_&_SUB_COM' ||
                      record.companyDistRange == null ||
                      companyValue == 'SUB_COM')
                      ? true
                      : false
                  }
                >
                  {visibleList.map(option => {
                    return (
                      <Option key={option.id} value={option.value}>
                        {option.name}
                      </Option>
                    );
                  })}
                </Select>
              )}
            </FormItem>

            {/* 部门 */}
            <div className="common-item-title"> {this.$t('acp.unitName')}</div>
            <FormItem {...formItemLayout} label={this.$t('billing.expense.participationSharing')}>
              {getFieldDecorator('departmentDistFlag', {
                initialValue: record.departmentDistFlag ? record.departmentDistFlag : false,
                valuePropName: 'checked',
              })(
                <Switch
                  checkedChildren={<Icon type="check" />}
                  unCheckedChildren={<Icon type="close" />}
                  onChange={value => this.clearDepartmentData(value)}
                  onClick={this.apportionmentDepartment}
                />
              )}
            </FormItem>
            {departmentSwitch && (
              <FormItem
                {...formItemLayout}
                label={this.$t('billing.expense.apportionedRange')}
                /**分摊范围 */ colon={false}
              >
                {getFieldDecorator('departmentDistRange', {
                  initialValue: record.departmentDistRange
                    ? record.departmentDistRange
                    : 'ALL_DEP_IN_TENANT',
                })(
                  <RadioGroup onChange={this.departmentRadioChange}>
                    <Radio style={radioStyle} value="ALL_DEP_IN_TENANT">
                      {this.$t('exp.all.tenant')}
                    </Radio>
                    <Radio style={radioStyle} value="ALL_DEP_IN_SOB">
                      {this.$t('exp.all.department')}
                    </Radio>
                    <Radio style={radioStyle} value="ALL_DEP_IN_COM">
                      {this.$t('exp.company.all.department')}
                    </Radio>
                    <Radio style={radioStyle} value="CUSTOM_RANGE">
                      {this.$t('exp.custom.range')}
                    </Radio>
                  </RadioGroup>
                )}
              </FormItem>
            )}
            {departmentSwitch &&
              departmentValue === 'CUSTOM_RANGE' && (
                <FormItem {...formItemLayout} label={this.$t('exp.custom.range')}>
                  {getFieldDecorator('departmentIdList', {
                    rules: [
                      {
                        required: true,
                        message: '请输入自定义范围',
                      },
                    ],
                    initialValue: record.departmentIdList || [],
                  })(
                    <Chooser
                      placeholder={this.$t('exp.select.department')}
                      labelKey="${departmentCode}-${name}"
                      valueKey="id"
                      selectorItem={departmentSelectorItem}
                      showNumber
                      single={false}
                      showDetail={false}
                      onChange={value => this.departmentValueTran(value)}
                      listExtraParams={{
                        tenantId: this.props.tenantId,
                        departmentDistRange: this.props.form.getFieldValue('departmentDistRange'),
                      }}
                    />
                  )}
                </FormItem>
              )}

            {/* 部门默认值 */}
            {departmentSwitch && (
              <FormItem {...formItemLayout} label={this.$t('exp.default.value')}>
                {getFieldDecorator('departmentDefaultId', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.select'),
                    },
                  ],
                  initialValue: record.id ? record.departmentDefaultId : undefined,
                })(
                  <Select>
                    {departmentList.map(option => {
                      return (
                        <Option key={option.id} value={option.id}>
                          {option.departmentCode}-{option.name}
                        </Option>
                      );
                    })}
                  </Select>
                )}
              </FormItem>
            )}

            <FormItem
              {...formItemLayout}
              validateTrigger="onBlur"
              label={this.$t('exp.display.settings')}
            >
              {getFieldDecorator('departmentVisible', {
                initialValue: record.departmentVisible ? record.departmentVisible : 'READ_ONLY',
              })(
                <Select placeholder={this.$t({ id: 'common.please.select' })}>
                  {visibleList.map(option => {
                    return (
                      <Option key={option.id} value={option.value}>
                        {option.name}
                      </Option>
                    );
                  })}
                </Select>
              )}
            </FormItem>

            {/* 责任中心 */}
            <div className="common-item-title"> {this.$t('structure.responsibilityCenter')}</div>
            <FormItem
              {...formItemLayout}
              label={this.$t('billing.expense.participationSharing')} /**参与分摊 */
            >
              {getFieldDecorator('resCenterDistFlag', {
                initialValue: record.resCenterDistFlag ? record.resCenterDistFlag : false,
                valuePropName: 'checked',
              })(
                <Switch
                  checkedChildren={<Icon type="check" />}
                  unCheckedChildren={<Icon type="close" />}
                  onChange={value => this.clearResData(value)}
                  onClick={this.apportionmentResponsibility}
                />
              )}
            </FormItem>
            {responsibilitySwitch && (
              <FormItem
                {...formItemLayout}
                label={this.$t('billing.expense.apportionedRange')}
                /**分摊范围 */ colon={false}
              >
                {getFieldDecorator('resDistRange', {
                  initialValue: record.resDistRange ? record.resDistRange : 'DEP_RES_CENTER',
                })(
                  <RadioGroup onChange={this.responsibilityRadioChange}>
                    <Radio style={radioStyle} value="DEP_RES_CENTER">
                      {this.$t('exp.department.for.res')}
                    </Radio>
                    <Radio style={radioStyle} value="ALL_RES_CENTER_IN_SOB">
                      {this.$t('exp.setOfBooks.all.res')}
                    </Radio>
                    <Radio style={radioStyle} value="CUSTOM_RANGE">
                      {this.$t('exp.custom.range')}
                    </Radio>
                  </RadioGroup>
                )}
              </FormItem>
            )}
            {responsibilitySwitch &&
              responsibilityValue == 'CUSTOM_RANGE' && (
                <FormItem {...formItemLayout} label={this.$t('exp.custom.range')}>
                  {getFieldDecorator('resIdList', {
                    rules: [
                      {
                        required: true,
                        message: '请输入自定义范围',
                      },
                    ],
                    initialValue: record.resIdList ? record.resIdList : [],
                  })(
                    <Chooser
                      placeholder={this.$t('common.input.name.or.code')}
                      type="responsibility_usable"
                      labelKey="responsibilityCenterName"
                      valueKey="id"
                      showNumber={true}
                      showDetail={false}
                      onChange={value => this.resValueTran(value)}
                      single={false}
                      listExtraParams={{
                        setOfBooksId: this.props.params.setOfBooksId,
                      }}
                    />
                  )}
                </FormItem>
              )}

            {/* 责任中心默认值  */}

            {responsibilitySwitch && (
              <FormItem {...formItemLayout} label={this.$t('exp.default.value')}>
                {getFieldDecorator('resDefaultId', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.select'),
                    },
                  ],
                  initialValue: record.id && record.resDefaultId ? record.resDefaultId : '',
                })(
                  <Select
                    placeholder={this.$t({ id: 'common.please.select' })}
                    disabled={responsibilityValue == 'DEP_RES_CENTER' ? true : false}
                  >
                    {resList.map(option => {
                      return (
                        <Option key={option.id} value={option.id}>
                          {option.responsibilityCenterCode}-{option.responsibilityCenterName}
                        </Option>
                      );
                    })}
                  </Select>
                )}
              </FormItem>
            )}
            <FormItem
              {...formItemLayout}
              validateTrigger="onBlur"
              label={this.$t('exp.display.settings')}
            >
              {getFieldDecorator('resVisible', {
                initialValue: record.resVisible ? record.resVisible : 'READ_ONLY',
              })(
                <Select placeholder={this.$t({ id: 'common.please.select' })}>
                  {visibleList.map(option => {
                    return (
                      <Option key={option.id} value={option.value}>
                        {option.name}
                      </Option>
                    );
                  })}
                </Select>
              )}
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
    companys: state.user.company,
    tenantId: state.user.company.tenantId,
  };
}
const WrappedNewPrePaymentType = Form.create()(NewApportionmentSetting);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewPrePaymentType);
