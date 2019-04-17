import React, { Component } from 'react';
import { connect } from 'dva';
import { Form, Input, Button, message, Select, Switch } from 'antd';
import 'styles/setting/params-setting/params-setting.scss';
import service from './dimension-definition.service';

const FormItem = Form.Item;
const Option = Select.Option;

class NewBuilt extends Component {
  constructor(props) {
    super(props);
    this.state = {
      saveLoading: false,
      paramsTypeList: [],
      dimensionNameList: [],
      swith: true,
      scenarios: {},
      section: {},
    };
  }

  // 生命周期
  componentDidMount() {
    this.getNumber();
  }

  // 获取序号
  getNumber = () => {
    const set = this.props.set;
    service
      .NumberDimensionSetting(set)
      .then(res => {
        this.setState({
          paramsTypeList: res.data,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ saveLoading: false });
      });
  };

  // 保存&&编辑
  handleSubmit = e => {
    e.preventDefault();
    const { params } = this.props;
    console.log();
    this.props.form.validateFields((err, values, record) => {
      const data = Object.assign(params, values);
      if (err) return;
      this.setState({
        saveLoading: true,
      });

      if (!params.id) {
        service
          .addDimensionSetting(values)
          .then(res => {
            message.success('新增成功！');
            this.setState({ saveLoading: false });
            this.props.close && this.props.close(true);
          })
          .catch(err => {
            message.error(err.response.data.message);
            this.setState({ saveLoading: false });
          });
      } else {
        service
          .editDimensionSetting(data)
          .then(res => {
            message.success('编辑成功！');
            this.setState({ saveLoading: false });
            this.props.close && this.props.close(true);
          })
          .catch(err => {
            message.error(err.response.data.message);
            this.setState({ saveLoading: false });
          });
      }
    });
  };

  // 取消
  handleCancel = () => {
    this.props.close && this.props.close();
  };

  // 状态开关
  onChange = checked => {
    this.setState({
      swith: checked,
    });
  };

  render() {
    const { getFieldDecorator, getFieldsError } = this.props.form;
    const { params, setOfBooks, set } = this.props;
    const { saveLoading, paramsTypeList, section } = this.state;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };
    return (
      <div>
        <Form onSubmit={this.handleSubmit}>
          <h3>基本信息</h3>
          <FormItem {...formItemLayout} label="账套">
            {getFieldDecorator('setOfBooksId', {
              rules: [
                {
                  required: true,
                },
              ],
              initialValue: JSON.stringify(params) === '{}' ? set : params.setOfBooksId,
            })(
              <Select disabled>
                {setOfBooks.map(option => {
                  return (
                    <Option key={option.value} value={option.value}>
                      {option.label}
                    </Option>
                  );
                })}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="序号" hasFeedback>
            {getFieldDecorator('dimensionSequence', {
              rules: [
                {
                  required: true,
                  message: '请选择',
                },
              ],
              initialValue: params.dimensionSequence || undefined,
            })(
              <Select placeholder="请选择" disabled={JSON.stringify(params) !== '{}'}>
                {paramsTypeList.map((item, index) => {
                  return (
                    <Select.Option key={index} value={item}>
                      {item}
                    </Select.Option>
                  );
                })}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="维度代码">
            {getFieldDecorator('dimensionCode', {
              rules: [
                {
                  required: true,
                  message: '请输入',
                },
              ],
              initialValue: this.props.params.dimensionCode || '',
            })(<Input placeholder="请输入" disabled={!!params.id} />)}
          </FormItem>
          <FormItem {...formItemLayout} label="维度名称">
            {getFieldDecorator('dimensionName', {
              rules: [
                {
                  required: true,
                  message: '请输入',
                },
              ],
              initialValue: params.dimensionName || '',
            })(
              <Input
                key={1}
                name={params.dimensionName}
                placeholder={this.$t('common.please.enter') /* 请输入 */}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="状态">
            {getFieldDecorator('enabled', {
              valuePropName: 'checked',
              initialValue: typeof params.id === 'undefined' ? true : params.enabled,
            })(<Switch />)}
            <span style={{ paddingLeft: '10px' }}>
              {this.props.form.getFieldValue('enabled') ? '启用' : '禁用'}
            </span>
          </FormItem>
          <div className="slide-footer">
            <Button className="btn" type="primary" htmlType="submit" loading={saveLoading}>
              {this.$t('common.save')}
            </Button>
            <Button className="btn" onClick={this.handleCancel}>
              {this.$t('common.cancel')}
            </Button>
          </div>
        </Form>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

const WrappedNewBuilt = Form.create()(NewBuilt);
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewBuilt);