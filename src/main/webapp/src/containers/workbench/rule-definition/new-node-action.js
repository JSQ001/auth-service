import React, { Component } from 'react';
import { Form, Input, InputNumber, Select, Button, message, Switch, Spin, Empty } from 'antd';
import service from './rule-definition-service';

const FormItem = Form.Item;
const Option = Select.Option;

class NodeAction extends Component {
  constructor(props) {
    super(props);
    this.state = {
      actionProcedureList: [],
      nodeProcedureList: [],
      saveLoading: false,
      selectedList: [],
      procedureLoading: false,
    };
  }

  componentDidMount() {
    this.getParams();
    this.props.businessActionId && this.nodeProcedure();
  }

  // 获取节点过程 获取动作过程
  getParams = () => {
    const params = { procedureType: 'PROCESSING', businessTypeId: this.props.businessTypeId };
    service
      .getEndProcedure(params)
      .then(res => this.setState({ nodeProcedureList: res.data }))
      .catch(err => message.error(err.response.data.message));
    this.setState({ procedureLoading: true });
    service
      .getActionProcedure()
      .then(res => this.setState({ actionProcedureList: res.data, procedureLoading: false }))
      .catch(err => message.error(err.response.data.message));
  };

  // 获取已选择的节点过程
  nodeProcedure = () => {
    const params = {
      businessNodeId: this.props.businessNodeId,
      businessActionId: this.props.businessActionId,
    };
    service
      .getNodeProcedureByAction(params)
      .then(res => {
        this.setState({ selectedList: res.data ? res.data.map(item => item.id) : [] });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 保存
  onSubmit = e => {
    e.preventDefault();
    const { businessNodeId, params } = this.props;
    this.props.form.validateFields((error, values) => {
      if (!error) {
        const { selectedList } = this.state;
        if (!selectedList.length) {
          return message.warning(this.$t('workbench.rule.select.process') /* 请选择节点过程！ */);
        }
        const data = {
          businessNodeAction: {
            ...values,
            businessNodeId,
            id: params.id,
          },
          businessNodeProcedures: selectedList.map(item => ({
            businessProcedureId: item,
            businessNodeId,
          })),
        };
        this.setState({ saveLoading: true });
        service
          .saveNodeAction(data)
          .then(() => {
            this.setState({ saveLoading: false });
            this.props.onClose(true);
            message.success(this.$t('common.operate.success'));
          })
          .catch(err => {
            message.error(err.response.data.message);
            this.setState({ saveLoading: false });
          });
      }
    });
  };

  // 关闭弹框
  handleNodeCancel = () => {
    this.props.onClose && this.props.onClose();
  };

  // 选择节点过程
  nodeProcedureSelect = id => {
    const { selectedList } = this.state;
    const index = selectedList.indexOf(id);
    if (index > -1) {
      selectedList.splice(index, 1);
    } else {
      selectedList.push(id);
    }
    this.setState({ selectedList });
  };

  render() {
    const {
      actionProcedureList,
      nodeProcedureList,
      saveLoading,
      selectedList,
      procedureLoading,
    } = this.state;
    const { params } = this.props;
    const { getFieldDecorator } = this.props.form;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };

    return (
      <Form onSubmit={this.onSubmit} style={{ paddingTop: '10px' }}>
        <FormItem {...formItemLayout} label={this.$t('workbench.rule.priority') /* 优先级 */}>
          {getFieldDecorator('priority', {
            rules: [
              {
                required: true,
                message: this.$t('common.please.enter'),
              },
            ],
            initialValue: params.id && params.priority,
          })(<InputNumber placeholder={this.$t('common.please.enter')} min={1} precision={0} />)}
        </FormItem>
        <FormItem {...formItemLayout} label={this.$t('workbench.rule.action.code') /* 动作代码 */}>
          {getFieldDecorator('nodeActionCode', {
            rules: [
              {
                required: true,
                message: this.$t('common.please.enter'),
              },
            ],
            initialValue: params.id && params.nodeActionCode,
          })(<Input placeholder={this.$t('common.please.enter')} disabled={!!params.id} />)}
        </FormItem>
        <FormItem {...formItemLayout} label={this.$t('workbench.rule.action.name') /* 动作名称 */}>
          {getFieldDecorator('nodeActionName', {
            rules: [
              {
                required: true,
                message: this.$t('common.please.enter'),
              },
            ],
            initialValue: params.id && params.nodeActionName,
          })(<Input placeholder={this.$t('common.please.enter')} />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={this.$t('workbench.rule.action.process') /* 动作过程 */}
        >
          {getFieldDecorator('actionProcedure', {
            rules: [
              {
                required: true,
                message: this.$t('common.please.select'),
              },
            ],
            initialValue: params.id && params.actionProcedure,
          })(
            <Select
              placeholder={this.$t('common.please.select')}
              getPopupContainer={node => node.parentNode}
            >
              {actionProcedureList.map(item => (
                <Option key={item.id} value={item.value}>
                  {item.name}
                </Option>
              ))}
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label={this.$t('common.column.status')}>
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
        <div className="modal-title">{this.$t('workbench.rule.node.process') /* 节点过程 */}</div>
        <Spin wrapperClassName="node-procedure" spinning={procedureLoading}>
          {nodeProcedureList.length ? (
            nodeProcedureList.map(item => {
              return (
                <Button
                  key={item.id}
                  type={selectedList.includes(item.id) ? 'primary' : 'default'}
                  onClick={() => this.nodeProcedureSelect(item.id)}
                  block
                >
                  {item.procedureCode}-{item.procedureName}
                </Button>
              );
            })
          ) : (
            <Empty />
          )}
        </Spin>
        <div className="slide-footer">
          <Button type="primary" htmlType="submit" loading={saveLoading}>
            {this.$t('common.save')}
          </Button>
          <Button onClick={this.handleNodeCancel}>{this.$t('common.cancel')}</Button>
        </div>
      </Form>
    );
  }
}

NodeAction = Form.create()(NodeAction);

export default NodeAction;
