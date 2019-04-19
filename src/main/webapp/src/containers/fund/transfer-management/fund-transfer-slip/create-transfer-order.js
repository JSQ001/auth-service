import React from 'react';
import { Form, Row, Button, Col, Input, Alert, message, Popconfirm } from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import Table from 'widget/table';
import moment from 'moment';
import FundSearchForm from '../../fund-components/fund-search-form';
import 'styles/fund/account.scss';
import fundTransferSlipService from './fund-transfer-slip-service';

const { Search } = Input;

class CreateTransferOrder extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false, // loading状态
      searchParams: {}, // 模糊查询数据搜索参数
      noticeAlert: null, // 提示信息
      selectedRow: [],
      selectedRowKeys: [],
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      tableData: [],
      searchForm: [
        {
          colSpan: 6,
          type: 'input',
          label: '单据类型',
          id: 'documentType',
        },
        {
          colSpan: 6,
          type: 'modalList',
          label: '单据公司',
          id: 'documentCompany',
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
          id: 'createdBy',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '审批状态',
          id: 'approvalStatus',
          options: [],
          valueListCode: 'ZJ_BILL_STATUS',
        },
        {
          colSpan: 6,
          type: 'intervalDate',
          id: 'intervalDate',
          fromlabel: '单据日期从',
          fromId: 'dateFrom',
          tolabel: '单据日期至',
          toId: 'dateTo',
        },
        {
          colSpan: 6,
          type: 'input',
          label: '笔数',
          id: 'items',
        },
        {
          colSpan: 6,
          type: 'intervalInput',
          label: '金额',
          id: 'amount',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '付款方式',
          id: 'paymentPurpose',
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
          dataIndex: 'adjustBillTypeName',
          width: 100,
        },
        {
          title: '所属公司',
          dataIndex: 'belongCorpName',
          width: 150,
        },
        {
          title: '单据日期',
          dataIndex: 'billDate',
          width: 150,
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: '付款方式',
          dataIndex: 'paymentMethod',
          width: 150,
        },
        {
          title: '笔数',
          dataIndex: 'adjustLineCount',
          width: 50,
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: 100,
          render: amount => this.filterMoney(amount),
        },
        {
          title: '描述',
          dataIndex: 'description',
          width: 200,
        },
        {
          title: '制单人',
          dataIndex: 'employeeName',
          width: 100,
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
   * 列表数据
   */
  getList() {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true });
    fundTransferSlipService
      .getFundTransferList(pagination.page, pagination.pageSize, searchParams)
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
   * 更多展示中的搜索
   */
  handleSearch = values => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          billType: values.documentType ? values.documentType : '', // 单据类型(input)
          corpId:
            values.documentCompany && values.documentCompany[0] ? values.documentCompany[0].id : '', // 所属公司（弹框）
          employeeId: values.createdBy ? values.createdBy : '', // 制单人(input)
          billStatus: values.approvalStatus ? values.approvalStatus.key : '', // 审批状态(select)
          billDateFrom: values.dateFrom ? moment(values.dateFrom).format('YYYY-MM-DD') : '', // 日期起
          billDateTo: values.dateTo ? moment(values.dateTo).format('YYYY-MM-DD') : '', // 日期止
          adjustLineCount: values.items ? values.items : '', // 笔数
          amount: values.amount ? values.amount : '', // 金额
          paymentMethod: values.paymentPurpose ? values.paymentPurpose.key : '', // 付款方式
        },
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
   * 根据单据号搜索
   */
  searchByDocumentNumber = value => {
    let { searchParams } = this.state;
    searchParams = {};
    this.setState(
      {
        searchParams: { ...searchParams, adjustBatchNumber: value },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 编辑或新建
   */
  createOrUpdate = id => {
    const { dispatch } = this.props;
    if (id) {
      dispatch(
        routerRedux.push({
          pathname: `/transfer-management/fund-transfer-slip/new-fund-transfer-list/${id}`,
        })
      );
    } else {
      dispatch(
        routerRedux.push({
          pathname: `/transfer-management/fund-transfer-slip/new-fund-transfer-list`,
        })
      );
    }
  };

  /**
   * 删除
   */
  delete = () => {
    const { selectedRowKeys } = this.state;
    fundTransferSlipService
      .batchDelete(selectedRowKeys)
      .then(response => {
        if (response.data) {
          message.success('删除成功');
          this.setState({
            selectedRowKeys: [],
          });
          this.getList();
        }
      })
      .catch(error => {
        message.error(error.response.data.message);
      });
  };

  /**
   * 提示
   */
  noticeAlert = rows => {
    const noticeAlert = (
      <span>
        已选择<span style={{ fontWeight: 'bold', color: '#108EE9' }}> {rows.length} </span> 项
      </span>
    );
    this.setState({
      noticeAlert: rows.length ? noticeAlert : null,
    });
  };

  /**
   * 行选择
   */
  onSelectChange = (selectedRowKeys, selectedRow) => {
    this.setState({ selectedRowKeys }, () => {
      if (selectedRowKeys.length > 0) {
        this.noticeAlert(selectedRow);
      } else {
        this.setState({
          noticeAlert: null,
        });
      }
    });
  };

  render() {
    const {
      loading,
      columns,
      searchForm,
      pagination,
      tableData,
      selectedRowKeys,
      selectedRow,
      noticeAlert,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div className="train">
        <div className="common-top-area">
          <Row>
            <FundSearchForm
              submitHandle={this.handleSearch}
              searchForm={searchForm}
              maxLength={4}
            />
          </Row>
        </div>
        <div className="table-header">
          <div className="table-header-buttons">
            <Row>
              <Col span={8}>
                <Button
                  type="primary"
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.createOrUpdate();
                  }}
                >
                  新建
                </Button>
                <Popconfirm
                  onConfirm={e => this.delete(e)}
                  title={this.$t('common.confirm.delete')}
                >
                  <Button
                    type="danger"
                    disabled={selectedRowKeys.length === 0}
                    onClick={e => {
                      e.preventDefault();
                      e.stopPropagation();
                    }}
                  >
                    删除
                  </Button>
                </Popconfirm>
              </Col>
              <Col span={8} offset={8}>
                <Search
                  placeholder="资金调拨单号"
                  enterButton
                  onSearch={this.searchByDocumentNumber}
                />
              </Col>
            </Row>
          </div>
          {noticeAlert ? (
            <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
          ) : (
            ''
          )}
        </div>
        <Table
          onRow={record => {
            return {
              onClick: e => {
                e.preventDefault();
                e.stopPropagation();
                this.createOrUpdate(record.id);
              },
            };
          }}
          rowKey={record => record.id}
          rowSelection={rowSelection}
          dataSource={tableData}
          columns={columns}
          bordered
          size="middle"
          pagination={pagination}
          loading={loading}
          onChange={this.onChangePager}
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

export default connect(mapStateToProps)(Form.create()(CreateTransferOrder));
