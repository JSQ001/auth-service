import React, { Component } from 'react';
import { Form, Select, Switch, Button, Input, Radio, message } from 'antd';
import Chooser from 'widget/chooser';
import PermissionsAllocation from 'widget/Template/permissions-allocation';
import { connect } from 'dva';
import config from 'config';
import service from './servers';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const { Option } = Select;
const valueType = {
  all: 1001,
  department: 1002,
  group: 1003,
};
const paramsType = {
  1001: 'all',
  1002: 'department',
  1003: 'group',
};

class NewProjectTypeDefinition extends Component {
  constructor(props) {
    super(props);
    this.state = {
      saveLoading: false,
      relevanceList: [],
      projectSelectorItem: {
        title: this.$t('contract.project.type.select') /* 选择项目类型 */,
        url: `${config.contractUrl}/api/project/type/query`,
        searchForm: [
          {
            type: 'input',
            id: 'projectTypeCode',
            label: this.$t('contract.projectTypeCode') /* 项目类型代码 */,
            colSpan: 8,
          },
          {
            type: 'input',
            id: 'projectTypeName',
            label: this.$t('contract.projectTypeName') /* 项目类型名称 */,
            colSpan: 8,
          },
        ],
        columns: [
          {
            title: this.$t('contract.projectTypeCode') /* 项目类型代码 */,
            dataIndex: 'projectTypeCode',
            align: 'center',
          },
          {
            title: this.$t('contract.projectTypeName') /* 项目类型名称 */,
            dataIndex: 'projectTypeName',
            align: 'center',
          },
        ],
        key: 'id',
      },
      projectType: props.params.id ? String(props.params.allTypeFlag) : 'true',
    };
  }

  componentDidMount() {
    this.getFormId();
  }

  // 获取审批流下拉框
  getFormId = () => {
    service
      .getFormId({ formTypeId: 801011 })
      .then(res => this.setState({ relevanceList: res.data }))
      .catch(err => message.error(err.response.data.message));
  };

  // 提交
  onSubmit = e => {
    e.preventDefault();
    const {
      params,
      onClose,
      form: { validateFields },
      company: { tenantId },
    } = this.props;
    validateFields((err, value) => {
      if (!err) {
        const { projectType } = this.state;
        const data = {
          id: params.id,
          tenantId,
          ...value,
          allTypeFlag: projectType,
          projectTypeList:
            value.projectTypeList &&
            value.projectTypeList.map(item => ({ projectTypeId: item.id })),
          formId: value.formId && value.formId.key,
          amountFlag: value.amountFlag === 'true',
          amountNullFlag: value.amountNullFlag && value.amountNullFlag === 'true',
          contractFlag: value.contractFlag === 'true',
          contractNullFlag: value.contractNullFlag && value.contractNullFlag === 'true',
          applyEmployee: valueType[value.applyEmployee.type],
          deptOrUserGroupList:
            value.applyEmployee.values &&
            value.applyEmployee.values.map(item => ({ id: item.key })),
        };
        //校验权限
        const listLen = data['deptOrUserGroupList'].length;
        if (data['applyEmployee'] === 1002 && listLen == 0) {
          message.error('请选择至少一个关于部门的权限');
          this.setState({ saveLoading: false });
          return;
        }
        if (data['applyEmployee'] === 1003 && listLen == 0) {
          message.error('请选择至少一个关于人员组的权限');
          this.setState({ saveLoading: false });
          return;
        }
        const method = params.id ? service.updateProjectType : service.addProjectType;
        method(data)
          .then(() => {
            onClose(true);
            message.success(this.$t('structure.saveSuccess'));
          })
          .catch(error => message.error(error.response.data.message));
      }
    });
  };

  // 项目类型 部分，全部切换
  projectTypeChange = e => {
    this.setState({ projectType: e.target.value });
  };

  render() {
    const { saveLoading, relevanceList, projectType, projectSelectorItem } = this.state;
    const { params, setOfBooksId, setOfBooksIdList, onClose } = this.props;
    const {
      form: { getFieldDecorator, getFieldValue },
    } = this.props;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };
    const formItemLayout2 = {
      labelCol: { span: 8 },
      wrapperCol: { span: 16 },
    };

