import React, { Component } from 'react';
import ReimburseCommon from './reimburse-common';

class ReimburseAudit extends Component {
  render() {
    const {
      match: { params },
    } = this.props;
    const { documentId, id } = params;

    return <ReimburseCommon documentId={documentId} id={id} isCreate={false} />;
  }
}

export default ReimburseAudit;
