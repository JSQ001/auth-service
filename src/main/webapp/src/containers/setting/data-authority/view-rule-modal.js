import React from 'react';
import { connect } from 'dva';
import {
  Modal,
  Button,
  Row,
  Col,
  Divider,
  Card,
  Form,
  Select,
  Input,
  Spin,
  message,
  Tabs,
} from 'antd';
import BasicInfo from 'widget/basic-info';
import ListSelector from 'components/Widget/list-selector';
import LineAddTransferModal from 'containers/setting/data-authority/line-add-transfer-modal';
import DataAuthorityService from 'containers/setting/data-authority/data-authority.service';
import CustomTable from 'components/Widget/custom-table';
import config from 'config';
const FormItem = Form.Item;
const Option = Select.Option;
const Search = Input.Search;
const TabPane = Tabs.TabPane;

class ViewRuleModal extends React.Component {
  constructor(props) {
    super(props);
    this.targetKey = 0;
    this.state = {
      infoList: [
        {
          //数据权限代码
          type: 'input',
          label: this.$t('base.the.data.access.code') /*数据权限代码*/,
          id: 'dataAuthorityCode',
          disabled: true,
        },
        {
          //数据权限名称
          type: 'input',
          label: this.$t('base.data.access.name') /*数据权限名称*/,
          id: 'dataAuthorityName',
          disabled: false,
        },
        {
          //描述
          type: 'input',
          label: this.$t('base.describe') /*描述*/,
          id: 'description',
          disabled: false,
        },
        {
          //状态
          type: 'switch',
          label: this.$t('base.state') /*状态*/,
          id: 'enabled',
        },
      ],
      infoData: {},
      renderSelectList: false,
      renderCompanyList: false,
      renderDepartmentList: false,
      renderEmplyeeList: false,
      show: true,
      tabListNoTitle: [
        {
          key: 'SOB',
          tab: this.$t('base.zhang.set.of.permissions') /*账套权限*/,
        },
        {
          key: 'COMPANY',
          tab: this.$t("base.company's.rights") /*公司权限*/,
        },
        {
          key: 'UNIT',
          tab: this.$t('base.department.permission') /*部门权限*/,
        },
        {
          key: 'EMPLOYEE',
          tab: this.$t('base.employee.rights') /*员工权限*/,
        },
      ],
      tabVal: 'SOB',
      renderNewChangeRules: [],
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
      editKey: '',
      ruleDetail: [],
      sobColumns: [
        {
          title: this.$t('base.zhang.set.of.code') /*账套代码*/,
          dataIndex: 'valueKeyCode',
        },
        {
          title: this.$t('base.zhang.set.of.names') /*账套名称*/,
          dataIndex: 'valueKeyDesc',
        },
      ],
      companyColumns: [
        {
          title: this.$t('base.company.code') /*公司代码*/,
          dataIndex: 'valueKeyCode',
        },
        {
          title: this.$t('base.the.name.of.the.company') /*公司名称*/,
          dataIndex: 'valueKeyDesc',
        },
      ],
      unitColumns: [
        {
          title: this.$t('base.department.code') /*部门代码*/,
          dataIndex: 'valueKeyCode',
        },
        {
          title: this.$t('base.department.name') /*部门名称*/,
          dataIndex: 'valueKeyDesc',
        },
      ],
      emplyeeColumns: [
        {
          title: this.$t('base.staff.code') /*员工代码*/,
          dataIndex: 'valueKeyCode',
        },
        {
          title: this.$t('base.staff.name') /*员工名称*/,
          dataIndex: 'valueKeyDesc',
        },
      ],
      dataTypeValue: '',
      loading: true,
      keyWord: '',
      ruleName: '',
      ruleDatail: [],
      renderSobList: false,
      getRulesArr: [],
      tenantVisible: false,
      tenantItem: {},
      sobText: this.$t('base.add.account.set') /*添加账套*/,
      sobIcon: 'plus',
      companyIcon: 'plus',
      companyText: this.$t('base.add.the.company') /*添加公司*/,
      companyVisible: false,
      companyItemsKeys: [],
      departmentIcon: 'plus',
      departmentText: this.$t('base.add.the.department') /*添加部门*/,
      departMentVisible: false,
      departMentItemsKeys: [],
      emplyeeIcon: 'plus',
      empolyeeVisible: false,
      employeeItem: {},
      employeeKeys: [],
      employeeText: this.$t('base.add.employees') /*添加员工*/,
      ruleId: '',
      deleted: undefined,
      versionNumber: undefined,
      createdBy: undefined,
      createdDate: undefined,
      lastUpdatedBy: undefined,
      lastUpdatedDate: undefined,
      sobValuesKeys: [],
      renderRuleInfo: {},
      selectedTenantList: [],
      selectedEmployeeList: [],
      selectedTreeInfo: [],
      selectedCompanyTreeInfo: [],
      selectedDepTreeInfo: [],
    };
  }
  componentWillMount() {
    DataAuthorityService.getSingleDataAuthorityDetail(this.props.dataId, this.props.targetId).then(
      res => {
        if (res.status === 200) {
          if (res.data.dataAuthorityRules[0].dataAuthorityRuleDetails[0].dataScope === '1004') {
            this.setState({
              sobColumns: [
                {
                  title: this.$t('base.zhang.set.of.code') /*账套代码*/,
                  dataIndex: 'valueKeyCode',
                },
                {
                  title: this.$t('base.zhang.set.of.names') /*账套名称*/,
                  dataIndex: 'valueKeyDesc',
                },
                {
                  title: this.$t('base.state.authority') /*权限状态*/,
                  dataIndex: 'filtrateMethodDesc',
                },
              ],
            });
          }
          if (res.data.dataAuthorityRules[0].dataAuthorityRuleDetails[1].dataScope === '1004') {
            this.setState({
              companyColumns: [
                {
                  title: this.$t('base.company.code') /*公司代码*/,
                  dataIndex: 'valueKeyCode',
                },
                {
                  title: this.$t('base.the.name.of.the.company') /*公司名称*/,
                  dataIndex: 'valueKeyDesc',
                },
                {
                  title: this.$t('base.state.authority') /*权限状态*/,
                  dataIndex: 'filtrateMethodDesc',
                },
              ],
            });
          }
          if (res.data.dataAuthorityRules[0].dataAuthorityRuleDetails[2].dataScope === '1004') {
            this.setState({
              unitColumns: [
                {
                  title: this.$t('base.department.code') /*部门代码*/,
                  dataIndex: 'valueKeyCode',
                },
                {
                  title: this.$t('base.department.name') /*部门名称*/,
                  dataIndex: 'valueKeyDesc',
                },
                {
                  title: this.$t('base.state.authority') /*权限状态*/,
                  dataIndex: 'filtrateMethodDesc',
                },
              ],
            });
          }
          if (res.data.dataAuthorityRules[0].dataAuthorityRuleDetails[3].dataScope === '1004') {
            this.setState({
              emplyeeColumns: [
                {
                  title: this.$t('base.staff.code') /*员工代码*/,
                  dataIndex: 'valueKeyCode',
                },
                {
                  title: this.$t('base.staff.name') /*员工名称*/,
                  dataIndex: 'valueKeyDesc',
                },
                {
                  title: this.$t('base.state.authority') /*权限状态*/,
                  dataIndex: 'filtrateMethodDesc',
                },
              ],
            });
          }
          this.setState({
            loading: false,
            infoData: res.data,
            renderRuleInfo: res.data,
            ruleDetail: res.data.dataAuthorityRules[0].dataAuthorityRuleDetails,
            tabVal: 'SOB',
            ruleName: res.data.dataAuthorityRules[0].dataAuthorityRuleName,
            ruleDatail: res.data.dataAuthorityRules[0].dataAuthorityRuleDetails,
            getRulesArr: res.data.dataAuthorityRules[0],
            ruleId: res.data.id,
            deleted: res.data.deleted,
            versionNumber: res.data.versionNumber,
            createdBy: res.data.createdBy,
            createdDate: res.data.createdDate,
            lastUpdatedBy: res.data.lastUpdatedBy,
            lastUpdatedDate: res.data.lastUpdatedDate,
          });
        }
      }
    );
  }

