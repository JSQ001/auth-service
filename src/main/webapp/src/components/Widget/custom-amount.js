import React from 'react';
import { InputNumber } from 'antd';
import PropTypes from 'prop-types';
import { toDecimal } from 'utils/utils';

class CustomAmount extends React.Component {
  onBlur = e => {
    if (e.target.value === '') return;
    const { value } = this.props;
    this.props.onChange && this.props.onChange(toDecimal(value));
  };

  onChange = value => {
    this.props.onChange && this.props.onChange(value);
  };

  render() {
    const { disabled, step, style, value, precision, min } = this.props;
    return (
      <InputNumber
        style={style}
        placeholder={this.$t('common.enter')}
        step={step}
        value={value}
        precision={precision}
        min={min}
        onChange={this.onChange}
        onBlur={this.onBlur}
        formatter={value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
        parser={value => value.replace(/(,*)/g, '')}
        disabled={disabled}
      />
    );
  }
}

CustomAmount.propTypes = {
  disabled: PropTypes.bool, //是否可用
  onChange: PropTypes.func, //输入后的回调
  len: PropTypes.number, //小数位数
};

CustomAmount.defaultProps = {
  disabled: false,
  len: 2,
  step: 0.01,
  precision: 2,
  min: 0,
  style: { width: '100%' },
};

export default CustomAmount;
