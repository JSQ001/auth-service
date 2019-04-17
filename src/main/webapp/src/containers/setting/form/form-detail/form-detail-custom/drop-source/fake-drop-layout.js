import React, { Component } from 'react';
import { DragLayer } from 'react-dnd';
import Widget from 'widget/Template/widget/widget';
import PropTypes from 'prop-types';

/**
 * 覆盖整个页面的drop源，当拖拽进入时显示对应的widget样式
 */
class FakeDropLayout extends Component {
  renderItem = (type, item) => {
    if (type) return <Widget widget={item} />;
  };

  getItemStyles = props => {
    const { initialOffset, currentOffset } = props;
    //初始位置或偏移位置没有表示当前没有拖拽发生
    if (!initialOffset || !currentOffset) {
      return {
        display: 'none',
      };
    }
    //有拖拽发生时，指定对应的translate
    let { x, y } = currentOffset;
    const transform = `translate(${x}px, ${y}px)`;
    return {
      transform,
      WebkitTransform: transform,
    };
  };

  render() {
    const { item, itemType, isDragging } = this.props;
    if (!isDragging) {
      return null;
    }
    const layerStyles = {
      position: 'fixed',
      pointerEvents: 'none',
      zIndex: 100,
      left: 0,
      top: 0,
      width: '100%',
      height: '100%',
    };
    return (
      <div style={layerStyles}>
        <div style={this.getItemStyles(this.props)}>{this.renderItem(itemType, item)}</div>
      </div>
    );
  }
}

FakeDropLayout.propTypes = {
  item: PropTypes.object,
  itemType: PropTypes.string,
  initialOffset: PropTypes.shape({
    x: PropTypes.number.isRequired,
    y: PropTypes.number.isRequired,
  }),
  currentOffset: PropTypes.shape({
    x: PropTypes.number.isRequired,
    y: PropTypes.number.isRequired,
  }),
  isDragging: PropTypes.bool.isRequired,
};

export default DragLayer(monitor => ({
  item: monitor.getItem(),
  itemType: monitor.getItemType(),
  initialOffset: monitor.getInitialSourceClientOffset(),
  currentOffset: monitor.getSourceClientOffset(),
  isDragging: monitor.isDragging(),
}))(FakeDropLayout);
