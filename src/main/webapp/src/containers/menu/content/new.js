/**
 * create by fudebao on 2019-02-25
 */
import React, { Component } from 'react';
import { Form, Input, Button, message, InputNumber, Icon, Select } from 'antd';
import service from '../service';

import icons from '../../../assets/icons';

const FormItem = Form.Item;

class NewContent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
    };
    this.formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 12,
      },
    };
  }

  //取消
  handleCancel = () => {
    this.props.onClose && this.props.onClose();
  };

  //提交
  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) return;

      const { params } = this.props;
      this.setState({ loading: true });
      const method = !params.id ? service.addContent : service.editContent;
      method({ ...params, ...values })
        .then(() => {
          message.success(params.id ? '新增成功！' : '编辑成功！');
          this.setState({ loading: false });
          this.props.onClose && this.props.onClose(true);
        })
        .catch(err => {
          message.error(err.response.data.message);
          this.setState({ loading: false });
        });
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const { loading } = this.state;
    const { params } = this.props;

    return (
      <Form onSubmit={this.handleSubmit}>
        <FormItem {...this.formItemLayout} label="名称">
          {getFieldDecorator('contentName', {
            rules: [
              {
                required: true,
                message: '请输入',
              },
            ],
            initialValue: params.contentName,
          })(<Input />)}
        </FormItem>
        {/* <FormItem
          {...this.formItemLayout}
          label="路由"
        >
          {getFieldDecorator("contentRouter", {
            rules: [{
              required: true, message: '请输入'
            }],
            initialValue: params.contentRouter
          })(
            <Input />
          )}
        </FormItem> */}
        <Form.Item {...this.formItemLayout} label="图标">
          {getFieldDecorator('icon', {
            rules: [
              {
                required: true,
                message: '请选择',
              },
            ],
            initialValue: params.icon,
          })(
            <Select allowClear optionLabelProp="value">
              {icons.map(item => {
                return (
                  <Select.Option key={item} value={item}>
                    <Icon type={item} />
                  </Select.Option>
                );
              })}
            </Select>
          )}
        </Form.Item>
        <FormItem {...this.formItemLayout} label="序号">
          {getFieldDecorator('sequenceNumber', {
            initialValue: params.sequenceNumber,
          })(<InputNumber />)}
        </FormItem>
        <div className="slide-footer">
          <Button type="primary" htmlType="submit" loading={loading} style={{ margin: '0 20px' }}>
            确定
          </Button>
          <Button onClick={this.handleCancel}>取消</Button>
        </div>
      </Form>
    );
  }
}

export default Form.create()(NewContent);
