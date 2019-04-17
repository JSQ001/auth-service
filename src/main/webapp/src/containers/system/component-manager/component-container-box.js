import React, { Component } from 'react';

@connect(({ components }) => ({
  components,
}))
export default class ComponentContainerBox extends Component {
  constructor(props) {
    super(props);
    this.state = {
      text: '',
      els: [],
      selected: false,
    };
  }

  say = () => {};
  selected = e => {
    this.setState({ selected: true });
  };
  render() {
    const { canDrop, isOver, connectDropTarget, dispatch, components } = this.props;

    const isActive = canDrop && isOver;

    const componentList = {
      button: Button,
    };

    let Element = componentList[this.state.text];
    let props = { type: 'primary' };

    return <div>444</div>;
  }
}
