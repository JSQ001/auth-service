import React from 'react';
import { Select, Spin, message } from 'antd';
import { connect } from 'dva';
import debounce from 'lodash/debounce';
import { selectDataByKey } from './utils';

import service from './service';

const { Option } = Select;

class FundAccountLov extends React.Component {
  constructor(props) {
    super(props);
    this.lastFetchId = 0;
    this.fetchAccount = debounce(this.fetchAccount, 300);
    this.state = {
      data: [],
      fetching: false,
      pagenation: {
        page: 0,
        size: 10,
      },
      accountLovPrams: {},
    };
  }

  componentDidMount() {
    this.handleSearch();
  }

  componentWillReceiveProps(nextProps) {
    const { accountLovPrams } = nextProps;
    this.setState(
      {
        accountLovPrams,
      },
      () => this.handleSearch()
    );
  }

  /**
   * 获取数据
   */
  fetchAccount = (page, pageSize, companyId, value) => {
    this.lastFetchId += 1;
    const fetchId = this.lastFetchId;
    this.setState({
      data: [],
      fetching: true,
    });
    const searchParms = value ? { name: value } : {};
    service
      .getAccountList(page, pageSize, companyId, searchParms)
      .then(response => {
        if (fetchId !== this.lastFetchId) {
          return;
        }
        if (response.data.length === 0) {
          message.warning('该公司下无所属账号');
        }
        this.setState({
          data: response.data,
          fetching: false,
        });
      })
      .catch(error => {
        message.error(error.response.data.message);
      });
  };

  /**
   * 搜索
   */
  handleSearch = value => {
    const { pagenation, accountLovPrams } = this.state;
    if (accountLovPrams) {
      this.fetchAccount(pagenation.page, pagenation.size, accountLovPrams.companyId, value, data =>
        this.setState({ data })
      );
    }
  };

  /**
   *
   */
  selectDataByKey = (key, value, data) => {
    return data.filter(item => {
      return item[key] === value;
    });
  };

  render() {
    const { onChange, value } = this.props;
    const { data, fetching } = this.state;
    const options = data.map(d => <Option key={d.id}>{d.accountNumber}</Option>);
    return (
      <Select
        labelInValue
        value={value}
        showSearch
        allowClear
        placeholder="请选择"
        filterOption={false}
        defaultActiveFirstOption={false}
        notFoundContent={fetching ? <Spin size="small" /> : null}
        onChange={changeValue => {
          onChange(changeValue, selectDataByKey('id', changeValue.key, data));
        }}
        onSearch={this.handleSearch}
      >
        {options}
      </Select>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    language: state.languages,
  };
}

export default connect(mapStateToProps)(FundAccountLov);
