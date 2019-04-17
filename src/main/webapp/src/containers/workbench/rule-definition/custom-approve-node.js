import React from 'react';
import { connect } from 'dva';
import { deepCopy } from 'utils/extend';
import { Form, Row, Col, Spin } from 'antd';

import HTML5Backend from 'react-dnd-html5-backend';
import { DragDropContext } from 'react-dnd';
import DragWidgetItem from 'containers/setting/workflow/drag-source/drag-widget-item';
import FakeDropLayout from 'containers/setting/workflow/drop-source/fake-drop-layout';
import NodeContent from 'containers/setting/workflow/drop-source/node-content';
import PropTypes from 'prop-types';

import 'styles/setting/workflow/workflow-detail.scss';

class CustomApproveNode extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      customWidget: [
        {
          type: 1001,
          nodeType: 'WORKBENCH',
          name: this.$t('workbench.rule.node') /* 节点 */,
          remark: this.$t('workbench.rule.node') /* 节点 */,
        },
      ],
      nowSelectedIndex: 0,
      nowSelectedId: '',
      counter: 0,
      endNodeWidget: null,
      endNodeSelected: false, // 结束节点是否被选中
    };
  }

  componentWillReceiveProps(nextProps) {
    // 刷新节点列表，重新选择节点，选第一个
    const { getParams, refreshFlag = 0 } = this.props;
    if (Number(nextProps.refreshFlag) > Number(refreshFlag)) {
      const indexParams = nextProps.nodeList[0];
      getParams(null, null, indexParams || -1);
      this.setState({ nowSelectedId: indexParams && indexParams.id, nowSelectedIndex: 0 });
    }
  }

  /**
   * 选择某一组件时的回调
   * @param index  列表中的第几个
   * @param widget  对应widget对象
   */
  handleSelectWidget = (index, widget) => {
    const { isRuleInEdit, getParams } = this.props;
    const { nowSelectedId } = this.state;
    if (widget && !isRuleInEdit && nowSelectedId !== widget.id) {
      getParams(null, null, widget);
      this.setState({
        nowSelectedIndex: index,
        nowSelectedId: widget.id,
      });
    } else if (!widget) {
      getParams(null, null, -1);
      this.setState({ nowSelectedIndex: undefined, nowSelectedId: '' });
    }
  };

  /**
   * phone-content内部排序后的事件
   */
  handleSort = result => {
    const { nowSelectedIndex, nowSelectedId } = this.state;
    let targetIndex = 0;
    result.map((item, index) => {
      if (String(item.key) === String(nowSelectedIndex)) {
        targetIndex = index;
      }
    });
    this.props.nodeSort(nowSelectedIndex, targetIndex, nowSelectedId);
  };

  /**
   * 从列表中把widget拖拽入phone-content时的事件
   * @param widget 拖入的widget
   * @param index 拖入后的index
   */
  handleDrop = (widget, index) => {
    const { showNode, getParams } = this.props;
    showNode && showNode();
    getParams && getParams(index, widget.nodeType);
  };

  /**
   * 删除节点
   * @param widget 节点信息
   */
  handleDelete = widget => {
    if (widget) {
      this.props.deleteNode(widget.id);
    } else {
      return true;
    }
  };

  render() {
    const { customWidget, nowSelectedIndex, endNodeSelected, endNodeWidget } = this.state;
    const { loadingParams, saving, nodeList } = this.props;
    const widgetList = deepCopy(nodeList);
    widgetList.map((item, index) => {
      item.type === 1005 && widgetList.splice(index, 1);
    });
    return (
      <div className="custom-approve-node">
        <FakeDropLayout />
        <Row className="widget-list-item-container">
          {customWidget.map(widget => {
            return (
              <Col span={6} key={widget.type}>
                <DragWidgetItem widget={widget} />
              </Col>
            );
          })}
        </Row>
        <Spin spinning={loadingParams}>
          <NodeContent
            widgetList={widgetList}
            onSort={this.handleSort}
            nowSelectedIndex={nowSelectedIndex}
            onSelect={this.handleSelectWidget}
            onDrop={this.handleDrop}
            onDelete={this.handleDelete}
            endNodeSelected={endNodeSelected}
            endNodeWidget={endNodeWidget}
            saving={saving}
            isRuleInEdit={false}
          />
        </Spin>
      </div>
    );
  }
}

CustomApproveNode.propTypes = {
  nodeList: PropTypes.array, // 节点数据
  isRuleInEdit: PropTypes.bool, // 是否有审批条件处于编辑状态
  nodeSort: PropTypes.func,
  deleteNode: PropTypes.func,
  refreshFlag: PropTypes.number,
  getParams: PropTypes.func, // 获取节点参数
  loadingParams: PropTypes.bool,
  saving: PropTypes.bool, // 获取数据时改变状态
};

CustomApproveNode.defaultProps = {
  nodeList: [],
  isRuleInEdit: false,
};

function mapStateToProps(state) {
  return {
    language: state.languages,
  };
}

const wrappedWorkflow = Form.create()(CustomApproveNode);

export default connect(mapStateToProps)(DragDropContext(HTML5Backend)(wrappedWorkflow));
