/* eslint-disable  */
import React from 'react';
import { connect } from 'dva';
import {
  Form,
  Card,
  Input,
  Radio,
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
const TextArea = Input.TextArea;

class NodeApproveAI extends React.Component {
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
      approvalAction: this.props.basicInfo.approvalActions,
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
      approvalAction,
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
            <FormItem
              {...formItemLayout}
              label={this.$t('setting.key1372' /*节点名称*/)}
              colon={false}
            >
              {getFieldDecorator('remark', {
                rules: [
                  {
                    max: 8,
                    message: this.$t('common.max.characters.length', { max: 8 }),
                  },
                ],
                initialValue: basicInfo.remark,
              })(
                <Input
                  placeholder={this.$t('common.please.enter')}
                  style={{ width: 200 }}
                  autoComplete="off"
                />
              )}
            </FormItem>
            <FormItem
              {...formItemLayout}
              label={this.$t('setting.key1373' /*节点类型*/)}
              colon={false}
            >
              {getFieldDecorator('approvalActions', {
                initialValue: basicInfo.approvalActions,
              })(
                <RadioGroup
                  onChange={e => {
                    this.setState({ approvalAction: e.target.value });
                  }}
                >
                  <Radio value="8001">
                    {this.$t('setting.key1374' /*通过*/)}
                    <span className="approve-type-notice">
                      {this.$t(
                        'setting.key1375' /*符合审批条件则系统自动审批通过,否则自动跳过到下一个节点*/
                      )}
                    </span>
                  </Radio>
                  <Radio value="8002">
                    {this.$t('setting.key1376' /*驳回*/)}
                    <span className="approve-type-notice">
                      {this.$t(
                        'setting.key1377' /*符合审批条件则系统自动审批驳回,否则自动跳过到下一个节点*/
                      )}
                    </span>
                  </Radio>
                </RadioGroup>
              )}
            </FormItem>
            <FormItem
              {...formItemLayout}
              label={this.$t('setting.key1378' /*审批意见*/)}
              colon={false}
            >
              {getFieldDecorator('comments', {
                rules: [
                  {
                    max: 50,
                    message: this.$t('common.max.characters.length', { max: 50 }),
                  },
                ],
                initialValue: basicInfo.comments,
              })(
                <TextArea
                  placeholder={
                    approvalAction === '8001'
                      ? this.$t('setting.key1379' /*系统自动通过*/)
                      : this.$t('setting.key1380' /*系统自动驳回*/)
                  }
                  rows={2}
                  style={{ resize: 'none', width: 400 }}
                />
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

const wrappedNodeApproveMan = Form.create()(NodeApproveAI);

export default connect(mapStateToProps)(wrappedNodeApproveMan);
