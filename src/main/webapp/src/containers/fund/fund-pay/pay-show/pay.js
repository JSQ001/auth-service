import React from 'react';
// import { messages } from 'utils/utils';
// import httpFetch from 'share/httpFetch';
import moment from 'moment';
import Table from 'widget/table';
import { Row, Button, Col, Modal, Form, Input, Alert, message } from 'antd';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import FundSearchForm from '../../fund-components/fund-search-form';
import PayShowService from './pay-show.service';
import 'styles/fund/pay.scss';

// const { TabPane } = Tabs;
const { confirm } = Modal;
const { Search } = Input;

class Pay extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      buttonBankCompany: true, // 银企直联按钮是否点击
      buttonsUnlinePay: true, // 线下支付结果录入按钮是否可点击
      payingButtons: 'none',
      successButtons: 'none',
      unpaidButtons: 'block',
      deleteId: [],
      showButton: 'none', // 显示提示框里面是否查看支付状态的button
      backList: '', // 返回列表
      lookPayStatus: '', // 查看支付状态
      sendSuccess: '', // 发送成功
      info: '', // 提示消息
      status: 'UNPAID', // 默认为未支付状态
      loading: false, // loading状态
      noticeAlert: null, // 提示信息
      selectedRowKeys: '', // 选中的单选ID
      selectedRow: {}, // 单选选中的一行
      documentBackVisible: false, // 退回对话框
      buttonsDisableClick: true, // 操作按钮是否可点击
      isTrue: false, // 银企直联的单据不可点击线下支付
      searchParams: {},
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
      searchForm: [
        {
          colSpan: 6,
          type: 'modalList',
          label: '付款账号',
          id: 'accountNumber',
          listType: 'paymentAccount',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '单据类型',
          id: 'pageType',
          options: [],
          valueListCode: 'ZJ_FORM_TYPE',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '付款方式',
          id: 'payStyle',
          options: [],
          valueListCode: 'ZJ_PAYMENT_TYPE',
        },
        {
          colSpan: 6,
          type: 'input',
          label: '制单人',
          id: 'createdBy',
        },
        {
          colSpan: 6,
          type: 'intervalInput',
          label: '本币金额',
          id: 'coinAmount',
        },
        {
          colSpan: 6,
          type: 'intervalDate',
          id: 'billDate',
          fromlabel: '日期从',
          fromId: 'dateFrom',
          tolabel: '日期到',
          toId: 'dateTo',
        },
      ],
      columns: [
        {
          title: '单据编号',
          dataIndex: 'paymentBatchNumber',
          width: 200,
          align: 'center',
          render: (paymentBatchNumber, record) => (
            <a
              onClick={event => {
                event.stopPropagation();
                this.goDetail(record);
              }}
            >
              {paymentBatchNumber}
            </a>
          ),
        },
        {
          title: '单据类型',
          dataIndex: 'billTypeDesc',
          width: 200,
          align: 'center',
        },
        {
          title: '付款账户',
          dataIndex: 'paymentAccountName',
          width: 100,
          align: 'center',
        },
        {
          title: '付款账号',
          dataIndex: 'paymentAccount',
          width: 200,
          align: 'center',
        },
        {
          title: '付款方式',
          dataIndex: 'paymentMethodDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '笔数',
          dataIndex: 'lineCount',
          width: 100,
          align: 'center',
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: 100,
          align: 'right',
        },
        {
          title: '币种',
          dataIndex: 'currencyCode',
          width: 200,
          align: 'center',
        },
        {
          title: '单据日期',
          dataIndex: 'billDateDesc',
          width: 200,
          align: 'center',
        },
        {
          title: '制单人',
          dataIndex: 'employeeName',
          width: 200,
          align: 'center',
        },
        {
          title: '处理状态',
          dataIndex: 'handleStatusDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '批付款状态',
          dataIndex: 'headStatusDesc',
          width: 150,
          align: 'center',
        },
        {
          title: '日志关联信息',
          dataIndex: '',
          width: 150,
          align: 'center',
        },
      ],
    };
  }

  componentDidMount() {
    const { nowStatus } = this.props;
    // console.log('子组件中的tab状态Status---', nowStatus);
    this.setState(
      {
        status: nowStatus,
      },
      () => {
        this.changeStatus(nowStatus);
        this.getList();
        // console.log('发送请求后的状态nowStatus===', status);
      }
    );
  }

  componentWillReceiveProps(nextProps) {
    // 切换tab页时，父组件传给子组件的nowStatus
    console.log('返回按钮');
    const { nowStatus } = nextProps;
    if (nowStatus === 'UNPAID') {
      this.setState(
        {
          status: 'UNPAID',
          unpaidButtons: 'block',
          payingButtons: 'none',
          successButtons: 'none',
        },
        () => {
          this.getList();
        }
      );
    } else if (nowStatus === 'BEING') {
      this.setState(
        {
          status: 'BEING',
          payingButtons: 'block',
          successButtons: 'none',
          unpaidButtons: 'none',
        },
        () => {
          this.getList();
        }
      );
    } else {
      this.setState(
        {
          status: 'SUCCESS',
          successButtons: 'block',
          unpaidButtons: 'none',
          payingButtons: 'none',
        },
        () => {
          this.getList();
        }
      );
    }
  }

  /**
   * 从明细页面返回后的tab状态不变
   */
  changeStatus = nowStatus => {
    // console.log('noeStatus', nowStatus);
    if (nowStatus === 'UNPAID') {
      this.setState({
        unpaidButtons: 'block',
        payingButtons: 'none',
        successButtons: 'none',
      });
    } else if (nowStatus === 'BEING') {
      this.setState({
        payingButtons: 'block',
        successButtons: 'none',
        unpaidButtons: 'none',
      });
    } else {
      this.setState({
        successButtons: 'block',
        unpaidButtons: 'none',
        payingButtons: 'none',
      });
    }
  };

  /**
   * 跳转付款单明细详情
   */
  goDetail = record => {
    const { status } = this.state;
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/fund-pay/pay-show/manual-query/${record.id}/${
          record.paymentBatchNumber
        }/${status}`,
      })
    );
  };

  /**
   * 获取列表数据
   */
  getList = () => {
    const { pagination, searchParams, status } = this.state;
    this.setState({ loading: true });
    PayShowService.getPayList(pagination.page, pagination.pageSize, searchParams, status).then(
      response => {
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
            pageSize: pagination.pageSize,
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
   * 行选择
   */
  onSelectChange = (selectedRowKeys, selectedRow) => {
    // 银企直联结果更新按钮是否显示
    if (selectedRow[0] && selectedRow[0].paymentMethodDesc === '银企直连') {
      this.setState(
        {
          selectedRowKeys,
          selectedRow,
          buttonBankCompany: !(selectedRowKeys.length > 0),
        },
        () => {
          this.setState({
            selectedRowKeys: selectedRowKeys[0], // 11...789
            selectedRow,
            // selectedRow: selectedRow,
          });
        }
      );
    }
    // 线下支付结果录入是否显示
    if (selectedRow[0] && selectedRow[0].paymentMethodDesc !== '银企直连') {
      this.setState({
        selectedRowKeys,
        selectedRow,
        buttonsUnlinePay: !(selectedRowKeys.length > 0),
      });
    }
  };

  /**
   * 展示删除弹框
   */
  showDeleteConfirm = () => {
    const aThis = this;
    confirm({
      title: '您确定删除所选择的单据，是否继续?',
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        aThis.deleteItems();
      },
    });
  };

  /**
   * 删除
   */
  deleteItems = () => {
    const { selectedRowKeys, deleteId } = this.state;
    deleteId.push(selectedRowKeys);
    if (deleteId[0] !== '') {
      PayShowService.deleteAccount(deleteId).then(res => {
        if (res.status === 200) {
          message.success('删除成功！');
          this.getList();
          this.setState({
            selectedRowKeys: '',
            deleteId: [],
          });
        }
      });
    } else {
      message.error('请选中要删除的单据！！');
    }
  };

  /**
   * 发送银行
   */
  sendBank = () => {
    const { selectedRowKeys, selectedRow } = this.state;
    if (selectedRowKeys !== '') {
      if (selectedRow[0] && selectedRow[0].paymentMethod === 'INTERFACE') {
        PayShowService.sendBank(selectedRowKeys)
          .then(() => {
            this.setState({
              sendSuccess: '发送成功',
              info:
                '单据已发送银行处理，30分钟后系统将自动从银行获取支付结果，您也可以进入支付中界面更新支付结果！',
              backList: '返回列表',
              showButton: 'inline',
              lookPayStatus: '查看支付状态',
              documentBackVisible: true,
            });
            message.success('请求成功！！！');
            this.getList();
          })
          .catch(() => {
            message.error('请求失败！！！');
          });
      } else {
        this.setState({
          sendSuccess: '发送失败',
          info: '请核对并修改以下信息后，再重新提交！',
          backList: '返回列表',
          showButton: 'none',
          documentBackVisible: true,
        });
      }
    } else {
      message.error('请选中需要发送银行请求的单据！！');
    }
  };

  /**
   * 返回列表
   */
  goList = () => {
    this.setState({
      documentBackVisible: false,
    });
    this.getList();
  };

  /**
   * 线下付款
   */
  sendUnlinePay = () => {
    const { selectedRowKeys } = this.state;
    if (selectedRowKeys !== '') {
      PayShowService.unlinePay(selectedRowKeys)
        .then(() => {
          this.setState({
            sendSuccess: '处理成功',
            info: '单据付款状态已经变更，请您进行线下付款，取回付款结果后，记得更新状态哦！！',
            backList: '返回列表',
            showButton: 'none',
            documentBackVisible: true,
          });
          message.success('请求成功！！！');
          // this.getList({status: 'UNPAID'});
          // this.getList({status: 'SUCCESS'});
        })
        .catch(() => {
          this.setState({
            sendSuccess: '发送失败',
            info: '请核对并修改以下信息后，再重新提交！',
            backList: '返回列表',
            showButton: 'none',
            documentBackVisible: true,
          });
          message.error('请求失败！！！');
        });
    } else {
      message.error('请选中需要线下付款请求的单据！！');
    }
  };

  /**
   * 选中checkbox后的提示框显示
   */
  noticeAlert = rows => {
    const initialAmount = 0;
    const totalAmount = rows.reduce(
      (accumulator, currentValue) => accumulator + currentValue.amount,
      initialAmount
    );
    const noticeAlert = (
      <span>
        已选择
        <span style={{ fontWeight: 'bold', color: '#108EE9' }}> {rows.length} </span> 项 |共
        <span style={{ fontWeight: 'bold', color: '#108EE9' }}>{totalAmount}</span>元
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
    if (selectedRow[0].paymentMethod === 'INTERFACE') {
      this.setState({
        isTrue: true,
        buttonBankCompany: false,
        buttonsUnlinePay: true,
      });
    } else {
      this.setState({
        isTrue: false,
        buttonBankCompany: true,
        buttonsUnlinePay: false,
      });
    }
    this.setState(
      {
        selectedRowKeys,
        selectedRow,
        buttonsDisableClick: !(selectedRowKeys.length > 0),
      },
      () => {
        // 获取当前行的id，发送银行请求、线下付款请求
        this.setState({
          selectedRowKeys: selectedRowKeys[0], // 11...789
          selectedRow,
        });
      }
    );
  };

  /**
   * 支付中-->发送银企直联请求
   */
  sendBankCompany = () => {
    const { selectedRowKeys, selectedRow } = this.state;
    if (selectedRow[0] && selectedRow[0].paymentMethod === 'INTERFACE') {
      PayShowService.sendBankCompany(selectedRowKeys)
        .then(() => {
          message.success('请求成功！！！');
        })
        .catch(() => {
          message.error('请求失败！！！');
        });
    }
  };

  /**
   * 支付中-->线下支付结果录入跳转到付款详细页面
   */
  payingSendUnlinePay = () => {
    const { selectedRowKeys, selectedRow, status } = this.state;
    const { dispatch } = this.props;
    if (selectedRowKeys !== '') {
      dispatch(
        routerRedux.push({
          pathname: `/fund-pay/pay-show/manual-query/${selectedRow[0].id}/${
            selectedRow[0].paymentBatchNumber
          }/${status}`,
        })
      );
    } else {
      message.error('请选中要操作的单据！！');
    }
  };

  /**
   * 更多展示中的搜索
   */
  handleSearch = values => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          billType: values.pageType ? values.pageType.key : '', // 单据类型
          paymentAccount: values.accountNumber ? values.accountNumber : '', // 付款账号
          paymentMethod: values.payStyle ? values.payStyle.key : '', // 付款方式
          employeeName: values.createdBy || '', // 制单人
          amountFrom: values.coinAmount.intervalFrom || '', // 金额区间
          amountTo: values.coinAmount.intervalTo || '',
          billDateFrom: values.dateFrom // 单据日期
            ? moment(values.dateFrom)
                .format()
                .slice(0, 10)
            : '',
          billDateTo: values.dateTo
            ? moment(values.dateTo)
                .format()
                .slice(0, 10)
            : '',
        },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 根据单据编号搜索
   */
  searchNumber = value => {
    let { searchParams } = this.state;
    searchParams = {};
    this.setState(
      {
        searchParams: { ...searchParams, paymentBatchNumber: value },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 弹框取消
   */
  handleCancel = () => {
    this.setState({
      documentBackVisible: false,
    });
  };

  render() {
    const {
      buttonsUnlinePay,
      buttonBankCompany,
      unpaidButtons,
      payingButtons,
      successButtons,
      showButton,
      lookPayStatus,
      backList,
      info,
      sendSuccess,
      columns,
      searchForm,
      noticeAlert,
      tableData,
      selectedRowKeys,
      selectedRow,
      loading,
      pagination,
      buttonsDisableClick,
      documentBackVisible,
      isTrue,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      type: 'radio',
      onChange: this.onSelectChange,
    };
    return (
      <div className="pay-show">
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
          <div className="table-header-buttons" style={{ display: unpaidButtons }}>
            <Row>
              <Col span={8}>
                <Button type="primary" onClick={this.sendBank}>
                  发送银行
                </Button>
                <Button type="primary" onClick={this.sendUnlinePay} disabled={isTrue}>
                  线下付款
                </Button>
                <Button
                  type="danger"
                  disabled={buttonsDisableClick}
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.showDeleteConfirm();
                    this.setState({
                      //   documentBackVisible: true,
                    });
                  }}
                >
                  删除
                </Button>
              </Col>
              <Col span={6} offset={10}>
                <Search placeholder="请输入单据编号" enterButton onSearch={this.searchNumber} />
              </Col>
            </Row>
          </div>
          <div className="table-header-buttons" style={{ display: payingButtons }}>
            <Row>
              <Col span={8}>
                <Button type="primary" disabled={buttonBankCompany} onClick={this.sendBankCompany}>
                  银企直联结果更新
                </Button>
                <Button
                  type="primary"
                  disabled={buttonsUnlinePay}
                  onClick={this.payingSendUnlinePay}
                >
                  线下支付结果录入
                </Button>
              </Col>
              <Col span={6} offset={10}>
                <Search placeholder="请输入单据编号" enterButton onSearch={this.searchNumber} />
              </Col>
            </Row>
          </div>
          <div className="table-header-buttons" style={{ display: successButtons }}>
            <Row>
              <Col span={6} offset={18}>
                <Search placeholder="请输入单据编号" enterButton onSearch={this.searchNumber} />
              </Col>
            </Row>
          </div>
          {noticeAlert ? (
            <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
          ) : (
            ''
          )}
          <Table
            rowKey={record => record.id}
            dataSource={tableData}
            loading={loading}
            columns={columns}
            pagination={pagination}
            scroll={{ x: 1500 }}
            rowSelection={rowSelection}
            onChange={this.onChangePager}
          />
          <Modal
            style={{ backgroundColor: 'gray' }}
            title=""
            closable="true"
            footer={null}
            visible={documentBackVisible}
          >
            <h1 style={{ textAlign: 'center' }}>{sendSuccess}</h1>
            <p style={{ width: '80%', textAlign: 'center' }}>{info}</p>
            <div style={{ width: '300px', marginLeft: '10%' }}>
              <Button
                onClick={this.goList}
                style={{
                  marginLeft: '50%',
                  marginRight: '15px',
                  textAlign: 'center',
                  color: 'white',
                  backgroundColor: 'lightBlue',
                }}
              >
                {backList}
              </Button>
              <Button style={{ display: showButton }}>{lookPayStatus}</Button>
            </div>
          </Modal>
        </div>
      </div>
    );
  }
}

const wrappedCompanyDistribution = Form.create()(Pay);

export default connect()(wrappedCompanyDistribution);
