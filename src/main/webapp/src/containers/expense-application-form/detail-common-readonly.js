import React from 'react';
import { connect } from 'dva';
import { Form, Row, Col, Breadcrumb, message, Card, Popover, Modal } from 'antd';
import Table from 'widget/table';
import SlideFrame from 'widget/slide-frame';
import { routerRedux } from 'dva/router';
import DocumentBasicInfo from 'widget/Template/document-basic-info';
import moment from 'moment';
import PropTypes from 'prop-types';
import ApplicationLineDetail from './application-line-detail';
import ContractDetail from 'containers/contract/contract-approve/contract-detail-common';

import ApproveHistory from 'widget/Template/approve-history-work-flow';
import service from './service';

import 'styles/pre-payment/my-pre-payment/pre-payment-detail.scss';

class PrePaymentCommon extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      lineLoading: false,
      historyLoading: false, //控制审批历史记录是否loading
      columns: [
        {
          title: this.$t('expense.the.serial.number') /*序号*/,
          dataIndex: 'number',
          width: 90,
          render: (value, record, index) =>
            (this.state.pagination.current - 1) * this.state.pagination.pageSize + index + 1,
        },
        {
          title: this.$t('expense.the.company') /*公司*/,
          dataIndex: 'companyName',
          width: 120,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: this.$t('expense.department') /*部门*/,
          dataIndex: 'departmentName',
          width: 120,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: this.$t('expense.responsibility.center') /*责任中心*/,
          dataIndex: 'responsibilityCenterCodeName',
          width: 120,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: this.$t('expense.application.type') /*申请类型*/,
          dataIndex: 'expenseTypeName',
          width: 120,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: this.$t('expense.apply.amount') /*申请金额*/,
          dataIndex: 'amount',
          width: 120,
          render: value => this.filterMoney(value),
        },
        {
          title: this.$t('expense.functional.currency.amount') /*本位币金额*/,
          dataIndex: 'functionalAmount',
          width: 120,
          render: value => this.filterMoney(value),
        },
      ],
      showLineDetail: false,
      showSlideFrame: false,
      slideFrameTitle: '',
      record: {},
      approveHistory: [],
      headerInfo: {},
      backLoadding: false,
      lineInfo: {},
      pagination: {
        current: 1,
        showSizeChanger: true,
        pageSize: 5,
        pageSizeOptions: ['5', '10', '20', '50', '100'],
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total: total,
          }),
      },
      showContract: false,
    };
  }

  componentDidMount() {
    //设置基本信息
    this.setBasicInfo();
    //设置表格动态列
    this.setTableColumns();
  }

  //设置基本信息
  setBasicInfo = () => {
    const { headerData } = this.props;
    let headerInfo = {
      businessCode: headerData.documentNumber,
      createdDate: headerData.requisitionDate,
      formName: headerData.typeName,
      createByName: headerData.createdName,
      currencyCode: headerData.currencyCode,
      totalAmount: headerData.totalFunctionAmount,
      statusCode: headerData.status,
      remark: headerData.remarks,
      infoList: [
        { label: this.$t('expense.reverse.apply.name'), value: headerData.employeeName } /*申请人*/,
        { label: this.$t('expense.the.company'), value: headerData.companyName } /*公司*/,
        { label: this.$t('expense.department'), value: headerData.departmentName } /*部门*/,
      ],
      customList: headerData.dimensions
        ? headerData.dimensions
            .filter(o => o.headerFlag)
            .map(o => ({ label: o.name, value: o.valueName }))
        : [],
      attachments: headerData.attachments,
    };

    if (headerData.associateContract) {
      headerInfo.infoList.push({
        label: this.$t('expense.associated.with.the.contract') /*关联合同*/,
        value: headerData.contractNumber,
        linkId: headerData.contractHeaderId,
        onClick: id => {
          this.setState({ showContract: true });
        },
      });
    }
    this.setState({ headerInfo });
  };

  //设置表格动态列
  setTableColumns = () => {
    const { headerData } = this.props;
    let { columns } = this.state;

    service.getColumnInfo(headerData.id).then(res => {
      res.data.map(item => {
        columns.push({
          ...item,
          width: 150,
        });
      });

      let option = {
        title: this.$t('expense.reverse.remark') /*备注*/,
        dataIndex: 'remarks',
      };
      if (res.data.length > 4) {
        option.width = 350;
      }
      columns.push(option);
      columns.push({
        title: this.$t('expense.operation') /*操作*/,
        dataIndex: 'options',
        width: 120,
        fixed: 'right',
        render: (value, record) => (
          <a onClick={() => this.lineDetail(record)}>{this.$t('expense.wallet.checkDetail')}</a>
        ) /*查看详情*/,
      });

      this.setState({ columns }, () => {
        this.getLineInfo();
      });
    });
  };

  //获取行数据
  getLineInfo = () => {
    const { headerData } = this.props;
    const {
      pagination: { pageSize, current },
      pagination,
    } = this.state;
    this.setState({ lineLoading: true });
    service
      .getApplicationLines(headerData.id, { size: pageSize, page: current - 1 })
      .then(res => {
        let { headerInfo } = this.state;
        headerInfo.totalAmount = res.data.currencyAmount ? res.data.currencyAmount.amount : '0.00';
        this.setState({
          headerInfo,
          lineInfo: res.data,
          lineLoading: false,
          pagination: { ...pagination, total: Number(res.headers['x-total-count']) },
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //侧滑
  showSlide = flag => {
    this.setState({ showSlideFrame: flag, record: {} });
  };

  //关闭侧滑
  handleCloseSlide = flag => {
    let { pagination } = this.state;
    this.setState({ showSlideFrame: false, record: {} }, () => {
      if (flag) {
        pagination.current = 1;
        this.setState({ pagination }, this.getLineInfo);
      }
    });
  };

  //编辑
  edit = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname:
          `/expense-application/expense-application/edit-expense-application/` +
          this.props.headerData.id,
      })
    );
  };

  //取消
  onCancel = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/expense-application/expense-application/expense-application-form',
      })
    );
  };

  //查看行详情
  lineDetail = record => {
    this.setState({ record }, () => {
      this.setState({
        showLineDetail: true,
        slideFrameTitle: this.$t('expense.editor.for.special'),
      }); /*编辑申请单行*/
    });
  };

  //扩展行
  expandedRow = record => {
    if (record.currencyCode == this.props.company.baseCurrency) return null;
    return (
      <div>
        <Row>
          <Col span={2}>
            <span style={{ float: 'right' }}>{this.$t('expense.amount.of.property')}</span>
            {/*金额属性*/}
          </Col>
          <Col span={6} offset={1}>
            {this.$t('expense.exchange.rate.date1')}
            {moment(record.exchangeDate).format('YYYY-MM-DD')}
          </Col>
          <Col span={6}>
            {this.$t('expense.exchange.rate1')}
            {record.exchangeRate}
          </Col>
        </Row>
      </div>
    );
  };

  tableChange = pagination => {
    this.setState({ pagination }, () => {
      this.getLineInfo();
    });
  };

  render() {
    const {
      lineInfo,
      showLineDetail,
      columns,
      record,
      lineLoading,
      pagination,
      headerInfo,
      showContract,
    } = this.state;
    const { headerData } = this.props;

    return (
      <div className="pre-payment-common">
        <Card
          style={{
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
          }}
          bodyStyle={{ padding: '24px 32px', paddingTop: 0 }}
        >
          <DocumentBasicInfo params={headerInfo} />
        </Card>

        <Card
          style={{
            marginTop: 20,
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
          }}
          title={this.$t('expense.application.information')} /*申请信息*/
        >
          <div className="table-header">
            {lineInfo.currencyAmount && (
              <div style={{ float: 'right' }}>
                <Breadcrumb style={{ marginBottom: '10px', lineHeight: '32px' }}>
                  <Breadcrumb.Item>
                    {this.$t('expense.apply.amount')}:/*申请金额*/
                    <span style={{ color: 'green' }}>
                      {' ' + lineInfo.currencyAmount.currencyCode}{' '}
                      {this.filterMoney(lineInfo.currencyAmount.amount)}
                    </span>
                  </Breadcrumb.Item>
                  <Breadcrumb.Item>
                    {this.$t('expense.local.currency.amount')}:<span style={{ color: 'green' }}>
                      {/*本币金额*/}
                      {' ' + this.props.company.baseCurrency}{' '}
                      {this.filterMoney(lineInfo.currencyAmount.functionalAmount)}
                    </span>
                  </Breadcrumb.Item>
                </Breadcrumb>
              </div>
            )}
          </div>
          <Table
            style={{ clear: 'both' }}
            rowKey={record => record.id}
            columns={columns}
            dataSource={lineInfo.lines || []}
            bordered
            loading={lineLoading}
            size="middle"
            pagination={pagination}
            expandedRowRender={this.expandedRow}
            onChange={this.tableChange}
            scroll={{ x: 1300 }}
          />
        </Card>

        <div
          style={{
            marginTop: 20,
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
          }}
        >
          <ApproveHistory type="801009" oid={headerData.documentOid} />
        </div>

        <SlideFrame
          title={this.$t('expense.details')} /*详情*/
          show={showLineDetail}
          onClose={() => {
            this.setState({ showLineDetail: false });
          }}
        >
          <ApplicationLineDetail
            close={() => {
              this.setState({ showLineDetail: false });
            }}
            headerData={this.props.headerData}
            lineId={record.id}
          />
        </SlideFrame>
        <Modal
          title={this.$t('expense.the.contract.details')} /*合同详情*/
          visible={showContract}
          onCancel={() => {
            this.setState({ showContract: false });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
          }}
          footer={null}
        >
          <ContractDetail id={headerData.contractHeaderId} isApprovePage={true} />
        </Modal>
      </div>
    );
  }
}

PrePaymentCommon.propTypes = {
  id: PropTypes.any.isRequired, //显示数据
  flag: PropTypes.bool, //是否显示审批历史
};

PrePaymentCommon.defaultProps = {
  flag: true,
};

const wrappedPrePaymentCommon = Form.create()(PrePaymentCommon);
function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedPrePaymentCommon);
