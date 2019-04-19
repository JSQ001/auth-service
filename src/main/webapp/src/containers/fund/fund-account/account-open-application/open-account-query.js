import React from 'react';
import { connect } from 'dva';
import SearchArea from 'components/Widget/search-area';
import Table from 'widget/table';
import { Button, Popover, Alert, message, Input, Row, Col, Modal } from 'antd';
import 'styles/fund/account.scss';
import SlideFrame from 'widget/slide-frame';
import moment from 'moment';
import accountService from './account.service';
import OpenAccountQuery from './open-account-maintain';

const { confirm } = Modal;
const { Search } = Input;

class OpenAccountList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false, // loading状态
      buttonLoading: false, // 按钮loading状态
      batchDelete: true, // 批量删除标志
      noticeAlert: null, // 提示信息
      slideVisible: false, // 侧边栏显示
      selectedRow: [],
      selectedRowKeys: [],
      searchParams: {}, // 查询条件
      editModel: {}, // 点击行时的编辑数据
      disabledSubmit: true, // 是否允许提交
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
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
          id: 'approveStatus',
          options: [],
          valueListCode: 'ZJ_BILL_STATUS',
        },
      ],
      // 列表columns
      columns: [
        {
          title: '申请单号',
          dataIndex: 'documentNumber',
          width: 100,
          align: 'center',
        },
        {
          title: '单据类型',
          dataIndex: 'applicationName',
          width: 100,
          align: 'center',
        },
        {
          title: '申请人',
          dataIndex: 'employeeName',
          width: 100,
          align: 'center',
        },
        {
          title: '申请日期',
          dataIndex: 'requisitionDate',
          width: 100,
          align: 'center',
        },
        {
          title: '开户银行',
          dataIndex: 'openBankName',
          width: 150,
          align: 'center',
        },
        {
          title: '申请公司',
          dataIndex: 'companyName',
          width: 100,
          align: 'center',
        },
        {
          title: '备注',
          dataIndex: 'remarks',
          width: 200,
          align: 'center',
          render: remarks => <Popover content={remarks}>{remarks}</Popover>,
        },
        {
          title: '状态',
          dataIndex: 'approveStatusDesc',
          width: 100,
          align: 'center',
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
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true, noticeAlert: null, selectedRowKeys: [] });
    accountService
      .getAccountHeadList(pagination.page, pagination.pageSize, searchParams)
      .then(response => {
        const { data } = response;
        /* eslint-disable */
        data.map(item => {
          item.requisitionDate = item.requisitionDate
            ? moment(new Date(item.requisitionDate)).format('YYYY-MM-DD')
            : '';
          item.applicationName = '开户申请';
        });
        /* eslint-disable */

        this.setState({
          tableData: data,
          loading: false,
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            current: pagination.page + 1,
            pageSize: pagination.pageSize,
            onChange: this.onChangePager,
            onShowSizeChange: this.onShowSizeChange,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              this.$t('common.show.total', { range0: `${range[0]}`, range1: `${range[1]}`, total }),
          },
        });
      })
      .catch(error => {
        message.error(error.response.data.message);
      });
  }

  /**
   * 分页点击
   */
  onChangePager = pagination => {
    const temp = {};
    console.log(pagination);
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
   * 新建
   */
  handleCreateClick = () => {
    this.setState({ slideVisible: true });
  };

  /**
   * 删除
   */
  deleteItems = () => {
    const selectedRowKeys = this.state.selectedRowKeys;
    accountService
      .batchDeleteAccount(selectedRowKeys)
      .then(res => {
        if (res.status === 200) {
          console.log(res);
          message.success('删除成功！');
          this.getList();
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 检查删除
   */
  checkDelete = () => {
    const { selectedRow } = this.state;
    const ok = selectedRow.every(item => {
      return item.approveStatus !== 'ZJ_APPROVED' && item.approveStatus !== 'ZJ_PENGDING';
    });
    if (ok) {
      this.showDeleteConfirm();
    } else {
      message.error('您所选单据中有已经审批或者审批中的单据！');
    }
  };

  /**
   * 展示删除弹框
   */
  showDeleteConfirm = () => {
    let _this = this;
    confirm({
      title: '确定删除选中的单据吗?',
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        _this.deleteItems();
      },
    });
  };

  /**
   * 提交
   */
  handleSubmitClick = () => {
    const selectedRowKeys = this.state.selectedRowKeys;
    accountService
      .submitAccount(selectedRowKeys)
      .then(res => {
        if (res.status === 200) {
          console.log(res);
          message.success('提交成功！');
          this.getList();
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
    return 'submit';
  };

  /**
   * 关闭侧边栏
   */
  handleClose = value => {
    console.log(value);
    this.setState({
      slideVisible: false,
      editModel: {},
    });
    if (value === 'save') {
      this.getList();
    }
  };

  /**
   * 重置
   */
  clear = () => {
    this.setState({
      searchParams: {},
    });
  };

  /**
   * 搜索
   */
  search = value => {
    const { searchParams } = this.state;
    console.log(value);
    this.setState(
      {
        searchParams: {
          ...searchParams,
          companyId: value.companyId ? value.companyId : '',
          openBank: value.openBank ? value.openBank : '',
          requisitionDateFrom: value.dateFrom ? moment(value.dateFrom).format('YYYY-MM-DD') : '',
          requisitionDateTo: value.dateTo ? moment(value.dateTo).format('YYYY-MM-DD') : '',
          approveStatus: value.approveStatus ? value.approveStatus : '',
          documentNumber: value.documentNumber ? value.documentNumber : '',
        },
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
    let copyValue = {
      documentNumber: value,
    };
    this.search(copyValue);
  };

  /**
   * 提示框显示
   */

  noticeAlert = rows => {
    const noticeAlert = (
      <span>
        已选择<span style={{ fontWeight: 'bold', color: '#108EE9' }}> {rows.length} </span> 项
      </span>
    );
    this.setState({
      noticeAlert: rows.length ? noticeAlert : null,
      batchDelete: !rows.length,
    });
  };

  /**
   * 行选择
   */
  onSelectChange = (selectedRowKeys, selectedRow) => {
    console.log(selectedRowKeys, selectedRow);
    this.setState(
      {
        selectedRowKeys,
        selectedRow,
        batchDelete: !(selectedRowKeys.length > 0),
        disabledSubmit: !(selectedRowKeys.length > 0),
      },
      () => {
        if (selectedRowKeys.length > 0) {
          this.noticeAlert(selectedRow);
        } else {
          this.setState({
            noticeAlert: null,
          });
        }
      }
    );
  };

  render() {
    const {
      pagination,
      searchForm,
      batchDelete,
      tableData,
      loading,
      buttonLoading,
      columns,
      noticeAlert,
      selectedRowKeys,
      selectedRow,
      slideVisible,
      editModel,
      disabledSubmit,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div className="train">
        {/* 搜索区域 */}
        <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.clear} />
        <div className="table-header">
          <div className="table-header-buttons">
            <Row>
              <Col span={8}>
                {/* 新建 */}
                <Button type="primary" onClick={this.handleCreateClick}>
                  {this.$t('common.create')}
                </Button>
                {/* 删除 */}
                <Button
                  type="danger"
                  disabled={batchDelete}
                  loading={buttonLoading}
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.checkDelete();
                  }}
                >
                  {this.$t('common.delete')}
                </Button>
                {/* 提交 */}
                <Button type="primary" onClick={this.handleSubmitClick} disabled={disabledSubmit}>
                  {this.$t('common.submit')}
                </Button>
              </Col>
              {/* 搜索框 */}
              <Col span={8} offset={8}>
                <Search
                  placeholder="请输入申请单单号"
                  enterButton
                  onSearch={this.searchByDocumentNumber}
                />
              </Col>
            </Row>
            {/* 侧边栏 */}
            <SlideFrame
              title={editModel.id ? '编辑' : '新建'}
              show={slideVisible}
              onClose={() => this.handleClose()}
            >
              <OpenAccountQuery onClose={this.handleClose} params={editModel} />
            </SlideFrame>
          </div>
          {noticeAlert ? (
            <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
          ) : (
            ''
          )}
          {/* 数据列表 */}
          <Table
            onRow={record => {
              return {
                onClick: () => {
                  this.setState({ editModel: record, slideVisible: true });
                },
              };
            }}
            rowKey={record => record.id}
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            rowSelection={rowSelection}
            loading={loading}
            onChange={this.onChangePager}
            bordered
            size="middle"
          />
        </div>
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

export default connect(mapStateToProps)(OpenAccountList);
