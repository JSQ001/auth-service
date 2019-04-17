import React, { Component } from 'react';
import { message } from 'antd';
import CustomApproveNode from './custom-approve-node';
import service from './rule-definition-service';

class RuleDefinitionLeft extends Component {
  constructor(props) {
    super(props);
    this.state = {
      nodeList: [],
      isRuleInEdit: false,
      saving: false,
      loadingParams: true,
      baseInfo: {},
      refreshFlag: 0,
    };
  }

  componentDidMount() {
    this.getNodeList();
    this.getRuleDetail();
  }

  // 获取规则详情
  getRuleDetail = () => {
    service
      .getRuleDetail({ id: this.props.businessRuleId })
      .then(res => this.setState({ baseInfo: res.data }))
      .catch(err => message.error(err.response.data.message));
  };

  // 获取规则定义下 节点
  getNodeList = () => {
    this.setState({ loadingParams: true });
    service
      .getNodeList({ businessRuleId: this.props.businessRuleId })
      .then(res => {
        const nodeList = res.data.map((item, index) => {
          return {
            ...item,
            counterFlag: index,
            name: item.businessNodeName,
            type: item.type || 1001,
          };
        });
        this.setState(
          {
            nodeList,
            loadingParams: false,
            refreshFlag: new Date().getTime(),
            saving: true,
          },
          () => this.setState({ saving: false })
        );
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 删除节点
  deleteNode = id => {
    this.setState({ loadingParams: true });
    service
      .deleteNode(id)
      .then(() => {
        this.getNodeList();
        message.success(this.$t('common.delete.success'));
      })
      .catch(err => {
        this.setState({ loadingParams: false });
        message.error(err.response.data.message);
      });
  };

  // 节点顺序改变
  nodeSort = (oldNode, newNode, nodeId) => {
    this.setState({ loadingParams: true });
    const params = {
      businessRuleId: this.props.businessRuleId,
      oldPriority: oldNode,
      newPriority: newNode,
      nodeId,
    };
    service
      .updateNodePriority(params)
      .then(() => {
        // this.getNodeList();
        this.setState({ loadingParams: false });
        message.success(this.$t('common.operate.success'));
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ loadingParams: false });
      });
  };

  // 取消
  handleCancel = () => {
    this.setState({ loadingParams: false });
  };

  render() {
    const { nodeList, loadingParams, refreshFlag, baseInfo, saving } = this.state;
    const { showNode, getParams } = this.props;

    return (
      <div className="rule-definition-left workflow-detail">
        <p className="info-title define-ellipsis" title={baseInfo.businessTypeName}>
          <span
            className="label define-ellipsis"
            title={this.$t('workbench.rule.businessType') /* 业务类型 */}
          >
            {this.$t('workbench.rule.businessType') /* 业务类型 */}
          </span>：{baseInfo.businessTypeName}
        </p>
        <p className="info-title define-ellipsis" title={baseInfo.procedureName}>
          <span
            className="label define-ellipsis"
            title={this.$t('workbench.rule.execute') /* 完成时执行 */}
          >
            {this.$t('workbench.rule.execute') /* 完成时执行 */}
          </span>：{baseInfo.procedureName}
        </p>
        <CustomApproveNode
          nodeList={nodeList}
          showNode={showNode}
          getParams={getParams}
          loadingParams={loadingParams}
          deleteNode={this.deleteNode}
          nodeSort={this.nodeSort}
          refreshFlag={refreshFlag}
          saving={saving}
        />
      </div>
    );
  }
}

export default RuleDefinitionLeft;
