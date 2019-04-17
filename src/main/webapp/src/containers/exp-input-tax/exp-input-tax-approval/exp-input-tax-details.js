import React, { Component } from 'react';
import DocumentBasicInfo from 'widget/Template/document-basic-info';
import ApproveHistory from 'widget/Template/approve-history-work-flow';
import CustomTable from 'widget/custom-table';
import { connect } from 'dva';
import { Spin, Card, Affix, Row, Col, Button, Popover, DatePicker, message } from 'antd';
import moment from 'moment';
import ApprovalBarDWY from './approval-bar-dwy';
import service from '../service';
import approvalService from './approval-page-service';

class ExpInputTaxDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      headerInfo: {}, // 部分头信息数据
      docHeaderInfo: {}, // 头信息数据
      columns: [
        {
          title: this.$t('tax.instructions') /*说明*/,
          dataIndex: 'num',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('tax.proof.of.date.of') /*凭证日期*/,
          dataIndex: 'transferDate',
          width: 150,
          render: text => {
            return (
              <Popover content={moment(text).format('YYYY-MM-DD')}>
                <span>{moment(text).format('YYYY-MM-DD')}</span>
              </Popover>
            );
          },
        },
        {
          title: this.$t('acp.company'),
          dataIndex: 'companyName',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('structure.responsibilityCenter'),
          dataIndex: 'responsibilityCenterName',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('accounting.subject'),
          dataIndex: 'subjectName',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('common.currency'),
          dataIndex: 'currencyCode',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('tax.the.original.currency.debit') /*原币借方*/,
          dataIndex: 'originalCurrencyDebit',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('tax.the.original.currency.credit') /*原币贷方*/,
          dataIndex: 'originalCurrencyCredit',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('tax.local.currency.debit') /*本币借方*/,
          dataIndex: 'localCurrencyDebit',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('tax.the.currency.credit') /*本币贷方*/,
          dataIndex: 'localCurrencyCredit',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('tax.subject.section.1') /*科目段1*/,
          dataIndex: 'subjectSection1',
          width: 120,
          tooltips: true,
        },
      ],
      id: props.match.params.id,
      oid: props.match.params.oid,
      time: null, // 默认时间
      passLoading: false,
      refuseLoading: false,
    };
  }

  componentDidMount = () => {
    this.getHeaderList();
  };

  // 转换业务大类type
  transferTypeValue = (type, rate) => {
    let newType = '';
    switch (type) {
      case 'FOR_SALE':
        newType = `${this.$t('tax.as.sale')}-${rate}%`;
        break;
      case 'ALL_TRANSFER':
        newType = `${this.$t('tax.full.roll.out')}`;
        break;
      case 'PART_TRANSFER':
        newType = `${this.$t('tax.scale.out')}-${rate}%`;
        break;
      default:
        break;
    }
    return newType;
  };

  // 转换status
  transferStatus = status => {
    let newStatus = '';
    switch (status) {
      case '1002':
        newStatus = 3002;
        break;
      case '1004':
        newStatus = 1006;
        break;
      case '1005':
        newStatus = 1007;
        break;
      default:
        newStatus = Number(status);
        break;
    }
    return newStatus;
  };

  // 获取头信息
  getHeaderList = () => {
    const { match } = this.props;
    service
      .getBusinessReceiptHeadValue(match.params.id)
      .then(res => {
        const newType = this.transferTypeValue(
          String(res.data.transferType),
          res.data.transferProportion
        );
        const newStatus = this.transferStatus(String(res.data.status));
        const headerInfo = {
          createdDate: res.data.createdDate,
          formName: this.$t('tax.business.receipt.input.tax'),
          currencyCode: res.data.currencyCode,
          statusCode: newStatus,
          attachments: res.data.attachments,
          businessCode: res.data.documentNumber,
          createByName: res.data.fullName,
          infoList: [
            { label: this.$t('common.applicant'), value: res.data.fullName },
            { label: this.$t('acp.company'), value: res.data.companyName },
            { label: this.$t('common.department'), value: res.data.departmentName },
            { label: this.$t('tax.business.categories'), value: newType },
            { label: this.$t('tax.use.type'), value: res.data.useTypeName },
          ],
          remark: res.data.description,
          totalAmount: res.data.amount,
        };
        this.setState({
          headerInfo,
          docHeaderInfo: { ...res.data },
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 创建凭证 ------调用核算模块接口，生成凭证信息。
  createCredentials = () => {
    let { time } = this.state;
    if (!time) time = moment(new Date()).format('YYYY-MM-DD');
    else time = moment(time).format('YYYY-MM-DD');
  };

  // 默认时间
  onChangeTime = time => {
    this.setState({
      time,
    });
  };

  // 通过
  passApproval = (error, value) => {
    if (error) return;
    console.log(value);
    this.setState({ passLoading: true });
    // const { headerInfo } = this.state;
    // approvalService
    //   .passCurApproval(headerInfo.id,value)
    //   .then(res => {
    //     if (res) message.success('通过');
    //     this.setState({ passLoading: false });
    //   })
    //   .catch(err => {
    //     message.error(err.response.data.message);
    //   });
  };

  // 驳回
  refuseApproval = (error, value) => {
    if (error) return;
    const { headerInfo } = this.state;
    this.setState({ refuseLoading: true });
    approvalService
      .refuseCurApproval(headerInfo.id, value)
      .then(res => {
        if (res) message.success(this.$t('constants.documentStatus.has.been.rejected'));
        this.setState({ refuseLoading: false });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  render() {
    const { headerInfo, columns, id, oid, docHeaderInfo, passLoading, refuseLoading } = this.state;
    // const { match } = this.props;

    return (
      <div>
        <Spin spinning={false}>
          <Card style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)', marginBottom: '10px' }}>
            <DocumentBasicInfo params={headerInfo} />
          </Card>
          <Card title={this.$t('tax.credential.info')}>
            <div style={{ marginBottom: '10px' }}>
              <Button
                type="primary"
                onClick={this.createCredentials}
                style={{ marginRight: '20px' }}
              >
                {/* 创建凭证 */}
                {this.$t('tax.credential.create')}
              </Button>
              <DatePicker
                onChange={this.onChangeTime}
                placeholder={this.$t('tax.curDate.default.enChange')} // "默认当前日期，可改"
                defaultValue={moment(new Date())}
              />
            </div>
            <CustomTable columns={columns} />
          </Card>
        </Spin>
        <div style={{ margin: '20px 0 80px', boxShadow: '0 2px 8px rgba(0, 0, 0, 0.2)' }}>
          <ApproveHistory
            type="9090"
            oid={oid || ''}
            headerId={id}
            // oid={headerInfo.documentOid || ''}
            // headerId={headerInfo.id}
          />
        </div>
        <Affix offsetBottom={0} className="tax-approval-bottom">
          <Row>
            <Col span={21}>
              <ApprovalBarDWY
                backUrl="/exp-input-tax/exp-input-tax/approval"
                onPass={this.passApproval}
                onRefuse={this.refuseApproval}
                passLoading={passLoading}
                refuseLoading={refuseLoading}
                flag={
                  String(docHeaderInfo.status) === '1002' || String(docHeaderInfo.status) === '1005'
                    ? 'Y'
                    : 'N'
                }
              />
            </Col>
          </Row>
        </Affix>
      </div>
    );
  }
}

export default connect()(ExpInputTaxDetails);
