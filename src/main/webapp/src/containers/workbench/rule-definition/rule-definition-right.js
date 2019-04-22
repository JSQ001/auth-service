import React, { Component } from 'react';
import {
  Card,
  Button,
  Col,
  Row,
  Select,
  Icon,
  Table,
  Divider,
  Tag,
  Popconfirm,
  Modal,
  Form,
  InputNumber,
  Switch,
  Spin,
  message,
  Empty,
  Popover,
} from 'antd';
import SlideFrame from 'widget/slide-frame';
import ListSelector from 'widget/list-selector';
import config from 'config';
import moment from 'moment';
import WorkTeamCard from './work-team-card';
import NodeActionModal from './new-node-action';
import service from './rule-definition-service';

const FormItem = Form.Item;
const Option = Select.Option;

class RuleDefinitionRight extends Component {
  constructor(props) {
    super(props);
    this.state = {
      actionVisible: false,
      interfaceList: [],
      params: {},
      dataSource: [],
      columns: [
        {
          title: this.$t('workbench.rule.priority') /* 优先级 */,
          dataIndex: 'priority',
          align: 'center',
          width: '80px',
        },
        {
          title: this.$t('workbench.rule.action.code') /* 动作代码 */,
          dataIndex: 'nodeActionCode',
          align: 'center',
        },
        {
          title: this.$t('workbench.rule.action.name') /* 动作名称 */,
          dataIndex: 'nodeActionName',
          align: 'center',
        },
        {
          title: this.$t('workbench.rule.action.process') /* 动作过程 */,
          dataIndex: 'actionProcedureName',
          align: 'center',
        },
        {
          title: this.$t('workbench.rule.node.process') /* 节点过程 */,
          dataIndex: 'businessProcedureId',
          align: 'center',
          render: (value, record) => (
            <a onClick={() => this.nodeProcedure(record.id)}>
              {this.$t('workbench.rule.node.process') /* 节点过程 */}
            </a>
          ),
        },
        {
          title: this.$t('common.column.status'),
          dataIndex: 'enabled',
          align: 'center',
          render: value =>
            value ? (
              <Tag color="green">{this.$t('common.status.enable')}</Tag>
            ) : (
              <Tag color="red">{this.$t('common.status.disable')}</Tag>
            ),
          width: '80px',
        },
        {
          title: this.$t('common.operation'),
          dataIndex: 'operation',
          align: 'center',
          width: '120px',
          render: (value, record) => {
            return (
              <span>
                <a onClick={() => this.editProcedure(record)}>{this.$t('common.edit')}</a>
                <Divider type="vertical" />
                <Popconfirm
                  title={this.$t('itinerary.record.public.delete.tip') /* 你确定删除这行内容吗? */}
                  onConfirm={() => this.deleteProcedure(record.id)}
                >
                  <a>{this.$t('common.delete')}</a>
                </Popconfirm>
              </span>
            );
          },
        },
      ],
      nodeProcedureVisible: false,
      nodeProcedureList: [],
      selectorItem: {
        title: this.$t('workbench.rule.add.team') /* 添加工作组 */,
        url: `${config.workbenchUrl}/api/workbench/businessWorkTeam/query/workTeam/by/cond`,
        searchForm: [
          {
            type: 'input',
            id: 'workTeamCode',
            label: this.$t('workbench.workTeam.code') /* 工作组代码 */,
            colSpan: 8,
          },
          {
            type: 'input',
            id: 'workTeamName',
            label: this.$t('workbench.workTeam.name') /* 工作组名称 */,
            colSpan: 8,
          },
        ],
        columns: [
          {
            title: this.$t('workbench.workTeam.code') /* 工作组代码 */,
            dataIndex: 'workTeamCode',
            align: 'center',
          },
          {
            title: this.$t('workbench.workTeam.name') /* 工作组名称 */,
            dataIndex: 'workTeamName',
            align: 'center',
          },
          {
            title: this.$t('workbench.workTeam.teamLoader') /* 组长 */,
            dataIndex: 'workManager',
            align: 'center',
          },
        ],
        key: 'id',
      },
      workTeamList: [],
      workTeamVisible: false,
      taskingVisible: false,
      methodList: [],
      taskingParams: {},
      workTeamCardList: [],
      ruleSelectorItem: {
        title: this.$t('workbench.rule.add'),
        url: `${config.workbenchUrl}/api/workbench/dispatchRule/query/rule/detail?businessTypeId=${
          props.businessTypeId
        }`,
        searchForm: [
          {
            type: 'input',
            id: 'ruleCode',
            label: this.$t('workbench.rule.task.code') /* 派工规则代码 */,
            colSpan: 8,
          },
          {
            type: 'input',
            id: 'ruleName',
            label: this.$t('workbench.rule.task.name') /* 派工规则名称 */,
            colSpan: 8,
          },
        ],
        columns: [
          {
            title: this.$t('workbench.rule.task.code') /* 派工规则代码 */,
            dataIndex: 'ruleCode',
            align: 'center',
          },
          {
            title: this.$t('workbench.rule.task.name') /* 派工规则名称 */,
            dataIndex: 'ruleName',
            align: 'center',
            render: this.renderRule,
          },
          {
            title: this.$t('budget.controlRule.effectiveDate') /* 有效日期 */,
            dataIndex: 'startDate',
            align: 'center',
            render: (value, record) => {
              return `${record.startDate ? moment(record.startDate).format('YYYY-MM-DD') : ''} ~${
                record.endDate ? moment(record.endDate).format('YYYY-MM-DD') : ''
              }`;
            },
          },
        ],
        key: 'id',
      },
      ruleVisibleList: [],
      ruleVisible: false,
      nodeLoading: false,
      businessTypeId: props.businessTypeId,
      nodeSaving: false,
      detailLoading: false,
      businessPage: undefined,
      businessPageLoading: false,
      workTeamLoading: false,
      clickWorkTeamId: '',
      businessActionId: '',
      ruleRefIndex: '',
      needRefresh: 1,
    };
  }

