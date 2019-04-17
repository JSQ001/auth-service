import React from 'react';
import { Row, Form, Col, Card, Table, Icon } from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import PaymentMaintenanceService from './fund-transfer-slip.service';

class PaymentQueryDetails extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      priority: '',
      tableData: [], // 数据列表
      loading: false,
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
      columns: [
        {
          title: '序号',
          dataIndex: 'priority',
          width: 50,
          render: (text, record, index) => `${index + 1}`,
        },

        {
          title: '调入公司',
          dataIndex: 'adjustInCorpDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '调入账号',
          dataIndex: 'adjustInAccount',
          width: 150,
          align: 'center',
        },
        {
          title: '调入银行',
          dataIndex: 'adjustInOpenBank',
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
          title: '币种',
          dataIndex: 'currency',
          width: 100,
          align: 'center',
        },
        {
          title: '摘要',
          dataIndex: 'description',
          width: 100,
          align: 'center',
        },
        {
          title: '调拨申请单号',
          dataIndex: 'adjustBatchNumber',
          width: 100,
          align: 'center',
        },
        {
          title: '付款用途',
          dataIndex: 'paymentUseCode',
          width: 100,
          align: 'center',
        },
        {
          title: '支付状态',
          dataIndex: 'paymentStatus',
          width: 100,
          align: 'center',
        },
        {
          title: '银行反馈信息',
          dataIndex: 'description',
          width: 100,
          align: 'center',
        },
        {
          title: '支付出纳',
          dataIndex: 'description',
          width: 100,
          align: 'center',
        },
        {
          title: '支付日期',
          dataIndex: 'paymentTime',
          width: 100,
          align: 'center',
        },
        {
          title: '回单编号',
          dataIndex: 'description',
          width: 100,
          align: 'center',
        },
      ],
    };
  }

  componentDidMount() {
    const { match } = this.props;
    if (match.params.id) {
      this.setState({
        baseId: match.params.id,
      });
      this.getList(match.params.id);
      this.getUserList(match.params.paymentBatchNumber);
    }
  }

  /**
   * 数据列表
   */

  getList = id => {
    const { pagination } = this.state;
    this.setState({ loading: true });
    PaymentMaintenanceService.getManualList(pagination.page, pagination.pageSize, id).then(
      response => {
        const { data } = response;
        this.setState({
          tableData: data.adjustLineInfos,
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
      }
    );
  };

  /**
   * 根据头id获取数据
   * @param {*} number
   */
  getUserList(number) {
    const { pagination } = this.state;
    const batchNumber = { paymentBatchNumber: number };
    PaymentMaintenanceService.getCpAdjustShow(
      pagination.page,
      pagination.pageSize,
      batchNumber
    ).then(response => {
      const { data } = response;
      this.setState({
        adjustBatchNumber: data[0].adjustBatchNumber || '', // 单据编号
        belongCorpName: data[0].belongCorpName || '', // 付款公司
        belongDeptName: data[0].belongDeptName || '', // 所属部门
        paymentMethodName: data[0].paymentMethodName || '', // 付款方式
        adjustOutAccountId: data[0].adjustOutAccountId || '', // 调出账号
        adjustInAccountName: data[0].adjustOutAccountName || '', // 调出账户
        billDateFormat: data[0].billDateFormat || '', // 单据日期
        employeeName: data[0].employeeName || '', // 制单人
        description: data[0].description, // 备注
      });
    });
  }

  /**
   * 分页点击
   */
  onChangePagerBank = pagination => {
    const temp = {};
    const { baseId } = this.state;
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList(baseId);
      }
    );
  };

  /**
   * 返回上一页
   */
  handleBack = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: '/transfer-management/fund-transfer-slip/fund-transfer-slip',
      })
    );
  };

  render() {
    const {
      loading,
      tableData,
      pagination,
      columns,
      adjustBatchNumber,
      belongCorpName,
      belongDeptName,
      paymentMethodName,
      adjustInAccountName,
      adjustOutAccountId,
      billDateFormat,
      description,
      priority,
      employeeName,
    } = this.state;
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
            <Col span={7}>单据编号:{adjustBatchNumber || ''}</Col>
            <Col span={6}>所属公司：{belongCorpName || ''}</Col>
            <Col span={6}>所属部门：{belongDeptName || ''}</Col>
            <Col span={5}>付款方式：{paymentMethodName || ''}</Col>
          </Row>
          <Row style={{ marginTop: '15px' }}>
            <Col span={7}>调出账号：{adjustOutAccountId || ''}</Col>
            <Col span={6}>调出账户：{adjustInAccountName || ''}</Col>
            <Col span={6}>单据日期：{billDateFormat || ''}</Col>
            <Col span={5}>制单人：{employeeName || ''}</Col>
          </Row>
          <Row style={{ marginTop: '15px' }}>
            <Col span={5}>描述：{description || ''}</Col>
          </Row>
        </Card>
        <div style={{ marginTop: '30px', paddingBottom: '15px' }}>
          <h3>跟踪单据：</h3>
          <Table
            rowKey={record => record.id}
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            loading={loading}
            onChange={this.onChangePagerBank}
            priority={priority}
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

function show(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(show)(Form.create()(PaymentQueryDetails));
