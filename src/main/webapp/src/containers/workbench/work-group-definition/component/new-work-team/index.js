import React, { Component } from 'react';
import { Form, Input, Button, message, Switch, Select } from 'antd';
import Chooser from 'widget/chooser';
import { connect } from 'dva';
import service from '../../work-group-definition-service';

const FormItem = Form.Item;
const Option = Select.Option;

class NewWorkTeamForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      saveLoading: false,
      value: '',
    };
  }

  componentDidMount = () => {};

  //保存
  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFields((err, value) => {
      if (err) return;
      let params = { ...value };
      params.workManagerId = value.workManagerId[0].id;
      params.workManager = value.workManagerId[0].name;
      params.parentId = value.parentId ? value.parentId.key : null;
      params.parent = value.parentId ? value.parentId.label : null;
      this.setState({ saveLoading: true });
      service
        .addWorkTeamValue(params)
        .then(res => {
          message.success(this.$t('workbench.team.save.success')); // 添加工作组操作成功
          this.setState({ saveLoading: false }, () => {
            this.props.close(true);
          });
        })
        .catch(err => {
          message.error(err.response.data.message);
          this.setState({ saveLoading: false });
        });
    });
  };

  //取消
  handleCancel = () => {
    this.props.close(false);
  };

  render() {
    const { saveLoading } = this.state;
    const { params, workTeamEnabledList, useOrNot } = this.props;
    const { getFieldDecorator } = this.props.form;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };
    return (
      <div>
        <Form>
          <FormItem {...formItemLayout} label={this.$t('workbench.workTeam.code')}>
            {getFieldDecorator('workTeamCode', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.workTeamCode || '',
            })(<Input placeholder={this.$t('common.please.enter')} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('workbench.workTeam.name')}>
            {getFieldDecorator('workTeamName', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.workTeamName || '',
            })(<Input placeholder={this.$t('common.please.enter')} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('workbench.workTeam.teamLoader')}>
            {getFieldDecorator('workManagerId', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.select'),
                },
              ],
              initialValue: params.workManagerId || '',
            })(
              <Chooser
                placeholder={this.$t('common.please.select')}
                type="select_employee"
                labelKey="name"
                valueKey="id"
                listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                single={true}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('workbench.workTeam.superior')}>
            {getFieldDecorator('parentId', {
              initialValue:
                params.parentId === null
                  ? { key: '', label: '' }
                  : { key: params.parentId, label: params.parent },
            })(
              <Select
                placeholder={this.$t('common.please.select')}
                disabled={params.disableParent}
                labelInValue
                getPopupContainer={triggerNode => triggerNode.parentNode}
              >
                {JSON.stringify(workTeamEnabledList) !== '[]' &&
                  workTeamEnabledList.map(team => <Option key={team.value}>{team.label}</Option>)}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('common.column.status')}>
            {getFieldDecorator('enabled', {
              initialValue: params.id ? params.enabled : true,
              valuePropName: 'checked',
            })(<Switch />)}
            <span style={{ paddingLeft: '20px' }}>
              {this.props.form.getFieldValue('enabled')
                ? this.$t('common.status.enable')
                : this.$t('common.status.disable')}
            </span>
          </FormItem>
          <div className="slide-footer">
            <Button
              type="primary"
              htmlType="submit"
              loading={saveLoading}
              onClick={this.handleSubmit}
            >
              {this.$t('common.save')}
            </Button>
            <Button onClick={this.handleCancel}>{this.$t('common.cancel')}</Button>
          </div>
        </Form>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    tenantMode: true,
    user: state.user.currentUser,
    company: state.user.company,
  };
}
const NewWorkTeam = Form.create()(NewWorkTeamForm);
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(NewWorkTeam);
