import React, { Component } from 'react';
import { connect } from 'dva';
// import SearchArea from "widget/search-area"
import { Button, message, Row, Alert, Table } from 'antd';
import SlideFrame from 'widget/slide-frame';

// import Lov from 'widget/Template/lov'
// import config from 'config'
// import CustomTable from 'widget/custom-table';
import FundSearchForm from '../../fund-components/fund-search-form';
import BalanceMaintainDetail from './balance-maintain-detail';
import balanceMaintainService from './cp-balance-maintain.service';

class CpBalanceMaintain extends Component {
  constructor(props) {
    super(props);
    this.state = {
      editModel: {}, // 点击行时的编辑数据
      loading: false, // loading状态
      noticeAlert: null, // 提示信息
      searchParams: {}, // 查询条件
      slideVisible: false, // 侧边栏显示
      selectedRow: [],
      tableData: [],
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
      searchForm: [
        // 公司
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
          type: 'modalList',
          label: '银行账号',
          id: 'paymentAccount',
          listType: 'paymentAccount',
        },
        // 所属银行
        // {
        //   colSpan: 6,
        //   type: 'value_list',
        //   label: '所属银行',
        //   id: 'openBank',
        //   options: [],
        //   valueListCode: 'ZJ_OPEN_BANK',
        // },
        {
          colSpan: 6,
          type: 'valueList',
          label: '所属银行',
          id: 'openBank',
          options: [],
          valueListCode: 'ZJ_OPEN_BANK',
        },
        // 期间
        {
          colSpan: 6,
          type: 'value_list',
          label: '状态',
          id: 'approveStatus',
          options: [],
          valueListCode: 'ZJ_BILL_STATUS',
        },
      ],
      columns: [
        {
          title: '银行账号',
          dataIndex: 'bankAccount',
          align: 'center',
        },
        {
          title: '银行户名',
          dataIndex: 'bankAccountName',
          align: 'center',
        },
        {
          title: '所属银行',
          dataIndex: 'gatherBankName',
          align: 'center',
        },
        {
          title: '期间',
          dataIndex: 'period',
          align: 'center',
        },
        {
          title: '币种',
          dataIndex: 'currency',
          align: 'center',
        },
        {
          title: '实际银行期初余额',
          dataIndex: 'periodInitAmountActual',
          align: 'center',
        },
        {
          title: '复核状态',
          dataIndex: 'reviewStatusDesc',
          align: 'center',
        },
      ],
      //   visibel: false,
      //   model: {},
    };
  }

  componentWillMount() {
    this.getList();
  }

  // 搜索
  search = values => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          gatherBank: values.openBank ? values.openBank.key : '', // 所属银行
          bankAccount: values.paymentAccount ? values.paymentAccount : '', // 付款账号
          companyId: values.documentCompany ? values.documentCompany[0].id : '', // 单据公司
        },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 保存
   */
  save = params => {
    balanceMaintainService
      .createOrUpdateList(params)
      .then(res => {
        if (res.status === 200) {
          message.success('保存成功！');
          // this.onFlash(res.data.id);
          this.setState({ slideVisible: false });
          this.getList();
        }
      })
      .catch(error => {
        message.error(error.errorCode);
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
   * 复核
   */
  review = () => {
    const { selectedRowKeys } = this.state;
    balanceMaintainService
      .maintainReview(selectedRowKeys)
      .then(res => {
        if (res.status === 200) {
          message.success('复核成功！');
          this.getList();
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 取消复核
   */
  cancelReview = () => {
    const { selectedRowKeys } = this.state;
    balanceMaintainService
      .maintainPassreview(selectedRowKeys)
      .then(res => {
        if (res.status === 200) {
          message.success('取消复核成功！');
          this.getList();
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

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

  /**
   * 新建
   */
  create = () => {
    this.setState({ slideVisible: true });
  };

  /**
   * 关闭侧边栏
   */
  handleClose = value => {
    this.setState({
      slideVisible: false,
      editModel: {},
    });
    if (value === 'save') {
      this.getList();
    }
  };

  // 获取列表
  getList = () => {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true });
    balanceMaintainService
      .getMaintainList(pagination.page, pagination.pageSize, searchParams)
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
          },
        });
      });
  };

  // 清除搜索条件
  empty = () => {
    this.search({});
  };

  render() {
    const {
      searchForm,
      columns,
      slideVisible,
      editModel,
      noticeAlert,
      selectedRowKeys,
      selectedRow,
      tableData,
      pagination,
      loading,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div>
        {/* <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          clearHandle={this.empty}
        /> */}
        <Row>
          <FundSearchForm searchForm={searchForm} submitHandle={this.search} />
        </Row>
        {/* 侧边栏 */}
        <SlideFrame
          title={editModel.id ? '编辑' : '新建'}
          show={slideVisible}
          onClose={this.handleClose}
        >
          <BalanceMaintainDetail onClose={this.handleClose} params={editModel} save={this.save} />
        </SlideFrame>
        <div className="table-header-buttons">
          <Button
            style={{ margin: '20px 10px' }}
            className="create-btn"
            type="primary"
            onClick={this.create}
          >
            新建
          </Button>
          {/* 复核 */}
          <Button onClick={this.review} style={{ margin: '20px 10px' }} type="primary">
            复核
          </Button>
          {/* 取消复核 */}
          <Button style={{ margin: '20px 10px' }} type="primary" onClick={this.cancelReview}>
            取消复核
          </Button>
        </div>
        {noticeAlert ? (
          <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
        ) : (
          ''
        )}
        <Table
          onRow={record => {
            return {
              onClick: () => {
                this.setState({ editModel: record, slideVisible: true });
              },
            };
          }}
          rowKey={record => record.id}
          pagination={pagination}
          columns={columns}
          dataSource={tableData}
          loading={loading}
          rowSelection={rowSelection}
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

export default connect(mapStateToProps)(CpBalanceMaintain);
