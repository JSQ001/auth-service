/* eslint-disable */
import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import config from 'config';
import { Badge, Button, Divider, Popconfirm, Popover, message, Tabs } from 'antd';
import SearchArea from 'widget/search-area';
import Table from 'widget/table';
import expensePolicyService from 'containers/setting/expense-policy/expense-policy.service';
import moment from 'moment';
import FileSaver from 'file-saver';
const TabPane = Tabs.TabPane;
import CustomTable from 'components/Widget/custom-table';
import ExcelExporter from 'widget/excel-exporter';
import SlideFrame from 'widget/slide-frame';
import NewInvoicingSite from './new-invoicing-site';
import invoicingSiteService from './invoicing-site.service';

class InvoicingSite extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      record: {},
      visible: false,
      loading: false,
      //   tabValue: 'apply',
      data: [],
      searchParams: {
        typeFlag: props.match.params.typeFlag === ':typeFlag' ? '0' : props.match.params.typeFlag,
        setOfBooksId:
          props.match.params.sob === ':sob' ? props.company.setOfBooksId : props.match.params.sob,
      },
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
      },
      searchForm: [
        // {
        //   type: 'select',
        //   id: 'setOfBooksId',
        //   label: this.$t({ id: 'form.setting.set.of.books' }),
        //   options: [],
        //   labelKey: 'setOfBooksName',
        //   allowClear: false,
        //   valueKey: 'id',
        //   colSpan: 6,
        //   event: 'SOB',
        //   getUrl: `${config.mdataUrl}/api/setOfBooks/by/tenant`,
        //   method: 'get',
        //   renderOption: item => item.setOfBooksCode + '-' + item.setOfBooksName,
        //   getParams: { roleType: 'TENANT', enabled: true },
        // },
        {
          type: 'input',
          id: 'invoicingSiteCode',
          placeholder: '请输入',
          label: '开票点编码',
          colSpan: 6,
        },

        {
          type: 'input',
          id: 'invoicingSiteName',
          placeholder: '请输入',
          label: '开票点名称',
          colSpan: 6,
        },
        {
          type: 'list',
          id: 'taxpayerId',
          colSpan: 6,
          listType: 'taxpayer_name',
          labelKey: 'taxpayerName',
          valueKey: 'id',
          event: 'taxpayerId',
          single: true,
          listExtraParams: {},
          label: '所属纳税主体' /*所属纳税主体*/,
        },
        {
          type: 'input',
          id: 'invoicingTerminal',
          placeholder: '请输入',
          label: '开票终端标识',
          colSpan: 6,
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'companyId',
          //label: this.$t({ id: 'my.contract.contractCompany' } /*公司*/),
          label: '开票点机构',
          listType: 'company',
          valueKey: 'id',
          labelKey: 'name',
          options: [],
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          single: true,
          event: 'COMPANY_ID',
          allowClear: true,
        },
        // {
        //   type: 'list',
        //   id: 'expenseTypeId',
        //   colSpan: 6,
        //   listType: 'expense_item',
        //   labelKey: 'name',
        //   valueKey: 'id',
        //   event: 'expenseTypeId',
        //   single: true,
        //   listExtraParams: {
        //     setOfBooksId: this.props.company.setOfBooksId,
        //     typeFlag: 0,
        //     roleType: 'TENANT',
        //   },
        //   // label: this.$t({ id: 'expense.policy.expenseTypeName' }) /*申请项目*/,
        //   label: '开票点成本中心',
        // },
        {
          type: 'list',
          id: 'departmentId',
          colSpan: 6,
          listType: 'responsibility_default',
          labelKey: 'responsibilityCenterName',
          valueKey: 'id',
          event: 'responsibilityCenterId',
          single: true,
          listExtraParams: {},
          label: '责任中心' /*申请项目*/,
        },
        // {
        //   type: 'value_list',
        //   id: 'dutyType',
        //   colSpan: 6,
        //   options: [],
        //   event: 'dutyType',
        //   valueListCode: 1002,
        //   label: this.$t({ id: 'expense.policy.dutyName' }) /*申请人职务*/,
        // },
        // {
        //   type: 'select',
        //   id: 'companyLevelId',
        //   options: [],
        //   getUrl: `${config.mdataUrl}/api/companyLevel/selectByTenantId`,
        //   colSpan: 6,
        //   method: 'get',
        //   labelKey: 'description',
        //   valueKey: 'id',
        //   event: 'companyLevelId',
        //   label: this.$t({ id: 'expense.policy.companyLevelName' }) /*申请人公司级别*/,
        // },
      ],
      columns: [
        {
          title: '开票点编码',
          dataIndex: 'invoicingSiteCode',
          align: 'center',
        },
        {
          title: '开票点名称',
          dataIndex: 'invoicingSiteName',
          align: 'center',
        },
        {
          title: '所属纳税主体',
          dataIndex: 'taxpayerName',
          align: 'center',
        },

        {
          title: '开票终端标识',
          dataIndex: 'invoicingTerminal',
          align: 'center',
          // render: () => {
          //   return (<span>{decodeURIComponent(props.match.params.taxCategoryName)}</span>)
          // }
        },

        {
          /*状态*/
          title: '状态',
          dataIndex: 'enabled',
          align: 'center',
          render: value => {
            return (
              <div>
                <Badge status={value ? 'success' : 'error'} />
                {value ? this.$t('common.status.enable') : this.$t('common.status.disable')}
              </div>
            );
          },
        },
        {
          title: '开票权限',
          // dataIndex: 'taxRate',
          align: 'center',
          render: (value, record) => (
            <a onClick={() => this.toInvoicingDimension(record.id, record)}>开票权限分配</a>
          ),
        },
        {
          title: '操作',
          dataIndex: 'operation',
          align: 'center',
          render: (value, record) => {
            return <a onClick={() => this.edit(record)}>编辑</a>;
          },
        },
      ],
      // excel表格不可见
      excelVisible: false,
      visibel: false,
      model: {},
      exportColumns: [
        { title: '开票点编码', dataIndex: 'invoicingSiteCode' },
        { title: '开票点名称', dataIndex: 'invoicingSiteName' },
        { title: '所属纳税主体名称', dataIndex: 'taxpayerName' },
        { title: '开票点地址', dataIndex: 'invoicingSiteAdd' },
        { title: '税控钥匙密码', dataIndex: 'passwod' },
        { title: '开票终端标识', dataIndex: 'invoicingTerminal' },
        { title: '安全码', dataIndex: 'securityCode' },
        { title: '上边距', dataIndex: 'printTop' },
        { title: '左边距', dataIndex: 'printLeft' },
        { title: '备注', dataIndex: 'remarks' },
        { title: '状态', dataIndex: 'enabled' },
      ],
    };
  }

  // 跳转开票权限分配页面
  toInvoicingDimension = (id, record) => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/basic-tax-information-management/vat-invoicing-site/invoicing-dimension/${id}/${
          record.taxpayerId
        }`,
      })
    );
  };
  handleDelete = record => {
    expensePolicyService
      .deletePolicy(record.id)
      .then(res => {
        message.success(this.$t('common.delete.success'));
        this.customTable.search(this.state.searchParams);
      })
      .catch(e => {
        message.error(this.$t('common.delete.failed'));
      });
  };

  componentDidMount() {
    const { searchForm } = this.state;
    let params = { roleType: 'TENANT', enabled: true };
    let setOfBooksId =
      this.props.match.params.sob === ':sob'
        ? this.props.company.setOfBooksId
        : this.props.match.params.sob;
    expensePolicyService.getTenantAllSob(params).then(res => {
      searchForm[0].options = res.data.map(item => {
        if (item.id === setOfBooksId) {
          searchForm[0].defaultValue = {
            key: item.id,
            label: item.setOfBooksCode + '-' + item.setOfBooksName,
          };
        }
        return {
          key: item.id,
          value: item.id,
          label: item.setOfBooksCode + '-' + item.setOfBooksName,
        };
      });
      this.setState({ searchForm });
    });
  }

  //员工信息，工号，电话等关键字是即时搜索
  // eventSearchAreaHandle = (e, item) => {
  //   let searchParams = this.state.searchParams;
  //   let pagination = this.state.pagination;
  //   pagination.page = 0;
  //   switch (e) {
  //     case 'SOB': {
  //       searchParams.setOfBooksId = item;
  //       break;
  //     }
  //     case 'expenseTypeId': {
  //       searchParams.expenseTypeId = item;
  //       break;
  //     }
  //     case 'dutyType': {
  //       searchParams.dutyType = item;
  //       break;
  //     }
  //     case 'companyLevelId': {
  //       searchParams.companyLevelId = item;
  //       break;
  //     }
  //   }
  //   this.setState(
  //     {
  //       pagination,
  //       searchParams,
  //     },
  //     () => {
  //       this.customTable.search(this.state.searchParams);
  //     }
  //   );
  // };
  // 点击搜索按钮
  search = result => {
    let pagination = this.state.pagination;
    pagination.page = 0;
    pagination.current = 1;
    pagination.total = 0;
    this.setState(
      {
        pagination,
        // searchParams: Object.assign(this.state.searchParams, result),
        searchParams: { ...result },
      },
      () => {
        this.customTable.search(this.state.searchParams);
      }
    );
  };
  // 点击清空按钮
  clear = () => {
    // this.setState({
    //     searchParams: {
    //         setOfBook: '',
    //     },
    // });
    this.customTable.search();
  };

  // 新建
  create = () => {
    this.setState({
      visibel: true,
    });
  };
  //编辑
  edit = record => {
    console.log(record);
    this.setState({ model: JSON.parse(JSON.stringify(record)), visibel: true });
  };
  // // 新建、编辑费用政策
  // handleCreate = (e, record) => {
  //     !!record && e.preventDefault();
  //     !!record && e.stopPropagation();
  //     let id = record ? record.id : 'new';
  //     let url = record ? `edit-expense-policy/${record.id}` : e.id ? `expense-policy-detail/${e.id}` : 'new-expense-policy/new';
  //     this.props.dispatch(
  //         routerRedux.push({
  //             pathname: `/admin-setting/expense-policy/${url}/${
  //                 this.state.searchParams.setOfBooksId
  //                 }/${this.state.searchParams.typeFlag}`,
  //         })
  //     );
  // };

  //导出维值--可视化导出模态框
  handleExport = () => {
    this.setState({ excelVisible: true });
  };
  //确认导出
  confirmExport = result => {
    let hide = message.loading('正在生成文件，请等待......');
    const { dimensionId } = this.state;
    invoicingSiteService
      .exportSelfTax(result, { page: 1, size: 10 }, dimensionId)
      .then(res => {
        if (res.status === 200) {
          message.success('导出成功');
          let fileName = res.headers['content-disposition'].split('filename=')[1];
          let f = new Blob([res.data]);
          FileSaver.saveAs(f, decodeURIComponent(fileName));
          hide();
        }
      })
      .catch(err => {
        //  message.error(err.response.data.message);
        message.error('下载失败，请重试!');
        hide();
      });
  };
  onExportCancel = () => {
    this.setState({ excelVisible: false });
  };
  //关闭侧拉框回调
  close = flag => {
    this.setState({ visibel: false, model: {} }, () => {
      if (flag) {
        this.customTable.search();
      }
    });
  };

  render() {
    const {
      columns,
      searchParams,
      searchForm,
      exportColumns,
      excelVisible,
      tabValue,
      model,
      visibel,
    } = this.state;
    return (
      <div className="train">
        {/* <Tabs defaultActiveKey={tabValue} onChange={this.handleTab}>
          <TabPane tab="费用申请政策" key="apply" />
          <TabPane tab="费用报销政策" key="export" />
        </Tabs> */}
        <SearchArea
          // eventHandle={this.eventSearchAreaHandle}
          searchForm={searchForm}
          submitHandle={this.search}
          clearHandle={this.clear}
          maxLength={4}
        />

        <div style={{ margin: '20px 0' }}>
          {/*新建*/}
          <Button
            style={{ margin: '20px 20px 20px 0' }}
            className="create-btn"
            type="primary"
            onClick={this.create}
          >
            新建
          </Button>
          <Button style={{ marginRight: '20px' }} onClick={this.handleExport}>
            导出
          </Button>
          <CustomTable
            columns={columns}
            url={`${config.taxUrl}/api/invoicing/site/pageByCondition`}
            ref={ref => (this.customTable = ref)}
          />
          <SlideFrame
            // title={model.id ? '编辑税种' : '新增税种'}
            show={visibel}
            onClose={() => {
              this.setState({
                visibel: false,
                model: {},
              });
            }}
          >
            <NewInvoicingSite params={model} close={this.close} />
          </SlideFrame>
          {/* <Button type="primary" onClick={this.handleCreate}>
                        {this.$t({ id: 'common.create' })}
                    </Button> */}
        </div>
        {/* 导出 */}
        <ExcelExporter
          visible={excelVisible}
          onOk={this.confirmExport}
          columns={exportColumns}
          canCheckVersion={false}
          fileName={'开票点信息'}
          onCancel={this.onExportCancel}
          excelItem={'PREPAYMENT_FINANCIAL_QUERY'}
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(InvoicingSite);
