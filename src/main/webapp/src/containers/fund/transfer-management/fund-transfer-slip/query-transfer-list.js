import React from 'react';
import { Form, Row } from 'antd';
import { connect } from 'dva';
import moment from 'moment';
// import { routerRedux } from 'dva/router';
import Table from 'widget/table';
import { routerRedux } from 'dva/router';
import FundSearchForm from '../../fund-components/fund-search-form';
import 'styles/fund/account.scss';

import accountService from './fund-transfer-slip.service';

class QueryTransferList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchParams: {}, // 模糊查询定义
      loading: false, // loading状态
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
      tableData: [],
      searchForm: [
        {
          colSpan: 6,
          type: 'input',
          label: '单据类型',
          id: 'billTypeName',
        },
        {
          colSpan: 6,
          type: 'modalList',
          label: '单据公司',
          id: 'belongCorpName',
          listType: 'company',
          labelKey: 'name',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'id',
          single: true,
        },
        {
          colSpan: 6,
          type: 'input',
          label: '制单人',
          id: 'employeeName',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '审批状态',
          id: 'billStatusName',
          options: [],
          valueListCode: 'ZJ_BILL_STATUS',
        },
        {
          colSpan: 6,
          type: 'intervalDate',
          id: 'billDateFormat',
          fromlabel: '单据日期从',
          fromId: 'billDateFrom',
          tolabel: '单据日期至',
          toId: 'billDateTo',
        },
        {
          colSpan: 6,
          type: 'input',
          label: '笔数',
          id: 'adjustLineCount',
        },
        {
          colSpan: 6,
          type: 'input',
          label: '金额',
          id: 'amount',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '付款方式',
          id: 'paymentMethodName',
          options: [],
          valueListCode: 'ZJ_PAYMENT_TYPE',
        },
      ],
      columns: [
        {
          title: '单据编号',
          dataIndex: 'adjustBatchNumber',
          width: 100,
        },
        {
          title: '单据类型',
          dataIndex: 'billTypeName',
          width: 100,
        },
        {
          title: '所属公司',
          dataIndex: 'belongCorpName',
          width: 150,
        },
        {
          title: '单据日期',
          dataIndex: 'billDateFormat',
          width: 150,
        },
        {
          title: '付款方式',
          dataIndex: 'paymentMethodName',
          width: 100,
        },
        {
          title: '笔数',
          dataIndex: 'adjustLineCount',
          width: 50,
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: 80,
        },
        {
          title: '描述',
          dataIndex: 'description',
          width: 200,
        },
        {
          title: '制单人',
          dataIndex: 'employeeName',
          width: 80,
        },
        {
          title: '审批状态',
          dataIndex: 'billStatusName',
          width: 100,
        },
      ],
    };
  }

  componentDidMount() {
    this.getList();
  }

  /**
   * 获取列表数据
   */
  getList() {
    // searchForm columns
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true });
    accountService
      .getCpAdjustShow(pagination.page, pagination.pageSize, searchParams)
      .then(response => {
        const { data } = response;
        this.setState({
          tableData: data,
          loading: false,
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            onChange: this.onChangePager,
            current: pagination.page + 1,
            onShowSizeChange: this.onShowSizeChange,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              this.$t('common.show.total', { range0: `${range[0]}`, range1: `${range[1]}`, total }),
          },
        });
      });
  }

  /**
   * 搜索
   */
  handleSearch = values => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          billTypeName: values.billTypeName ? values.billTypeName : '', // 单据类型（input输入框）
          documentCompany: values.documentCompany ? values.documentCompany[0].id : '', // 单据公司（弹框）
          employeeName: values.employeeName ? values.employeeName : '', // 创建人
          billStatus: values.billStatusName ? values.billStatusName.key : '', // 审批状态(select框---值列表)
          billDateFrom: values.billDateFrom ? moment(values.billDateFrom).format('YYYY-MM-DD') : '',
          billDateTo: values.billDateTo ? moment(values.billDateTo).format('YYYY-MM-DD') : '',
          adjustLineCount: values.adjustLineCount ? values.adjustLineCount : '', // 笔数（input输入框）
          amount: values.amount ? values.amount : '', // 金额（input输入框）
          paymentMethod: values.paymentMethodName ? values.paymentMethodName.key : '', // 付款方式(select框)
        },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 改变每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    const temp = {};
    temp.page = current - 1;
    temp.pageSize = pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 分页点击
   */
  onChangePager = pagination => {
    const temp = {};
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 跳转详情页
   */
  goDetail = record => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/transfer-management/fund-transfer-slip/fund-transfer-slip-list/${record.id}`,
      })
    );
  };

  render() {
    const { loading, pagination, searchForm, columns, tableData } = this.state;
    return (
      <div className="train">
        <div className="common-top-area" style={{ paddingBottom: '10px', marginBottom: '40px' }}>
          <Row>
            <FundSearchForm
              submitHandle={this.handleSearch}
              searchForm={searchForm}
              maxLength={4}
            />
          </Row>
        </div>
        <Table
          onRow={record => {
            return {
              onClick: () => {
                this.goDetail(record);
              },
            };
          }}
          rowKey={record => record.id}
          dataSource={tableData}
          pagination={pagination}
          onChange={this.onChangePager}
          columns={columns}
          loading={loading}
          bordered
          size="middle"
        />
      </div>
    );
  }
}
/**
 * 建立组件和数据的映射关系 注意state必传 返回的是需要绑定的model
 * @param {*} state
 */
function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
/**
 * 关联 model
 */
export default connect(map)(Form.create()(QueryTransferList));
