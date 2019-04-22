// 为了0416迭代上线，重构此文件
import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import config from 'config';
import { Form, Button, Icon, Tabs, message, Popover, Checkbox, Badge } from 'antd';
import Table from 'widget/table';
import 'styles/budget-setting/budget-organization/budget-structure/budget-structure-detail.scss';
import ListSelector from 'components/Widget/list-selector';
import companyMaintainService from 'containers/enterprise-manage/company-maintain/company-maintain.service';
const TabPane = Tabs.TabPane;

class WrappedCompanyMaintainDetail extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      showImportFrame: false,
      saving: false,
      loading: true,
      pagination: {
        current: 1,
        page: 0,
        total: 0,
        pageSize: 10,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total: total,
          }),
      },
      selectedRowKeys: [],
      tabs: [
        {
          key: 'BANK',
          name: this.$t('company.maintain.bankAccountInfo1'),
        } /*银行账户信息*/,
        {
          key: 'USER',
          name: this.$t('company.maintain.userInfo1'),
        } /*员工信息*/,
      ],
      data: [],
      tabsData: {
        BANK: {
          rowSelection: null,
          columns: [
            {
              //银行名称
              title: this.$t('company.maintain.bank.account.bankName'),
              dataIndex: 'bankName',
              width: '15%',
              render: desc => (
                <span>
                  <Popover content={desc}>{desc ? desc : '-'}</Popover>
                </span>
              ),
            },
            {
              //支行名称
              title: this.$t('bank.bankBranchName'),
              dataIndex: 'bankBranchName',
              width: '15%',
              render: bankBranchName => (
                <span>
                  <Popover content={bankBranchName}>
                    {bankBranchName ? bankBranchName : '-'}
                  </Popover>
                </span>
              ),
            },
            {
              //国家
              title: this.$t('company.maintain.bank.account.country'),
              dataIndex: 'country',
              width: '15%',
              render: desc => (
                <span>
                  <Popover content={desc}>{desc ? desc : '-'}</Popover>
                </span>
              ),
            },
            {
              //开户地
              title: this.$t('company.maintain.bank.account.opening'),
              dataIndex: 'bankAddress',
              width: '15%',
            },
            {
              //详细地址
              title: this.$t('company.maintain.bank.account.bankAddress'),
              dataIndex: 'accountOpeningAddress',
              width: '15%',
              render: desc => (
                <span>
                  <Popover content={desc}>{desc ? desc : '-'}</Popover>
                </span>
              ),
            },
            {
              //账户名称
              title: this.$t('company.maintain.bank.account.bankAccountName'),
              dataIndex: 'bankAccountName',
              width: '15%',
              render: desc => (
                <span>
                  <Popover content={desc}>{desc ? desc : '-'}</Popover>
                </span>
              ),
            },
            {
              //银行账户账号
              title: this.$t('company.maintain.bank.account.bankAccountNumber'),
              dataIndex: 'bankAccountNumber',
              width: '15%',
              render: desc => (
                <span>
                  <Popover content={desc}>{desc ? desc : '-'}</Popover>
                </span>
              ),
            },
            {
              //swiftCode
              title: this.$t('company.maintain.bank.account.swiftCode'),
              dataIndex: 'swiftCode',
              width: '12%',
              render: desc => (
                <span>
                  <Popover content={desc}>{desc ? desc : '-'}</Popover>
                </span>
              ),
            },
            {
              title: this.$t('company.maintain.bank.account.remark'),
              dataIndex: 'remark',
              width: '15%',
              render: remark => <Popover content={remark}>{remark ? remark : '-'}</Popover>,
            },
            {
              title: this.$t('common.operation'),
              dataIndex: 'operation',
              width: '10%',
              render: (text, record) => (
                <span>
                  <a onClick={e => this.editItem(e, record)}>{this.$t('common.edit')}</a>
                </span>
              ),
            },
          ],
        },
        USER: {
          rowSelection: {
            type: 'checkbox',
            selectedRowKeys: [],
            onChange: this.onSelectChange,
          },
          columns: [
            {
              title: this.$t('company.maintain.fullName1'),
              key: 'fullName',
              dataIndex: 'fullName',
              width: '16%',
            } /*姓名*/,
            {
              title: this.$t('company.maintain.id1'),
              key: 'id',
              dataIndex: 'employeeId',
              width: '8%',
            } /*工号*/,
            {
              title: this.$t('company.maintain.departmentName1'),
              key: 'departmentName',
              dataIndex: 'departmentName',
              width: '10%',
            } /*部门名称*/,
            {
              title: this.$t('company.maintain.mobile1'),
              key: 'mobile',
              dataIndex: 'mobile',
              width: '10%',
            } /*联系方式*/,
            {
              title: this.$t('company.maintain.email1'),
              key: 'email',
              dataIndex: 'email',
              width: '10%',
            } /*邮箱*/,
            {
              title: this.$t('common.column.status'), // "状态",
              key: 'status',
              dataIndex: 'status',
              render: text => {
                if (text === 1001) {
                  return (
                    <span>
                      <Popover
                        placement="topLeft"
                        content={this.$t('person.manage.working.person')}
                      >
                        {this.$t('person.manage.working.person')}
                      </Popover>
                    </span>
                  );
                } else if (text === 1002) {
                  return (
                    <span>
                      <Popover
                        placement="topLeft"
                        content={this.$t('person.manage.will.go.person')}
                      >
                        {this.$t('person.manage.will.go.person')}
                      </Popover>
                    </span>
                  );
                } else if (text === 1003) {
                  return (
                    <span>
                      <Popover placement="topLeft" content={this.$t('person.manage.gone.person')}>
                        {this.$t('person.manage.gone.person')}
                      </Popover>
                    </span>
                  );
                }
              },
            },
          ],
        },
        // 部门
        DEPARTMENT: {
          columns: [
            {
              title: this.$t('company.associate.department.departmentCode'),
              key: 'departmentCode',
              dataIndex: 'departmentCode',
              tooltips: true,
              width: 100,
            } /*部门代码*/,
            {
              title: this.$t('company.associate.department.departmentName'),
              key: 'departmentName',
              dataIndex: 'departmentName',
              tooltips: true,
              width: 120,
            } /*部门名称*/,
            {
              title: this.$t('company.associate.department.parentDepartmentName'),
              key: 'parentDepartmentName',
              dataIndex: 'parentDepartmentName',
              width: 120,
              tooltips: true,
            } /*上级部门*/,
            {
              title: this.$t('company.associate.department.responsibilityName'),
              key: 'responsibilityName',
              dataIndex: 'responsibilityName',
              width: 120,
              tooltips: true,
            } /*默认责任中心*/,
            {
              title: this.$t('company.associate.department.responsibilityInfo'),
              key: 'responsibilityInfo',
              dataIndex: 'responsibilityInfo',
              width: 80,
              align: 'center',
              render: (value, record) => {
                return <a onClick={() => this.viewresponsibilityInfo(record)}>责任中心</a>;
              },
            } /*员工信息*/,
            {
              title: this.$t('company.associate.department.employeeInfo'),
              key: 'employeeInfo',
              dataIndex: 'employeeInfo',
              width: 80,
              align: 'center',
              render: (value, record) => {
                return <a onClick={() => this.viewEmployeeInfo(record)}>员工信息</a>;
              },
            } /*员工信息*/,
            {
              title: this.$t('common.column.status') /*状态*/,
              dataIndex: 'enabled',
              width: 40,
              align: 'center',
              render: (enabled, record) => (
                <Checkbox
                  defaultChecked={enabled}
                  onChange={e => this.handleStatusChange(e, record)}
                />
              ),
            },
          ],
        },
      },
      // rowSelection: {
      //   type: 'checkbox',
      //   selectedRowKeys: [],
      //   onChange: this.onSelectChange,
      // },
      nowStatus: 'BANK',
      showAssociateDepartment: false, // 添加公司部门维护弹出框
      departmentSelectItem: {
        title: this.$t('company.associate.department.addDepartment'), //添加部门
        url: `${config.mdataUrl}/api/company/associate/department/query/canAssociate`,
        searchForm: [
          {
            type: 'input',
            id: 'codeName',
            label: this.$t('company.associate.department.departmentCodeName' /*部门代码\/名称*/),
          },
          {
            type: 'input',
            id: 'codeFrom',
            label: this.$t('company.associate.department.departmentCodeFrom' /*部门代码从*/),
          },
          {
            type: 'input',
            id: 'codeTo',
            label: this.$t('company.associate.department.departmentCodeTo' /*部门代码至*/),
          },
        ],
        columns: [
          {
            title: this.$t('company.associate.department.departmentCode'),
            key: 'departmentCode',
            dataIndex: 'departmentCode',
            tooltips: true,
            width: 100,
          } /*部门代码*/,
          {
            title: this.$t('company.associate.department.departmentName'),
            key: 'departmentName',
            dataIndex: 'departmentName',
            tooltips: true,
            width: 120,
          } /*部门名称*/,
          {
            title: this.$t('company.associate.department.parentDepartmentName'),
            key: 'parentDepartmentName',
            dataIndex: 'parentDepartmentName',
            width: 120,
            tooltips: true,
          } /*上级部门*/,
          {
            title: this.$t('common.column.status') /*状态*/,
            dataIndex: 'enabled',
            width: 70,
            align: 'center',
            render: enabled => (
              <Badge
                status={enabled ? 'success' : 'error'}
                text={enabled ? this.$t('common.status.enable') : this.$t('common.status.disable')}
              />
            ),
          },
        ],
        key: 'departmentId',
      },
      //公司部门员工详细
      contactSelectItem: {
        title: this.$t('company.associate.department.userInfoTitle'), //员工信息
        url: `${config.mdataUrl}/api/company/associate/department/query/contact`,
        searchForm: [
          {
            type: 'input',
            id: 'codeName',
            label: this.$t('company.associate.department.userCodeName' /*员工姓名/工号*/),
          },
          {
            id: 'dutyCode',
            label: this.$t('company.associate.department.userDuty' /*职务*/),
            type: 'value_list',
            options: [],
            valueListCode: 1002,
          },
          {
            type: 'select',
            id: 'status',
            label: this.$t('common.column.status'),
            options: [
              { label: this.$t('person.manage.working.person'), value: 1001 },
              { label: this.$t('person.manage.will.go.person'), value: 1002 },
              { label: this.$t('person.manage.gone.person'), value: 1003 },
            ],
            labelKey: 'label',
            valueKey: 'value',
          },
        ],
        columns: [
          {
            title: this.$t('company.maintain.fullName1'),
            key: 'fullName',
            dataIndex: 'fullName',
            width: 120,
          } /*姓名*/,
          {
            title: this.$t('company.maintain.id1'),
            key: 'id',
            dataIndex: 'employeeId',
            width: 80,
          } /*工号*/,
          {
            title: this.$t('company.maintain.mobile1'),
            key: 'mobile',
            dataIndex: 'mobile',
            width: 120,
          } /*联系方式*/,
          {
            title: this.$t('company.maintain.email1'),
            key: 'email',
            dataIndex: 'email',
            width: 100,
          } /*邮箱*/,
          {
            title: this.$t('company.associate.department.userDuty'),
            key: 'duty',
            dataIndex: 'duty',
            width: 100,
          } /*职务*/,
          {
            title: this.$t('common.column.status'), // "状态",
            key: 'status',
            dataIndex: 'status',
            render: text => {
              if (text === 1001) {
                return (
                  <span>
                    <Popover placement="topLeft" content={this.$t('person.manage.working.person')}>
                      {this.$t('person.manage.working.person')}
                    </Popover>
                  </span>
                );
              } else if (text === 1002) {
                return (
                  <span>
                    <Popover placement="topLeft" content={this.$t('person.manage.will.go.person')}>
                      {this.$t('person.manage.will.go.person')}
                    </Popover>
                  </span>
                );
              } else if (text === 1003) {
                return (
                  <span>
                    <Popover placement="topLeft" content={this.$t('person.manage.gone.person')}>
                      {this.$t('person.manage.gone.person')}
                    </Popover>
                  </span>
                );
              }
            },
          },
        ],
        key: 'id',
      },
      departmentId: null,
      showContactInfo: false,
      showResponsibilityInfo: false,
      //公司部门责任中心详情
      responsibilityInfoSelectItem: {
        title: this.$t('company.associate.department.responsibilityInfo'), //员工信息
        url: `${config.mdataUrl}/api/company/associate/department/query/responsibility`,
        searchForm: [
          {
            type: 'input',
            id: 'codeName',
            label: this.$t(
              'company.associate.department.responsibilityCodeName' /*责任中心代码名称/工号*/
            ),
          },
        ],
        columns: [
          {
            title: this.$t('company.associate.department.responsibilityCode'),
            key: 'code',
            dataIndex: 'code',
            width: 120,
          } /*责任中心代码*/,
          {
            title: this.$t('company.associate.department.responsibilityName'),
            key: 'name',
            dataIndex: 'name',
            width: 150,
          } /*责任中心名称*/,
          {
            title: this.$t('common.column.status') /*状态*/,
            dataIndex: 'enabled',
            width: 70,
            align: 'center',
            render: enabled => (
              <Badge
                status={enabled ? 'success' : 'error'}
                text={enabled ? this.$t('common.status.enable') : this.$t('common.status.disable')}
              />
            ),
          },
        ],
        key: 'id',
      },
      // newBankAccountPage: menuRoute.getRouteItem('new-bank-account', 'key'), //新建银行账户
    };
  }

  componentWillMount() {
    if (this.props.company.companyUnitFlag) {
      let departmentTab = {
        key: 'DEPARTMENT',
        name: this.$t('company.associate.department.departmentInfo'),
      };
      let { tabs } = this.state;
      tabs.push(departmentTab);
      this.setState({ tabs });
    }
  }
  componentDidMount() {
    this.getList(this.state.nowStatus);
  }

  //根据tab的key获取对应的列表
  getList = key => {
    if (key === 'BANK') {
      this.getBankAccountInfo();
    } else if (key === 'USER') {
      this.getUserInfo();
    } else {
      this.getDepartmentInfo();
    }
  };
  //获取银行账户列表
  getBankAccountInfo = () => {
    const { pagination } = this.state;
    let params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      companyId: this.props.match.params.companyId,
    };
    companyMaintainService.getBankAccountInfo(params).then(response => {
      response.data.map((item, index) => {
        item.key = item.id ? item.id : index;
      });
      pagination.total = Number(response.headers['x-total-count']);
      this.setState({
        data: response.data,
        loading: false,
        pagination,
      });
    });
  };
  //获取公司人员
  getUserInfo = () => {
    const { pagination } = this.state;
    let params = {
      page: pagination.page,
      size: pagination.pageSize,
      corporationOid: this.props.match.params.companyOId,
    };
    companyMaintainService.getUserInfo(params).then(response => {
      response.data.map((item, index) => {
        item.key = item.id ? item.id : index;
      });
      pagination.total = Number(response.headers['x-total-count']);
      this.setState({
        data: response.data,
        loading: false,
        pagination,
      });
    });
  };
  //获取公司部门
  getDepartmentInfo = () => {
    const { pagination } = this.state;
    let params = {
      page: pagination.page,
      size: pagination.pageSize,
      companyId: this.props.match.params.companyId,
    };
    companyMaintainService.getDepartmentInfo(params).then(response => {
      pagination.total = Number(response.headers['x-total-count']);
      this.setState({
        data: response.data,
        loading: false,
        pagination,
      });
    });
  };
  //选项改变时的回调，重置selection
  onSelectChange = (selectedRowKeys, selectedRows) => {
    let tabsData = this.state.tabsData;
    tabsData.USER.rowSelection.selectedRowKeys = selectedRowKeys;
    this.setState({ tabsData, selectedRowKeys });
  };

  //编辑
  editItem = (e, record) => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/enterprise-manage/company-maintain/edit-bank-account/${
          this.props.match.params.companyOId
        }/${this.props.match.params.companyId}/${record.id}`,
      })
    );
    // let path = this.state.newBankAccountPage.url
    //   .replace(':companyOId', this.props.match.params.companyOId)
    //   .replace(':companyId', this.props.match.params.companyId)
    //   .replace(':flag', record.id);
    // this.context.router.push(path);
  };
  //分页点击
  onChangePager = (pagination, filters, sorter) => {
    let temp = this.state.pagination;
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        loading: true,
        pagination: temp,
      },
      () => {
        this.getList(this.state.nowStatus);
      }
    );
  };

  //渲染Tabs
  renderTabs = () => {
    return this.state.tabs.map(tab => {
      return <TabPane tab={tab.name} key={tab.key} />;
    });
  };

  //点击
  onChangeTabs = key => {
    let pagination = this.state.pagination;
    pagination.page = 0;
    pagination.pageSize = 10;
    this.setState(
      {
        nowStatus: key,
        loading: true,
        pagination,
        data: [],
      },
      () => {
        this.getList(key);
      }
    );
  };

  //新建
  handleNew = () => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/enterprise-manage/company-maintain/new-bank-account/${
          this.props.match.params.companyOId
        }/${this.props.match.params.companyId}/create`,
      })
    );
    // let path = this.state.newBankAccountPage.url
    //   .replace(':companyOId', this.props.match.params.companyOId)
    //   .replace(':companyId', this.props.match.params.companyId)
    //   .replace(':flag', 'create');
    // this.context.router.push(path);
  };

  //返回
  handleBack = () => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/enterprise-manage/company-maintain/company-maintain`,
      })
    );
    // this.context.router.goBack();
  };

  // 保存公司部门关联关系
  saveCompanyDepartment = result => {
    if (result.result.length === 0) {
      return;
    }
    let departmentIds = result.result.map(item => item.departmentId);
    const { companyId } = this.props.match.params;
    companyMaintainService
      .associate(companyId, departmentIds)
      .then(res => {
        message.success(this.$t('common.operate.success')); //保存成功
        this.setState({ showAssociateDepartment: false });
        this.getList(this.state.nowStatus);
      })
      .catch(e => {
        message.error(this.$t('common.operate.filed'));
      });
  };
  // 更改公司部门关联关系状态
  handleStatusChange = (e, record) => {
    record.enabled = e.target.checked;
    companyMaintainService
      .updateAssociate(record)
      .then(res => {
        message.success(this.$t('common.operate.success')); //保存成功
        this.setState({ showAssociateDepartment: false });
        this.getList(this.state.nowStatus);
      })
      .catch(e => {
        message.error(this.$t('common.operate.filed'));
      });
  };
  // 查询改公司部门下的员工
  viewEmployeeInfo = record => {
    this.setState({ showContactInfo: true, departmentId: record.departmentId });
  };
  // 查询公司部门下的责任中心
  viewresponsibilityInfo = record => {
    this.setState({ showResponsibilityInfo: true, departmentId: record.departmentId });
  };
  //渲染按钮
  renderButton = () => {
    const { saving, pagination, selectedRowKeys } = this.state;

    if (this.state.nowStatus === 'USER') {
      return (
        <div>
          <div className="table-header-title">
            {/*共 {pagination.total} 条数据 / 已经选择了 {this.state.selectedRowKeys.length} 条数据*/}
            {this.$t('common.total1', { total: pagination.total })}
            /
            {this.$t('common.total.selected', { total: this.state.selectedRowKeys.length })}
          </div>
          <div className="table-header-buttons">
            <Button onClick={this.removeUser} disabled={selectedRowKeys.length <= 0}>
              {/*移动*/}
              {this.$t('common.move')}
            </Button>
          </div>
        </div>
      );
    } else if (this.state.nowStatus === 'DEPARTMENT') {
      return (
        <div>
          <div className="table-header-buttons">
            <Button
              type="primary"
              onClick={() => this.setState({ showAssociateDepartment: true })}
              loading={saving}
            >
              {this.$t('company.associate.department.addDepartment')}
            </Button>
          </div>
        </div>
      );
    } else {
      return (
        <div>
          <div className="table-header-buttons">
            <Button type="primary" onClick={this.handleNew} loading={saving}>
              {this.$t('common.create')}
            </Button>
          </div>
        </div>
      );
    }
  };

  //确认移动员工
  submitHandle = value => {
    const companyOidTo = value.result[0].companyOid;
    const companyOidFrom = this.props.match.params.companyOId;
    const selectedRowKeys = this.state.selectedRowKeys;
    let params = {
      companyOidFrom: companyOidFrom,
      companyOidTo: companyOidTo,
      userOids: selectedRowKeys,
      selectMode: 'default',
    };
    companyMaintainService.movePersonToCompany(params).then(res => {
      message.success(this.$t('common.operate.success'));
      this.getList(this.state.nowStatus);
      this.setState({
        selectedRowKeys: [],
      });
      this.showImport(false);
    });
  };

  //员工移动
  removeUser = () => {
    this.showImport(true);
  };

  showImport = value => {
    this.setState({
      showImportFrame: value,
    });
  };

  cancelHandle = () => {
    this.showImport(false);
  };

  render() {
    const { tabsData, loading, pagination, nowStatus, data } = this.state;

    return (
      <div style={{ paddingBottom: 20 }}>
        <Tabs onChange={this.onChangeTabs} style={{ marginTop: 20 }}>
          {this.renderTabs()}
        </Tabs>
        <div className="table-header">{this.renderButton()}</div>
        <Table
          columns={tabsData[nowStatus].columns}
          dataSource={data}
          pagination={pagination}
          onChange={this.onChangePager}
          loading={loading}
          bordered
          size="middle"
          rowKey={reCode => {
            return reCode.userOid;
          }}
          rowSelection={tabsData[nowStatus].rowSelection}
        />
        <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleBack}>
          <Icon type="rollback" style={{ marginRight: '5px' }} />
          {this.$t('common.back')}
        </a>

        <ListSelector
          visible={this.state.showImportFrame}
          onOk={this.submitHandle}
          onCancel={this.cancelHandle}
          type="available_company"
          single={true}
          extraParams={{
            versionId: this.props.match.params.versionId,
            enabled: true,
            setOfBooksId: this.props.match.params.setOfBooksId,
            ignoreCompanyId: this.props.match.params.companyId,
          }}
        />

        <ListSelector
          visible={this.state.showAssociateDepartment}
          onOk={this.saveCompanyDepartment}
          onCancel={() => this.setState({ showAssociateDepartment: false })}
          selectorItem={this.state.departmentSelectItem}
          single={false}
          valueKey="departmentId"
          clearFlag={true}
          extraParams={{
            companyId: this.props.match.params.companyId,
          }}
        />
        <ListSelector
          visible={this.state.showContactInfo}
          onReturn={() => this.setState({ showContactInfo: false })}
          onCancel={() => this.setState({ showContactInfo: false })}
          selectorItem={this.state.contactSelectItem}
          hideRowSelect={true}
          hideFooter={true}
          diyFooter={true}
          extraParams={{
            companyId: this.props.match.params.companyId,
            departmentId: this.state.departmentId,
          }}
        />

        <ListSelector
          visible={this.state.showResponsibilityInfo}
          onReturn={() => this.setState({ showResponsibilityInfo: false })}
          onCancel={() => this.setState({ showResponsibilityInfo: false })}
          selectorItem={this.state.responsibilityInfoSelectItem}
          hideRowSelect={true}
          hideFooter={true}
          diyFooter={true}
          extraParams={{
            companyId: this.props.match.params.companyId,
            departmentId: this.state.departmentId,
          }}
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

const CompanyMaintainDetail = Form.create()(WrappedCompanyMaintainDetail);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(CompanyMaintainDetail);
