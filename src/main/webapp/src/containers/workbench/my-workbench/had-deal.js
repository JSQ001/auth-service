import React, { Component } from 'react';
import { Input, Row, Col, Spin, message } from 'antd';
import config from 'config';
import SearchArea from 'widget/search-area';
import Table from 'widget/table';
import moment from 'moment';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import Service from './service';

const { Search } = Input;

class HadDeal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'list',
          id: 'companyId',
          label: '单据公司',
          colSpan: 6,
          listType: 'available_company',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'id',
          labelKey: 'name',
          single: true,
        },
        {
          type: 'list',
          id: 'employeeId',
          label: '申请人',
          colSpan: 6,
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'abc',
          items: [
            { type: 'date', id: 'dateFrom', colSpan: 3, label: '入池日期从' },
            { type: 'date', id: 'dateTo', colSpan: 3, label: '入池日期至' },
          ],
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'businessTypeId',
          label: '业务类型',
          listType: 'bussiness_type',
          labelKey: 'businessTypeName',
          valueKey: 'id',
          single: true,
        },
        {
          type: 'select',
          colSpan: 6,
          id: 'documentTypeId',
          label: '单据类型',
          options: [],
          method: 'get',
          getUrl: `${config.expenseUrl}/api/invoice/type/query/for/invoice?tenantId=${
            props.company.tenantId
          }&setOfBooksId=${props.company.setOfBooksId}`,
          labelKey: 'invoiceTypeName',
          valueKey: 'id',
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'currentNodeId',
          label: '处理业务节点',
          listType: 'business_node',
          labelKey: 'businessNodeName',
          valueKey: 'id',
          single: true,
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'currentActionId',
          label: '处理节点动作',
          listType: 'node_action',
          labelKey: 'nodeActionName',
          valueKey: 'id',
          single: true,
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'abcd',
          items: [
            { type: 'date', id: 'handleDateFrom', colSpan: 3, label: '处理完成日期从' },
            { type: 'date', id: 'handleDateTo', colSpan: 3, label: '处理完成日期至' },
          ],
        },
      ],
      detailColumns: [
        {
          title: '单据编号',
          dataIndex: 'documentNumber',
          align: 'center',
        },
        {
          title: '单据公司',
          dataIndex: 'companyName',
          align: 'center',
        },
        {
          title: '业务类别',
          dataIndex: 'businessTypeName',
          align: 'center',
        },
        {
          title: '单据类型',
          dataIndex: 'documentTypeName',
          align: 'center',
        },
        {
          title: '申请人',
          dataIndex: 'employeeName',
          align: 'center',
        },
        {
          title: '入池日期',
          dataIndex: 'enterDate',
          align: 'center',
        },
        {
          title: '处理业务节点',
          dataIndex: 'currentNodeName',
          align: 'center',
        },
        {
          title: '处理节点动作',
          dataIndex: 'actionName',
          align: 'center',
        },
        {
          title: '处理完成时间',
          dataIndex: 'handleDate',
          align: 'center',
        },
        {
          title: '处理意见',
          dataIndex: 'operatorText',
          align: 'center',
        },
      ],
      tableData: [],
      tableTotalNum: 0,
      pagination: {
        showSizeChanger: true,
        showQuickJumper: true,
      },
      currentPage: 0,
      size: 10,
    };
  }

  componentDidMount() {
    this.search();
  }

  /**
   * 搜索
   */
  search = (params = {}) => {
    const newParams = {
      companyId: params.companyId && params.companyId[0],
      employeeId: params.employeeId && params.employeeId[0],
      dateFrom: params.dateFrom && params.dateFrom.format('YYYY-MM-DD'),
      dateTo: params.dateTo && params.dateTo.format('YYYY-MM-DD'),
      businessTypeId: params.businessTypeId && params.businessTypeId[0],
      documentTypeId: params.documentTypeId,
      currentNodeId: params.currentNodeId && params.currentNodeId[0],
      currentActionId: params.currentActionId && params.currentActionId[0],
      handleDateFrom: params.handleDateFrom && params.handleDateFrom.format('YYYY-MM-DD'),
      handleDateTo: params.handleDateTo && params.handleDateTo.format('YYYY-MM-DD'),
    };
    this.getList(newParams);
  };

  /**
   * 切换分页
   */
  onChangeCheckedPage = page => {
    const { currentPage } = this.state;

    if (page - 1 !== currentPage) {
      this.setState(
        {
          currentPage: page - 1,
          loading: true,
        },
        () => {
          this.getList();
        }
      );
    }
  };

  /**
   * 切换每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    this.setState(
      {
        size: pageSize,
        loading: true,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 获取表格数据
   */
  getList = (value = {}) => {
    const { currentPage, size } = this.state;

    const params = { ...value, page: currentPage, size };
    this.setState({ loading: true });

    Service.queryHadDealOrder(params)
      .then(data => {
        const total = Number(data.headers['x-total-count']) || 0;
        if (data && data.data && data.data.length >= 0) {
          const newData = data.data.map(item => {
            return Object.assign(item, {
              enterDate: item.enterDate ? moment(item.enterDate).format('YYYY-MM-DD') : '',
              handleDate: item.handleDate ? moment(item.handleDate).format('YYYY-MM-DD') : '',
            });
          });
          this.setState({
            tableData: newData,
            tableTotalNum: total,
            loading: false,
          });
        }
      })
      .catch(err => message.error(err.response.data.message));
  };

  searchOrder = order => {
    this.getList({ documentNumber: order });
  };

  /**
   * 点击跳转
   */
  select = record => {
    const { dispatch } = this.props;
    if (record && record.pageContent) {
      dispatch(
        routerRedux.push({
          pathname: `/workbench/my-workbench/detail-reimburse-read/${record.id}/${
            record.documentId
          }`,
        })
      );
    }
  };

  render() {
    const { searchForm, detailColumns, tableData, tableTotalNum, pagination, loading } = this.state;
    Object.assign(pagination, {
      total: tableTotalNum,
      onChange: this.onChangeCheckedPage,
      onShowSizeChange: this.onShowSizeChange,
      showTotal: total => `共搜到 ${total} 条数据`,
    });

    return (
      <div style={{ paddingTop: '10px' }}>
        {loading && (
          <div
            style={{
              position: 'fixed',
              left: '55%',
              top: '50%',
              zIndex: 10000,
            }}
          >
            <Spin size="large" />
          </div>
        )}
        <SearchArea searchForm={searchForm} maxLength={4} submitHandle={this.search} />
        <Row style={{ marginBottom: 10, marginTop: 10 }}>
          <Col span={18} />
          <Col span={6}>
            <Search placeholder="请输入单据编号" onSearch={this.searchOrder} enterButton />
          </Col>
        </Row>
        <Table
          onRow={record => {
            return {
              onClick: event => {
                this.select(record, event);
              }, // 点击行
            };
          }}
          rowKey={record => record.id}
          columns={detailColumns}
          dataSource={tableData}
          pagination={pagination}
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

export default connect(mapStateToProps)(HadDeal);
