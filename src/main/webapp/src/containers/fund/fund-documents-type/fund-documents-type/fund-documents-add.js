import React from 'react';
import { connect } from 'dva';
// import { routerRedux } from 'dva/router';

import {
  Form,
  Switch,
  Icon,
  Input,
  Select,
  Button,
  //   Row,
  //   Col,
  message,
  Spin,
  //   Radio,
  Tooltip,
} from 'antd';

import httpFetch from 'share/httpFetch';
import config from 'config';

import PermissionsAllocation from 'widget/Template/permissions-allocation';
// import FundDocumentsService from './fund-documents.service';

// import SelectCheckSheetType from './select-check-sheet-type';

const FormItem = Form.Item;
const { Option } = Select;
// const RadioButton = Radio.Button;
// const RadioGroup = Radio.Group;

class FundDocumentsAdd extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      //   options: [],
      fetching: false,
      nowType: {},
      setOfBooks: [], // 账套
      relatedType: 'BASIS_01', // 关联报账单的类型
      //   isRelated: true, // 是否关联报账单
      //   accordingAsRelated: 'BASIS_01',
      // showSelectDepartment: false,
      employeeList: [],
      // selectEmployeeText: '',
      applyEmployee: 'BASIS_01',
      // defaultApplyEmployee: 'BASIS_01',
      // isNew: false,
      // editTag: false,
      formTypeOptions: [],
      // showSelectRelated: false,
      relatedList: [],
      // acpReqTypeDefine: '/document-type-manage/payment-requisition-type',
      // relatedListOptions: [],
      // queryFlag: true,
      permissions: {
        type: 'all',
        values: [],
      },
      // list: [],
    };
  }

  componentWillMount() {
    httpFetch.get(`${config.mdataUrl}/api/setOfBooks/by/tenant?roleType=TENANT`).then(res => {
      this.setState({ setOfBooks: res.data });
    });
    // this.getSystemValueList(2105).then(res => {
    //   this.setState({ options: res.data.values });
    // });
    const { params } = this.props;
    console.log(params);
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
    // this.getFormType();
  }

  componentDidMount() {
    const { params } = this.props;
    const {
      applyEmployee,
      // applyEmployee,
      relatedType,
    } = this.state;
    if (params.record.record !== undefined) {
      // eslint-disable-next-line react/no-unused-state
      this.setState({ nowType: params.record.record, editTag: true });
      if (params.record.record !== undefined) {
        httpFetch
          .get(`${config.payUrl}/api/acp/request/type/query/${params.record.record.id}`)
          .then(res => {
            const temp = res.data;
            const relatedLists = [];
            if (temp.paymentRequisitionTypes.relatedType !== 'BASIS_01') {
              // eslint-disable-next-line array-callback-return
              temp.paymentRequisitionTypesToRelateds.map(item => {
                relatedLists.push(item.typeId);
              });
            }
            const applyEmployeeType = {
              BASIS_01: 'all',
              BASIS_02: 'department',
              BASIS_03: 'group',
            };
            const employeeLists = [];
            if (temp.paymentRequisitionTypes.applyEmployee !== 'BASIS_01') {
              // eslint-disable-next-line array-callback-return
              temp.paymentRequisitionTypesToUsers.map(item => {
                employeeLists.push(item.userGroupId);
              });
            }
            this.setState({
              employeeList: employeeLists,
              applyEmployee: temp.paymentRequisitionTypes.applyEmployee,
              relatedType: temp.paymentRequisitionTypes.relatedType,
              //   isRelated: true,
              // queryFlag: false,
              // editTag: true,
              relatedList: relatedLists,
              permissions: {
                type: applyEmployeeType[temp.paymentRequisitionTypes.applyEmployee],
                values: temp.paymentRequisitionTypesToUsers
                  ? temp.paymentRequisitionTypesToUsers.map(item => {
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
        applyEmployee: applyEmployee || 'BASIS_01',
        relatedType: relatedType || 'BASIS_01',
        // isRelated: true,
        // editTag: false,
        // accordingAsRelated: 'BASIS_01',
        // showSelectRelated: false,
      });
    }
  }

  /**
   * 侧边栏form表单中的取消按钮
   */
  onCancel = () => {
    const { form, onClose } = this.props;
    onClose(false);
    form.resetFields();
  };

  /**
   * 侧边栏form表单中的保存按钮
   */
  handleSave = e => {
    e.preventDefault();
    const {
      form,
      // company,
      onClose,
    } = this.props;
    const {
      //   nowType,
      //   formTypeOptions,
      relatedType,
      applyEmployee,
      //   accordingAsRelated,
      //   isRelated,
      employeeList,
      //   applyEmployee,
      relatedList,
      //   relatedType,
    } = this.state;
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        //     values = {
        //       ...nowType,
        //       ...values,
        //     };
        // formTypeOptions.map(item => {
        //   if (item.formOid === values.formOid) {
        //     values.formName = item.formName;
        //     values.formType = item.formType;
        //   }
        // });
        //     values.relatedType = relatedType;
        //     values.applyEmployee = applyEmployee;
        //     values.accordingAsRelated = accordingAsRelated;
        //     values.related = isRelated;
        //     values.tenantId = company.tenantId;
        //     delete values.form;

        const acpRequstTypesToUsers = [];

        if (values.applyEmployee === 'BASIS_01') {
          const userList = {};
          userList.userType = 'BASIS_01';
          acpRequstTypesToUsers.push(userList);
        } else {
          // eslint-disable-next-line array-callback-return
          employeeList.map(item => {
            const userList = {};
            userList.userType = applyEmployee;
            userList.userGroupId = item;
            // userList["pathOrName"] = this.state.list[index].label;
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
        if (values.relatedType === 'BASIS_01') {
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
        // if (values.relatedType === 'BASIS_02' && !acpRequestTypesToRelateds.length) {
        //   message.warning('请至少选择一个可关联报账单类型');
        //   return;
        // }

        const params = {
          paymentRequisitionTypes: values,
          paymentRequisitionTypesToRelateds: acpRequestTypesToRelateds,
          paymentRequisitionTypesToUsers: acpRequstTypesToUsers,
        };
        httpFetch
          .post(`${config.payUrl}/api/acp/request/type`, params)
          .then(res => {
            this.setState({ loading: false });
            message.success(
              this.$t(
                { id: 'common.save.success' },
                { name: res.data.paymentRequisitionTypes.description }
              )
            ); // 保存成功
            onClose(false);
            this.setState({
              nowType: {},
              applyEmployee: 'BASIS_01',
              //   isRelated: true,
              //   accordingAsRelated: 'BASIS_01',
              // selectEmployeeText: '',
              employeeList: [],
              relatedList: [],
              permissions: {
                type: 'all',
                values: [],
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
    const { formTypeOptions, nowType } = this.state;
    const { params } = this.props;
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
            {getFieldDecorator('acpReqTypeCode', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }), // 请输入
                },
              ],
              initialValue: nowType.acpReqTypeCode,
            })(
              <Input
                placeholder={this.$t({ id: 'common.please.enter' }) /* 请输入 */}
                disabled={!!nowType.acpReqTypeCode}
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
                  required: true,
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
