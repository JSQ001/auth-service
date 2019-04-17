import React from 'react';
import { Card, Row, Col } from 'antd';
import 'styles/components/basic-info.scss';

class BasicInfo extends React.Component {
  render() {
    const { infoList, infoData } = this.props;
    return (
      <div className="basic-info">
        <Card title={'责任中心配置'}>
          <Row>
            {infoList.map((item, index) => {
              return (
                <Col span={8} style={{ marginBottom: '15px', paddingRight: '5px' }} key={index}>
                  <div style={{ color: '#989898' }}>{item.label}</div>
                  {infoData && infoData[item.id]}
                </Col>
              );
            })}
          </Row>
        </Card>
      </div>
    );
  }
}

export default BasicInfo;
