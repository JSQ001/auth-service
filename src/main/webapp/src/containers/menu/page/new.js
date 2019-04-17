import React, { Component } from 'react';
import { Form, Input, Button, message, Tooltip, Icon } from 'antd';
import service from '../service';

const FormItem = Form.Item;

class NewPage extends Component {
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
      const method = !params.id ? service.addPage : service.editPage;
      method({ ...params, ...values })
        .then(() => {
          message.success(!params.id ? '新增成功！' : '编辑成功！');
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
          {getFieldDecorator('pageName', {
            rules: [
              {
                required: true,
                message: '请输入',
              },
            ],
            initialValue: params.pageName,
          })(<Input />)}
        </FormItem>
        <FormItem
          {...this.formItemLayout}
          label={
            <span>
              url&nbsp;
              <Tooltip title="必须以/开头">
                <Icon type="question-circle-o" />
              </Tooltip>
            </span>
          }
        >
          {getFieldDecorator('fullUrl', {
            rules: [
              {
                required: true,
                message: '请输入',
              },
              {
                validator: (rule, value, callback) => {
                  if (value[0] !== '/') {
                    callback('必须以/开头');
                    return;
                  }
                  callback();
                },
              },
            ],
            initialValue: params.fullUrl,
          })(<Input />)}
        </FormItem>
        <FormItem
          {...this.formItemLayout}
          label={
            <span>
              路由&nbsp;
              <Tooltip title="必须以/开头">
                <Icon type="question-circle-o" />
              </Tooltip>
            </span>
          }
        >
          {getFieldDecorator('fullRouter', {
            rules: [
              {
                required: true,
                message: '请输入',
              },
              {
                validator: (rule, value, callback) => {
                  if (value[0] !== '/') {
                    callback('必须以/开头');
                    return;
                  }
                  callback();
                },
              },
            ],
            initialValue: params.fullRouter,
          })(<Input />)}
        </FormItem>
        <FormItem {...this.formItemLayout} label="组件">
          {getFieldDecorator('filePath', {
            rules: [
              {
                required: true,
                message: '请输入',
              },
            ],
            initialValue: params.filePath,
          })(<Input />)}
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

export default Form.create()(NewPage);
