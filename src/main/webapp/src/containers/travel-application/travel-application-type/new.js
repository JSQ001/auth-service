import React from 'react';
import { connect } from 'react-redux';
import { Form, Switch, Icon, Input, Select, Button, Spin, Radio, Tooltip, message } from 'antd';
import PermissionsAllocation from 'widget/Template/permissions-allocation';
import CustomChooser from 'components/Template/custom-chooser';

import service from './service';

const FormItem = Form.Item;
const Option = Select.Option;

const type = {
  1001: 'all',
  1002: 'department',
  1003: 'group',
};
const permissionsType = {
  all: '1001',
  department: '1002',
  group: '1003',
};
class NewApplicationType extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      saveLoading: false,
      loading: !!props.params.id,
      setOfBooks: [],
      formList: [],
      record: {},
      //差旅大类ID
      travelTypeCategoryId: '',
    };
  }
  componentDidMount() {
    if (!this.props.params.id) {
      this.getFormList();
    }
    this.getInfoById();
    this.getTravelExpenseTypesCategory();
  }

  //获取差旅大类ID
  getTravelExpenseTypesCategory = () => {
    service.getExpenseTypesCategoryBySetOfBooksId(this.props.params.setOfBooksId).then(res => {
      res.data.map(item => {
        if (item.travelTypeFlag) {
          this.setState({
            travelTypeCategoryId: item.id,
          });
        }
      });
    });
  };

  //获取详情
  getInfoById = () => {
    if (this.props.params.id) {
      service
        .getInfoById(this.props.params.id)
        .then(res => {
          let record = { ...res.data };

          record.allTypeFlag ||
            (record.requisitionTypeList = {
              radioValue: record.allTypeFlag,
              chooserValue: res.data.requisitionTypeList.map(o => ({
                id: o.requisitionTypeId,
                name: o.requisitionTypeName,
              })),
            });

          record.visibleUserScope != '1001' &&
            (record.deptOrUserGroupList = {
              type: type[record.visibleUserScope],
              values: res.data.deptOrUserGroupList.map(o => ({
                key: o.id,
                value: o.id,
                label: o.name,
              })),
            });

          this.setState({ loading: false, record }, () => {
            this.getFormList(res.data.setOfBooksId);
          });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  //获取可关联表单类型
  getFormList = (setOfBooksId = this.props.params.setOfBooksId) => {
    service
      .getFormList(setOfBooksId)
      .then(res => {
        this.setState({ formList: res.data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  onCancel = () => {
    this.props.close && this.props.close();
  };

  handleSave = e => {
    e.preventDefault();

    let { record } = this.state;
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (err) return;

      let deptOrUserGroupList = values.visibleUserScope.values.map(o => ({ id: o.value }));
      let requisitionTypeList = values.allTypeFlag.chooserValue.map(o => ({
        requisitionTypeId: o.id,
      }));

      values.allTypeFlag = values.allTypeFlag.radioValue;
      values.visibleUserScope = permissionsType[values.visibleUserScope.type];

      if (!values.allTypeFlag && requisitionTypeList.length <= 0) {
        message.error('请至少选择一个申请类型！');
        return;
      }
      if (values.visibleUserScope == '1002' && deptOrUserGroupList.length <= 0) {
        message.error('请至少选择一个部门！');
        return;
      }
      if (values.visibleUserScope == '1003' && deptOrUserGroupList.length <= 0) {
        message.error('请至少选择一个人员组！');
        return;
      }

      this.setState({ saveLoading: true });
      values = {
        ...values,
        deptOrUserGroupList,
        requisitionTypeList,
      };

      let method = service.addApplicationType;

      if (record.id) {
        method = service.updateApplicationType;
        values = { id: record.id, versionNumber: record.versionNumber, ...values };
      }
      method(values)
        .then(res => {
          this.setState({ saveLoading: false });
          message.success(record.id ? '更新成功！' : '新增成功！');
          this.props.close && this.props.close(true);
        })
        .catch(err => {
          this.setState({ saveLoading: false });
          message.error(err.response.data.message);
        });
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const { formList, saveLoading, record, loading, travelTypeCategoryId } = this.state;
    const { params, setOfBooks } = this.props;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };

    return (
      <div>
        {loading ? (
          <Spin />
        ) : (
          <Form onSubmit={this.handleSave}>
            <div className="common-item-title">
              {this.$t({ id: 'pre.payment.essential.information' }) /*基本信息*/}
            </div>
            <FormItem
              {...formItemLayout}
              label={this.$t({ id: 'pre.payment.setOfBookName' } /*账套*/)}
            >
              {getFieldDecorator('setOfBooksId', {
                rules: [{ required: true, message: '请输入' }],
                initialValue: record.id ? record.setOfBooksId : params.setOfBooksId,
              })(
                <Select disabled>
                  {setOfBooks.map(option => {
                    return <Option key={option.value}>{option.label}</Option>;
                  })}
                </Select>
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="差旅申请单类型代码">
              {getFieldDecorator('code', {
                rules: [{ required: true, message: '请输入' }],
                initialValue: record.code || '',
              })(<Input disabled={!!record.id} />)}
            </FormItem>
            <FormItem {...formItemLayout} label="差旅申请单类型名称">
              {getFieldDecorator('name', {
                rules: [{ required: true, message: '请输入' }],
                initialValue: record.name || '',
              })(<Input />)}
            </FormItem>
            <FormItem
              {...formItemLayout}
              label={
                <span>
                  关联表单类型&nbsp;
                  <Tooltip title="关联表单设计器中的单据类型，用来使用工作流">
                    <Icon type="question-circle-o" />
                  </Tooltip>
                </span>
              }
            >
              {getFieldDecorator('formId', {
                rules: [{ required: true, message: '请选择' }],
                initialValue: record.formId || '',
              })(
                <Select allowClear>
                  {formList.map(item => {
                    return <Option key={item.formId}>{item.formName}</Option>;
                  })}
                </Select>
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="状态">
              {getFieldDecorator('enabled', {
                initialValue: record.id ? record.enabled : true,
                valuePropName: 'checked',
              })(
                <Switch
                  checkedChildren={<Icon type="check" />}
                  unCheckedChildren={<Icon type="close" />}
                />
              )}
            </FormItem>
            <div className="common-item-title">预算管控设置</div>
            <FormItem {...formItemLayout} colon={false} label={<span />}>
              {getFieldDecorator('budgetFlag', {
                initialValue: record.id ? record.budgetFlag : false,
              })(
                <Radio.Group>
                  <Radio value={true}>启用</Radio>
                  <Radio value={false}>不启用</Radio>
                </Radio.Group>
              )}
            </FormItem>
            <div className="common-item-title">申请类型设置</div>
            <FormItem {...formItemLayout} validateTrigger="onBlur" label="可用申请类型">
              {getFieldDecorator('allTypeFlag', {
                initialValue: record.requisitionTypeList || { radioValue: true, chooserValue: [] },
              })(
                <CustomChooser
                  params={{
                    setOfBooksId: record.id ? record.setOfBooksId : params.setOfBooksId,
                    typeCategoryId: travelTypeCategoryId,
                    typeFlag: 0,
                  }}
                  type="application_type"
                  valueKey="id"
                  labelKey="name"
                />
              )}
            </FormItem>
            <div className="common-item-title">行程填写</div>
            <FormItem {...formItemLayout} colon={false} label={<span />}>
              {getFieldDecorator('route', {
                initialValue: record.id ? record.route : true,
              })(
                <Radio.Group>
                  <Radio value={true}>审批前</Radio>
                  <Radio value={false}>审批后</Radio>
                </Radio.Group>
              )}
            </FormItem>
            <div className="common-item-title">权限设置</div>
            <FormItem labelCol={{ span: 8 }} wrapperCol={{ span: 16 }} label="适用人员">
              {getFieldDecorator('visibleUserScope', {
                initialValue: record.deptOrUserGroupList || { type: 'all', values: [] },
              })(
                <PermissionsAllocation
                  params={{ setOfBooksId: record.id ? record.setOfBooksId : params.setOfBooksId }}
                />
              )}
            </FormItem>
            <div className="slide-footer">
              <Button type="primary" htmlType="submit" loading={saveLoading}>
                {this.$t({ id: 'common.save' }) /* 保存 */}
              </Button>
              <Button onClick={this.onCancel}>{this.$t({ id: 'common.cancel' }) /* 取消 */}</Button>
            </div>
          </Form>
        )}
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.login.company,
  };
}
const WrappedNewPrePaymentType = Form.create()(NewApplicationType);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewPrePaymentType);
