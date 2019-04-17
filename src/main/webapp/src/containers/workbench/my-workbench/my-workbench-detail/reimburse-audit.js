import React, { Component } from 'react';
import config from 'config';
import ReimburseCommon from './reimburse-common';

class ReimburseAudit extends Component {
  render() {
    const {
      match: { params },
    } = this.props;
    const { documentId, id } = params;
    return (
      <ReimburseCommon
        documentId={documentId}
        id={id}
        isCreate
        key={id}
        createUrl={`${config.expenseUrl}/api/expense/report/create/accounting`}
        deleteUrl={`${config.accountingUrl}/api/accounting/gl/journal/lines/delete/exp/report`}
      />
    );
  }
}

export default ReimburseAudit;