    return (
      <Form onSubmit={this.onSubmit}>
        <div className="common-item-title clear">{this.$t('common.baseInfo') /* 基本信息 */}</div>
        <FormItem {...formItemLayout} label={this.$t('chooser.data.setOfBooks') /* 账套 */}>
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
        <FormItem
          {...formItemLayout}
          label={this.$t('contract.project.type.code') /* 项目申请单类型代码 */}
        >
          {getFieldDecorator('projectReqTypeCode', {
            rules: [
              {
                required: true,
                message: this.$t('common.please.enter'),
              },
            ],
            initialValue: params.id && params.projectReqTypeCode,
          })(<Input placeholder={this.$t('common.please.enter')} disabled={!!params.id} />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={this.$t('contract.project.type.name') /* 项目申请单类型名称 */}
        >
          {getFieldDecorator('projectReqTypeName', {
            rules: [
              {
                required: true,
                message: this.$t('common.please.enter'),
              },
            ],
            initialValue: params.id && params.projectReqTypeName,
          })(<Input placeholder={this.$t('common.please.enter')} />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={this.$t('contract.project.relevance.approve') /* 关联审批流 */}
        >
          {getFieldDecorator('formId', {
            rules: [
              {
                required: true,
                message: this.$t('common.please.select'),
              },
            ],
            initialValue: params.id && { key: params.formId, label: params.formName },
          })(
            <Select
              placeholder={this.$t('common.please.select')}
              getPopupContainer={node => node.parentNode}
              labelInValue
            >
              {relevanceList.map(item => {
                return <Option key={item.formId}>{item.formName}</Option>;
              })}
            </Select>
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

        <div className="common-item-title clear">
          {this.$t('contract.project.type.setting') /* 项目类型设置 */}
        </div>
        <FormItem
          {...formItemLayout}
          label={this.$t('contract.project.type.canUse') /* 可用项目类型 */}
        >
          <RadioGroup value={projectType} onChange={this.projectTypeChange}>
            <Radio value="true">{this.$t('common.all.type') /* 全部类型 */}</Radio>
            <Radio value="false" style={{ marginLeft: '30px' }}>
              {this.$t('visibleExpenseTypeScope.part') /* 部分类型 */}
            </Radio>
          </RadioGroup>
          {projectType === 'true' ? (
            <Input disabled placeholder={this.$t('common.all.type') /* 全部类型 */} />
          ) : (
            getFieldDecorator('projectTypeList', {
              initialValue:
                params.projectTypeList &&
                params.projectTypeList.map(item => ({ id: item.projectTypeId })),
            })(
              <Chooser
                selectorItem={projectSelectorItem}
                listExtraParams={{ enabled: true, setOfBooksId }}
                showDetail={false}
                showNumber
                valueKey="id"
                labelKey="name"
              />
            )
          )}
        </FormItem>

        <div className="common-item-title clear">
          {this.$t('contract.project.contract.relevance') /* 关联合同设置 */}
        </div>
        <FormItem
          {...formItemLayout}
          label={this.$t('contract.project.is.relevance') /* 是否关联 */}
        >
          {getFieldDecorator('contractFlag', {
            initialValue: params.id ? String(params.contractFlag) : 'false',
          })(
            <RadioGroup>
              <Radio value="true">{this.$t('contract.project.relevance.true') /* 可关联 */}</Radio>
              <Radio value="false" style={{ marginLeft: '30px' }}>
                {this.$t('contract.project.relevance.false') /* 可关联 */}
              </Radio>
            </RadioGroup>
          )}
        </FormItem>
        {getFieldValue('contractFlag') === 'true' && (
          <FormItem
            {...formItemLayout}
            label={this.$t('contract.project.contract.required') /* 合同是否必输 */}
          >
            {getFieldDecorator('contractNullFlag', {
              initialValue: params.id ? String(params.contractNullFlag) : 'false',
            })(
              <RadioGroup>
                <Radio value="true">{this.$t('common.required.true') /* 必输 */}</Radio>
                <Radio value="false" style={{ marginLeft: '30px' }}>
                  {this.$t('common.required.false') /* 非必输 */}
                </Radio>
              </RadioGroup>
            )}
          </FormItem>
        )}

        <div className="common-item-title clear">
          {this.$t('contract.project.amount.setting') /* 金额设置 */}
        </div>
        <FormItem {...formItemLayout} label={this.$t('contract.project.is.show') /* 是否显示 */}>
          {getFieldDecorator('amountFlag', {
            initialValue: params.id ? String(params.amountFlag) : 'false',
          })(
            <RadioGroup>
              <Radio value="true">{this.$t('contract.project.show.true') /* 可显示 */}</Radio>
              <Radio value="false" style={{ marginLeft: '30px' }}>
                {this.$t('contract.project.show.false') /* 不可显示 */}
              </Radio>
            </RadioGroup>
          )}
        </FormItem>
        {getFieldValue('amountFlag') === 'true' && (
          <FormItem
            {...formItemLayout}
            label={this.$t('contract.project.amount.required') /* 金额是否必输 */}
          >
            {getFieldDecorator('amountNullFlag', {
              initialValue: params.id ? String(params.amountNullFlag) : 'false',
            })(
              <RadioGroup>
                <Radio value="true">{this.$t('common.required.true') /* 必输 */}</Radio>
                <Radio value="false" style={{ marginLeft: '30px' }}>
                  {this.$t('common.required.false') /* 非必输 */}
                </Radio>
              </RadioGroup>
            )}
          </FormItem>
        )}

        <div className="common-item-title clear">
          {this.$t('adjust.authority.set') /* 权限设置 */}
        </div>
        <FormItem
          {...formItemLayout2}
          label={this.$t('adjust.applicable.personnel') /* "适用人员" */}
        >
          {getFieldDecorator('applyEmployee', {
            initialValue: params.applyEmployee
              ? {
                  type: paramsType[params.applyEmployee],
                  values: params.deptOrUserGroupList
                    ? params.deptOrUserGroupList.map(item => ({ key: item.id }))
                    : [],
                }
              : { type: 'all', values: [] },
          })(<PermissionsAllocation />)}
        </FormItem>
        <div className="slide-footer">
          <Button type="primary" htmlType="submit" loading={saveLoading}>
            {this.$t('common.save')}
          </Button>
          <Button onClick={() => onClose()}>{this.$t('common.cancel')}</Button>
        </div>
      </Form>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

const WrapNewProjectTypeDefinition = Form.create()(NewProjectTypeDefinition);

export default connect(mapStateToProps)(WrapNewProjectTypeDefinition);
