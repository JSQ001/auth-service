import React, { Component } from 'react';
import { Form, Select, Input, Button, Switch, Icon, message } from 'antd';
import projectTypeSevice from './service';

const FormItem = Form.Item;
const { Option } = Select;

class NewDefinition extends Component {
  constructor(props) {
    super(props);
    this.state = {
      saveLoading: false,
    };
  }

  handleSave = e => {
    e.preventDefault();
    const { params, form } = this.props;
    form.validateFields((err, values) => {
      if (err) return;
      this.setState({ saveLoading: true });
      let handleEvent;
      let msg;
      let param;
      if (!params.id) {
        handleEvent = projectTypeSevice.createProjectType;
        msg = '新增成功!';
        param = {
          tenantId: params.tenantId,
          ...values,
        };
      } else {
        handleEvent = projectTypeSevice.updateProjectType;
        msg = '编辑成功!';
        param = {
          ...params,
          ...values,
        };
      }
      handleEvent(params.id ? param : [param])
        .then(() => {
          this.setState({ saveLoading: false });
          message.success(msg);
          this.handleCancel(true);
        })
        .catch(error => {
          message.error(error.response.data.message);
          this.setState({ saveLoading: false });
        });
    });
  };

  handleCancel = flag => {
    const { onClose } = this.props;
    onClose(flag);
  };

  render() {
    const { saveLoading } = this.state;
    const {
      form: { getFieldDecorator },
      params: { setOfBooksId, setOfBookList, id, projectTypeCode, projectTypeName, enabled },
    } = this.props;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14, offset: 1 },
    };

    return (
      <div>
        <Form onSubmit={this.handleSave}>
          <Form.Item {...formItemLayout} label={this.$t('账套')} colon>
            {getFieldDecorator('setOfBooksId', {
              initialValue: setOfBooksId,
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.select' }),
                },
              ],
            })(
              <Select
                disabled
                className="input-disabled-color"
                placeholder={this.$t({ id: 'common.please.select' })}
              >
                {
                  <Option key={setOfBooksId}>
                    {setOfBookList.find(o => o.value === setOfBooksId).label}
                  </Option>
                }
              </Select>
            )}
          </Form.Item>

          <Form.Item {...formItemLayout} label={this.$t('项目类型代码')} colon>
            {getFieldDecorator('projectTypeCode', {
              initialValue: projectTypeCode,
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.select' }),
                },
              ],
            })(
              <Input
                disabled={!!id}
                className="input-disabled-color"
                placeholder={this.$t({ id: 'common.please.enter' })}
              />
            )}
          </Form.Item>

          <Form.Item {...formItemLayout} label={this.$t('项目类型名称')} colon>
            {getFieldDecorator('projectTypeName', {
              initialValue: projectTypeName,
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.select' }),
                },
              ],
            })(
              <Input
                className="input-disabled-color"
                placeholder={this.$t({ id: 'common.please.enter' })}
              />
            )}
          </Form.Item>

          <FormItem {...formItemLayout} label={this.$t({ id: 'common.column.status' })} colon>
            {getFieldDecorator('enabled', {
              valuePropName: 'checked',
              initialValue: id ? enabled : true,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
                onChange={this.switchChange}
              />
            )}
          </FormItem>
          <div className="slide-footer">
            <Button type="primary" htmlType="submit" loading={saveLoading}>
              {this.$t({ id: 'common.save' })}
            </Button>
            <Button onClick={() => this.handleCancel()}>{this.$t({ id: 'common.cancel' })}</Button>
          </div>
        </Form>
      </div>
    );
  }
}

const WrappedNewDefinition = Form.create()(NewDefinition);
export default WrappedNewDefinition;
