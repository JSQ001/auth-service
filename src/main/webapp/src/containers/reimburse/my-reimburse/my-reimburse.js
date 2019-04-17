import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import {
  Button,
  Table,
  Menu,
  Dropdown,
  Icon,
  Row,
  Col,
  Badge,
  Popconfirm,
  Popover,
  Input,
  InputNumber,
  message,
  Divider,
} from 'antd';
import config from 'config';
// import menuRoute from 'routes/menuRoute';
import httpFetch from 'share/httpFetch';
import moment from 'moment';

import 'styles/reimburse/reimburse.scss';
import SearchArea from 'widget/search-area';

import CustomTable from 'widget/custom-table';

const statusList = [
  { value: 1001, label: '编辑中' },
  { value: 1002, label: '审批中' },
  { value: 1003, label: '撤回' },
  { value: 1004, label: '审批通过' },
  { value: 1005, label: '审批驳回' },
  { value: 2002, label: '审核通过' },
  { value: 2001, label: '审核驳回' },
];

const Search = Input.Search;
class MyReimburse extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      pagination: {
        total: 0,
      },
      status: {
        1001: { label: '编辑中', state: 'default' },
        1004: { label: '审批通过', state: 'success' },
        1002: { label: '审批中', state: 'processing' },
        1005: { label: '审批驳回', state: 'error' },
        1003: { label: '撤回', state: 'warning' },
        0: { label: '未知', state: 'warning' },
        2004: { label: '支付成功', state: 'success' },
        2003: { label: '支付中', state: 'processing' },
        2002: { label: '审核通过', state: 'success' },
        2001: { label: '审核驳回', state: 'error' },
      },
      searchForm: [
        {
          type: 'list',
          listType: 'select_report_type',
          options: [],
          id: 'documentTypeId',
          label: '单据类型',
          valueKey: 'id',
          labelKey: 'reportTypeName',
          single: true,
          colSpan: 6,
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
        },
        {
          type: 'items',
          id: 'date',
          items: [
            { type: 'date', id: 'requisitionDateFrom', label: '申请日期从' },
            { type: 'date', id: 'requisitionDateTo', label: '申请日期至' },
          ],
          colSpan: 6,
        },
        {
          type: 'list',
          listType: 'select_authorization_user',
          options: [],
          id: 'applicantId',
          label: '申请人',
          labelKey: 'userName',
          valueKey: 'userId',
          single: true,
          colSpan: 6,
        },
        { type: 'select', id: 'status', label: '状态', options: statusList, colSpan: 6 },
        {
          type: 'select',
          key: 'currency',
          id: 'currencyCode',
          label: '币种',
          getUrl: `${config.mdataUrl}/api/currency/rate/list`,
          getParams: {
            // enable: true,
            setOfBooksId: this.props.company.setOfBooksId,
            tenantId: this.props.company.tenantId,
          },
          options: [],
          method: 'get',
          labelKey: 'currencyCode',
          renderOption: item => item.currencyCode + '-' + item.currencyName,
          valueKey: 'currencyCode',
          colSpan: 6,
          dataKey: 'records',
        },
        {
          type: 'items',
          id: 'amount',
          colSpan: 6,
          items: [
            { type: 'input', id: 'amountFrom', label: '金额从' },
            { type: 'input', id: 'amountTo', label: '金额至' },
          ],
        },
        {
          type: 'input',
          id: 'remark',
          colSpan: 12,
          label: '备注',
        },
      ],
      columns: [
        {
          /*单号*/
          title: this.$t('myReimburse.businessCode'),
          dataIndex: 'requisitionNumber',
          width: 220,
          align: 'center',
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          /*单据名称*/
          title: '单据类型',
          dataIndex: 'documentTypeName',
          width: 150,
          align: 'center',
          render: recode => <Popover content={recode}>{recode || '-'}</Popover>,
        },
        {
          /*申请人*/
          title: this.$t('myReimburse.applicationName'),
          dataIndex: 'applicantName',
          width: 90,
          align: 'center',
        },
        {
          /*提交日期*/
          title: '申请日期',
          dataIndex: 'requisitionDate',
          width: 120,
          align: 'center',
          render: value => (
            <Popover content={value ? moment(value).format('YYYY-MM-DD') : ''}>
              {value ? moment(value).format('YYYY-MM-DD') : ''}
            </Popover>
          ),
        },
        {
          /*币种*/
          title: this.$t('myReimburse.currencyCode'),
          dataIndex: 'currencyCode',
          width: 80,
          align: 'center',
          render: recode => <Popover content={recode}>{recode || '-'}</Popover>,
        },
        {
          /*金额*/
          title: this.$t('myReimburse.totalAmount'),
          dataIndex: 'totalAmount',
          width: 110,
          align: 'center',
          render: this.filterMoney,
        },
        {
          /*本币金额*/
          title: this.$t('myReimburse.functionalAmount'),
          dataIndex: 'functionalAmount',
          width: 110,
          align: 'center',
          render: this.filterMoney,
        },
        {
          /*事由*/
          title: this.$t('myReimburse.remark'),
          dataIndex: 'description',
          align: 'center',
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          title: '状态',
          dataIndex: 'status',
          width: 110,
          align: 'center',
          render: (value, record) => {
            return (
              <Badge
                status={this.state.status[value].state}
                text={this.state.status[value].label}
              />
            );
          },
        },
      ],
      data: [],
      menu: [],
      page: 0,
      pageSize: 10,
      loading: false,
      // newReimburePage: menuRoute.getRouteItem('new-reimburse', 'key'),
      // detailReimburePage: menuRoute.getRouteItem('reimburse-detail', 'key'),
      searchParams: {},
      total: 0,
    };
  }

  search = e => {
    if (e.applicationId && e.applicationId.length) {
      e.applicationId = e.applicationId[0];
    }
    console.log(e);
    let params = { ...this.state.searchParams, ...e, allForm: false };
    params.documentTypeId && (params.documentTypeId = params.documentTypeId[0]);
    params.requisitionDateFrom &&
      (params.requisitionDateFrom = moment(params.requisitionDateFrom).format('YYYY-MM-DD'));
    params.requisitionDateTo &&
      (params.requisitionDateTo = moment(params.requisitionDateTo).format('YYYY-MM-DD'));

    this.setState({ searchParams: params }, () => {
      this.refs.table.search(this.state.searchParams);
    });
  };

  searchNumber = e => {
    this.setState(
      { searchParams: { ...this.state.searchParams, requisitionNumber: e, allForm: false } },
      () => {
        this.refs.table.search(this.state.searchParams);
      }
    );
  };

  componentWillMount() {
    this.getForms();
  }

  getForms = () => {
    const { menu } = this.state;

    httpFetch
      .get(
        `${config.expenseUrl}/api/expense/report/type/owner/all?setOfBooksId=${
          this.props.company.setOfBooksId
        }`
      )
      .then(res => {
        this.setState({ menu: res.data });
      })
      .catch(err => {
        message.error('网路错误！请稍后重试');
      });
  };

  //跳转到新建页面
  newReimburseForm = value => {
    const { menu } = this.state;
    let menuItem = menu.find(item => item.id === value.key);
    this.props.dispatch(
      routerRedux.push({
        pathname: `/my-reimburse/my-reimburse/new-reimburse/new/${menuItem.id}/${menuItem.formId}`,
      })
    );
  };

  //跳转到详情
  handleRowClick = recod => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/my-reimburse/my-reimburse/reimburse-detail/${recod.id}`,
      })
    );
  };

  render() {
    const { checkboxListForm, searchForm, pagination, menu, columns } = this.state;
    return (
      <div className="reimburse-container">
        <SearchArea searchForm={searchForm} submitHandle={this.search} maxLength={4} />
        <Row style={{ marginBottom: 10, marginTop: 10 }}>
          <Col id="my-reimburse-drop" style={{ position: 'relative' }} span={18}>
            <Dropdown
              getPopupContainer={() => document.getElementById('my-reimburse-drop')}
              trigger={['click']}
              overlay={
                <Menu onClick={this.newReimburseForm}>
                  {menu.map(item => <Menu.Item key={item.id}>{item.reportTypeName}</Menu.Item>)}
                </Menu>
              }
            >
              <Button type="primary">
                新建对公报账单<Icon type="down" />
              </Button>
            </Dropdown>
          </Col>
          <Col span={6}>
            <Search
              placeholder="请输入报账单单号"
              style={{ width: '100%' }}
              onSearch={this.searchNumber}
              enterButton
            />
          </Col>
        </Row>

        <CustomTable
          onClick={this.handleRowClick}
          ref="table"
          columns={columns}
          url={`${config.expenseUrl}/api/expense/report/header/my`}
        />
      </div>
    );
  }
}
// MyReimburse.contextTypes = {
//   router: React.PropTypes.object
// }
function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(MyReimburse);
