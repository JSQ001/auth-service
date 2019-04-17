import React, { Component } from 'react';
import { Form, Row, Col, Input, Button, Spin, message } from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import PropTypes from 'prop-types';
import service from './service';
import 'styles/workbench/my-workbench/workbench-approve-bar.scss';

class WorkBenchApproveBar extends Component {
  constructor(props) {
    super(props);
    this.state = {
      buttonList: [],
      loading: [],
      buttonLoading: true,
      statusLoading: true,
      automaticFlag: true,
    };
  }

  componentDidMount() {
    this.getButton();
    this.getStatus();
  }
  // 获取阅单状态
  getStatus = () => {
    service
      .getDocumentStatus()
      .then(res => {
        this.setState({ automaticFlag: res.data.automaticFlag, statusLoading: false });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 获取按钮
  getButton = () => {
    const { id, holdFlag } = this.props;
    service
      .getButtonBar({ id })
      .then(res => {
        const button =
          res.data &&
          res.data.map(item => {
            if (item.label.includes('驳回')) {
              return { ...item, className: 'reject-btn', type: 'default' };
            }
            return item;
          });
        const buttonList = [
          ...button,
          { label: '暂挂单据', id: '111', onClick: this.cancelPending },
          { label: '返回', id: '123', type: 'default', onClick: this.goBack },
          { label: false, id: '133', onClick: this.changeStatus },
        ];
        this.setState({ buttonList, buttonLoading: false });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 返回
  goBack = () => {
    const { dispatch, backUrl } = this.props;
    if (backUrl) {
      dispatch(
        routerRedux.replace({
          pathname: backUrl,
        })
      );
    }
  };

  // 按钮点击操作
  onSubmit = (params, index) => {
    const {
      form: { validateFields },
      id,
    } = this.props;
    validateFields((errors, values) => {
      if (!errors) {
        const data = {
          ...values,
          actionId: params.id,
          id,
        };
        this.buttonOperation(data, index);
      }
    });
  };

  // 按钮操作
  buttonOperation = (data, index) => {
    const { loading } = this.state;
    loading[index] = true;
    this.setState({ loading });
    service
      .operateButton(data)
      .then(() => {
        const {
          form: { setFieldsValue },
        } = this.props;
        message.success('操作成功');
        loading[index] = false;
        this.setState({ loading });
        this.goBack();
        setFieldsValue({ operatorText: undefined });
      })
      .catch(err => {
        message.error(err.response.data.message);
        loading[index] = false;
        this.setState({ loading });
      });
  };

  // 改变阅单
  changeStatus = (item, index) => {
    const { loading, automaticFlag } = this.state;
    loading[index] = true;
    this.setState({ loading });
    service
      .changeDocumentStatus(!automaticFlag)
      .then(() => {
        message.success('操作成功');
        loading[index] = false;
        this.setState({ automaticFlag: !automaticFlag, loading });
      })
      .catch(err => {
        message.error(err.response.data.message);
        loading[index] = false;
        this.setState({ loading });
      });
  };

  // 暂挂
  cancelPending = () => {
    const {
      form: { validateFields },
      id,
    } = this.props;
    validateFields((errors, values) => {
      if (!errors) {
        const data = {
          ...values,
          id,
        };
        service
          .hold(data)
          .then(res => {
            console.log(res);
            if (res.data) {
              const { dispatch } = this.props;
              dispatch(
                routerRedux.push({
                  pathname: `${res.data.pageContent}/${res.data.id}/${res.data.documentId}`,
                })
              );
            } else {
              this.goBack();
            }
            message.success('操作成功');
          })
          .catch(err => {
            message.error('操作失败！');
          });
      }
    });
  };

  render() {
    const { buttonList, loading, buttonLoading, statusLoading, automaticFlag } = this.state;
    const {
      form: { getFieldDecorator },
    } = this.props;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 18 },
    };

    return (
      <Form layout="vertical" className="workbench-approve-bar">
        <Row>
          <Col span={8} className="suggestion-input">
            <Form.Item label="处理意见：" {...formItemLayout}>
              {getFieldDecorator('operatorText', {
                rules: [
                  {
                    max: 200,
                    message: '最多输入200个字符',
                  },
                ],
              })(<Input placeholder="请输入，最多200个字符" />)}
            </Form.Item>
          </Col>
          <Col span={16} className="btn-bar">
            <Spin spinning={buttonLoading && statusLoading}>
              {buttonList.map((item, index) => {
                return (
                  <Button
                    key={item.id}
                    type={item.type || 'primary'}
                    onClick={() =>
                      item.onClick ? item.onClick(item, index) : this.onSubmit(item, index)
                    }
                    className={item.className}
                    loading={loading[index]}
                  >
                    {item.label || (automaticFlag ? '停止自动阅单' : '自动阅单')}
                  </Button>
                );
              })}
            </Spin>
          </Col>
        </Row>
      </Form>
    );
  }
}

WorkBenchApproveBar.propTypes = {
  backUrl: PropTypes.string, // 点击"返回"按钮跳转到的页面
};

WorkBenchApproveBar.defaultProps = {
  backUrl: '',
};

const WorkBenchApproveBars = Form.create()(WorkBenchApproveBar);

export default connect()(WorkBenchApproveBars);
