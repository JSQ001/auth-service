import React from 'react';
import { message } from 'antd';
import { connect } from 'dva';
import moment from 'moment';
import MaintenanceDetailApplicationInformation from './maintenance-detail-application-information';
import MaintenanceeDetailForm from './maintenance-detail-form';
import maintenanceService from './account-open-maintenance.service';

class AccountOpenMaintenanceDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      applicationInformation: {},
      formData: {},
    };
  }

  componentDidMount() {
    const { match } = this.props;
    this.getAccountOpenMaintenanceDetail(match.params.id);
  }

  /**
   * 根据ID查询账户开户维护详情
   */
  getAccountOpenMaintenanceDetail = id => {
    maintenanceService
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

  /**
   * 设置申请信息
   */
  setApplicationInformation = data => {
    return {
      documentNumber: data.documentNumber,
      companyName: data.companyName,
      departmentName: data.departmentName,
      openBankName: data.openBankName,
      branchBankName: data.branchBankName,
      currencyCode: data.currencyCode,
      requisitionDate: moment(new Date(data.requisitionDate)).format('YYYY-MM-DD'),
      remarks: data.remarks,
      employeeName: data.employeeName,
    };
  };

  render() {
    const { applicationInformation, formData } = this.state;
    return (
      <div>
        <MaintenanceDetailApplicationInformation applicationInformation={applicationInformation} />
        <MaintenanceeDetailForm formData={formData} from="openMaintenanceList" />
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

export default connect(mapStateToProps)(AccountOpenMaintenanceDetail);
