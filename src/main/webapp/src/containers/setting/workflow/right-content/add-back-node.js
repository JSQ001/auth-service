import React from 'react';
import { connect } from 'dva';
import config from 'config';
import httpFetch from 'share/httpFetch';
import { Modal, Icon, message, Spin } from 'antd';
import manApprovalImg from 'images/setting/workflow/man-approval.svg';
import manApprovalDisabledImg from 'images/setting/workflow/man-approval-disabled.svg';
import PropTypes from 'prop-types';
import 'styles/approve-setting/return-back.scss';

class ReturnBack extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      ruleApprovalNodes: [],
      currentNodeOid: '',
      selectedNodes: props.selectedNodes || [],
      loading: true,
    };
  }

  componentWillReceiveProps(nextProps) {
    const { modalVisible, nodeOid } = this.props;
    if (nextProps.modalVisible && !modalVisible) {
      this.getList(nodeOid);
    }
  }

  getList = nodeOid => {
    const url = `${
      config.workflowUrl
    }/api/rule/custom/form/return/node?ruleApprovalNodeOid=${nodeOid}`;
    this.setState({ loading: true });
    httpFetch
      .get(url)
      .then(res => {
        const ruleNodes = res.data.map(item => {
          const result = {};
          result.ruleApprovalNodeOid = item.ruleApprovalNodeOid;
          result.remark = item.remark;
          result.enabled = item.ruleApprovalNodeOid !== nodeOid;
          return result;
        });
        this.setState({
          ruleApprovalNodes: ruleNodes,
          currentNodeOid: nodeOid,
          loading: false,
        });
      })
      .catch(() => {
        this.setState({ loading: false });
        message.error('嗷~~~~~~,服务器发生异常！');
      });
  };

  onOk = () => {
    const { onOk } = this.props;
    const { selectedNodes } = this.state;
    if (!selectedNodes || !selectedNodes.length) {
      message.warning('请选择要退回的节点！');
      return;
    }
    if (onOk) {
      onOk(selectedNodes);
    }
  };

  onClose = () => {
    const { onClose } = this.props;
    onClose();
  };

  // 获取节点图片
  getNodeImg = enabled => {
    if (enabled) {
      // eslint-disable-next-line jsx-a11y/alt-text
      return <img src={manApprovalImg} className="node-img" />;
    } else {
      // eslint-disable-next-line jsx-a11y/alt-text
      return (
        <img style={{ cursor: 'not-allowed' }} src={manApprovalDisabledImg} className="node-img" />
      );
    }
  };

  handleSkip = node => {
    const { selectedNodes } = this.state;
    if (node.enabled) {
      const index = selectedNodes.findIndex(
        o => o.ruleApprovalNodeOid === node.ruleApprovalNodeOid
      );
      if (index >= 0) {
        selectedNodes.splice(index, 1);
      } else {
        selectedNodes.push(node);
      }
    }
    this.setState({
      selectedNodes,
    });
  };

  selected = value => {
    const { selectedNodes } = this.state;
    return selectedNodes.findIndex(o => o.ruleApprovalNodeOid === value) >= 0;
  };

  render() {
    const { ruleApprovalNodes, currentNodeOid, btnLoading, loading } = this.state;
    const { modalVisible } = this.props;
    return (
      <Modal
        title="退回指定节点"
        width="50%"
        onCancel={this.onClose}
        onOk={this.onOk}
        confirmLoading={btnLoading}
        destroyOnClose
        visible={modalVisible}
        bodyStyle={{ height: 200 }}
      >
        <div className="back-node">
          <div className="admin-skip-wrap">
            <Icon type="info-circle" className="info" /> 提示：彩色节点为您可以选择退回的节点
            <Spin spinning={loading}>
              <div style={{ height: 200 }} className="admin-skip">
                {ruleApprovalNodes &&
                  !!ruleApprovalNodes.length &&
                  ruleApprovalNodes.map((node, index) => {
                    return (
                      <div
                        onClick={() => {
                          this.handleSkip(node);
                        }}
                        className="node-wrap"
                        key={node.ruleApprovalNodeOid}
                      >
                        {node.ruleApprovalNodeOid === currentNodeOid && (
                          <span className="node-current">当前</span>
                        )}
                        {this.selected(node.ruleApprovalNodeOid) && (
                          <span className="node-selected">
                            <Icon className="selected-icon" type="check" />
                          </span>
                        )}
                        {this.getNodeImg(node.enabled)}
                        {index < ruleApprovalNodes.length - 1 && (
                          <Icon type="arrow-right" className="right-arrow" />
                        )}
                        <span className="node-remark">{node.remark}</span>
                      </div>
                    );
                  })}
              </div>
            </Spin>
          </div>
        </div>
      </Modal>
    );
  }
}

ReturnBack.propTypes = {
  modalVisible: PropTypes.bool, // 是否展示
};

ReturnBack.defaultProps = {
  modalVisible: false,
};

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(ReturnBack);
