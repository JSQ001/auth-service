import React from 'react';
import { Row, Col, Button, Alert, message, Modal, Input, DatePicker, Form, Popover } from 'antd';
import { connect } from 'dva';
import Table from 'widget/table';
import moment from 'moment';
import { routerRedux } from 'dva/router';
import paymentWorkbenchService from './payment-workbench-service';
import FundSearchForm from '../../fund-components/fund-search-form';
import { accAdd } from '../../fund-components/utils';
import 'styles/fund/account.scss';

const { TextArea } = Input;
class PaymentWorkbench extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      buttonsDisableClick: true, // 操作按钮是否可点击
      noticeAlert: null, // 提示信息
      loading: false, // loading状态
      selectedRowKeys: [],
      selectedRow: [],
      documentBackVisible: false, // 退回对话框
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
      searchParams: {},
      searchForm: [
        {
          colSpan: 6,
          type: 'valueList',
          label: this.$t('fund.system.source') /* 系统来源 */,
          id: 'sourceSystem',
          options: [],
          valueListCode: 'ZJ_SOURCE',
        },
        {
          colSpan: 6,
          type: 'modalList',
          label: this.$t('fund.documentary.company') /* 单据公司 */,
          id: 'documentCompany',
          listType: 'company',
          labelKey: 'name',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'id',
          single: true,
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: this.$t('fund.public.private.signs') /* 公私标志 */,
          id: 'propFlag',
          options: [],
          valueListCode: 'ZJ_BATCH_PAY_ACCOUNT_TYPE',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: this.$t('fund.payment.method') /* 付款方式 */,
          id: 'paymentPurpose',
          options: [],
          valueListCode: 'ZJ_PAYMENT_TYPE',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: this.$t('fund.receiving.bank') /* 收款银行 */,
          id: 'gatherBankNum',
          options: [],
          valueListCode: 'ZJ_OPEN_BANK',
        },
        {
          colSpan: 6,
          type: 'input',
          label: this.$t('fund.collection.account') /* 收款账户 */,
          id: 'gatherAccountName',
        },
        {
          colSpan: 6,
          type: 'input',
          label: this.$t('fund.receiving.account') /* 收款账号 */,
          id: 'gatherAccount',
        },
        {
          colSpan: 6,
          type: 'input',
          label: this.$t('fund.single.person') /* 制单人 */,
          id: 'createdBy',
        },
        {
          colSpan: 6,
          type: 'input',
          label: this.$t('fund.source.document.no.') /* 来源单据号 */,
          id: 'sourceDocumentNum',
        },
        {
          colSpan: 6,
          type: 'intervalInput',
          label: this.$t('fund.credit.zone') /* 信用分区间 */,
          id: 'creditScore',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: this.$t('fund.early.warning.state') /* 预警状态 */,
          id: 'warningStatus',
          options: [],
          valueListCode: 'ZJ_WARNING_TYPE',
        },
        {
          colSpan: 6,
          type: 'checkBox',
          label: this.$t('fund.including.subsidiary') /* 包含子公司 */,
          id: 'childCompanyFlag',
        },
      ],
      columns: [
        {
          title: this.$t('fund.early.warning.state') /* 预警状态 */,
          dataIndex: 'warningStatusDesc',
          width: 100,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.early.warning.information') /* 预警信息 */,
          dataIndex: 'warningData',
          width: 150,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.source.system') /* 来源系统 */,
          dataIndex: 'sourceSystemDesc',
          width: 100,
        },
        {
          title: this.$t('fund.source.document.number') /* 来源单据编号 */,
          dataIndex: 'sourceDocumentNum',
          width: 200,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.payment.purpose') /* 付款用途 */,
          dataIndex: 'paymentAccountName',
          width: 100,
        },
        {
          title: this.$t('fund.affiliated.company') /* 所属公司 */,
          dataIndex: 'companyName',
          width: 150,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.collection.account') /* 收款账户 */,
          dataIndex: 'gatherAccountName',
          width: 150,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.receiving.bank') /* 收款银行 */,
          dataIndex: 'gatherBankName',
          width: 230,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.collection.branch') /* 收款分行 */,
          dataIndex: 'gatherBranchBankName',
          width: 200,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.receiving.account') /* 收款账号 */,
          dataIndex: 'gatherAccount',
          width: 160,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.amount') /* 金额 */,
          dataIndex: 'amount',
          width: 140,
          render: amount => this.filterMoney(amount),
        },
        {
          title: this.$t('fund.payment.method') /* 付款方式 */,
          dataIndex: 'paymentMethodDesc',
          width: 120,
        },
        {
          title: this.$t('fund.abstract') /* 摘要 */,
          dataIndex: 'summary',
          width: 200,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.public.private.signs') /* 公私标志 */,
          dataIndex: 'propFlagDesc',
          width: 80,
          align: 'center',
        },
        {
          title: this.$t('fund.single.person') /* 制单人 */,
          dataIndex: 'createdByFk',
          width: 120,
        },
      ],
    };
  }

  componentWillMount() {
    this.getPaymentWorkbenchList();
  }

  /**
   * 提示框显示
   */
  noticeAlert = rows => {
    const initialAmount = 0;
    const totalAmount = rows.reduce(
      (accumulator, currentValue) => accAdd(accumulator, currentValue.amount),
      initialAmount
    );
    const noticeAlert = (
      <span>
        {this.$t('fund.selected')}
        {/* 已选择 */}
        <span style={{ fontWeight: 'bold', color: '#108EE9' }}> {rows.length} </span>{' '}
        {this.$t('fund.desc.code7')}
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
    this.setState(
      {
        selectedRowKeys,
        selectedRow,
        buttonsDisableClick: !(selectedRowKeys.length > 0),
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
   * 搜索
   */
  handleSearch = values => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          sourceSystem: values.sourceSystem ? values.sourceSystem.key : '',
          documentCompany: values.documentCompany ? values.documentCompany[0].id : '',
          propFlag: values.propFlag ? values.propFlag.key : '',
          paymentPurpose: values.paymentPurpose ? values.paymentPurpose.key : '',
          gatherBankNum: values.gatherBankNum ? values.gatherBankNum.key : '',
          gatherAccountName: values.gatherAccountName || '',
          gatherAccount: values.gatherAccount || '',
          createdBy: values.createdBy || '',
          sourceDocumentNum: values.sourceDocumentNum || '',
          creditScoreFrom: values.creditScore.intervalFrom || '',
          creditScoreTo: values.creditScore.intervalTo || '',
          warningStatus: values.warningStatus ? values.warningStatus.key : '',
          childCompanyFlag: values.childCompanyFlag || false,
        },
      },
      () => {
        this.getPaymentWorkbenchList();
      }
    );
  };

  /**
   * 获取资金付款工作台列表数据
   */
  getPaymentWorkbenchList = () => {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true, noticeAlert: null, selectedRowKeys: [] });
    paymentWorkbenchService
      .getPaymentWorkbenchList(pagination.page, pagination.pageSize, searchParams)
      .then(response => {
        this.setState({
          tableData: response.data.paymentInInterfaceDTOS,
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
  };

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
        this.getPaymentWorkbenchList();
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
        this.getPaymentWorkbenchList();
      }
    );
  };

  /**
   * 创建批
   */

  createBatch = () => {
    const { selectedRowKeys, selectedRow } = this.state;
    const { dispatch } = this.props;
    const hasLocked = selectedRow.every(item => {
      return item.warningStatus !== 'LOCKED';
    });
    if (hasLocked) {
      paymentWorkbenchService
        .createBatch(selectedRowKeys)
        .then(response => {
          if (response.data === true) {
            dispatch(
              routerRedux.push({
                pathname: `/payment-management/payment-workbench/payment-workbench-edit`,
              })
            );
          }
        })
        .catch(error => {
          message.error(error.response.data.message);
        });
    } else {
      message.error(this.$t('fund.desc.code9')); // 你所选择的单据包含锁定状态的单据,不能创建批！
    }
  };

  /**
   * 根据规则创建批
   */
  allCreate = () => {
    const { searchParams } = this.state;
    paymentWorkbenchService
      .allCreate(searchParams)
      .then(res => {
        if (res.status === 200) {
          message.success('创建成功');
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 单据锁定
   */
  documentLock = () => {
    const { selectedRowKeys } = this.state;
    paymentWorkbenchService
      .documentLock(selectedRowKeys)
      .then(response => {
        if (response.data === true) {
          message.success(this.$t('fund.lock.success')); /* 锁定成功 */
          this.getPaymentWorkbenchList();
        }
      })
      .catch(error => {
        message.error(error.response.data.message);
      });
  };

  /**
   * 单据解锁
   */
  documentUnlock = () => {
    const { selectedRowKeys } = this.state;
    paymentWorkbenchService
      .documentUnlock(selectedRowKeys)
      .then(response => {
        if (response.data === true) {
          message.success(this.$t('fund.unlock.success')); /* 解锁成功 */
          this.getPaymentWorkbenchList();
        }
      })
      .catch(error => {
        message.error(error.response.data.message);
      });
  };

  /**
   * 弹框确定
   */
  handleOk = () => {
    const {
      form,
      form: { resetFields },
    } = this.props;
    const values = form.getFieldsValue();
    this.documentBack(values.backReason);
    this.setState({
      documentBackVisible: false,
    });
    resetFields('backReason');
  };

  /**
   * 弹框取消
   */
  handleCancel = () => {
    this.setState({
      documentBackVisible: false,
    });
    const {
      form: { resetFields },
    } = this.props;
    resetFields('backReason');
  };

  /**
   * 单据退回
   */
  documentBack = reason => {
    const { selectedRowKeys } = this.state;
    paymentWorkbenchService
      .documentBack(selectedRowKeys, reason)
      .then(response => {
        if (response.data === true) {
          message.success(this.$t('fund.return.success')); /* 退回成功 */
          this.getPaymentWorkbenchList();
        }
      })
      .catch(error => {
        message.error(error.response.data.message);
      });
  };

  render() {
    const {
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
    } = this.state;
    const {
      form: { getFieldDecorator },
    } = this.props;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div className="train">
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
          <div className="table-header-buttons">
            <Row>
              <Button
                type="primary"
                disabled={buttonsDisableClick}
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.createBatch();
                }}
              >
                {this.$t('fund.create.batch')}
                {/* 创建批 */}
              </Button>
              <Button
                type="primary"
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.allCreate();
                }}
              >
                {this.$t('fund.create.batches.according.rules')}
              </Button>
              {/* 根据规则创建批 */}
              <Button
                type="primary"
                disabled={buttonsDisableClick}
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.documentLock();
                }}
              >
                {this.$t('fund.document.locked')}
                {/* 单据锁定 */}
              </Button>
              <Button
                type="primary"
                disabled={buttonsDisableClick}
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.documentUnlock();
                }}
              >
                {this.$t('fund.locking.relieve')}
                {/* 锁定解除 */}
              </Button>
              <Button
                type="danger"
                disabled={buttonsDisableClick}
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.setState({
                    documentBackVisible: true,
                  });
                }}
              >
                {this.$t('fund.return')}
                {/* 退回 */}
              </Button>
            </Row>
          </div>
          {noticeAlert ? (
            <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
          ) : (
            ''
          )}
          <Table
            rowKey={record => record.id}
            rowClassName={record => {
              if (record.warningStatus === 'LOCKED') {
                return 'danger';
              }
            }}
            bordered
            dataSource={tableData}
            loading={loading}
            columns={columns}
            pagination={pagination}
            onChange={this.onChangePager}
            scroll={{ x: 1500 }}
            rowSelection={rowSelection}
          />
          <Modal
            title={this.$t('fund.return.documents')} /* 单据退回 */
            visible={documentBackVisible}
            onOk={this.handleOk}
            onCancel={this.handleCancel}
          >
            <Form>
              <Row>
                <Col span={4}>
                  <span>{this.$t('fund.back.to.the.time:')}</span>
                  {/* 退回时间： */}
                </Col>
                <Col span={20}>
                  <Form.Item>
                    {getFieldDecorator('backTime', {
                      initialValue: moment(new Date()),
                    })(<DatePicker />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row style={{ marginTop: '20px' }}>
                <Col span={4}>
                  <span>{this.$t('fund.return.reason:')}</span>
                  {/* 退回原因： */}
                </Col>
                <Col span={20}>
                  <Form.Item>
                    {getFieldDecorator('backReason', {
                      initialValue: '',
                    })(
                      <TextArea
                        placeholder={this.$t('fund.input.return.cause')} /* 请输入退回原因 */
                        autosize={{ minRows: 4, maxRows: 6 }}
                      />
                    )}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Modal>
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

export default connect(mapStateToProps)(Form.create()(PaymentWorkbench));
