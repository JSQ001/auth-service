import React, { Component } from 'react';
import { Modal, Checkbox, Row, Col } from 'antd';

const CheckboxGroup = Checkbox.Group;

class AddNoticeAction extends Component {
  constructor(props) {
    super(props);
    this.state = {
      checkedValues: props.checkedValues || [],
      actions: Object.keys(props.actions).map(key => ({ value: key, label: props.actions[key] })),
    };
  }

  componentWillReceiveProps(nextProps) {
    this.setState({ checkedValues: nextProps.checkedValues });
  }

  onChange = values => {
    this.setState({ checkedValues: values });
  };

  onOk = () => {
    const { onOk } = this.props;
    const { checkedValues } = this.state;
    onOk(checkedValues);
  };

  render() {
    const { visible, onCancel, loading } = this.props;
    const { checkedValues, actions } = this.state;

    return (
      <Modal
        title="选择发送通知的动作"
        visible={visible}
        onOk={this.onOk}
        onCancel={onCancel}
        confirmLoading={loading}
      >
        <CheckboxGroup
          value={checkedValues.map(o => o.toString())}
          style={{ width: '100%' }}
          onChange={this.onChange}
        >
          <Row>
            {actions.map(item => (
              <Col key={item.value} style={{ marginBottom: 10 }} span={8}>
                <Checkbox value={item.value}>{item.label}</Checkbox>
              </Col>
            ))}
          </Row>
        </CheckboxGroup>
      </Modal>
    );
  }
}

export default AddNoticeAction;
