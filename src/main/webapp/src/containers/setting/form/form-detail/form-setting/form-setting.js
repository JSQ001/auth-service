import React from 'react';
import { connect } from 'dva';

import { Tabs } from 'antd';
const TabPane = Tabs.TabPane;
import HuilianyiManagement from 'containers/setting/form/form-detail/form-setting/huilianyi-management';
import SupplierManagement from 'containers/setting/form/form-detail/form-setting/supplier-management';
import TravelItinerarySetting from 'containers/setting/form/form-detail/form-setting/travel-itinerary-setting';
import formService from 'containers/setting/form/form.service';
import 'styles/setting/form/form-detail.scss';
import PropTypes from 'prop-types';

class FormSetting extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      mode: 'left', //切换面板的排列方式
      data: [],
      activeKey: 'SupplierManagement',
      propertyList: [], //接口获取的数据propertyList
    };
  }

  componentWillMount() {
    const { formOid } = this.props;
    this.getFields(formOid);
    this.getPropertyList(formOid);
  }

  //获取表单字段
  getFields = formOid => {
    let field = [];
    formService.getFormField(formOid).then(res => {
      if (res.status === 200) {
        field.controlFieldsHotel = JSON.parse(res.data.controlFieldsHotel);
        field.controlFieldsFlight = JSON.parse(res.data.controlFieldsFlight);
        field.manageType = JSON.parse(res.data.manageType);
        field.controlFieldsTrain = JSON.parse(res.data.controlFieldsTrain);
        field.costCenterCustom =
          res.data.costCenterCustom === '' ? {} : JSON.parse(res.data.costCenterCustom);
        this.setState({
          data: field,
        });
      }
    });
  };
  //获取propertyList
  getPropertyList = formOid => {
    formService.getPropertyList(formOid).then(res => {
      if (res.status === 200) {
        this.setState({
          propertyList: res.data,
        });
      }
    });
  };
  //切换tab按钮
  isEditingChange = activeKey => {
    this.setState({
      activeKey: activeKey,
      isTabing: true,
    });
  };

  handleSave = param => {
    if (param) {
      this.props.handlePageJump(true);
    }
  };

  render() {
    const { mode, data, activeKey, propertyList, isTabing } = this.state;
    const { formOid } = this.props;
    let params = {
      formOid,
      activeKey,
    };
    return (
      <div className="form-setting">
        <Tabs activeKey={activeKey} tabPosition={mode} onChange={this.isEditingChange}>
          <TabPane tab={this.$t('form.setting.supplier') /*供应商管控*/} key="SupplierManagement">
            {data.manageType && (
              <SupplierManagement
                data={data}
                params={params}
                propertyList={propertyList}
                saveHandle={this.handleSave}
              />
            )}
          </TabPane>
          <TabPane tab={this.$t('form.setting.huilianyi') /*融智汇管控*/} key="HuilianyiManagement">
            {data.manageType && (
              <HuilianyiManagement
                data={data}
                params={params}
                propertyList={propertyList}
                saveHandle={this.handleSave}
              />
            )}
          </TabPane>
          <TabPane
            tab={this.$t('form.setting.travel.form') /*行程表单设置*/}
            key="TravelItinerarySetting"
          >
            {data.manageType && (
              <TravelItinerarySetting
                data={data}
                params={params}
                propertyList={propertyList}
                saveHandle={this.handleSave}
              />
            )}
          </TabPane>
        </Tabs>
      </div>
    );
  }
}

FormSetting.childContextTypes = {
  saveHandle: PropTypes.func,
};

FormSetting.contextTypes = {
  router: PropTypes.object,
};

function mapStateToProps(state) {
  return {};
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(FormSetting);
