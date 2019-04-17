import React, { Component } from 'react';
import { message } from 'antd';
import { connect } from 'dva';
import DocumentBasicInfo from 'components/Widget/Template/document-basic-info';
import ApproveHistory from 'widget/Template/approve-history-work-flow';
import config from 'config';
import WorkbenchApproveBar from './workbench-approve-bar';
import CredentialTable from './credential-info';
import service from './service';
import 'styles/workbench/my-workbench/my-workbench-detail.scss';

class MyWorkBenchDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      historyOid: undefined,
      documentId: props.documentId,
      id: props.id,
      tableParams: {},
      deleteParams: {},
    };
  }

  componentDidMount() {
    this.getBase();
    this.setParams();
  }

  // 设置参数
  setParams = () => {
    const {
      company: { tenantId },
    } = this.props;
    const { documentId } = this.state;
    this.setState({
      tableParams: {
        tenantId,
        transactionHeaderId: documentId,
        sourceTransactionType: 'EXP_REPORT',
      },
      deleteParams: {
        transactionHeaderId: documentId,
      },
    });
  };

  // 获取单据基本信息
  getBase = () => {
    const { documentId } = this.state;
    service
      .getBase({ expenseReportId: documentId })
      .then(res => {
        const { data } = res;
        const infoList = [
          { label: '申请人', value: data.applicantName },
          { label: '公司', value: data.companyName },
          { label: '部门', value: data.departmentName },
          ...[data.contractNumber && { label: '合同', value: data.contractNumber, linkId: true }],
        ];
        const customList =
          data.expenseDimensions &&
          data.expenseDimensions.map(item => {
            return {
              label: item.name,
              value: item.valueName,
            };
          });
        this.setState({
          params: {
            businessCode: data.requisitionNumber,
            createdDate: data.createdDate,
            createByName: data.createdName,
            totalAmount: data.totalAmount,
            statusCode: data.status,
            formName: data.documentTypeName,
            remark: data.description,
            infoList,
            customList,
          },
          historyOid: data.documentOid,
        });
      })
      .catch(err => message.error(err.response.data.message));
  };

  render() {
    const { params, historyOid, id, tableParams, deleteParams, documentId } = this.state;
    const { isCreate, createUrl, deleteUrl } = this.props;

    return (
      <div className="workbench-detail">
        <div className="workbench-card workbench-basic">
          <DocumentBasicInfo params={params} />
        </div>

        <CredentialTable
          deleteParams={deleteParams}
          params={tableParams}
          isCreate={isCreate}
          url={`${config.accountingUrl}/api/accounting/gl/journal/lines/query/by/line`}
          queryMethod="post"
          createUrl={createUrl}
          deleteUrl={deleteUrl}
          documentId={documentId}
        />

        <div className="workbench-card">
          <ApproveHistory type="801001" oid={historyOid} />
        </div>

        <WorkbenchApproveBar id={id} backUrl="/workbench/my-workbench/my-workbench/WORKING" />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(MyWorkBenchDetail);
