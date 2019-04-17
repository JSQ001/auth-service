import React, { Component } from 'react';
import { Button } from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import ProjectDetailsCommon from '../../my-project-apply/project-apply-details/project-details-common';

class ProjectApplyDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
    };
  }

  //  返回
  onCancel = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/project-manage/project-change/project-change`,
      })
    );
  };

  render() {
    const { match } = this.props;
    const { loading } = this.state;
    return (
      <div style={{ paddingBottom: '70px' }}>
        <ProjectDetailsCommon match={match} readOnly onlyChange />

        <div className="bottom-bar bottom-bar-approve">
          <div style={{ lineHeight: '50px', paddingLeft: 20 }}>
            <Button loading={loading} onClick={this.onCancel} className="back-btn">
              {this.$t({ id: 'common.back' } /*返回*/)}
            </Button>
          </div>
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    organization: state.user.organization || {},
  };
}

export default connect(mapStateToProps)(ProjectApplyDetails);
