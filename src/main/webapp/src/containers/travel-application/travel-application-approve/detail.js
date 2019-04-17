import React from 'react';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import { Button, message, Spin } from 'antd';
import TravelCommon from '../travel-application/details-common-readonly';
import ApproveBar from 'components/Widget/Template/approve-bar';
import service from './service';

@connect()
class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      dLoading: false,
      headerData: {},
      passLoading: false,
      getLoading: true,
    };
  }

  componentDidMount() {
    this.getInfo();
  }

  //获取差旅申请单头信息
  getInfo = () => {
    service
      .getApplicationDetail(this.props.match.params.id)
      .then(res => {
        this.setState({ headerData: res.data, getLoading: false });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //取消
  onCancel = () => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/approval-management/travel-application-approve/travel-application-approve`,
      })
    );
  };

  render() {
    const { loading, dLoading, headerData, getLoading } = this.state;

    return (
      <div
        className="contract-detail"
        style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)', paddingBottom: 100 }}
      >
        {getLoading ? <Spin /> : <TravelCommon headerData={headerData} />}
        {this.props.match.params.status === 'unapproved' ? (
          <div className="bottom-bar bottom-bar-approve">
            <ApproveBar
              passLoading={loading}
              style={{ paddingLeft: 20 }}
              backUrl={'/approval-management/travel-application-approve/travel-application-approve'}
              rejectLoading={dLoading}
              documentType={801010}
              documentOid={headerData.documentOid}
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

export default Detail;
