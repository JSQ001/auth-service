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
      showButtonOne: 'none', // 其余发送请求时---显示一个button
      showButtonTwo: 'none', // 发送银行---发送成功是显示两个buttons
      showErrorInfo: 'none', // 显示发送失败时的提示消息的div
      error1: '', // 发送失败时的提示消息1
      error2: '', // 发送失败时的提示消息2
      imgSuccess: '', // img图标
      imgDelete: '', // delete的img图标
      showClose: false, // modal的关闭图标
      buttonBankCompany: true, // 银企直联按钮是否点击
      buttonsUnlinePay: true, // 线下支付结果录入按钮是否可点击
      payingButtons: 'none', // 支付中的按钮显示
      successButtons: 'none', // 已支付的按钮显示
      unpaidButtons: 'block', // 未支付的按钮显示
      deleteId: [],
      backList: '', // 返回列表
      lookPayStatus: '', // 查看支付状态
      sendSuccess: '', // 发送成功
      info: '', // 提示消息
      status: 'UNPAID', // 页面初始化默认为未支付状态
      loading: false, // loading状态
      noticeAlert: null, // 提示信息
      selectedRowKeys: '', // 选中的单选ID
      selectedRow: {}, // 单选选中的一行
      documentBackVisible: false, // 退回对话框
      buttonsDisableClick: true, // 操作按钮是否可点击
      isTrue: false, // 银企直联的单据不可点击线下支付
      searchParams: {}, // 模糊查询搜索参数
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
          label: this.$t('fund.payment.account') /* 付款账号 */,
          id: 'accountNumber',
          listType: 'paymentAccount',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: this.$t('fund.type.of.document') /* 单据类型 */,
          id: 'pageType',
          options: [],
          valueListCode: 'ZJ_FORM_TYPE',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: this.$t('fund.payment.method') /* 付款方式 */,
          id: 'payStyle',
          options: [],
          valueListCode: 'ZJ_PAYMENT_TYPE',
        },
        {
          colSpan: 6,
          type: 'input',
          label: this.$t('fund.single.person') /* 制单人 */,
          id: 'createdBy',
        },
        {
          colSpan: 6,
          type: 'intervalInput',
          label: this.$t('fund.local.currency.amount') /* 本币金额 */,
          id: 'coinAmount',
        },
        {
          colSpan: 6,
          type: 'intervalDate',
          id: 'billDate',
          fromlabel: this.$t('fund.date.from') /* 日期从 */,
          fromId: 'dateFrom',
          tolabel: this.$t('fund.the.date.to') /* 日期到 */,
          toId: 'dateTo',
        },
      ],
      columns: [
        {
          title: this.$t('fund.receipt.number') /* 单据编号 */,
          dataIndex: 'paymentBatchNumber',
          width: 200,
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
          title: this.$t('fund.type.of.document') /* 据类型 */,
          dataIndex: 'billTypeDesc',
          width: 150,
        },
        {
          title: this.$t('fund.payment.account') /* 付款账户 */,
          dataIndex: 'paymentAccountName',
          width: 100,
        },
        {
          title: this.$t('fund.payment.account') /* 付款账号 */,
          dataIndex: 'paymentAccount',
          width: 200,
        },
        {
          title: this.$t('fund.payment.method') /* 付款方式 */,
          dataIndex: 'paymentMethodDesc',
          width: 100,
        },
        {
          title: this.$t('fund.the.number') /* 笔数 */,
          dataIndex: 'lineCount',
          width: 80,
          render: value => <div style={{ textAlign: 'right' }}>{value}</div>,
        },
        {
          title: this.$t('fund.amount') /* 金额 */,
          dataIndex: 'amount',
          width: 140,
          render: value => <div style={{ textAlign: 'right' }}>{value}</div>,
        },
        {
          title: this.$t('fund.currency.code') /* 币种 */,
          dataIndex: 'currencyCode',
          width: 80,
        },
        {
          title: this.$t('fund.document.date') /* 单据日期 */,
          dataIndex: 'billDateDesc',
          width: 135,
          align: 'center',
        },
        {
          title: this.$t('fund.single.person') /* 制单人 */,
          dataIndex: 'employeeName',
          tooltips: true,
          width: 90,
        },
        {
          title: this.$t('fund.processing.state') /* 处理状态 */,
          dataIndex: 'handleStatusDesc',
          width: 100,
          align: 'center',
        },
        {
          title: this.$t('fund.batch.of.payment.status') /* 批付款状态 */,
          dataIndex: 'headStatusDesc',
          width: 100,
          align: 'center',
        },
        {
          title: this.$t('fund.the.log.information.associated') /* 日志关联信息 */,
          dataIndex: '',
          width: 150,
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
      }
    );
  }

  componentWillReceiveProps(nextProps) {
    // 切换tab页时，父组件传给子组件的nowStatus
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
    if (
      selectedRow[0] &&
      selectedRow[0].paymentMethodDesc === this.$t('fund.the.fact.the.directly.connected')
    ) {
      /* 银企直连 */
      this.setState(
        {
          selectedRowKeys,
          selectedRow,
          buttonBankCompany: !(selectedRowKeys.length > 0),
        },
        () => {
          this.setState({
            selectedRowKeys: selectedRowKeys[0],
            selectedRow,
            // selectedRow: selectedRow,
          });
        }
      );
    }
    // 线下支付结果录入是否显示
    if (
      selectedRow[0] &&
      selectedRow[0].paymentMethodDesc !== this.$t('fund.the.fact.the.directly.connected')
    ) {
      /* 银企直连 */
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
      title: this.$t('fund.delete.selected.document'), // 您确定删除所选择的单据，是否继续?
      okText: this.$t('fund.determine') /* 确定 */,
      okType: 'danger',
      cancelText: this.$t('fund.cancel') /* 取消 */,
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
          message.success(this.$t('fund.delete.successful1')); /* 删除成功！ */
          this.getList();
          this.setState({
            selectedRowKeys: '',
            deleteId: [],
          });
        }
      });
    } else {
      message.error(this.$t('fund.select.delete.document')); // 请选中要删除的单据！
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
              // eslint-disable-next-line global-require
              imgSuccess: require('./images/right-f.png'),
              sendSuccess: '发送成功',
              info:
                '单据已发送银行处理，30分钟后系统将自动从银行获取支付结果，您也可以进入支付中界面更新支付结果！',
              backList: '返回列表',
              showButtonOne: 'none',
              showButtonTwo: 'block', // 显示两个按钮
              showErrorInfo: 'none',
              lookPayStatus: this.$t('fund.to.check.the.payment.status') /* 查看支付状态 */,
              documentBackVisible: true,
            });
            message.success(this.$t('fund.request.successful1')); // 请求成功！
            this.getList();
          })
          .catch(() => {
            message.error(this.$t('fund.request.failed1')); // 请求失败！
          });
      } else {
        this.setState({
          // eslint-disable-next-line global-require
          imgSuccess: require('./images/delete.png'),
          // eslint-disable-next-line global-require
          imgDelete: require('./images/deleteSmall.png'),
          sendSuccess: '发送失败',
          info: '请核对并修改以下信息后，再重新提交！',
          error1: '该账户银企直联状态未开启，请开启账户银企直联状态',
          error2: '调取银行前置机失败，请查看网络连接状态',
          backList: '返回列表',
          showErrorInfo: 'inline',
          showButtonOne: 'block', // 只显示返回列表这一个按钮
          showButtonTwo: 'none',
          documentBackVisible: true,

          // ========以下可以删除，只是为了测试发送银行的发送成功==========
          // eslint-disable-next-line global-require
          // imgSuccess: require('./images/right-f.png'),
          // sendSuccess: '发送成功',
          // info:
          //   '单据已发送银行处理，30分钟后系统将自动从银行获取支付结果，您也可以进入支付中界面更新支付结果！',
          // backList: '返回列表',
          // showButtonOne: 'none',
          // showButtonTwo: 'block',
          // showErrorInfo: 'none',
          // lookPayStatus: this.$t('fund.to.check.the.payment.status') /* 查看支付状态 */,
          // documentBackVisible: true,
        });
      }
    } else {
      message.error(this.$t('fund.desc.code2')); // 请选中需要发送银行请求的单据！
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
      PayShowService.unlinePay(selectedRowKeys).then(() => {
        this.setState({
          // eslint-disable-next-line global-require
          imgSuccess: require('./images/right-f.png'),
          sendSuccess: '处理成功',
          info: '单据付款状态已经变更，请您进行线下付款，取回付款结果后，记得更新状态哦！！',
          backList: '返回列表',
          showButtonOne: 'block',
          showButtonTwo: 'none',
          showErrorInfo: 'none',
          documentBackVisible: true,
        });
        message.success(this.$t('fund.request.successful1')); /* 请求成功！ */
      });
      // .catch(() => {
      //   this.setState({
      //     // eslint-disable-next-line global-require
      //     imgSuccess: require('./images/delete.png'),
      //     sendSuccess: '发送失败',
      //     info: '请核对并修改以下信息后，再重新提交！',
      //     backList: '返回列表',
      //     showButtonOne: 'block',
      //     showButtonTwo: 'none',
      //     documentBackVisible: true,
      //   });
      //   message.error(this.$t('fund.request.failed1')); // 请求失败！
      // });
    } else {
      message.error(this.$t('fund.desc.code5')); // 请选中需要线下付款请求的单据！
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
        {this.$t('fund.selected')}
        {/* 已选择 */}
        <span style={{ fontWeight: 'bold', color: '#108EE9' }}> {rows.length} </span>{' '}
        {this.$t('fund.desc.code7')}
        {/* 项 共 */}
        <span style={{ fontWeight: 'bold', color: '#108EE9' }}>{totalAmount}</span>
        {this.$t('fund.yuan')}
        {/* 元 */}
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
          message.success(this.$t('fund.request.successful1')); /* 请求成功！ */
        })
        .catch(() => {
          message.error('请求失败！');
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
      message.error(this.$t('fund.desc.code6')); // 请选中要操作的单据！
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
          paymentAccount: values.accountNumber ? values.accountNumber.accountNumber : '', // 付款账号
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
      showButtonTwo,
      showButtonOne,
      error1,
      error2,
      showErrorInfo,
      showClose,
      buttonsUnlinePay,
      buttonBankCompany,
      unpaidButtons,
      payingButtons,
      successButtons,
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
      imgSuccess,
      imgDelete,
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
                  {this.$t('fund.send.the.bank')}
                  {/* 发送银行 */}
                </Button>
                <Button type="primary" onClick={this.sendUnlinePay} disabled={isTrue}>
                  {this.$t('fund.offline.payment')}
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
                  {this.$t('fund.delete')}
                </Button>
              </Col>
              <Col span={6} offset={10}>
                <Search
                  placeholder={this.$t('fund.please.enter.the.receipt.number')}
                  enterButton
                  onSearch={this.searchNumber}
                />
              </Col>
            </Row>
          </div>
          <div className="table-header-buttons" style={{ display: payingButtons }}>
            <Row>
              <Col span={8}>
                <Button type="primary" disabled={buttonBankCompany} onClick={this.sendBankCompany}>
                  {this.$t('fund.update.the.fact.straight.league.results')}
                </Button>
                <Button
                  type="primary"
                  disabled={buttonsUnlinePay}
                  onClick={this.payingSendUnlinePay}
                >
                  {this.$t('fund.offline.payment.entry.as.a.result')}
                </Button>
              </Col>
              <Col span={6} offset={10}>
                <Search
                  placeholder={this.$t('fund.please.enter.the.receipt.number')}
                  enterButton
                  onSearch={this.searchNumber}
                />
              </Col>
            </Row>
          </div>
          <div className="table-header-buttons" style={{ display: successButtons }}>
            <Row>
              <Col span={6} offset={18}>
                <Search
                  placeholder={this.$t('fund.please.enter.the.receipt.number')}
                  enterButton
                  onSearch={this.searchNumber}
                />
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
          <Modal title="" closable={showClose} footer={null} visible={documentBackVisible}>
            <div>
              <img
                style={{ width: '75px', height: '75px', marginLeft: '43%' }}
                src={imgSuccess}
                alt="..."
              />
            </div>
            <h1 style={{ fontWeight: 'bolder', textAlign: 'center', margin: '10px 0' }}>
              {sendSuccess}
            </h1>
            <p style={{ margin: '3px auto', width: '80%', textAlign: 'center', color: 'gray' }}>
              {info}
            </p>
            {/* 发送失败时提示失败的原因 */}
            <div style={{ display: showErrorInfo, margin: '10px 0' }}>
              <div style={{ margin: '0 auto', width: '80%', textAlign: 'center' }}>
                <img src={imgDelete} alt="" />
                <span style={{ marginLeft: '5px' }}>{error1}</span>
              </div>
              <div style={{ margin: '0 auto', width: '80%', textAlign: 'center' }}>
                <img src={imgDelete} alt="" />
                <span style={{ marginLeft: '5px' }}>{error2}</span>
              </div>
            </div>
            {/* 返回列表---按钮 */}
            <div
              style={{
                display: showButtonOne,
                width: '300px',
                marginLeft: '24%',
                marginTop: '30px',
              }}
            >
              <Button
                onClick={this.goList}
                style={{
                  marginLeft: '26%',
                  marginRight: '15px',
                  textAlign: 'center',
                  color: 'white',
                  backgroundColor: '#0086ff',
                }}
              >
                {backList}
              </Button>
            </div>
            {/* 返回列表---查看支付状态按钮 */}
            <div style={{ display: showButtonTwo, marginTop: '30px' }}>
              <Button
                onClick={this.goList}
                style={{
                  marginLeft: '26%',
                  marginRight: '15px',
                  textAlign: 'center',
                  color: 'white',
                  backgroundColor: '#0086ff',
                }}
              >
                {backList}
              </Button>
              <Button>{lookPayStatus}</Button>
            </div>
          </Modal>
        </div>
      </div>
    );
  }
}

const wrappedCompanyDistribution = Form.create()(Pay);

export default connect()(wrappedCompanyDistribution);
