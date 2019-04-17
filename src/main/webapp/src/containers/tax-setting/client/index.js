import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Button, Menu, Dropdown, Icon, Row, Col, Input, message, Badge } from 'antd';
import config from 'config';
import moment from 'moment';
import NewUpdateClient from './new-update-client';
import SlideFrame from 'components/Widget/slide-frame';

import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';

import service from './service';

const Search = Input.Search;

const statusList = [
  { value: 'GENERATE', label: '编辑中' },
  { value: 'APPROVAL_PASS', label: '审批通过' },
  { value: 'APPROVAL', label: '审批中' },
  { value: 'APPROVAL_REJECT', label: '审批驳回' },
  { value: 'WITHDRAW', label: '撤回' },
];
const sourceList = [{ value: 'MANUAL', label: '手工' }, { value: 'INTERFACE', label: '系统接口' }];
const transactionTypeList = [
  { value: 'TAX_CLIENT_NEW', label: '客户信息新增' },
  { value: 'TAX_CLIENT_EDIT', label: '客户信息修改' },
];
const clientTypeList = [
  { value: '01', label: '企业' },
  { value: '02', label: '个人' },
  { value: '03', label: '事业单位' },
];

class TravelApplicationForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      pagination: {
        total: 0,
      },
      status: {
        GENERATE: { label: '编辑中', state: 'default' },
        APPROVAL_PASS: { label: '审批通过', state: 'success' },
        APPROVAL: { label: '审批中', state: 'processing' },
        APPROVAL_REJECT: { label: '审批驳回', state: 'error' },
        WITHDRAW: { label: '撤回', state: 'warning' },
      },
      source: {
        MANUAL: { label: '手工', state: '1' },
        INTERFACE: { label: '系统接口', state: '2' },
      },
      searchForm: [
        {
          type: 'input',
          id: 'applicationCode',
          label: '申请编号',
          options: [],
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientNumber',
          label: '客户编号',
          options: [],
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientName',
          label: '客户名称',
          options: [],
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'clientType',
          label: '客户类型',
          options: clientTypeList,
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'transactionType',
          label: '事务类型',
          options: transactionTypeList,
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'transactionStatus',
          label: '事务状态',
          options: statusList,
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'importFlag',
          label: '数据来源',
          options: sourceList,
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: '申请编号',
          dataIndex: 'applicationCode',
          align: 'center',
          width: 200,
          tooltips: false,
          render: (desc, record) => {
            return (
              <span>
                <a
                  onClick={e =>
                    this.setState({ showClientFlag: true, record: record, title: '编辑客户' })
                  }
                >
                  {desc}
                </a>
              </span>
            );
          },
        },
        {
          title: '客户编号',
          dataIndex: 'clientNumber',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '客户名称',
          dataIndex: 'clientName',
          align: 'center',
          width: 100,
        },
        // {
        //   title: '申请日期',
        //   dataIndex: 'requisitionDate',
        //   align: 'center',
        //   width: 120,
        //   render: value => moment(value).format('YYYY-MM-DD'),
        // },
        {
          title: '客户类型',
          dataIndex: 'clientTypeName',
          align: 'center',
          width: 90,
        },
        {
          title: '纳税人名称',
          dataIndex: 'taxpayerName',
          align: 'center',
          tooltips: true,
          width: 200,
        },
        {
          title: '事务类型',
          dataIndex: 'transactionTypeName',
          align: 'center',
          width: 150,
        },
        {
          title: '事务状态',
          dataIndex: 'transactionStatus',
          align: 'center',
          width: 100,
          render: value => (
            <Badge status={this.state.status[value].state} text={this.state.status[value].label} />
          ),
        },
        {
          title: '数据来源',
          dataIndex: 'importFlag',
          align: 'center',
          width: 100,
          render: value => (
            <Badge status={this.state.source[value].state} text={this.state.source[value].label} />
          ),
        },
      ],
      searchParams: {},
      menus: [],
    };
  }

  componentDidMount() {
    // this.getApplicationTypeList();
  }

  //获取申请单类型
  getApplicationTypeList = () => {
    let searchForm = this.state.searchForm;
    //获取可新建的单据类型
    this.setState({ menus: transactionTypeList });
  };

  //获取列表
  getList = () => {
    let { searchParams } = this.state;

    this.table.search(searchParams);
  };

  //搜索
  search = values => {
    this.setState({ searchParams: { ...this.state.searchParams, ...values } }, () => {
      this.getList();
    });
  };

  //清除
  clear = () => {
    this.setState({ searchParams: {} });
  };

  //跳转到新建页面
  newApplicationForm = value => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/tax-setting/client/new-client-application/' + value.key,
      })
    );
  };

  handleMenuClick = e => {
    if (e.key == 1) {
      this.setState({
        showClientFlag: true,
        record: {
          transactionType: 'TAX_CLIENT_NEW',
          transactionTypeName: '客户信息新增',
          employeeName: this.props.user.userName,
          transactionStatus: 'GENERATE',
        },
        title: '新建客户',
      });
    } else {
      this.setState({
        showClientFlag: true,
        record: {
          transactionType: 'TAX_CLIENT_EDIT',
          transactionTypeName: '客户信息修改',
          employeeName: this.props.user.userName,
          transactionStatus: 'GENERATE',
        },
        title: '修改客户',
      });
    }
  };

  handleCloseClient = refresh => {
    this.setState({ showClientFlag: false }, () => {
      refresh === true && this.table.search();
    });
  };

  render() {
    const {
      searchForm,
      columns,
      menus,
      showClientFlag,
      title,
      record,
      expenseSource,
      businessCardEnabled,
    } = this.state;
    const { user } = this.props;
    const menusUi = (
      <Menu onClick={this.handleMenuClick}>
        <Menu.Item key={1}>新建客户申请单</Menu.Item>
        <Menu.Item key={2}>修改客户申请单</Menu.Item>
      </Menu>
    );

    return (
      <div className="client-container">
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          maxLength={4}
          clearHandle={this.clear}
        />
        <Row style={{ marginBottom: 10, marginTop: 10 }}>
          <Col id="client-application-form-drop" style={{ position: 'relative' }} span={18}>
            <Dropdown
              getPopupContainer={() => document.getElementById('client-application-form-drop')}
              trigger={['click']}
              overlay={menusUi}
            >
              <Button type="primary">
                客户申请单<Icon type="down" />
              </Button>
            </Dropdown>
          </Col>
        </Row>
        <CustomTable
          ref={ref => (this.table = ref)}
          columns={columns}
          url={`${config.taxUrl}/api/tax/client/application/query/condition`}
        />
        <SlideFrame
          show={showClientFlag}
          title={title}
          onClose={() =>
            this.setState({ showClientFlag: false, nowExpense: null, expenseSource: '' })
          }
          width="900px"
        >
          <NewUpdateClient
            params={{
              record,
              expenseSource,
              slideFrameShowFlag: showClientFlag,
              businessCardEnabled,
              user,
            }}
            onClose={this.handleCloseClient}
          />
        </SlideFrame>
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

export default connect(mapStateToProps)(TravelApplicationForm);
