import React from 'react';
import { message } from 'antd';
import { connect } from 'dva';
// import moment from 'moment';
// import MaintenanceDetailApplicationInformation from './account-open-entry-info';
// import MaintenanceeDetailForm from './maintenance-detail-form';
import accountOpenEntryService from './account-open-entry.service';
/* eslint-disable */

class AccountOpenEntryDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      applicationInformation: {},
      isNew: true, // 是否为新建单据
      formData: {},
    };
  }

  componentDidMount() {
    const { match } = this.props;
    if (match.params.id === 'new') {
      console.log('new');
    } else {
      this.setState({
        isNew: false,
      });
      //   this.getAccountOpenMaintenanceDetail(match.params.id);
    }
  }

  /**
   * 根据ID查询账户开户维护详情
   */
  getAccountOpenMaintenanceDetail = id => {
    accountOpenEntryService
      .getAccountOpenMaintenanceDetail(id)
      .then(res => {
        this.setState({
          applicationInformation: this.setApplicationInformation(res.data),
          formData: res.data,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  render() {
    // const { applicationInformation, formData } = this.state;
    return (
      <div>
        heool
        {/* <MaintenanceDetailApplicationInformation /> */}
        {/* <MaintenanceeDetailForm formData={formData} from="openMaintenanceList" /> */}
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(AccountOpenEntryDetail);
