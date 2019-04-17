import React, { Component } from 'react';
import { Tag, Col, Row, Popconfirm, message, Popover, Spin, Empty, Icon } from 'antd';
import service from './rule-definition-service';

class WorkTeamCard extends Component {
  constructor(props) {
    super(props);
    this.state = {
      params: props.params,
      ruleLoading: false,
    };
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.needRefresh > this.props.needRefresh) {
      this.setState({ params: nextProps.params });
    }
  }

  // 规则删除
  tagClose = id => {
    this.setState({ ruleLoading: true });
    service
      .deleteRule(id)
      .then(() => {
        this.getWorkTeamRule();
        message.success(this.$t('common.delete.success'));
      })
      .catch(err => {
        this.setState({ ruleLoading: false });
        message.error(err.response.data.message);
      });
  };

  // 获取工作组下的 规则
  getWorkTeamRule = () => {
    this.setState({ ruleLoading: true });
    service
      .getWorkTeamRule({ businessNodeTeamId: this.props.params.id })
      .then(res => {
        const { params } = this.state;
        params.dispatchRuleDTOS = res.data;
        this.setState({ params, ruleLoading: false });
      })
      .catch(err => message.error(err.response.data.message));
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

  render() {
    const { edit, deleteCard, addRule } = this.props;
    const { params, ruleLoading } = this.state;

    return (
      <div className="work-team-card">
        <div className="card-title clearfix">
          <span className="title-left required">
            {this.$t('workbench.workTeam.team') /* 工作组 */}：{params.workTeamName}
          </span>
          <div className="title-right">
            {params.enabled ? (
              <Tag color="green">{this.$t('common.status.enable')}</Tag>
            ) : (
              <Tag color="red">{this.$t('common.status.disable')}</Tag>
            )}
            <a onClick={() => addRule()}>{this.$t('workbench.rule.add') /* 添加规则 */}</a>
            <a onClick={() => edit()}>{this.$t('common.edit') /* 编辑 */}</a>
            <Popconfirm title={this.$t('common.confirm.delete')} onConfirm={() => deleteCard()}>
              <a>{this.$t('common.delete')}</a>
            </Popconfirm>
          </div>
        </div>
        <Row className="card-content">
          <Col span={8}>
            <span className="required">{this.$t('workbench.rule.method') /* 处理方式 */}：</span>
            {params.handleModelName}
          </Col>
          <Col span={8}>
            <span className="required">
              {this.$t('workbench.rule.maxAssignNumber') /* 取单最大值 */}：
            </span>
            {params.maxAssignNumber}
          </Col>
          <Col span={8}>
            <span className="required">
              {this.$t('workbench.rule.maxHandleNumber') /* 单据在手量 */}：
            </span>
            {params.maxHandleNumber}
          </Col>
        </Row>
        <div className="rule-box clearfix">
          <div className="rule-left">{this.$t('workbench.rule.task')}：</div>
          <Spin wrapperClassName="rule-right clearfix" spinning={ruleLoading} size="small">
            {params.dispatchRuleDTOS &&
              params.dispatchRuleDTOS.map(item => {
                return (
                  <div className="rule-item" key={item.id}>
                    <Popover
                      title={
                        <div className="pop-title">
                          {item.ruleCode}-{item.ruleName}
                        </div>
                      }
                      content={this.renderPop(item.dispatchRuleDetails)}
                      getPopupContainer={() => document.querySelector('.card-wrapper')}
                      overlayClassName="pop-card"
                      trigger="click"
                    >
                      <Tag>
                        {item.ruleCode}-{item.ruleName}
                      </Tag>
                    </Popover>
                    <span className="tag-close" onClick={() => this.tagClose(item.id)}>
                      <Icon type="close" />
                    </span>
                  </div>
                );
              })}
          </Spin>
        </div>
      </div>
    );
  }
}

export default WorkTeamCard;
