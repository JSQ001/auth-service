/**
 * 新增地点级别定义
 * Created by zhanhua.cheng on 2019/3/27.
 */
import React from 'react';
import { connect } from 'react-redux';

import { Button, Form, Switch, Input, message, Select, Modal } from 'antd';
import { routerRedux } from 'dva/router';
const FormItem = Form.Item;
const Option = Select.Option;
const { TextArea } = Input;
import locationLevelService from './service';

class NewLocationLevel extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      loading: false,
    };
  }

  componentWillReceiveProps(nextProps) {
    if (!nextProps.params.visible && this.props.params.visible) {
      this.props.form.resetFields();
    }
  }

  // 保存地点级别
  saveLocationLevel = values => {
    if (!this.props.params.id) {
      let toValue = {
        ...this.props.params,
        ...values,
      };
      locationLevelService
        .createLocationLevel(toValue)
        .then(res => {
          this.setState({ loading: false });
          this.props.form.resetFields();
          this.props.onClose(true);
          message.success(this.$t('common.operate.success'));
          let id = res.data.id;
          this.props.dispatch(
            routerRedux.push({
              pathname: `/admin-setting/location-level/distribute-location/` + id,
            })
          );
        })
        .catch(e => {
          this.setState({ loading: false });
          message.error(this.$t('common.save.filed') + `${e.response.data.message}`);
        });
    } else {
      let toValue = {
        ...this.props.params,
        ...values,
      };
      locationLevelService
        .updateLocationLevel(toValue)
        .then(res => {
          this.setState({ loading: false });
          this.props.form.resetFields();
          this.props.onClose(true);
          message.success(this.$t('common.operate.success'));
        })
        .catch(e => {
          this.setState({ loading: false });
          message.error(this.$t('common.save.filed') + `${e.response.data.message}`);
        });
    }
  };

  //新建或编辑
  handleSave = e => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        this.setState({ loading: true });
        let enabled = values.enabled;
        if (!enabled) {
          Modal.confirm({
            title: this.$t('common.info'), //提示
            content: '禁用后地点级别中的地点将被清空，是否确认禁用?',
            onOk: () => {
              this.saveLocationLevel(values);
            },
            onCancel: () => {
              this.setState({
                loading: false,
              });
            },
          });
        } else {
          this.saveLocationLevel(values);
        }
      }
    });
  };

  onCancel = () => {
    this.props.form.resetFields();
    this.props.onClose();
  };
  //监听表单值
  handleFormChange = e => {
    if (this.state.loading) {
      this.setState({
        loading: false,
      });
    }
  };
  render() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { params, setOfBooksId, setOfBooksIdList, onClose } = this.props;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 14, offset: 1 },
    };

    return (
      <div className="new-payment-method">
        <Form onSubmit={this.handleSave} onChange={this.handleFormChange}>
          <FormItem {...formItemLayout} label={this.$t('setting.set.of.book')}>
            {getFieldDecorator('setOfBooksId', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: setOfBooksId,
            })(
              <Select disabled>
                {setOfBooksIdList.map(item => <Option key={item.value}>{item.label}</Option>)}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('mdata.location.level.code')}>
            {getFieldDecorator('code', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: params.code || '',
            })(<Input placeholder={this.$t('common.please.enter')} disabled={!!params.id} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('mdata.location.level.name')}>
            {getFieldDecorator('name', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: params.name || '',
            })(<Input placeholder={this.$t('common.please.enter')} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('mdata.location.level.remark')}>
            {getFieldDecorator('remarks', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: params.remarks || '',
            })(
              <TextArea
                autosize={{ minRows: 4 }}
                style={{ minWidth: '100%' }}
                placeholder={this.$t({ id: 'common.please.enter' } /*请输入*/)}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('common.column.status') /* 状态 */}>
            {getFieldDecorator('enabled', {
              valuePropName: 'checked',
              initialValue: params.id ? params.enabled : true,
            })(<Switch />)}
            <span style={{ paddingLeft: '10px' }}>
              {getFieldValue('enabled')
                ? this.$t('common.status.enable') /* 启用 */
                : this.$t('common.status.disable') /* 禁用 */}
            </span>
          </FormItem>

          <div className="slide-footer">
            <Button type="primary" htmlType="submit" loading={this.state.loading}>
              {this.$t('common.save')}
            </Button>
            <Button onClick={this.onCancel}>{this.$t('common.cancel')}</Button>
          </div>
        </Form>
      </div>
    );
  }
}

const WrappedNewLocationLevel = Form.create()(NewLocationLevel);
function mapStateToProps(state) {
  return {};
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewLocationLevel);
