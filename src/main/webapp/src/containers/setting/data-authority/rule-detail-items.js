import React from 'react';
import { connect } from 'dva';
import {
  Button,
  Form,
  Switch,
  Input,
  message,
  Icon,
  InputNumber,
  Select,
  Modal,
  Card,
  Row,
  Col,
  Badge,
  Divider,
  Popconfirm,
} from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
import 'styles/setting/data-authority/data-authority.scss';
import ViewRuleModal from 'containers/setting/data-authority/view-rule-modal';
import LineAddTransferModal from 'containers/setting/data-authority/line-add-transfer-modal';
import ListSelector from 'components/Widget/list-selector';
import DataAuthorityService from 'containers/setting/data-authority/data-authority.service';
import config from 'config';

class RuleDetailItem extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      show: true,
      ruleName: '',
      ruleDatail: [],
      dataType: {
        SOB: { label: this.$t('base.zhang.set') } /*账套*/,
        COMPANY: { label: this.$t('base.the.company') } /*公司*/,
        UNIT: { label: this.$t('base.department') } /*部门*/,
        EMPLOYEE: { label: this.$t('base.employees') } /*员工*/,
      },
      dataScopeDesc: {
        '1001': { label: this.$t('base.all') } /*全部*/,
        '1002': { label: this.$t('base.the.current') } /*当前*/,
        '1003': { label: this.$t('base.the.current.and.the.subordinate') } /*当前及下属*/,
        '1004': { label: this.$t('base.manually.choose') } /*手动选择*/,
      },
      filtrateMethodDesc: {
        INCLUDE: { label: this.$t('base.contains') } /*包含*/,
        EXCLUDE: { label: this.$t('base.to.rule.out') } /*排除*/,
      },
      renderSobList: false,
      renderCompanyList: false,
      renderDepartmentList: false,
      renderEmplyeeList: false,
      tenantVisible: false,
      empolyeeVisible: false,
      tenantItem: {},
      employeeItem: {},
      sobValuesKeys: [],
      employeeKeys: [],
      companyVisible: false,
      companyItemsKeys: [],
      departMentVisible: false,
      departMentItemsKeys: [],
      ruleId: '',
      deleted: undefined,
      versionNumber: undefined,
      createdBy: undefined,
      createdDate: undefined,
      lastUpdatedBy: undefined,
      lastUpdatedDate: undefined,
      getRulesArr: {},
      renderRuleInfo: {},
      sobText: this.$t('base.add.account.set') /*添加账套*/,
      sobIcon: 'plus',
      companyText: this.$t('base.add.the.company') /*添加公司*/,
      departmentText: this.$t('base.add.the.department') /*添加部门*/,
      employeeText: this.$t('base.add.employees') /*添加员工*/,
      companyIcon: 'plus',
      departmentIcon: 'plus',
      emplyeeIcon: 'plus',
    };
  }
  componentWillMount = () => {
    let params = this.props.params;
    this.setState({
      ruleName: params.name,
      ruleDatail: params.ruleDatail,
      ruleId: params.ruleId,
      deleted: params.deleted,
      versionNumber: params.versionNumber,
      createdBy: params.createdBy,
      createdDate: params.createdDate,
      lastUpdatedBy: params.lastUpdatedBy,
      lastUpdatedDate: params.lastUpdatedDate,
      getRulesArr: params.getRulesArr,
      renderRuleInfo: params.renderRuleInfo,
    });
  };
  editRuleItem = () => {
    let { ruleDatail } = this.state;
    if (ruleDatail[0].dataScope === '1004') {
      this.setState({
        renderSobList: true,
      });
    }
    if (ruleDatail[1].dataScope === '1004') {
      this.setState({
        renderCompanyList: true,
      });
    }
    if (ruleDatail[2].dataScope === '1004') {
      this.setState({
        renderDepartmentList: true,
      });
    }
    if (ruleDatail[3].dataScope === '1004') {
      this.setState({
        renderEmplyeeList: true,
      });
    }
    this.setState({
      show: false,
    });
  };
  removeRule = () => {
    this.setState({
      show: true,
    });
  };
  /**保存单条规则 */
  saveRuleItem = (e, targeKey) => {
    e.preventDefault();
    let tenantId = this.props.company.tenantId;
    this.props.form.validateFields((err, values) => {
      let {
        ruleId,
        deleted,
        versionNumber,
        createdBy,
        createdDate,
        lastUpdatedBy,
        lastUpdatedDate,
        getRulesArr,
        dataScopeDesc,
        sobValuesKeys,
        employeeKeys,
        filtrateMethodDesc,
        companyItemsKeys,
        departMentItemsKeys,
        renderRuleInfo,
      } = this.state;
      if (!err) {
        let params = {
          id: ruleId ? ruleId : null,
          i18n: null,
          enabled: renderRuleInfo.enabled,
          tenantId: tenantId,
          dataAuthorityCode: renderRuleInfo.dataAuthorityCode,
          dataAuthorityName: renderRuleInfo.dataAuthorityName,
          description: renderRuleInfo.description,
          deleted: deleted,
          versionNumber: versionNumber,
          createdBy: createdBy,
          createdDate: createdDate,
          lastUpdatedBy: lastUpdatedBy,
          lastUpdatedDate: lastUpdatedDate,
          dataAuthorityRules: [
            {
              i18n: null,
              dataAuthorityRuleName: values[`dataAuthorityRuleName`],
              dataAuthorityRuleDetails: [
                {
                  dataType: 'SOB',
                  dataScopeDesc: dataScopeDesc[values[`dataScope1`]].label,
                  dataScope: values[`dataScope1`],
                  filtrateMethod: values[`filtrateMethod1`] ? values[`filtrateMethod1`] : null,
                  filtrateMethodDesc: values[`filtrateMethod1`]
                    ? filtrateMethodDesc[values[`filtrateMethod1`]].label
                    : null,
                  dataAuthorityRuleDetailValues: values[`filtrateMethod1`] ? sobValuesKeys : [],
                  id: getRulesArr.dataAuthorityRuleDetails
                    ? getRulesArr.dataAuthorityRuleDetails[0].id
                    : null,
                  deleted: getRulesArr.deleted,
                  versionNumber: getRulesArr.versionNumber,
                  createdBy: getRulesArr.createdBy,
                  createdDate: getRulesArr.createdDate,
                  lastUpdatedBy: getRulesArr.lastUpdatedBy,
                  lastUpdatedDate: getRulesArr.lastUpdatedDate,
                  dataAuthorityId: ruleId ? ruleId : null,
                  dataAuthorityRuleId: getRulesArr.id,
                },
                {
                  dataType: 'COMPANY',
                  dataScopeDesc: dataScopeDesc[values[`dataScope2`]].label,
                  dataScope: values[`dataScope2`],
                  filtrateMethod: values[`filtrateMethod2`] ? values[`filtrateMethod2`] : null,
                  filtrateMethodDesc: values[`filtrateMethod2`]
                    ? filtrateMethodDesc[values[`filtrateMethod2`]].label
                    : null,
                  dataAuthorityRuleDetailValues: values[`filtrateMethod2`] ? companyItemsKeys : [],
                  id: getRulesArr.dataAuthorityRuleDetails
                    ? getRulesArr.dataAuthorityRuleDetails[1].id
                    : null,
                  deleted: getRulesArr.deleted,
                  versionNumber: getRulesArr.versionNumber,
                  createdBy: getRulesArr.createdBy,
                  createdDate: getRulesArr.createdDate,
                  lastUpdatedBy: getRulesArr.lastUpdatedBy,
                  lastUpdatedDate: getRulesArr.lastUpdatedDate,
                  dataAuthorityId: ruleId ? ruleId : null,
                  dataAuthorityRuleId: getRulesArr.id,
                },
                {
                  dataType: 'UNIT',
                  dataScopeDesc: dataScopeDesc[values[`dataScope3`]].label,
                  dataScope: values[`dataScope3`],
                  filtrateMethod: values[`filtrateMethod3`] ? values[`filtrateMethod3`] : null,
                  filtrateMethodDesc: values[`filtrateMethod3`]
                    ? filtrateMethodDesc[values[`filtrateMethod3`]].label
                    : null,
                  dataAuthorityRuleDetailValues: values[`filtrateMethod3`]
                    ? departMentItemsKeys
                    : [],
                  id: getRulesArr.dataAuthorityRuleDetails
                    ? getRulesArr.dataAuthorityRuleDetails[2].id
                    : null,
                  deleted: getRulesArr.deleted,
                  versionNumber: getRulesArr.versionNumber,
                  createdBy: getRulesArr.createdBy,
                  createdDate: getRulesArr.createdDate,
                  lastUpdatedBy: getRulesArr.lastUpdatedBy,
                  lastUpdatedDate: getRulesArr.lastUpdatedDate,
                  dataAuthorityId: ruleId ? ruleId : null,
                  dataAuthorityRuleId: getRulesArr.id,
                },
                {
                  dataType: 'EMPLOYEE',
                  dataScopeDesc: dataScopeDesc[values[`dataScope4`]].label,
                  dataScope: values[`dataScope4`],
                  filtrateMethod: values[`filtrateMethod4`] ? values[`filtrateMethod4`] : null,
                  filtrateMethodDesc: values[`filtrateMethod4`]
                    ? filtrateMethodDesc[values[`filtrateMethod4`]].label
                    : null,
                  dataAuthorityRuleDetailValues: values[`filtrateMethod4`] ? employeeKeys : [],
                  id: getRulesArr.dataAuthorityRuleDetails
                    ? getRulesArr.dataAuthorityRuleDetails[3].id
                    : null,
                  deleted: getRulesArr.deleted,
                  versionNumber: getRulesArr.versionNumber,
                  createdBy: getRulesArr.createdBy,
                  createdDate: getRulesArr.createdDate,
                  lastUpdatedBy: getRulesArr.lastUpdatedBy,
                  lastUpdatedDate: getRulesArr.lastUpdatedDate,
                  dataAuthorityId: ruleId ? ruleId : null,
                  dataAuthorityRuleId: getRulesArr.id,
                },
              ],
              id: getRulesArr.id,
              deleted: getRulesArr.deleted,
              versionNumber: getRulesArr.versionNumber,
              createdBy: getRulesArr.createdBy,
              createdDate: getRulesArr.createdDate,
              lastUpdatedBy: getRulesArr.lastUpdatedBy,
              lastUpdatedDate: getRulesArr.lastUpdatedDate,
              dataAuthorityId: ruleId ? ruleId : null,
            },
          ],
        };
        DataAuthorityService.saveDataAuthority(params)
          .then(res => {
            if (res.status === 200) {
              this.setState(
                {
                  ruleDatail: res.data.dataAuthorityRules[0].dataAuthorityRuleDetails,
                  ruleName: res.data.dataAuthorityRules[0].dataAuthorityRuleName,
                  getRulesArr: res.data.dataAuthorityRules[0],
                  ruleId: res.data.id,
                  deleted: res.data.deleted,
                  versionNumber: res.data.versionNumber,
                  createdBy: res.data.createdBy,
                  createdDate: res.data.createdDate,
                  lastUpdatedBy: res.data.lastUpdatedBy,
                  lastUpdatedDate: res.data.lastUpdatedDate,
                },
                () => {
                  this.props.refresh(this.state.ruleDatail);
                  this.setState({
                    show: true,
                  });
                }
              );
            }
          })
          .catch(e => {
            message.error(e.response.data.message);
          });
      }
    });
  };
  /**选中手动选择 */
  handleChangeRuleChange = value => {
    if (value === '1004') {
      this.setState({
        renderSobList: true,
      });
    } else {
      this.setState({
        renderSobList: false,
      });
    }
  };
  handleChangeCompany = value => {
    if (value === '1004') {
      this.setState({
        renderCompanyList: true,
      });
    } else {
      this.setState({
        renderCompanyList: false,
      });
    }
  };
  handleChangeDepartment = value => {
    if (value === '1004') {
      this.setState({
        renderDepartmentList: true,
      });
    } else {
      this.setState({
        renderDepartmentList: false,
      });
    }
  };
  handleEmplyee = value => {
    if (value === '1004') {
      this.setState({
        renderEmplyeeList: true,
      });
    } else {
      this.setState({
        renderEmplyeeList: false,
      });
    }
  };
  //添加账套
  addTenant = () => {
    const ruleId = this.props.params.getRulesArr.id;
    const tenantItem = {
      title: this.$t('base.add.account.set') /*添加账套*/,
      url: `${
        config.baseUrl
      }/api/data/authority/rule/detail/values/select?ruleId=${ruleId}&dataType=SOB`,
      searchForm: [
        {
          type: 'input',
          id: 'code',
          label: this.$t('base.zhang.set.of.code'),
          colSpan: 6,
        } /*账套代码*/,
        {
          type: 'input',
          id: 'name',
          label: this.$t('base.zhang.set.of.names'),
          colSpan: 6,
        } /*账套名称*/,
        {
          type: 'select',
          id: 'scope',
          label: this.$t('base.to.view'),
          defaultValue: 'all',
          options: [
            /*查看*/
            { value: 'all', label: this.$t('base.all') } /*全部*/,
            { value: 'selected', label: this.$t('base.the.selected') } /*已选*/,
            { value: 'noChoose', label: this.$t('base.not.to.choose') } /*未选*/,
          ],
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: this.$t('base.zhang.set.of.code'),
          dataIndex: 'valueKeyCode',
          width: 150,
        } /*账套代码*/,
        { title: this.$t('base.zhang.set.of.names'), dataIndex: 'valueKeyDesc' } /*账套名称*/,
      ],
      key: 'valueKey',
    };
    this.setState({
      tenantVisible: true,
      tenantItem,
    });
  };
  handleTenantListOk = result => {
    let resultArr = result.result;
    let arr = [];
    for (let i = 0; i < resultArr.length; i++) {
      arr.push(resultArr[i].valueKey);
    }
    this.setState({
      tenantVisible: false,
      sobValuesKeys: arr,
      sobText: this.$t('base.desc.code6', { total: resultArr.length }), // `已选择${resultArr.length}个账套`
      sobIcon: null,
    });
  };
  cancelTenantList = flag => {
    this.setState({
      tenantVisible: flag,
    });
  };
  //添加员工
  addEmployee = () => {
    const ruleId = this.props.params.getRulesArr.id;
    const employeeItem = {
      title: this.$t('base.add.employees') /*添加员工*/,
      url: `${
        config.baseUrl
      }/api/data/authority/rule/detail/values/select?ruleId=${ruleId}&dataType=EMPLOYEE`,
      searchForm: [
        { type: 'input', id: 'code', label: this.$t('base.staff.code'), colSpan: 6 } /*员工代码*/,
        { type: 'input', id: 'name', label: this.$t('base.staff.name'), colSpan: 6 } /*员工名称*/,
        {
          type: 'select',
          id: 'scope',
          label: this.$t('base.to.view'),
          defaultValue: 'all',
          options: [
            /*查看*/
            { value: 'all', label: this.$t('base.all') } /*全部*/,
            { value: 'selected', label: this.$t('base.the.selected') } /*已选*/,
            { value: 'noChoose', label: this.$t('base.not.to.choose') } /*未选*/,
          ],
          colSpan: 6,
        },
      ],
      columns: [
        { title: this.$t('base.staff.code'), dataIndex: 'valueKeyCode', width: 150 } /*员工代码*/,
        { title: this.$t('base.staff.name'), dataIndex: 'valueKeyDesc' } /*员工名称*/,
      ],
      key: 'id',
    };
    this.setState({
      empolyeeVisible: true,
      employeeItem,
      employeeText: this.$t('base.desc.code3', { total: resultArr.length }), // `已选择${resultArr.length}个员工`
      emplyeeIcon: null,
    });
  };
  handleEmployeeListOk = result => {
    let resultArr = result.result;
    let arr = [];
    for (let i = 0; i < resultArr.length; i++) {
      arr.push(resultArr[i].valueKey);
    }
    this.setState({
      empolyeeVisible: false,
      employeeKeys: arr,
    });
  };
  cancelEmployeeList = () => {
    this.setState({
      empolyeeVisible: false,
    });
  };
  //添加公司
  addCompany = () => {
    this.setState({
      companyVisible: true,
    });
  };
  cancelCompanyList = () => {
    this.setState({
      companyVisible: false,
    });
  };
  //获取公司，部门选择的值
  transferCompanyList = items => {
    let resultArr = items;
    let arr = [];
    for (let i = 0; i < resultArr.length; i++) {
      arr.push(resultArr[i].id);
    }
    this.setState({
      companyItemsKeys: arr,
      companyVisible: false,
      companyText: this.$t('base.desc.code11', { total: resultArr.length }), // `已选择${resultArr.length}个公司`
      companyIcon: null,
    });
  };
  //添加部门
  addDepartment = () => {
    this.setState({
      departMentVisible: true,
    });
  };
  cancelDepartMentList = () => {
    this.setState({
      departMentVisible: false,
    });
  };
  transDePferList = items => {
    let resultArr = items;
    let arr = [];
    for (let i = 0; i < resultArr.length; i++) {
      arr.push(resultArr[i].id);
    }
    this.setState({
      departMentItemsKeys: arr,
      departMentVisible: false,
      departmentText: this.$t('base.desc.code12', { total: resultArr.length }), // `已选择${resultArr.length}个部门`
      departmentIcon: null,
    });
  };
  render() {
    const {
      show,
      ruleName,
      ruleDatail,
      dataType,
      renderSobList,
      tenantVisible,
      tenantItem,
      renderCompanyList,
      renderDepartmentList,
      departmentText,
      renderEmplyeeList,
      employeeItem,
      empolyeeVisible,
      companyVisible,
      departMentVisible,
      sobText,
      sobIcon,
      companyIcon,
      companyText,
      departmentIcon,
      employeeText,
      emplyeeIcon,
    } = this.state;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const ruleFormLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 16, offset: 1 },
    };
    return (
      <div className="add-rule-form">
        {show && (
          <Card
            title={ruleName || ''}
            style={{ marginTop: 25, background: '#f7f7f7' }}
            extra={
              <span>
                <a style={{ paddingLeft: 15 }} onClick={this.editRuleItem}>
                  {this.$t('base.the.editor')}
                </a>
                {/*编辑*/}
              </span>
            }
          >
            <Row>
              {ruleDatail.map(item => (
                <Col span={24}>
                  <span>{dataType[item.dataType].label}:</span>
                  {item.dataScope === '1004' ? (
                    <span>
                      {item.filtrateMethodDesc}
                      {`${item.dataAuthorityRuleDetailValues.length}{this.$t("base.a")}`}
                      {dataType[item.dataType].label}/*个*/
                    </span>
                  ) : (
                    <span>
                      {item.dataScopeDesc}
                      {dataType[item.dataType].label}
                    </span>
                  )}
                </Col>
              ))}
            </Row>
          </Card>
        )}
        {!show && (
          <div className="add-rule-form">
            <Card style={{ background: '#f7f7f7', marginTop: 25 }}>
              <Form>
                <Row>
                  <Col span={22} className="rule-form-title">
                    <FormItem {...ruleFormLayout} label="" className="rule-item-name">
                      {getFieldDecorator(`dataAuthorityRuleName`, {
                        rules: [
                          {
                            required: true,
                            message: this.$t({ id: 'common.please.enter' }),
                          },
                        ],
                        initialValue: ruleName || '',
                      })(
                        <Input
                          className="input_title"
                          placeholder={this.$t('base.please.enter.the.rule.name')}
                        />
                      )}
                    </FormItem>
                  </Col>
                  <Col span={2}>
                    <a type="primary" onClick={e => this.saveRuleItem(e, this.props.key)}>
                      {this.$t({ id: 'common.save' })}{' '}
                    </a>
                    <a style={{ marginLeft: 10 }} onClick={this.removeRule}>
                      {this.$t({ id: 'common.cancel' })}
                    </a>
                  </Col>
                </Row>
                <Divider style={{ marginTop: '-50px' }} />
                <Row className="rule-form-item">
                  <Col span={4}>
                    <FormItem {...ruleFormLayout} label={this.$t('base.zhang.set')} /*账套*/>
                      {getFieldDecorator(`dataScope1`, {
                        rules: [],
                        initialValue: ruleDatail.length ? ruleDatail[0].dataScope : '1001',
                      })(
                        <Select
                          placeholder={this.$t({ id: 'common.please.enter' })}
                          onSelect={this.handleChangeRuleChange}
                        >
                          <Option value="1001">{this.$t('base.all')}</Option>
                          {/*全部*/}
                          <Option value="1002">{this.$t('base.the.current')}</Option>
                          {/*当前*/}
                          <Option value="1004">{this.$t('base.manually.choose')}</Option>
                          {/*手动选择*/}
                        </Select>
                      )}
                    </FormItem>
                  </Col>
                  {renderSobList && (
                    <Col span={10}>
                      <Row>
                        <Col span={6} style={{ marginLeft: 10 }}>
                          <FormItem {...ruleFormLayout} label="">
                            {getFieldDecorator(`filtrateMethod1`, {
                              rules: [],
                              initialValue:
                                ruleDatail.length && ruleDatail[0].filtrateMethod
                                  ? ruleDatail[0].filtrateMethod
                                  : 'INCLUDE',
                            })(
                              <Select placeholder={this.$t({ id: 'common.please.enter' })}>
                                <Option value="INCLUDE">{this.$t('base.contains')}</Option>
                                {/*包含*/}
                                <Option value="EXCLUDE">{this.$t('base.to.rule.out')}</Option>
                                {/*排除*/}
                              </Select>
                            )}
                          </FormItem>
                        </Col>
                        <Col span={4} style={{ marginLeft: '-25px' }}>
                          <FormItem {...ruleFormLayout} label="">
                            {getFieldDecorator('addTenant')(
                              <Button icon={sobIcon} onClick={this.addTenant}>
                                {sobText}
                              </Button>
                            )}
                          </FormItem>
                        </Col>
                      </Row>
                    </Col>
                  )}
                </Row>
                <Row className="rule-form-item">
                  <Col span={4}>
                    <FormItem {...ruleFormLayout} label={this.$t('base.the.company')} /*公司*/>
                      {getFieldDecorator(`dataScope2`, {
                        rules: [],
                        initialValue: ruleDatail.length ? ruleDatail[1].dataScope : '1001',
                      })(
                        <Select
                          placeholder={this.$t({ id: 'common.please.enter' })}
                          onSelect={this.handleChangeCompany}
                        >
                          <Option value="1001">{this.$t('base.all')}</Option>
                          {/*全部*/}
                          <Option value="1002">{this.$t('base.the.current')}</Option>
                          {/*当前*/}
                          <Option value="1003">
                            {this.$t('base.the.current.and.the.subordinate')}
                          </Option>
                          {/*当前及下属*/}
                          <Option value="1004">{this.$t('base.manually.choose')}</Option>
                          {/*手动选择*/}
                        </Select>
                      )}
                    </FormItem>
                  </Col>
                  {renderCompanyList && (
                    <Col span={10}>
                      <Row>
                        <Col span={6} style={{ marginLeft: 10 }}>
                          <FormItem {...ruleFormLayout} label="">
                            {getFieldDecorator(`filtrateMethod2`, {
                              rules: [],
                              initialValue:
                                ruleDatail.length && ruleDatail[1].filtrateMethod
                                  ? ruleDatail[1].filtrateMethod
                                  : 'INCLUDE',
                            })(
                              <Select placeholder={this.$t({ id: 'common.please.enter' })}>
                                <Option value="INCLUDE">{this.$t('base.contains')}</Option>
                                {/*包含*/}
                                <Option value="EXCLUDE">{this.$t('base.to.rule.out')}</Option>
                                {/*排除*/}
                              </Select>
                            )}
                          </FormItem>
                        </Col>
                        <Col span={4} style={{ marginLeft: '-25px' }}>
                          <FormItem {...ruleFormLayout} label="">
                            {getFieldDecorator('addCompany')(
                              <Button icon={companyIcon} onClick={this.addCompany}>
                                {companyText}
                              </Button>
                            )}
                          </FormItem>
                        </Col>
                      </Row>
                    </Col>
                  )}
                </Row>
                <Row className="rule-form-item">
                  <Col span={4}>
                    <FormItem {...ruleFormLayout} label={this.$t('base.department')} /*部门*/>
                      {getFieldDecorator(`dataScope3`, {
                        rules: [],
                        initialValue: ruleDatail.length ? ruleDatail[2].dataScope : '1001',
                      })(
                        <Select
                          placeholder={this.$t({ id: 'common.please.enter' })}
                          onSelect={this.handleChangeDepartment}
                        >
                          <Option value="1001">{this.$t('base.all')}</Option>
                          {/*全部*/}
                          <Option value="1002">{this.$t('base.the.current')}</Option>
                          {/*当前*/}
                          <Option value="1003">
                            {this.$t('base.the.current.and.the.subordinate')}
                          </Option>
                          {/*当前及下属*/}
                          <Option value="1004">{this.$t('base.manually.choose')}</Option>
                          {/*手动选择*/}
                        </Select>
                      )}
                    </FormItem>
                  </Col>
                  {renderDepartmentList && (
                    <Col span={10}>
                      <Row>
                        <Col span={6} style={{ marginLeft: 10 }}>
                          <FormItem {...ruleFormLayout} label="">
                            {getFieldDecorator(`filtrateMethod3`, {
                              rules: [],
                              initialValue:
                                ruleDatail.length && ruleDatail[2].filtrateMethod
                                  ? ruleDatail[2].filtrateMethod
                                  : 'INCLUDE',
                            })(
                              <Select placeholder={this.$t({ id: 'common.please.enter' })}>
                                <Option value="INCLUDE">{this.$t('base.contains')}</Option>
                                {/*包含*/}
                                <Option value="EXCLUDE">{this.$t('base.to.rule.out')}</Option>
                                {/*排除*/}
                              </Select>
                            )}
                          </FormItem>
                        </Col>
                        <Col span={4} style={{ marginLeft: '-25px' }}>
                          <FormItem {...ruleFormLayout} label="">
                            {getFieldDecorator('addDepartment')(
                              <Button icon={departmentIcon} onClick={this.addDepartment}>
                                {departmentText}
                              </Button>
                            )}
                          </FormItem>
                        </Col>
                      </Row>
                    </Col>
                  )}
                </Row>
                <Row className="rule-form-item">
                  <Col span={4}>
                    <FormItem {...ruleFormLayout} label={this.$t('base.employees')} /*员工*/>
                      {getFieldDecorator(`dataScope4`, {
                        rules: [],
                        initialValue: ruleDatail.length ? ruleDatail[3].dataScope : '1001',
                      })(
                        <Select
                          placeholder={this.$t({ id: 'common.please.enter' })}
                          onSelect={this.handleEmplyee}
                        >
                          <Option value="1001">{this.$t('base.all')}</Option>
                          {/*全部*/}
                          <Option value="1002">{this.$t('base.the.current')}</Option>
                          {/*当前*/}
                          <Option value="1004">{this.$t('base.manually.choose')}</Option>
                          {/*手动选择*/}
                        </Select>
                      )}
                    </FormItem>
                  </Col>
                  {renderEmplyeeList && (
                    <Col span={10}>
                      <Row>
                        <Col span={6} style={{ marginLeft: 10 }}>
                          <FormItem {...ruleFormLayout} label="">
                            {getFieldDecorator(`filtrateMethod4`, {
                              rules: [],
                              initialValue:
                                ruleDatail.length && ruleDatail[3].filtrateMethod
                                  ? ruleDatail[3].filtrateMethod
                                  : 'INCLUDE',
                            })(
                              <Select placeholder={this.$t({ id: 'common.please.enter' })}>
                                <Option value="INCLUDE">{this.$t('base.contains')}</Option>
                                {/*包含*/}
                                <Option value="EXCLUDE">{this.$t('base.to.rule.out')}</Option>
                                {/*排除*/}
                              </Select>
                            )}
                          </FormItem>
                        </Col>
                        <Col span={4} style={{ marginLeft: '-25px' }}>
                          <FormItem {...ruleFormLayout} label="">
                            {getFieldDecorator('addEmpolyee')(
                              <Button icon={emplyeeIcon} onClick={this.addEmployee}>
                                {employeeText}
                              </Button>
                            )}
                          </FormItem>
                        </Col>
                      </Row>
                    </Col>
                  )}
                </Row>
              </Form>
            </Card>
          </div>
        )}
        <LineAddTransferModal
          visible={companyVisible}
          title={this.$t('base.add.the.company')} /*添加公司*/
          onCloseTransferModal={this.cancelCompanyList}
          isAddCompany={true}
          transferList={this.transferCompanyList}
        />
        <LineAddTransferModal
          visible={departMentVisible}
          title={this.$t('base.add.the.department')} /*添加部门*/
          onCloseTransferModal={this.cancelDepartMentList}
          isAddCompany={false}
          transferList={this.transDePferList}
        />
        <ListSelector
          visible={tenantVisible}
          selectorItem={tenantItem}
          onOk={this.handleTenantListOk}
          onCancel={() => this.cancelTenantList(false)}
          showSelectTotal={true}
        />
        <ListSelector
          visible={empolyeeVisible}
          selectorItem={employeeItem}
          onOk={this.handleEmployeeListOk}
          onCancel={() => this.cancelEmployeeList(false)}
          showSelectTotal={true}
        />
      </div>
    );
  }
}

const WrappedNewSubjectSheet = Form.create()(RuleDetailItem);

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewSubjectSheet);