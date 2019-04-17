import React from 'react';
import { Popover, Tag, Menu, message, Tooltip, Alert, Button, Dropdown } from 'antd';
import config from 'config';
import SelectRoles from './roles';
import { connect } from 'dva';
import { isEmptyObj, deepCopy } from 'utils/extend';
import SearchArea from 'widget/search-area.js';
import { routerRedux } from 'dva/router';
import ImporterNew from 'widget/Template/importer-new';
import ExcelExporter from 'widget/excel-exporter';
import 'styles/enterprise-manage/person-manage/person-manage.scss';
import service from './service';
import FileSaver from 'file-saver';
import CustomTable from 'components/Widget/custom-table';

class Employee extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      allocShow: false,
      userId: '',
      cacheObj: {
        keyword: '', //关键字
        departmentOids: [], //部门
        corporationOids: [], //公司
        status: 'all', //员工状态
      }, //缓存变量
      CREATE_DATA_TYPE: true,
      loading: false,
      data: [], //条件下所有人
      pagination: {
        page: 0,
        total: 0,
        pageSize: 10,
      },

      params: {
        keyword: '',
        departmentOids: [],
        corporationOids: [],
        status: 'all',
        tenantId: '',
      },
      extraDep: {
        res: [],
        title: this.$t('person.manage.select'), //"请选择",
        depClassName: 'f-right select-dep-close-wrap',
        className: [
          'f-right select-dep-close-wrap',
          'f-right select-dep-close-wrap select-dep-close-wrap-show',
        ],
      },
      searchForm: [
        {
          type: 'input',
          id: 'keyword',
          label: this.$t('person.manage.name.employeeId.email.mobile'), //'员工姓名、工号、邮箱、电话',
          event: 'keywordChange',
          colSpan: 6,
        },
        {
          type: 'list',
          id: 'corporationOids',
          label: this.$t('person.manage.company'), //'公司',
          listType: 'all_company_with_legal_entity',
          labelKey: 'companyName',
          valueKey: 'companyOid',
          single: true,
          placeholder: this.$t('person.manage.select'), //"请选择",
          event: 'companyOidChange',
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'status',
          label: this.$t('person.manage.status'), //"状态",
          event: 'statusChange',
          defaultValue: 'all',
          options: [
            {
              label: this.$t('person.manage.working.person'), // '在职员工',
              value: 1001,
            },
            {
              label: this.$t('person.manage.will.go.person'), //'待离职员工',
              value: 1002,
            },
            {
              label: this.$t('person.manage.gone.person'), //'离职员工',
              value: 1003,
            },
            {
              label: this.$t('person.manage.all.person'), //'全部员工',
              value: 'all',
            },
          ],
          colSpan: 6,
        },
        {
          type: 'list',
          listType: 'department',
          id: 'departmentOids',
          label: this.$t('mdata.department') /*部门*/,
          options: [],
          labelKey: 'name',
          valueKey: 'departmentOid',
          single: true,
          listExtraParams: { tenantId: this.props.user.tenantId },
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: this.$t('person.manage.company'), //公司
          dataIndex: 'companyName',
          showTooltip: true,
          width: 168,
          render: text => (
            <span>
              {text ? (
                <Popover placement="topLeft" content={text}>
                  {text}
                </Popover>
              ) : (
                '-'
              )}
            </span>
          ),
        },
        {
          title: this.$t('person.manage.employeeId'), //"工号",
          key: 'employeeId',
          dataIndex: 'employeeId',
          width: 110,
          render: text => (
            <span>
              {text ? (
                <Popover placement="topLeft" content={text}>
                  {text}
                </Popover>
              ) : (
                '-'
              )}
            </span>
          ),
        },
        {
          title: this.$t('person.manage.name'), //姓名
          dataIndex: 'fullName',
          width: 88,
          render: (value, record) => (
            <Tooltip title={record.fullName}>
              <Tag color="green">{value}</Tag>
            </Tooltip>
          ),
        },
        {
          title: this.$t('person.manage.dep'), //部门
          dataIndex: 'departmentName',
          showTooltip: true,
          width: 131,
          render: text => (
            <span>
              {text ? (
                <Popover placement="topLeft" content={text}>
                  {text}
                </Popover>
              ) : (
                '-'
              )}
            </span>
          ),
        },
        {
          title: this.$t('person.manage.contact'), //联系方式
          dataIndex: 'mobile',
          width: 142,
          render: text => (
            <span>
              {text ? (
                <Popover placement="topLeft" content={text}>
                  {text}
                </Popover>
              ) : (
                '-'
              )}
            </span>
          ),
        },
        {
          title: this.$t('person.manage.email'), //邮箱
          dataIndex: 'email',
          width: 208,
          render: text => (
            <span>
              {text ? (
                <Popover placement="topLeft" content={text}>
                  {text}
                </Popover>
              ) : (
                '-'
              )}
            </span>
          ),
        },
        {
          title: this.$t('person.manage.status'), // "状态",
          key: 'status',
          dataIndex: 'status',
          width: 92,
          align: 'center',
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
        {
          title: this.$t('person.manage.operation'), //操作
          dataIndex: 'option',
          width: 135,
          align: 'center',
          render: (value, record) => {
            return (
              <span>
                <a onClick={() => this.alloc(record)}>{this.$t('mdata.assign.roles')}</a>
                {/*分配角色*/}
                <span className="ant-divider" />
                <a onClick={e => this.editItemPerson(e, record)}>
                  {/*详情*/}
                  {this.$t('common.detail')}
                </a>
              </span>
            );
          },
        },
      ],
      importerVisible: false,
      exportVisible: false,
      importList: [
        {
          templateUrl: `${config.mdataUrl}/api/user/info/export/template`,
          title: this.$t('mdata.import.the.personnel.data') /*导入人员数据*/,
          fileName: this.$t('mdata.personnel.information.into.the.template') /*人员信息导入模板*/,
        },
        {
          templateUrl: `${config.mdataUrl}/api/user/bankcard/export/template`,
          title: this.$t('mdata.import.bank.data') /*导入银行数据*/,
          fileName: this.$t('mdata.the.bank.information.into.the.template') /*银行信息导入模板*/,
        },
        {
          templateUrl: `${config.mdataUrl}/api/user/idcard/export/template`,
          title: this.$t('mdata.the.import.documents.data') /*导入证件数据*/,
          fileName: this.$t('mdata.certificate.information.into.the.template') /*证件信息导入模板*/,
        },
      ],
      currentImport: {},
      // 人员导出列
      exportPersonColumns: {
        fileName: this.$t('mdata.personnel.data.export') /*人员数据导出*/,
        exportColumns: [
          { title: this.$t('mdata.the.company'), dataIndex: 'companyName' } /*公司*/,
          { title: this.$t('mdata.work.number'), dataIndex: 'employeeId' } /*工号*/,
          { title: this.$t('mdata.the.name'), dataIndex: 'fullName' } /*姓名*/,
          { title: this.$t('mdata.department'), dataIndex: 'departmentName' } /*部门*/,
          { title: this.$t('mdata.email'), dataIndex: 'email' } /*邮箱*/,
          { title: this.$t('mdata.mobile.phone'), dataIndex: 'mobile' } /*手机*/,
          {
            title: this.$t('mdata.directly.under.the.leadership'),
            dataIndex: 'directManagerName',
          } /*直属领导*/,
          { title: this.$t('mdata.position'), dataIndex: 'duty' } /*职务*/,
          { title: this.$t('mdata.position'), dataIndex: 'title' } /*职位*/,
          { title: this.$t('mdata.personnel.type'), dataIndex: 'employeeType' } /*人员类型*/,
          { title: this.$t('mdata.level'), dataIndex: 'rank' } /*级别*/,
          { title: this.$t('mdata.gender'), dataIndex: 'genderCode' } /*性别*/,
          { title: this.$t('mdata.birthday'), dataIndex: 'birthday' } /*生日*/,
          { title: this.$t('mdata.in.the.time'), dataIndex: 'entryDate' } /*入职时间*/,
        ],
        url: `${config.mdataUrl}/api/export/user/info/new`,
      },
      // 银行导出列
      exportBankColumns: {
        fileName: this.$t('mdata.export.data.bank') /*银行数据导出*/,
        exportColumns: [
          { title: this.$t('mdata.work.number'), dataIndex: 'employeeId' } /*工号*/,
          { title: this.$t('mdata.bank.card.number'), dataIndex: 'bankAccountNo' } /*银行卡号*/,
          { title: this.$t('mdata.the.bank.account'), dataIndex: 'bankAccountName' } /*开行账户*/,
          { title: this.$t('mdata.bank.name'), dataIndex: 'bankName' } /*银行名称*/,
          { title: this.$t('mdata.the.name.of.the.branch'), dataIndex: 'branchName' } /*支行名称*/,
          {
            title: this.$t('mdata.to.open.an.account.to'),
            dataIndex: 'accountLocation',
          } /*开户地*/,
          { title: this.$t('mdata.the.default.logo'), dataIndex: 'primaryStr' } /*默认标识*/,
          { title: this.$t('mdata.enable.identification'), dataIndex: 'enabledStr' } /*启用标识*/,
        ],
        url: `${config.mdataUrl}/api/contact/bank/account/export/new`,
      },
      // 证件导出列
      exportCardColumns: {
        fileName: this.$t('mdata.data.export.documents') /*证件数据导出*/,
        exportColumns: [
          { title: this.$t('mdata.work.number'), dataIndex: 'employeeId' } /*工号*/,
          { title: this.$t('mdata.the.surname'), dataIndex: 'lastName' } /*姓*/,
          { title: this.$t('mdata.the.name'), dataIndex: 'firstName' } /*名*/,
          { title: this.$t('mdata.nationality'), dataIndex: 'nationality' } /*国籍*/,
          { title: this.$t('mdata.document.type'), dataIndex: 'cardTypeName' } /*证件类型*/,
          { title: this.$t('mdata.identification.number'), dataIndex: 'cardNoStr' },
          {
            title: this.$t('mdata.certificate.expiration.date'),
            dataIndex: 'cardExpiredTimeStr',
          } /*证件失效日期*/,
          { title: this.$t('mdata.the.default.logo'), dataIndex: 'primaryStr' } /*默认标识*/,
          { title: this.$t('mdata.enable.identification'), dataIndex: 'enabledStr' } /*启用标识*/,
        ],
        url: `${config.mdataUrl}/api/contact/cards/export/new`,
      },
      currentExport: {},
    };
  }

  componentDidMount() {
    let _pagination = this.getBeforePage();
    let pagination = this.state.pagination;
    pagination.page = _pagination.page;
    pagination.current = _pagination.page + 1;
    this.setState(
      {
        pagination,
      },
      () => {
        this.clearBeforePage();
        //取出页面缓存状态
        let cache = localStorage.getItem('person-manage-cache');
        let cacheObj = {};
        if (cache + '' != 'null' && cache + '' != '{}') {
          //cache要有值
          cacheObj = JSON.parse(cache);
        } else {
          this.getPersonList();
        }
        //人员导入方式：this.props.company.createDataType如果是 1002，属于接口导入
        // 新增与导入按钮需要隐藏
        let CREATE_DATA_TYPE = parseInt(this.props.company.createDataType) != 1002;
        this.setState(
          {
            CREATE_DATA_TYPE,
            cacheObj,
          },
          () => {
            this.setDefaultSearchForm(this.state.cacheObj);
          }
        );
      }
    );
  }

  //设置默认的搜索值
  setDefaultSearchForm = defaultVal => {
    if (isEmptyObj(defaultVal)) {
      return;
    }
    const { params, searchForm, extraDep } = this.state;
    searchForm[0].defaultValue = defaultVal.keyword;
    searchForm[1].defaultValue = defaultVal.corporationOids;
    searchForm[2].defaultValue = defaultVal.status || 'all';
    //部门的稍微麻烦一点
    let deps = [];
    extraDep.res = defaultVal.departmentOids || [];
    if (extraDep.res.length > 0) {
      extraDep.depClassName = extraDep.className[1];
    } else {
      extraDep.depClassName = extraDep.className[0];
    }
    for (let i = 0; i < extraDep.res.length; i++) {
      deps.push(extraDep.res[i].departmentOid);
    }
    extraDep.title = this.renderButtonTitle(extraDep.res);

    //查询参数，重新设置
    params.keyword = defaultVal.keyword;
    let corporationOids = [];
    if (defaultVal && defaultVal.corporationOids && defaultVal.corporationOids.map) {
      corporationOids = defaultVal.corporationOids.map(data => {
        return data.companyOid;
      });
    }
    params.corporationOids = corporationOids;
    params.status = defaultVal.status || 'all';
    params.departmentOids = defaultVal.departmentOids;
    this.setState(
      {
        extraDep,
        params,
      },
      () => {
        this.getPersonList();
      }
    );
  };

  //获取员工表格
  getPersonList = () => {
    const pagination = this.state.pagination;
    this.setState({
      loading: true,
    });
    let params = {
      sort: 'status',
      tenantId: this.props.user.tenantId,
      keyword: this.state.params.keyword,
      departmentOid: this.state.params.departmentOids,
      corporationOid: this.state.params.corporationOids,
      status: this.state.params.status,
      isInactiveSearch: false,
    };
    this.table.search(params);
  };

  //分页点击
  onChangePager = (pagination, filters, sorter) => {
    this.setState(
      {
        pagination: {
          current: pagination.current,
          page: pagination.current - 1,
          pageSize: pagination.pageSize,
        },
      },
      () => {
        this.getPersonList();
      }
    );
  };

  //新增员工
  handleCreatePerson = () => {
    this.setBeforePage(this.state.pagination);
    let cacheObj = this.state.cacheObj;
    let cacheObjStr = JSON.stringify(cacheObj);
    localStorage.setItem('person-manage-cache', cacheObjStr);
    this.props.dispatch(
      routerRedux.push({
        pathname: `/setting/employee/person-detail/NEW`,
      })
    );
  };

  //渲染已经选择的部门
  renderButtonTitle(titleArr) {
    if (titleArr.length < 1) {
      // 请选择
      return this.$t('person.manage.select');
    }
    let node = [];
    titleArr.map((item, i) => {
      node.push(<Tag key={i}>{item.name}</Tag>);
    });
    return node;
  }

  //编辑员工
  editItemPerson = (e, record) => {
    this.setBeforePage(this.state.pagination);
    let cacheObj = this.state.cacheObj;
    let cacheObjStr = JSON.stringify(cacheObj);
    localStorage.setItem('person-manage-cache', cacheObjStr);
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/setting/employee/person-detail/${record.userOid}`,
      })
    );
  };

  // 点击分配角色 回调
  alloc = record => {
    this.setState({ allocShow: true, userId: record.id });
  };

  // 分配角色弹出框 关闭
  close = flag => {
    this.setState({ allocShow: false }, () => {
      flag && this.getPersonList();
    });
  };

  //点击搜搜索
  handleSearch = values => {
    const { params } = this.state;
    if (values.corporationOids && values.corporationOids[0]) {
      values.corporationOids = values.corporationOids[0];
    }
    if (values.departmentOids && values.departmentOids[0]) {
      values.departmentOids = values.departmentOids[0];
    }
    params.keyword = values.keyword;
    params.corporationOids = values.corporationOids;
    params.status = values.status;
    params.departmentOids = values.departmentOids;
    let pagination = this.state.pagination;
    pagination.page = 0;
    pagination.current = 1;
    this.setState(
      {
        pagination,
        params: deepCopy(params),
      },
      () => {
        this.getPersonList();
      }
    );
  };

  // 人员导入
  handleImportClick = () => {
    const { importList } = this.state;
    this.setState({ importerVisible: true, currentImport: importList[0] });
  };

  //银行、证件信息导入
  handleImportMenuClick = e => {
    const { importList } = this.state;
    let currentImport;
    if (e.key === '1') {
      currentImport = importList[1];
    } else if (e.key === '2') {
      currentImport = importList[2];
    }
    this.setState({ currentImport, importerVisible: true });
  };

  // 确认导入
  onConfirmImport = transactionID => {
    service
      .confirmImporter(transactionID)
      .then(() => {
        this.setState({ importerVisible: false });
        this.centerTable.search();
        message.success(this.$t('mdata.import.success')); /*导入成功*/
      })
      .catch(err => {
        this.setState({ importerVisible: false });
      });
  };

  // 导出人员信息
  handleExportClick = () => {
    const { exportPersonColumns } = this.state;
    this.setState({ currentExport: exportPersonColumns, exportVisible: true });
  };

  // 导出银行、证件信息
  handleExportMenuClick = e => {
    const { exportBankColumns, exportCardColumns } = this.state;
    let currentExport;
    if (e.key === '1') {
      currentExport = exportBankColumns;
    } else if (e.key === '2') {
      currentExport = exportCardColumns;
    }
    this.setState({ currentExport, exportVisible: true });
  };

  // 确认导出
  confirmExport = values => {
    const url = this.state.currentExport.url;
    let hide = message.loading(this.$t('mdata.desc.code3')); // 正在生成文件，请等待......
    service
      .confirmExport(url, values)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t('mdata.export.success')); /*导出成功*/
          let fileName = res.headers['content-disposition'].split('filename=')[1];
          let f = new Blob([res.data]);
          FileSaver.saveAs(f, decodeURIComponent(fileName));
          hide();
        }
      })
      .catch(err => {
        message.error(this.$t('mdata.desc.code2')); // 下载失败，请重试!
        hide();
      });
  };

  render() {
    const importMenu = (
      <Menu onClick={this.handleImportMenuClick}>
        <Menu.Item key="1">{this.$t('mdata.import.the.bank.information')}</Menu.Item>
        {/*导入银行信息*/}
        <Menu.Item key="2">{this.$t('mdata.the.import.certificate.information')}</Menu.Item>
        {/*导入证件信息*/}
      </Menu>
    );
    const exportMenu = (
      <Menu onClick={this.handleExportMenuClick}>
        <Menu.Item key="1">{this.$t('mdata.export.bank.information')}</Menu.Item>
        {/*导出银行信息*/}
        <Menu.Item key="2">{this.$t('mdata.export.certificate.information')}</Menu.Item>
        {/*导出证件信息*/}
      </Menu>
    );
    const {
      columns,
      allocShow,
      userId,
      searchForm,
      importerVisible,
      currentImport,
      exportVisible,
      currentExport,
    } = this.state;

    return (
      <div className="person-manage-wrap">
        <Alert
          style={{ marginBottom: 10 }}
          closable
          message={this.$t('mdata.desc.code1')} // "操作成功后，刷新当前页面或重新登录才能生效！"
          type="info"
        />
        <SearchArea
          maxLength={4}
          isExtraFields={true}
          submitHandle={this.handleSearch}
          searchForm={searchForm}
        />

        <div className="table-header">
          <div className="table-header-buttons">
            <div className="f-left">
              {this.state.CREATE_DATA_TYPE ? (
                <Button
                  type="primary"
                  disabled={!this.props.tenantMode}
                  onClick={this.handleCreatePerson}
                >
                  {/*新增员工*/}
                  {this.$t('person.manage.new.person')}
                </Button>
              ) : (
                <span />
              )}
              {this.state.CREATE_DATA_TYPE ? (
                <Dropdown.Button
                  onClick={this.handleImportClick}
                  overlay={importMenu}
                  disabled={!this.props.tenantMode}
                >
                  {/*导入人员数据*/}
                  {this.$t('person.manage.im.person.data')}
                </Dropdown.Button>
              ) : (
                <span />
              )}

              <Dropdown.Button
                onClick={this.handleExportClick}
                overlay={exportMenu}
                disabled={false}
              >
                {/* 导出人员信息 */}
                {this.$t('person.manage.ex.person.data')}
              </Dropdown.Button>
            </div>
          </div>
        </div>

        <div style={{ margin: '24px 0' }}>
          <CustomTable
            columns={columns}
            url={`${config.mdataUrl}/api/users/v3/search`}
            ref={ref => (this.table = ref)}
          />
        </div>

        <SelectRoles userId={userId} onCancel={this.close} visible={allocShow} />

        {/* 导入 */}
        <ImporterNew
          visible={importerVisible}
          templateUrl={currentImport.templateUrl}
          deleteDataUrl={`${config.mdataUrl}/api/user/import/new/delete`}
          errorUrl={`${config.mdataUrl}/api/user/import/new/error/export`}
          errorDataQueryUrl={`${config.mdataUrl}/api/user/import/new/query/result`}
          uploadUrl={`${config.mdataUrl}/api/user/import/new`}
          fileName={currentImport.fileName}
          title={currentImport.title}
          afterClose={() => this.setState({ importerVisible: false })}
          onOk={this.onConfirmImport}
        />

        {/* 导出 */}
        {exportVisible && (
          <ExcelExporter
            visible={exportVisible}
            onOk={this.confirmExport}
            columns={currentExport.exportColumns}
            canCheckVersion={false}
            fileName={currentExport.fileName}
            onCancel={() => this.setState({ exportVisible: false })}
            excelItem={'PREPAYMENT_FINANCIAL_QUERY'}
          />
        )}
      </div>
    );
  }
}
function mapStateToProps(state) {
  return {
    // profile: state.login.profile,
    user: state.user.currentUser,
    tenantMode: true,
    company: state.user.company,
  };
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(Employee);
