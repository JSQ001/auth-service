import React from 'react';
import { Form, Input, Select, message, Button, Tooltip, Icon } from 'antd';
import service from './interface.service';

const FormItem = Form.Item;

class NewInterface extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      confirmLoading: false,
    };
  }

  handleOk = () => {
    const { appId, params } = this.props;
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({ confirmLoading: true });
        if (params.id) {
          service
            .update({ ...params, ...values, appId })
            .then(() => {
              this.setState({ confirmLoading: false });
              message.success('更新成功！');
              this.handleCancel(true);
            })
            .catch(err => {
              this.setState({ confirmLoading: false });
              message.error(err.response.data.messages);
            });
        } else {
          service
            .add({ ...values, appId })
            .then(() => {
              this.setState({ confirmLoading: false });
              message.success('添加成功！');
              this.handleCancel(true);
            })
            .catch(err => {
              this.setState({ confirmLoading: false });
              message.error(err.response.data.messages);
            });
        }
      }
    });
  };

  handleCancel = flag => {
    this.props.onClose && this.props.onClose(flag);
  };

  render() {
    const {
      form: { getFieldDecorator },
      params,
    } = this.props;
    const { confirmLoading } = this.state;
    const formItemLayout = {
      labelCol: {
        span: 6,
      },
      wrapperCol: {
        span: 12,
      },
    };

    return (
      <Form>
        <FormItem {...formItemLayout} label="名称">
          {getFieldDecorator('interfaceName', {
            rules: [
              {
                required: true,
                message: '请输入',
              },
            ],
            initialValue: params.interfaceName,
          })(<Input />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={
            <span>
              url&nbsp;
              <Tooltip title="只需要精确到/api，前面的模块不用带上。">
                <Icon type="question-circle-o" />
              </Tooltip>
            </span>
          }
        >
          {getFieldDecorator('reqUrl', {
            rules: [
              {
                required: true,
                message: '请输入',
              },
            ],
            initialValue: params.reqUrl,
          })(<Input />)}
        </FormItem>
        <FormItem {...formItemLayout} label="请求方式">
          {getFieldDecorator('requestMethod', {
            rules: [
              {
                required: true,
                message: '请输入',
              },
            ],
            initialValue: params.requestMethod || 'get',
          })(
            <Select style={{ width: 120 }}>
              <Select.Option value="get">GET</Select.Option>
              <Select.Option value="post">POST</Select.Option>
              <Select.Option value="put">PUT</Select.Option>
              <Select.Option value="delete">DELETE</Select.Option>
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label="请求协议">
          {getFieldDecorator('requestProtocol', {
            rules: [
              {
                required: true,
                message: '请选择',
              },
            ],
            initialValue: params.requestProtocol || 'http',
          })(
            <Select>
              <Select.Option value="http">HTTP</Select.Option>
              <Select.Option value="https">HTTPS</Select.Option>
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label="请求格式">
          {getFieldDecorator('requestFormat', {
            rules: [
              {
                required: true,
                message: '请选择',
              },
            ],
            initialValue: params.requestFormat || 'application/json',
          })(
            <Select style={{ width: 200 }}>
              <Select.Option value="application/json">application/json</Select.Option>
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label="响应格式">
          {getFieldDecorator('responseFormat', {
            rules: [
              {
                required: true,
                message: '请选择',
              },
            ],
            initialValue: params.responseFormat || 'application/json',
          })(
            <Select style={{ width: 200 }}>
              <Select.Option value="application/json">application/json</Select.Option>
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label="备注">
          {getFieldDecorator('remark', {
            rules: [
              {
                required: false,
                message: '请输入',
              },
            ],
            initialValue: params.remark || '',
          })(<Input.TextArea autosize={{ minRows: 3, maxRows: 6 }} />)}
        </FormItem>
        <div className="slide-footer">
          <Button
            onClick={this.handleOk}
            type="primary"
            loading={confirmLoading}
            style={{ margin: '0 20px' }}
          >
            {this.$t('common.ok')}
          </Button>
          <Button onClick={this.handleCancel}>{this.$t('common.cancel')}</Button>
        </div>
      </Form>
    );
  }
}

export default Form.create()(NewInterface);
