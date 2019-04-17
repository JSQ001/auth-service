import React, { Component } from 'react';
import { connect } from 'dva';
import { Row, Button, Table } from 'antd';
import SlideFrame from 'widget/slide-frame';
import FundSearchForm from '../../fund-components/fund-search-form';
import accountStatementService from './account-statement.service';

class accountStatement extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false, // 加载画面
      searchParams: {}, // 查询条件
      tableData: [], // 表单数据
      editModel: {}, // 点击编辑数据
      slideVisible: false, // 侧滑进行显示
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      searchForm: [
        // 公司
        {
          colSpan: 6,
          type: 'modalList',
          label: '公司',
          id: 'documentCompany',
          listType: 'company',
          labelKey: 'name',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'id',
          single: true,
        },
        //  银行账号
        {
          colSpan: 6,
          type: 'modalList',
          label: '银行账号',
          id: 'accountId',
          listType: 'paymentAccount',
        },
        // 所属银行
        {
          colSpan: 6,
          type: 'valueList',
          label: '所属银行',
          id: 'bankBelong',
          options: [],
          valueListCode: 'ZJ_OPEN_BANK',
        },
        //  日期区间
        {
          colSpan: 6,
          type: 'intervalDate',
          id: 'intervalDate',
          fromlabel: '发生日期从',
          placeholder: '请选择',
          fromId: 'accountDateFrom',
          tolabel: '发生日期至',
          toId: 'accountDateTo',
        },
        // 对方账户
        {
          colSpan: 6,
          type: 'input',
          label: '对方账户',
          id: 'otherAccount',
        },
        // 生成单据标志
        {
          colSpan: 6,
          type: 'valueList',
          label: '生成单据标志',
          id: 'isGenerate',
          options: [],
          customizeOptions: [{ value: false, name: '待生成' }, { value: true, name: '已生成' }],
          valueListCode: '',
        },
        // 发生金额
        {
          colSpan: 6,
          type: 'intervalInput',
          label: '金额',
          id: 'amount',
        },
        // 借贷方向
        {
          colSpan: 6,
          type: 'valueList',
          label: '借贷方向',
          id: 'direction',
          options: [],
          customizeOptions: [{ value: 'D', name: '借方' }, { value: 'C', name: '贷方' }],
          valueListCode: '',
        },
      ],
      columns: [
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
          dataIndex: 'isGenerate',
          align: 'center',
          width: 120,
        },
      ],
    };
    this.search = this.search.bind(this);
    this.getList = this.getList.bind(this);
    this.create = this.create.bind(this);
    this.handleClose = this.handleClose.bind(this);
    // this.transformTime = this.transformTime.bind(this);
  }

  /**
   * 点击改变搜索参数
   */
  search = values => {
    console.log('values', values);
    const accountDateFrom = this.moment(values.accountDateFrom).format();
    const accountDateTo = this.moment(values.accountDateTo).format();
    console.log(accountDateFrom, accountDateTo);
    this.setState(
      {
        searchParams: {
          accountId: values.accountId || '',
          accountDateFrom: accountDateFrom || '',
          accountDateTo: accountDateTo || '',
          otherAccount: values.otherAccount || '',
          amountFrom: values.amount.intervalFrom || '',
          amountTo: values.amount.intervalTo || '',
          direction: values.direction.key || '',
          isGenerate: values.isGenerate.key || '',
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
    // eslint-disable-next-line no-console
    console.log('searchParams', searchParams);
    this.setState({ loading: true });
    accountStatementService
      .getMaintainList(pagination.page, pagination.pageSize, searchParams)
      .then(response => {
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

  // transformTime = date => {
  //   const y = date.getFullYear();
  //   const m = date.getMonth();
  //   const d = date.getDate();
  //   return `${y}-${m + 1}-${d}`;
  // };

  render() {
    const {
      searchForm,
      columns,
      editModel,
      slideVisible,
      pagination,
      loading,
      tableData,
    } = this.state;
    const rowSelection = {
      onChange: (selectedRowKeys, selectedRows) => {
        // eslint-disable-next-line no-console
        console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
      },
      onSelect: (record, selected, selectedRows) => {
        // eslint-disable-next-line no-console
        console.log(record, selected, selectedRows);
      },
      onSelectAll: (selected, selectedRows, changeRows) => {
        // eslint-disable-next-line no-console
        console.log(selected, selectedRows, changeRows);
      },
    };
    return (
      <div className="account">
        <Row>
          <FundSearchForm searchForm={searchForm} maxLength={4} submitHandle={this.search} />
        </Row>
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
        <Button type="primary" style={{ margin: '20px 10px', backgroundColor: 'gray', border: 0 }}>
          删除
        </Button>
        <SlideFrame
          title={editModel.id ? '编辑' : '新增'}
          show={slideVisible}
          onClose={this.handleClose}
        />
        <Table
          scroll={{ x: 1200 }}
          columns={columns}
          rowSelection={rowSelection}
          pagination={pagination}
          loading={loading}
          dataSource={tableData}
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
