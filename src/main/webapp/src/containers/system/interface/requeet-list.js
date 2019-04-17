import React, { Component } from 'react';
import { Modal, Form, Input, message } from 'antd';

import service from './interface.service';

class RequestList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      requestList: [],
    };
  }

  componentWillReceiveProps(nextProps) {
    const { visible } = this.props;
    if (!visible && nextProps.visible) {
      const { id } = nextProps;
      service
        .getRequestList(id)
        .then(res => {
          this.setState({ requestList: res });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  }

  cancel = () => {
    const { onCancel } = this.props;
    onCancel();
  };

  save = () => {
    const { form } = this.props;
    form.validateFields((err, values) => {
      if (err) return;
      const { onOk } = this.props;
      onOk(values);
    });
  };

  render() {
    const {
      visible,
      form: { getFieldDecorator },
    } = this.props;
    const { requestList } = this.state;
    const formItemLayout = {
      labelCol: {
        span: 6,
      },
      wrapperCol: {
        span: 12,
      },
    };
    return (
      <Modal title="请求参数" visible={visible} onOk={this.save} onCancel={this.cancel}>
        <Form>
          {requestList.map(item => (
            <Form.Item key={item.keyCode} {...formItemLayout} label={item.keyCode}>
              {getFieldDecorator(item.keyCode, {
                rules: [
                  {
                    required: item.requiredFlag,
                    message: '请输入',
                  },
                ],
              })(<Input />)}
            </Form.Item>
          ))}
        </Form>
      </Modal>
    );
  }
}

export default Form.create()(RequestList);
