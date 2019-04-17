import React from 'react';
import { Card, Row, Col } from 'antd';
import { connect } from 'dva';

// 申请信息组件
class maintenanceDetailApplicationInformation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    const { applicationInformation } = this.props;
    return (
      <div>
        <Card
          style={{
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
            marginRight: 15,
            marginLeft: 15,
          }}
        >
          <div style={{ borderBottom: '1px solid rgb(236, 236, 236)', marginTop: '-20px' }}>
            <h3>申请信息:</h3>
          </div>
          <Row style={{ marginTop: '15px' }}>
            <Col span={8}>申请单号：{applicationInformation.documentNumber}</Col>
            <Col span={6}>申请公司：{applicationInformation.companyName}</Col>
            <Col span={4}>申请部门：{applicationInformation.departmentName}</Col>
            <Col span={6}>开户银行：{applicationInformation.openBankName}</Col>
          </Row>
          <Row style={{ marginTop: '15px' }}>
            <Col span={8}>分支行信息：{applicationInformation.branchBankName}</Col>
            <Col span={6}>币种：{applicationInformation.currencyCode}</Col>
            <Col span={8}>申请日期：{applicationInformation.requisitionDate}</Col>
          </Row>
          <Row style={{ marginTop: '15px' }}>
            <Col span={8}>备注：{applicationInformation.remarks}</Col>
            <Col span={4}>申请人：{applicationInformation.employeeName}</Col>
          </Row>
        </Card>
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

export default connect(mapStateToProps)(maintenanceDetailApplicationInformation);
