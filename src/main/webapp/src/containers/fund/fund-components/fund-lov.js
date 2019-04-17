import React from 'react';
import { Select, message } from 'antd';
import { connect } from 'dva';

import service from './service';

const { Option } = Select;

class FundLov extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      companyValueList: [], // 公司列表
      pagenation: {
        page: 0,
        size: 10,
      },
    };
  }

  componentDidMount() {
    const { selectType } = this.props;
    this.getValueList(selectType);
  }

  getValueList = async (type, searchPrams = {}) => {
    this.setState({
      companyValueList: [],
    });
    const { company } = this.props;
    const { pagenation } = this.state;
    switch (type) {
      default: {
        await service
          .getCompanys(pagenation.page, pagenation.size, company.setOfBooksId, searchPrams)
          .then(response => {
            this.setState({
              companyValueList: response.data,
            });
          })
          .catch(error => {
            message.error(error.response.data.message);
          });
      }
    }
  };

  companySearch = async inputValue => {
    const searchPrams = {
      name: inputValue,
    };
    await this.getValueList('company', searchPrams);
  };

  render() {
    const { selectType } = this.props;
    const { companyValueList } = this.state;
    switch (selectType) {
      case 'company': {
        return (
          <Select showSearch style={{ width: 200 }} placeholder="请选择">
            {companyValueList.map(option => {
              return <Option key={option.id}>{option.name}</Option>;
            })}
          </Select>
        );
      }
      default: {
        return (
          <Select
            showSearch
            style={{ width: 200 }}
            placeholder="请选择"
            onSearch={this.companySearch}
          >
            {companyValueList.map(option => {
              return <Option key={option.id}>{option.name}</Option>;
            })}
          </Select>
        );
      }
    }
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    language: state.languages,
  };
}

export default connect(mapStateToProps)(FundLov);
