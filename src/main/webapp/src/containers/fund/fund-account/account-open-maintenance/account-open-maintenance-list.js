import React from 'react';
import { connect } from 'dva';
import SearchArea from 'components/Widget/search-area';
import Table from 'widget/table';
import { Button, Row, Col, Input, message, Modal } from 'antd';
import 'styles/fund/account.scss';
import moment from 'moment';
import { routerRedux } from 'dva/router';
import maintenanceService from './account-open-maintenance.service';

const { Search } = Input;
const { confirm } = Modal;
class AccountOpenMaintenanceList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      loading: false,
      disabledEdit: true,
      searchParams: {},
      selectedRowKeys: [], // 列表选择的行ID
      selectedRow: [], // 列表选择的对象
      searchForm: [
        // 公司
        {
          colSpan: 6,
          type: 'list',
          id: 'companyId',
          label: '公司',
          listType: 'company',
          labelKey: 'name',
          valueKey: 'id',
          single: true,
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          placeholder: '请选择',
        },
        // 时间
        {
          type: 'items',
          id: 'date',
          items: [
            { type: 'date', id: 'dateFrom', label: '时间从' },
            { type: 'date', id: 'dateTo', label: '时间至' },
          ],
          colSpan: 6,
        },
        // 开户银行
        {
          colSpan: 6,
          type: 'value_list',
          label: '开户银行',
          id: 'openBank',
          options: [],
          valueListCode: 'ZJ_OPEN_BANK',
        },
        // 状态
        {
          colSpan: 6,
          type: 'value_list',
          label: '状态',
          id: 'status',
          options: [],
          valueListCode: 'ZJ_BILL_STATUS',
        },
      ],
      columns: [
        {
          title: '账号',
          dataIndex: 'accountNumber',
          width: 100,
          align: 'center',
        },
        {
          title: '户名',
          dataIndex: 'accountName',
          width: 100,
          align: 'center',
        },
        {
          title: '状态',
          dataIndex: 'maintainApproveStatusDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '申请日期',
          dataIndex: 'requisitionDate',
          width: 100,
          align: 'center',
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: '开户银行',
          dataIndex: 'openBankName',
          width: 100,
          align: 'center',
        },
        {
          title: '申请单号',
          dataIndex: 'documentNumber',
          width: 200,
          align: 'center',
        },
      ],
    };
  }

  componentDidMount() {
    this.getList();
  }

  /**
   * 搜索
   */
  handleSearch = value => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          companyId: value.companyId ? value.companyId[0] : '',
          requisitionDateFrom: value.dateFrom ? moment(value.dateFrom).format('YYYY-MM-DD') : '',
          requisitionDateTo: value.dateTo ? moment(value.dateTo).format('YYYY-MM-DD') : '',
          openBank: value.openBank ? value.openBank : '',
          maintainApproveStatus: value.status ? value.status : '',
          documentNumber: value.documentNumber ? value.documentNumber : '',
        },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 获取列表页数据
   */
  getList = () => {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true });
    maintenanceService
      .getAccountOpenMaintenanceList(pagination.page, pagination.pageSize, searchParams)
      .then(response => {
        const { data } = response;
        data.map(item => {
          return item.requisitionDate
            ? moment(new Date(item.requisitionDate)).format('YYYY-MM-DD')
            : '';
        });
        this.setState({
          tableData: data,
          loading: false,
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            current: pagination.page + 1,
            // pageSize: pagination.pageSize,
            onChange: this.onChangePager,
            // onShowSizeChange: this.onShowSizeChange,
            // showSizeChanger: true,
            // showQuickJumper: true,
            // showTotal: (total, range) =>
            //   this.$t('common.show.total', { range0: `${range[0]}`, range1: `${range[1]}`, total }),
          },
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
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

  // /**
  //  * 改变每页显示的条数
  //  */
  // onChangePager = pagination => {
  //   const temp = {};
  //   temp.page = pagination.current - 1;
  //   temp.current = pagination.current;
  //   temp.pageSize = pagination.pageSize;
  //   this.setState(
  //     {
  //       pagination: temp,
  //     },
  //     () => {
  //       this.getList();
  //     }
  //   );
  // };

  /**
   * 根据单号搜索searchByDocumentNumber
   */
  searchByDocumentNumber = value => {
    const copyValue = {
      documentNumber: value,
    };
    this.handleSearch(copyValue);
  };

  /**
   * onChange选中项发生变化时的回调
   */
  onSelectChange = (selectedRowKeys, selectedRow) => {
    if (selectedRowKeys.length > 0) {
      this.setState({
        selectedRowKeys,
        selectedRow,
        disabledEdit: false,
      });
    }
  };

  /**
   * 维护按钮
   */
  handleMaintainClick = () => {
    const { selectedRow } = this.state;
    this.goDetail(selectedRow[0]);
  };

  /**
   * 作废按钮
   */
  voidClick = () => {
    const { selectedRowKeys } = this.state;
    maintenanceService
      .obsolete(selectedRowKeys[0])
      .then(res => {
        message.success('作废成功！');
        this.getList();
        console.log(res);
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 展示作废弹框
   */
  showDeleteConfirm = () => {
    const that = this;
    confirm({
      title: '确定作废选中的单据吗?',
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        that.voidClick();
      },
    });
  };

  /**
   * 跳转维护详情页
   */
  goDetail = record => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/account-manage/account-open-maintenance/account-open-maintenance-detail/${
          record.id
        }`,
      })
    );
  };

  render() {
    const { searchForm, columns, tableData, disabledEdit, loading, pagination } = this.state;
    const rowSelection = {
      type: 'radio',
      onChange: this.onSelectChange,
    };
    return (
      <div className="train">
        {/* 搜索区域 */}
        <SearchArea searchForm={searchForm} submitHandle={this.handleSearch} />
        <div className="table-header">
          <Row>
            <Col span={18}>
              <div className="table-header-buttons">
                {/* 维护 */}
                <Button type="primary" onClick={this.handleMaintainClick} disabled={disabledEdit}>
                  维护
                </Button>
                <Button type="danger" disabled={disabledEdit} onClick={this.showDeleteConfirm}>
                  作废
                </Button>
              </div>
            </Col>
            <Col span={6}>
              <Search
                placeholder="请输入申请单单号"
                enterButton
                onSearch={this.searchByDocumentNumber}
              />
            </Col>
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
          pagination={pagination}
          loading={loading}
          rowKey={record => record.id}
          rowSelection={rowSelection}
          columns={columns}
          onChange={this.onChangePager}
          dataSource={tableData}
          bordered
          size="middle"
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

export default connect(mapStateToProps)(AccountOpenMaintenanceList);
