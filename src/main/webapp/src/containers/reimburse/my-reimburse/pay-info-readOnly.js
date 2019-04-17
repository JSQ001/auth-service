import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import {
  Form,
  Select,
  Breadcrumb,
  Tag,
  Divider,
  Input,
  Tabs,
  Button,
  Menu,
  Radio,
  Dropdown,
  Row,
  Col,
  Spin,
  Timeline,
  message,
  Popover,
  Popconfirm,
  Icon,
  Card,
} from 'antd';
import config from 'config';
import 'styles/reimburse/reimburse.scss';
import Table from 'widget/table';
import reimburseService from 'containers/reimburse/my-reimburse/reimburse.service';
import moment from 'moment';

import Verification from 'containers/reimburse/my-reimburse/verification';

class PayInfo extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      columns: [
        {
          title: '序号',
          align: 'center',
          dataIndex: 'scheduleLineNumber',
          key: 'scheduleLineNumber',
          width: 50,
          render: (desc, record, index) => index + 1,
        },
        // {
        //     title: "币种", width: 60, dataIndex: "currency", key: "currency"
        // },
        // {
        //     title: "核销金额", width: 100, dataIndex: "writeOffAmount", key: "writeOffAmount", render: (value, record) => {
        //         return <span> {this.filterMoney(record.writeOffAmount)}</span>
        //     }
        // },
        {
          title: '付款金额',
          width: 100,
          dataIndex: 'amount',
          align: 'center',
          key: 'amount',
          render: (value, record) => {
            return this.filterMoney(record.amount);
          },
        },
        {
          title: '收款对象',
          width: 120,
          align: 'center',
          dataIndex: 'partnerName',
          key: 'partnerName',
          render: (value, record) => {
            return (
              <div>
                {/* <Tag color="#000">{record.payeeCategory == "EMPLOYEE" ? "员工" : "供应商"}</Tag> */}
                {record.payeeCategory && (
                  <Tag color="#000">{record.payeeCategory == 'EMPLOYEE' ? '员工' : '供应商'}</Tag>
                )}
                <div style={{ whiteSpace: 'normal' }}>{record.payeeName || '-'}</div>
              </div>
            );
          },
        },
        {
          title: '收款账户',
          width: 180,
          align: 'center',
          dataIndex: 'accountName',
          key: 'accountName',
          render: (value, record) => {
            return (
              <Popover
                content={
                  <div>
                    <div>户名：{record.accountName}</div>
                    <div>账户：{record.accountNumber}</div>
                  </div>
                }
              >
                <div>
                  <div
                    style={{ overflow: 'hidden', whiteSpace: 'nowrap', textOverflow: 'ellipsis' }}
                  >
                    户名：{record.accountName}
                  </div>
                  <div
                    style={{ overflow: 'hidden', whiteSpace: 'nowrap', textOverflow: 'ellipsis' }}
                  >
                    账户：{record.accountNumber}
                  </div>
                </div>
              </Popover>
            );
          },
        },
        {
          title: '付款属性',
          width: 200,
          align: 'center',
          dataIndex: 'cshTransactionClassName',
          key: 'cshTransactionClassName',
          render: (value, record) => {
            return (
              <Popover
                content={
                  <div>
                    <div>付款方式类型：{record.paymentMethodName}</div>
                    <div>付款用途：{record.cshTransactionClassName}</div>
                  </div>
                }
              >
                <div>
                  <div
                    style={{ overflow: 'hidden', whiteSpace: 'nowrap', textOverflow: 'ellipsis' }}
                  >
                    付款方式类型：{record.paymentMethodName || '-'}
                  </div>
                  <div
                    style={{ overflow: 'hidden', whiteSpace: 'nowrap', textOverflow: 'ellipsis' }}
                  >
                    付款用途：{record.cshTransactionClassName || '-'}
                  </div>
                </div>
              </Popover>
            );
          },
        },
        {
          title: '计划付款日期',
          width: 120,
          align: 'center',
          dataIndex: 'paymentScheduleDate',
          key: 'paymentScheduleDate',
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: '备注',
          align: 'center',
          dataIndex: 'description',
          key: 'description',
          render: desc => desc || '-',
        },
      ],
      data: [],
      loading: false,
      pagination: {
        total: 0,
      },
      page: 0,
      pageSize: 5,
      headerData: {},
      visible: false,
      model: {},
      flag: false,
      record: {},
    };
  }

  componentWillReceiveProps(nextProps) {
    if (this.state.flag != nextProps.flag) {
      this.setState({ flag: nextProps.flag, page: 0 }, () => {
        this.getList();
      });
    }

    if (nextProps.headerData.id && !this.state.headerData.id) {
      this.setState({ headerData: nextProps.headerData }, () => {
        this.getList();
      });
    }

    if (nextProps.disabled && this.state.columns.length === 8) {
      let columns = this.state.columns;
      columns.splice(columns.length - 1, 1);
      this.setState({ columns });
    }
  }

  //编辑
  edit = record => {
    this.props.payEdit && this.props.payEdit(record);
  };

  //获取数据列表
  getList = () => {
    this.setState({ loading: true });
    const { page, pageSize, headerData } = this.state;
    const params = {
      page: page,
      size: pageSize,
      reportHeaderId: headerData.id,
    };
    reimburseService.getPayLineList(params).then(res => {
      this.setState({
        data: res.data,
        pagination: {
          total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
          current: this.state.page + 1,
          pageSize: this.state.pageSize,
          onChange: this.onChangePaper,
          showSizeChanger: true,
          showQuickJumper: true,
          onShowSizeChange: this.onShowSizeChange,
          showTotal: (total, range) =>
            this.$t(
              { id: 'common.show.total' },
              { range0: `${range[0]}`, range1: `${range[1]}`, total: total }
            ),
          pageSizeOptions: ['5', '10'],
        },
        loading: false,
      });
    });
  };

  onChangePaper = page => {
    if (page - 1 !== this.state.page) {
      this.setState({ page: page - 1 }, () => {
        this.getList();
      });
    }
  };
  onShowSizeChange = (current, pageSize) => {
    this.setState(
      {
        page: current - 1,
        pageSize,
      },
      () => {
        this.getList();
      }
    );
  };
  addItem = () => {
    this.props.addPayPlan && this.props.addPayPlan();
  };

  deletePay = record => {
    this.props.deletePay && this.props.deletePay(record);
  };

  writeOff = record => {
    // let { tenantId, companyId, partnerCategory, partnerId, formId, exportHeaderId, contract } = this.props.params;
    let headerData = this.state.headerData;
    let model = {
      tenantId: this.props.company.tenantId,
      companyId: headerData.companyId,
      partnerCategory: record.payeeCategory,
      partnerId: record.payeeId,
      formId: headerData.formId,
      exportHeaderId: headerData.id,
      documentLineId: record.id,
      currencyCode: headerData.currencyCode,
    };
    model.writeOffAmount = record.writeOffAmount;
    model.amount = record.amount;
    model.contractHeaderId = record.contractHeaderId;
    this.setState({ visible: true, model, record });
  };

  //核销确定
  writeOffOk = cashWriteOffMsg => {
    const { record } = this.state;

    let data = {
      partnerCategory: record.payeeCategory,
      partnerId: record.payeeId,
      companyId: record.companyId,
      documentType: 'PUBLIC_REPORT',
      documentHeaderId: record.expReportHeaderId,
      documentLineId: record.id,
      documentLineAmount: record.amount,
      cashWriteOffMsg: cashWriteOffMsg,
    };

    reimburseService
      .writeOff(data)
      .then(res => {
        message.success('核销成功！');
        this.setState({ visible: false, model: {}, record: {} });
        this.props.writeOffOk && this.props.writeOffOk(true);
      })
      .catch(err => {
        message.error('核销失败：' + err.response.data.message);
      });
  };

  //跳转到合同详情
  toContract = id => {
    // if (id) {
    //     let url = menuRoute.getRouteItem('contract-detail', 'key');
    //     window.open(url.url.replace(':id', id).replace(':from', "reimburse"), "_blank");
    // }
  };

  expandedRowRender = record => {
    let { contractHeaderLineDTO } = record;
    contractHeaderLineDTO = contractHeaderLineDTO ? contractHeaderLineDTO : {};
    record.paidInfoDTO = record.paidInfoDTO
      ? record.paidInfoDTO
      : { paidAmount: 0, returnAmount: 0 };
    return (
      <div>
        {!!contractHeaderLineDTO.lineNumber && (
          <Row>
            <Col style={{ textAlign: 'right' }} span={2}>
              <span>关联合同:</span>
            </Col>
            <Col span={20} offset={1}>
              <span>合同名称：</span>
              <span style={{ marginRight: 20 }}>
                {contractHeaderLineDTO.contractName ? contractHeaderLineDTO.contractName : '_'}
              </span>
              <span>合同编号：</span>
              <a
                onClick={() => {
                  this.toContract(contractHeaderLineDTO.headerId);
                }}
                style={{ marginRight: 20 }}
              >
                {contractHeaderLineDTO.contractNumber ? contractHeaderLineDTO.contractNumber : '_'}
              </a>
              <span>行号：</span>
              <span style={{ marginRight: 20 }}>
                {contractHeaderLineDTO.lineNumber ? contractHeaderLineDTO.lineNumber : '_'}
              </span>
              <span>金额：</span>
              <span>
                {contractHeaderLineDTO.lineAmount ? contractHeaderLineDTO.lineAmount : '_'}
              </span>
            </Col>
          </Row>
        )}
        {record.paidInfoDTO.paidAmount && record.paidInfoDTO.returnAmount ? <Divider /> : null}
        {record.paidInfoDTO.paidAmount && record.paidInfoDTO.returnAmount ? (
          <Row>
            <Col style={{ textAlign: 'right' }} span={2}>
              <span>付款日志：</span>
            </Col>
            <Col span={20} offset={1}>
              <Row>
                {record.paidInfoDTO.paidAmount && (
                  <Col span={12}>
                    已付款总金额：<span>
                      {record.currency + ' ' + this.formatMoney(record.paidInfoDTO.paidAmount)}
                    </span>
                  </Col>
                )}
                {record.paidInfoDTO.returnAmount && (
                  <Col span={12}>
                    退款总金额：<span>
                      {record.currency + ' ' + this.formatMoney(record.paidInfoDTO.returnAmount)}
                    </span>
                  </Col>
                )}
              </Row>
            </Col>
          </Row>
        ) : null}
        {record.cashWriteOffDtoList && record.cashWriteOffDtoList.length ? <Divider /> : null}
        {record.cashWriteOffDtoList && record.cashWriteOffDtoList.length ? (
          <Row>
            <Col style={{ textAlign: 'right' }} span={2}>
              <span>核销历史：</span>
            </Col>
            <Col span={20} offset={1}>
              {record.cashWriteOffDtoList &&
                record.cashWriteOffDtoList.map((item, index) => {
                  return (
                    <Row>
                      <Col span={9}>
                        预付款单编号：<a>{item.prepaymentRequisitionNumber}</a>
                      </Col>
                      <Col span={6}>
                        预付款单类型：<span>{item.prepaymentRequisitionTypeDesc}</span>
                      </Col>
                      <Col span={5}>
                        核销金额：<span>{this.formatMoney(item.writeOffAmount)}</span>
                      </Col>
                      <Col span={4}>
                        交易日期：<span>{item.payDate}</span>
                      </Col>
                    </Row>
                  );
                })}
            </Col>
          </Row>
        ) : null}
      </div>
    );
  };

  //格式化金额
  formatMoney = x => {
    var f = parseFloat(x);
    if (isNaN(f)) {
      return false;
    }
    var f = Math.round(x * 100) / 100;
    var s = f.toString();
    var rs = s.indexOf('.');
    if (rs < 0) {
      rs = s.length;
      s += '.';
    }
    while (s.length <= rs + 2) {
      s += '0';
    }
    return s;
  };

  render() {
    const { loading, data, columns, visible, model } = this.state;
    const { summaryView, headerData } = this.props;
    let writeOffAmount = 0;
    data.map(item => (writeOffAmount += item.writeOffAmount));

    return (
      <div>
        <Card
          style={{ marginTop: 20, boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)' }}
          title="付款信息"
        >
          <div className="table-header" style={{ marginTop: '0px' }}>
            <span style={{ float: 'right' }}>
              报账总金额：<span style={{ color: 'green' }}>
                {headerData.currencyCode} {this.filterMoney(headerData.totalAmount)}
              </span>&nbsp;&nbsp;/&nbsp;&nbsp;付款总金额：<span style={{ color: 'green' }}>
                {headerData.currencyCode}{' '}
                {this.filterMoney(headerData.totalAmount - writeOffAmount)}
              </span>{' '}
              &nbsp;&nbsp;/&nbsp;&nbsp;核销总金额：{' '}
              <span style={{ color: 'green' }}>
                {headerData.currencyCode} {this.filterMoney(writeOffAmount)}
              </span>
            </span>
          </div>
          <Table
            style={{ clear: 'both' }}
            rowKey={record => record.id}
            columns={columns}
            dataSource={data}
            loading={loading}
            pagination={this.state.pagination}
            expandedRowRender={this.expandedRowRender}
            bordered
            size="middle"
          />

          <Verification
            handleOk={this.writeOffOk}
            model={model}
            close={() => {
              this.setState({ visible: false });
            }}
            visible={visible}
          />
        </Card>
      </div>
    );
  }
}

// PayInfo.contextTypes = {
//     router: React.PropTypes.object
// }

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

//FormList = Form.create()(FormList);

// export default injectIntl(PayInfo)

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(PayInfo);