  componentWillReceiveProps(nextProps) {
    const nextId = nextProps.selectedNode.id;
    if (nextId && nextId !== this.props.selectedNode.id) {
      this.getAllDetail(nextId);
      this.getInterfaceList();
    }
  }

  componentWillUnmount() {
    this.setState = () => {}; // 销毁前，清除异步对 state 的操作
  }

  // 获取全部信息
  getAllDetail = id => {
    this.setState({ detailLoading: true });
    service
      .getNodeInfoDetail({ businessNodeId: id })
      .then(res => {
        const data = res.data;
        const businessPage = data.businessNode.businessPage;
        this.setState({
          businessPage: businessPage ? { key: businessPage } : undefined,
          dataSource: data.actionProcedureDTOS.map(item => item.businessNodeAction),
          workTeamCardList: data.nodeTeamModValDTOS,
          detailLoading: false,
        });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 节点 编辑
  onEditClick = e => {
    e.preventDefault();
    this.props.onEdit && this.props.onEdit();
  };

  // 获取操作界面 下拉框
  getInterfaceList = () => {
    service
      .getInterfaceList({ businessTypeId: this.state.businessTypeId })
      .then(res => {
        const interfaceList = res.data.map(item => {
          return {
            key: item.id,
            label: `${item.pageCode}-${item.pageName}`,
          };
        });
        this.setState({ interfaceList });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 获取处理方式
  getHandleMethod() {
    service
      .getHandleMethod()
      .then(res => this.setState({ methodList: res.data }))
      .catch(err => message.error(err.response.data.message));
  }

  // 操作界面改变
  interfaceChange = value => {
    const node = this.props.selectedNode;
    const params = {
      // ...this.props.selectedNode,
      id: node.id,
      businessRuleId: node.businessRuleId,
      dealModel: node.dealModel,
      priority: node.priority,
      nodeType: node.nodeType,
      businessNodeCode: node.businessNodeCode,
      businessNodeName: node.businessNodeName,
      enabled: node.enabled,
      businessPage: value.key,
    };
    this.setState({ businessPageLoading: true });
    service
      .saveNodeInfo(params)
      .then(res => {
        message.success(this.$t('common.operate.success'));
        this.setState({ businessPage: value, selectedNode: res.data, businessPageLoading: false });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 关闭节点动作侧滑框
  closeAction = flag => {
    this.setState({ actionVisible: false, params: {}, businessActionId: '' });
    if (flag) {
      this.getActionNodeList();
    }
  };

  // 获取节点动作列表
  getActionNodeList = () => {
    this.setState({ tableLoading: true });
    service
      .getNodeActionList({ businessNodeId: this.props.selectedNode.id })
      .then(res => {
        this.setState({ dataSource: res.data, tableLoading: false });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 节点动作表 点击节点过程
  nodeProcedure = id => {
    const params = {
      businessNodeId: this.props.selectedNode.id,
      businessActionId: id,
    };
    this.setState({ nodeProcedureVisible: true, nodeProcedureList: [], nodeLoading: true });
    service
      .getNodeProcedureByAction(params)
      .then(res => {
        this.setState({ nodeLoading: false, nodeProcedureList: res.data });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 节点动作编辑
  editProcedure = record => {
    this.setState({ params: record, actionVisible: true, businessActionId: record.id });
  };

  // 节点动作删除
  deleteProcedure = id => {
    service
      .deleteNodeAction(id, this.props.selectedNode.id)
      .then(() => {
        message.success(this.$t('common.delete.success'));
        this.getActionNodeList();
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 点击添加工作组
  addWorkTeam = () => {
    const { businessPage, dataSource } = this.state;
    if (businessPage && dataSource.length) {
      this.setState({ workTeamVisible: true });
    } else if (!businessPage && dataSource.length) {
      message.warning(this.$t('workbench.rule.please.page')); /* 请先添加操作界面！ */
    } else if (businessPage && !dataSource.length) {
      message.warning(this.$t('workbench.rule.please.action')); /* 请先添加节点动作！ */
    } else {
      message.warning(this.$t('workbench.rule.please.all')); /* 请先添加操作界面和节点动作！ */
    }
  };

  // 添加工作组下一步
  onNextOk = value => {
    if (!value.result.length) return message.warning(this.$t('workbench.rule.please.team')); // 请选择工作组！
    this.setState({ workTeamVisible: false, workTeamList: value.result }, () => {
      this.setState({ taskingVisible: true }); // 待优化
      this.getHandleMethod();
    });
  };

  // 重新选择工作组 点击事件
  workTeamClick = () => {
    this.setState({ workTeamVisible: true, taskingVisible: false });
    this.workTeamRef.blur();
  };

  // 工作组编辑
  workTeamEdit = record => {
    this.setState({ taskingVisible: true, taskingParams: record });
    this.getHandleMethod();
  };

  // 节点派工保存
  nodeSubmit = () => {
    this.props.form.validateFields((error, values) => {
      if (!error) {
        const { taskingParams } = this.state;
        const params = {
          ...values,
          businessNodeId: this.props.selectedNode.id,
          workTeamId: values.workTeamId.key,
          id: taskingParams.id,
          assignNumberId: taskingParams.assignNumberId,
          handleNumberId: taskingParams.handleNumberId,
          versionNumber: taskingParams.versionNumber,
        };
        this.setState({ nodeSaving: true });
        service
          .saveNodeTasking(params)
          .then(() => {
            this.setState({ nodeSaving: false, workTeamList: [] });
            this.getWorkTeamDetail();
            this.handleNodeCancel();
            message.success(this.$t('common.operate.success'));
          })
          .catch(err => {
            message.error(err.response.data.message);
            this.props.form.resetFields();
            this.setState({ nodeSaving: false, taskingParams: params });
          });
      }
    });
  };

  // 节点派工取消
  handleNodeCancel = () => {
    this.setState({ taskingVisible: false, taskingParams: {}, workTeamList: [] });
    this.props.form.resetFields();
  };

  // 获取全部工作组数据
  getWorkTeamDetail = () => {
    this.setState({ workTeamLoading: true });
    service
      .getWorkTeamDetail({ businessNodeId: this.props.selectedNode.id })
      .then(res => {
        this.setState({
          workTeamCardList: res.data,
          workTeamLoading: false,
          needRefresh: new Date().getTime(),
        });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 删除工作组
  deleteCard = params => {
    service
      .deleteWorkTeam(params.id, this.props.selectedNode.id)
      .then(() => {
        message.success(this.$t('common.delete.success'));
        this.getWorkTeamDetail();
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 点击 添加派发规则
  addTaskRule = (params, index) => {
    this.setState({ ruleVisible: true, clickWorkTeamId: params.id, ruleRefIndex: index });
  };

  // 添加规则 确定
  onRuleOk = value => {
    if (!value.result.length) return message.warning(this.$t('workbench.rule.add.data')); // 请先选择需要添加的数据！
    const { clickWorkTeamId, ruleRefIndex } = this.state;
    const params = {
      businessNodeTeamId: clickWorkTeamId,
      dispatchRuleId: value.result.map(item => item.id),
    };
    service
      .saveRule(params)
      .then(() => {
        this.setState({ ruleVisible: false });
        message.success(this.$t('common.add.success'));
        this[`workTeam${ruleRefIndex}`].getWorkTeamRule();
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 添加规则弹框  规则明细弹框
  renderRule = (value, record, index) => {
    const { ruleVisibleList } = this.state;
    return (
      <Popover
        title={
          <div className="pop-title">
            {record.ruleCode}-{record.ruleName}
          </div>
        }
        content={this.renderPop(record.dispatchRuleDetails)}
        getPopupContainer={node => node.parentNode}
        overlayClassName="pop-card"
        visible={ruleVisibleList[index]}
      >
        <div>{value}</div>
      </Popover>
    );
  };

  // 规则弹框
  renderPop = params => {
    return params && params.length ? (
      <div className="ant-popover-box">
        {params.map(item => {
          return (
            <Row key={item.id} className="pop-content">
              <Col span={2}>{item.andOr}</Col>
              <Col span={2}>{item.leftBracket}</Col>
              <Col span={8}>{item.parameterName}</Col>
              <Col span={2}>{item.judgeRuleName}</Col>
              <Col span={8}>{item.judgeData}</Col>
              <Col span={2}>{item.rightBracket}</Col>
            </Row>
          );
        })}
      </div>
    ) : (
      <Empty />
    );
  };

  // 鼠标移入、移出 行
  onMouseEnterLeave = (index, visible) => {
    const { ruleVisibleList } = this.state;
    ruleVisibleList[index] = visible;
    setTimeout(() => this.setState({ ruleVisibleList }), 200); // 待优化
  };

  render() {
    const {
      interfaceList,
      actionVisible,
      params,
      columns,
      dataSource,
      nodeProcedureVisible,
      nodeProcedureList,
      workTeamVisible,
      selectorItem,
      workTeamList,
      taskingVisible,
      methodList,
      taskingParams,
      workTeamCardList,
      ruleSelectorItem,
      ruleVisible,
      nodeLoading,
      businessTypeId,
      nodeSaving,
      detailLoading,
      businessPage,
      businessPageLoading,
      workTeamLoading,
      tableLoading,
      businessActionId,
      needRefresh,
    } = this.state;
    const { selectedNode } = this.props;
    const { getFieldDecorator } = this.props.form;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };

    return (
      <div
        className="rule-definition-right"
        style={{ display: selectedNode.id ? 'block' : 'none' }}
      >
        <Spin spinning={detailLoading} size="large">
          <Card
            title={`${selectedNode.businessNodeCode}-${selectedNode.businessNodeName}`}
            extra={<a onClick={this.onEditClick}>{this.$t('common.edit')}</a>}
            className="node-card"
          >
            <Row>
              <Col
                className="required define-ellipsis"
                span={4}
                title={this.$t('workbench.rule.page')}
              >
                {this.$t('workbench.rule.page') /* 操作界面 */}：
              </Col>
              <Col span={18}>
                <Select
                  onChange={this.interfaceChange}
                  placeholder={this.$t('common.please.select')}
                  getPopupContainer={node => node.parentNode}
                  labelInValue
                  value={businessPage}
                  loading={businessPageLoading}
                >
                  {interfaceList.map(item => <Option key={item.key}>{item.label}</Option>)}
                </Select>
              </Col>
            </Row>
            <Row>
              <Col
                className="required define-ellipsis"
                span={4}
                title={this.$t('workbench.rule.node.action')}
              >
                {this.$t('workbench.rule.node.action') /* 节点动作 */}：
              </Col>
              <Col span={20}>
                <Button onClick={() => this.setState({ actionVisible: true })}>
                  <Icon type="plus" />
                  {this.$t('workbench.rule.node.action.add') /* 添加节点动作 */}
                </Button>
                <Table
                  columns={columns}
                  dataSource={dataSource}
                  rowKey="id"
                  pagination={false}
                  style={{ marginTop: '15px' }}
                  loading={tableLoading}
                />
              </Col>
            </Row>
          </Card>

          <Button type="primary" style={{ margin: '15px 0' }} onClick={this.addWorkTeam}>
            {this.$t('workbench.rule.add.team') /* 添加工作组 */}
          </Button>

          {/* 工作组 */}
          <Spin spinning={workTeamLoading} wrapperClassName="card-wrapper">
            {workTeamCardList.map((item, index) => (
              <WorkTeamCard
                key={index}
                params={item}
                edit={() => this.workTeamEdit(item)}
                addRule={() => this.addTaskRule(item, index)}
                deleteCard={() => this.deleteCard(item)}
                ref={ref => (this[`workTeam${index}`] = ref)}
                needRefresh={needRefresh}
              />
            ))}
          </Spin>
        </Spin>

        <SlideFrame
          title={
            params.id
              ? this.$t('workbench.rule.add.action') /* 编辑节点动作 */
              : this.$t('workbench.rule.add.action') /* 新建节点动作 */
          }
          show={actionVisible}
          onClose={() => this.closeAction()}
        >
          <NodeActionModal
            onClose={this.closeAction}
            params={params}
            businessTypeId={businessTypeId}
            businessNodeId={selectedNode.id}
            businessActionId={businessActionId}
          />
        </SlideFrame>

        <Modal
          title={this.$t('workbench.rule.node.process') /* 节点过程 */}
          visible={nodeProcedureVisible}
          footer={null}
          onCancel={() => this.setState({ nodeProcedureVisible: false })}
          wrapClassName="node-procedure-modal"
          centered
        >
          <Spin wrapperClassName="node-procedure" spinning={nodeLoading}>
            {nodeProcedureList.length ? (
              nodeProcedureList.map(item => {
                return (
                  <Button key={item.id} type="primary" block>
                    {item.procedureCode}-{item.procedureName}
                  </Button>
                );
              })
            ) : (
              <Empty />
            )}
          </Spin>
        </Modal>

        {/* 添加工作组 */}
        <ListSelector
          visible={workTeamVisible}
          selectorItem={selectorItem}
          extraParams={{ nodeId: selectedNode.id }}
          onOk={this.onNextOk}
          onCancel={() => this.setState({ workTeamVisible: false, workTeamList: [] })}
          single
          selectedData={workTeamList}
          valueKey="id"
          okText={this.$t('acp.next')}
        />

        {/* 节点派工方式 */}
        <Modal
          title={
            taskingParams.id
              ? this.$t('workbench.rule.task.add') /* 编辑节点派工方式 */
              : this.$t('workbench.rule.task.add') /* 节点派工方式 */
          }
          visible={taskingVisible}
          className="node-modal"
          onOk={this.nodeSubmit}
          onCancel={this.handleNodeCancel}
          okText={this.$t('common.save')}
          cancelText={this.$t('common.cancel')}
          confirmLoading={nodeSaving}
        >
          <Form onSubmit={this.nodeSubmit}>
            <FormItem {...formItemLayout} label={this.$t('workbench.workTeam.team') /* 工作组 */}>
              {getFieldDecorator('workTeamId', {
                rules: [
                  {
                    required: true,
                    message: this.$t('common.please.select'),
                  },
                ],
                initialValue: workTeamList[0]
                  ? { key: workTeamList[0].id, label: workTeamList[0].workTeamName }
                  : taskingParams.workTeamId && {
                      key: taskingParams.workTeamId,
                      label: taskingParams.workTeamName,
                    },
              })(
                <Select
                  placeholder={this.$t('common.please.select')}
                  onFocus={this.workTeamClick}
                  dropdownStyle={{ display: 'none' }}
                  ref={ref => (this.workTeamRef = ref)}
                  labelInValue
                  disabled={!!taskingParams.id}
                />
              )}
            </FormItem>
            <FormItem {...formItemLayout} label={this.$t('workbench.rule.method') /* 处理方式 */}>
              {getFieldDecorator('handleModel', {
                rules: [
                  {
                    required: true,
                    message: this.$t('common.please.select'),
                  },
                ],
                initialValue: taskingParams.handleModel,
              })(
                <Select placeholder={this.$t('common.please.select')}>
                  {methodList.map(item => {
                    return <Option key={item.value}>{item.name}</Option>;
                  })}
                </Select>
              )}
            </FormItem>
            <FormItem
              {...formItemLayout}
              label={this.$t('workbench.rule.maxAssignNumber') /* 取单最大值 */}
            >
              {getFieldDecorator('maxAssignNumber', {
                rules: [
                  {
                    required: true,
                    message: this.$t('common.please.enter'),
                  },
                ],
                initialValue: taskingParams.maxAssignNumber,
              })(
                <InputNumber
                  placeholder={this.$t('common.please.enter')}
                  min={1}
                  precision={0}
                  style={{ width: '100%' }}
                />
              )}
            </FormItem>
            <FormItem
              {...formItemLayout}
              label={this.$t('workbench.rule.maxHandleNumber') /* 单据在手量 */}
            >
              {getFieldDecorator('maxHandleNumber', {
                rules: [
                  {
                    required: true,
                    message: this.$t('common.please.enter'),
                  },
                ],
                initialValue: taskingParams.maxHandleNumber,
              })(
                <InputNumber
                  placeholder={this.$t('common.please.enter')}
                  min={1}
                  precision={0}
                  style={{ width: '100%' }}
                />
              )}
            </FormItem>
            <FormItem {...formItemLayout} label={this.$t('common.column.status')}>
              {getFieldDecorator('enabled', {
                valuePropName: 'checked',
                initialValue: taskingParams.id ? taskingParams.enabled : true,
              })(<Switch />)}
              <span style={{ paddingLeft: '10px' }}>
                {this.props.form.getFieldValue('enabled')
                  ? this.$t('common.status.enable')
                  : this.$t('common.status.disable')}
              </span>
            </FormItem>
          </Form>
        </Modal>

        {/* 添加规则 */}
        <ListSelector
          visible={ruleVisible}
          selectorItem={ruleSelectorItem}
          extraParams={{ enabled: true }}
          onOk={this.onRuleOk}
          onCancel={() => this.setState({ ruleVisible: false })}
          valueKey="id"
          onRowMouseEnter={(record, index) => this.onMouseEnterLeave(index, true)}
          onRowMouseLeave={(record, index) => this.onMouseEnterLeave(index, false)}
        />
      </div>
    );
  }
}

RuleDefinitionRight = Form.create()(RuleDefinitionRight);

export default RuleDefinitionRight;
