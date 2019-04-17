import React, { Component } from 'react';
import { Card, Row, Col, Breadcrumb, Popover, Tag, Affix, Button, message } from 'antd';
import DocumentBasicInfo from 'widget/Template/document-basic-info';
import { connect } from 'dva';
import config from 'config';
import moment from 'moment';
import CustomTable from 'components/Widget/custom-table';
import ApproveHistory from 'widget/Template/approve-history-work-flow';
import { routerRedux } from 'dva/router';

import detailsService from './details-service';

const labelStyle = { fontWeight: '600', textDecoration: 'underline', textAlign: 'right' };

class NewContractDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      contractId: props.match.params.id, // 合同id
      // contractId: '1099692273659715586',
      contractHeaderInfo: {}, // 头信息
      contractParams: {}, // 头信息（全）
      columns: [
        {
          title: this.$t({ id: 'my.contract.currency' }),
          dataIndex: 'currency',
          align: 'center',
          width: 90,
        },
        {
          title: this.$t({ id: 'request.amount' }),
          dataIndex: 'amount',
          align: 'center',
          render: desc => this.filterMoney(desc),
        },
        {
          title: this.$t({ id: 'request.base.amount' }),
          dataIndex: 'funcAmount',
          align: 'center',
          render: (desc, record) => this.filterMoney(record.functionAmount),
        },
        {
          title: this.$t({ id: 'my.receivable' }),
          dataIndex: 'partnerName',
          align: 'center',
          render: (value, record) => {
            return (
              <div>
                <Tag color="#000">
                  {record.partnerCategory === 'EMPLOYEE'
                    ? this.$t('acp.employee')
                    : this.$t('acp.vendor')}
                </Tag>
                <div style={{ whiteSpace: 'normal' }}>{record.partnerName}</div>
              </div>
            );
          },
        },
        {
          title: this.$t({ id: 'my.contract.plan.pay.date' }),
          dataIndex: 'dueDate',
          align: 'center',
          render: value => (
            <Popover content={value ? moment(value).format('YYYY-MM-DD') : '-'}>
              {value ? moment(value).format('YYYY-MM-DD') : '-'}
            </Popover>
          ),
        },
        {
          title: this.$t({ id: 'common.remark' }),
          dataIndex: 'remark',
          align: 'center',
          render: value =>
            value ? (
              <Popover content={value} overlayStyle={{ maxWidth: 300 }}>
                {value}
              </Popover>
            ) : (
              '-'
            ),
        },
      ],
    };
  }

  componentDidMount = () => {
    this.getHeaderInfo();
  };

  getHeaderInfo = () => {
    const { contractId } = this.state;
    detailsService
      .getContractHeaderInfo(contractId)
      .then(response => {
        const contractHeaderInfo = {
          formName: response.data.contractTypeName,
          totalAmount: response.data.amount ? response.data.amount : 0,
          statusCode: response.data.status,
          remark: response.data.remark,
          businessCode: '12',
          currencyCode: response.data.currency,
          infoList: [
            { label: this.$t('my.contract.number'), value: response.data.contractNumber },
            {
              label: this.$t('common.applicant'),
              value:
                response.data.employee &&
                `${response.data.employee.employeeCode}-${response.data.employee.fullName}`,
            },
            { label: this.$t('my.contract.category'), value: response.data.contractCategoryName },
          ],
          attachments: response.data.attachments,
        };
        this.setState({ contractHeaderInfo, contractParams: { ...response.data } });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 返回
  onBack = () => {
    const { dispatch, match } = this.props;
    dispatch(
      routerRedux.replace({
        pathname: `/project-manage/my-project-apply/project-apply-details/${match.params.applyId}`,
      })
    );
  };

  render() {
    const { contractHeaderInfo, contractId, columns, contractParams } = this.state;
    const { company } = this.props;
    return (
      <div className="project-apply-contract-details">
        <Card>
          <DocumentBasicInfo params={contractHeaderInfo} noHeader />
        </Card>
        <Card title="合同信息" style={{ margin: '20px 0' }}>
          <div style={{ marginLeft: 15, fontSize: '12px' }}>
            <Row gutter={24}>
              <Col span={2} style={labelStyle}>
                {this.$t('common.baseInfo')}:
              </Col>
              <Col span={2} offset={1}>
                {this.$t('my.contract.contractCompany')}:
              </Col>
              <Col span={5}>
                <span title={contractParams.companyName}>{contractParams.companyName}</span>
              </Col>
              <Col span={2}>{this.$t('acp.contract.name')}:</Col>
              <Col span={5}>
                <span title={contractParams.contractName}>{contractParams.contractName}</span>
              </Col>
              <Col span={2}>{this.$t('my.contract.signDate')}:</Col>
              <Col span={5}>
                <span
                  title={
                    contractParams.contractName
                      ? contractParams.signDate
                        ? moment(new Date(contractParams.signDate)).format('YYYY-MM-DD')
                        : '-'
                      : ''
                  }
                >
                  {contractParams.contractName
                    ? contractParams.signDate
                      ? moment(new Date(contractParams.signDate)).format('YYYY-MM-DD')
                      : '-'
                    : ''}
                </span>
              </Col>
            </Row>
            <Row gutter={24} style={{ marginBottom: '10px', marginTop: '10px' }}>
              <Col span={2} style={labelStyle}>
                {this.$t('my.contract.party.info')}:
              </Col>

              <Col span={2} offset={1}>
                {this.$t('my.contract.partner.category')}:
              </Col>
              <Col span={5}>
                <span title={contractParams.partnerCategoryName}>
                  {contractParams.partnerCategoryName}
                </span>
              </Col>
              <Col span={2}>{this.$t('my.contract.partner')}:</Col>
              <Col span={5}>
                <span title={contractParams.partnerName}>{contractParams.partnerName}</span>
              </Col>
            </Row>
            <Row gutter={24}>
              <Col span={2} style={labelStyle}>
                {this.$t('supplier.management.otherInfo')}:
              </Col>

              <Col span={2} offset={1}>
                {this.$t('my.contract.responsible.department')}:
              </Col>
              <Col span={5}>
                <span title={contractParams.unitName}>
                  {contractParams.contractName
                    ? contractParams.unitName
                      ? contractParams.unitName
                      : '-'
                    : ''}
                </span>
              </Col>

              <Col span={2}>{this.$t('my.contract.responsible.person')}:</Col>
              <Col span={5}>
                <span title={contractParams.employeeId}>
                  {contractParams.contractName
                    ? contractParams.employee && contractParams.employeeId
                      ? contractParams.employee.fullName
                      : '-'
                    : ''}
                </span>
              </Col>
              <Col span={2}>{this.$t('budget.controlRule.effectiveDate')}:</Col>
              <Col span={5}>
                <span>
                  {contractParams.contractName
                    ? `${
                        contractParams.startDate
                          ? moment(new Date(contractParams.startDate)).format('YYYY-MM-DD')
                          : '-'
                      }
                       ~
                      ${
                        contractParams.endDate
                          ? moment(new Date(contractParams.endDate)).format('YYYY-MM-DD')
                          : '-'
                      }`
                    : ''}
                </span>
              </Col>
            </Row>
          </div>
        </Card>
        <Card title="付款信息">
          <Row>
            <Col span={12} push={12} className="header-tips" style={{ textAlign: 'right' }}>
              <Breadcrumb style={{ marginBottom: '10px' }}>
                <Breadcrumb.Item>
                  <span style={{ color: 'rgba(0, 0, 0, 0.60)' }}>{this.$t('common.amount')}:</span>&nbsp;
                  <span style={{ color: 'Green' }}>
                    {' '}
                    {contractParams.currency}&nbsp;{this.filterMoney(contractParams.amount)}
                  </span>
                </Breadcrumb.Item>
                <Breadcrumb.Item>
                  <span style={{ color: 'rgba(0, 0, 0, 0.60)' }}>
                    {this.$t('acp.function.amount')}
                  </span>
                  <span style={{ color: 'Green' }}>
                    {company.baseCurrency}&nbsp;{this.filterMoney(contractParams.functionAmount)}
                  </span>
                </Breadcrumb.Item>
              </Breadcrumb>
            </Col>
          </Row>
          <CustomTable
            ref={ref => {
              this.table = ref;
            }}
            url={`${config.contractUrl}/api/contract/line/herder/${contractId}`}
            showNumber
            pagination={{ pageSize: 5 }}
            columns={columns}
          />
        </Card>
        <Card
          style={{
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
            margin: '20px 0 40px',
          }}
        >
          <ApproveHistory type="801004" oid={contractParams.documentOid} />
        </Card>
        <Affix
          offsetBottom={0}
          className="bottom-bar bottom-bar-approve"
          style={{
            position: 'fixed',
            bottom: 0,
            width: '100%',
            height: '50px',
            boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.15)',
            background: '#fff',
            lineHeight: '50px',
            paddingLeft: '30px',
            zIndex: 1,
          }}
        >
          <Button onClick={this.onBack}>{this.$t('budgetJournal.return')}</Button>
        </Affix>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}
export default connect(mapStateToProps)(NewContractDetails);
