import React, { Component } from 'react';
import { Row, Col, Modal, Form, Input, Switch, Icon, message } from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import RuleDefinitionRight from './rule-definition-right';
import RuleDefinitionLeft from './rule-definition-left';
import 'styles/workbench/rule-definition-details/rule-definition-details.scss';
import service from './rule-definition-service';

const FormItem = Form.Item;

class RuleDefinitionDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      nodeVisible: false,
      nodeParams: {},
      params: {},
      nodeLoading: false,
      businessRuleId: props.match.params.businessRuleId,
      selectedNode: {},
      nodeIndex: '',
      nodeType: '',
    };
  }

  // 节点弹框 保存
  nodeSubmit = e => {
    e.preventDefault();
    const { nodeIndex, nodeType, params } = this.state;
    this.props.form.validateFields((error, values) => {
      if (!error) {
        const data = {
          ...values,
          businessRuleId: this.state.businessRuleId,
          dealModel: 'RANDOM',
          priority: nodeIndex,
          nodeType,
          ...{ id: params.id },
        };
        this.setState({ nodeLoading: true });
        service
          .saveNodeInfo(data)
          .then(() => {
            if (params.id) {
              message.success(this.$t('common.update.success'));
            } else {
              message.success(this.$t('common.add.success'));
            }
            this.setState({ nodeLoading: false, nodeVisible: false, params: {} });
            this.props.form.resetFields();
            this.leftComponent.getNodeList();
          })
          .catch(err => {
            message.error(err.response.data.message);
            this.setState({ nodeLoading: false });
          });
      }
    });
  };

  // 节点弹框 取消
  handleNodeCancel = () => {
    this.setState({ nodeVisible: false, params: {} });
    this.props.form.resetFields();
    this.leftComponent.handleCancel();
  };

  // 显示节点弹框
  showNode = () => this.setState({ nodeVisible: true });

  // 编辑节点信息
  editNode = () => {
    const { nodeParams } = this.state;
    const params = {
      id: nodeParams.id,
      businessNodeCode: nodeParams.businessNodeCode,
      businessNodeName: nodeParams.businessNodeName,
      enabled: nodeParams.enabled,
    };
    this.setState({
      nodeVisible: true,
      params,
      nodeIndex: nodeParams.priority,
      nodeType: nodeParams.nodeType,
    });
  };

  // 获取参数
  getParams = (nodeIndex, nodeType, params) => {
    if (params) {
      if (params !== -1) {
        this.setState({ nodeParams: params, selectedNode: params });
      } else {
        this.setState({ selectedNode: {} });
      }
    } else {
      this.setState({ nodeIndex, nodeType });
    }
  };

  // 返回到作业规则定义
  onBackClick = e => {
    e.preventDefault();
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/workbench/rule-definition/rule-definition/${
          this.props.match.params.setOfBooksId
        }`,
      })
    );
  };

  render() {
    const { nodeVisible, params, nodeLoading, businessRuleId, selectedNode } = this.state;
    const { getFieldDecorator } = this.props.form;
    const nodeLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };

    return (
      <div style={{ paddingTop: '10px' }}>
        <Row>
          <Col span={6}>
            <RuleDefinitionLeft
              showNode={this.showNode}
              businessRuleId={businessRuleId}
              ref={ref => (this.leftComponent = ref)}
              getParams={this.getParams}
            />
          </Col>
          <Col span={18}>
            <RuleDefinitionRight
              onEdit={this.editNode}
              businessTypeId={this.props.match.params.businessTypeId}
              selectedNode={selectedNode}
            />
          </Col>
        </Row>

        <Modal
          title={this.$t('workbench.rule.node.info') /* 节点信息 */}
          visible={nodeVisible}
          maskClosable={false}
          className="node-modal"
          onOk={this.nodeSubmit}
          onCancel={this.handleNodeCancel}
          okText={this.$t('common.save')}
          cancelText={this.$t('common.cancel')}
          confirmLoading={nodeLoading}
        >
          <Form onSubmit={this.nodeSubmit}>
            <FormItem {...nodeLayout} label={this.$t('workbench.rule.node.code') /* 节点代码 */}>
              {getFieldDecorator('businessNodeCode', {
                rules: [
                  {
                    required: true,
                    message: this.$t('common.please.enter'),
                  },
                ],
                initialValue: params.id && params.businessNodeCode,
              })(<Input disabled={!!params.id} placeholder={this.$t('common.please.enter')} />)}
            </FormItem>
            <FormItem {...nodeLayout} label={this.$t('workbench.rule.node.name') /* 节点名称 */}>
              {getFieldDecorator('businessNodeName', {
                rules: [
                  {
                    required: true,
                    message: this.$t('common.please.enter'),
                  },
                ],
                initialValue: params.id && params.businessNodeName,
              })(<Input placeholder={this.$t('common.please.enter')} />)}
            </FormItem>
            <FormItem {...nodeLayout} label={this.$t('common.column.status')}>
              {getFieldDecorator('enabled', {
                valuePropName: 'checked',
                initialValue: params.id ? params.enabled : true,
              })(<Switch />)}
              <span style={{ paddingLeft: '10px' }}>
                {this.props.form.getFieldValue('enabled')
                  ? this.$t('common.status.enable')
                  : this.$t('common.status.disable')}
              </span>
            </FormItem>
          </Form>
        </Modal>
        <div className="go-back-fixed">
          <a onClick={this.onBackClick}>
            <Icon type="rollback" style={{ paddingRight: '5px' }} />
            {this.$t('budgetJournal.return') /* 返回 */}
          </a>
        </div>
      </div>
    );
  }
}

RuleDefinitionDetails = Form.create()(RuleDefinitionDetails);

export default connect()(RuleDefinitionDetails);
