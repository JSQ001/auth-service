import { Select, Spin } from 'antd';
import debounce from 'lodash/debounce';
import React from 'react';
import httpFetch from 'utils/fetch';
import config from 'config';

const { Option } = Select;

class SelectPlace extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      data: [],
      value: [],
      fetching: false,
    };
    this.fetchData = debounce(this.fetchData, 800);
  }

  componentDidMount() {
    if (this.props.value) {
      this.setState({ value: this.props.value });
    }
  }

  componentWillReceiveProps = nextProps => {
    console.log(nextProps);
  };

  fetchData = value => {
    console.log('fetching user', value);
    if (!value) return;
    this.setState({ data: [], fetching: true });
    httpFetch
      .get(`${config.mdataUrl}/api/location/search/cities?description=${value}`)
      .then(res => {
        this.setState({ data: res, fetching: false });
      });
  };

  handleChange = value => {
    this.setState({
      value,
      data: [],
      fetching: false,
    });
    this.props.onChange(value);
  };

  render() {
    const { fetching, data, value } = this.state;
    return (
      <Select
        mode="multiple"
        labelInValue
        value={value}
        placeholder="选择地点"
        notFoundContent={fetching ? <Spin size="small" /> : null}
        filterOption={false}
        onSearch={this.fetchData}
        onChange={this.handleChange}
        style={{ width: '100%' }}
      >
        {data.map(item => <Option key={item.id}>{item.description}</Option>)}
      </Select>
    );
  }
}

SelectPlace.defaultProps = {
  onChange: () => {},
};
export default SelectPlace;