  onCloseRuleModal = () => {
    this.props.closeRuleModal();
  };
  onBackRuleModal = () => {
    this.props.backRuleModal();
  };
  removeRule = () => {
    this.setState({
      show: true,
    });
  };
  /**选中手动选择 */
  handleChangeRuleChange = value => {
    if (value === '1004') {
      this.setState({
        renderSelectList: true,
      });
    } else {
      this.setState({
        renderSelectList: false,
        selectedTenantList: [],
        sobText: this.$t('base.add.account.set') /*添加账套*/,
        sobIcon: 'plus',
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
        selectedCompanyTreeInfo: [],
        companyText: this.$t('base.add.the.company') /*添加公司*/,
        companyIcon: 'plus',
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
        selectedDepTreeInfo: [],
        departmentText: this.$t('base.add.the.department') /*添加部门*/,
        departmentIcon: 'plus',
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
        selectedEmployeeList: [],
        employeeText: this.$t('base.add.employees') /*添加员工*/,
        emplyeeIcon: 'plus',
      });
    }
  };
  onTabChange = key => {
    this.setState({
      tabVal: key,
      // dataTypeValue: key
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
      selectedTenantList: result.result,
    });
  };
  cancelTenantList = flag => {
    this.setState({
      tenantVisible: flag,
    });
  };
  /**刷新表格 */
  refresh = ruleDatail => {
    this.setState({ ruleDetail: ruleDatail }, () => {
      let { tabVal, ruleDetail } = this.state;
      if (tabVal === 'SOB') {
        if (ruleDetail[0].dataScope === '1004') {
          this.setState({
            sobColumns: [
              {
                title: this.$t('base.zhang.set.of.code') /*账套代码*/,
                dataIndex: 'valueKeyCode',
              },
              {
                title: this.$t('base.zhang.set.of.names') /*账套名称*/,
                dataIndex: 'valueKeyDesc',
              },
              {
                title: this.$t('base.state.authority') /*权限状态*/,
                dataIndex: 'filtrateMethodDesc',
              },
            ],
          });
        } else {
          this.setState({
            sobColumns: [
              {
                title: this.$t('base.zhang.set.of.code') /*账套代码*/,
                dataIndex: 'valueKeyCode',
              },
              {
                title: this.$t('base.zhang.set.of.names') /*账套名称*/,
                dataIndex: 'valueKeyDesc',
              },
            ],
          });
        }
        this.sobTable.search();
      }
      if (tabVal === 'COMPANY') {
        if (ruleDetail[1].dataScope === '1004') {
          this.setState({
            companyColumns: [
              {
                title: this.$t('base.company.code') /*公司代码*/,
                dataIndex: 'valueKeyCode',
              },
              {
                title: this.$t('base.the.name.of.the.company') /*公司名称*/,
                dataIndex: 'valueKeyDesc',
              },
              {
                title: this.$t('base.state.authority') /*权限状态*/,
                dataIndex: 'filtrateMethodDesc',
              },
            ],
          });
        } else {
          this.setState({
            companyColumns: [
              {
                title: this.$t('base.company.code') /*公司代码*/,
                dataIndex: 'valueKeyCode',
              },
              {
                title: this.$t('base.the.name.of.the.company') /*公司名称*/,
                dataIndex: 'valueKeyDesc',
              },
            ],
          });
        }
        this.companyTable.search();
      }
      if (tabVal === 'UNIT') {
        if (ruleDetail[2].dataScope === '1004') {
          this.setState({
            unitColumns: [
              {
                title: this.$t('base.department.code') /*部门代码*/,
                dataIndex: 'valueKeyCode',
              },
              {
                title: this.$t('base.department.name') /*部门名称*/,
                dataIndex: 'valueKeyDesc',
              },
              {
                title: this.$t('base.state.authority') /*权限状态*/,
                dataIndex: 'filtrateMethodDesc',
              },
            ],
          });
        } else {
          this.setState({
            unitColumns: [
              {
                title: this.$t('base.department.code') /*部门代码*/,
                dataIndex: 'valueKeyCode',
              },
              {
                title: this.$t('base.department.name') /*部门名称*/,
                dataIndex: 'valueKeyDesc',
              },
            ],
          });
        }

        this.unitTable.search();
      }
      if (tabVal === 'EMPLOYEE') {
        if (ruleDetail[3].dataScope === '1004') {
          this.setState({
            emplyeeColumns: [
              {
                title: this.$t('base.staff.code') /*员工代码*/,
                dataIndex: 'valueKeyCode',
              },
              {
                title: this.$t('base.staff.name') /*员工名称*/,
                dataIndex: 'valueKeyDesc',
              },
              {
                title: this.$t('base.state.authority') /*权限状态*/,
                dataIndex: 'filtrateMethodDesc',
              },
            ],
          });
        } else {
          this.setState({
            emplyeeColumns: [
              {
                title: this.$t('base.staff.code') /*员工代码*/,
                dataIndex: 'valueKeyCode',
              },
              {
                title: this.$t('base.staff.name') /*员工名称*/,
                dataIndex: 'valueKeyDesc',
              },
            ],
          });
        }
        this.employeeTable.search();
      }
    });
  };
  /**按照账套代码/名称查询 */
  onSobDetailSearch = value => {
    this.setState(
      {
        keyWord: value,
      },
      () => {
        this.sobTable.search();
      }
    );
  };
  /**按照公司代码/名称查询 */
  onCompanyDetailSearch = value => {
    this.setState(
      {
        keyWord: value,
      },
      () => {
        this.companyTable.search();
      }
    );
  };
  /**按照部门代码/名称查询 */
  onUnitDetailSearch = value => {
    this.setState(
      {
        keyWord: value,
      },
      () => {
        this.unitTable.search();
      }
    );
  };
  /**按照员工代码/名称查询 */
  onEmployeeDetailSearch = value => {
    this.setState(
      {
        keyWord: value,
      },
      () => {
        this.employeeTable.search();
      }
    );
  };
  /**详情页面权限规则编辑 */
  editRuleItem = () => {
    let { ruleDatail } = this.state;
    if (ruleDatail[0].dataScope === '1004') {
      let detaileValues0 = ruleDatail[0].dataAuthorityRuleDetailValues;
      let ruleDetailValueDTOs0 = ruleDatail[0].dataAuthorityRuleDetailValueDTOs;
      this.setState({
        renderSobList: true,
        sobText: this.$t('base.desc.code6', { total: detaileValues0.length }), // `已选择${detaileValues0.length}个账套`
        sobIcon: null,
        selectedTenantList: ruleDetailValueDTOs0,
        sobValuesKeys: detaileValues0,
      });
    }
    if (ruleDatail[1].dataScope === '1004') {
      let detaileValues1 = ruleDatail[1].dataAuthorityRuleDetailValues;
      let ruleDetailValueDTOs1 = ruleDatail[1].dataAuthorityRuleDetailValueDTOs;
      this.setState({
        renderCompanyList: true,
        companyText: this.$t('base.desc.code7', { total: detaileValues1.length }), // `已选择${detaileValues1.length}个公司`
        companyIcon: null,
        selectedCompanyTreeInfo: ruleDetailValueDTOs1,
        companyItemsKeys: detaileValues1,
      });
    }
    if (ruleDatail[2].dataScope === '1004') {
      let detaileValues2 = ruleDatail[2].dataAuthorityRuleDetailValues;
      let ruleDetailValueDTOs2 = ruleDatail[2].dataAuthorityRuleDetailValueDTOs;
      this.setState({
        renderDepartmentList: true,
        departmentText: this.$t('base.desc.code4', { total: detaileValues2.length }), // `已选择${detaileValues2.length}个部门`
        departmentIcon: null,
        selectedDepTreeInfo: ruleDetailValueDTOs2,
        departMentItemsKeys: detaileValues2,
      });
    }
    if (ruleDatail[3].dataScope === '1004') {
      let detaileValues3 = ruleDatail[3].dataAuthorityRuleDetailValues;
      let ruleDetailValueDTOs3 = ruleDatail[3].dataAuthorityRuleDetailValueDTOs;
      this.setState({
        renderEmplyeeList: true,
        employeeText: this.$t('base.desc.code3', { total: detaileValues3.length }), // `已选择${detaileValues3.length}个员工`
        emplyeeIcon: null,
        selectedEmployeeList: ruleDetailValueDTOs3,
        employeeKeys: detaileValues3,
      });
    }
    this.setState({
      show: false,
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
  //添加账套
  addTenant = () => {
    const ruleId = this.state.getRulesArr.id;
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
      if (resultArr[i].valueKey) {
        arr.push(resultArr[i].valueKey);
      } else {
        arr.push(resultArr[i].id);
      }
    }
    this.setState({
      companyItemsKeys: arr,
      companyVisible: false,
      companyText: this.$t('base.desc.code11', { total: resultArr.length }), //已选择${resultArr.length}个公司
      companyIcon: null,
      selectedCompanyTreeInfo: items,
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
      if (resultArr[i].valueKey) {
        arr.push(resultArr[i].valueKey);
      } else {
        arr.push(resultArr[i].id);
      }
    }
    this.setState({
      departMentItemsKeys: arr,
      departMentVisible: false,
      departmentText: this.$t('base.desc.code12', { total: resultArr.length }), // 已选择${resultArr.length}个部门
      departmentIcon: null,
      selectedDepTreeInfo: items,
    });
  };
  //添加员工
  addEmployee = () => {
    const ruleId = this.state.getRulesArr.id;
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
      key: 'valueKey',
    };
    this.setState({
      empolyeeVisible: true,
      employeeItem,
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
      employeeText: this.$t('base.desc.code3', { total: resultArr.length }), // 已选择${resultArr.length}个员工
      emplyeeIcon: null,
      selectedEmployeeList: result.result,
    });
  };
  cancelEmployeeList = () => {
    this.setState({
      empolyeeVisible: false,
    });
  };
  /**保存单条规则 */
  saveRuleItem = e => {
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
                  this.refresh(this.state.ruleDatail);
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

  render() {
    const { visibel } = this.props;
    const {
      infoList,
      infoData,
      dataTypeValue,
      loading,
      key,
      tenantItem,
      renderRuleInfo,
      dataType,
      sobText,
      ruleDetail,
      tabListNoTitle,
      tenantVisible,
      keyWord,
      columns,
      show,
      ruleName,
      ruleDatail,
      departmentIcon,
      sobIcon,
      renderSobList,
      companyIcon,
      companyVisible,
      renderCompanyList,
      companyText,
      renderDepartmentList,
      departmentText,
      departMentVisible,
      renderEmplyeeList,
      emplyeeIcon,
      empolyeeVisible,
      employeeItem,
      employeeText,
      selectedTenantList,
      selectedEmployeeList,
      selectedDepTreeInfo,
      selectedCompanyTreeInfo,
      sobColumns,
      companyColumns,
      unitColumns,
      emplyeeColumns,
      tabVal,
    } = this.state;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const ruleFormLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 16, offset: 1 },
    };
    return (
      <Modal
        visible={visibel}
        footer={[
          <Button key="back" onClick={this.onBackRuleModal}>
            {this.$t({ id: 'common.ok' } /* 返回*/)}
          </Button>,
        ]}
        width={1200}
        destroyOnClose={true}
        closable={false}
        onCancel={this.onCloseRuleModal}
      >
        <div>
          <BasicInfo infoList={infoList} isHideEditBtn={true} infoData={infoData} colSpan={6} />
          <Spin spinning={loading} style={{ marginTop: 24 }}>
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
                          <a type="primary" onClick={e => this.saveRuleItem(e)}>
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
                          <FormItem
                            {...ruleFormLayout}
                            label={this.$t('base.the.company')} /*公司*/
                          >
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
            </div>
          </Spin>
          <Tabs defaultActiveKey={tabVal} onChange={this.onTabChange}>
            <TabPane tab={this.$t('base.zhang.set.of.permissions')} key="SOB">
              {/*账套权限*/}
              <div>
                <Row style={{ margin: 20 }}>
                  <Col span={18}>
                    {ruleDetail.length ? ruleDetail[0].dataScopeDesc : null}
                    {ruleDetail.length ? dataType[ruleDetail[0].dataType].label : null}
                  </Col>
                  <Col span={6}>
                    <Search
                      placeholder="请输入账套代码/名称" /*请输入账套代码 名称*/
                      onSearch={this.onSobDetailSearch}
                      enterButton
                    />
                  </Col>
                </Row>

                <div style={{ margin: 20 }}>
                  <CustomTable
                    columns={sobColumns}
                    url={`${config.baseUrl}/api/data/authority/rule/detail/values?ruleId=${
                      this.props.targetId
                    }&dataType=SOB&keyWord=${keyWord}`}
                    ref={ref => (this.sobTable = ref)}
                  />
                </div>
              </div>
            </TabPane>
            <TabPane tab={this.$t("base.company's.rights")} key="COMPANY">
              {/*公司权限*/}
              <div>
                <Row style={{ margin: 20 }}>
                  <Col span={18}>
                    {ruleDetail.length ? ruleDetail[1].dataScopeDesc : null}
                    {ruleDetail.length ? dataType[ruleDetail[1].dataType].label : null}
                  </Col>
                  <Col span={6}>
                    <Search
                      placeholder={this.$t('base.desc.code1')} /*请输入公司代码 名称*/
                      onSearch={this.onCompanyDetailSearch}
                      enterButton
                    />
                  </Col>
                </Row>

                <div style={{ margin: 20 }}>
                  <CustomTable
                    columns={companyColumns}
                    url={`${config.baseUrl}/api/data/authority/rule/detail/values?ruleId=${
                      this.props.targetId
                    }&dataType=COMPANY&keyWord=${keyWord}`}
                    ref={ref => (this.companyTable = ref)}
                  />
                </div>
              </div>
            </TabPane>
            <TabPane tab={this.$t('base.department.permission')} key="UNIT">
              {/*部门权限*/}
              <div>
                <Row style={{ margin: 20 }}>
                  <Col span={18}>
                    {ruleDetail.length ? ruleDetail[2].dataScopeDesc : null}
                    {ruleDetail.length ? dataType[ruleDetail[2].dataType].label : null}
                  </Col>
                  <Col span={6}>
                    <Search
                      placeholder={this.$t('base.desc.code2')} /*请输入部门代码 名称*/
                      onSearch={this.onUnitDetailSearch}
                      enterButton
                    />
                  </Col>
                </Row>

                <div style={{ margin: 20 }}>
                  <CustomTable
                    columns={unitColumns}
                    url={`${config.baseUrl}/api/data/authority/rule/detail/values?ruleId=${
                      this.props.targetId
                    }&dataType=UNIT&keyWord=${keyWord}`}
                    ref={ref => (this.unitTable = ref)}
                  />
                </div>
              </div>
            </TabPane>
            <TabPane tab={this.$t('base.employee.rights')} key="EMPLOYEE">
              {/*员工权限*/}
              <div>
                <Row style={{ margin: 20 }}>
                  <Col span={18}>
                    {ruleDetail.length ? ruleDetail[3].dataScopeDesc : null}
                    {ruleDetail.length ? dataType[ruleDetail[3].dataType].label : null}
                  </Col>
                  <Col span={6}>
                    <Search
                      placeholder={this.$t('base.desc.code15')} /*请输入员工代码 名称*/
                      onSearch={this.onEmployeeDetailSearch}
                      enterButton
                    />
                  </Col>
                </Row>
                <div style={{ margin: 20 }}>
                  <CustomTable
                    columns={emplyeeColumns}
                    url={`${config.baseUrl}/api/data/authority/rule/detail/values?ruleId=${
                      this.props.targetId
                    }&dataType=EMPLOYEE&keyWord=${keyWord}`}
                    ref={ref => (this.employeeTable = ref)}
                  />
                </div>
              </div>
            </TabPane>
          </Tabs>,
          <ListSelector
            visible={tenantVisible}
            selectorItem={tenantItem}
            onOk={this.handleTenantListOk}
            onCancel={() => this.cancelTenantList(false)}
            showSelectTotal={true}
            selectedData={selectedTenantList}
          />
          <LineAddTransferModal
            visible={companyVisible}
            title={this.$t('base.add.the.company')} /*添加公司*/
            onCloseTransferModal={this.cancelCompanyList}
            isAddCompany={true}
            transferList={this.transferCompanyList}
            selectedTreeInfo={selectedCompanyTreeInfo}
          />
          <LineAddTransferModal
            visible={departMentVisible}
            title={this.$t('base.add.the.department')} /*添加部门*/
            onCloseTransferModal={this.cancelDepartMentList}
            isAddCompany={false}
            transferList={this.transDePferList}
            selectedTreeInfo={selectedDepTreeInfo}
          />
          <ListSelector
            visible={empolyeeVisible}
            selectorItem={employeeItem}
            onOk={this.handleEmployeeListOk}
            onCancel={() => this.cancelEmployeeList(false)}
            showSelectTotal={true}
            selectedData={selectedEmployeeList}
          />
        </div>
      </Modal>
    );
  }
}

const WrappedLineModelChangeRules = Form.create()(ViewRuleModal);

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
)(WrappedLineModelChangeRules);
