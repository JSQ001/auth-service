import React, { Component } from 'react';
import config from 'config';
import { Button, Form, Input, Select, Radio, Switch, message, Tooltip, Icon } from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
import { connect } from 'dva';
import baseService from 'share/base.service';
import glWorkOrderTypeService from './gl-work-order-type.service';
import ListSelector from 'components/Widget/list-selector';
import PermissionsAllocation from 'widget/Template/permissions-allocation';
import Chooser from 'components/Widget/chooser';
class NewGLWorkOrderType extends Component {
  /**
   * 构造函数
   */
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      //存储上个页面传过来的当前行
      nowTypeList: {},
      //存储上个页面条件查询中选中的账套
      setOfBooksId: '',
      //账套列表数据
      setOfBooksIdList: [],
      //关联表单类型
      relatedFormTypeList: [],
      /**
       * 需清空字段
       */
      /**
       * 科目
       */
      accountScope: '1001',
      accountIdList: [],
      accountVisible: false,
      /**
       *核算公司设置
       */
      /**
       * 适用人员-权限设置
       */
      permissions: {
        type: 'all',
        values: [],
      },
      visibleUserScope: '1001',
      departmentOrUserGroupIdList: [],
      allResponsibilityCenter: 'Y',
      defaultList: [],
      ids: [],
      allDefaultList: [],
    };
  }
  /**
   * 生命周期函数
   */
  componentWillMount = () => {
    this.getSetOfBookList();
    this.getRelatedFormList();
  };

  componentDidMount = () => {
    const applyEmployeeType = {
      '1001': 'all',
      '1003': 'department',
      '1002': 'group',
    };
    if (this.props.params.glWorkOrderTypeList.id) {
      //编辑时
      glWorkOrderTypeService
        .getTypeById(this.props.params.glWorkOrderTypeList.id)
        .then(res => {
          if (res.status === 200) {
            //当前数据
            let nowTypeList = res.data.generalLedgerWorkOrderType;
            this.setState({
              setOfBooksId: this.props.params.setOfBooksId,
              nowTypeList: nowTypeList,
            });
            //科目
            let accountIdList_value = [];
            res.data.accountIdList &&
              res.data.accountIdList.map(item => {
                accountIdList_value.push({ id: item });
              });
            //人员权限
            let departmentOrUserGroupIdList_value = res.data.departmentOrUserGroupIdList
              ? res.data.departmentOrUserGroupIdList
              : [];
            const ids = res.data.responsibilityCenterList || [];
            this.setState(
              {
                accountScope: JSON.stringify(nowTypeList.accountScope),
                visibleUserScope: JSON.stringify(nowTypeList.visibleUserScope),
                accountIdList: accountIdList_value,
                departmentOrUserGroupIdList: departmentOrUserGroupIdList_value,
                permissions: {
                  type: applyEmployeeType[nowTypeList.visibleUserScope],
                  values: res.data.departmentOrUserGroupList
                    ? res.data.departmentOrUserGroupList.map(item => {
                        return {
                          label: item.name,
                          value: item.id,
                          key: item.id,
                        };
                      })
                    : [],
                },
                defaultList: ids,
                ids,
                allResponsibilityCenter: nowTypeList.allResponsibilityCenter,
              },
              () => {
                let flag = nowTypeList.allResponsibilityCenter === 'Y' ? true : false;
                this.getResponsibleCenter(flag);
              }
            );
          }
        })
        .catch(e => {
          console.log(`获取核算工单类型详情失败：${e}`);
          if (e.response) {
            message.error(`获取核算工单类型详情失败：${e.response.data.message}`);
          }
        });
    } else {
      //新增时
      let nowTypeList = this.props.params.glWorkOrderTypeList;
      this.setState({
        setOfBooksId: this.props.params.setOfBooksId,
        nowTypeList: nowTypeList,
      });
      this.getResponsibleCenter(true);
    }
  };
  /**
   * 获取关联表单类型
   */
  getRelatedFormList = () => {
    glWorkOrderTypeService
      .getRelatedFormList(this.props.company.setOfBooksId)
      .then(res => {
        if (res.status === 200) {
          this.setState({
            relatedFormTypeList: res.data,
          });
        }
      })
      .catch(e => {
        console.log(`获取关联表单类型失败：${e}`);
      });
  };
  /**
   * 获取账套列表数据
   */
  getSetOfBookList = () => {
    baseService
      .getSetOfBooksByTenant()
      .then(res => {
        if (res.status === 200) {
          this.setState({
            setOfBooksIdList: res.data,
          });
        }
      })
      .catch(e => {
        console.log(`获取账套列表数据失败：${e}`);
      });
  };
  /**
   * 取消（保存旁边的按钮）
   */
  onSliderFormCancel = () => {
    this.props.onClose();
  };
  /**
   * 保存方法
   */
  onSubmit = e => {
    e.preventDefault();
    this.setState({ loading: true });

    //当前传过来的数据
    let { nowTypeList } = this.state;
    //科目
    let { accountScope, accountIdList } = this.state;
    let accountIdList_id = [];
    accountIdList &&
      accountIdList.map(item => {
        accountIdList_id.push(item.id);
      });
    //适用人员
    let { visibleUserScope, departmentOrUserGroupIdList, allResponsibilityCenter } = this.state;

    //验证科目
    if (accountScope === '1002' && accountIdList_id.length == 0) {
      message.warning('请选择至少一个可用科目');
      this.setState({ loading: false });
      return;
    }
    if (allResponsibilityCenter === 'N' && !this.props.form.getFieldValue('ids')) {
      message.warning('请选择至少一个责任中心');
    }

    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        values = {
          ...this.state.nowTypeList,
          ...values,
        };
        let params = {
          generalLedgerWorkOrderType: {
            id: nowTypeList.id,
            setOfBooksId: values.setOfBooksId,
            tenantId: this.props.company.tenantId,
            workOrderTypeCode: values.workOrderTypeCode,
            workOrderTypeName: values.workOrderTypeName,
            i18n: {},
            formOid: values.formOid.substring(0, values.formOid.indexOf('$$')),
            formName: values.formOid.substring(
              values.formOid.indexOf('$$') + 2,
              values.formOid.length
            ),
            accountScope: accountScope,
            visibleUserScope: visibleUserScope,
            visibleCompany: values.visibleCompany,
            enabled: values.enabled,
            responsibilityCenterRequired: values.responsibilityCenterRequired,
            allResponsibilityCenter: allResponsibilityCenter,
            defaultResponsibilityCenter:
              values.defaultResponsibilityCenter && values.defaultResponsibilityCenter.key,
          },
          accountIdList: accountIdList_id,
          departmentOrUserGroupIdList: departmentOrUserGroupIdList,
          responsibilityCenterIdList: values.ids && values.ids.map(item => item.id),
        };
        if (nowTypeList.id) {
          params.generalLedgerWorkOrderType.versionNumber = nowTypeList.versionNumber;
        }
        //适用人员
        if (
          this.state.visibleUserScope == '1003' &&
          !this.state.departmentOrUserGroupIdList.length
        ) {
          this.setState({ loading: false });
          message.warning(this.$t('adjust.departmentGroupIdList.warn' /*请至少选择一个部门*/));
          return;
        }
        if (
          this.state.visibleUserScope == '1002' &&
          !this.state.departmentOrUserGroupIdList.length
        ) {
          this.setState({ loading: false });
          message.warning(this.$t('adjust.userGroupIdList.warn' /*请至少选择一个员工组*/));
          return;
        }
        delete values.departmentOrUserGroupIdList;
        values.departmentOrUserGroupIdList =
          this.state.visibleUserScope == '1001' ? [] : this.state.departmentOrUserGroupIdList;
        if (nowTypeList.id) {
          //编辑时
          glWorkOrderTypeService
            .typeUpdate(params)
            .then(res => {
              if (res.status === 200) {
                message.success('保存成功');
                this.setState({
                  loading: false,
                });
                this.props.onClose(true);
              }
            })
            .catch(e => {
              console.log(`保存失败：${e}`);
              if (e.response) {
                message.error(`保存失败：${e.response.data.message}`);
              }
              this.setState({
                loading: false,
              });
            });
        } else {
          //新增时
          glWorkOrderTypeService
            .typeInsert(params)
            .then(res => {
              if (res.status === 200) {
                message.success('保存成功');
                this.setState({
                  loading: false,
                });
                this.props.onClose(true);
              }
            })
            .catch(e => {
              console.log(`保存失败：${e}`);
              if (e.response) {
                message.error(`保存失败：${e.response.data.message}`);
              }
              this.setState({
                loading: false,
              });
            });
        }
      } else {
        this.setState({ loading: false });
      }
    });
  };
  /**
   * 科目切换事件
   */
  onAccountChange = e => {
    this.setState({
      accountScope: e.target.value,
      accountIdList: [],
    });
  };
  /**
   * 打开科目弹窗
   */
  onAccountClick = () => {
    this.refs.SelectAccount.blur();
    this.setState({
      accountVisible: true,
    });
  };
  /**
   * 科目弹窗取消
   */
  onAccountCancel = () => {
    this.setState({
      accountVisible: false,
    });
  };
  /**
   * 科目弹窗确定
   */
  onAccountOk = value => {
    this.setState({
      accountIdList: value.result,
      accountVisible: false,
    });
  };
  /**
   * 权限设置
   */
  onPermissionChange = values => {
    let nowApplyEmployee = '';
    let nowDepartOrUserIdList = [];
    if (values.type == 'all') {
      nowApplyEmployee = '1001';
    } else if (values.type == 'department') {
      nowApplyEmployee = '1003';
    } else if (values.type == 'group') {
      nowApplyEmployee = '1002';
    }
    values.values.map(value => {
      nowDepartOrUserIdList.push(value['value']);
    });
    this.setState({
      visibleUserScope: nowApplyEmployee,
      departmentOrUserGroupIdList: nowDepartOrUserIdList,
    });
  };

  // 或者账套下的 责任中心
  getResponsibleCenter = flag => {
    glWorkOrderTypeService
      .getResponsibleCenter({ setOfBooksId: this.props.params.setOfBooksId })
      .then(res => {
        const { defaultList } = this.state;
        this.setState({ allDefaultList: res.data, defaultList: flag ? res.data : defaultList });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 可用责任中心选择
  onResponsibilityCenter = e => {
    this.setState({ allResponsibilityCenter: e.target.value });
    this.resetDefault();
    if (e.target.value == 'Y') {
      const { allDefaultList } = this.state;
      this.setState({ defaultList: allDefaultList, ids: undefined });
    } else {
      this.setState({ defaultList: [] });
    }
  };

  // 可用责任中心切换
  onResponsibilityChange = value => {
    this.setState({ defaultList: value });
    this.resetDefault();
  };

  // 清空默认责任中心
  resetDefault = () => {
    this.props.form.setFieldsValue({ defaultResponsibilityCenter: undefined });
  };

  /**
   * 渲染函数
   */
  render() {
    //按钮控制
    const { loading, defaultList, allResponsibilityCenter, ids } = this.state;
    //上个页面传递过来的数据
    const { nowTypeList, setOfBooksId } = this.state;
    //列表数据-账套，关联表单类型
    const { setOfBooksIdList, relatedFormTypeList } = this.state;
    //科目
    const { accountScope, accountVisible, accountIdList } = this.state;
    //公司设置
    const radioStyle = {
      display: 'block',
      height: '30px',
      lineHeight: '30px',
    };
    //权限设置
    const { permissions } = this.state;
    //表单
    const { getFieldDecorator } = this.props.form;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10, offset: 1 },
    };
    const accountParams = nowTypeList.id
      ? {
          setOfBooksId: nowTypeList.id ? nowTypeList.setOfBooksId : setOfBooksId,
          id: nowTypeList.id,
        }
      : { setOfBooksId: nowTypeList.id ? nowTypeList.setOfBooksId : setOfBooksId };
    const paramsList = this.props.params.glWorkOrderTypeList;
    const selectorItem = {
      title: '可用责任中心',
      url: `${config.mdataUrl}/api/responsibilityCenter/query/default?setOfBooksId=${setOfBooksId}`,
      searchForm: [
        {
          type: 'input',
          id: 'info',
          label: '责任中心',
          colSpan: 8,
          placeholder: '请输入代码或名称',
        },
        { type: 'input', id: 'codeFrom', label: '责任中心代码从', colSpan: 8 },
        { type: 'input', id: 'codeTo', label: '责任中心代码至', colSpan: 8 },
      ],
      columns: [
        { title: '责任中心代码', dataIndex: 'responsibilityCenterCode', align: 'center' },
        { title: '责任中心名称', dataIndex: 'responsibilityCenterName', align: 'center' },
      ],
      key: 'id',
    };

    return (
      <div>
        <Form onSubmit={this.onSubmit}>
          <div className="common-item-title">基本信息</div>
          <FormItem {...formItemLayout} label="账套">
            {getFieldDecorator('setOfBooksId', {
              rules: [
                {
                  required: true,
                },
              ],
              initialValue: nowTypeList.id ? nowTypeList.setOfBooksId : setOfBooksId,
            })(
              <Select disabled>
                {setOfBooksIdList.map(item => {
                  return (
                    <Option key={item.id}>
                      {item.setOfBooksCode}-{item.setOfBooksName}
                    </Option>
                  );
                })}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="核算工单类型代码">
            {getFieldDecorator('workOrderTypeCode', {
              rules: [
                {
                  required: true,
                  message: '请输入',
                },
              ],
              initialValue: nowTypeList.id ? nowTypeList.workOrderTypeCode : '',
            })(<Input placeholder="请输入" disabled={!!nowTypeList.id} />)}
          </FormItem>
          <FormItem {...formItemLayout} label="核算工单类型名称">
            {getFieldDecorator('workOrderTypeName', {
              rules: [
                {
                  required: true,
                  message: '请输入',
                },
              ],
              initialValue: nowTypeList.id ? nowTypeList.workOrderTypeName : '',
            })(<Input placeholder="请输入" />)}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={
              <span>
                关联表单类型
                <Tooltip placement="topRight" title="关联表单设计器中的单据类型，用来使用工作流。">
                  <Icon type="info-circle-o" />
                </Tooltip>
              </span>
            }
          >
            {getFieldDecorator('formOid', {
              rules: [
                {
                  required: true,
                  message: '请选择',
                },
              ],
              initialValue: nowTypeList.id ? nowTypeList.formOid + '$$' + nowTypeList.formName : '',
            })(
              <Select placeholder="请选择" getPopupContainer={node => node.parentNode}>
                {relatedFormTypeList.map(item => {
                  return <Option key={item.formOid + '$$' + item.formName}>{item.formName}</Option>;
                })}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="状态">
            {getFieldDecorator('enabled', {
              rules: [
                {
                  required: true,
                },
              ],
              initialValue: nowTypeList.id ? nowTypeList.enabled : true,
              valuePropName: 'checked',
            })(<Switch />)}&nbsp;&nbsp;&nbsp;
            {this.props.form.getFieldValue('enabled') ? '启用' : '禁用'}
          </FormItem>
          <div className="common-item-title">科目设置</div>
          <FormItem {...formItemLayout} label="可用科目">
            <RadioGroup value={accountScope} onChange={this.onAccountChange}>
              <Radio value="1001">全部科目</Radio>
              <Radio value="1002">部分科目</Radio>
            </RadioGroup>
            <Input
              ref="SelectAccount"
              onFocus={this.onAccountClick}
              placeholder="请选择"
              disabled={accountScope === '1001' ? true : false}
              value={accountScope === '1001' ? '全部科目' : `已选择了${accountIdList.length}个科目`}
            />
          </FormItem>
          <div className="common-item-title">责任中心设置</div>
          <FormItem {...formItemLayout} label="是否必填">
            {getFieldDecorator('responsibilityCenterRequired', {
              initialValue: nowTypeList.id ? nowTypeList.responsibilityCenterRequired : 'Y',
            })(
              <RadioGroup>
                <Radio value="Y">必填</Radio>
                <Radio value="N">非必填</Radio>
              </RadioGroup>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="可用责任中心" required>
            <RadioGroup value={allResponsibilityCenter} onChange={this.onResponsibilityCenter}>
              <Radio value="Y">全部</Radio>
              <Radio value="N">部分</Radio>
            </RadioGroup>
            {allResponsibilityCenter === 'Y' ? (
              <Input disabled placeholder="全部" />
            ) : (
              getFieldDecorator('ids', {
                rules: [
                  {
                    required: true,
                    message: '请选择至少一个责任中心',
                  },
                ],
                initialValue: ids,
              })(
                <Chooser
                  placeholder="请选择责任中心"
                  selectorItem={selectorItem}
                  showDetail={false}
                  showNumber={true}
                  onChange={this.onResponsibilityChange}
                  valueKey="id"
                  labelKey="defaultResponsibilityCenterName"
                />
              )
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="默认责任中心">
            {getFieldDecorator('defaultResponsibilityCenter', {
              initialValue: nowTypeList.defaultResponsibilityCenter
                ? {
                    key: nowTypeList.defaultResponsibilityCenter,
                    label: nowTypeList.defaultResponsibilityCenterName,
                  }
                : undefined,
            })(
              <Select placeholder="请选择" labelInValue getPopupContainer={node => node.parentNode}>
                {defaultList &&
                  defaultList.map(item => {
                    return <Option key={item.id}>{item.responsibilityCenterCodeName}</Option>;
                  })}
              </Select>
            )}
          </FormItem>
          <div className="common-item-title">核算公司设置</div>
          <FormItem {...formItemLayout} label=" " colon={false}>
            {getFieldDecorator('visibleCompany', {
              initialValue: nowTypeList.id ? JSON.stringify(nowTypeList.visibleCompany) : '1004',
            })(
              <RadioGroup>
                <Radio style={radioStyle} value="1001">
                  账套下全部公司
                </Radio>
                <Radio style={radioStyle} value="1002">
                  本公司及下属公司
                </Radio>
                <Radio style={radioStyle} value="1003">
                  下属公司
                </Radio>
                <Radio style={radioStyle} value="1004">
                  仅本公司
                </Radio>
              </RadioGroup>
            )}
          </FormItem>
          <div className="common-item-title">权限设置</div>
          <FormItem {...formItemLayout} label="适用人员">
            {getFieldDecorator('departmentOrUserGroupIdList', {
              initialValue: permissions,
            })(
              <PermissionsAllocation
                params={{ setOfBooksId: nowTypeList.id ? nowTypeList.setOfBooksId : setOfBooksId }}
                onChange={this.onPermissionChange}
              />
            )}
          </FormItem>
          <div className="slide-footer">
            <Button type="primary" htmlType="submit" loading={loading}>
              {this.$t({ id: 'common.save' })}
            </Button>
            <Button onClick={this.onSliderFormCancel}>{this.$t({ id: 'common.cancel' })}</Button>
          </div>
        </Form>
        <ListSelector
          visible={accountVisible}
          onCancel={this.onAccountCancel}
          onOk={this.onAccountOk}
          type="gl_select_account"
          selectedData={accountIdList}
          extraParams={accountParams}
          single={false}
        />
      </div>
    );
  }
}
function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}
const WrappedNewGLWorkOrderType = Form.create()(NewGLWorkOrderType);
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewGLWorkOrderType);
