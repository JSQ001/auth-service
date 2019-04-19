import React from 'react';
import { Select, Spin } from 'antd';
import { connect } from 'dva';
import debounce from 'lodash/debounce';
import service from './service';

const { Option } = Select;
class FundCompanyLov extends React.Component {
  constructor(props) {
    super(props);
    this.lastFetchId = 0;
    this.fetchCompany = debounce(this.fetchCompany, 300);
    this.state = {
      data: [],
      searchValue: '',
      fetching: false,
      pagenation: {
        page: 0,
        size: 10,
      },
    };
  }

  componentDidMount() {
    this.fetchCompany();
  }

  fetchCompany = (page, pageSize, fundSetOfBooksId, value) => {
    const { company } = this.props;
    const copyFundSetOfBooksId = fundSetOfBooksId || company.setOfBooksId;
    this.lastFetchId += 1;
    const fetchId = this.lastFetchId;
    this.setState({
      data: [],
      fetching: true,
    });
    const searchParms = value ? { name: value } : {};
    service.getFundCompanys(page, pageSize, copyFundSetOfBooksId, searchParms).then(response => {
      if (fetchId !== this.lastFetchId) {
        return;
      }
      this.setState({
        data: response.data,
        fetching: false,
      });
    });
  };

  handleSearch = value => {
    const { company } = this.props;
    const { pagenation } = this.state;
    this.setState({
      searchValue: value,
    });
    this.fetchCompany(pagenation.page, pagenation.size, company.setOfBooksId, value);
  };

  onPopupScroll = () => {
    const { searchValue, pagenation } = this.state;
    this.setState({
      pagenation: {
        ...pagenation,
        size: pagenation.size + 5,
      },
    });
    this.handleSearch(searchValue);
  };

  render() {
    const { onChange, value } = this.props;
    const { data, fetching } = this.state;
    const options = data.map(d => <Option key={d.id}>{d.name}</Option>);
    return (
      <Select
        labelInValue
        value={value}
        showSearch
        allowClear
        placeholder="请选择"
        notFoundContent={fetching ? <Spin size="small" /> : null}
        filterOption={false}
        defaultActiveFirstOption={false}
        onChange={onChange}
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

export default connect(mapStateToProps)(FundCompanyLov);
