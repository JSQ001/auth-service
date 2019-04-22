import React, { Component } from 'react';
import { Input, Icon } from 'antd';
import ListSelector from './list-selector';
import PropTypes from 'prop-types';

// 示例
{
  /* <Lov 
  code="company"
  valueKey="id"
  labelKey="name"
  single
/> */
}

/**
 * 弹框组件
 */
class Lov extends Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      value: '',
    };
  }

  componentDidMount() {
    const { value, single, labelKey } = this.props;
    if (value) {
      if (!single) {
        this.setState({
          value: `已选择${nextProps.value.length}条`,
        });
      } else {
        this.setState({
          value: value[labelKey],
        });
      }
    } else {
      this.setState({
        value: ``,
      });
    }
  }

  componentWillReceiveProps(nextProps) {
    const { value, single, labelKey } = nextProps;
    if (value) {
      if (!single) {
        this.setState({
          value: `已选择${nextProps.value.length}条`,
        });
      } else {
        this.setState({
          value: value[labelKey],
        });
      }
    } else {
      this.setState({
        value: ``,
      });
    }
  }

  // 输入框获取焦点，显示弹出框
  focusHandle = () => {
    this.input.blur();
    this.setState({ visible: true });
  };

  // 确定的回调
  okHandle = values => {
    const { onChange } = this.props;
    if (onChange) {
      onChange(values);
      this.setState({ visible: false });
    }
  };

  // 取消的回调
  cancelHandle = () => {
    this.setState({ visible: false });
  };

  //清除
  onChange = e => {
    if (!e.target.value) {
      const { single } = this.props;
      if (single) {
        this.okHandle({});
      } else {
        this.okHandle([]);
      }
    }
  };

  render() {
    const { visible, value } = this.state;
    const { allowClear, placeholder, disabled } = this.props;
    return (
      <div>
        <Input
          value={value}
          ref={ref => (this.input = ref)}
          onFocus={this.focusHandle}
          allowClear={allowClear}
          placeholder={this.$t(placeholder)}
          disabled={disabled}
          onChange={this.onChange}
        />
        <ListSelector
          onOk={this.okHandle}
          onCancel={this.cancelHandle}
          visible={visible}
          {...this.props}
        />
      </div>
    );
  }
}

Lov.propTypes = {
  placeholder: PropTypes.string, // 输入框空白时的显示文字
  disabled: PropTypes.bool, // 是否可用
  code: PropTypes.string.isRequired, // lov的code
  valueKey: PropTypes.string.isRequired, //表单项的id变量名
  labelKey: PropTypes.string.isRequired, //表单项的显示变量名
  extraParams: PropTypes.object, //listSelector的额外参数
  onChange: PropTypes.func, // 进行选择后的回调
  single: PropTypes.bool, // 是否单选
  value: PropTypes.oneOfType([PropTypes.array, PropTypes.object]), //已选择的值，需要传入完整目标数组
  allowClear: PropTypes.bool, // 是否支持清除
};

Lov.defaultProps = {
  placeholder: 'common.please.select',
  disabled: false,
  extraParams: {},
  single: false,
  allowClear: true,
};

export default Lov;
