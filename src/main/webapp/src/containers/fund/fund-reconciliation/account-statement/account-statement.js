import React, { Component } from 'react';
import { connect } from 'dva';
import { Button, Table, Alert, Modal, message, Badge } from 'antd';
import SlideFrame from 'widget/slide-frame';
import AccountStatementSearch from './account-statement-search';
import AccountStatementDetail from './account-statement-detail';
import AccountStatementService from './account-statement.service';

const { confirm } = Modal;
class accountStatement extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false, // 加载画面
      searchParams: {}, // 查询条件
      tableData: [], // 表单数据
      editModel: {}, // 点击编辑数据
      slideVisible: false, // 侧滑进行显示
      selectedRowKeys: [], // 选中行的key值
      selectedRow: [], // 选中行的所有value
      noticeAlert: null, // 提示信息
      batchDelete: true, // 删除按钮状态(是否禁用)
      pagination: {
        // 分页属性
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      columns: [
        // Table组件的属性
        {
          title: '交易时间',
          dataIndex: 'accountDate',
          align: 'center',
          width: 120,
        },
        {
          title: '银行账号',
          dataIndex: 'accountId',
          align: 'center',
          width: 200,
        },
        {
          title: '借方金额',
          dataIndex: 'debitAmount',
          align: 'center',
          width: 110,
        },
        {
          title: '贷方金额',
          dataIndex: 'creditAmount',
          align: 'center',
          width: 110,
        },
        {
          title: '余额',
          dataIndex: 'sinceAmount',
          align: 'center',
          width: 110,
        },
        {
          title: '对方账号',
          dataIndex: 'otherAccount',
          align: 'center',
          width: 110,
        },
        {
          title: '对方账户',
          dataIndex: 'otherAccountName',
          align: 'center',
          width: 110,
        },
        {
          title: '备注',
          dataIndex: 'summary',
          align: 'center',
          width: 110,
        },
        {
          title: '交易流水号',
          dataIndex: 'bankSn',
          align: 'center',
          width: 110,
        },
        {
          title: '对账码',
          dataIndex: 'checkCode',
          align: 'center',
          width: 110,
        },
        {
          title: '回单编号',
          dataIndex: 'returnNumber',
          align: 'center',
          width: 110,
        },
        {
          title: '发生时间',
          dataIndex: 'rightTimestamp',
          align: 'center',
          width: 110,
        },
        {
          title: '对账标志',
          dataIndex: 'isBalance',
          align: 'center',
          width: 110,
        },
        {
          title: '生成单据标志',
          dataIndex: 'generateFlag',
          align: 'center',
          width: 120,
          render: generateFlag => <Badge status={generateFlag ? 'success' : 'error'} />,
        },
      ],
    };
  }

  /**
   * 点击改变搜索参数
   */
  search = values => {
    console.log('values', values);
    this.setState(
      {
        searchParams: {
          accountId: values.accountId.id || '',
          accountDateFrom: values.accountDateFrom
            ? values.accountDateFrom.format('YYYY-MM-DD')
            : '',
          accountDateTo: values.accountDateTo ? values.accountDateTo.format('YYYY-MM-DD') : '',
          otherAccount: values.otherAccount ? values.otherAccount : '',
          amountFrom: values.amountFrom ? values.amountFrom : '',
          amountTo: values.amountTo ? values.amountTo : '',
          direction: values.direction ? values.direction : '',
          generateFlag: values.generateFlag ? values.generateFlag : '',
        },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 搜索请求数据
   */
  getList = () => {
    const { pagination, searchParams } = this.state;
    console.log('searchParams', searchParams);
    this.setState({ loading: true });
    AccountStatementService.getMaintainList(
      pagination.page,
      pagination.pageSize,
      searchParams
    ).then(response => {
      const { data } = response;
      console.log('data', data);
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
        },
      });
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

  /**
   * 新建
   */
  create = () => {
    this.setState({ slideVisible: true });
  };

  /**
   * 关闭侧滑栏
   */
  handleClose = () => {
    this.setState({
      slideVisible: false,
      editModel: {},
    });
  };

  /**
   * 选中提示框
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
   * 选中改变时
   */
  onSelectChange = (selectedRowKeys, selectedRow) => {
    console.log('key', selectedRowKeys);
    this.setState(
      {
        selectedRowKeys,
        batchDelete: !(selectedRowKeys.length > 0),
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

  /**
   * 保存新建或编辑数据
   */
  save = saveData => {
    console.log(saveData);
    AccountStatementService.addFlow(saveData).then(response => {
      console.log(response);
    });
  };

  /**
   * 显示删除弹窗
   */
  showDeleteConfirm = () => {
    const that = this;
    confirm({
      title: '您将删除选择的单据，删除后数据将永久删除。是否继续？',
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        that.deleteItems();
      },
    });
  };

  /**
   * 删除手工增加
   */
  deleteItems = () => {
    const { selectedRowKeys } = this.state;
    AccountStatementService.deleteAccount(selectedRowKeys).then(res => {
      if (res.status === 200) {
        message.success(this.$t('fund.delete.successful1')); /* 删除成功！ */
        this.getList();
        this.setState({
          batchDelete: true,
          selectedRowKeys: [],
          noticeAlert: null,
        });
      }
    });
  };

  // transformTime = date => {
  //   const y = date.getFullYear();
  //   const m = date.getMonth();
  //   const d = date.getDate();
  //   return `${y}-${m + 1}-${d}`;
  // };

  render() {
    const {
      columns,
      editModel,
      slideVisible,
      pagination,
      loading,
      tableData,
      selectedRowKeys,
      selectedRow,
      noticeAlert,
      batchDelete,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div className="account">
        <AccountStatementSearch submitHandle={this.search} />
        <Button type="primary" style={{ margin: '20px 10px' }}>
          同步
        </Button>
        <Button type="primary" style={{ margin: '20px 10px' }}>
          导入
        </Button>
        <Button type="primary" style={{ margin: '20px 10px' }}>
          导出
        </Button>
        <Button type="primary" style={{ margin: '20px 10px' }}>
          生成单据
        </Button>
        <Button type="primary" style={{ margin: '20px 10px' }} onClick={this.create}>
          新增
        </Button>
        <Button
          style={{ margin: '20px 10px' }}
          type="danger"
          disabled={batchDelete}
          // loading={buttonLoading}
          onClick={e => {
            e.preventDefault();
            e.stopPropagation();
            this.showDeleteConfirm();
          }}
        >
          {this.$t('common.delete')}
        </Button>
        <SlideFrame
          title={editModel.id ? '编辑' : '新增'}
          show={slideVisible}
          onClose={this.handleClose}
        >
          <AccountStatementDetail onClose={this.handleClose} params={editModel} save={this.save} />
        </SlideFrame>
        {noticeAlert ? (
          <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
        ) : (
          ''
        )}
        <Table
          onRow={record => {
            return {
              onClick: () => {
                console.log('record', record);
                this.setState({ editModel: record, slideVisible: true });
              },
            };
          }}
          rowKey={record => record.id}
          scroll={{ x: 1200 }}
          columns={columns}
          rowSelection={rowSelection}
          pagination={pagination}
          loading={loading}
          dataSource={tableData}
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

export default connect(mapStateToProps)(accountStatement);
