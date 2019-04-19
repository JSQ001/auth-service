import React from 'react';
import { connect } from 'dva';
import {
  Form,
  Card,
  Input,
  Radio,
  Select,
  Button,
  Icon,
  Row,
  Col,
  Switch,
  Checkbox,
  Tag,
  Divider,
  message,
  Popconfirm,
} from 'antd';
import AddBackNode from './add-back-node';
import AddNoticeAction from './add-notice-action';
import AddNoticePerson from './add-notice-person';
import NoticeConditionList from './notice-condition-list';
import AddApproveRuleModal from 'containers/setting/workflow/right-content/add-rule-modal';
import debounce from 'lodash/debounce';
import service from '../workflow.service';
import { deepCopy } from 'utils/extend';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;

class NodeApproveMan extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      pages: [],
      selfApprovalRule: props.basicInfo.selfApprovalRule,
      addBackNodeVisible: false,
      backNodes: deepCopy(props.basicInfo.customNodes || []),
      addNoticeActionVisible: false,
      noticeActions: deepCopy(props.basicInfo.ruleNotices || []),
      ruleNoticeOid: '',
      addNoticeActionsLoading: false,
      addNoticePersonVisible: false,
      actionsCheckedValues: [],
      batchCode: null,
      addApproveRuleVisible: false,
      users: [],
      isNoticeRuleInEdit: false,
    };
    this.actions = {
      1001: '审批人手动通知',
      1002: '节点到达',
      1003: '审批通过',
      1004: '审批驳回',
      1005: '转交',
      1006: '加签',
      1007: '退回指定节点',
      1008: '撤回',
    };
    this.handleSearch = debounce(this.handleSearch, 300);
  }

  notifyEnableChange = value => {
    this.props.form.setFieldsValue({ notifyMethod: [] });
  };

  handleSearch = value => {
    if (!value) {
      this.setState({ pages: [] });
      return;
    }
    service.getPages(value).then(res => {
      this.setState({ pages: res.data });
    });
  };

  submit = () => {
    const { form, basicInfo } = this.props;
    form.validateFields((err, values) => {
      if (err) return;

      const { selfApprovalRule, backNodes } = this.state;
      if (values.selfApprovalRule === 5000) {
        values.selfApprovalRule = selfApprovalRule;
      }
      service
        .modifyApprovalNodes({
          ...basicInfo,
          ...values,
          ruleApprovers: [],
          customNodes: backNodes,
          ruleNotices: null,
        })
        .then(res => {
          message.success('保存成功！');
          this.props.basicInfoSaveHandle();
        });
    });
  };

  addBackNode = () => {
    this.setState({ addBackNodeVisible: true });
  };

  selfApprovalRuleSelectChange = value => {
    this.setState({ selfApprovalRule: value });
  };

  selfApprovalRuleChange = e => {
    if (e.target.value === 5000) {
      this.setState({ selfApprovalRule: 5005 });
    }
  };

  addBackNodeHandle = nodes => {
    this.setState({ backNodes: nodes, addBackNodeVisible: false });
  };

  deleteBackNode = item => {
    const { backNodes } = this.state;
    const index = backNodes.findIndex(o => o.ruleApprovalNodeOid === item.ruleApprovalNodeOid);
    backNodes.splice(index, 1);
    this.setState({ backNodes });
  };

  addNoticeAction = (item = {}) => {
    const { isNoticeRuleInEdit } = this.state;
    if (isNoticeRuleInEdit) {
      message.warning(this.$t('setting.key1319' /*你有一个编辑中的审批条件未保存*/));
      return;
    }
    this.setState({
      addNoticeActionVisible: true,
      ruleNoticeOid: item.ruleNoticeOid,
      actionsCheckedValues: item.actions || [],
    });
  };

  addNoticeActionHandle = values => {
    const { ruleNoticeOid } = this.state;
    const {
      basicInfo: { ruleApprovalNodeOid },
    } = this.props;
    if (ruleNoticeOid) {
      this.setState({ addNoticeActionsLoading: true });
      service
        .updateNoticeActions({
          ruleNoticeOid,
          actions: values,
        })
        .then(() => {
          this.setState({
            addNoticeActionVisible: false,
            addNoticeActionsLoading: false,
            ruleNoticeOid: '',
          });
          message.success('操作成功！');
          this.props.basicInfoSaveHandle();
        })
        .catch(err => {
          this.setState({ addNoticeActionsLoading: false });
          message.error(err.response.data.message);
        });
    } else {
      this.setState({ addNoticeActionsLoading: true });
      service
        .addNoticeActions({
          ruleApprovalNodeOid,
          actions: values,
        })
        .then(() => {
          this.setState({
            addNoticeActionVisible: false,
            addNoticeActionsLoading: false,
            ruleNoticeOid: '',
          });
          message.success('操作成功！');
          this.props.basicInfoSaveHandle();
        })
        .catch(err => {
          this.setState({ addNoticeActionsLoading: false });
          message.error(err.response.data.message);
        });
    }
  };

  addNoticePerson = ({ ruleNoticeOid, users }) => {
    this.setState({ addNoticePersonVisible: true, ruleNoticeOid, users });
  };

  deleteNotice = ({ ruleNoticeOid }) => {
    service
      .deleteNotice(ruleNoticeOid)
      .then(() => {
        message.success('删除成功！');
        this.props.basicInfoSaveHandle();
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  deleteNoticeUsers = ({ ruleNoticeOid }) => {
    service
      .deleteNoticeUsers({ ruleNoticeOid })
      .then(() => {
        message.success('删除成功！');
        this.props.basicInfoSaveHandle();
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 审批条件modal
  handleAdditionModalShow = (condition, { ruleNoticeOid }) => {
    const { isNoticeRuleInEdit } = this.state;
    if (isNoticeRuleInEdit) {
      message.warning(this.$t('setting.key1319' /*你有一个编辑中的审批条件未保存*/));
      return;
    }

    this.setState({
      batchCode: null,
      addApproveRuleVisible: true,
      ruleNoticeOid,
    });
  };

  handleEdititionModalShow = (condition, { ruleNoticeOid }) => {
    this.setState({
      batchCode: condition[0] ? condition[0].batchCode : null,
      addApproveRuleVisible: true,
      ruleNoticeOid,
    });
  };

  // 新增/编辑审批条件
  handleAddCondition = (rules, batchCode) => {
    if (rules.length) {
      const { noticeActions, ruleNoticeOid } = this.state;
      noticeActions.map(item => {
        if (item.ruleNoticeOid === ruleNoticeOid) {
          if (batchCode) {
            //编辑
            item.conditions[batchCode].push(...rules);
          } else {
            // 新增
            item.conditions = item.conditions || {};
            item.conditions[9999] = rules;
          }
        }
      });
      this.setState({ addApproveRuleVisible: false, noticeActions, isNoticeRuleInEdit: true });
    } else {
      message.error('请至少选择一个条件！');
    }
  };

  handleCancelEditCondition = index => {
    const { basicInfo } = this.props;
    const { noticeActions } = this.state;
    noticeActions[index].conditions = basicInfo.ruleNotices[index].conditions;
    this.setState({ noticeActions, isNoticeRuleInEdit: false });
  };

  //审批条件 点击"编辑"
  handleEditCondition = (condition, approverIndex, conditionIndex) => {
    const { isNoticeRuleInEdit } = this.state;
    if (!condition[0].isEdit && isNoticeRuleInEdit) {
      message.warning(this.$t('setting.key1319' /*你有一个编辑中的审批条件未保存*/));
      return;
    }
    let ruleApprovers = this.state.noticeActions;
    Object.keys(ruleApprovers[approverIndex].conditions).map((key, index) => {
      if (index === conditionIndex) {
        ruleApprovers[approverIndex].conditions[key].map((conditionItem, conditionItemIndex) => {
          ruleApprovers[approverIndex].conditions[key][conditionItemIndex].isEdit = true;
          let item = ruleApprovers[approverIndex].conditions[key][conditionItemIndex];
          if (
            item.remark === 'default_user_department_extend' ||
            item.remark === 'custom_form_department_extend'
          ) {
            item.field = `${item.field},${item.remark}`;
          }
        });
      }
    });
    this.setState({
      noticeActions: ruleApprovers,
      batchCode: condition[0].batchCode,
      isNoticeRuleInEdit: true,
    });
  };

  onApproverChange = () => {
    const { basicInfoSaveHandle } = this.props;
    this.setState({ isNoticeRuleInEdit: false }, () => {
      basicInfoSaveHandle();
    });
  };

  render() {
    const {
      pages,
      selfApprovalRule,
      addBackNodeVisible,
      backNodes,
      addNoticeActionVisible,
      noticeActions,
      addNoticeActionsLoading,
      addNoticePersonVisible,
      actionsCheckedValues,
      ruleNoticeOid,
      batchCode,
      addApproveRuleVisible,
      users,
    } = this.state;
    const { getFieldDecorator } = this.props.form;
    const {
      basicInfo,
      form,
      formInfo,
      ruleApprovers,
      ruleApprovalNodeOid,
      basicInfoSaveHandle,
    } = this.props;
    const formItemLayout = {
      labelCol: { span: 5 },
      wrapperCol: { span: 16 },
    };
    // 两列的布局
    const formItemLayout1 = {
      labelCol: { span: 10 },
      wrapperCol: { span: 14 },
    };

    return (
      <div>
        <Form>
          <div style={{ textAlign: 'right', marginBottom: 10 }}>
            <Button onClick={this.submit} type="primary">
              保存
            </Button>
          </div>
          <Card bodyStyle={{ padding: 10 }} title="基本信息">
            <Row>
              <Col span={12}>
                <FormItem {...formItemLayout} label="节点名称">
                  {getFieldDecorator('remark', {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.enter'), //请输入
                      },
                    ],
                    initialValue: basicInfo.remark,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem {...formItemLayout} label="节点页面">
                  {getFieldDecorator('pageId', {
                    initialValue: basicInfo.pageId,
                  })(
                    <Select
                      showSearch
                      placeholder="请输入"
                      defaultActiveFirstOption={false}
                      showArrow={false}
                      filterOption={false}
                      onSearch={this.handleSearch}
                      notFoundContent={null}
                      style={{ maxWidth: 300, width: '100%' }}
                    >
                      {pages.map(item => (
                        <Select.Option key={item.id}>{item.pageName}</Select.Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
            </Row>
          </Card>
          <Card bodyStyle={{ padding: 10 }} title="审批规则">
            <FormItem {...formItemLayout} label="审批规则">
              {getFieldDecorator('countersignRule', {
                initialValue: basicInfo.countersignRule,
              })(
                <Radio.Group>
                  <Radio value={3001}>所有人通过</Radio>
                  <Radio value={3002}>一票通过/一票驳回</Radio>
                  <Radio value={3003}>任一人通过</Radio>
                </Radio.Group>
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="包含申请人">
              {getFieldDecorator('selfApprovalRule', {
                initialValue:
                  basicInfo.selfApprovalRule === 5002 || basicInfo.selfApprovalRule === 5001
                    ? basicInfo.selfApprovalRule
                    : 5000,
              })(
                <RadioGroup onChange={this.selfApprovalRuleChange}>
                  <Radio value={5002}>不跳过</Radio>
                  <Radio value={5001}>跳过</Radio>
                  <Radio value={5000}>
                    转交部门经理
                    {form.getFieldValue('selfApprovalRule') === 5000 && (
                      <Select
                        size="small"
                        value={selfApprovalRule}
                        onChange={this.selfApprovalRuleSelectChange}
                        style={{ marginLeft: 10, width: 150 }}
                      >
                        <Select.Option value={5005}>本级</Select.Option>
                        <Select.Option value={5003}>上级</Select.Option>
                      </Select>
                    )}
                  </Radio>
                </RadioGroup>
              )}
            </FormItem>
            <Row>
              <Col span={12}>
                <FormItem {...formItemLayout1} label="是否允许加签">
                  {getFieldDecorator('addsignFlag', {
                    initialValue: basicInfo.addsignFlag,
                  })(
                    <Radio.Group>
                      <Radio value={true}>是</Radio>
                      <Radio value={false}>否</Radio>
                    </Radio.Group>
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem {...formItemLayout1} label="是否允许转交">
                  {getFieldDecorator('transferFlag', {
                    initialValue: basicInfo.transferFlag,
                  })(
                    <Radio.Group>
                      <Radio value={true}>是</Radio>
                      <Radio value={false}>否</Radio>
                    </Radio.Group>
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={12}>
                <FormItem {...formItemLayout1} label="节点为空时">
                  {getFieldDecorator('nullableRule', {
                    initialValue: basicInfo.nullableRule,
                  })(
                    <Radio.Group>
                      <Radio value={2001}>跳过</Radio>
                      <Radio value={2002}>不跳过</Radio>
                    </Radio.Group>
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem {...formItemLayout1} label="是否重复审批">
                  {getFieldDecorator('repeatRule', {
                    initialValue: basicInfo.repeatRule,
                  })(
                    <Radio.Group>
                      <Radio value={4001}>是</Radio>
                      <Radio value={4002}>否</Radio>
                    </Radio.Group>
                  )}
                </FormItem>
              </Col>
            </Row>
            <FormItem {...formItemLayout} label="驳回后再次提交处理">
              {getFieldDecorator('rejectRule', {
                initialValue: basicInfo.rejectRule,
              })(
                <Radio.Group>
                  <Radio value={1001}>重新全部审批</Radio>
                  <Radio value={1002}>直接跳回本节点</Radio>
                  <Radio value={1003}>驳回人自主判断(二者均可)</Radio>
                </Radio.Group>
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="允许退回指定节点">
              {getFieldDecorator('returnFlag', {
                initialValue: basicInfo.returnFlag,
              })(
                <Radio.Group>
                  <Radio value={true}>是</Radio>
                  <Radio value={false}>否</Radio>
                </Radio.Group>
              )}
            </FormItem>
            {form.getFieldValue('returnFlag') && (
              <FormItem {...formItemLayout} label="可退回节点">
                {getFieldDecorator('returnType', {
                  initialValue: basicInfo.returnType,
                })(
                  <Radio.Group>
                    <Radio value={1001}>本节点前任意审批节点</Radio>
                    <Radio value={1002}>自选节点</Radio>
                  </Radio.Group>
                )}
              </FormItem>
            )}
            {form.getFieldValue('returnFlag') &&
              form.getFieldValue('returnType') === 1002 && (
                <Row>
                  <Col span={16} offset={5}>
                    {backNodes.map(item => (
                      <Tag
                        closable
                        onClose={() => this.deleteBackNode(item)}
                        key={item.ruleApprovalNodeOid}
                      >
                        {item.remark}
                      </Tag>
                    ))}
                    <Tag
                      style={{ background: '#fff', borderStyle: 'dashed' }}
                      onClick={this.addBackNode}
                    >
                      <Icon type="plus" />添加
                    </Tag>
                  </Col>
                </Row>
              )}
            <FormItem {...formItemLayout} label="退回审批通过后处理">
              {getFieldDecorator('returnRule', {
                initialValue: basicInfo.returnRule,
              })(
                <Radio.Group>
                  <Radio value={1001}>重新全部审批</Radio>
                  <Radio value={1002}>直接跳回本节点</Radio>
                  <Radio value={1003}>退回人自主判断(二者均可)</Radio>
                </Radio.Group>
              )}
            </FormItem>
          </Card>
          <Card bodyStyle={{ padding: 10 }} title="审批流通知">
            <FormItem {...formItemLayout} label="开启审批流通知">
              {getFieldDecorator('notifyFlag', {
                initialValue: basicInfo.notifyFlag,
                valuePropName: 'checked',
              })(
                <Switch
                  onChange={this.notifyEnableChange}
                  checkedChildren="启用"
                  unCheckedChildren="禁用"
                />
              )}
            </FormItem>
            {form.getFieldValue('notifyFlag') && (
              <FormItem {...formItemLayout} label="通知方式">
                {getFieldDecorator('notifyMethod', {
                  initialValue: basicInfo.notifyMethod || [],
                  rules: [
                    {
                      required: form.getFieldValue('notifyFlag'),
                      message: this.$t('common.please.select'), //请输入
                    },
                  ],
                })(
                  <Checkbox.Group style={{ width: '100%' }}>
                    <Row>
                      <Col span={8}>
                        <Checkbox value={1001}>PC消息</Checkbox>
                      </Col>
                      <Col span={8}>
                        <Checkbox value={1002}>APP消息</Checkbox>
                      </Col>
                      <Col span={8}>
                        <Checkbox value={1003}>邮件通知</Checkbox>
                      </Col>
                    </Row>
                  </Checkbox.Group>
                )}
              </FormItem>
            )}
          </Card>
        </Form>
        {form.getFieldValue('notifyFlag') && (
          <React.Fragment>
            <div style={{ marginTop: 10 }} className="node-approve-man">
              <div className="add-btn-container">
                <Button type="primary" onClick={() => this.addNoticeAction()}>
                  添加通知动作
                </Button>
                <Icon type="exclamation-circle-o" className="approve-info-icon" />
                <span className="approve-info-text">{this.$t('setting.key1381')}</span>
              </div>
            </div>
            {noticeActions.map((item, index) => (
              <div key={item.ruleNoticeOid} style={{ border: '1px solid #e8e8e8', marginTop: 10 }}>
                <Row
                  style={{
                    padding: '6px 12px',
                    backgroundColor: '#fafafa',
                    borderBottom: '1px solid #e8e8e8',
                  }}
                >
                  <Col span={18}>通知动作：{item.actions.map(o => this.actions[o]).join('、')}</Col>
                  <Col style={{ textAlign: 'right' }} span={6}>
                    <a onClick={() => this.addNoticeAction(item)}>添加通知动作</a>
                    <Divider type="vertical" />
                    <Popconfirm
                      title="确定删除?"
                      onConfirm={() => this.deleteNotice(item)}
                      okText="确定"
                      cancelText="取消"
                    >
                      <a>删除</a>
                    </Popconfirm>
                  </Col>
                </Row>
                <Row
                  style={{
                    padding: '6px 12px',
                    backgroundColor: '#fafafa',
                    borderBottom: '1px solid #e8e8e8',
                  }}
                >
                  <Col span={18}>通知人员：{item.users.map(o => o.name).join('、')}</Col>
                  <Col style={{ textAlign: 'right' }} span={6}>
                    <a onClick={() => this.addNoticePerson(item)}>添加通知人员</a>
                    <Divider type="vertical" />
                    <Popconfirm
                      title="确定删除?"
                      onConfirm={() => this.deleteNoticeUsers(item)}
                      okText="确定"
                      cancelText="取消"
                    >
                      <a>删除</a>
                    </Popconfirm>
                  </Col>
                </Row>
                <div
                  style={{
                    padding: '6px 12px',
                    backgroundColor: '#fafafa',
                    borderBottom: '1px solid #e8e8e8',
                  }}
                >
                  <div>
                    请先{' '}
                    <a onClick={() => this.handleAdditionModalShow([], item)}>[添加审批条件]</a>
                  </div>
                </div>
                <NoticeConditionList
                  formOid={this.props.formOid}
                  basicInfo={basicInfo}
                  formInfo={formInfo}
                  onApproverChange={this.onApproverChange}
                  judgeRuleInEdit={isRuleInEdit => this.setState({ isRuleInEdit })}
                  ruleConditions={item.conditions}
                  approverIndex={index}
                  ruleNoticeOid={item.ruleNoticeOid}
                  handleCancelEditCondition={() => this.handleCancelEditCondition(index)}
                  notice={item}
                  handleEditCondition={this.handleEditCondition}
                  batchCode={batchCode}
                  handleAdditionModalShow={condition =>
                    this.handleEdititionModalShow(condition, item)
                  }
                />
              </div>
            ))}
          </React.Fragment>
        )}
        <div style={{ marginTop: 10 }} className="node-approve-man">
          <div className="add-btn-container">
            <Button type="primary" onClick={() => this.props.modalVisibleHandle(true)}>
              {this.$t('setting.key1395')}
            </Button>
            <Icon type="exclamation-circle-o" className="approve-info-icon" />
            <span className="approve-info-text">{this.$t('setting.key1381')}</span>
          </div>
        </div>
        <AddBackNode
          onClose={() => {
            this.setState({ addBackNodeVisible: false });
          }}
          nodeOid={basicInfo.ruleApprovalNodeOid}
          modalVisible={addBackNodeVisible}
          onOk={this.addBackNodeHandle}
          selectedNodes={backNodes}
        />
        <AddNoticeAction
          onCancel={() => {
            this.setState({ addNoticeActionVisible: false });
          }}
          // nodeOid={basicInfo.ruleApprovalNodeOid}
          visible={addNoticeActionVisible}
          onOk={this.addNoticeActionHandle}
          loading={addNoticeActionsLoading}
          actions={this.actions}
          checkedValues={actionsCheckedValues}
          // selectedNodes={backNodes}
        />
        <AddNoticePerson
          visible={addNoticePersonVisible}
          formInfo={formInfo}
          ruleApprovalNodeOid={ruleApprovalNodeOid}
          ruleApprovers={ruleApprovers}
          ruleNoticeOid={ruleNoticeOid}
          onCancel={() => this.setState({ addNoticePersonVisible: false })}
          onSelect={basicInfoSaveHandle}
          onDelete={basicInfoSaveHandle}
          users={users}
        />
        <AddApproveRuleModal
          visible={addApproveRuleVisible}
          formOid={this.props.formOid}
          ruleApproverOid={''}
          batchCode={batchCode}
          defaultValue={[]}
          onOk={this.handleAddCondition}
          onCancel={() => {
            this.setState({ addApproveRuleVisible: false });
          }}
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    language: state.languages,
  };
}

const wrappedNodeApproveMan = Form.create()(NodeApproveMan);

export default connect(mapStateToProps)(wrappedNodeApproveMan);
