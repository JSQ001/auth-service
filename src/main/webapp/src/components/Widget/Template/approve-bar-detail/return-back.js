import React from 'react';
import { connect } from 'dva';
import config from 'config';
import httpFetch from 'share/httpFetch';
import { messages } from 'utils/utils';
import { Form, Modal, Icon, message, Spin, Select } from 'antd';
import manApprovalImg from 'images/setting/workflow/man-approval.svg';
import manApprovalDisabledImg from 'images/setting/workflow/man-approval-disabled.svg';
import PropTypes from 'prop-types';
import 'styles/approve-setting/return-back.scss';

const FormItem = Form.Item;
const { Option } = Select;

class ReturnBack extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      ruleApprovalNodes: [],
      currentNodeOid: '',
      skipNodeOid: '',
      loading: true,
      btnloading: false,
      backTypeOptions: [
        { label: '继续原审批流程', value: 'START' },
        { label: '直接跳回本节点', value: 'CURRENT' },
      ],
      backTypeFlag: false,
    };
  }

  componentWillMount() {
    const { documentType, documentOid } = this.props;
    const url = `${
      config.workflowUrl
    }/api/workflow/back/nodes?entityOid=${documentOid}&entityType=${documentType}`;
    this.setState({ loading: true });
    httpFetch
      .get(url)
      .then(res => {
        const backTypeFlag = res.data.backFlag;
        const temp = res.data.approvalNodeDTOList.filter(e => e.type === 1001);
        let nodeOid = '';
        const ruleNodes = temp.map((item, index) => {
          const result = {};
          result.ruleApprovalNodeOid = item.ruleApprovalNodeOid;
          result.remark = item.remark;
          result.enabled = item.status === 2;
          if (index === temp.length - 1) {
            nodeOid = item.ruleApprovalNodeOid;
          }
          return result;
        });
        this.setState({
          ruleApprovalNodes: ruleNodes,
          currentNodeOid: nodeOid,
          loading: false,
          backTypeFlag,
        });
      })
      .catch(() => {
        this.setState({ loading: false });
        message.error('嗷~~~~~~,服务器发生异常！');
      });
  }

  onOk = () => {
    const { documentType, documentOid, form } = this.props;
    const { skipNodeOid } = this.state;

    if (skipNodeOid === null || skipNodeOid === '') {
      message.warning('请选择要跳转的节点！');
      return;
    }
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        let params = {
          approvalNodeOid: skipNodeOid,
          backTypeEnum: values.backTypeEnum || 'START',
          entityOid: documentOid,
          entityType: documentType,
        };
      }
    });
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
    let { ruleApprovalNodes } = this.state;
    let skipNodeOid = '';
    if (node.enabled) {
      skipNodeOid = node.ruleApprovalNodeOid;
      ruleApprovalNodes = ruleApprovalNodes.map(item => {
        return {
          ...item,
          selected: item.ruleApprovalNodeOid === node.ruleApprovalNodeOid,
        };
      });
    } else {
      ruleApprovalNodes = ruleApprovalNodes.map(item => {
        return {
          ...item,
          selected: false,
        };
      });
    }
    this.setState({
      skipNodeOid,
      ruleApprovalNodes,
    });
  };

  render() {
    const {
      ruleApprovalNodes,
      currentNodeOid,
      btnLoading,
      loading,
      backTypeOptions,
      backTypeFlag,
    } = this.state;
    const {
      form: { getFieldDecorator },
      modalVisible,
    } = this.props;
    return (
      <Modal
        title="退回指定节点"
        width="50%"
        onCancel={this.onClose}
        onOk={this.onOk}
        confirmLoading={btnLoading}
        destroyOnClose
        visible={modalVisible}
      >
        <div className="admin-skip-wrap">
          <Icon type="info-circle" className="info" /> 提示：彩色节点为您可以选择退回的节点
          <Spin spinning={loading}>
            <div className="admin-skip">
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
                      {node.selected && (
                        <span className="node-selected">
                          <Icon className="selected-icon" type="check" />
                        </span>
                      )}
                      {this.getNodeImg(node.enabled)}
                      {index < ruleApprovalNodes.length - 1 && (
                        <Icon type="arrow-right" className="right-arrow" />
                      )}
                      <span className="node-text">{node.remark}</span>
                    </div>
                  );
                })}
              {backTypeFlag && (
                <Form style={{ marginTop: '40px' }}>
                  <FormItem
                    label="退回审批通过后处理"
                    labelCol={{ span: 6 }}
                    wrapperCol={{ span: 9 }}
                  >
                    {getFieldDecorator('backTypeEnum', {
                      rules: [
                        {
                          required: true,
                          message: this.$t({ id: 'common.please.select' } /* 请选择 */),
                        },
                      ],
                    })(
                      <Select placeholder={messages('common.please.select')}>
                        {backTypeOptions.map(option => {
                          return <Option key={option.value}>{option.label}</Option>;
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Form>
              )}
            </div>
          </Spin>
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

const wrappedReturnBack = Form.create()(ReturnBack);
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
)(wrappedReturnBack);
