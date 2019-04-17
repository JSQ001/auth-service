import React from 'react';
import Table from 'widget/table';
import { connect } from 'dva';

import { Row, Col, DatePicker, Button, Form, message } from 'antd';
import moment from 'moment';
import accountBalanceService from './account-balance-service';

class AccountHistoryBalance extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      columns: [
        {
          title: '日期',
          dataIndex: 'lastUpdate',
          width: 100,
          align: 'center',
        },
        {
          title: '余额',
          dataIndex: 'balanceAmount',
          width: 100,
          align: 'center',
          render: this.filterMoney,
        },
      ],
      startDate: moment(new Date().setDate(1)).format('YYYY-MM-DD'),
      endDate: moment(new Date().format('YYYY-MM-DD')),
      loading: false,
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
    };
  }

  componentDidMount() {
    this.getAccountBalanceHistory();
  }

  search = () => {
    this.getAccountBalanceHistory();
  };

  clearSearch = () => {
    const { form } = this.props;
    form.resetFields();
  };

  getAccountBalanceHistory = () => {
    const { accountHistoryBalance } = this.props;
    const { startDate, endDate, pagination } = this.state;
    const { accountId } = accountHistoryBalance;
    this.setState({
      loading: true,
    });
    accountBalanceService
      .getAccountBalanceHistory(accountId, startDate, endDate)
      .then(response => {
        this.setState({
          tableData: response.data,
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
        this.getAccountBalanceHistory();
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
        this.getAccountBalanceHistory();
      }
    );
  };

  render() {
    const {
      accountHistoryBalance,
      form: { getFieldDecorator },
    } = this.props;
    const { columns, tableData, loading, pagination } = this.state;
    return (
      <div>
        <Row style={{ marginBottom: '10px' }}>
          <Col span={6}>银行账户：{accountHistoryBalance.accountNumber}</Col>
          <Col span={6}>账户户名：{accountHistoryBalance.accountName}</Col>
          <Col span={6}>所属公司：{accountHistoryBalance.companyName} </Col>
          <Col span={6}>币种：人民币</Col>
        </Row>
        <Row style={{ paddingTop: '20px', marginBottom: '20px', borderTop: '1px solid #e9e9e9' }}>
          <Col span={2} style={{ paddingTop: '10px' }}>
            {' '}
            <span> 余额期间：</span>
          </Col>
          <Col span={10}>
            <Form>
              <Col span={8}>
                <Form.Item>
                  {getFieldDecorator('startDate', {
                    initialValue: moment(new Date().setDate(1)),
                  })(
                    <DatePicker
                      onChange={value => {
                        this.setState({
                          startDate: moment(value).format('YYYY-MM-DD'),
                        });
                      }}
                    />
                  )}
                </Form.Item>
              </Col>

              <Col span={8} offset={1}>
                <Form.Item>
                  {getFieldDecorator('endDate', {
                    initialValue: moment(new Date()),
                  })(
                    <DatePicker
                      onChange={value => {
                        this.setState({
                          endDate: moment(value).format('YYYY-MM-DD'),
                        });
                      }}
                    />
                  )}
                </Form.Item>
              </Col>
            </Form>
          </Col>
          <Col span={10}>
            <Col span={8}>
              <Button type="primary" onClick={this.search}>
                查询
              </Button>
            </Col>
            <Col span={4}>
              <Button type="primary" onClick={this.clearSearch}>
                重置
              </Button>
            </Col>
          </Col>
        </Row>
        <Table
          dataSource={tableData}
          columns={columns}
          loading={loading}
          pagination={pagination}
          onChange={this.onChangePager}
          bordered
          size="middle"
        />
      </div>
    );
  }
}
// export default AccountHistoryBalance;

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(Form.create()(AccountHistoryBalance));
