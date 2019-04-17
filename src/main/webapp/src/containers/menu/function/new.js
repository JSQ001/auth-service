import React, { Component } from 'react';
import { Form, Input, Button, message, InputNumber, Select, Icon, Tooltip } from 'antd';
import service from '../service';
import icons from '../../../assets/icons';

const FormItem = Form.Item;

class NewFunction extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      pages: [],
      appList: [],
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

  componentDidMount() {
    service
      .getPageList({ page: 0, size: 9999 })
      .then(res => {
        this.setState({ pages: res.data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });

    service
      .getAppList()
      .then(res => {
        this.setState({ appList: res.data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
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
      const method = !params.id ? service.addFunction : service.editFunction;
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
    const { loading, pages, appList } = this.state;
    const { params } = this.props;

    return (
      <Form onSubmit={this.handleSubmit}>
        <FormItem {...this.formItemLayout} label="名称">
          {getFieldDecorator('functionName', {
            rules: [
              {
                required: true,
                message: '请输入',
              },
            ],
            initialValue: params.functionName,
          })(<Input />)}
        </FormItem>
        {/* <FormItem {...this.formItemLayout} label="路由">
          {getFieldDecorator('functionRouter', {
            rules: [
              {
                required: true,
                message: '请输入',
              },
            ],
            initialValue: params.functionRouter,
          })(<Input />)}
        </FormItem> */}
        <FormItem {...this.formItemLayout} label="页面">
          {getFieldDecorator('pageId', {
            rules: [
              {
                required: true,
                message: '请选择',
              },
            ],
            initialValue: params.pageId,
          })(
            <Select>
              {pages.map(item => <Select.Option key={item.id}>{item.pageName}</Select.Option>)}
            </Select>
          )}
        </FormItem>
        <FormItem {...this.formItemLayout} label="应用">
          {getFieldDecorator('applicationId', {
            rules: [
              {
                required: true,
                message: '请输入',
              },
            ],
            initialValue: params.applicationId,
          })(
            <Select>
              {appList.map(item => <Select.Option key={item.id}>{item.appName}</Select.Option>)}
            </Select>
          )}
        </FormItem>
        <Form.Item
          {...this.formItemLayout}
          label={
            <span>
              图标&nbsp;
              <Tooltip title="只有一级目录的时候，才需要配置，当有上级目录时，即使配置也不会生效。">
                <Icon type="question-circle-o" />
              </Tooltip>
            </span>
          }
        >
          {getFieldDecorator('functionIcon', {
            initialValue: params.functionIcon,
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
        <FormItem {...this.formItemLayout} label="参数">
          {getFieldDecorator('param', {
            initialValue: params.param,
          })(<Input />)}
        </FormItem>
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

export default Form.create()(NewFunction);
