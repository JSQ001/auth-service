import React from 'react';
import { Row, Col, Spin, Card, Divider, Popconfirm } from 'antd';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import RequestParams from './request-params';
import ResponseParams from './response-params';
import service from './interface.service';
import interfaceService from './interface.service';
import './index.less';

const Description = ({ term, children, span = 6 }) => (
  <Col span={span}>
    <div style={{ display: 'table' }}>
      <div
        className="term"
        style={{
          display: 'table-cell',
          marginRight: '8px',
          paddingBottom: '8px',
          whiteSpace: 'nowrap',
          lineHeight: '20px',
        }}
      >
        {term}
      </div>
      <div
        style={{
          display: 'table-cell',
          paddingBottom: '8px',
          width: '100%',
          lineHeight: '20px',
          fontWeight: 'bold',
          fontSize: 16,
        }}
      >
        {children}
      </div>
    </div>
  </Col>
);

class InterfaceDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      confirmLoading: false,
      data: {},
      loading: true,
    };
  }

  componentDidMount() {
    this.getInterfaceDetail();
  }

  getInterfaceDetail = () => {
    const {
      match: {
        params: { id },
      },
    } = this.props;
    service.getInterfaceById(id).then(res => {
      this.setState({ data: res, loading: false });
    });
  };

  renderItem = (laebl, value) => {
    return (
      <Row gutter={10}>
        <Col style={{ textAlign: 'right' }} span={8}>
          {laebl}:
        </Col>
        <Col span={16}>{value}</Col>
      </Row>
    );
  };

  back = () => {
    const {
      dispatch,
      match: {
        params: { appId },
      },
    } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/setting/interface/interface-list/${appId}`,
      })
    );
  };

  render() {
    const { loading, data } = this.state;
    const {
      match: {
        params: { id },
      },
    } = this.props;

    const operation = (
      <span>
        <a href="#">测试</a>
        <Divider type="vertical" />
        <Popconfirm
          title="确定删除？"
          onConfirm={() => {
            interfaceService
              .delete(value)
              .then(() => {
                message.success('删除成功！');
                this.back();
              })
              .catch(err => {
                message.error(err.response.data.message);
              });
          }}
          okText="确定"
          cancelText="取消"
        >
          <a>删除</a>
        </Popconfirm>
        <Divider type="vertical" />
        <a onClick={this.back}>返回</a>
      </span>
    );

    return (
      <Spin spinning={loading}>
        <Card
          title="基础信息"
          style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)' }}
          extra={operation}
        >
          <Row className="interface">
            <Description span={24} term="接口名称">
              {data.interfaceName}
            </Description>
            <Description span={24} term="url">
              {data.reqUrl}
            </Description>
            <Description term="请求方式">{data.requestMethod}</Description>
            <Description term="请求协议">{data.requestProtocol}</Description>
            <Description term="请求格式">{data.requestFormat}</Description>
            <Description term="响应格式">{data.responseFormat}</Description>
            <Description span={24} term="备注">
              {data.remark}
            </Description>
          </Row>
        </Card>
        <Card
          title="请求参数"
          style={{ marginTop: 10, boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)' }}
        >
          <RequestParams id={id} />
        </Card>
        <Card
          title="响应参数"
          style={{ marginTop: 10, boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)' }}
        >
          <ResponseParams interfaceInfo={data} id={id} />
        </Card>
      </Spin>
    );
  }
}

export default connect()(InterfaceDetail);
