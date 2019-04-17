import React from 'react';
import { connect } from 'dva';
import {
  Button,
  Form,
  Divider,
  Input,
  Switch,
  Icon,
  Alert,
  Row,
  Col,
  Spin,
  message,
  Select,
} from 'antd';
import LanguageInput from 'components/Widget/Template/language-input/language-input';
import ResponsibilityService from 'containers/setting/responsibility-center/responsibility-service';
const FormItem = Form.Item;
const Option = Select.Option;
class NewResponsibilityCenter extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      newDataPrams: {},
    };
  }
  componentWillMount() {
    this.setState({
      newDataPrams: this.props.params,
    });
  }
  handleSave = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (err) return;
      let params = {
        tenantId: this.props.company.tenantId,
        setOfBooksId: values.setOfBooksId,
        responsibilityCenterCode: values.responsibilityCenterCode,
        responsibilityCenterName: values.responsibilityCenterName,
        enabled: values.enabled,
      };
      let messageValue = '';
      const { newDataPrams } = this.state;
      if (newDataPrams.id) {
        params.id = newDataPrams.id;
        messageValue = '编辑成功';
      } else messageValue = '新增成功';
      ResponsibilityService.saveResponsibility(params)
        .then(res => {
          if (res.status === 200) {
            this.props.close();
            message.success(messageValue);
          }
        })
        .catch(e => {
          message.error(e.response.data.message);
        });
    });
  };
  onCancel = () => {
    this.props.close();
  };
  //责任中心名称：多语言
  i18nNameChange = (name, i18nName) => {
    const newDataPrams = this.state.newDataPrams;
    newDataPrams.responsibilityCenterName = name;
    if (!newDataPrams.i18n) {
      newDataPrams.i18n = {};
    }
    newDataPrams.i18n.responsibilityCenterName = i18nName;
  };
  render() {
    const { getFieldDecorator } = this.props.form;
    const { newDataPrams } = this.state;
    const { setOfBooks, params, set } = this.props;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 10, offset: 1 },
    };
    return (
      <div>
        <Form onSubmit={this.handleSave}>
          <FormItem {...formItemLayout} label="账套">
            {getFieldDecorator('setOfBooksId', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: newDataPrams.id ? set : params.setOfBooksId,
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
          <FormItem {...formItemLayout} label="责任中心代码">
            {getFieldDecorator('responsibilityCenterCode', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: newDataPrams.responsibilityCenterCode || '',
            })(
              <Input
                placeholder={this.$t('common.please.enter')}
                disabled={newDataPrams.id ? true : false}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="责任中心名称">
            {getFieldDecorator('responsibilityCenterName', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: newDataPrams.responsibilityCenterName || '',
            })(
              <div>
                <LanguageInput
                  key={1}
                  name={newDataPrams.responsibilityCenterName}
                  i18nName={
                    newDataPrams.i18n && newDataPrams.i18n.responsibilityCenterName
                      ? newDataPrams.i18n.responsibilityCenterName
                      : null
                  }
                  placeholder={this.$t('common.please.enter') /* 请输入 */}
                  isEdit={newDataPrams.id ? true : false}
                  nameChange={this.i18nNameChange}
                />
              </div>
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={this.$t('common.status', { status: '' })}
            colon={true}
          >
            {getFieldDecorator('enabled', {
              rules: [],
              initialValue: newDataPrams.id ? newDataPrams.enabled : true,
              valuePropName: 'checked',
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
              />
            )}
          </FormItem>
          <div className="slide-footer">
            <Button type="primary" htmlType="submit">
              {this.$t({ id: 'common.save' })}
            </Button>
            <Button onClick={this.onCancel}>{this.$t({ id: 'common.cancel' })}</Button>
          </div>
        </Form>
      </div>
    );
  }
}
const WrappedNewSubjectSheet = Form.create()(NewResponsibilityCenter);
function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewSubjectSheet);