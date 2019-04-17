import React, { Component } from 'react';
import { Form, Button, message, Select, Radio, Input } from 'antd';
import 'styles/setting/params-setting/params-setting.scss';
import Service from './responsibility-service';
import Chooser from 'components/Widget/chooser';
import { connect } from 'dva';
const RadioGroup = Radio.Group;
const FormItem = Form.Item;
const Option = Select.Option;

class Responsibility extends Component {
  constructor(props) {
    super(props);
    this.state = {
      saveLoading: false,
      allResponsibilityCenter: 'Y',
      setOfBooksId: props.company.setOfBooksId,
      usableValue: [],
      allDefaultList: [],
      companyId: '',
    };
  }

  componentDidMount() {
    this.getById();
  }

  //提交
  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({ saveLoading: true });
        let params = {
          id: this.props.params ? this.props.params.id : '',
          tenantId: this.props.company.tenantId,
          setOfBooksId: values.setOfBooksId,
          departmentId: this.props.departmentId,
          companyId: values.companyId ? values.companyId[0].id : null,
          defaultResponsibilityCenter: values.defaultResponsibilityCenter
            ? values.defaultResponsibilityCenter
            : null,
          allResponsibilityCenter: this.state.allResponsibilityCenter,
          ids: values.ids ? values.ids.map(item => item.id) : [],
        };
        Service.addResponsibility(params)
          .then(res => {
            this.setState({ saveLoading: false });
            this.props.close(true);
            message.success('配置责任中心保存成功！');
          })
          .catch(e => {
            message.error(e.response.data.message);
            this.setState({ saveLoading: false });
          });
      }
    });
  };

  // 通过id获取值
  getById = () => {
    if (this.props.params.id) {
      Service.getById(this.props.params.id)
        .then(res => {
          this.setState(
            {
              allResponsibilityCenter: res.data.allResponsibilityCenter,
              usableValue: res.data.responsibilityCentersList,
              setOfBooksId: this.props.params.setOfBooksId,
              companyId: res.data.companyId,
            },
            () => {
              let flag = res.data.allResponsibilityCenter === 'Y' ? true : false;
              this.getResponsibleCenter(flag);
            }
          );
        })
        .catch(err => message.error(err.response.data.message));
    } else {
      this.getResponsibleCenter(true);
    }
  };

  //取消
  handleCancel = () => {
    this.props.close && this.props.close();
  };

  // 清空默认责任中心
  resetDefault = () => {
    this.props.form.setFieldsValue({
      defaultResponsibilityCenter: undefined,
    });
  };

  // 切换默认责任中心全选或部分
  onDimensionChange = e => {
    this.setState({
      allResponsibilityCenter: e.target.value,
    });
    this.resetDefault();
    this.props.params.responsibilityCentersList = [];
    if (e.target.value == 'Y') {
      const { allDefaultList } = this.state;
      this.setState({ usableValue: allDefaultList });
    } else {
      this.setState({ usableValue: [] });
    }
  };

  //改变账套后清除原账套下的表单数据
  clearForm = value => {
    this.props.form.setFieldsValue({
      companyId: '',
      defaultResponsibilityCenter: '',
    });
    this.setState({
      allResponsibilityCenter: 'Y',
      setOfBooksId: value,
      companyId: '',
      usableValue: [],
    });
    Service.getResponsibleCenter({ setOfBooksId: value })
      .then(res => {
        const { usableValue } = this.state;
        this.setState({ allDefaultList: res.data, usableValue: res.data });
      })
      .catch(err => message.error('获取数据失败'));
  };

  clearFormData = value => {
    let companyId;
    if (value && value.length) {
      companyId = value[0].id;
    }
    this.props.form.setFieldsValue({
      defaultResponsibilityCenter: '',
    });
    this.setState(
      {
        companyId: companyId,
        allResponsibilityCenter: 'Y',
      },
      () => {
        this.getResponsibleCenter(true);
      }
    );
  };

  // 选择可用责任中心模态框点击确定
  onUsableOk = value => {
    if (value.length === 0) {
      message.warn(this.$t('common.select.one.more'));
      return;
    } else {
      this.setState({ usableValue: value });
    }
    this.resetDefault();
  };

  // 或者账套下的 责任中心
  getResponsibleCenter = flag => {
    const { setOfBooksId, companyId } = this.state;
    Service.getResponsibleCenter({ setOfBooksId: setOfBooksId, companyId: companyId })
      .then(res => {
        const { usableValue } = this.state;
        this.setState({ allDefaultList: res.data, usableValue: flag ? res.data : usableValue });
      })
      .catch(err => message.error(err.response.data.message));
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const { params, allSetBooks, company } = this.props;
    const { saveLoading, allResponsibilityCenter, usableValue } = this.state;
    const formItemLayout = {
      labelCol: { span: 10 },
      wrapperCol: { span: 12 },
    };
    return (
      <div>
        <Form onSubmit={this.handleSubmit}>
          <FormItem {...formItemLayout} label={'账套'}>
            {getFieldDecorator('setOfBooksId', {
              rules: [
                {
                  required: true,
                  message: '请选择',
                },
              ],
              initialValue: params.id ? params.setOfBooksId : company.setOfBooksId,
            })(
              <Select onChange={value => this.clearForm(value)}>
                {allSetBooks.map((item, index) => (
                  <Option key={item.value} value={item.value}>
                    {item.label}
                  </Option>
                ))}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="公司">
            {getFieldDecorator('companyId', {
              rules: [
                {
                  required: false,
                  message: '请选择',
                },
              ],
              initialValue:
                params.id && params.companyId
                  ? [
                      {
                        id: params.companyId,
                        companyCodeName: params.companyCode + '-' + params.companyName,
                      },
                    ]
                  : '',
            })(
              <Chooser
                type="responsibility_company"
                labelKey="companyCodeName"
                valueKey="id"
                single={true}
                onChange={value => this.clearFormData(value)}
                listExtraParams={{ setOfBooksId: this.props.form.getFieldValue('setOfBooksId') }}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="可用责任中心" required>
            <RadioGroup value={allResponsibilityCenter} onChange={this.onDimensionChange}>
              <Radio value="Y">全部</Radio>
              <Radio value="N">部分</Radio>
            </RadioGroup>
            {allResponsibilityCenter === 'Y' ? (
              <span />
            ) : (
              getFieldDecorator('ids', {
                rules: [
                  {
                    required: true,
                    message: this.$t('common.select.one.more'),
                  },
                ],
                initialValue: params.responsibilityCentersList
                  ? params.responsibilityCentersList.map(item => ({ id: item.id }))
                  : '',
              })(
                <Chooser
                  placeholder="请输入代码或名称"
                  type="responsibility_usable"
                  labelKey="responsibilityCenterName"
                  valueKey="id"
                  showDetail={false}
                  showNumber={true}
                  onChange={value => this.onUsableOk(value)}
                  single={false}
                  listExtraParams={{
                    setOfBooksId: this.props.form.getFieldValue('setOfBooksId'),
                    companyId: this.state.companyId,
                  }}
                />
              )
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="默认责任中心">
            {getFieldDecorator('defaultResponsibilityCenter', {
              rules: [
                {
                  required: false,
                  message: '请输入',
                },
              ],
              initialValue:
                params.id && params.defaultResponsibilityCenter
                  ? params.defaultResponsibilityCenter
                  : null,
            })(
              <Select>
                {usableValue.map(o => {
                  return (
                    <Option key={o.key} value={o.id}>
                      {o.responsibilityCenterCode}-{o.responsibilityCenterName}
                    </Option>
                  );
                })}
              </Select>
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

const WrappedResponsibility = Form.create()(Responsibility);

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedResponsibility);
