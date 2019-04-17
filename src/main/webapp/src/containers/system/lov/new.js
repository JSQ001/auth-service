import React, { Component } from 'react';
import { Form, Input, Select, Button, message, Spin } from 'antd';
import Chooser from 'widget/chooser';
import Lov from 'widget/Template/lov';

import service from './service';
import appService from '../app/app.service';
import apiService from '../interface/interface.service';

const FormItem = Form.Item;

class NewLov extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      appId: '',
      apiId: '',
      apps: [],
      apis: [],
      responseList: [],
      record: {},
      spinning: true,
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
    const { params } = this.props;
    if (params.id) {
      service.getInfoById(params.id).then(res => {
        this.setState({ record: res, spinning: false, appId: res.appId, apiId: res.apiId });
      });
    } else {
      this.setState({ spinning: false });
    }
  }

  // 获取应用列表
  getApps = flag => {
    if (flag) {
      appService
        .getAll()
        .then(res => {
          this.setState({ apps: res });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  // 获取接口列表
  getApis = flag => {
    const { appId } = this.state;
    if (flag) {
      apiService
        .getInterfaceListByAppId(appId)
        .then(res => {
          this.setState({ apis: res });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

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
      const { record } = this.state;
      this.setState({ loading: true });
      const method = !params.id ? service.add : service.edit;
      values.responseColumn = values.responseColumn.map(o => o.id).join(',');
      values.requestColumn = values.requestColumn.map(o => o.id).join(',');
      values.appId = values.appId.key;
      values.apiId = values.apiId.key;
      method({ ...record, ...values })
        .then(() => {
          message.success(
            !params.id ? this.$t('common.create.success') : this.$t('common.edit.success')
          );
          this.setState({ loading: false });
          this.props.onClose && this.props.onClose(true);
        })
        .catch(err => {
          message.error(err.response.data.message);
          this.setState({ loading: false });
        });
    });
  };

  handleAppChange = value => {
    this.props.form.setFieldsValue({
      apiId: { label: '', key: '' },
      requestColumn: [],
      responseColumn: [],
    });
    this.setState({ appId: value.key, apiId: '' });
  };

  handleApiChange = value => {
    this.props.form.setFieldsValue({
      requestColumn: [],
      responseColumn: [],
    });
    this.setState({ apiId: value.key });
  };

  render() {
    const { loading, apps, apis, appId, apiId, record, spinning } = this.state;
    const {
      form: { getFieldDecorator },
    } = this.props;

    return spinning ? (
      <Spin />
    ) : (
      <Form onSubmit={this.handleSubmit}>
        <FormItem {...this.formItemLayout} label={this.$t('lov.info.code')}>
          {getFieldDecorator('lovCode', {
            rules: [
              {
                required: true,
                message: this.$t('common.enter'),
              },
            ],
            initialValue: record.lovCode,
          })(<Input />)}
        </FormItem>
        <FormItem {...this.formItemLayout} label={this.$t('lov.info.name')}>
          {getFieldDecorator('lovName', {
            rules: [
              {
                required: true,
                message: this.$t('common.enter'),
              },
            ],
            initialValue: record.lovName,
          })(<Input />)}
        </FormItem>
        <FormItem {...this.formItemLayout} label={this.$t('app.info.name')}>
          {getFieldDecorator('appId', {
            rules: [
              {
                required: true,
                message: this.$t('common.enter'),
              },
            ],
            initialValue: { label: record.appName, key: record.appId },
          })(
            <Select
              labelInValue
              onDropdownVisibleChange={this.getApps}
              onChange={this.handleAppChange}
            >
              {apps &&
                apps.length > 0 &&
                apps.map(item => <Select.Option key={item.id}>{item.appName}</Select.Option>)}
            </Select>
          )}
        </FormItem>
        <FormItem {...this.formItemLayout} label={this.$t('lov.info.interface')}>
          {getFieldDecorator('apiId', {
            rules: [
              {
                required: true,
                message: this.$t('common.enter'),
              },
            ],
            initialValue: { label: record.apiName, key: record.apiId },
          })(
            <Select
              onChange={this.handleApiChange}
              labelInValue
              onDropdownVisibleChange={this.getApis}
              disabled={!appId}
            >
              {apis &&
                apis.length > 0 &&
                apis.map(item => <Select.Option key={item.id}>{item.interfaceName}</Select.Option>)}
            </Select>
          )}
        </FormItem>
        <FormItem {...this.formItemLayout} label="搜索项">
          {getFieldDecorator('requestColumn', {
            rules: [
              {
                required: true,
                message: this.$t('common.enter'),
              },
            ],
            initialValue: record.requestColumnInfo || [],
          })(
            <Chooser
              type="select_requset_fields"
              labelKey="name"
              valueKey="id"
              disabled={!apiId}
              listExtraParams={{ interfaceId: apiId }}
              showClear={true}
            />
          )}
        </FormItem>
        <FormItem {...this.formItemLayout} label="显示项">
          {getFieldDecorator('responseColumn', {
            rules: [
              {
                required: true,
                message: this.$t('common.enter'),
              },
            ],
            initialValue: record.responseColumnInfo || [],
          })(
            <Chooser
              type="select_response_fields"
              labelKey="name"
              valueKey="id"
              disabled={!apiId}
              listExtraParams={{ interfaceId: apiId }}
              showClear={true}
            />
          )}
        </FormItem>

        {/* <FormItem {...this.formItemLayout} label="lov测试">
            {getFieldDecorator('test', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.enter'),
                },
              ],
              initialValue: { id: "1106444913238528002", fullName: "哈哈" },
            })(
              <Lov
                code="contract_user"
                valueKey="id"
                labelKey="fullName"
                single
                extraParams={{ companyId: "1083751704185716737" }}
              />
            )}
          </FormItem> */}

        <div className="slide-footer">
          <Button type="primary" htmlType="submit" loading={loading} style={{ margin: '0 20px' }}>
            {this.$t('common.ok')}
          </Button>
          <Button onClick={this.handleCancel}>{this.$t('common.cancel')}</Button>
        </div>
      </Form>
    );
  }
}

export default Form.create()(NewLov);
