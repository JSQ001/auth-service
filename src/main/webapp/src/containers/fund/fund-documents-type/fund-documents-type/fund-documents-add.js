import React from 'react';
import { connect } from 'dva';
import { Form, Switch, Icon, Input, Select, Button, message, Spin, Tooltip } from 'antd';
import httpFetch from 'share/httpFetch';
import config from 'config';
import PermissionsAllocation from 'widget/Template/permissions-allocation';

const FormItem = Form.Item;
const { Option } = Select;

class FundDocumentsAdd extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      fetching: false,
      nowType: {},
      setOfBooks: [], // 账套
      relatedType: 'BASIS_01', // 关联报账单的类型
      isRelated: true, // 是否关联报账单
      accordingAsRelated: 'BASIS_01',
      employeeList: [],
      applyEmployee: 'BASIS_01',
      formTypeOptions: [],
      relatedList: [],
      permissions: {
        type: 'all',
        values: [],
      },
    };
  }

  componentWillMount() {
    const { params } = this.props;
    httpFetch.get(`${config.mdataUrl}/api/setOfBooks/by/tenant?roleType=TENANT`).then(res => {
      this.setState({ setOfBooks: res.data });
    });
    this.getSystemValueList(2105).then(() => {});
    httpFetch
      .get(
        `${
          config.workflowUrl
        }/api/custom/forms/setOfBooks/my/available/all?formTypeId=801005&setOfBooksId=${
          params.record.setOfBooksId
            ? params.record.setOfBooksId
            : params.record.record.setOfBooksId
        }`
      )
      .then(res => {
        this.setState({ formTypeOptions: res.data, fetching: false });
      });
  }

  componentDidMount() {
    const { params } = this.props;
    const { relatedType } = this.state;
    if (params.record.record !== undefined) {
      this.setState({
        nowType: params.record.record,
      });
      if (params.record.record !== undefined) {
        httpFetch
          // .get(
          //   `http://10.211.110.100:9099/api/setting/request/type/query/${params.record.record.id}`
          // )
          .get(`${config.fundUrl}/api/setting/request/type/query/${params.record.record.id}`)
          .then(res => {
            const temp = res.data;
            const relatedLists = [];
            if (temp.fundReqTypes && temp.fundReqTypes.relatedType !== 'BASIS_01') {
              if (temp.fundReqTypesToRelateds) {
                // eslint-disable-next-line array-callback-return
                temp.fundReqTypesToRelateds.map(item => {
                  relatedLists.push(item.typeId);
                });
              }
            }
            const applyEmployeeType = {
              BASIS_01: 'all',
              BASIS_02: 'department',
              BASIS_03: 'group',
            };
            const employeeLists = [];
            if (temp.fundReqTypes && temp.fundReqTypes.applyEmployee !== 'BASIS_01') {
              if (temp.fundReqTypesToUsers) {
                // eslint-disable-next-line array-callback-return
                temp.fundReqTypesToUsers.map(item => {
                  employeeLists.push(item.userGroupId);
                });
              }
            }
            this.setState({
              employeeList: employeeLists,
              applyEmployee: temp.fundReqTypes && temp.fundReqTypes.applyEmployee,
              relatedType: temp.fundReqTypes && temp.fundReqTypes.relatedType,
              isRelated: true,
              relatedList: relatedLists,
              permissions: {
                type: applyEmployeeType[temp.fundReqTypes && temp.fundReqTypes.applyEmployee],
                values: temp.fundReqTypesToUsers
                  ? temp.fundReqTypesToUsers.map(item => {
                      return {
                        label: item.pathOrName,
                        value: item.userGroupId,
                        key: item.userGroupId,
                      };
                    })
                  : [],
              },
            });
          });
      }
    } else {
      this.setState({
        nowType: {},
        relatedType: relatedType || 'BASIS_01',
        isRelated: true,
        accordingAsRelated: 'BASIS_01',
      });
    }
  }

  /**
   * form表单中的取消按钮
   */
  onCancel = () => {
    const { onClose, form } = this.props;
    onClose(false);
    form.resetFields();
  };

  /**
   * form表单中的保存按钮
   */
  handleSave = e => {
    e.preventDefault();
    const { form, company, onClose } = this.props;
    const {
      nowType,
      formTypeOptions,
      relatedType,
      applyEmployee,
      accordingAsRelated,
      isRelated,
      employeeList,
      relatedList,
    } = this.state;
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        let value = values;
        value = {
          ...nowType,
          ...value,
        };
        // eslint-disable-next-line array-callback-return
        formTypeOptions.map(item => {
          if (item.formOid === value.formOid) {
            value.formName = item.formName;
            value.formType = item.formType;
          }
        });
        value.relatedType = relatedType;
        value.applyEmployee = applyEmployee;
        value.accordingAsRelated = accordingAsRelated;
        value.related = isRelated;
        value.tenantId = company.tenantId;
        delete value.form;

        const acpRequstTypesToUsers = [];
        if (value.applyEmployee === 'BASIS_01') {
          const userList = {};
          userList.userType = 'BASIS_01';
          acpRequstTypesToUsers.push(userList);
        } else {
          // eslint-disable-next-line array-callback-return
          employeeList.map(item => {
            const userList = {};
            userList.userType = applyEmployee;
            userList.userGroupId = item;
            acpRequstTypesToUsers.push(userList);
          });
        }
        if (applyEmployee === 'BASIS_02' && !acpRequstTypesToUsers.length) {
          message.warning('请至少选择一个部门');
          return;
        }
        if (applyEmployee === 'BASIS_03' && !acpRequstTypesToUsers.length) {
          message.warning('请至少选择一个员工组');
          return;
        }
        const acpRequestTypesToRelateds = [];
        if (value.relatedType === 'BASIS_01') {
          const relatedLists = {};
          relatedLists.relatedType = 'BASIS_01';
          acpRequestTypesToRelateds.push(relatedLists);
        } else {
          // eslint-disable-next-line array-callback-return
          relatedList.map(item => {
            const relatedLists = {};
            relatedLists.relatedType = relatedType;
            relatedLists.typeId = item;
            acpRequestTypesToRelateds.push(relatedLists);
          });
        }
        if (value.relatedType === 'BASIS_02' && !acpRequestTypesToRelateds.length) {
          message.warning('请至少选择一个可关联报账单类型');
          return;
        }
        const params = {
          fundReqTypes: value,
          fundReqTypesToUsersList: acpRequstTypesToUsers,
        };
        httpFetch
          // .post(`http://10.211.110.100:9099/api/setting/request/type`, params)
          .post(`${config.fundUrl}/api/setting/request/type`, params)
          .then(res => {
            this.setState({ loading: false });
            message.success(
              this.$t({ id: 'common.save.success' }, { name: res.data.fundReqTypes.description })
            ); // 保存成功
            onClose(false);
            this.setState({
              nowType: {},
              applyEmployee: 'BASIS_01',
              isRelated: true,
              accordingAsRelated: 'BASIS_01',
              employeeList: [],
              relatedList: [],
              permissions: {
                type: 'all',
                value: [],
              },
            });
          })
          .catch(() => {
            if (e.response) {
              message.error(`保存失败, ${e.response.data.message}`);
            }
            this.setState({ loading: false });
          });
      }
    });
  };

  /**
   * 基本信息：获取关联表单类型
   */
  getFormType = () => {
    const { params } = this.props;
    const { formTypeOptions, nowType } = this.state;
    if (formTypeOptions.length) return;
    this.setState({ fetching: true });
    let setOfBooksId = !nowType.id ? params.record.setOfBooksId : nowType.setOfBooksId;
    if (!setOfBooksId) {
      setOfBooksId = 0;
    }
    httpFetch
      .get(
        `${
          config.workflowUrl
        }/api/custom/forms/setOfBooks/my/available/all?formTypeId=801005&setOfBooksId=${setOfBooksId}`
      )
      .then(res => {
        this.setState({ formTypeOptions: res.data, fetching: false });
      });
  };

  /**
   * 权限设置：选择人员或者员工组使用公共组件，监听onChange事件
   */
  onPermissionChange = values => {
    let nowApplyEmployee = '';
    const nowDepartOrUserIdList = [];
    if (values.type === 'all') {
      nowApplyEmployee = 'BASIS_01';
    } else if (values.type === 'department') {
      nowApplyEmployee = 'BASIS_02';
    } else if (values.type === 'group') {
      nowApplyEmployee = 'BASIS_03';
    }
    // eslint-disable-next-line array-callback-return
    values.values.map(value => {
      nowDepartOrUserIdList.push(value.value);
    });
    this.setState({
      applyEmployee: nowApplyEmployee,
      employeeList: nowDepartOrUserIdList,
      // eslint-disable-next-line react/no-unused-state
      list: values.values || [],
    });
  };

  render() {
    const { form, params } = this.props;
    const { getFieldDecorator } = form;
    const { nowType, fetching, setOfBooks, loading, formTypeOptions, permissions } = this.state;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10, offset: 1 },
    };
    const formLabel = (
      <span>
        关联表单类型
        <Tooltip title="关联表单设计器中的单据类型，用来使用工作流" overlayStyle={{ width: 220 }}>
          <Icon type="info-circle-o" style={{ margin: '0 3px' }} />
        </Tooltip>
      </span>
    );

    return (
      <div>
        <Form onSubmit={this.handleSave}>
          <div className="common-item-title">基本信息</div>

          <FormItem {...formItemLayout} label="账套">
            {getFieldDecorator('setOfBooksId', {
              rules: [
                {
                  required: true,
                },
              ],
              initialValue: !nowType.id ? params.record.setOfBooksId : nowType.setOfBooksId,
            })(
              <Select
                placeholder={this.$t({ id: 'common.please.select' }) /* 请选择 */}
                notFoundContent={<Spin size="small" />}
                disabled
              >
                {setOfBooks.map(option => {
                  return (
                    <Option key={option.id}>
                      {option.setOfBooksCode}-{option.setOfBooksName}
                    </Option>
                  );
                })}
              </Select>
            )}
          </FormItem>

          <FormItem {...formItemLayout} label="单据类型代码">
            {getFieldDecorator('cpReqTypeCode', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }), // 请输入
                },
              ],
              initialValue: nowType.cpReqTypeCode,
            })(
              <Input
                placeholder={this.$t({ id: 'common.please.enter' }) /* 请输入 */}
                disabled={!!nowType.cpReqTypeCode}
              />
            )}
          </FormItem>

          <FormItem {...formItemLayout} label="单据类型名称">
            {getFieldDecorator('description', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }), // 请输入
                },
              ],
              initialValue: nowType.description,
            })(<Input placeholder={this.$t({ id: 'common.please.enter' }) /* 请输入 */} />)}
          </FormItem>

          <FormItem {...formItemLayout} label={formLabel}>
            {getFieldDecorator('formOid', {
              rules: [
                {
                  required: false,
                  message: this.$t({ id: 'common.please.select' }), // 请输入
                },
              ],
              initialValue: nowType.formOid,
            })(
              <Select
                placeholder="请选择"
                onFocus={this.getFormType}
                notFoundContent={fetching ? <Spin size="small" /> : '无匹配结果'}
              >
                {formTypeOptions.map(option => {
                  return <Option key={option.formOid}>{option.formName}</Option>;
                })}
              </Select>
            )}
          </FormItem>

          <FormItem {...formItemLayout} label={this.$t({ id: 'common.column.status' }) /* 状态 */}>
            {getFieldDecorator('enabled', {
              initialValue: !nowType.id ? true : nowType.enabled,
              valuePropName: 'checked',
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
              />
            )}&nbsp;&nbsp;&nbsp;&nbsp;{form.getFieldValue('enabled')
              ? this.$t({ id: 'common.status.enable' })
              : this.$t({ id: 'common.status.disable' })}
          </FormItem>

          <div className="common-item-title">权限设置</div>
          <FormItem {...formItemLayout} label="适用人员">
            {getFieldDecorator('employeeList', {
              initialValue: permissions,
            })(
              <PermissionsAllocation
                params={{
                  setOfBooksId: !nowType.id ? params.record.setOfBooksId : nowType.setOfBooksId,
                }}
                onChange={this.onPermissionChange}
              />
            )}
          </FormItem>

          <div className="slide-footer">
            <Button type="primary" htmlType="submit" loading={loading}>
              {this.$t({ id: 'common.save' }) /* 保存 */}
            </Button>
            <Button onClick={this.onCancel}>{this.$t({ id: 'common.cancel' }) /* 取消 */}</Button>
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
const WrappedFundDocumentsAdd = Form.create()(FundDocumentsAdd);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedFundDocumentsAdd);
