import React from 'react';
import { Row, Form, Col, Card, Table, Icon, Badge, Alert, Button, Input, message } from 'antd';
import SlideFrame from 'widget/slide-frame';
import { connect } from 'dva';
import { messages } from 'utils/utils';
import { routerRedux } from 'dva/router';
import DistributionAdd from './distribution-add';

import distributionService from './distribution.service';

const { Search } = Input;

class CapitalTransactionReport extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      noticeAlert: null, // 提示信息
      tableData: [], // 数据列表
      buttonLoading: false, // 按钮loading状态
      batchDelete: true, // 批量删除标志
      loading: false,
      slideVisible: false,
      isGatherRate1: false, //
      isautoshow1: false,
      isNew: true,
      isNew2: true,
      ationRateValue: '',
      ationRateValue1: false,
      editModel: {}, // 点击行时的编辑数据
      handMsg: {},
      searchParams: {},
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      columns: [
        {
          title: '子账号',
          dataIndex: 'bankAccount',
          width: 100,
          align: 'center',
        },
        {
          title: '子账户',
          dataIndex: 'bankAccountName',
          width: 100,
          align: 'center',
        },
        {
          title: '所属机构',
          dataIndex: 'companyName',
          width: 100,
          align: 'center',
        },
        {
          title: '所属银行',
          dataIndex: 'bankName',
          width: 100,
          align: 'center',
        },
        {
          title: '归集方式',
          dataIndex: 'gatherTypeDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: 100,
          align: 'center',
        },
        {
          title: '比例',
          dataIndex: 'gatherRate',
          width: 100,
          align: 'center',
        },
        {
          title: '归集频率',
          dataIndex: 'gatherFrequencyDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '归集时间',
          dataIndex: 'gatherTime',
          width: 100,
          align: 'center',
        },
        {
          title: '开始时间',
          dataIndex: 'startDateDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '最后修改人',
          dataIndex: 'lastUpdateName',
          width: 100,
          align: 'center',
        },
        {
          title: '编辑',
          dataIndex: 'tradeCode',
          width: 100,
          align: 'center',
          render: () => <a>编辑</a>,
        },
      ],
    };
  }

  componentWillMount() {
    const { match } = this.props;
    this.setState({
      headId: match.params.id,
    });
    this.getList(match.params.id);
    this.getHeader(match.params.id);
  }

  /**
   * 获取头展示数据
   */
  getHeader = id => {
    const { pagination } = this.state;
    this.setState({ loading: true });
    distributionService
      .getDistributionHeader(pagination.page, pagination.pageSize, id)
      .then(response => {
        const { data } = response;
        console.log('head message');
        console.log(data[0]);
        this.setState({
          handMsg: data[0],
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
   * 获取列表数据
   */
  getList = id => {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true });
    distributionService
      .getDistributionList(pagination.page, pagination.pageSize, id, searchParams)
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
          searchParams: {},
        });
      });
  };

  /**
   * 新建
   */
  handleCreateClick = () => {
    this.setState({
      slideVisible: true,
    });
  };

  /**
   * 删除
   */
  showDeleteConfirm = () => {
    const { selectedRowKeys } = this.state;
    const { match } = this.props;
    console.log('selectedRowKeys');
    console.log(selectedRowKeys);
    distributionService
      .deleteDistributionList(selectedRowKeys)
      .then(res => {
        if (res.status === 200) {
          message.success('删除成功！');
          this.getList(match.params.id);
          this.setState({
            noticeAlert: null,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 点击行编辑
   */
  handleRowClick = record => {
    let record1 = record;
    console.log('record1');
    console.log(record1);

    record1 = {
      ...record1,
      /* eslint-disable */
      gatherTime: '2019-04-08T' + record.gatherTime + ':00+08:00',
      /* eslint-enable */
    };
    this.setState({ editModel: record1, slideVisible: true, isNew: false, isNew2: false });
    if (record.gatherTypeDesc === '固定余额') {
      console.log('1211111111111111');
      this.setState({
        isGatherRate1: false,
        isautoshow1: false,
      });
    }
    if (record.gatherTypeDesc === '固定比例') {
      console.log('sssyy');
      this.setState({
        isGatherRate1: true,
        isautoshow1: true,
      });
    }
    if (record.gatherTypeDesc === '固定金额') {
      this.setState({
        isGatherRate1: false,
        isautoshow1: true,
      });
    }
    if (record.gatherFrequency === 'EVERYDAY') {
      this.setState({
        ationRateValue: 'EVERYDAY',
      });
    }
    if (record.gatherFrequency === 'EVERYMONTH') {
      this.setState({
        ationRateValue: 'EVERYMONTH',
      });
    }
    if (record.gatherFrequency === 'EVERYWEEK') {
      this.setState({
        ationRateValue: 'EVERYWEEK',
      });
    }
    if (record.gatherFrequency === 'TRADING_DAY') {
      this.setState({
        ationRateValue: 'TRADING_DAY',
      });
    }
    if (record.gatherFrequency === 'WORKING_DAY') {
      this.setState({
        ationRateValue: 'WORKING_DAY',
      });
    }
    if (record.gatherFrequency) {
      console.log('come in');
      this.setState({
        ationRateValue1: true,
      });
    }
  };

  /**
   * 关闭
   */
  handleClose = () => {
    const { match } = this.props;
    this.setState({
      slideVisible: false,
      editModel: {},
    });
    this.getList(match.params.id);
  };

  /**
   * 数据列表分页点击
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

  // 子账号搜索
  searchNumber = value => {
    const { match } = this.props;
    // let { searchParams } = this.state;
    // searchParams = {};
    this.setState(
      {
        searchParams: { bankAccount: value },
      },
      () => {
        this.getList(match.params.id);
      }
    );
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

  onNew = () => {
    this.setState({
      isNew: true,
    });
  };

  onNew2 = () => {
    this.setState({
      isNew2: true,
    });
  };

  /**
   * 行选择
   */
  onSelectChange = (selectedRowKeys, selectedRow) => {
    this.setState(
      {
        selectedRowKeys,
        selectedRow,
        // batchDelete: !(selectedRowKeys.length > 0),
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
   * 返回
   */
  handleBack = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/transfer-management/account-transfer-relationship-setting/account-transfer-relationship-setting/`,
      })
    );
  };

  render() {
    const {
      noticeAlert,
      tableData,
      columns,
      pagination,
      loading,
      selectedRow,
      selectedRowKeys,
      batchDelete,
      buttonLoading,
      slideVisible,
      editModel,
      headId,
      isGatherRate1,
      isautoshow1,
      isNew,
      isNew2,
      ationRateValue,
      ationRateValue1,
      handMsg,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div>
        <Card
          style={{
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
            marginRight: 15,
            marginLeft: 15,
          }}
        >
          <div style={{ borderBottom: '1px solid rgb(236, 236, 236)', marginTop: '-20px' }}>
            <h3>明细信息:</h3>
          </div>
          <Row style={{ marginTop: '15px' }}>
            <Col span={7}>母账号：{handMsg.bankAccount}</Col>
            <Col span={6}>母账户：{handMsg.bankAccountName}</Col>
            <Col span={6}>所属银行：{handMsg.bankName}</Col>
            <Col span={5}>所属公司：{handMsg.companyName}</Col>
          </Row>
          <Row style={{ marginTop: '15px' }}>
            {/* <Col span={7}>状态：{handMsg.enabled}</Col> */}
            <Col span={6}>
              状态：
              <Badge
                status={handMsg.enabled ? 'success' : 'error'}
                text={
                  handMsg.enabled
                    ? messages('common.status.enable')
                    : messages('common.status.disable')
                }
              />
            </Col>
          </Row>
        </Card>
        <div>
          {/* </Row> */}
          <div
            className="table-header-buttons"
            style={{ marginTop: '20px', paddingBottom: '10px' }}
          >
            <Row>
              <Col span={8}>
                {/* 新建 */}
                <Button type="primary" onClick={this.handleCreateClick}>
                  {this.$t('common.create')}
                </Button>
                {/* 删除 */}
                <Button
                  style={{ marginLeft: '10px' }}
                  type="danger"
                  disabled={batchDelete}
                  loading={buttonLoading}
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.showDeleteConfirm();
                  }}
                >
                  {this.$t('common.delete')}
                </Button>
              </Col>
              <Col span={6} offset={10}>
                <Search placeholder="请输入账号" enterButton onSearch={this.searchNumber} />
              </Col>
            </Row>
          </div>
          {/* 侧边栏 */}
          <SlideFrame
            title={editModel.id ? '子账号分配编辑' : '子账号分配新建'}
            show={slideVisible}
            onClose={() => this.handleClose()}
          >
            <DistributionAdd
              onClose={this.handleClose}
              ationRateValue2={ationRateValue}
              ationRateValue3={ationRateValue1}
              onNew={this.onNew}
              onNew2={this.onNew2}
              isNew2={isNew2}
              params={editModel}
              isNew={isNew}
              addId={headId}
              isGatherRate1={isGatherRate1}
              isautoshow1={isautoshow1}
            />
          </SlideFrame>
          {/* 提示信息 */}
          {noticeAlert ? (
            <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
          ) : (
            ''
          )}
          {/* 数据列表 */}
          <Table
            rowKey={record => record.id}
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            rowSelection={rowSelection}
            loading={loading}
            onChange={this.onChangePager}
            onRowClick={this.handleRowClick}
            bordered
            size="middle"
            scroll={{ x: 1500 }}
          />
          <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleBack}>
            <Icon type="rollback" style={{ marginRight: '5px' }} />返回
          </a>
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

export default connect(mapStateToProps)(Form.create()(CapitalTransactionReport));
