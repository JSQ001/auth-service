import React from 'react';
import { Button, Icon } from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import service from './services';
import BasicInfo from 'components/Widget/basic-info';

class details extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      detailData: {},
      detailList: [
        {
          type: 'input',
          id: 'clientAcc',
          isRequired: true,
          label: '客户编号',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientTaxName',
          isRequired: true,
          label: '客户名称',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientTaxTypeName',
          isRequired: true,
          label: '客户类型',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'documentTypeName',
          isRequired: true,
          label: '证件类型',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'documentNum',
          isRequired: true,
          label: '证件号码',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'contacts',
          isRequired: true,
          label: '联系人',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'contactsPhone',
          isRequired: true,
          label: '联系人电话',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'contactsAdd',
          isRequired: true,
          label: '联系人地址',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'taxpayerName',
          isRequired: true,
          label: '纳税人名称',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientTaxNum',
          isRequired: true,
          label: '纳税人识别号',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'taxpayerTypeName',
          isRequired: true,
          label: '纳税人资质',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientTaxAdd',
          isRequired: true,
          label: '地址',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientTaxTel',
          isRequired: true,
          label: '电话',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientTaxBank',
          isRequired: true,
          label: '开户行',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientTaxAcc',
          isRequired: true,
          label: '银行账号',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientEmail',
          isRequired: true,
          label: '电子邮箱',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientPhone',
          isRequired: true,
          label: '手机号码',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'mailAddress',
          isRequired: true,
          label: '邮寄地址',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'addressee',
          isRequired: true,
          label: '收件人',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'addresseePhone',
          isRequired: true,
          label: '收件人电话',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'org',
          isRequired: true,
          label: '归属机构',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'project',
          isRequired: true,
          label: '项目',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'importFlag',
          isRequired: true,
          label: '数据来源',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'importSysName',
          isRequired: true,
          label: '来源系统',
          colSpan: 6,
        },
      ],
    };
  }

  componentDidMount() {
    //获取详细信息
    this.getPeriodInfo();
  }
  //返回
  onCancel = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/inter-management/cust-inter/customer-inter',
      })
    );
  };

  getPeriodInfo = () => {
    service
      .getInterfaceDetail(this.props.match.params.id)
      .then(res => {
        this.setState({ detailData: res.data });
      })
      .catch(e => {});
  };

  render() {
    return (
      <div>
        <BasicInfo
          infoList={this.state.detailList}
          infoData={this.state.detailData}
          isHideEditBtn={true}
        />
        {/* <Button style={{ marginLeft: '20px' }} onClick={this.onCancel}>
                    返 回
                </Button> */}
        <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.onCancel}>
          <Icon type="rollback" style={{ margin: '10px 0' }} />
          {this.$t('common.back')}
        </a>
      </div>
    );
  }
}

// export default details
function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(details);
