import React, { Component } from 'react';
import { Form, Input, Select, DatePicker, Button, message } from 'antd';
import Chooser from 'components/Widget/chooser';
import { connect } from 'dva';
import 'styles/setting/params-setting/params-setting.scss';
import workflowService from 'containers/setting/workflow/workflow.service';
import Service from './transfer-approval-servce';
import moment from 'moment';
const FormItem = Form.Item;
const { TextArea } = Input;

class NewMyPassing extends Component {
  constructor(props) {
    super(props);
    this.state = {
      saveLoading: false,
      isDisable: true,
      setBIllBooks: [],
    };
  }

  // 生命周期
  componentDidMount() {
    const { documentCategory } = this.props.params;
    let params = {
      documentCategory: documentCategory,
      booksID: this.props.tenantMode ? this.props.company.setOfBooksId : '',
    };
    workflowService
      .getWorkflowList(params)
      .then(({ data }) => {
        let list = [];
        data.map(item => {
          list.push({ value: item.id, label: `${item.formName}` });
        });
        this.setState({ setBIllBooks: list });
      })
      .catch(err => {
        message.error(err.response.message);
      });
  }

  // 提交
  handleSave = e => {
    e.preventDefault();
    let { params } = this.props;
    this.props.form.validateFields((err, values, record) => {
      if (err) return;
      this.setState({ saveLoading: true });
      let handleEvent, msg, param;
      if (!params.id) {
        handleEvent = Service.addPassingSetting;
        msg = '新增成功!';
        param = {
          ...values,
          agentId: values.agentId ? values.agentId[0].userId : '',
        };
      } else {
        handleEvent = Service.editPassingSetting;
        msg = '编辑成功!';
        param = {
          id: params.id,
          ...values,
          agentId: values.agentId ? values.agentId[0].userId : '',
          tenantId: params.tenantId,
          versionNumber: params.versionNumber,
          setOfBooksId: params.setOfBooksId,
          authorizerId: params.authorizerId,
        };
      }
      handleEvent(param)
        .then(res => {
          this.setState({ saveLoading: false });
          message.success(msg);
          this.handleCancel(true);
        })
        .catch(err => {
          message.error(err.response.data.message);
          this.setState({ saveLoading: false });
        });
    });
  };

  //取消
  handleCancel = event => {
    this.props.close && this.props.close(event);
  };

  // 审批流可用
  changDisabled = value => {
    if (value) {
      this.props.form.setFieldsValue({ workflowId: '' });
      this.setState({ isDisable: false });
      let params = {
        documentCategory: value,
        booksID: this.props.tenantMode ? this.props.company.setOfBooksId : '',
      };
      workflowService
        .getWorkflowList(params)
        .then(({ data }) => {
          let list = [];
          data.map(item => {
            list.push({ value: item.id, label: `${item.formName}` });
          });
          this.setState({ setBIllBooks: list });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    } else {
      this.props.form.setFieldsValue({ workflowId: '' });
      this.setState({ isDisable: true, setBIllBooks: [] });
    }
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 10, offset: 1 },
    };
    const { saveLoading, isDisable, setBIllBooks } = this.state;
    const { params, setOfBooks } = this.props;
    return (
      <div>
        <Form onSubmit={this.handleSave}>
          <FormItem {...formItemLayout} label="授托人">
            {getFieldDecorator('authorizeId', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: this.props.user.id,
            })(
              <Select disabled>
                <Select.Option value={this.props.user.id}>
                  {this.props.user.remark}-{this.props.user.userName}
                </Select.Option>
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="单据大类">
            {getFieldDecorator('documentCategory', {
              rules: [
                {
                  required: false,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.id ? params.documentCategory : '',
            })(
              <Select allowClear onChange={this.changDisabled}>
                {setOfBooks.map(item => {
                  return (
                    <Select.Option key={item.value} value={item.value}>
                      {' '}
                      {item.label}
                    </Select.Option>
                  );
                })}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="审批流">
            {getFieldDecorator('workflowId', {
              rules: [
                {
                  required: false,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.id ? params.workflowId : '',
            })(
              <Select allowClear disabled={isDisable && (params.id ? false : true)}>
                {setBIllBooks.map(item => {
                  return (
                    <Select.Option key={item.value} value={item.value}>
                      {item.label}
                    </Select.Option>
                  );
                })}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="代理人">
            {getFieldDecorator('agentId', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue:
                params.id && params.agentId
                  ? [
                      {
                        userId: params.agentId,
                        userName: params.agentName,
                        userCode: params.agentCode,
                      },
                    ]
                  : '',
            })(
              <Chooser
                type="select_authorization_user"
                // labelKey="userName"
                labelKey="${userCode}-${userName}"
                valueKey="userId"
                single={true}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="有效日期从">
            {getFieldDecorator('startDate', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.startDate ? moment(params.startDate) : null,
            })(<DatePicker style={{ width: '100%' }} />)}
          </FormItem>
          <FormItem {...formItemLayout} label="有效日期至">
            {getFieldDecorator('endDate', {
              rules: [
                {
                  required: false,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.endDate ? moment(params.endDate) : null,
            })(<DatePicker allowClear style={{ width: '100%' }} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t({ id: 'common.remark' } /*备注*/)}>
            {getFieldDecorator('authorizationNotes', {
              initialValue: params.authorizationNotes || '',
            })(
              <TextArea
                autosize={{ minRows: 4 }}
                style={{ minWidth: '100%' }}
                placeholder={this.$t({ id: 'common.please.enter' } /*请输入*/)}
              />
            )}
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
  console.log(state, 'state');
  return {
    tenantMode: true,
    user: state.user.currentUser,
    company: state.user.company,
    organization: state.user.organization || {},
  };
}

const WrappedNewBuilt = Form.create()(NewMyPassing);
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewBuilt);
