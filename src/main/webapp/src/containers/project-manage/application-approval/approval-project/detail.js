import React from 'react';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import { Button, Spin } from 'antd';
import ApproveBar from 'components/Widget/Template/approve-bar';
import ProjectDetailsCommon from '../../my-project-apply/project-apply-details/project-details-common';

@connect()
class ApprovalDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      dLoading: false,
      headerData: {},
      passLoading: false,
      // getLoading: !! this.props.match.params.id,
      getLoading: false,
    };
  }

  //取消
  onCancel = () => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/approval-management/approval-project/approval-project`,
      })
    );
  };

  render() {
    const { loading, dLoading, headerData, getLoading } = this.state;
    const match = {
      params: {
        id: this.props.match.params.id,
      },
    };
    return (
      <div
        className="contract-detail"
        style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)', paddingBottom: 100 }}
      >
        {getLoading ? <Spin /> : <ProjectDetailsCommon match={match} readOnly />}
        {this.props.match.params.status === 'unapproved' ? (
          <div className="bottom-bar bottom-bar-approve">
            <ApproveBar
              passLoading={loading}
              style={{ paddingLeft: 20 }}
              backUrl={'/approval-management/approval-project/approval-project'}
              rejectLoading={dLoading}
              documentType={801011}
              documentOid={this.props.match.params.oId}
            />
          </div>
        ) : (
          <div className="bottom-bar bottom-bar-approve">
            <div style={{ lineHeight: '50px', paddingLeft: 20 }}>
              <Button loading={loading} onClick={this.onCancel} className="back-btn">
                {this.$t({ id: 'common.back' } /*返回*/)}
              </Button>
            </div>
          </div>
        )}
      </div>
    );
  }
}

export default ApprovalDetail;
